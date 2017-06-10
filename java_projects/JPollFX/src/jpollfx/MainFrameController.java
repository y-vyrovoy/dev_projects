/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpollfx;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;



public class MainFrameController 
{
    
    public static final String KW_NEW_STAGE_CAPTION = "New stage";
    public static final String KW_NEW_STAGE_ID = "A";
    public static final String KW_NEW_ALTERNATIVE = "New answer";

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;    
    
    // ------------- Stages Table Buttons --------------

    @FXML
    private Button btnStageAdd;
    
    @FXML
    private Button btnStageRemove;

    @FXML
    private Button btnStageUp;

    @FXML
    private Button btnStageDown;
 
    
    // ------------- Stages Table --------------
    
    @FXML
    private TableView<CQuestStage> tblStages;
    
    @FXML
    private TableColumn<CQuestStage, String> colStageID;

    @FXML
    private TableColumn<CQuestStage, String> colStageCaption;
    
    @FXML
    private TableColumn<CQuestStage, String> colStageType;
    

    // ------------- Alternatives Table --------------
    
    @FXML
    private TableView<CQuestAnswer> tblAlternatives;
    
    @FXML
    private TableColumn<CQuestAnswer, String> colAlternativeName;

    @FXML
    private TableColumn<CQuestAnswer, String> colAlternativeNext;
    

    // ------------- Stages Table Buttons --------------

    @FXML
    private Button btnAltAdd;

    @FXML
    private Button btnAltRemove;

    @FXML
    private Button btnAltUp;

    @FXML
    private Button btnAltDown;

    @FXML
    private Button btnAltSave;

    @FXML
    private Button btnAltLoad;

    // ------------- Common Buttons --------------

    @FXML
    private Button btnQuestionnaireSave;

    @FXML
    private Button btnQuestionnaireLoad;

    @FXML
    private Button btnQuestionnaireExport;

    @FXML
    private Button btnQnrNew ;
    
    // ---------
    
    
    private CQuestionnaire qnrData;
    private boolean m_bChanged;
    
    @FXML
    void initialize() 
    {
        assert tblStages != null : "fx:id=\"tblStages\" was not injected: check your FXML file 'frameSplitted.fxml'.";
        assert colStageID != null : "fx:id=\"colStageID\" was not injected: check your FXML file 'frameSplitted.fxml'.";
        assert colStageCaption != null : "fx:id=\"colStageCaption\" was not injected: check your FXML file 'frameSplitted.fxml'.";
        assert colStageType != null : "fx:id=\"colStageType\" was not injected: check your FXML file 'frameSplitted.fxml'.";

        assert btnStageAdd != null : "fx:id=\"btnStageAdd\" was not injected: check your FXML file 'frameSplitted.fxml'.";
        assert btnStageRemove != null : "fx:id=\"btnStageRemove\" was not injected: check your FXML file 'frameSplitted.fxml'.";
        assert btnStageUp != null : "fx:id=\"btnStageUp\" was not injected: check your FXML file 'frameSplitted.fxml'.";
        assert btnStageDown != null : "fx:id=\"btnStageDown\" was not injected: check your FXML file 'frameSplitted.fxml'.";
        
        
        assert tblAlternatives != null : "fx:id=\"tblAlternatives\" was not injected: check your FXML file 'frameSplitted.fxml'.";
        assert colAlternativeName != null : "fx:id=\"colAlternativeName\" was not injected: check your FXML file 'frameSplitted.fxml'.";
        assert colAlternativeNext != null : "fx:id=\"colAlternativeNext\" was not injected: check your FXML file 'frameSplitted.fxml'.";
        
        assert btnAltAdd != null : "fx:id=\"btnAltAdd\" was not injected: check your FXML file 'frameSplitted.fxml'.";
        assert btnAltRemove != null : "fx:id=\"btnAltRemove\" was not injected: check your FXML file 'frameSplitted.fxml'.";
        assert btnAltUp != null : "fx:id=\"btnAltUp\" was not injected: check your FXML file 'frameSplitted.fxml'.";
        assert btnAltDown != null : "fx:id=\"btnAltDown\" was not injected: check your FXML file 'frameSplitted.fxml'.";
        assert btnAltSave != null : "fx:id=\"btnAltSave\" was not injected: check your FXML file 'frameSplitted.fxml'.";
        assert btnAltLoad != null : "fx:id=\"btnAltLoad\" was not injected: check your FXML file 'frameSplitted.fxml'.";
        
        assert btnQuestionnaireSave != null : "fx:id=\"btnQuestionnaireSave\" was not injected: check your FXML file 'frameSplitted.fxml'.";
        assert btnStageRemove != null : "fx:id=\"btnStageRemove\" was not injected: check your FXML file 'frameSplitted.fxml'.";
        assert btnQuestionnaireExport != null : "fx:id=\"btnQuestionnaireExport\" was not injected: check your FXML file 'frameSplitted.fxml'.";
        assert btnQnrNew != null : "fx:id=\"btnQnrNew\" was not injected: check your FXML file 'frameSplitted.fxml'.";
        
        // ------- Stages buttons handlers
        btnStageAdd.setOnAction( event ->  AddEmptyStage() );                          
        btnStageRemove.setOnAction( event -> RemoveStage() );  
        btnStageUp.setOnAction( event -> MoveSelectedStageUp() );
        btnStageDown.setOnAction( event -> MoveSelectedStageDown() );

        
        // ------- Alternative buttons handlers
        btnAltAdd.setOnAction( event ->  AddEmptyAlternative() );                          
        btnAltRemove.setOnAction( event -> RemoveAlternative() );  
        btnAltUp.setOnAction( event -> MoveSelectedAlternativeUp());
        btnAltDown.setOnAction( event -> MoveSelectedAlternativeDown());
        
        btnAltSave.setOnAction( event -> SaveCurrentAnswersList());
        btnAltLoad.setOnAction( event -> LoadAnswersList());
        
        // ------- Common buttons handlers
        btnQnrNew.setOnAction( event -> CreateNewQuestionnaire());
        btnQuestionnaireSave.setOnAction( event -> SaveQuestionnaire());
        btnQuestionnaireLoad.setOnAction( event -> LoadQuestionnaire());
        btnQuestionnaireExport.setOnAction(event -> ExportQuestionnaire());
        
        // ------- Table view and data initiation
        SetupStagesTable();
        SetupAlternativeTable();       
        
        InitNewQuestionnaire(null);        
    }

    
    private void InitNewQuestionnaire(CQuestionnaire qnrNew)
    {
        if(qnrNew == null)
        {
            qnrData = new CQuestionnaire();
        }
        else
        {
            qnrData = qnrNew;
        }
        setQuestionnaireChanged(false);
        
        // ------- Setting up tables
        SyncStagesTableWithQustionnaire();
        SyncAlternativeTable(null);
        
        UpdateButtonsState();

    }
    
