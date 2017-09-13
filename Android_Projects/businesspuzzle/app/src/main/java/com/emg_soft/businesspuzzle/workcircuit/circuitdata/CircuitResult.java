package com.emg_soft.businesspuzzle.workcircuit.circuitdata;

/**
 * Created by Yura Vyrovoy on 9/7/2017.
 */

// circuit operators class
public class CircuitResult extends CircuitItem {

    private CircuitItem in;
    private String mTextSuccess;
    private String mTextFail;


    public CircuitItem getIn() {
        return in = getIn_one();
    }

    public void setIn(CircuitItem in) {
        this.setIn_one(in);
        this.in = in;
    }

    @Override
    public void setIn_one(CircuitItem in) {
        super.setIn_one(in);
        this.in = in;
    }

    @Override
    public boolean getValue(){

        if(in == null){
            return false;
        }
        return in.getValue();
    }

    @Override
    public ItemType getType(){return ItemType.TYPE_RESULT;}

    public InputType getItemInputNumber(CircuitItem item) {

        if( getIn_one() == item ) {
            return InputType.INPUT_ONLY;
        }
        else {
            return InputType.INPUT_NO_INPUT;
        }
    }

    public String getmTextSuccess() {
        return mTextSuccess;
    }

    public void setmTextSuccess(String mTextSuccess) {
        this.mTextSuccess = mTextSuccess;
    }

    public String getmTextFail() {
        return mTextFail;
    }

    public void setmTextFail(String mTextFail) {
        this.mTextFail = mTextFail;
    }

}
