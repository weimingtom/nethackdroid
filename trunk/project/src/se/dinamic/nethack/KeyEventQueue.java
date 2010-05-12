/*
    This file is part of nethackdroid,
    copyright (c) 2010 Henrik Andersson.

    darktable is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    darktable is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with darktable.  If not, see <http://www.gnu.org/licenses/>.
*/

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
	    Log.d(NetHack.LOGTAG,"KeyEventQueue.getKey() getting key from queue...");
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
                case KeyEvent.KEYCODE_J:
                case KeyEvent.KEYCODE_DPAD_UP:
                    key = 'J' | ((keysym&KeyEvent.META_SHIFT_LEFT_ON)!=0?NETHACK_META_BIT:0);
                break;
                
                case KeyEvent.KEYCODE_K:
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    key = 'k' | ((keysym&KeyEvent.META_SHIFT_LEFT_ON)!=0?NETHACK_META_BIT:0);
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
            if(key!=0) {
	        Log.d(NetHack.LOGTAG,"KeyEventQueue.addKey() adding key "+keysym+" to queue...");
                _queue.put(key);
	    }
            
        } catch (InterruptedException e) {
        }
    }
}