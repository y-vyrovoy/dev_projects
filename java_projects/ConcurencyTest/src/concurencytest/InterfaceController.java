/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package concurencytest;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

public class InterfaceController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;
    
    @FXML
    private TextArea textContent;    
    
    @FXML
    private TextField txtFile;    

    @FXML
    private Button btnBrowse;

    @FXML
    private Button btnDo;
    
    private FolderWatcher _watcher;

    @FXML
    void initialize() {
        assert textContent != null : "fx:id=\"textFile\" was not injected: check your FXML file 'interface.fxml'.";
        assert txtFile != null : "fx:id=\"txtFile\" was not injected: check your FXML file 'interface.fxml'.";
        assert btnBrowse != null : "fx:id=\"btnBrowse\" was not injected: check your FXML file 'interface.fxml'.";
        assert btnDo != null : "fx:id=\"btnDo\" was not injected: check your FXML file 'interface.fxml'.";
        
        btnBrowse.setOnAction(event -> selectFile());
        btnDo.setOnAction(event -> onBtnDo());
    }
    
    @FXML
    public void exitApplication(ActionEvent event) {    }
    
    private void onBtnDo() {
        _watcher.stopWatching();
    }
    
    public void stopWatcher() {
        if(_watcher != null) {
            _watcher.stopWatching();
        }  
    }
    
    private void selectFile() {
        
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Select Folder");
        File selectedFile = dirChooser.showDialog(btnBrowse.getScene().getWindow());
        
        if (selectedFile != null)
        {
            try{
                String folder = selectedFile.getCanonicalPath();
                txtFile.setText( folder );
                Path path = FileSystems.getDefault().getPath(folder);
                
                _watcher = new FolderWatcher(path, () -> {
                    refreshFile();
                });
                _watcher.startWatching();
                
            }catch(IOException ex){
                System.err.println(ex.getLocalizedMessage());
            }
        }        
    }
    
    private void refreshFile() {
        
        String filePathName = txtFile.getText();
        Path path = FileSystems.getDefault().getPath(filePathName, "text.txt");
        
        try {
            StringBuilder builder = new StringBuilder();
            Files.lines(path, Charset.defaultCharset()).forEach((t) -> {
                builder.append(t).append("\n");
            });
            
            textContent.setText(builder.toString());
            
        } catch (IOException ex) {
            ex.printStackTrace(System.out);
        }
    }
    
 
}