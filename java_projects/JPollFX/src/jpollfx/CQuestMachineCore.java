package jpollfx;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;



public class CQuestMachineCore
{

    private static CQuestionnaire InitQuestionnaireFromCSV(File filCSV)
    {
        List<List<String>> lstCommands = CreateListFromCSV(filCSV);

        CQuestionnaire qnrNew = CreateQuestionnaireFromList(lstCommands);

        return qnrNew;
    }
	
    private static List<List<String>> CreateListFromCSV(File filCSV)
    {
        List<List<String>> lstCommands = new ArrayList<>();

        try
        {

            Scanner scnrSource = new Scanner(filCSV);

            // Reading string by string from input file
            // Commas separate tags. Tags are added to list
            while(scnrSource.hasNextLine() == true)
            {
                String sNextLine = scnrSource.nextLine();

                List<String> lstLine = new ArrayList<String>();

                int nStart = 0;
                int nEnd = -1;

                while (nStart >=0 )
                {
                    String sCurrentLine = sNextLine;

                    nEnd = sCurrentLine.indexOf(",", nStart)-1;
                    if (nEnd < 0)
                    {
                        nEnd = sCurrentLine.length()-1;
                    }

                    if(nEnd >= nStart)
                    {
                        String sTmp = new String( sCurrentLine.substring(nStart, nEnd+1));
                        lstLine.add(sTmp);

                        nStart = sCurrentLine.indexOf(",", nEnd + 1)+1;
                    }
                    else
                    {
                        lstLine.add("");
                        nStart = sCurrentLine.indexOf(",", nStart)+1;
                    }

                    if (nEnd >= sCurrentLine.length()-1)
                    {
                        nStart = -1;						
                    }

                }

                lstCommands.add(lstLine);
            }

            scnrSource.close();

        }
        catch(IOException ex)
        {
            System.out.print(ex.getMessage()); // debug option
            return new ArrayList<List<String>>();
        }

        return lstCommands;
    }
	
    private static CQuestionnaire CreateQuestionnaireFromList(List<List<String>> lstCommands)
    {
        CQuestionnaire qnrNew = new CQuestionnaire(); 

        //------------------------------------------------------------		
        // Now let's fill CQuestionnaire with data from CSV!

        Iterator<List<String>> itLine = lstCommands.iterator();

        while (itLine.hasNext() == true)
        {
            CQuestStage stgNew = new CQuestStage();	

            Iterator<String> itCurrentLine = itLine.next().iterator();

            // filling questionnaire stage info 
            stgNew.setID(itCurrentLine.next());


            stgNew.setStageType(itCurrentLine.next().equals("S")?
                                                            CQuestStage.enmStageType.ST_SINGLE_ANSWER.name():
                                                            CQuestStage.enmStageType.ST_MULTY_ANSWER.name());

            //system tag. unused
            itCurrentLine.next(); 

            qnrNew.AddStage(stgNew, null);
        }

        itLine = lstCommands.iterator();
        Iterator<CQuestStage> itStages = qnrNew.getStagesIterator();

        while ( (itLine.hasNext() == true) && (itStages.hasNext() == true) ) 
        {			
            CQuestStage stgCurrent  = itStages.next();	

            Iterator<String> itCurrentLine = itLine.next().iterator();

            // filling questionnaire stage info 
            itCurrentLine.next(); // Caption
            itCurrentLine.next(); // Quest type
            itCurrentLine.next(); // System tag. unused 

            while (itCurrentLine.hasNext() == true)
            {
                CQuestAnswer qaNew = new CQuestAnswer();

                // filling questionnaire stage info
                qaNew.setCaption(itCurrentLine.next());

                int nNextStage = 0;

                try
                {
                    nNextStage = Integer.valueOf(itCurrentLine.next());
                }
                catch(NumberFormatException ex)
                {
                    System.out.println("Wrong next_stage_number:");
                    System.out.println(ex.getLocalizedMessage());
                }

                ListIterator<CQuestStage> itTmp = qnrNew.getStagesIterator();
                int iStage = 1;

                while(itTmp.hasNext() == true)
                {
                    itTmp.next();

                    if(iStage == nNextStage)
                        break;

                    iStage++;

                }

                qaNew.setNextStage(itTmp.previous());

                stgCurrent.AddItem(qaNew);
            }

        }

        return qnrNew;
    }	
	
