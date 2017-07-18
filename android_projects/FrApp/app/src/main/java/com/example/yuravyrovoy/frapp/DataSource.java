package com.example.yuravyrovoy.frapp;

import android.util.Log;

import org.w3c.dom.ls.LSException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yura Vyrovoy on 7/18/2017.
 */



public class DataSource {

    List<DataItem> mListData;
    private static final int EXAMPLE_LEN = 10;

    public DataSource() {
        mListData = new ArrayList<>();
        FillExamples();
    }

    public List<String> getHeaderList(){
        if(mListData == null){
            return null;
        }

        List<String> lstReturn = new ArrayList<>();
        for(DataItem di : mListData){
            lstReturn.add(di.getsHeader());
        }
        return lstReturn;
    }

    public List<String> getTextList(){
        if(mListData == null){
            return null;
        }

        List<String> lstReturn = new ArrayList<>();
        for(DataItem di : mListData){
            lstReturn.add(di.getsText());
        }
        return lstReturn;
    }

    public int getLength(){

        if(mListData == null) {
            return -1;
        }
        else{
            return mListData.size();
        }
    }

    public void AddItem(String sHeader, String sText){

        if(mListData == null){
            mListData = new ArrayList<>();
        }

        mListData.add(new DataItem(sHeader, sText));
    }

    public String getItemHeader(int position){
        if( (mListData != null) && (mListData.size() > position) )
        {
            return mListData.get(position).getsHeader();
        }
        else{
            return "";
        }
    }

    public String getItemText(int position){
        if( (mListData != null) && (mListData.size() > position) )
        {
            return mListData.get(position).getsText();
        }
        else{
            return "";
        }
    }

    public void FillExamples(){
        for(int i = 0; i < EXAMPLE_LEN; i++){
            mListData.add(new DataItem("Header " + Integer.toString(i), "Article article article article article article article article article "));
        }
    }

    private class DataItem{
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

        String sHeader;
        String sText;

        public DataItem(){
            sHeader = new String();
            sText = new String();
        }

        public DataItem(String sHeader, String sText) {
            this.sHeader = sHeader;
            this.sText = sText;
        }
    }


}
