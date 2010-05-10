package se.dinamic.nethack;
import android.util.Log;
import java.util.concurrent.LinkedBlockingQueue;
import java.lang.InterruptedException;
import android.view.KeyEvent;

public class KeyEventQueue {
    private LinkedBlockingQueue<Integer> _queue;
    public final static int NETHACK_META_BIT = 128;
    
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
        int key=0;
        try {
            switch(keysym) {
                /*
                 * NAVIGATION KEY MAPPING
                 */
                case KeyEvent.KEYCODE_K:
                case KeyEvent.KEYCODE_DPAD_UP:
                    key = 'k' | ((keysym&KeyEvent.META_SHIFT_LEFT_ON)!=0?NETHACK_META_BIT:0);
                break;
                
                case KeyEvent.KEYCODE_J:
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    key = 'j' | ((keysym&KeyEvent.META_SHIFT_LEFT_ON)!=0?NETHACK_META_BIT:0);
                break;
                
                case KeyEvent.KEYCODE_H:
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    key = 'h' | ((keysym&KeyEvent.META_SHIFT_LEFT_ON)!=0?NETHACK_META_BIT:0);
                break;
                
                case KeyEvent.KEYCODE_L:
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    key = 'l' | ((keysym&KeyEvent.META_SHIFT_LEFT_ON)!=0?NETHACK_META_BIT:0);
                break;
                
                case KeyEvent.KEYCODE_PERIOD:
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    key = '.' | ((keysym&KeyEvent.META_SHIFT_LEFT_ON)!=0?NETHACK_META_BIT:0);
                break;
                
                case KeyEvent.KEYCODE_Q:
                case KeyEvent.KEYCODE_BACK:
                    key = 'q' | ((keysym&KeyEvent.META_SHIFT_LEFT_ON)!=0?NETHACK_META_BIT:0);
                break;
                
            }
            
            // Add key to queue
            if(key!=0)
                _queue.put(key);
            
        } catch (InterruptedException e) {
        }
    }
}
