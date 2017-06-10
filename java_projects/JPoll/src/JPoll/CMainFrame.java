package JPoll;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

import sun.applet.resources.MsgAppletViewer_pt_BR;



public class CMainFrame extends Frame implements ActionListener 
{
	private static final long serialVersionUID = 20150925;
	
	private CQuestionnaire m_qnrData;

	public java.awt.List m_lstStages;
	public Button m_btnLoadFile;
	public Button m_btnCreateNewQuest;
	
	public Button m_btnAddQuestion;
	public Button m_btnRemoveQuestion;
	public Button m_btnMoveQuestionUp;
	public Button m_btnMoveQuestionDown;
	
	public Label m_lblCaption;
	public TextArea m_txtCaption;
	
	public Label m_lblType;
	public Panel m_pnlType;
	public Checkbox m_chSingle;
	public Checkbox m_chMultiple;
	
	public Label m_lblAnswerList;
	public CTwoColList m_lstTwo;
	
	public Button m_btnAddAnswer;
	public Button m_btnRemoveAnswer;
	public Button m_btnEditAnswer;
	
	public Button m_btnLoadAnswerList;
	public Button m_btnSaveAnswerList;
	
	public Button m_btnMoveAnswerUp;
	public Button m_btnMoveAnswerDown;
	
	public Button m_btnSaveQuestionnaire;
	
	private CQuestStage m_stgCurrent;
	private int m_nCurrentSelectedItem;
	
	private String m_sCurrentAnswerIOPath;
	
	public static final String CMP_MAIN_FRAME = "CMP_MAIN_FRAME";
	

