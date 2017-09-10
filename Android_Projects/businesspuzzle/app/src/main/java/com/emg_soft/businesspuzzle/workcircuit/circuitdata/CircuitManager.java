package com.emg_soft.businesspuzzle.workcircuit.circuitdata;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;

import com.emg_soft.businesspuzzle.R;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yura Vyrovoy on 9/10/2017.
 */

public class CircuitManager {

    private static final List<CircuitContainer> lstContainers = new ArrayList<>();

    public static void initFromResources(Resources resources, String packageName){

        lstContainers.clear();

        Field[] fields = R.raw.class.getFields();

        for (int i = 0; i < fields.length; i++) {

            String name = fields[i].getName();

            int id = resources.getIdentifier(name, "raw", packageName);
            CircuitContainer container = null;

            try {
                InputStream inputStream = resources.openRawResource(id);
                container = CircuitContainer.createCurcuitFromXML(inputStream);
            }
            catch (Exception ex){
                ex.printStackTrace();
            }

            if(container != null){
                lstContainers.add(container);
            }
        }
    }

    public static int ContainersCount(){
        return lstContainers.size();
    }

    @Nullable
    public static CircuitContainer getContainer(int i){

        if(i <= lstContainers.size()) {
            return lstContainers.get(i);
        }
        else{
            return null;
        }
    }

}
