/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loadpaalim;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import static loadpaalim.CHtmlParseProcessor.SearchForWordAtSite;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;



/**
 *
 * @author vyrovoy
 */
public class CDownloadProcessor {
    
    private CDownloadProcessor(){
        
    }
    
    public static final String TAG_DATA_SET = "paalim";
    public static final String TAG_DATA_UNIT = "paal";
    public static final String TAG_ID = "id";
    public static final String TAG_VERB = "verb";
    public static final String TAG_URL = "link";
    public static final String TAG_WORD = "word";
    
    public static final String TAG_BODY = "body";
    public static final String TAG_P = "p";
    public static final String TAG_DIV = "div";
    
    public static String SaveXMLDBFromLinksList(String sSourceFile, iLogger logger){
        
        Path p = Paths.get(sSourceFile);
        Path sCurrentFolder = p.getParent();
        String sDestinationFile = sCurrentFolder + "\\Downloaded.xml";

        
        File fileSelected = new File(sSourceFile);
        if(fileSelected.canRead() == false){
            System.out.println("Can't read file " + fileSelected.getAbsolutePath());
            return null;
        }
        
        List<String> lstURLs = ReadXMLVerbsList(fileSelected, logger);      
        
        List<Map<String, String>> lstPaals = 
                CDownloadProcessor.ProcessPaalimsFromList(lstURLs, logger);        
        
        CDownloadProcessor.SaveXMLVerbs(lstPaals, new File(sDestinationFile), logger);  
        
        return sDestinationFile;
    }
    
    public static String SaveXMLDBFromWordsList(String sSourceFile, iLogger logger){
        
        Path p = Paths.get(sSourceFile);
        Path sCurrentFolder = p.getParent();
        String sDestinationFile = sCurrentFolder + "\\Downloaded.xml";
        
        File fileSelected = new File(sSourceFile);
        if(fileSelected.canRead() == false){
            System.out.println("Can't read file " + fileSelected.getAbsolutePath());
            return null;
        }
        
        List<String> lstURLs = SearchXMLVerbsList(fileSelected, logger); 
        
        List<Map<String, String>> lstPaals = 
                CDownloadProcessor.ProcessPaalimsFromList(lstURLs, logger);        
        
        CDownloadProcessor.SaveXMLVerbs(lstPaals, new File(sDestinationFile), logger);  
        
        return sDestinationFile;
    }
    
