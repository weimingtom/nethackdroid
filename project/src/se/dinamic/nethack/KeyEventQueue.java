package se.dinamic.nethack;
import android.util.Log;
import java.util.concurrent.LinkedBlockingQueue;
import java.lang.InterruptedException;

public class KeyEventQueue {
    private LinkedBlockingQueue<Integer> _queue;
    
    public KeyEventQueue() {
        _queue=new LinkedBlockingQueue<Integer>(100);
    }
    
    /** This will block until queue have an key event to fetch */
    public int getKey() {
        while(true) {
            try {
                return _queue.take();
            } catch (java.lang.InterruptedException e) {
            }
        }
    }
    
    /** Add a key to the queue */
    public void addKey(int keysym) {
        Log.v("KeyEventQueue.addKey","Added key "+keysym);
        try {
            _queue.put(keysym);
        } catch (InterruptedException e) {
        }
    }
}
