/*
    This file is part of nethackdroid,
    copyright (c) 2010 Henrik Andersson.

    nethackdroid is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    nethackdroid is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with nethackdroid.  If not, see <http://www.gnu.org/licenses/>.
*/

package se.dinamic.nethack;

import java.util.Random;
import android.util.Log;
import android.view.View;
import android.view.KeyEvent;
import android.app.Application;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import android.opengl.GLSurfaceView;

public class NetHackEngine extends LibNetHack implements View.OnKeyListener
{
    private Application _application;
    private KeyEventQueue _keyevent;
    private static NetHackView _view;
    private NetHackWindowManager _wm;
    private NetHackSound _sound;
	
    public NetHackEngine(Application application) 
   {
	_application=application;
	// Setup view
	_sound = new NetHackSound( application );
	_wm = new NetHackWindowManager( application );
	_view = new NetHackView( application );
	_keyevent = new KeyEventQueue();
         
	
	_view.addRenderer( _wm );
	   
	_view.setOnKeyListener(this);
        
	// Let initialize...
	_view.setState( NetHackView.STATE_INITIALIZE_GAME );
   }	   
    
   public GLSurfaceView getView() {
       return _view;
   }
   
    public static void startGame() {
	_view.setState( NetHackView.STATE_GAME_RUN );
       run();
    }
    
    public void onPause() {
        _view.onPause();
    }
    
    public void onResume() {
        // Also pause/resume game when we find a way...
        _view.onResume();
    }
   
   /** 
    *   Implementations of libnethack abstract functions
    */
    public void onInitWindows() {
        // Les do the splash screen....
    }
    
    public void onRawPrint(String status) {
       // This should probable been overlayed on ui as a log...
        Toast toast = Toast.makeText(_application, status, Toast.LENGTH_LONG);
        toast.show();
    }
    
    public void onPutStr(int winid,int attr,String status) { 
	    _sound.putStr( attr, status );
	    _wm.putStr( winid, attr, status);	    
    }
    
    public void onPrintGlyph(int winid,int x,int y,int glyph) { _wm.handleGlyph( winid, x, y, glyph ); }
   
    public int onGetKey() { return _keyevent.getKey(); }
   
    public int onCreateWindow(int type) {  return _wm.create( type ); }
    
    public void onDisplayWindow(int winid,int flag) { _wm.display( winid, flag ); }
    
    public void onDestroyWindow(int winid) { _wm.destroy( winid ); }
    
    /**
     *  implementation of View.onKeyListener 
     */
    public boolean onKey(View v, int keyCode, KeyEvent event) {
	int action=event.getAction();
	
	if(  action == KeyEvent.ACTION_DOWN ) {
		switch( keyCode ) {
			case KeyEvent.KEYCODE_VOLUME_UP:
				_wm.zoomOutMap();
			break;
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				_wm.zoomInMap();
			break;
			
			case KeyEvent.KEYCODE_DPAD_CENTER:
				_wm.endModalWindow();
			break;
		}
	}
		
	// Pass it on th the eventqueue for nethack core
	if( action == KeyEvent.ACTION_DOWN || action ==KeyEvent.ACTION_MULTIPLE)
		_keyevent.addKey(keyCode);
	
	return true;
    }
}
