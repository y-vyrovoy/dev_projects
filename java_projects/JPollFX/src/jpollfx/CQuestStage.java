package jpollfx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class CQuestStage 
{
    public static enum enmViewType
    {
        VT_VERTICAL(1), VT_HORIZONTAL(2), VT_COMBO(3);

        private IntegerProperty propValue;

        private enmViewType(int i) 
        {
            this.propValue.setValue(i);
        }

    }

    public static enum enmStageType 
    {
        ST_SINGLE_ANSWER, ST_MULTY_ANSWER;
    }
	
	
    // fields
	
    private final StringProperty m_sCaption;

    private enmViewType m_vtViewType;
    private final StringProperty sStageType;

    //String question ID. Questionnaire navigation use it, MUST be unique!!!
    private final SimpleStringProperty sID;  

    //List of alternatives
    private List<CQuestAnswer> lstItems;

    public CQuestStage()
    {
        lstItems = new ArrayList<>();
        m_sCaption = new SimpleStringProperty();
        sID = new SimpleStringProperty();
        sStageType = new SimpleStringProperty();
    }

    // Copy constructor
    public CQuestStage(CQuestStage stgSrc)
    {
        lstItems = new ArrayList<>();
        
        m_sCaption = new SimpleStringProperty(stgSrc.m_sCaption.getValue());
        sID = new SimpleStringProperty(stgSrc.getID());
        m_vtViewType = stgSrc.m_vtViewType;
        sStageType = new SimpleStringProperty(stgSrc.sStageType.getValue());
        lstItems.addAll(stgSrc.lstItems);

    }



    public String getCaption() 
    {
        return m_sCaption.getValue();
    }

    public void setCaption(String sParam) 
    {
        this.m_sCaption.setValue(sParam);
    }

    public StringProperty paramSCaption()
    {
        return m_sCaption;
    }

    public String getID() 
    {
        return sID.getValue();
    }

    public void setID(String sParam) 
    {
        this.sID.setValue(sParam);
    }

    public SimpleStringProperty paramSID()
    {
        return sID;
    }

    public void setViewType(enmViewType vtViewType)
    {
        this.m_vtViewType = vtViewType;
    }

    public enmViewType getViewType()
    {
        return this.m_vtViewType;
    }

    public void setStageType(String sStageType)
    {
        this.sStageType.setValue(sStageType);
    }

    public String getStageType()
    {
        return this.sStageType.getValue();
    }	

    public StringProperty paramSStageType()
    {
        return sStageType;
    }

    public List<CQuestAnswer> getAnswerList() 
    {
        return lstItems;
    }

    /**
     * ��������� ���� ������� � ������ 
     * @param qaNew 
     * @return true if item was added successfully
     */
    public boolean AddItem(CQuestAnswer qaNew)
    {
        CQuestAnswer qaTmp = new  CQuestAnswer(qaNew);
        return lstItems.add(qaTmp);
    }

    public void setItemsList(List<CQuestAnswer> lst)
    {
        lstItems = lst;
    }
    
    public int getAnswersListSize()
    {
        return lstItems.size();
    }

    public ListIterator<CQuestAnswer> GetAnswersIterator()
    {
        ListIterator<CQuestAnswer> it = lstItems.listIterator();
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


        while ((it.hasNext() == true) || 
                ((iAnswerOne < 0) && (iAnswerTwo < 0) ))
        {
            CQuestAnswer qaCurrent = it.next();

            if(qaCurrent == qaOne){
                iAnswerOne = iCurStage;
            }

            if(qaCurrent == qaTwo){
                iAnswerTwo = iCurStage;
            }

            iCurStage++;
        }

        if(iAnswerOne >= 0 && iAnswerTwo >= 0)
        {
            Collections.swap(lstItems, iAnswerOne, iAnswerTwo);
        }
    }
    
    public boolean AlternativeExists(String sTest)
    {
        return lstItems.stream().filter(o -> o.getCaption().equals(sTest)).findFirst().isPresent();
    }
    
}