    private void SyncStagesTableWithQustionnaire()
    {

        ObservableList<CQuestStage> olData = FXCollections.observableList(qnrData.getStageList(), (CQuestStage param) -> {

            
                // !!!  params listeners are set here 
                param.paramSID().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    setQuestionnaireChanged(true);
                });

                param.paramSCaption().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    setQuestionnaireChanged(true);
                });
                
                
                param.paramSStageType().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    setQuestionnaireChanged(true);
                });                

                setQuestionnaireChanged(true);

                return new Observable[]{param.paramSID(),
                                        param.paramSStageType()
                };
        });
        
        olData.addListener((ListChangeListener.Change<? extends CQuestStage> c) -> {
            
            while (c.next()) 
            {
                if (c.wasAdded() || (c.wasUpdated()) || c.wasPermutated())  { 
                    setQuestionnaireChanged(true); 
                    SetupAltTableNextCombo(tblStages.getSelectionModel().getSelectedItem());
                }
                
                if (c.wasRemoved()) 
                {
                    c.getRemoved().forEach(item -> qnrData.RemoveStageFromAnswers(item) );
                    setQuestionnaireChanged(true);
                    SetupAltTableNextCombo(tblStages.getSelectionModel().getSelectedItem());
                }
            }
        });

        tblStages.setItems(olData);
    }
    
    
    private void SetupStagesTable()
    {
               
        // Stages table data 
        colStageID.setCellValueFactory((CellDataFeatures<CQuestStage, String> p) -> p.getValue().paramSID());
        colStageCaption.setCellValueFactory((CellDataFeatures<CQuestStage, String> p) -> p.getValue().paramSCaption());
        colStageType.setCellValueFactory((CellDataFeatures<CQuestStage, String> p) -> p.getValue().paramSStageType());
        
        colStageID.setCellFactory(TextFieldTableCell.forTableColumn());
        colStageCaption.setCellFactory(TextFieldTableCell.forTableColumn());
        colStageCaption.setOnEditCommit( (TableColumn.CellEditEvent<CQuestStage, String> t) -> t.getRowValue().setCaption(t.getNewValue()) );
        
        colStageType.setCellFactory(ComboBoxTableCell.forTableColumn( FXCollections.observableList(Arrays.asList(new String[]{"S", "M"})) ));
        colStageType.setOnEditCommit( (TableColumn.CellEditEvent<CQuestStage, String> t) -> t.getRowValue().setStageType(t.getNewValue()) );

        // ID MUST be unique so let's make user input safe
        colStageID.setOnEditCommit((TableColumn.CellEditEvent<CQuestStage, String> event) -> {

            if(qnrData.isStageIDUnique(event.getNewValue()) == false){
                event.getRowValue().setID(event.getNewValue());
            }
            else{
                event.getRowValue().setID(event.getOldValue());
            }
            
            colStageID.setVisible(false);
            colStageID.setVisible(true);
        });

        
        
        tblStages.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null)
            {
                SyncAlternativeTable(newValue.getAnswerList());
                SetupAltTableNextCombo(newValue);
                UpdateButtonsState();
            }
        });
    }
    
    private void SyncAlternativeTable(List<CQuestAnswer> lstAnswers)
    {
        if(lstAnswers != null)
        {
            ObservableList<CQuestAnswer> olData = FXCollections.observableList(lstAnswers, (CQuestAnswer param) -> {

                param.paramSCaption().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    setQuestionnaireChanged(true);
                });

                return new Observable[]{
                        param.paramSCaption(),
                };
            });

            olData.addListener((ListChangeListener.Change<? extends CQuestAnswer> c) -> {

                while (c.next()) 
                {
                    if (c.wasAdded()) {setQuestionnaireChanged(true);}
                    if (c.wasUpdated()) {setQuestionnaireChanged(true);}
                    if (c.wasPermutated()) {setQuestionnaireChanged(true);}
                    if (c.wasRemoved()) {setQuestionnaireChanged(true);}
                }
            });

            tblAlternatives.setItems(olData);
        }
        else
        {
            tblAlternatives.setItems(null);
        }
    }    
    
    private void SetupAlternativeTable()
    {
        // Alternatives table data 
        colAlternativeName.setCellValueFactory((CellDataFeatures<CQuestAnswer, String> p) -> p.getValue().paramSCaption());
        
        colAlternativeNext.setCellValueFactory((CellDataFeatures<CQuestAnswer, String> param) -> {
                return (param.getValue().getNextStage() == null)?   
                        new SimpleStringProperty(CQuestAnswer.NEXT_STAGE_NEXT):
                        param.getValue().getNextStage().paramSID();
        });
    
        colAlternativeName.setCellFactory(TextFieldTableCell.forTableColumn());
        colAlternativeName.setOnEditCommit( (TableColumn.CellEditEvent<CQuestAnswer, String> t) -> 
                                                                t.getRowValue().setCaption(t.getNewValue()) 
                                            );
        
        colAlternativeNext.setCellFactory(ComboBoxTableCell.forTableColumn(CreateComboNextSatgesList(null)));    
        colAlternativeNext.setOnEditCommit( (TableColumn.CellEditEvent<CQuestAnswer, String> t) -> {
                                                                t.getRowValue().setNextStage(qnrData.getStageByID(t.getNewValue()));
                                                                
                                                                // no ideas how to do this correctly :(
                                                                setQuestionnaireChanged(true);
                                                                UpdateButtonsState();
                                                            });

        tblAlternatives.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null)
            {
                UpdateButtonsState();
            }
        });
    }
    
    private void SetupAltTableNextCombo( CQuestStage stg )
    {
        colAlternativeNext.setCellFactory(ComboBoxTableCell.forTableColumn(CreateComboNextSatgesList(stg)));
    }
    
    private ObservableList<String> CreateComboNextSatgesList(CQuestStage stgCurrent)
    {
        ObservableList<String> lstReturn = FXCollections.observableArrayList();
        
        lstReturn.add(CQuestAnswer.NEXT_STAGE_NEXT);
        
        
        if(stgCurrent != null)
        {
            Iterator<CQuestStage> it = qnrData.getStageList().iterator();

            boolean bFound = false;
            while(it.hasNext())
            {
                CQuestStage stg = it.next();

                if(bFound == true)
                {
                    lstReturn.add(stg.getID());
                }

                if(stg.equals(stgCurrent))
                {
                    bFound = true;
                }
            }
        }
        
        return lstReturn;
    }
    
    private void AddEmptyStage()
    {
        int iAttempt = 0;
        String sNewID = "";
        String sNewCaption = "";
        
        do
        {
            sNewID = KW_NEW_STAGE_ID + ". " + Integer.toString(iAttempt);
            sNewCaption = KW_NEW_STAGE_CAPTION + " " + Integer.toString(iAttempt);
            iAttempt++;
        } 
        while (qnrData.isStageIDUnique(sNewID) == true);
        
        CQuestStage stgTmp = new CQuestStage();
        stgTmp.setID(sNewID);
        stgTmp.setCaption(sNewCaption);
        stgTmp.setStageType("S");
        
        tblStages.getItems().add(stgTmp);
        UpdateButtonsState();
    }
    
    private void RemoveStage()
    {
        tblStages.getItems().remove(tblStages.getSelectionModel().getSelectedIndex());

        UpdateButtonsState();
    }
    
    private void MoveSelectedStageUp()
    {
        try
        {
            Collections.swap(tblStages.getItems(), 
                            tblStages.getSelectionModel().getSelectedIndex(), 
                            tblStages.getSelectionModel().getSelectedIndex() - 1) ;
        }
        catch(IndexOutOfBoundsException ex){}
    }
    
    private void MoveSelectedStageDown()
    {
        try
        {
            Collections.swap(tblStages.getItems(), 
                            tblStages.getSelectionModel().getSelectedIndex(), 
                            tblStages.getSelectionModel().getSelectedIndex() + 1) ;
        }
        catch(IndexOutOfBoundsException ex){}
    }
 
    private static boolean AlternativeExists(List<CQuestAnswer> c, String sTest)
    {
        return c.stream().filter(o -> o.getCaption().equals(sTest)).findFirst().isPresent();
    }
        
    private void AddEmptyAlternative()
    {
        int iAttempt = 0;
        String sTest;
        
        do
        {
            sTest = KW_NEW_ALTERNATIVE + " " + Integer.toString(iAttempt++);
        } 
        while (AlternativeExists(tblAlternatives.getItems(), sTest) == true);
        
        CQuestAnswer ansTmp = new CQuestAnswer();

        ansTmp.setCaption(sTest);
        ansTmp.setNextStage(null);

        tblAlternatives.getItems().add(ansTmp);    
        
        UpdateButtonsState();
    }

    private void RemoveAlternative()
    {
        tblAlternatives.getItems().remove(tblAlternatives.getSelectionModel().getSelectedIndex());

        UpdateButtonsState();
    }

    private void MoveSelectedAlternativeUp()
    {
        try
        {
            Collections.swap(tblAlternatives.getItems(), 
                            tblAlternatives.getSelectionModel().getSelectedIndex(), 
                            tblAlternatives.getSelectionModel().getSelectedIndex() - 1) ;
        }
        catch(IndexOutOfBoundsException ex){}
    }
    
    private void MoveSelectedAlternativeDown()
    {
        try
        {
            Collections.swap(tblAlternatives.getItems(), 
                            tblAlternatives.getSelectionModel().getSelectedIndex(), 
                            tblAlternatives.getSelectionModel().getSelectedIndex() + 1) ;
        }
        catch(IndexOutOfBoundsException ex){}
    }    
    
    private void DisableStagesButtons(boolean bDisable)
    {
        btnStageRemove.setDisable(bDisable);
        btnStageDown.setDisable(bDisable);
        btnStageUp.setDisable(bDisable);
        btnAltAdd.setDisable(bDisable);
        btnAltLoad.setDisable(bDisable);
    }

    private void DisableAlternativesButtons(boolean bDisable)
    {
        btnAltRemove.setDisable(bDisable);
        btnAltUp.setDisable(bDisable);
        btnAltDown.setDisable(bDisable);
    }
    
    
    private void UpdateButtonsState()
    {
        DisableStagesButtons(tblStages.getSelectionModel().isEmpty() == true);
        DisableAlternativesButtons(tblAlternatives.getSelectionModel().isEmpty() == true);
        
        btnAltSave.setDisable( (tblStages.getSelectionModel().isEmpty() == true) || 
                                (tblAlternatives.getItems().isEmpty() == true));
    }

    
    private void  CreateNewQuestionnaire()
    {
        if( getQuestionnaireChanged() == true)
        {
            new Alert(Alert.AlertType.ERROR, "Questionnaire is not saved.\nDo you want to save it?", ButtonType.YES, ButtonType.NO)
                    .showAndWait()
                    .filter(response -> response == ButtonType.YES)
                    .ifPresent(response -> SaveQuestionnaire());
        }
        
        InitNewQuestionnaire(null);
    }
    
    private void setQuestionnaireChanged(boolean bChanged)
    {
        m_bChanged = bChanged;
        btnQuestionnaireSave.setDisable(bChanged == false);
    }
    
    private boolean getQuestionnaireChanged()
    {
        return m_bChanged;
    }
    
    private void SaveQuestionnaire()
    {   
        FileChooser fchDialog = new FileChooser();
        fchDialog.setTitle("Save Questionnaire");
        fchDialog.getExtensionFilters().addAll(
               new ExtensionFilter("JPollFX questionnaires", "*.jpq") 
        );
        
        
        File selectedFile = fchDialog.showSaveDialog(btnQuestionnaireSave.getScene().getWindow());
        
        if (selectedFile != null) 
        {
            if (CQuestMachineCore.SaveQuestionnaire(qnrData, selectedFile) == true){
                setQuestionnaireChanged(false);
            }
        }
        
        
    }
    
    private void LoadQuestionnaire()
    {
        if( getQuestionnaireChanged() == true)
        {
            new Alert(Alert.AlertType.ERROR, "Questionnaire is not saved.\nDo you want to save it?", ButtonType.YES, ButtonType.NO)
                    .showAndWait()
                    .filter(response -> response == ButtonType.YES)
                    .ifPresent(response -> SaveQuestionnaire());
        }
        
        FileChooser fchDialog = new FileChooser();
        fchDialog.setTitle("Load Questionnaire");
        fchDialog.getExtensionFilters().addAll(
               new ExtensionFilter("JPollFX questionnaires", "*.jpq") 
        );
        File selectedFile = fchDialog.showOpenDialog(btnQuestionnaireLoad.getScene().getWindow());            

        CQuestionnaire qnrNew = CQuestMachineCore.LoadQuestionnaire(selectedFile);
        if(qnrNew != null){
            InitNewQuestionnaire(qnrNew);
        }
        else{
            new Alert(Alert.AlertType.ERROR, "Quetionnaire was not loaded", ButtonType.OK).showAndWait();
        }
        
    }
    
    private void SaveCurrentAnswersList()
    {
        FileChooser fchDialog = new FileChooser();
        fchDialog.setTitle("Save answers list");
        fchDialog.getExtensionFilters().addAll(
               new ExtensionFilter("JPollFX answers lists", "*.jpa") 
        );
        
        
        File selectedFile = fchDialog.showSaveDialog(btnAltSave.getScene().getWindow());
        
        if (selectedFile != null) 
        {
            String sListName = selectedFile.getName().substring(0, selectedFile.getName().lastIndexOf("."));
            
            if(CQuestMachineCore.SaveAnswers(tblAlternatives.getItems(), sListName, selectedFile) == false)
            {
                new Alert(Alert.AlertType.ERROR, "Alternatives list was not saves correctly", ButtonType.OK).showAndWait();
            }
        }        
    }
    
    private void LoadAnswersList()
    {
        
        FileChooser fchDialog = new FileChooser();
        fchDialog.setTitle("Save answers list");
        fchDialog.getExtensionFilters().addAll(
               new ExtensionFilter("JPollFX answers lists", "*.jpa") 
        );
        
        
        File selectedFile = fchDialog.showOpenDialog(btnAltSave.getScene().getWindow());
        
        if ( (selectedFile != null) && (tblStages.getSelectionModel().getSelectedItem() != null) )
        {
            List<CQuestAnswer> lst = CQuestMachineCore.LoadAnswersList(selectedFile);
            if (lst != null)
            {
                //tblStages.getSelectionModel().getSelectedItem().setItemsList(lst);
                tblStages.getSelectionModel().getSelectedItem().setItemsList(lst);
                SyncAlternativeTable(tblStages.getSelectionModel().getSelectedItem().getAnswerList());
                UpdateButtonsState();
            }
        }
    }
    
    private void ExportQuestionnaire()
    {
        FileChooser fchDialog = new FileChooser();
        fchDialog.setTitle("Export questionnaire to CSV");
        fchDialog.getExtensionFilters().addAll(
               new ExtensionFilter("CSV files", "*.csv") 
        );

        File selectedFile = fchDialog.showSaveDialog(btnAltSave.getScene().getWindow());
        
        if (selectedFile != null) 
        {
            CQuestMachineCore.SaveQuestionnaireToCSV(qnrData, Paths.get(selectedFile.getAbsolutePath()));
        }
    }
    
}
