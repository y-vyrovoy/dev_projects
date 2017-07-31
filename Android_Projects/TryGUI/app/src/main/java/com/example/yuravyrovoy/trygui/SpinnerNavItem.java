package com.example.yuravyrovoy.trygui;


/**
 * Created by Yura Vyrovoy on 7/31/2017.
 */



public class SpinnerNavItem {

    private String title;
    private int icon;

    public SpinnerNavItem(String title, int icon){
        this.title = title;
        this.icon = icon;
    }

    public String getTitle(){
        return this.title;
    }

    public int getIcon(){
        return this.icon;
    }
}