	public CMainFrame()
	{
		m_qnrData = new CQuestionnaire();
		m_stgCurrent = null;
		m_nCurrentSelectedItem = -1;
		
		m_sCurrentAnswerIOPath = "";
		
		CMainFrameLOM lmMain = new CMainFrameLOM();
		setLayout(lmMain);

		m_lstStages = new java.awt.List();

		m_lstStages.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) 
			{
				EnableAnswerControls(true);
				InitFrameFromListItem();
			}
		});

		
		InitStagesList();
		
		m_btnAddQuestion = new Button("+");
		m_btnRemoveQuestion = new Button("-");
		m_btnMoveQuestionUp = new Button("Up");
		m_btnMoveQuestionDown = new Button("Down");
		
		
		m_btnLoadFile = new Button("Load CSV file");
		m_btnCreateNewQuest = new Button("Create new questionnaire");
		
		m_lblCaption = new Label("Question caption");
		m_txtCaption = new TextArea("",1,0,TextArea.SCROLLBARS_VERTICAL_ONLY);
		
		m_lblType = new Label("Question type");
		
		CheckboxGroup grType = new CheckboxGroup();
		m_chSingle = new Checkbox("Single", grType, false);
		m_chMultiple = new Checkbox("Multiple", grType, false);
		
		m_pnlType = new Panel();
		m_pnlType.setLayout(new GridLayout(1,2));
		m_pnlType.add(m_chSingle);
		m_pnlType.add(m_chMultiple);
		
		m_lblAnswerList = new Label("Alternatives");
		m_lstTwo = new CTwoColList();
		
		m_btnAddAnswer = new Button("+");
		m_btnRemoveAnswer = new Button("-");
		m_btnEditAnswer = new Button("Edit");
		m_btnLoadAnswerList = new Button("Load");
		m_btnSaveAnswerList = new Button("Save");
		m_btnMoveAnswerUp = new Button("Up");
		m_btnMoveAnswerDown = new Button("Down");
		
		EnableAnswerControls(false);
		
		m_btnSaveQuestionnaire = new Button("Save questionnaire");
		
		
		add(m_lstStages);
		
		add(m_btnAddQuestion);
		add(m_btnRemoveQuestion);
		add(m_btnMoveQuestionUp);
		add(m_btnMoveQuestionDown);
		
		add(m_btnLoadFile);
		add(m_btnCreateNewQuest);
		
		add(m_lblCaption);
		add(m_txtCaption);
		
		add(m_lblType);
		add(m_pnlType);
		
		add(m_lblAnswerList);
		add(m_lstTwo);
		
		add(m_btnAddAnswer);
		add(m_btnRemoveAnswer);
		add(m_btnEditAnswer);
		
		add(m_btnLoadAnswerList);
		add(m_btnSaveAnswerList);
		
		add(m_btnMoveAnswerUp);
		add(m_btnMoveAnswerDown);
		
		add(m_btnSaveQuestionnaire);
		
		
		m_btnLoadFile.addActionListener(this);
		m_btnCreateNewQuest.addActionListener(this);
		m_btnAddQuestion.addActionListener(this);
		m_btnRemoveQuestion.addActionListener(this);
		m_btnMoveQuestionUp.addActionListener(this);
		m_btnMoveQuestionDown.addActionListener(this);
		
		m_btnSaveQuestionnaire.addActionListener(this);
		
		m_btnAddAnswer.addActionListener(this);
		m_btnRemoveAnswer.addActionListener(this);
		m_btnEditAnswer.addActionListener(this);

		m_btnLoadAnswerList.addActionListener(this);
		m_btnSaveAnswerList.addActionListener(this);
		
		m_btnMoveAnswerUp.addActionListener(this);
		m_btnMoveAnswerDown.addActionListener(this);
		
		m_txtCaption.addFocusListener(new FocusAdapter() {
			
			@Override
			public void focusLost(FocusEvent e)
			{
				if(m_stgCurrent == null)
				{
					return;
				}

				if(m_stgCurrent.getID().equals(m_txtCaption.getText()) == false)
				{
					m_stgCurrent.setID(m_txtCaption.getText());
					m_lstStages.replaceItem(m_stgCurrent.getID(), m_nCurrentSelectedItem);
					m_lstStages.select(m_nCurrentSelectedItem);
					
				}
			}
		});
		
		m_chSingle.addItemListener(new ItemListener(){
			 public void itemStateChanged(ItemEvent e)
			 {
				 UpdateStageType();
			 }
		});
		
		m_chMultiple.addItemListener(new ItemListener(){
			 public void itemStateChanged(ItemEvent e)
			 {
				 UpdateStageType();
			 }
		});
		
		lmMain.addLayoutComponent(CMP_MAIN_FRAME, (Component)this);
		
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
	}
	


	public CQuestionnaire getData()
	{
		return m_qnrData;
	}

	private void EnableAnswerControls(boolean bEnable)
	{
		m_btnRemoveQuestion.setEnabled(bEnable);
		m_btnMoveQuestionUp.setEnabled(bEnable);
		m_btnMoveQuestionDown.setEnabled(bEnable);
		
		m_txtCaption.setEnabled(bEnable);

		m_chSingle.setEnabled(bEnable);
		m_chMultiple.setEnabled(bEnable);
		
		m_lstTwo.setEnabled(bEnable);
		
		EnableAnswerButtons(bEnable);
	}
	
	
	/**
	 * Clears stages list and fills it from m_qnrData
	 */
	private void InitStagesList()
	{
		m_lstStages.removeAll();
		Iterator<CQuestStage> it = m_qnrData.getStagesIterator();
		
		while (it.hasNext() == true)
		{
			CQuestStage stgCurrent = (CQuestStage)it.next(); 
			m_lstStages.add(stgCurrent.getID());
		}

	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if( e.getSource() == m_btnLoadFile  )
		{
			LoadCSV();
		}
		else if(e.getSource() == m_btnCreateNewQuest)
		{
			m_qnrData = new CQuestionnaire();
			ResetFrame();
		}
		else if (e.getSource() == m_btnAddQuestion)
		{
			AddQuestion();
		}
		else if (e.getSource() == m_btnRemoveQuestion)
		{
			RemoveCurrentQuestion();
			InitFrameFromListItem();
		}
		else if (e.getSource() == m_btnMoveQuestionUp)
		{
			MoveQuestionUp();
		}
		else if (e.getSource() == m_btnMoveQuestionDown)
		{
			MoveQuestionDown();
		}
		else if(e.getSource() == m_btnAddAnswer)
		{
			ShowAddAnswerDlg(false);
		}
		else if(e.getSource() == m_btnEditAnswer)
		{
			ShowAddAnswerDlg(true);
		}
		else if(e.getSource() == m_btnLoadAnswerList)
		{
			LoadAnswerList();
		}
		else if(e.getSource() == m_btnSaveAnswerList)
		{
			SaveAnswerList();
		}
		else if(e.getSource() == m_btnRemoveAnswer)
		{
			RemoveAnswer();
		}
		else if(e.getSource() == m_btnMoveAnswerUp)
		{
			MoveAnswerUp();
		}
		else if(e.getSource() == m_btnMoveAnswerDown)
		{
			MoveAnswerDown();
		}
		else if(e.getSource() == m_btnSaveQuestionnaire)
		{
			SaveQuestionnaire();
		}
		
		
	}
	

	
	private void UpdateStageType()
	{
		if(m_stgCurrent == null)
		{
			return;
		}
		
		if(m_chMultiple.getState() == true)
		{
			m_stgCurrent.setStageType(CQuestStage.enmStageType.ST_MULTY_ANSWER);
		}
		else if(m_chSingle.getState() == true)
		{
			m_stgCurrent.setStageType(CQuestStage.enmStageType.ST_SINGLE_ANSWER);
		}
		
	}
	
	
	private void RefreshAnswerList()
	{
		m_lstTwo.RemoveAll();
		Iterator<CQuestAnswer> itAnswers = m_stgCurrent.GetAnswersIterator();
		while(itAnswers.hasNext() == true)
		{
			CQuestAnswer qaCurrrent = itAnswers.next();
			
			if(qaCurrrent.getNextStage() != null)
			{	
				m_lstTwo.AddItem(qaCurrrent.getCaption(), qaCurrrent.getNextStage().getID());
			}
			else
			{
				m_lstTwo.AddItem(qaCurrrent.getCaption(), CQuestAnswer.NEXT_STAGE_NEXT);
			}
			
		}
	}
	
	/**
	 * Inits all  controls with data that corresponds to selected item in stages list
	 */
	private void InitFrameFromListItem()
	{
		if(m_lstStages.getSelectedIndex() < 0)
		{
			return;
		}
		
		Iterator<CQuestStage> it = m_qnrData.getStagesIterator();
		int i = 0;
		
		while(it.hasNext() && i < m_lstStages.getSelectedIndex())
		{
			it.next();
			i++;
		}
		
		if(i == m_lstStages.getSelectedIndex())
		{
			m_stgCurrent = it.next();
			m_nCurrentSelectedItem = i;

			// Caption
			m_txtCaption.setText(m_stgCurrent.getID());
			
			// Question type
			if(m_stgCurrent.getStageType() == CQuestStage.enmStageType.ST_SINGLE_ANSWER)
			{
				m_chSingle.setState(true);
			}
			else if (m_stgCurrent.getStageType() == CQuestStage.enmStageType.ST_MULTY_ANSWER)
			{
				m_chMultiple.setState(true);
			}
			else
			{
				m_chSingle.getCheckboxGroup().setSelectedCheckbox(null);
			}
			
			// Alternatives
			RefreshAnswerList();
		}
		
	}
	
	
	/**
	 * Inits pointer to CQuestStage and m_nCurrentSelectedItem 
	 * corresponding with the item that is selected in stages list
	 */
	private void InitCurrentStage()
	{
		m_stgCurrent = null;
		m_nCurrentSelectedItem = -1;
		
		int i = -1;
		
		if( m_lstStages.getSelectedIndex() >= 0)
		{
			Iterator<CQuestStage> it = m_qnrData.getStagesIterator();
			CQuestStage stgCurrent = null;
			
			while(it.hasNext() == true)
			{
				stgCurrent  = it.next();
				i++;
				if( stgCurrent.getID().equals(m_lstStages.getSelectedItem()) )
				{
					m_stgCurrent = stgCurrent;
					m_nCurrentSelectedItem = i;
					return;
				}
			}
		}
	}

	private void ResetFrame()
	{
		m_lstStages.removeAll();
		m_lstTwo.RemoveAll();
		m_txtCaption.setText("");
		
		m_chSingle.getCheckboxGroup().setSelectedCheckbox(null);
	}	
	
	private void AddQuestion()
	{
		InitCurrentStage();
		CQuestStage stgNew = new CQuestStage();
		String sNewName = "New Item";
		while(m_qnrData.isStageIDUnique(sNewName) == false)
		{
			sNewName = sNewName + "1";
		}
		
		stgNew.setID(sNewName);
		stgNew.setStageType(CQuestStage.enmStageType.ST_SINGLE_ANSWER);
		m_qnrData.AddStage(stgNew, m_stgCurrent);
		
		m_lstStages.add(stgNew.getID(), m_qnrData.getStageNumber(m_stgCurrent) );
		InitCurrentStage();
		
	}
	
	private void RemoveCurrentQuestion()
	{
		int iCurrentItem = m_lstStages.getSelectedIndex();
		if (iCurrentItem >= 0)
		{
			ListIterator<CQuestStage> it = m_qnrData.getStagesIterator();
			while(it.hasNext() == true)
			{
				CQuestStage qsTmp = it.next();
				if (qsTmp.getID() == m_lstStages.getSelectedItem())
				{
					it.remove();
				}
			}
			
			InitStagesList();
			m_lstStages.select(iCurrentItem-1 >= 0 ? iCurrentItem-1 : 0);
		}
		
	}
	
	private void MoveQuestionUp()
	{
		if(m_lstStages.getSelectedIndex() < 0)
		{
			return;
		}
		
		if (m_stgCurrent.getID().equals(m_lstStages.getSelectedItem()) == false)
		{
			InitCurrentStage();
		}
		
		 ListIterator<CQuestStage> it = m_qnrData.getStagesIterator();
		
		 CQuestStage stgOne = null;
		 CQuestStage stgTwo = null;
		 
		 while(it.hasNext() == true)
		 {
			 stgOne = it.next();
			 
			 if(stgOne == m_stgCurrent)
			 {
				 it.previous();
				 
				 if(it.hasPrevious() == true)
					 stgTwo = it.previous();
				 
				 break;
			 }
			
		 }
		 
		 if(stgOne != null && stgTwo != null)
		 {
			 m_qnrData.SwapStages(stgOne, stgTwo);
			 
			 int nSelected = m_lstStages.getSelectedIndex();
			 String sTmp = m_lstStages.getItem(nSelected - 1);
			 
			 m_lstStages.remove(nSelected-1);
			 m_lstStages.add(sTmp, nSelected);
			 
		 }
	}
	
	private void MoveQuestionDown()
	{
		if(m_lstStages.getSelectedIndex() < 0)
		{
			return;
		}
		
		if (m_stgCurrent.getID().equals(m_lstStages.getSelectedItem()) == false)
		{
			InitCurrentStage();
		}
		
		 ListIterator<CQuestStage> it = m_qnrData.getStagesIterator();
		
		 CQuestStage stgOne = null;
		 CQuestStage stgTwo = null;
		 
		 while(it.hasNext() == true)
		 {
			 stgOne = it.next();
			 
			 if(stgOne == m_stgCurrent)
			 {
				 if(it.hasNext() == true)
					 stgTwo = it.next();
				 
				 break;
			 }
			
		 }
		 
		 if(stgOne != null && stgTwo != null)
		 {
			 m_qnrData.SwapStages(stgOne, stgTwo);
			 
			 int nSelected = m_lstStages.getSelectedIndex();
			 String sTmp = m_lstStages.getItem(nSelected + 1);
			 
			 m_lstStages.remove(nSelected+1);
			 m_lstStages.add(sTmp, nSelected);
			 
		 }
	}
	
	private void ShowAddAnswerDlg(boolean bEdit)
	{
		if( (bEdit == true) && (getCurrentAnswer() == null))
			return;
		
		CAddAnswerDialog dlgAddAnswer = new CAddAnswerDialog(this, m_stgCurrent, bEdit);
		EnableAnswerButtons(false);
		dlgAddAnswer.setVisible(true);
	}
	
	public void AddAnswer(CQuestAnswer qaNew)
	{
		if(m_stgCurrent == null)
		{
			return;
		}
		
		if (qaNew != null)
		{
			m_stgCurrent.AddItem(qaNew);
			InitFrameFromListItem();
		}
		
	}
	
	
	public void EditAnswer(CQuestAnswer qaNew)
	{
		if(m_stgCurrent == null)
		{
			return;
		}
		
		if (qaNew != null)
		{
			CQuestAnswer qaCurrent = getCurrentAnswer();
			qaCurrent.setCaption(qaNew.getCaption());
			qaCurrent.setNextStage(qaNew.getNextStage());
			
			InitFrameFromListItem();
		}
		
	}
	
	
	/**
	 * @return Current path where answers lists are saved to and loaded from 
	 */
	public String getAnswerIOPath()
	{
		String sReturn = new String(m_sCurrentAnswerIOPath);
		return sReturn;
	}
	
	/**
	 * @param sPath is the path where answers lists are saved to and loaded from
	 */
	public void setAnswerIOPath(String sPath)
	{
		m_sCurrentAnswerIOPath = sPath;
	}
	
	
	/**
	 * Clears answers list and loads list of  answers from file
	 */
	private void LoadAnswerList()
	{
		
		
		FileDialog dlgFile = new FileDialog(this, "Choose file to load", FileDialog.LOAD);


		dlgFile.setDirectory(m_sCurrentAnswerIOPath);
		
		dlgFile.setMultipleMode(false);
		dlgFile.setVisible(true);
		
		String sFile = dlgFile.getDirectory() + dlgFile.getFile();
		
		if (sFile.equals("") == false)
		{
			m_sCurrentAnswerIOPath = dlgFile.getDirectory();
			
			CAnswersListIOprocessor ioProc = new CAnswersListIOprocessor();
			CAnswerListIOcontainer ioCont = ioProc.ReadAnswerListToFile(sFile);
			
			java.util.List<CQuestAnswer> lstAnswers = m_stgCurrent.getAnswerList();
			lstAnswers.clear();
			lstAnswers.addAll(ioCont.getAnswerList());
			
			RefreshAnswerList();
		}
	}
	
	/**
	 * Saves current answer list to file 
	 */
	private void SaveAnswerList()
	{
		FileDialog dlgFile = new FileDialog(this, "Choose file to save", FileDialog.SAVE);

		dlgFile.setDirectory(m_sCurrentAnswerIOPath);
		
		dlgFile.setMultipleMode(false);
		dlgFile.setVisible(true);
		
		String sFile = dlgFile.getDirectory() + dlgFile.getFile();
		
		if (sFile.equals("") == false)
		{
			m_sCurrentAnswerIOPath = dlgFile.getDirectory();

			//sFile = sFile + ".ans";
			
			CAnswersListIOprocessor ioProc = new CAnswersListIOprocessor();
			CAnswerListIOcontainer ioCont = new CAnswerListIOcontainer();
			
			java.util.List<CQuestAnswer> lstAnswers = new ArrayList<CQuestAnswer>();
			lstAnswers.addAll(m_stgCurrent.getAnswerList());
				
			
			Iterator<CQuestAnswer> it = lstAnswers.iterator();
			
			while(it.hasNext())
			{
				CQuestAnswer qa = it.next();
				qa.setNextStage(null);
				qa.setValue(-1);

			}
			
			ioCont.setName( dlgFile.getFile());
			
			ioCont.setAnswerList(lstAnswers);
			ioProc.WriteAnswerListToFile(ioCont, sFile);
			
		}
	}
	
	
	/**
	 * Removes answer from list on frame and from data
	 */
	private void RemoveAnswer()
	{
		Iterator<CQuestAnswer> it = m_stgCurrent.GetAnswersIterator();
		
		while(it.hasNext() == true)
		{
			if(((CQuestAnswer)it.next()).getCaption().equals(m_lstTwo.getSelectedItem()))
			{
				it.remove();
				m_lstTwo.RemoveItem(m_lstTwo.getSelectedIndex());
			}
		}
	}
	
	/**
	 * @return CQuestAnswer that the selected item in list
	 */
	public CQuestAnswer getCurrentAnswer()
	{
		if (m_stgCurrent == null)
			return null;
		
		CQuestAnswer qaCurrent = m_stgCurrent.getAnswerByCaption(m_lstTwo.getSelectedItem());
		
		return qaCurrent;
	}
	
	/**
	 * Moves answer up on one item in list and in data
	 */
	private void MoveAnswerUp()
	{
		if(m_lstStages.getSelectedIndex() < 0)
		{
			return;
		}
		
		if (m_stgCurrent.getID().equals(m_lstStages.getSelectedItem()) == false)
		{
			InitCurrentStage();
		}
		
		CQuestAnswer qaCurrent = getCurrentAnswer();
		
		 ListIterator<CQuestAnswer> it = m_stgCurrent.GetAnswersIterator();
		
		 CQuestAnswer qaOne = null;
		 CQuestAnswer qaTwo = null;
		 
		 while(it.hasNext() == true)
		 {
			 qaOne = it.next();
			 
			 if(qaOne == qaCurrent)
			 {
				 it.previous();
				 
				 if(it.hasPrevious() == true)
					 qaTwo = it.previous();
				 
				 break;
			 }
			
		 }
		 
		 if(qaOne != null && qaTwo != null)
		 {
			 m_stgCurrent.SwapAnswers(qaOne, qaTwo);
			 
			 int nSelected = m_lstTwo.getSelectedIndex();
			 
			 String sTmpAnswer = m_lstTwo.getItemCaption(nSelected - 1);
			 String sTmpNextStage = m_lstTwo.getItemNextStage(nSelected - 1);
			 
			 m_lstTwo.RemoveItem(nSelected-1);
			 m_lstTwo.AddItem(sTmpAnswer, sTmpNextStage, nSelected);
			 
		 }
	}
	
	/**
	 * Moves answer down on one item in list and in data
	 */	
	private void MoveAnswerDown()
	{
		if(m_lstStages.getSelectedIndex() < 0)
		{
			return;
		}
		
		if (m_stgCurrent.getID().equals(m_lstStages.getSelectedItem()) == false)
		{
			InitCurrentStage();
		}
		
		CQuestAnswer qaCurrent = m_stgCurrent.getAnswerByCaption(m_lstTwo.getSelectedItem());
		
		ListIterator<CQuestAnswer> it = m_stgCurrent.GetAnswersIterator();
		
		CQuestAnswer qaOne = null;
		CQuestAnswer qaTwo = null;
		 
		while(it.hasNext() == true)
		{
			qaOne = it.next();
			 
			if(qaOne == qaCurrent)
			{
				if(it.hasNext() == true)
					qaTwo = it.next();
				 
				break;
			}
			
		}
		 
		if(qaOne != null && qaTwo != null)
		{
			m_stgCurrent.SwapAnswers(qaOne, qaTwo);
			 
			int nSelected = m_lstTwo.getSelectedIndex();
			 
			String sTmpAnswer = m_lstTwo.getItemCaption(nSelected + 1);
			String sTmpNextStage = m_lstTwo.getItemNextStage(nSelected + 1);
			 
			m_lstTwo.RemoveItem(nSelected+1);
			m_lstTwo.AddItem(sTmpAnswer, sTmpNextStage, nSelected);
			 
		}
	}
	
	private void LoadCSV()
	{
		
		FileDialog dlgFile = new FileDialog(this, "Choose datafile", FileDialog.LOAD);
		
		dlgFile.setFilenameFilter(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".csv");
			}
		});

		dlgFile.setDirectory("C:\\Users\\Yura Vyrovoy\\Desktop");

		
		dlgFile.setMultipleMode(false);
		dlgFile.setVisible(true);
		
		String sFile = dlgFile.getDirectory() + dlgFile.getFile();
		
		
		if (sFile.equals("") == false)
		{
			File fileInput = new File(sFile);
		
			if (fileInput.exists() == false) 
			{
				System.out.println("No file has been choosen.");
				return;
			}
			
			ResetFrame();
			m_qnrData = CQuestMachineCore.InitQuestionnaireFromCSV(fileInput);
			InitStagesList();
		}
		
	}
	
	private void SaveQuestionnaire()
	{
		
		FileDialog dlgFile = new FileDialog(this, "Choose destination", FileDialog.SAVE);
		
		dlgFile.setFilenameFilter(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".csv");
			}
		});

		dlgFile.setDirectory("C:\\Users\\Yura Vyrovoy\\Desktop");
		//dlgFile.setDirectory(""C:\\Users\\VYROVOY\\Desktop");
		
		//dlgFile.setFile("*.csv");
		
		dlgFile.setMultipleMode(false);
		dlgFile.setVisible(true);
		
		String sFile = dlgFile.getDirectory() + dlgFile.getFile();
		
		if (sFile.equals("") == false)
		{
			Path p = Paths.get(sFile);
			CQuestMachineCore.SaveQuestionnaireToCSV(m_qnrData, p);
		}
		
	}
	
	public void EnableAnswerButtons(boolean bEnable)
	{
		m_btnAddAnswer.setEnabled(bEnable);
		m_btnRemoveAnswer.setEnabled(bEnable);
		m_btnEditAnswer.setEnabled(bEnable);
		
		m_btnLoadAnswerList.setEnabled(bEnable);
		m_btnSaveAnswerList.setEnabled(bEnable);
		
		m_btnMoveAnswerUp.setEnabled(bEnable);
		m_btnMoveAnswerDown.setEnabled(bEnable);
	}
	
	
}


