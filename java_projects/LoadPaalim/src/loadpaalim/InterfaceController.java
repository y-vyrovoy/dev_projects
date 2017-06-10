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
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * FXML Controller class
 *
 * @author vyrovoy
 */
public class InterfaceController implements Initializable {

    
    @FXML
    private TableView<CPaalData> tblVerbs;

    @FXML
    private TableColumn<CPaalData, String> colRus;

    @FXML
    private TableColumn<CPaalData, String> colHebrew;

    @FXML
    private Button btnDownload;
    
    @FXML
    private Button btnSavePresent;


    private ObservableList<CPaalData> lstData;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        lstData = FXCollections.observableArrayList();
        
        tblVerbs.setItems(lstData);
        
        btnDownload.setOnAction(event -> handleBtnDownload());
        btnSavePresent.setOnAction(event -> voidhandleBtnSavePresent());
    }    
    
    
    private void handleBtnDownload(){
        
        String sSourceFile = ""; //C:\\Users\\vyrovoy.ATOFFICE\\Desktop\\Рідна мова\\grabbing verbs\\Hebrew verbs.xml";
        
        String sCurrentFolder = null;
        
        FileChooser fchDialog = new FileChooser();
        fchDialog.setTitle("Select verbs list");
        fchDialog.getExtensionFilters().addAll(
               new ExtensionFilter("XML files", "*.xml") 
        );
        
        
        File selectedFile = fchDialog.showOpenDialog(btnDownload.getScene().getWindow());
        
        if (selectedFile != null )
        {
            try{
                sSourceFile  = selectedFile.getCanonicalPath();
                Path pathCurrent = Paths.get(selectedFile.getPath());
                sCurrentFolder = pathCurrent.getParent().toString();
                
            }catch(IOException ex){
                System.err.println(ex.getLocalizedMessage());
            }
        }
        
        List<Map<String, String>> lstPaals = CDownloadProcessor.ProcessPaalims(sSourceFile);
        
        String sDestinationFile = sCurrentFolder + "\\Downloaded.xml";

        CDownloadProcessor.SaveXMLVerbs(lstPaals, new File(sDestinationFile));
        
    }
    
    private void voidhandleBtnSavePresent(){
        
        String sDestinationFile = "";
        String sCurrentFolder = null;
        
        FileChooser fchDialog = new FileChooser();
        fchDialog.setTitle("Select downloaded file");
        fchDialog.getExtensionFilters().addAll(
               new ExtensionFilter("XML files", "*.xml") 
        );
        
        
        File selectedFile = fchDialog.showOpenDialog(btnDownload.getScene().getWindow());
        
        if (selectedFile != null )
        {
            try{
                sDestinationFile  = selectedFile.getCanonicalPath();
                Path pathCurrent = Paths.get(selectedFile.getPath());
                sCurrentFolder = pathCurrent.getParent().toString();
                
            }catch(IOException ex){
                System.err.println(ex.getLocalizedMessage());
            }
        }
        
        String sPresentCardsFile = sCurrentFolder + "\\Verbs present cards.txt";
        
        CDownloadProcessor.SaveVerbsCardsPresent(sDestinationFile, sPresentCardsFile);
        
    }
    
}



