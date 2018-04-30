import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class CQuestionnaire {

	// fields
	private List<CQuestStage> m_lstStages;
	
	public CQuestionnaire()
	{
		m_lstStages = new ArrayList<CQuestStage>();
	}

	// Copy constructor
	public CQuestionnaire(CQuestionnaire qnrSrc)
	{
		
		m_lstStages = new ArrayList<CQuestStage>();
		
		m_lstStages.addAll(qnrSrc.m_lstStages);
	}
	
	
	//methods

	public boolean AddStage(CQuestStage stgNew)
	{
		
		CQuestStage stgTmp = new CQuestStage(stgNew);
		return m_lstStages.add(stgTmp);
		
	}

	
	// находит stage с заданным ID и возвращает его
	public CQuestStage setStageByID(String sID)
	{
		
		CQuestStage stReturn = null;
		
		ListIterator<CQuestStage> it = m_lstStages.listIterator(); 
		boolean bFind = false;
		
		while(it.hasNext() && bFind==false)
		{
			
			stReturn = it.next();
			
			if (stReturn.getID()==sID)
			{
				bFind = true;				
			}
		}
		
		return stReturn;
		
	}
		
	public int getStagesListSize()
	{
		return m_lstStages.size();
	}
	
	public ListIterator<CQuestStage> getStagesIterator()
	{
		
		ListIterator<CQuestStage> it = m_lstStages.listIterator();
		return it;
	}		
	
	public int getStageNumberByID(String sStageID)
	{

		if (getStagesListSize() < 1)
		{
			return -1;
		}
		
		Iterator<CQuestStage> it = getStagesIterator();
		
		boolean bFind = false;
		int nResult = 0;
		
		do
		{
			nResult++;
			
			
			if(it.next().getID() == sStageID)
			{
				bFind = true;				
			}
		}
		while( (it.hasNext() == true) && bFind == false);
		
		if (bFind == false)
		{
			nResult++;
		}
		
		return nResult;
	}
}
