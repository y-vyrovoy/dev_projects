package JPoll;
import java.io.Serializable;
import java.util.*;


/**
* @author vyrovoy
*	This class is container for serialization of answer lists sets
*/

class CAnswerListIOcontainer implements Serializable
{

	private static final long serialVersionUID = 3071165171447052940L;
	
	private List<CQuestAnswer> m_lstAnswers;
	private String m_sSetName;
	
	public CAnswerListIOcontainer()
	{
		m_sSetName = "";
		m_lstAnswers = new ArrayList<CQuestAnswer>();
	}
	
	
	
	/**
	 * @param lstAnswers - list of answers
	 * @param sSetName - name of this set
	 */
	public CAnswerListIOcontainer(List<CQuestAnswer> lstAnswers, String sSetName)
	{
		m_sSetName = sSetName;
		
		m_lstAnswers = new ArrayList<CQuestAnswer>();
		m_lstAnswers.addAll(lstAnswers);
	}
	
	public void setAnswerList(List<CQuestAnswer> lstAnswers)
	{
		m_lstAnswers.addAll(lstAnswers);
	}
	
	public List<CQuestAnswer> getAnswerList()
	{
		return m_lstAnswers;
	}
	
	public void setName(String sSetName)
	{
		m_sSetName = sSetName;
	}
	
	public String getName()
	{
		return m_sSetName;
	}
}
	
