import java.util.ArrayList;
//import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;



public class CQuestion {
	
	private List<CQuestAnswer> m_lstItems; 	//List of alternatives
	private String m_sCaption; 				//Text of the question 
	private String m_sID; 					//String question ID. Questionnaire navigation use it, MUST be unique!!!   
	

	public CQuestion()
	{
		m_lstItems= new ArrayList<CQuestAnswer>();	
	} 

	// Copy constructor
	public CQuestion(CQuestion qstSrc)
	{

		m_lstItems= new ArrayList<CQuestAnswer>();
		
		m_sCaption = qstSrc.m_sCaption;
		m_sID = qstSrc.m_sID;
		
		m_lstItems.addAll(qstSrc.m_lstItems);	
		
	}

	
	// добавляем один элемент в список 
	public boolean AddItem(CQuestAnswer qaNew)
	{
		CQuestAnswer qaTmp = new  CQuestAnswer(qaNew);
		return m_lstItems.add(qaTmp);
	}
	
	public String getCaption() 
	{
		return m_sCaption;
	}
	
	public void setCaption(String sCaption) 
	{
		m_sCaption = sCaption;
	}
	
	public String getID() 
	{
		return m_sID;
	}

	public void setID(String sID) 
	{
		m_sID = sID;
	}
	
	public int GetAnswersListSize()
	{
		return m_lstItems.size();
	}
	
	public ListIterator<CQuestAnswer> GetAnswersIterator()
	{
		
		ListIterator<CQuestAnswer> it = m_lstItems.listIterator();
		return it;
	}
	

}
