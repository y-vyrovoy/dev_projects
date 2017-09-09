package com.emg_soft.businesspuzzle.workcircuit.circuitdata;

/**
 * Created by Yura Vyrovoy on 9/7/2017.
 */

// circuit operators class

public class CircuitOperator extends CircuitItem {

    // Circuit items classes

    public enum CircuitOperatorType{
        OP_AND, OP_OR, OP_XOR, OP_NOT, OP_NO_TYPE;

        @Override
        public String toString(){

            switch (this){

                case OP_AND:
                    return "AND";

                case OP_OR:
                    return "OR";

                case OP_XOR:
                    return "XOR";

                case OP_NOT:
                    return "NOT";

                case OP_NO_TYPE:
                default:
                    return "NO_OP";
            }
        }
    }

    private CircuitOperatorType type;

    public CircuitOperator(CircuitOperatorType operatorType){
        super();
        this.type = operatorType;
    }

    @Override
    public boolean getValue(){

        switch(type){

            case OP_AND:
                return getIn_one().getValue() && getIn_two().getValue();

            case OP_OR:
                return getIn_one().getValue() || getIn_two().getValue();

            case OP_XOR:
                return (getIn_one().getValue() && !getIn_two().getValue()) ||
                        (!getIn_one().getValue() && getIn_two().getValue());

            case OP_NOT:
                return !getIn_one().getValue();

            default:
                return false;
        }
    }

    @Override
    public ItemType getType(){return ItemType.TYPE_OPERATOR;}

    public CircuitOperatorType getOperatorType(){
        return type;
    }

    public InputType getItemInputNumber(CircuitItem item) {

        if( (type == CircuitOperatorType.OP_NOT) && (getIn_one() == item) ) {
            return InputType.INPUT_ONLY;
        }
        else {
            return super.getItemInputNumber(item);
        }
    }

    public int getOperatorLevel(){
        if(getCurcuit() == null){
            return -1;
        }

        return getCurcuit().getOperatorLevel(this);
    }
}
