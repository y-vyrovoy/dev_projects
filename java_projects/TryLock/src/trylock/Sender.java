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
public class Sender extends Thread {

    private MessageCenter _center = null;
    
    public Sender(MessageCenter center) {
        _center = center;
    }
    
    @Override
    public void run() {

        String messages[] = new String [] {
            "Message #1",
            "Message #2",
            "Message #3",
            "Message #4",
            "Message #5",
            "Message #6",
            "Message #7",
            "Message #8",
            "Message #9",
            "Message #10",
        };
        
        for(String msg : messages) {
            _center.putMessage(msg);
            
            try{
                sleep(300);
            }catch (InterruptedException ex){
                return;
            }
        }
        
         _center.putMessage(MessageCenter.EOT);
    }

}