    public static List<Map<String, String>> ProcessPaalimsFromList(List<String> lstURLs, iLogger logger){

        
        List<Map<String, String>> lstResults = new ArrayList<>();
        
        if(lstURLs.size() > 0){

            lstURLs.stream().forEach((u) -> {
                                            Map<String, String> result = CHtmlParseProcessor.ParseWebPage(u, logger);
                                            if( result != null){
                                                lstResults.add(result);
                                            }
                                        });
        

        }
        
        return lstResults;
            
    }

    
    public static List<String> ReadXMLVerbsList(File fileSelected, iLogger logger){
       
        // Make an  instance of the DocumentBuilderFactory
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            
            List<String> lstURLs = new ArrayList<>();
            
            // use the factory to take an instance of the document builder
            DocumentBuilder builder = factory.newDocumentBuilder();
        
            // parse using the builder to get the DOM mapping of the    
            // XML file
            Document document = builder.parse(fileSelected);

            // is it proper document
            NodeList nlRoot = document.getElementsByTagName(TAG_DATA_SET);
            if( (nlRoot == null) || (nlRoot.getLength() < 1)){
                return null;
            }
            
            NodeList nlDataUnits = ((Element)nlRoot.item(0)).getElementsByTagName(TAG_DATA_UNIT);
            if( (nlDataUnits == null) || (nlDataUnits.getLength() < 1)){
                return null;
            }

            for(int iNode = 0; iNode < nlDataUnits.getLength(); iNode++){
                
                String sAnswer = ((Element)nlDataUnits.item(iNode).getChildNodes()).getElementsByTagName(TAG_URL).item(0).getTextContent();
                lstURLs.add(sAnswer);
            }            
            
            
            return lstURLs;

        } catch (ParserConfigurationException | SAXException pce) {
            System.out.println(pce.getMessage());
            return null;
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
            return null;
        }      
        
    }

    public static List<String> SearchXMLVerbsList(File fileSelected, iLogger logger){
        
        // Make an  instance of the DocumentBuilderFactory
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            
            List<String> lstURLs = new ArrayList<>();
            
            // use the factory to take an instance of the document builder
            DocumentBuilder builder = factory.newDocumentBuilder();
        
            // parse using the builder to get the DOM mapping of the    
            // XML file
            Document document = builder.parse(fileSelected);

            // is it proper document
            NodeList nlRoot = document.getElementsByTagName(TAG_DATA_SET);
            if( (nlRoot == null) || (nlRoot.getLength() < 1)){
                return null;
            }
            
            NodeList nlDataUnits = ((Element)nlRoot.item(0)).getElementsByTagName(TAG_DATA_UNIT);
            if( (nlDataUnits == null) || (nlDataUnits.getLength() < 1)){
                return null;
            }

            for(int iNode = 0; iNode < nlDataUnits.getLength(); iNode++){
                
                String sWord = ((Element)nlDataUnits.item(iNode).getChildNodes()).getElementsByTagName(TAG_WORD).item(0).getTextContent();
                String sURL = SearchForWordAtSite(sWord, logger);
                lstURLs.add(sURL);
            }            
            
            logger.SetCurrentStatus("Words search finished");
            return lstURLs;

        } catch (ParserConfigurationException | SAXException pce) {
            System.out.println(pce.getMessage());
            return null;
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
            return null;
        }   
    }
    
    public static boolean SaveXMLVerbs(List<Map<String, String>> lstVerbs, File fileDestination, iLogger logger){
        
        // instance of a DocumentBuilderFactory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try{

            // use factory to get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();

            // create instance of DOM
            Document dom = db.newDocument();    
            
            // create the root element
            Element eleRoot = dom.createElement("paalims");
            

            for(Map<String, String> curr : lstVerbs){
             
                Element eCurrVerb = dom.createElement("paal");
                
                Set<String> setKeys = curr.keySet();
                
                setKeys.forEach( (s) -> {
                    
                    Element eleVerbType = dom.createElement(s);
                    eleVerbType.setTextContent(curr.get(s));
                    eCurrVerb.appendChild(eleVerbType); 
                    
                });                
                eleRoot.appendChild(eCurrVerb);
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
            } catch (IOException ex) {
                System.out.println(ex.getLocalizedMessage());
                return false;
            }     
            
        
            
        }
        catch (ParserConfigurationException pce) {
            System.out.println("UsersXML: Error trying to instantiate DocumentBuilder " + pce);
            return false;
        }
            
            
        return true;
    }
    
    public static boolean SaveVerbsCardsPresent(String sSourceFile, String sDestinationFile, iLogger logger){
        
        BufferedWriter bwDestination = null;
                
        // Make an  instance of the DocumentBuilderFactory
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            
            // use the factory to take an instance of the document builder
            DocumentBuilder builder = factory.newDocumentBuilder();
        
            // parse using the builder to get the DOM mapping of the    
            // XML file
            Document document = builder.parse(new File(sSourceFile));

            // is it proper document
            NodeList nlRoot = document.getElementsByTagName("paalims");
            if( (nlRoot == null) || (nlRoot.getLength() < 1)){
                return false;
            }
            
            NodeList nlDataUnits = ((Element)nlRoot.item(0)).getElementsByTagName("paal");
            if( (nlDataUnits == null) || (nlDataUnits.getLength() < 1)){
                return false;
            }

            bwDestination = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sDestinationFile, false), Charset.forName("UTF-8").newEncoder()));
            
            

            int iNode;
            for(iNode = 0; iNode < nlDataUnits.getLength(); iNode++){

                String sTranlation = ((Element)nlDataUnits.item(iNode).getChildNodes()).getElementsByTagName("verb_translation").item(0).getTextContent();
                sTranlation = sTranlation.replaceAll("; ", "<br>");
                
                String sCard = sTranlation + ";";
                
                sCard += "<span class=\"annotation\">(inf)</span> " + ((Element)nlDataUnits.item(iNode).getChildNodes()).getElementsByTagName("verb_infinitive").item(0).getTextContent() + "<br>";
                sCard += "<hr>";
                sCard += "<span class=\"annotation\">(m.s)</span> " + ((Element)nlDataUnits.item(iNode).getChildNodes()).getElementsByTagName("present_ms").item(0).getTextContent() + "<br>";
                sCard += "<span class=\"annotation\">(f.s)</span> " + ((Element)nlDataUnits.item(iNode).getChildNodes()).getElementsByTagName("present_fs").item(0).getTextContent() + "<br>";
                sCard += "<span class=\"annotation\">(m.p)</span> " + ((Element)nlDataUnits.item(iNode).getChildNodes()).getElementsByTagName("present_mp").item(0).getTextContent() + "<br>";
                sCard += "<span class=\"annotation\">(f.p)</span> " + ((Element)nlDataUnits.item(iNode).getChildNodes()).getElementsByTagName("present_fp").item(0).getTextContent() + "<br>";
                
                sCard += "<br>";
                
                sCard += "<span class=\"annotation\">root</span> " + ((Element)nlDataUnits.item(iNode).getChildNodes()).getElementsByTagName("verb_root").item(0).getTextContent();

                bwDestination.write(sCard + "\r\n");
                bwDestination.flush();
                
                System.out.println("Flushed: " + Integer.toString(iNode) +". "+ sTranlation);
                
            }
            
            bwDestination.close();
            
            System.out.println(iNode);
            
            return true;

        } catch (ParserConfigurationException | SAXException pce) {
            System.out.println(pce.getMessage());
            //oswDestination.close();
            return false;
        }
        //oswDestination.close();
         catch (IOException ioe) {
            System.err.println(ioe.getMessage());
            //oswDestination.close();
            return false;
        }        
    }
}
