package JPoll;
import java.util.ArrayList;
import java.util.*;


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
	private String m_sCaption;
	private List<CQuestAnswer> m_lstItems; 	
	private enmViewType m_vtViewType;
	private enmStageType m_stStageType;
	
	//String question ID. Questionnaire navigation use it, MUST be unique!!!
	private String m_sID;  
	
	//List of alternatives
	
	
	public CQuestStage()
	{
		m_lstItems= new ArrayList<CQuestAnswer>();
	}

	// Copy constructor
	public CQuestStage(CQuestStage stgSrc)
	{

		
		m_lstItems= new ArrayList<CQuestAnswer>();
		m_sCaption = stgSrc.m_sCaption;
		m_sID = stgSrc.m_sID;
		m_vtViewType = stgSrc.m_vtViewType;
		m_stStageType = stgSrc.m_stStageType;
		m_lstItems.addAll(stgSrc.m_lstItems);	
		
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
	
	public List<CQuestAnswer> getAnswerList() 
	{
		return m_lstItems;
	}

	public void setID(List<CQuestAnswer> lstItems) 
	{
		this.m_lstItems = lstItems;
	}
	
	
	/**
	 * добавляем один элемент в список 
	 * @param qaNew 
	 * @return true if item was added successfully
	 */
	public boolean AddItem(CQuestAnswer qaNew)
	{
		CQuestAnswer qaTmp = new  CQuestAnswer(qaNew);
		return m_lstItems.add(qaTmp);
	}

	public int getAnswersListSize()
	{
		return m_lstItems.size();
	}
	
	public ListIterator<CQuestAnswer> GetAnswersIterator()
	{
		
		ListIterator<CQuestAnswer> it = m_lstItems.listIterator();
		return it;
	}
	
	public CQuestAnswer getAnswerByCaption(String sCaption)
	{
		if ( sCaption == null)
		{
			return null;
		}
		ListIterator<CQuestAnswer> it = GetAnswersIterator();
		
		CQuestAnswer qaReturn = null;
		while(it.hasNext() == true)
		{
			qaReturn = it.next();
			if(sCaption.equals(qaReturn.getCaption()) == true)
				break;
		}
		
		return qaReturn;
	}
	
	public void SwapAnswers(CQuestAnswer qaOne, CQuestAnswer qaTwo)
	{
		int iAnswerOne = -1;
		int iAnswerTwo = -1;
		int iCurStage = 0;
		
		Iterator<CQuestAnswer> it = GetAnswersIterator();
		
		
		while (it.hasNext() == true)
		{
			CQuestAnswer qaCurrent = it.next();
			
			if(qaCurrent == qaOne)
			{
				iAnswerOne = iCurStage;
			}

			if(qaCurrent == qaTwo)
			{
				iAnswerTwo = iCurStage;
			}
			
			iCurStage++;
		}
		
		if(iAnswerOne >= 0 && iAnswerTwo >= 0)
		{
			Collections.swap(m_lstItems, iAnswerOne, iAnswerTwo);
		}
	}
}