    /**
     * 
     * @param qnrSource
     * @param pDestinationFile 
     * Saves questionnaire to CSV file in format of Poll by IMESC
     */
    public static void SaveQuestionnaireToCSV(CQuestionnaire qnrSource, Path pDestinationFile)
    {

        // If this path doesn't exist or is not writable
        if (Files.exists(pDestinationFile) == true &&
                        Files.isWritable(pDestinationFile) == false) 
        {
            return;
        }


        List<String> lstCommands = new ArrayList<String>();

        ListIterator<CQuestStage> itStages = qnrSource.getStagesIterator();
        int iStageIndex = 1;

        while(itStages.hasNext() == true)
        {
            String sCommand = "";

            CQuestStage qstStage = itStages.next();
            sCommand = sCommand + qstStage.getID() + ",";

            if (qstStage.getStageType() == CQuestStage.enmStageType.ST_SINGLE_ANSWER.name())
            {
                sCommand = sCommand  + "S";
            }
            else
            {
                sCommand =  sCommand  + "M";
            }
            sCommand = sCommand + ",";

            // Unused tag
            sCommand = sCommand + "0";

            Iterator<CQuestAnswer> itAnswer = qstStage.GetAnswersIterator();

            while(itAnswer.hasNext() == true)
            {
                CQuestAnswer qaAnswer = itAnswer.next();

                if(qaAnswer.getCaption().equals(CQuestAnswer.OPENED_ANSWER) == false)
                {
                    sCommand = sCommand + "," + qaAnswer.getCaption();
                }
                else
                {
                    sCommand = sCommand + ",";
                }

                if(qaAnswer.getNextStage() != null)
                {
                    sCommand = sCommand + "," + qnrSource.getStageNumber(qaAnswer.getNextStage());
                }
                else
                {
                    sCommand = sCommand + "," + Integer.toString(iStageIndex+1);
                }

            }


            lstCommands.add(sCommand);
            iStageIndex++;
        }

        //Path pDestFile = Paths.get(pDestinationFolder.toAbsolutePath().toString(), "Test Quest Result.csv");

        try
        {

            ///byte [] abBomUTF8 = new byte[]{(byte)0xEF, (byte)0xBB, (byte)0xBF};

            OutputStream osDestination = Files.newOutputStream(pDestinationFile, 
                                                                StandardOpenOption.CREATE, 
                                                                StandardOpenOption.TRUNCATE_EXISTING, 
                                                                StandardOpenOption.WRITE);

            //osDestination.write(abBomUTF8);
            osDestination.flush();

            OutputStreamWriter oswDestination = new OutputStreamWriter(osDestination /*, StandardCharsets.UTF_8*/);

            Iterator<String> itCommands = lstCommands.iterator();

            while(itCommands.hasNext())
            {
                oswDestination.write(itCommands.next());
                oswDestination.write(System.lineSeparator());
                oswDestination.flush();
            }


            oswDestination.close();


        }
        catch(IOException ex)
        {
            System.out.printf("%s%n", ex);			
        }

    }
	
	
    public static void ShowQuestionnaire(CQuestionnaire qnrNew)
    {

        ListIterator<CQuestStage> itStage = qnrNew.getStagesIterator();

        while(itStage.hasNext()){

            CQuestStage stStage = (CQuestStage)itStage.next();

            System.out.print(stStage.getID());
            System.out.print(". ");
            System.out.println(stStage.getCaption());

            ListIterator<CQuestAnswer> itAnswer = stStage.GetAnswersIterator();

            System.out.print("\t(");

            while(itAnswer.hasNext()){

                CQuestAnswer qaAnswer = (CQuestAnswer)itAnswer.next();

                System.out.print(qaAnswer.getCaption() + " | " + Integer.toString( qaAnswer.getValue())+"\t");

            }

            System.out.print(")");

            System.out.print("\n");

        }
    }

    
    public static final String TAG_JPOLLFXDOC = "jpollfxdoc";
    public static final String TAG_VERSION = "version";
    public static final String TAG_ANSWERSLIST = "answerslist";
    public static final String TAG_NAME = "name";
    public static final String TAG_ALTERNATIVE = "alternative";
    public static final String TAG_ALTERNATIVENAME = "alternativename";
    
