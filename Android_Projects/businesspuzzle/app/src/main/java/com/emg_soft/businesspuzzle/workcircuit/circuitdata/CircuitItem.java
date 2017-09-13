package com.emg_soft.businesspuzzle.workcircuit.circuitdata;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yura Vyrovoy on 9/7/2017.
 */
// base class for all elements in circuit
public  class CircuitItem {

    public enum ItemType{
        TYPE_INPUT, TYPE_OPERATOR, TYPE_RESULT;
    }

    public enum InputType {
        INPUT_ONE(1), INPUT_TWO(2), INPUT_ONLY(3), INPUT_NO_INPUT(-1);

        private int value;

        InputType(int v){
            value = v;
        }

    }

    private ItemType type;

    private CircuitItem in_one;
    private CircuitItem in_two;

    private String name;
    private String caption;

    private int mLevel;

    private CircuitContainer mCircuit;

    public CircuitItem(){

        in_one = null;
        in_two = null;

        name = "";
        caption = "";
    }

    public static interface CircuitItemChangeListener{
        public void onChange(boolean bOutputTrue);
    }

    List<CircuitItem.CircuitItemChangeListener> lstListeners = new ArrayList<>();

    public void addChangeListener(CircuitItem.CircuitItemChangeListener listener){
        lstListeners.add(listener);
    }

    public void removeChangeListener(CircuitItem.CircuitItemChangeListener listener){
        lstListeners.remove(listener);
    }

    /**
     * This funcion allows to know which input items come to
     * @param item - circuit item that might be input for current item
     * @return input type
     */
    public InputType getItemInputNumber(CircuitItem item){

        if(in_one == item){
            return InputType.INPUT_ONE;
        }
        else if(in_two == item){
            return InputType.INPUT_TWO;
        }
        else{
            return InputType.INPUT_NO_INPUT;
        }

    }

    public CircuitItem getIn_one() {
        return in_one;
    }

    public void setIn_one(CircuitItem in_one) {
        this.in_one = in_one;
    }

    public CircuitItem getIn_two() {
        return in_two;
    }

    public void setIn_two(CircuitItem in_two) {
        this.in_two = in_two;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }


    public boolean getValue(){
        throw new RuntimeException("Circuit item getValue() method should be implemented") ;
    }

    public ItemType getType(){
        throw new RuntimeException("Circuit item getType() method should be implemented") ;
    }

    public CircuitContainer getCurcuit() {
        return mCircuit;
    }

    public void setCurcuit(CircuitContainer mCurcuit) {
        this.mCircuit = mCurcuit;
    }


    public int getLevel() {
        return mLevel;
    }

    public void setLevel(int mLevel) {
        this.mLevel = mLevel;
    }

    public int getIndexInLevel(){
        return mCircuit.getLstOperatorLevels().indexOf(mLevel);
    }

}
