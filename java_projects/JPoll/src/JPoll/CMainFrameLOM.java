package JPoll;
import java.awt.*;

public class CMainFrameLOM implements LayoutManager 
{

	private CMainFrame m_frmParent;
	
	public CMainFrameLOM()
	{
		
	}
	

	public void addLayoutComponent(String name, Component comp)
	{

		if (CMainFrame.CMP_MAIN_FRAME.equals(name) == true) 
		{
			m_frmParent = (CMainFrame)comp;
		}

	}
	
	public void removeLayoutComponent(Component comp)
	{
		if (m_frmParent == comp){ m_frmParent = null; }

	}
	
	public Dimension preferredLayoutSize(Container parent)
	{		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double dblWidth = screenSize.getWidth()/4;
		double dblHeight = screenSize.getHeight()/4;
		
		Dimension dimResult = new Dimension((int)dblWidth, (int)dblHeight);
		
		return dimResult;
	}
	
	public Dimension minimumLayoutSize(Container parent)
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double dblWidth = screenSize.getWidth()/6;
		double dblHeight = screenSize.getHeight()/6;
		
		Dimension dimResult = new Dimension((int)dblWidth, (int)dblHeight);
		
		return dimResult;

	}
	
	public void layoutContainer(Container parent)
	{

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension dimParent = parent.getSize();
		
		int nRightPartLeft = (int)(dimParent.getWidth() * 0.5);

		int nButtonWidth = (int)(screenSize.getWidth() * 0.1);
		int nButtonHeight = (int)(screenSize.getHeight() * 0.025);
		
		int nIntBorderH = (int)(screenSize.getWidth() * 0.005);
		int nIntBorderV = (int)(screenSize.getHeight() * 0.005);
		
		int nBorderLeft = (int)(screenSize.getWidth() * 0.02);
		int nBorderTop = (int)(screenSize.getHeight() * 0.05);
		
		int nBorderRight = (int)(dimParent.getWidth() - nBorderLeft);
		
		int nSmallQuestionButtonsLeft = nRightPartLeft - 2 * nBorderLeft - nIntBorderH;
		int nSmallButtonsWidth = nButtonHeight * 2;

		int nSmallAnswerButtonsLeft = (int)dimParent.getWidth() - 2 * nBorderLeft - nIntBorderH;
		
		int nLableHeight = nButtonHeight;
		

		int nListWidth = (int)(nSmallQuestionButtonsLeft - nBorderLeft - nIntBorderH);
		int nListHeight = (int)(dimParent.getHeight() * 0.85);
		
		int nAddQuestionTop = (int)(nBorderTop + nListHeight/4 - (nButtonHeight + nIntBorderV/2) );
		int nRemoveQuestionTop = (int)(nBorderTop + nListHeight/4 + (nButtonHeight + nIntBorderV/2) );

		int nMoveQuestionUpTop = (int)(nBorderTop + nListHeight*3/4 - (nButtonHeight + nIntBorderV/2) );
		int nMoveQuestionDownTop = (int)(nBorderTop + nListHeight*3/4 + (nButtonHeight + nIntBorderV/2) );		
		
		int nCaptionHeight = (int)(screenSize.getHeight() * 0.05);
		int nCaptionWidth = nBorderRight - nRightPartLeft;
		
		int nCaptionLblTop = nBorderTop;
		int nCaptionTop = nCaptionLblTop + nLableHeight + nIntBorderV;
		int nTypeLableTop = nCaptionTop + nCaptionHeight + 2 * nIntBorderV;
		int nTypeTop = nTypeLableTop + nLableHeight + nIntBorderV;
		
		int nAnswerListLableTop = nTypeTop + + nCaptionHeight + 2 * nIntBorderV;
		int nAnswerListTop = nAnswerListLableTop + nLableHeight + nIntBorderV;
		
		int nAnswerListHeight = nBorderTop + nListHeight - nAnswerListTop;
		int nAnswerListWidth = (int)(nSmallAnswerButtonsLeft - nRightPartLeft - nIntBorderH);


		int nAddAnswerTop = (int)(nAnswerListTop + nAnswerListHeight/4 - (3*nButtonHeight + 2.5*nIntBorderV) );
		int nRemoveAnswerTop = (int)(nAnswerListTop + nAnswerListHeight/4 - ( 2*nButtonHeight + 1.5*nIntBorderV ) );
		int nEditAnswerTop = (int)(nAnswerListTop + nAnswerListHeight/4 - ( nButtonHeight + 0.5*nIntBorderV ) );
		
		int nLoadAnswerTop = (int)(nAnswerListTop + nAnswerListHeight/4 + ( 0.5*nIntBorderV) );
		int nSaveAnswerTop = (int)(nAnswerListTop + nAnswerListHeight/4 + ( nButtonHeight + 1.5*nIntBorderV) );

		int nMoveAnswerUpTop = (int)(nAnswerListTop + nAnswerListHeight*3/4 - (nButtonHeight + nIntBorderV/2) );
		int nMoveAnswerDownTop = (int)(nAnswerListTop + nAnswerListHeight*3/4 + (nButtonHeight + nIntBorderV/2) );
		
		
		
		//-------------------------------
		// Question list
		if (m_frmParent.m_lstStages != null)
		{
			m_frmParent.m_lstStages.setBounds(nBorderLeft, 
												nBorderTop, 
												nListWidth, 
												nListHeight);
			
			
		}

		//-------------------------------
		// Add and Remove Question button
		if (m_frmParent.m_btnAddQuestion != null)
		{
			m_frmParent.m_btnAddQuestion.setBounds(nSmallQuestionButtonsLeft, 
													nAddQuestionTop, 
													nSmallButtonsWidth, 
													nButtonHeight);	
		}
		
		if (m_frmParent.m_btnRemoveQuestion != null)
		{
			m_frmParent.m_btnRemoveQuestion.setBounds(nSmallQuestionButtonsLeft, 
														nRemoveQuestionTop, 
														nSmallButtonsWidth, 
														nButtonHeight);	
		}
		
		
		//-------------------------------
		// MoveUp and MoveDown Questions buttons
		if (m_frmParent.m_btnMoveQuestionUp != null)
		{
			m_frmParent.m_btnMoveQuestionUp.setBounds(nSmallQuestionButtonsLeft, 
										nMoveQuestionUpTop, 
										nSmallButtonsWidth, 
										nButtonHeight);	
		}		

		if (m_frmParent.m_btnMoveQuestionDown != null)
		{
			m_frmParent.m_btnMoveQuestionDown.setBounds(nSmallQuestionButtonsLeft, 
														nMoveQuestionDownTop, 
														nSmallButtonsWidth, 
														nButtonHeight);	
		}		
		
		//-------------------------------		
		// Load button
		if (m_frmParent.m_btnLoadFile != null)
		{
			m_frmParent.m_btnLoadFile.setBounds(nBorderLeft, 
												nBorderTop + nListHeight + nIntBorderV, 
												nButtonWidth, 
												nButtonHeight);
			
		}

		//-------------------------------
		// Create new questionnaire button
		if (m_frmParent.m_btnCreateNewQuest != null)
		{
			m_frmParent.m_btnCreateNewQuest.setBounds(nBorderLeft + nListWidth - nButtonWidth, 
														nBorderTop + nListHeight + nIntBorderV, 
														nButtonWidth, 
														nButtonHeight);

		}
		
		//-------------------------------
		// Question caption
		if (m_frmParent.m_lblCaption != null)
		{
			m_frmParent.m_lblCaption.setBounds(nRightPartLeft, 
												nCaptionLblTop, 
												nCaptionWidth, 
												nLableHeight);	
		}
		
		if (m_frmParent.m_txtCaption != null)
		{
			m_frmParent.m_txtCaption.setBounds(nRightPartLeft, 
												nCaptionTop, 
												nCaptionWidth, 
												nCaptionHeight);	
		}	
		
		//-------------------------------
		// Question type
		if (m_frmParent.m_lblType != null)
		{
			m_frmParent.m_lblType.setBounds(nRightPartLeft, 
												nTypeLableTop, 
												nCaptionWidth, 
												nLableHeight);	
		}

		if (m_frmParent.m_pnlType != null)
		{
			m_frmParent.m_pnlType.setBounds(nRightPartLeft, 
											nTypeTop, 
											nCaptionWidth, 
											nLableHeight);	
		}
		
		//-------------------------------
		// Answers list
		if (m_frmParent.m_lblAnswerList != null)
		{
			m_frmParent.m_lblAnswerList.setBounds(nRightPartLeft, 
									nAnswerListLableTop, 
									nAnswerListWidth, 
									nLableHeight);	
		}

		
		if (m_frmParent.m_lstTwo != null)
		{
			m_frmParent.m_lstTwo.setBounds(nRightPartLeft, 
								nAnswerListTop, 
								nAnswerListWidth, 
								nAnswerListHeight);	
		}

		//-------------------------------
		// Add, Remove, Edit, Load and Save Answer button
		if (m_frmParent.m_btnAddAnswer != null)
		{
			m_frmParent.m_btnAddAnswer.setBounds(nSmallAnswerButtonsLeft, 
										nAddAnswerTop, 
										nSmallButtonsWidth, 
										nButtonHeight);	
		}
		
		if (m_frmParent.m_btnRemoveAnswer != null)
		{
			m_frmParent.m_btnRemoveAnswer.setBounds(nSmallAnswerButtonsLeft, 
										nRemoveAnswerTop, 
										nSmallButtonsWidth, 
										nButtonHeight);	
		}
		
		if (m_frmParent.m_btnEditAnswer != null)
		{
			m_frmParent.m_btnEditAnswer.setBounds(nSmallAnswerButtonsLeft, 
										nEditAnswerTop, 
										nSmallButtonsWidth, 
										nButtonHeight);	
		}

		if (m_frmParent.m_btnLoadAnswerList != null)
		{
			m_frmParent.m_btnLoadAnswerList.setBounds(nSmallAnswerButtonsLeft, 
														nLoadAnswerTop, 
														nSmallButtonsWidth, 
														nButtonHeight);	
		}

		if (m_frmParent.m_btnSaveAnswerList != null)
		{
			m_frmParent.m_btnSaveAnswerList.setBounds(nSmallAnswerButtonsLeft, 
														nSaveAnswerTop, 
														nSmallButtonsWidth, 
														nButtonHeight);	
		}
		
		//-------------------------------
		// MoveUp and MoveDown Answer buttons
		if (m_frmParent.m_btnMoveAnswerUp != null)
		{
			m_frmParent.m_btnMoveAnswerUp.setBounds(nSmallAnswerButtonsLeft, 
										nMoveAnswerUpTop, 
										nSmallButtonsWidth, 
										nButtonHeight);	
		}		

		if (m_frmParent.m_btnMoveAnswerDown != null)
		{
			m_frmParent.m_btnMoveAnswerDown.setBounds(nSmallAnswerButtonsLeft, 
										nMoveAnswerDownTop, 
										nSmallButtonsWidth, 
										nButtonHeight);	
		}
		
		//-------------------------------
		// Update Question button
		if (m_frmParent.m_btnSaveQuestionnaire != null)
		{
			m_frmParent.m_btnSaveQuestionnaire.setBounds(nRightPartLeft, 
											nBorderTop + nListHeight + nIntBorderV, 
											nButtonWidth, 
											nButtonHeight);	
		}
	}
	
}