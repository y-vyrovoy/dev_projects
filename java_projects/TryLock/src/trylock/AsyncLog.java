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
public class AsyncLog {
    
    private static final Object _lck = new LockObject();
    
    private static boolean _readyForNext = true;

    public static void doLog(String msg, long period) {
        new LogThread(msg, period).start();
    }
    
    private static class LogThread extends Thread {
        
        private final String _msg;
        private final long _period;
        
        public LogThread(String msg, long period) {
            _msg = msg;
            _period = period;
        }
        
        @Override
        public void run() {
           
            //synchronized(_lck) {
            printLog(_msg, _period);
        }
    }
    
    private static synchronized void printLog(String msg, long period) {
        
        long idThread = Thread.currentThread().getId();

            long timeStart = System.currentTimeMillis();
            int curSec = 0;

            System.out.print(msg + " -> [");

            while (System.currentTimeMillis() < timeStart + period) {

                int tmpSec = (int)(System.currentTimeMillis() - timeStart) / 1000;

                if( tmpSec > curSec) {
                    curSec++;
                    System.out.print(curSec + " (" + idThread + ") -> ");
                }
            }
            System.out.println("|| ]");
    }
    
    
    private static class LockObject extends Object {
        
        private static int _cntLock = 0; 
        
        public LockObject() {
            System.out.println("LockObject new #" + _cntLock);
            _cntLock++;
        }
        
    }
}
