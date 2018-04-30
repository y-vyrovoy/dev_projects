package JPoll;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * @author Yura
 * CAddAnswerDialog implements an interface to add answer alternatives for
 * question and to set up shifts along the questionnaire
 */
public class CAddAnswerDialog extends Dialog implements ActionListener
{
	private static final long serialVersionUID = 20150930;
	public static final String CMP_ADD_ANSWER = "CMP_ADD_ANSWER";
	
	private CMainFrame m_frmParent;
	
	public Label m_lblAnswer;
	public TextField m_txtAnswer;
	
	public Label m_lblStages;
	public java.awt.List m_lstStages;
	
	public Button m_btnAdd;
	public Button m_btnClose;
	
	private CQuestionnaire m_qnrData;
	private boolean m_bEditAnswer;
	
	/**
	  * @param frmParent is parent frame
	 */
	public CAddAnswerDialog(Frame frmParent)
	{
		this(frmParent, null, false);
	}
	
	
	/**
	 * @param frmParent is a parent frame
	 * @param stgStart the list of next stage options will be filled with the stages after stgStart
	 * @param bEdit shows whether answer is edited or a new one
	 */
	public CAddAnswerDialog(Frame frmParent, CQuestStage stgStart, boolean bEdit)
	{
		
		super(frmParent, true); 
		
		m_bEditAnswer = bEdit; 

		m_frmParent = (CMainFrame)frmParent;
		m_qnrData = m_frmParent.getData();
		
		setSize((int)(frmParent.getSize().width*.5), (int)(frmParent.getSize().height*.9));
		
		m_lblAnswer = new Label("Answer text");
		m_txtAnswer = new TextField(CQuestAnswer.OPENED_ANSWER);
		
		m_lblStages = new Label("Next stage");
		m_lstStages = new java.awt.List();
		
		Iterator<CQuestStage> it = m_qnrData.getStagesIterator();
		m_lstStages.add(CQuestAnswer.NEXT_STAGE_NEXT);
		
		// moving along the stages list
		boolean bFind = false;
		
		while(it.hasNext() == true)
		{
			CQuestStage stgCurrent = it.next();
			
			if(bFind == true)
			{
				// if we have found stgStart let's fill the list
				m_lstStages.add( stgCurrent.getID());
			}
			
			if(stgCurrent == stgStart)
			{
				// if stgStart is found let's set up marker
				bFind = true;
			}
			
		}
		
		// set up button caption depending on dialog type (Add|Edit)
		String sAddCaption = "";
		if(m_bEditAnswer == false)
		{
			sAddCaption = "Add";
		}
		else
		{
			sAddCaption = "Edit";
		}
		
		m_btnAdd = new Button(sAddCaption);
		m_btnClose = new Button("Close");
		
		
		
		add(m_lblAnswer);
		add(m_txtAnswer);
		add(m_lblStages);
		add(m_lstStages);
		
		add(m_btnAdd);
		add(m_btnClose);
		
		m_btnAdd.addActionListener(this);
		m_btnClose.addActionListener(this);
		
		CAddAnswerDialogLOM lmDlg = new CAddAnswerDialogLOM();
		setLayout(lmDlg);
		
		lmDlg.addLayoutComponent(CMP_ADD_ANSWER, (Component)this);
		
		
		
		addWindowListener(new WindowAdapter() {
	            
			public void windowClosing(WindowEvent windowEvent){
				m_frmParent.EnableAnswerButtons(true);
				setVisible(false);
			}
		});
		
		
		int nNextStage = 0;
		if(m_bEditAnswer == true)
		{
			// dialog type is "edit"
			if( (m_frmParent.getCurrentAnswer() != null) && 
				(m_frmParent.getCurrentAnswer() != null) )
			{
				// if current stage and current answer alternative 
				// are set up on parent dialog
				
				CQuestStage stgNext = m_frmParent.getCurrentAnswer().getNextStage();
	
				if (stgNext != null)
				{
					// if next stage was set up in current answer alternative
					// let find its index to select it in the stages list
					
					int i = 0;
					while( i < m_lstStages.getItemCount() )
					{
						if(stgNext.getID().equals(m_lstStages.getItem(i)) )
						{
							nNextStage = i;
							break;
						}
						i++;
					}
				}
			}
			
			m_txtAnswer.setText(m_frmParent.getCurrentAnswer().getCaption());
		}

		m_lstStages.select(nNextStage);
		
		m_txtAnswer.selectAll();
		m_txtAnswer.requestFocus();
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if( e.getSource() == m_btnClose  )
		{
			// on Close button dialog is closing
			m_frmParent.EnableAnswerButtons(true);
			setVisible(false);
		}
		else if( e.getSource() == m_btnAdd  )
		{
			// on Add button answer is added, dialog is still visible
			AddAnswer();
		}

	}
	
	/**
	 * Adds answer to parent frame of CMainFrame type
	 */
	private void AddAnswer()
	{
		CMainFrame frmParent = (CMainFrame)getParent();

		CQuestAnswer qaAdd = new CQuestAnswer();
		
		qaAdd.setCaption(m_txtAnswer.getText());
		qaAdd.setNextStage(m_qnrData.getStageByID(m_lstStages.getSelectedItem()));

		
		if (m_bEditAnswer == false)
		{
			// if dialog type is "add" let's add new answer
			// Dialog is still visible
			
			frmParent.AddAnswer(qaAdd);
		}
		else
		{
			// if dialog type is "edit" let's set up current answer
			// Dialog is closed
			
			frmParent.EditAnswer(qaAdd);
			m_frmParent.EnableAnswerButtons(true);
			setVisible(false);
		}
		
	}

}


