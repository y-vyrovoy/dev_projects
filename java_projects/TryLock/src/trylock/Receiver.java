/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trylock;

/**
 *
 * @author Yura Vyrovoy
 */
public class Receiver  extends Thread {

    private MessageCenter _center = null;
    
    public Receiver(MessageCenter center) {
        _center = center;
    }    
    
    @Override
    public void run() {
        
        while(Thread.currentThread().isInterrupted() == false) {
            
            String msgCurrent = _center.getMessage();
            System.out.println(msgCurrent);
            
            if(msgCurrent.contains(MessageCenter.EOT)) {
                return;
            }
            
            try{
                sleep(500);
            }catch (InterruptedException ex){
                return;
            }            
        }
    }
}
