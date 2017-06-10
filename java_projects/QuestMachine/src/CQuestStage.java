import java.util.ArrayList;
// import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class CQuestStage 
{
	public static enum enmViewType
	{
		VT_VERTICAL, VT_HORIZONTAL, VT_COMBO;
	}

	public static enum enmStageType 
	{
		ST_SINGLE_ANSWER, ST_MULTY_ANSWER;
	}
	
	// fields
	protected String m_sCaption;
	protected List<CQuestion> m_lstQuestions;
	protected enmViewType m_vtViewType;
	protected enmStageType m_stStageType;
	
	//String question ID. Questionnaire navigation use it, MUST be unique!!!
	protected String m_sID;   	
	
	public CQuestStage()
	{
		m_lstQuestions = new ArrayList<CQuestion>();
	}

	// Copy constructor
	public CQuestStage(CQuestStage stgSrc)
	{

		m_lstQuestions = new ArrayList<CQuestion>();
		
		m_sCaption = stgSrc.m_sCaption;
		m_sID = stgSrc.m_sID;
		m_vtViewType = stgSrc.m_vtViewType;
		m_stStageType = stgSrc.m_stStageType;
		m_lstQuestions.addAll(stgSrc.m_lstQuestions);	
		
	}

	// добавляем один элемент в список 
	public boolean AddQuestion(CQuestion qstNew)
	{
		
		CQuestion qstTmp = new CQuestion(qstNew);
		return m_lstQuestions.add(qstTmp);
		
	}

	public String getCaption() 
	{
		return m_sCaption;
	}
	
	public void setCaption(String sCaption) 
	{
		this.m_sCaption = sCaption;
	}
	
	public String getID() {
		return m_sID;
	}

	public void setID(String sID) 
	{
		this.m_sID = sID;
	}
	
	public void setViewType(enmViewType vtViewType)
	{
		this.m_vtViewType = vtViewType;
	}
	
	public enmViewType getViewType()
	{
		return this.m_vtViewType;
	}

	public void setStageType(enmStageType stStageType)
	{
		this.m_stStageType = stStageType;
	}
	
	public enmStageType getStageType()
	{
		return this.m_stStageType;
	}	
	
	public int getQuestionListSize()
	{
		return m_lstQuestions.size();
	}
	
	public ListIterator<CQuestion> getQuestionsIterator(){
		
		ListIterator<CQuestion> it = m_lstQuestions.listIterator();
		return it;
	}	
}
