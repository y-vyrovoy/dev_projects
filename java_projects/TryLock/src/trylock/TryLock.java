/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trylock;

/**
 *
 * @author yura
 */
public class TryLock {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //AsyncLog.doLog("Message One", 7200);
        //AsyncLog.doLog("Message Two", 6400);
        
        MessageCenter center = new MessageCenter();
        new Receiver(center).start();
        new Sender(center).start();
    }
    
}
