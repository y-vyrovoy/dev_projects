package jpollfx;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;



public class CQuestionnaire 
{

	// fields
	private final List<CQuestStage> m_lstStages;
	
	public CQuestionnaire()
	{
            m_lstStages = new ArrayList<>();
        }

	// Copy constructor
	public CQuestionnaire(CQuestionnaire qnrSrc)
	{
		
            m_lstStages = new ArrayList<>();

            m_lstStages.addAll(qnrSrc.m_lstStages);
	}
	
	
/**
 * 
 * @param stgNew New stage that must be added
 * @param stgBefore Stage that should prevent stgNew
 * @return true if stage was added successfully and false if not
 */
        public boolean AddStage(CQuestStage stgNew, CQuestStage stgBefore)
	{

            CQuestStage stgTmp = new CQuestStage(stgNew);
            if(stgBefore == null){
                return m_lstStages.add(stgTmp);
            }
            else{
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

	
	// ������� stage � �������� ID � ���������� ���
	public CQuestStage setStageByID(String sID)
	{
            CQuestStage stReturn;

            ListIterator<CQuestStage> it = m_lstStages.listIterator(); 
            boolean bFind = false;

            while(it.hasNext() && bFind==false)
            {

                stReturn = it.next();

                if (stReturn.getID().equals( sID) )
                {
                    return stReturn;
                }
            }

            return null;
		
	}
	
        public List<CQuestStage> getStageList()
        {
            return m_lstStages;
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

            if ( (getStagesListSize() < 1) ||
                (sStageID.equals(CQuestAnswer.NEXT_STAGE_NEXT) == true) )
            {
                return null;
            }

            Iterator<CQuestStage> it = getStagesIterator();

            CQuestStage stgResult;

            while( (it.hasNext() == true) )
            {
                stgResult = it.next();
                if(stgResult.getID().equals(sStageID) == true)
                {
                    return stgResult;				
                }
            }
            
            return null;
	}
	
        /**
         * Returns stage number by its ID
         * @param stgStage
         * @return 
         */
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
	
	public void RemoveStageFromAnswers(CQuestStage stgToRemove)
	{
         
            m_lstStages.forEach(stgItem -> {
                                    ((CQuestStage)stgItem).getAnswerList().forEach(ansItem -> {
                                        if( (((CQuestAnswer)ansItem).getNextStage() != null) && 
                                                ((CQuestAnswer)ansItem).getNextStage().equals(stgToRemove))
                                        {
                                            ((CQuestAnswer)ansItem).setNextStageNext();
                                        }
                                    });
                                });
	}
        
        
	public boolean isStageIDUnique(String sNewStageID)
	{
            return m_lstStages.stream().filter(o -> o.getID().equals(sNewStageID)).findFirst().isPresent();
	}

}
