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
import java.util.prefs.Preferences;
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
    private TextField txtPeriod1;

    @FXML
    private Button btnStart1;

    @FXML
    private TextField txtPeriod2;

    @FXML
    private Button btnStart2;

    @FXML
    private TextField txtPeriod3;

    @FXML
    private Button btnStart3;

    @FXML
    private TextField txtFile;

    @FXML
    private Button btnBrowse;

    @FXML
    private Button btnStart;

    @FXML
    private Button btnStop;
    
    private FolderWatcher _watcher;
    private String _fileName;
    
    private static final String PREF_LOCATION = "pref_folder";
    private static final String PREF_FILE_NAME = "pref_filename";
    private static final String PREF_PERIOD_1 = "pref_period_1";
    private static final String PREF_PERIOD_2 = "pref_period_2";
    private static final String PREF_PERIOD_3 = "pref_period_3";
    
    private static final String FILE_NAME = "text.txt";
    
    private static final int DEF_PERIOD_1 = 2700;
    private static final int DEF_PERIOD_2 = 1700;
    private static final int DEF_PERIOD_3 = 400;
    
    
    @FXML
    void initialize() {
        assert textContent != null : "fx:id=\"textFile\" was not injected: check your FXML file 'interface.fxml'.";
        assert txtFile != null : "fx:id=\"txtFile\" was not injected: check your FXML file 'interface.fxml'.";
        assert btnBrowse != null : "fx:id=\"btnBrowse\" was not injected: check your FXML file 'interface.fxml'.";
        assert btnStart != null : "fx:id=\"btnStart\" was not injected: check your FXML file 'interface.fxml'.";
        assert btnStop != null : "fx:id=\"btnStop\" was not injected: check your FXML file 'interface.fxml'.";
        
        loadParams();
        
        LogSaver.init(FileSystems.getDefault().getPath(txtFile.getText(), _fileName));
        
        btnBrowse.setOnAction(event -> selectFile());
        btnStart.setOnAction(event -> onBtnStart());
        btnStop.setOnAction(event -> onBtnStop());
    }
    
    @FXML
    public void exitApplication(ActionEvent event) {    }
    
    private void onBtnStart() {
        LogSaver.addChannel(1, "Channel #1", Integer.parseInt(txtPeriod1.getText()));
        LogSaver.runAllChannels();
    }
    
    private void onBtnStop() {
        LogSaver.removeChannel(1);
    }
   
    public void finilize() {
        LogSaver.removeAllChannels();
        
        if(_watcher != null) {
            _watcher.stopWatching();
        }  
        
        saveParams();
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
                LogSaver.init(FileSystems.getDefault().getPath(txtFile.getText(), _fileName));
                
                _watcher = new FolderWatcher(FileSystems.getDefault().getPath(folder), () -> {
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
        Path path = FileSystems.getDefault().getPath(filePathName, _fileName);
        
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
        
    private void saveParams() {
        Preferences prefs = Preferences.userNodeForPackage(InterfaceController.class);
        
        prefs.put(PREF_LOCATION, txtFile.getText());
        prefs.put(PREF_FILE_NAME, _fileName);
        
        prefs.putInt(PREF_PERIOD_1, Integer.parseInt(txtPeriod1.getText()));
        prefs.putInt(PREF_PERIOD_2, Integer.parseInt(txtPeriod2.getText()));
        prefs.putInt(PREF_PERIOD_3, Integer.parseInt(txtPeriod3.getText()));
   }
    
    private void loadParams() {
        Preferences prefs = Preferences.userNodeForPackage(InterfaceController.class);
        
        txtFile.setText(prefs.get(PREF_LOCATION, ""));
        _fileName = prefs.get(PREF_FILE_NAME, FILE_NAME);
        
        txtPeriod1.setText(Integer.toString(prefs.getInt(PREF_PERIOD_1, DEF_PERIOD_1)));
        txtPeriod2.setText(Integer.toString(prefs.getInt(PREF_PERIOD_2, DEF_PERIOD_2)));
        txtPeriod3.setText(Integer.toString(prefs.getInt(PREF_PERIOD_3, DEF_PERIOD_3)));
    }
}