    public static final String TAG_QUESTIONNAIRE = "questionnaire";
    public static final String TAG_STAGE = "stage";
    public static final String TAG_CAPTION = "caption";
    public static final String TAG_NEXT_STAGE = "nextstage";
    public static final String TAG_TYPE = "type";
    public static final String TAG_ID = "id";
    
    
    public static final String VER_1_0 = "1.0";
    
    public static final String VER_CURRENT = VER_1_0;
    
    /**
     * Saves list of answers to XML file
     * @param lstAnswers
     * @param sName
     * @param fileDestination
     * @return true if file was saved successfully
     */
    public static boolean SaveAnswers(List<CQuestAnswer> lstAnswers, String sName, File fileDestination){
        
        // instance of a DocumentBuilderFactory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try{

            // use factory to get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();

            // create instance of DOM
            Document dom = db.newDocument();    
            
            // create the root element
            Element eleRoot = dom.createElement(TAG_JPOLLFXDOC);
            eleRoot.setAttribute(TAG_VERSION, VER_CURRENT);
            
            Element eleList = dom.createElement(TAG_ANSWERSLIST);
            eleList.setAttribute(TAG_NAME, sName);
            eleRoot.appendChild(eleList);

            for(CQuestAnswer ansCurr:lstAnswers){
             
                Element e = dom.createElement(TAG_ALTERNATIVE);
                e.setTextContent(ansCurr.getCaption());
                eleList.appendChild(e);
            }
            
            dom.appendChild(eleRoot);            
            
            try {
                Transformer tr = TransformerFactory.newInstance().newTransformer();
                tr.setOutputProperty(OutputKeys.INDENT, "yes");
                tr.setOutputProperty(OutputKeys.METHOD, "xml");
                tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

                // send DOM to file
                tr.transform(new DOMSource(dom), 
                                new StreamResult(new FileOutputStream(fileDestination)));

            } catch (TransformerException te) {
                System.out.println(te.getMessage());
                return false;
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
                return false;
            }            
            
        }
        catch (ParserConfigurationException pce) {
            System.out.println("UsersXML: Error trying to instantiate DocumentBuilder " + pce);
            return false;
        }
            
            
        return true;
    }
            
    /**
     * Loads alternatives list from XML file
     * @param selectedFile is XML file that contains queston answer alternatives
     * @return List with CQuestAnswers
    */
    public static List<CQuestAnswer> LoadAnswersList(File selectedFile)
    {
        
        // Make an  instance of the DocumentBuilderFactory
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
              
            List<CQuestAnswer> lstReturn = new ArrayList<>();
            
            // use the factory to take an instance of the document builder
            DocumentBuilder builder = factory.newDocumentBuilder();
        
            // parse using the builder to get the DOM mapping of the    
            // XML file
            Document document = builder.parse(selectedFile);

            // is it proper document
            NodeList nlRoot = document.getElementsByTagName(TAG_JPOLLFXDOC);
            if( (nlRoot == null) || (nlRoot.getLength() < 1)){
                return null;
            }
            
            NodeList nlAnswerList = ((Element)nlRoot.item(0)).getElementsByTagName(TAG_ANSWERSLIST);
            if(nlAnswerList == null){
                return null;
            }

            NodeList nlAlternatives = ((Element)nlAnswerList.item(0)).getElementsByTagName(TAG_ALTERNATIVE);
            if( (nlAlternatives == null) || (nlAlternatives.getLength() < 1)){
                return null;
            }

            for(int iNode = 0; iNode < nlAlternatives.getLength(); iNode++){
                String sAnswer = nlAlternatives.item(iNode).getTextContent();
                lstReturn.add(new CQuestAnswer(sAnswer));
            }            
            
            return lstReturn;

        } catch (ParserConfigurationException pce) {
            System.out.println(pce.getMessage());
            return null;
        } catch (SAXException se) {
            System.out.println(se.getMessage());
            return null;
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
            return null;
        }
    }

    /**
     * Saves questionnaire to XML file
     * @param qnrSource
     * @param fileDestination 
     * @return  Returns true if questionnaire was saved successfully
     */
    public static boolean SaveQuestionnaire(CQuestionnaire qnrSource, File fileDestination)
    {

        // If this path doesn't exist or is not writable
        if ( (fileDestination.exists() == true) && 
                (fileDestination.canWrite() == false) ){
            return false;
        }
       
        // instance of a DocumentBuilderFactory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try{

            // use factory to get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();

            // create instance of DOM
            Document dom = db.newDocument();    
            
            // create the root element
            Element eleRoot = dom.createElement(TAG_JPOLLFXDOC);
            eleRoot.setAttribute(TAG_VERSION, VER_CURRENT);

            // create questionnaire element
            Element eleQuestionnaire = dom.createElement(TAG_QUESTIONNAIRE);
            eleQuestionnaire.setAttribute(TAG_NAME, fileDestination.getName());
            
            eleRoot.appendChild(eleQuestionnaire);
            
            // create and add every question
            for(CQuestStage stgCurrent:qnrSource.getStageList()){
                
                // create stage element
                Element eleStage = dom.createElement(TAG_STAGE);
                eleStage.setAttribute(TAG_ID, stgCurrent.getID());
                eleStage.setAttribute(TAG_TYPE, stgCurrent.getStageType());
                
                // create stage caption
                Element eleCaption = dom.createElement(TAG_CAPTION);
                eleCaption.setTextContent(stgCurrent.getCaption());
                eleStage.appendChild(eleCaption);
                
                // create answerlist
                Element eleAnswerLst = dom.createElement(TAG_ANSWERSLIST);

                
                // create alternatives list
                for(CQuestAnswer qaCurrent:stgCurrent.getAnswerList()){
                    Element eleAlternative = dom.createElement(TAG_ALTERNATIVE);
                    
                    // alternative caption
                    Element eleAltCaption = dom.createElement(TAG_CAPTION);
                    eleAltCaption.setTextContent(qaCurrent.getCaption());
                    eleAlternative.appendChild(eleAltCaption);
                    
                    // alternative next stage
                    Element eleNextStage = dom.createElement(TAG_NEXT_STAGE);
                    eleNextStage.setTextContent((qaCurrent.getNextStage() == null)?"":qaCurrent.getNextStage().getID());
                    eleAlternative.appendChild(eleNextStage);
                    
                    eleAnswerLst.appendChild(eleAlternative);
                }
                
                eleStage.appendChild(eleAnswerLst);
                eleQuestionnaire.appendChild(eleStage);
            }
                
            
            
            dom.appendChild(eleRoot);            
            
            try {
                Transformer tr = TransformerFactory.newInstance().newTransformer();
                tr.setOutputProperty(OutputKeys.INDENT, "yes");
                tr.setOutputProperty(OutputKeys.METHOD, "xml");
                tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

                // send DOM to file
                tr.transform(new DOMSource(dom), 
                                new StreamResult(new FileOutputStream(fileDestination)));

            } catch (TransformerException te) {
                System.out.println(te.getMessage());
                return false;
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
                return false;
            }            
            
        }
        catch (ParserConfigurationException pce) {
            System.out.println("UsersXML: Error trying to instantiate DocumentBuilder " + pce);
            return false;
        }
            
            
        return true;
    }        
    
    public static CQuestionnaire LoadQuestionnaire(File selectedFile)
    {
        
        // Make an  instance of the DocumentBuilderFactory
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
              
            CQuestionnaire qnrResult = new CQuestionnaire();
            
            // use the factory to take an instance of the document builder
            DocumentBuilder builder = factory.newDocumentBuilder();
        
            // parse using the builder to get the DOM mapping of the    
            // XML file
            Document document = builder.parse(selectedFile);

            // is it proper document
            NodeList nlRoot = document.getElementsByTagName(TAG_JPOLLFXDOC);
            if( (nlRoot == null) || (nlRoot.getLength() < 1)){
                return null;
            }
            
            NodeList nlQuestionnaire = ((Element)nlRoot.item(0)).getElementsByTagName(TAG_QUESTIONNAIRE);
            if( (nlQuestionnaire == null) || (nlQuestionnaire.getLength() < 1)){
                return null;
            }
            
            NodeList nlStagesList = ((Element)nlQuestionnaire.item(0)).getElementsByTagName(TAG_STAGE);
            if( (nlStagesList == null) || (nlStagesList.getLength() < 1)){
                return null;
            }
            
            // reading stages. 
            // At firts - creating stages with IDs
            
            for(int iStage = 0; iStage <  nlStagesList.getLength(); iStage++){

                Element nodStage = (Element) nlStagesList.item(iStage);
                if(nodStage != null ){
                    
                    String sID = nodStage.getAttribute(TAG_ID);
                    
                    if( (sID != null) && (sID.equals("") == false) ) {
                    
                        String sType = nodStage.getAttribute(TAG_TYPE);
                        String sCaption = "";

                        Element eleCaption = (Element)nodStage.getElementsByTagName(TAG_CAPTION).item(0);
                        if (eleCaption != null){
                            sCaption = eleCaption.getTextContent();
                        }

                        System.out.println(sID + " | " + sCaption + " | " + sType);
                        
                        CQuestStage curStage = new CQuestStage();
                        curStage.setID(sID);
                        curStage.setCaption(sCaption);
                        curStage.setStageType(sType);

                        qnrResult.AddStage(curStage, null);
                        
                    }
                }
            }
            
            
            // reading alternatives
            // When stages are ready Next Stage pointers could be set
            for(int iStage = 0; iStage <  nlStagesList.getLength(); iStage++){

                Element nodStage = (Element) nlStagesList.item(iStage);
                if(nodStage != null ){
                    
                    String sID = nodStage.getAttribute(TAG_ID);
            
                    List<CQuestAnswer> lstAnswers = new ArrayList<>();
                    
                    NodeList nlAltList = nodStage.getElementsByTagName(TAG_ANSWERSLIST);
                    if ( (nlAltList != null) && (nlAltList.getLength() > 0) )
                    {

                        NodeList nlAltternatives = ((Element)nlAltList.item(0)).getElementsByTagName(TAG_ALTERNATIVE);
                        if( (nlAltternatives != null) && (nlAltternatives.getLength() > 0)){

                            for(int iAlternative = 0; iAlternative < nlAltternatives.getLength(); iAlternative++){

                                String sAltCaption = ((Element)nlAltternatives
                                                        .item(iAlternative)
                                                        .getChildNodes()).getElementsByTagName(TAG_CAPTION).item(0).getTextContent();

                                String sAltNext = ((Element)nlAltternatives
                                                        .item(iAlternative)
                                                        .getChildNodes()).getElementsByTagName(TAG_NEXT_STAGE).item(0).getTextContent();

                                System.out.println("\t" + sAltCaption);

                                CQuestAnswer qaAnswer = new CQuestAnswer(sAltCaption);
                                CQuestStage stgNext = qnrResult.getStageByID(sAltNext);
                                qaAnswer.setNextStage(stgNext);

                                lstAnswers.add(qaAnswer);
                            }                        

                        }
                        
                    }
                    
                    CQuestStage curStage = qnrResult.getStageByID(sID);
                    curStage.setItemsList(lstAnswers);
  
                }
               
            }
            
            
            return qnrResult;

        } catch (ParserConfigurationException pce) {
            System.out.println(pce.getMessage());
            return null;
        } catch (SAXException se) {
            System.out.println(se.getMessage());
            return null;
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
            return null;
        }
    }
}
