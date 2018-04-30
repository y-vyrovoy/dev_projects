package jpollfx;

import java.io.*;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class CQuestAnswer implements Serializable {

    private SimpleStringProperty sCaption;
    private IntegerProperty nValue;
    private CQuestStage stgNext;

    public final static String NEXT_STAGE_NEXT = "#Next stage!#";
    public final static String OPENED_ANSWER = "#Opened answer!#";
    public final static String NO_ANSWER = "No answer";

    public CQuestAnswer() {
        sCaption = new SimpleStringProperty("");
        nValue = new SimpleIntegerProperty(-1);

        stgNext = null;
    }

    public CQuestAnswer(String sName) {
        sCaption = new SimpleStringProperty(sName);
        nValue = new SimpleIntegerProperty(-1);

        stgNext = null;
    }

    public CQuestAnswer(CQuestAnswer qaSrc) {
        sCaption.setValue(qaSrc.sCaption.getValue());
        nValue.setValue(qaSrc.nValue.getValue());

        stgNext = qaSrc.stgNext;
    }

    public String getCaption() {
        return sCaption.getValue();
    }

    public void setCaption(String sC) {
        sCaption.setValue(sC);
    }

    public SimpleStringProperty paramSCaption() {
        return sCaption;
    }

    public int getValue() {
        return nValue.getValue();
    }

    public void setValue(int nV) {
        nValue.setValue(nV);
    }

    public IntegerProperty paramNValue() {
        return nValue;
    }

    public CQuestStage getNextStage() {
        return stgNext;
    }

    public void setNextStage(CQuestStage stgParam) {
        stgNext = stgParam;
    }

    public boolean isNextStageNext() {
        return (stgNext == null);
    }

    public void setNextStageNext() {
        stgNext = null;
    }

    public boolean isAnswerOpened() {
        return sCaption.getValue().equals(OPENED_ANSWER);
    }

    public void setAnswerOpened() {
        sCaption.setValue(OPENED_ANSWER);
    }

}
