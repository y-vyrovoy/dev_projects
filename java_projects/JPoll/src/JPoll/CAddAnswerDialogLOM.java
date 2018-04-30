package JPoll;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Toolkit;


public class CAddAnswerDialogLOM implements LayoutManager 
{
	private CAddAnswerDialog m_dlgParent;
	
	public CAddAnswerDialogLOM()
	{
		
	}
	

	public void addLayoutComponent(String name, Component comp)
	{

		if (CAddAnswerDialog.CMP_ADD_ANSWER.equals(name) == true) 
		{
			m_dlgParent = (CAddAnswerDialog)comp;
		}

	}
	
	public void removeLayoutComponent(Component comp)
	{
		if (m_dlgParent == comp){ m_dlgParent = null; }

	}
	
	public Dimension preferredLayoutSize(Container parent)
	{		
		Dimension screenSize = new Dimension(0,0);
		
		return screenSize;
	}
	
	public Dimension minimumLayoutSize(Container parent)
	{
		Dimension screenSize = new Dimension(0,0);
		
		return screenSize;

	}
	
	public void layoutContainer(Container parent)
	{

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		//Dimension dimParent = parent.getSize();
		
		
		int nBorderLeft = (int)(screenSize.getWidth() * 0.02);
		int nBorderTop = (int)(screenSize.getHeight() * 0.05);
		
		//int nIntBorderH = (int)(screenSize.getWidth() * 0.005);
		int nIntBorderV = (int)(screenSize.getHeight() * 0.004);		
		
		int nControHeight = (int)(screenSize.getHeight() * 0.027);
		
		int nControlWidth = (int)(parent.getWidth() - 2 * nBorderLeft); 
		
		int nButtonWidth = (int)(screenSize.getWidth() * 0.1);
		int nButtonHeight = (int)(screenSize.getHeight() * 0.025);
		
		
		int nAnswerLabelTop = nBorderTop;
		int nAnswerTop = nAnswerLabelTop + nControHeight + nIntBorderV;
		
		int nStagesLabelTop = nAnswerTop + nControHeight + 3 * nIntBorderV;
		int nStagesTop = nStagesLabelTop + nControHeight + nIntBorderV;


		
		int nButtonTop = (int)(parent.getHeight() - nBorderTop);
		int nCancelButtonLeft = nBorderLeft;
		int nAddButtonLeft = (int)(parent.getWidth() - nButtonWidth - nBorderLeft);
		
		int nStagesHeight = nButtonTop - nStagesTop - 2*nIntBorderV;
		
		
		
		//-------------------------------
		// Answer
		if (m_dlgParent.m_lblAnswer != null)
		{
			m_dlgParent.m_lblAnswer.setBounds(nBorderLeft, 
											nAnswerLabelTop, 
											nControlWidth, 
											nControHeight);
			
			
		}
		
		if (m_dlgParent.m_txtAnswer != null)
		{
			m_dlgParent.m_txtAnswer.setBounds(nBorderLeft, 
											nAnswerTop, 
											nControlWidth, 
											nControHeight);
			
			
		}


		//-------------------------------
		// Stages
		if (m_dlgParent.m_lblStages != null)
		{
			m_dlgParent.m_lblStages.setBounds(nBorderLeft, 
											nStagesLabelTop, 
											nControlWidth, 
											nControHeight);
			
			
		}
		
		if (m_dlgParent.m_lstStages != null)
		{
			m_dlgParent.m_lstStages.setBounds(nBorderLeft, 
												nStagesTop, 
												nControlWidth, 
												nStagesHeight);
			
			
		}
		
		//-------------------------------
		// Buttons
		if (m_dlgParent.m_btnClose != null)
		{
			m_dlgParent.m_btnClose.setBounds(nCancelButtonLeft, 
												nButtonTop, 
												nButtonWidth, 
												nButtonHeight);
			
			
		}
		
		if (m_dlgParent.m_btnAdd != null)
		{
			m_dlgParent.m_btnAdd.setBounds(nAddButtonLeft, 
											nButtonTop, 
											nButtonWidth, 
											nButtonHeight);
			
			
		}


		
	}
}
