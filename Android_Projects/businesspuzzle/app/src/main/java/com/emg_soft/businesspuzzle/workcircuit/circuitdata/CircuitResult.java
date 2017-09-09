package com.emg_soft.businesspuzzle.workcircuit.circuitdata;

/**
 * Created by Yura Vyrovoy on 9/7/2017.
 */

// circuit operators class
public class CircuitResult extends CircuitItem {
    CircuitItem in;

    public CircuitItem getIn() {
        return in;
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
            return super.getItemInputNumber(item);
        }

    }

}
