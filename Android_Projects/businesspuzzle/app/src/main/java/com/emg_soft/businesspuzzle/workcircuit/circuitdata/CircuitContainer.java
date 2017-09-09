package com.emg_soft.businesspuzzle.workcircuit.circuitdata;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Yura Vyrovoy on 9/4/2017.
 */

public class CircuitContainer {

    private static final String TAG = CircuitContainer.class.getSimpleName();

    private static final String TAG_ROOT = "business_circiut";
    private static final String TAG_INPUT_GROUP = "input_group";
    private static final String TAG_INPUT = "input";
    private static final String TAG_OPS_GROUP = "operators_group";
    private static final String TAG_OPERATOR = "operator";
    private static final String TAG_RES_GROUP = "result_group";
    private static final String TAG_RESULT = "result";

    private static final String TAG_NAME = "name";
    private static final String TAG_CAPTION = "caption";
    private static final String TAG_HEADER = "header";
    private static final String TAG_SUBHEADER = "subheader";

    private static final String TAG_TYPE = "type";
    private static final String TAG_IN_ONE = "in_one";
    private static final String TAG_IN_TWO = "in_two";
    private static final String TAG_IN = "in";

    private static final String TAG_TYPE_AND = "and";
    private static final String TAG_TYPE_OR = "or";
    private static final String TAG_TYPE_XOR = "xor";
    private static final String TAG_TYPE_NOT = "not";


    private String mName;
    private String mHeader;
    private String mSubheader;

    public CircuitContainer(){
    }

    private List<CircuitItem> lstItems = new ArrayList<>();
    private List<CircuitInput> lstInputs = new ArrayList<>();
    private List<CircuitOperator> lstOperators = new ArrayList<>();
    private List<CircuitResult> lstResults = new ArrayList<>();
    private List<List<CircuitItem>> lstOperatorLevels = null;
    private List<CircuitTrack> lstTracks = new ArrayList<>();





    // static list and tools to use it
    private static final List<CircuitContainer> lstCircuits = new ArrayList<>();

    public static void addCircuit(CircuitContainer circuit){
        lstCircuits.add(circuit);
    }

    public static CircuitContainer getCircuit(int item){
        return lstCircuits.get(item);
    }

    public static int getCircuitsCount(){
        return lstCircuits.size();
    }

    public static void removeAllCircuits(){
        lstCircuits.clear();
    }



