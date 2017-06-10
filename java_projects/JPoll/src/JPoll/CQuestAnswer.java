package JPoll;

import java.io.*;

public class CQuestAnswer implements Serializable 
{

	private static final long serialVersionUID = -4518623337300597414L;

	private String m_sCaption;
	private int m_nValue;
	//private String m_sNextStageID;
	private CQuestStage m_stgNext;
	
	
	
	public final static String NEXT_STAGE_NEXT = "#Next stage!#";
	public final static String OPENED_ANSWER = "#Opened answer!#";
	public final static String NO_ANSWER = "No answer";

	public CQuestAnswer()
	{
		m_sCaption = "";
		m_nValue = -1;
		m_stgNext = null;
	}
	
	public CQuestAnswer(CQuestAnswer qaSrc)
	{
		m_sCaption = qaSrc.m_sCaption;
		m_nValue = qaSrc.m_nValue;
		//m_sNextStageID = qaSrc.m_sNextStageID;
		
		m_stgNext = qaSrc.m_stgNext;
	}
	
	public String getCaption()
	{
		return m_sCaption;
	}

	public void setCaption(String sCaption)
	{
		m_sCaption = sCaption;
	}

	public int getValue()
	{
		return m_nValue;
	}

	public void setValue(int nValue)
	{
		m_nValue = nValue;
	}

	/*
	public String getNextStageID()
	{
		return m_sNextStageID;
	}

	public void setNextStageID(String sNextStageID)
	{
		m_sNextStageID = sNextStageID;
	}
*/
	
	public CQuestStage getNextStage()
	{
		return m_stgNext;
	}

	public void setNextStage(CQuestStage stgNext)
	{
		m_stgNext = stgNext;
	}
	
	public boolean isNextStageNext()
	{
		return (m_stgNext == null);
	}
	
	public void setNextStageNext()
	{
		m_stgNext = null;
	}
	
	public boolean isAnswerOpened()
	{
		return m_sCaption.equals(OPENED_ANSWER);
	}
	
	public void setAnswerOpened()
	{
		m_sCaption = OPENED_ANSWER;
	}

}
