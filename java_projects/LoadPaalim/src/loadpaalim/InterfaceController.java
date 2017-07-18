/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loadpaalim;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import static loadpaalim.CDownloadProcessor.SaveXMLDBFromLinksList;
import static loadpaalim.CDownloadProcessor.SaveXMLDBFromWordsList;

/**
 * FXML Controller class
 *
 * @author vyrovoy
 */
public class InterfaceController implements Initializable {


    @FXML
    private Button btnDownloadLinks;
    
    @FXML
    private Button btnDownloadWordsList;
    
    @FXML
    private Button btnSavePresent;
    
    @FXML
    private ListView<String> lstErrorsView;
  
    @FXML
    private ListView<String> lstStatusView;
    
    @FXML
    private Button btnSetDBFile;
    
    @FXML
    private TextField edtDBFile;
    


    private ObservableList<String> lstErrorsData;
    private ObservableList<String> lstStatusData;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        assert btnDownloadLinks != null : "fx:id=\"btnDownloadLinks\" was not injected: check your FXML file 'Interface.fxml'.";
        assert lstErrorsView != null : "fx:id=\"lstErrorsView\" was not injected: check your FXML file 'Interface.fxml'.";
        assert lstStatusView != null : "fx:id=\"lstStatusView\" was not injected: check your FXML file 'Interface.fxml'.";
        assert btnSavePresent != null : "fx:id=\"btnSavePresent\" was not injected: check your FXML file 'Interface.fxml'.";
        assert btnSetDBFile != null : "fx:id=\"btnSetDBFile\" was not injected: check your FXML file 'Interface.fxml'.";
        assert edtDBFile != null : "fx:id=\"edtDBFile\" was not injected: check your FXML file 'Interface.fxml'.";
        
        lstErrorsData = FXCollections.observableArrayList();
        lstErrorsView.setItems(lstErrorsData);

        lstStatusData = FXCollections.observableArrayList();
        lstStatusView.setItems(lstStatusData);
        
        btnDownloadLinks.setOnAction(event -> handleBtnDownloadLinkList());
        btnDownloadWordsList.setOnAction(event -> handleBtnDownloadWordsList());
        btnSavePresent.setOnAction(event -> handleBtnSavePresent());
        btnSetDBFile.setOnAction(event -> handleSetDBFile());
    }    
    
    
    private void handleBtnDownloadLinkList(){
        
        String sSourceFile = ""; 
        
        FileChooser fchDialog = new FileChooser();
        fchDialog.setTitle("Select verbs list");
        fchDialog.getExtensionFilters().addAll(
               new ExtensionFilter("XML files", "*.xml") 
        );
        
        File selectedFile = fchDialog.showOpenDialog(btnDownloadLinks.getScene().getWindow());
        
        if (selectedFile != null )
        {
            try{
                sSourceFile  = selectedFile.getCanonicalPath();
                
            }catch(IOException ex){
                System.err.println(ex.getLocalizedMessage());
            }
        }
        
        String sDestinationFile = SaveXMLDBFromLinksList(sSourceFile, new iLogger() {
            @Override
            public void AddErrorMessage(String sMessage) {
                lstErrorsData.add(sMessage);
            }

            @Override
            public void SetCurrentStatus(String sMessage) {
                lstStatusData.add(sMessage);
            }
        });
        edtDBFile.setText(sDestinationFile);
    }

    private void handleBtnDownloadWordsList(){
        String sSourceFile = ""; 
        
        FileChooser fchDialog = new FileChooser();
        fchDialog.setTitle("Select verbs list");
        fchDialog.getExtensionFilters().addAll(
               new ExtensionFilter("XML files", "*.xml") 
        );
        
        File selectedFile = fchDialog.showOpenDialog(btnDownloadLinks.getScene().getWindow());
        
        if (selectedFile != null )
        {
            try{
                sSourceFile  = selectedFile.getCanonicalPath();
                
            }catch(IOException ex){
                System.err.println(ex.getLocalizedMessage());
            }
        }
        
        String sDestinationFile = SaveXMLDBFromWordsList(sSourceFile, new iLogger() {
            @Override
            public void AddErrorMessage(String sMessage) {
                lstErrorsData.add(sMessage);
            }

            @Override
            public void SetCurrentStatus(String sMessage) {
                lstStatusData.add(sMessage);
            }
        });
        edtDBFile.setText(sDestinationFile);        
    }
    
    private void handleBtnSavePresent(){
        
//        String sDestinationFile = "";
//        String sCurrentFolder = null;
//        
//        FileChooser fchDialog = new FileChooser();
//        fchDialog.setTitle("Select downloaded file");
//        fchDialog.getExtensionFilters().addAll(
//               new ExtensionFilter("XML files", "*.xml") 
//        );
//        
//        
//        File selectedFile = fchDialog.showOpenDialog(btnDownloadLinks.getScene().getWindow());
//        
//
//
//        if (selectedFile != null )
//        {
//            try{
//                sDestinationFile  = selectedFile.getCanonicalPath();
//                Path pathCurrent = Paths.get(selectedFile.getPath());
//                sCurrentFolder = pathCurrent.getParent().toString();
//                
//            }catch(IOException ex){
//                System.err.println(ex.getLocalizedMessage());
//            }
//        }
        
        String sDestinationFile = edtDBFile.getText();
        if(sDestinationFile.isEmpty()){
            return;
        }
        
        
        String sCurrentFolder = Paths.get(sDestinationFile).getParent().toString();
        
        String sPresentCardsFile = sCurrentFolder + "\\Verbs present cards.txt";
        
        CDownloadProcessor.SaveVerbsCardsPresent(sDestinationFile, sPresentCardsFile, new iLogger() {
            @Override
            public void AddErrorMessage(String sMessage) {
                lstErrorsData.add(sMessage);
            }

            @Override
            public void SetCurrentStatus(String sMessage) {
                lstStatusData.add(sMessage);
            }
        });
        
    }

    private void handleSetDBFile(){
        
        String sDestinationFile = edtDBFile.getText();
        String sCurrentFolder = null;
        
        FileChooser fchDialog = new FileChooser();
        fchDialog.setTitle("Select downloaded file");
        fchDialog.setInitialFileName(sDestinationFile);
        
        fchDialog.getExtensionFilters().addAll(
               new ExtensionFilter("XML files", "*.xml") 
        );
        
        
        File selectedFile = fchDialog.showOpenDialog(btnDownloadLinks.getScene().getWindow());
        
        if (selectedFile != null )
        {
            edtDBFile.setText(selectedFile.toString());
        }
        
    }
            

}


