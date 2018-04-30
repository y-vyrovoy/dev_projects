
public class CQuestAnswer {

	protected String m_sCaption;
	protected int m_nValue;
	protected String m_sNextStageID;
	
	public final String NEXT_STAGE_NEXT = "#Next stage!#";
	public final String OPENED_ANSWER = "#Opened answer!#";

	public CQuestAnswer()
	{
		m_sCaption = "";
		m_nValue = -1;
	}
	
	public CQuestAnswer(CQuestAnswer qaSrc)
	{
		m_sCaption = qaSrc.m_sCaption;
		m_nValue = qaSrc.m_nValue;
		m_sNextStageID = qaSrc.m_sNextStageID;
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

	public String getNextStageID()
	{
		return m_sNextStageID;
	}

	public void setNextStageID(String sNextStageID)
	{
		m_sNextStageID = sNextStageID;
	}

	public boolean isNextStageNext()
	{
		return m_sNextStageID.equals(NEXT_STAGE_NEXT);
	}
	
	public void setNextStageNext()
	{
		m_sNextStageID = NEXT_STAGE_NEXT;
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
