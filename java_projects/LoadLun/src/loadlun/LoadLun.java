/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loadlun;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import javax.xml.transform.stream.StreamResult;

/**
 *
 * @author vyrovoy
 */
public class LoadLun {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        LoadLunProcessor llp = LoadLunProcessor.GetNewProcessor();

        llp.StartLoading();
                
    }
    
}
