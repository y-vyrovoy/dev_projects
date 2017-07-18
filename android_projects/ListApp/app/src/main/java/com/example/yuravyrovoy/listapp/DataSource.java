package com.example.yuravyrovoy.listapp;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Yura Vyrovoy on 7/17/2017.
 */

public class DataSource {

    private static int nCounter = 0;

    private List<cDataItem> lstReturn;

    public DataSource(){

        lstReturn = new ArrayList<>();

        lstReturn.add(new cDataItem("Title 1", "v" + Integer.toString(nCounter)));
        lstReturn.add(new cDataItem("Title 2", "v" + Integer.toString(nCounter)));
        lstReturn.add(new cDataItem("Title 3", "v" + Integer.toString(nCounter)));
        lstReturn.add(new cDataItem("Title 4", "v" + Integer.toString(nCounter)));
        lstReturn.add(new cDataItem("Title 5", "v" + Integer.toString(nCounter)));

        nCounter++;
    }

    public int size(){
        return lstReturn.size();
    }

    public cDataItem getItem(int position){
        return lstReturn.get(position);
    }

    public class cDataItem{

        public cDataItem(String sHeader, String sText){
            setsHeader(sHeader);
            setsText(sText);
        }

        public String getsHeader() {
            return sHeader;
        }

        public void setsHeader(String sHeader) {
            this.sHeader = sHeader;
        }

        public String getsText() {
            return sText;
        }

        public void setsText(String sText) {
            this.sText = sText;
        }

        protected String sHeader;

        protected String sText;
    }
}
