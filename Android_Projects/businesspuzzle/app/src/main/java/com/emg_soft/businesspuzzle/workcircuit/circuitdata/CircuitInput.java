package com.emg_soft.businesspuzzle.workcircuit.circuitdata;

/**
 * Created by Yura Vyrovoy on 9/7/2017.
 */

public class CircuitInput extends CircuitItem {

    private boolean value;

    public void setValue(boolean param){
        value = param;

        for (CircuitItemChangeListener listener : lstListeners) {
            listener.onChange(param);
        }
    }

    @Override
    public boolean getValue(){
        return value;
    }

    @Override
    public ItemType getType(){return ItemType.TYPE_INPUT;}

}