    public static CircuitContainer createCurcuitFromXML(InputStream inStream) {

        CircuitContainer containerResult = new CircuitContainer();

        CircuitConnectionsMap conn = new CircuitConnectionsMap();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            // use the factory to take an instance of the document builder
            DocumentBuilder builder = factory.newDocumentBuilder();

            // parse using the builder to get the DOM mapping of the XML file
            Document document = builder.parse(inStream);

            // is it proper document
            NodeList nlRoot = document.getElementsByTagName(TAG_ROOT);
            if( (nlRoot == null) || (nlRoot.getLength() < 1)){
                return null;
            }

            // reading circuit parameters
            for(int iNode = 0; iNode < nlRoot.getLength(); iNode++){

                Element ele = (Element) nlRoot.item(iNode);

                containerResult.setName(ele.getAttribute(TAG_NAME));
                containerResult.setHeader(ele.getAttribute(TAG_HEADER));
                containerResult.setSubheader(ele.getAttribute(TAG_SUBHEADER));

            }

            // ---- ==== Loading inputs ==== ----

            NodeList rootInputsGroup = ((Element)nlRoot.item(0)).getElementsByTagName(TAG_INPUT_GROUP);
            if( (rootInputsGroup == null) || (rootInputsGroup.getLength() < 1)){
                return null;
            }

            NodeList rootInputsItems = ((Element)rootInputsGroup.item(0)).getElementsByTagName(TAG_INPUT);
            if( (rootInputsItems == null) || (rootInputsItems.getLength() < 1)){
                return null;
            }

            for(int iNode = 0; iNode < rootInputsItems.getLength(); iNode++){

                CircuitInput itemInput = new CircuitInput();
                itemInput.setCurcuit(containerResult);

                Element ele = (Element) rootInputsItems.item(iNode);

                itemInput.setName(ele.getAttribute(TAG_NAME));
                itemInput.setCaption(ele.getAttribute(TAG_CAPTION));

                containerResult.lstItems.add(itemInput);
                containerResult.lstInputs.add(itemInput);
            }


            // ---- ==== Loading operators ==== ----

            NodeList rootOperatorsGroup = ((Element)nlRoot.item(0)).getElementsByTagName(TAG_OPS_GROUP);
            if( (rootOperatorsGroup == null) || (rootOperatorsGroup.getLength() < 1)){
                return null;
            }

            NodeList rootOperatorsItems = ((Element)rootOperatorsGroup.item(0)).getElementsByTagName(TAG_OPERATOR);
            if( (rootOperatorsItems == null) || (rootOperatorsItems.getLength() < 1)){
                return null;
            }

            for(int iNode = 0; iNode < rootOperatorsItems.getLength(); iNode++){

                Element ele = (Element) rootOperatorsItems.item(iNode);
                String itemType = ele.getAttribute(TAG_TYPE);

                CircuitOperator itemOperator;

                if(itemType.equals(TAG_TYPE_AND)) {
                    itemOperator = new CircuitOperator(CircuitOperator.CircuitOperatorType.OP_AND);
                } else if(itemType.equals(TAG_TYPE_OR)) {
                    itemOperator = new CircuitOperator(CircuitOperator.CircuitOperatorType.OP_OR);
                } else if(itemType.equals(TAG_TYPE_XOR)) {
                    itemOperator = new CircuitOperator(CircuitOperator.CircuitOperatorType.OP_XOR);
                }else if(itemType.equals(TAG_TYPE_NOT)) {
                    itemOperator = new CircuitOperator(CircuitOperator.CircuitOperatorType.OP_NOT);
                } else {
                    itemOperator = new CircuitOperator(CircuitOperator.CircuitOperatorType.OP_NO_TYPE);
                }

                itemOperator.setCurcuit(containerResult);

                // adding new item to the lists
                itemOperator.setName(ele.getAttribute(TAG_NAME));
                itemOperator.setCaption(itemType);

                containerResult.lstItems.add(itemOperator);
                containerResult.lstOperators.add(itemOperator);

                // saving connection data to set up references later
                conn.addConnection(itemOperator.getName(),
                                    ele.getAttribute(TAG_IN_ONE),
                                    ele.getAttribute(TAG_IN_TWO));
            }


            // ---- ==== Loading results ==== ----

            NodeList rootResultsGroup = ((Element)nlRoot.item(0)).getElementsByTagName(TAG_RES_GROUP);
            if( (rootResultsGroup == null) || (rootResultsGroup.getLength() < 1)){
                return null;
            }

            NodeList rootResultsItems = ((Element)rootResultsGroup.item(0)).getElementsByTagName(TAG_RESULT);
            if( (rootResultsItems == null) || (rootResultsItems.getLength() < 1)){
                return null;
            }

            for(int iNode = 0; iNode < rootResultsItems.getLength(); iNode++){

                CircuitResult itemResult = new CircuitResult();

                itemResult.setCurcuit(containerResult);

                Element ele = (Element) rootResultsItems.item(iNode);
                itemResult.setName(ele.getAttribute(TAG_NAME));
                itemResult.setCaption(ele.getAttribute(TAG_CAPTION));

                containerResult.lstItems.add(itemResult);
                containerResult.lstResults.add(itemResult);

                // saving connection data to set up references later
                conn.addConnection(itemResult.getName(),
                                    ele.getAttribute(TAG_IN), "");

            }

            // ---- ==== let's setup references between items ==== ----
            containerResult.setConnections(conn);

            containerResult.createOpsLevelsLists();

        } catch (ParserConfigurationException | SAXException pce ) {
            System.out.println(pce.getMessage());
            return null;
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
            return null;
        }catch (Exception ex){
            ex.printStackTrace(System.out);
        }

