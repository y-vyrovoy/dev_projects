/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trylock;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Yura Vyrovoy
 */
public class MessageCenter {

    public static final String EOT = "[#end_of_transaction#]";
    
    private String _msg;
    private volatile boolean _readyForNext;
    
    public MessageCenter() {
        _readyForNext = true;
    }

    public synchronized void putMessage(String msg) {
        
        while(_readyForNext == false) {
            try {
                wait();
            } catch (InterruptedException ex) {
                return;
            }
        }
        _readyForNext = false;

        _msg = msg + " [s:" + MessageCenter.MsToString(System.currentTimeMillis()) + "]";
        notifyAll();
    }
    
    public synchronized String getMessage() {
        while(_readyForNext == true) {
            try {
                wait();
            } catch (InterruptedException ex) {
                return null;
            }
        }
        String ret = _msg + " [r:" + MessageCenter.MsToString(System.currentTimeMillis()) + "]";

        _readyForNext = true;
        notifyAll();
        
        return ret;
    }
    
    public static String MsToString(long millis) {
        Date date = new Date(millis);
        SimpleDateFormat formatter= new SimpleDateFormat("HH:mm:ss.SSS");
        String formatted = formatter.format(date );
        return formatted;
    }

}
