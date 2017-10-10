/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package concurencytest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author Yura Vyrovoy
 */
public final class LogSaver {
    
    public static final long TIME_QUANTUM_MS = 100;
    
    private static LogSaver _instance = null;
    private Path _path = null;
    private final Map<Integer, MessageChannel> _mapChannels = new HashMap<>();
    
    private static final Object _lock = new Object();
    
    private LogSaver(Path path) {
        _path = path;
    }
    
    public static void init(Path path) {
        _instance = new LogSaver(path);
    }
    
    public static void saveMessage(String msg, long period) {
        new SaveMessageThread(msg, period).start();
    }
    
    private static class SaveMessageThread extends Thread {

        
        private final String _msg;
        private final long _length;
        
        public SaveMessageThread(String msg, long length) {
            _msg = msg;
            _length = length;
        }
        
        @Override
        public void run() {

            if(_instance == null) {
                return;
            }
            
            synchronized(_lock){

                FileWriter writer = null;
                try {
                    long timeStart = System.currentTimeMillis();
                    long timeCurr = timeStart;

                    writer = new FileWriter(new File(_instance._path.toString()), true);
                    writer.flush();

                    String startTimeString = MsToString(timeStart);
                    writer.write(_msg + ": " + startTimeString + "[");

                    while(timeCurr < timeStart + _length){
                        writer.write(".");
                        writer.flush();
                        sleep(TIME_QUANTUM_MS);
                        timeCurr = System.currentTimeMillis();
                    }

                    String endTimeString = MsToString(System.currentTimeMillis());
                    writer.write("] " + endTimeString);
                    writer.write("\n");
                    writer.close();
                    sleep(25);
                } catch (InterruptedException ex) {
                    if(writer != null) {
                        try {
                            writer.close();
                        } catch(IOException exIO) {}
                    }
                } catch(IOException exIO){
                    exIO.printStackTrace(System.out);
                }
            }
        }
    }
    
    private static String MsToString(long millis) {
        Date date = new Date(millis);
        SimpleDateFormat formatter= new SimpleDateFormat("HH:mm:ss.SSS");
        String formatted = formatter.format(date );
        
        return formatted;
    }

    public static void addChannel(Integer id, String msg, long period) {
        if(_instance == null) {
            throw new NullPointerException();
        }
        
        removeChannel(id);
        
        _instance._mapChannels.put(id, new MessageChannel(msg, period));
        
    }
    
    public static void removeChannel(Integer id) {
        if(_instance == null) {
            throw new NullPointerException();
        }
        
        if(_instance._mapChannels.containsKey(id) == false) {
            return;
        }
        
        _instance._mapChannels.get(id).stop();
        _instance._mapChannels.remove(id);
    }
    
    public static void removeAllChannels() {
        if(_instance == null) {
            throw new NullPointerException();
        }   
        
        new ArrayList<>(_instance._mapChannels.keySet()).forEach((t) -> removeChannel(t));
    }
    
    public static void runAllChannels() {
        if(_instance == null) {
            throw new NullPointerException();
        }
        
        new ArrayList<>(_instance._mapChannels.values()).forEach((t) -> t.run());
    }
    
    private static int c = 0;
    
    private static class MessageChannel {
        
        private final String _msg;
        private final long _period;
        private Thread _thread;
        
        public MessageChannel(String msg, long period) {
            _msg = msg;
            _period = period;
        }
        
        public void run() {        
            
            _thread = new Thread( () -> {
                
                while(Thread.currentThread().isInterrupted() == false) {
                    saveMessage(_msg, _period);   

                    c++;
                    if(c == 10) {
                        Thread.currentThread().interrupt();
                    }

                }
            });
            _thread.start();
        }
        
        public void stop() {
            if(_thread != null) {
                _thread.interrupt();
            }
        }
                
    }
}
