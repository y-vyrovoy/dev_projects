package JPoll;
import java.util.*;


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

	public boolean AddStage(CQuestStage stgNew, CQuestStage stgBefore)
	{

		CQuestStage stgTmp = new CQuestStage(stgNew);
		if(stgBefore == null)
		{
			return m_lstStages.add(stgTmp);
		}
		else
		{
			try
			{
				m_lstStages.add(getStageNumber(stgBefore), stgTmp);
				return true;
			}
			catch(Exception e)
			{
				return false;
			}
		}
		
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

	public CQuestStage getStageByID(String sStageID)
	{

		if (getStagesListSize() < 1)
		{
			return null;
		}
		
		
		if (sStageID.equals(CQuestAnswer.NEXT_STAGE_NEXT) == true)
		{
			return null;
		}
		
		Iterator<CQuestStage> it = getStagesIterator();
		
		CQuestStage stgResult = null;
		boolean bFind = false;

		while( (it.hasNext() == true) && bFind == false)
		{
			stgResult = it.next();
			if(stgResult.getID().equals(sStageID) == true)
			{
				bFind = true;				
			}
		}
		
		
		if (bFind == true)
		{
			return stgResult;
		}
		else
		{
			return null;
		}
	}
	
	public int getStageNumber(CQuestStage stgStage)
	{

		int nResult = 1;
		
		if(stgStage == null)
			return -1;
		
		Iterator<CQuestStage> it = getStagesIterator();
		
		while( it.hasNext() == true )
		{
			if(it.next() == stgStage)
			{
				return nResult;
			}
			nResult++;
		}
		return -1;
	}
	
	public int getStageNumberByID(String sStageID)
	{
		return getStageNumber(getStageByID(sStageID));
	}
	
	public void RemoveStage(ListIterator<CQuestStage> itRemove)
	{
		
		itRemove.remove();
		
	}
	
	public boolean isStageIDUnique(String sNewStageID)
	{
		boolean bResult = true;
		Iterator<CQuestStage> it = getStagesIterator();
		
		while (it.hasNext() == true)
		{
			if(((CQuestStage)it.next()).getID().equals(sNewStageID))
			{
				bResult = false;
				break;
			}
		}
		
		return bResult;
	}
	
	public void SwapStages(CQuestStage stgOne, CQuestStage stgTwo)
	{
		int iStageOne = -1;
		int iStageTwo = -1;
		int iCurStage = 0;
		
		Iterator<CQuestStage> it = getStagesIterator();
		
		while (it.hasNext() == true)
		{
			CQuestStage stgCurrent = it.next();
			
			if(stgCurrent == stgOne)
			{
				iStageOne = iCurStage;
			}

			if(stgCurrent == stgTwo)
			{
				iStageTwo = iCurStage;
			}
			
			iCurStage++;
		}
		
		if(iStageOne >= 0 && iStageTwo >= 0)
		{
			Collections.swap(m_lstStages, iStageOne, iStageTwo);
		}
	}
	

}
