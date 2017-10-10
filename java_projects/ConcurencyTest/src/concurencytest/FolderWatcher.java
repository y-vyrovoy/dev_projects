/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package concurencytest;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

/**
 *
 * @author Yura Vyrovoy
 */
public class FolderWatcher {
    
    public interface WatcherListener {
        public void onChange();
    }
    
    WatcherListener _listener;
    Path _folder;
    WatchService _watcher;
    
    WatcherThread _thread;
    
    public FolderWatcher(Path folder, WatcherListener listener) {
        _folder = folder;
        _listener = listener;
        
        try {
            _watcher = FileSystems.getDefault().newWatchService();
            _folder.register(_watcher, StandardWatchEventKinds.ENTRY_MODIFY );
            
        } catch(IOException ex) {
            ex.printStackTrace(System.out);
        }
    }
     
    public void startWatching() {
        _thread = new WatcherThread();
        _thread.start();
    }
    
    public void stopWatching() {
        if( _thread != null) {
            _thread.interrupt();
        }
    }
    
    public void ss() {
        _listener.onChange();
    }
    
    private class WatcherThread extends Thread {
        
        @Override
        public void run() {
            
            try {
            
                while(true) {
                    
                    if( Thread.currentThread().isInterrupted() == true) {
                        return;
                    }
                    
                    WatchKey key;
                    key = _watcher.take();
                    
                    for (WatchEvent<?> event: key.pollEvents()) {
                           WatchEvent.Kind<?> kind = event.kind();
                    
                        // The filename is the context of the event.
                        WatchEvent<Path> ev = (WatchEvent<Path>)event;
                        Path filename = ev.context();
                        
                        if (filename.endsWith("text.txt") == true) {
                            _listener.onChange();
                        }
                    }
                    
                    // Reset the key -- this step is critical if you want to
                    // receive further watch events.  If the key is no longer valid,
                    // the directory is inaccessible so exit the loop.
                    boolean valid = key.reset();
                    if (!valid) {
                        break;
                    }                    
                }
          
            
            } catch (InterruptedException exInterrupt) {
                //exInterrupt.printStackTrace(System.out);
            } catch(Exception ex) {
                ex.printStackTrace(System.out);
            }
            
        }
        
    }
}
