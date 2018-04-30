package com.emg_soft.businesspuzzle.workcircuit.circuitdata;

/**
 * Created by Yura Vyrovoy on 9/8/2017.
 */

public class CircuitTrack {

    private CircuitItem itemStart = null;
    private CircuitItem itemEnd = null;

    public CircuitTrack(CircuitItem start, CircuitItem end){

        setItemStart(start);
        setItemEnd(end);
    }

    public CircuitItem getItemStart() {
        return itemStart;
    }

    public void setItemStart(CircuitItem itemStart) {
        this.itemStart = itemStart;
    }

    public CircuitItem getItemEnd() {
        return itemEnd;
    }

    public void setItemEnd(CircuitItem itemEnd) {
        this.itemEnd = itemEnd;
    }

}