        return containerResult;
    }

    private void setConnections(CircuitConnectionsMap conn){

        for(CircuitItem item : lstItems){

            String itemName = item.getName();
            String inOneName = conn.getInOne(itemName);
            String inTwoName = conn.getInTwo(itemName);

            if( (inOneName != null) && (!inOneName.isEmpty()) ){
                CircuitItem itemInOne = findItem(inOneName);
                item.setIn_one(itemInOne);
                lstTracks.add(new CircuitTrack(itemInOne, item));
            }

            if( (inTwoName != null) && (!inTwoName.isEmpty()) ){
                CircuitItem itemInTwo = findItem(inTwoName);
                item.setIn_two(itemInTwo);
                lstTracks.add(new CircuitTrack(itemInTwo, item));
            }
        }

    }

    // returns circuit item with particular name
    public CircuitItem findItem(String name){

        for(CircuitItem item : lstItems){
            if (item.getName() == name){
                return item;
            }
        }

        return null;
    }

    public void setInputs(Map<String, Boolean> mapInputValues){

        for(CircuitInput item : lstInputs){
            String n = item.getName();
            if(mapInputValues.containsKey(n)) {
                boolean b = mapInputValues.get(n);
                item.setValue(mapInputValues.get(item.getName()));
            }
        }
    }

    // arranges operators by levels according to distance from input
    public void createOpsLevelsLists(){

        List<List<CircuitItem>> lstReturn = new ArrayList<List<CircuitItem>>();

        List<CircuitItem> lstNotSorted = new ArrayList<>();
        lstNotSorted.addAll(lstOperators);

        List<CircuitItem> lstItemsLower = new ArrayList<>();
        lstItemsLower.addAll(lstInputs);

        int currentLevel = 1;

        // looking if items sources are on level lower
        while(lstNotSorted.size() > 0) {

            for (CircuitItem item : lstNotSorted) {

                // if both inputs are lower - adding item tu current level`
                if( (item.getIn_one() == null) && (item.getIn_two() != null)){

                }
                if ( ( (item.getIn_one() == null) ||
                        (lstItemsLower.indexOf(item.getIn_one()) >= 0) ) &&
                    ( (item.getIn_two() == null) ||
                                (lstItemsLower.indexOf(item.getIn_two()) >= 0) )) {

                    List<CircuitItem> lstCurrent;

                    // if there is no list for current level - create it
                    if (lstReturn.size() < currentLevel){
                        lstCurrent = new ArrayList<>();
                        lstReturn.add(lstCurrent);
                    }
                    else {
                        lstCurrent = lstReturn.get(currentLevel - 1);
                    }

                    lstCurrent.add(item);

                }
            }

            if( lstReturn.size() == currentLevel ) {
                lstNotSorted.removeAll(lstReturn.get(currentLevel - 1));
                lstItemsLower.addAll(lstReturn.get(currentLevel - 1));
                currentLevel++;
            }
            else{
                break;
            }
        }

        lstOperatorLevels = lstReturn;
    }

    public int getViewColumnsNumber(){

        if(lstOperatorLevels == null){
            return -1;
        }

        int nColumnsReturn = lstInputs.size();

        if( nColumnsReturn % (lstResults.size() + 1) != 0){
            nColumnsReturn *= lstResults.size() + 1;
        }

        for (List<CircuitItem> item : lstOperatorLevels) {
            int nItems = item.size();

            if(nItems <= 0){
                continue;
            }

            if( nColumnsReturn % (nItems + 1) != 0){
                nColumnsReturn *= nItems + 1;
            }
        }

        return nColumnsReturn;
    }

    public int getOperatorLevel(CircuitItem item){

        for(int iLevel = 0; iLevel < getLstOperatorLevels().size(); iLevel++){

            if(getLstOperatorLevels().get(iLevel).indexOf(item) >= 0){
                return iLevel;
            }
        }

        return -1;
    }


    // setters and getters

    public List<CircuitItem> getLstItems() {
        return lstItems;
    }

    public List<CircuitInput> getLstInputs() {
        return lstInputs;
    }

    public List<CircuitOperator> getLstOperators() {
        return lstOperators;
    }

    public List<CircuitResult> getLstResults() {
        return lstResults;
    }

    public List<List<CircuitItem>> getLstOperatorLevels() {
        return lstOperatorLevels;
    }

    public List<CircuitTrack> getLstTracks() {
        return lstTracks;
    }





    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }


    public String getSubheader() {
        return mSubheader;
    }

    public void setSubheader(String mSubheader) {
        this.mSubheader = mSubheader;
    }


    public String getHeader() {
        return mHeader;
    }

    public void setHeader(String mHeader) {
        this.mHeader = mHeader;
    }



    private static class CircuitConnectionsMap {

        Map<String, List<String>> mapItems;

        private CircuitConnectionsMap() {
            this.mapItems = new HashMap<String, List<String>>();
        }

        public void addConnection(String itemName,
                                  String nameInOne,
                                  String nameInTwo){

            List<String> lstIns = new ArrayList<>();
            lstIns.add(nameInOne);
            lstIns.add(nameInTwo);

            mapItems.put(itemName, lstIns);
        }

        // returns the first input name of itemName item
        public String getInOne(String itemName){

            if(mapItems.containsKey(itemName)) {
                return mapItems.get(itemName).get(0);
            }
            else{
                return null;
            }
        }

        // returns the second input name of itemName item
        public String getInTwo(String itemName){

            if(mapItems.containsKey(itemName)) {
                return mapItems.get(itemName).get(1);
            }
            else{
                return null;
            }
        }


    }


    /**
     * getInputsMap() initialize map with inputs values for <b>CircuitContainer</b>
     * @param inStream - opened stream with XML file
     * @return map of inputs initial state
     */
    public static Map<String, Boolean> getInputsMap(InputStream inStream){

        Map<String, Boolean> mapReturn = new HashMap<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            // use the factory to take an instance of the document builder
            DocumentBuilder builder = factory.newDocumentBuilder();

            // parse using the builder to get the DOM mapping of the XML file
            Document document = builder.parse(inStream);

            // is it proper document
            NodeList nlRoot = document.getElementsByTagName("circiut_inputs");
            if ((nlRoot == null) || (nlRoot.getLength() < 1)) {
                return null;
            }

            String circuitName = ((Element) nlRoot.item(0)).getAttribute(TAG_NAME);

            // reading inputs

            NodeList rootInputsItems = ((Element)nlRoot.item(0)).getElementsByTagName(TAG_INPUT);
            if( (rootInputsItems == null) || (rootInputsItems.getLength() < 1)){
                return null;
            }

            for(int iNode = 0; iNode < rootInputsItems.getLength(); iNode++){

                Element ele = (Element) rootInputsItems.item(iNode);

                String inputName = ele.getAttribute(TAG_NAME);
                Boolean inputValue = Boolean.valueOf(ele.getAttribute("value"));

                mapReturn.put(inputName, inputValue);
            }



        }catch (ParserConfigurationException | SAXException pce ) {
            System.out.println(pce.getMessage());
            return null;
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
            return null;
        }catch (Exception ex){
            ex.printStackTrace(System.out);
        }

        return mapReturn;

    }
}

