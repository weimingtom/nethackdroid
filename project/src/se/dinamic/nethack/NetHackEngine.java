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

import java.util.Random;
import android.util.Log;
import android.view.View;
import android.view.KeyEvent;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import android.opengl.GLSurfaceView;

public class NetHackEngine extends LibNetHack implements View.OnKeyListener
{
    private Activity _context;
    private KeyEventQueue _keyevent;
    private NetHackView _view;
    private NetHackWindowManager _wm;
    
    public NetHackEngine(Activity context) 
   {
	_context=context;
	// Setup view
	_wm = new NetHackWindowManager(context.getResources());
	_view = new NetHackView(context);
	_keyevent = new KeyEventQueue();
         
	_view.addRenderer( _wm );
	   
	_view.setOnKeyListener(this);
        
   }	   
    
   public GLSurfaceView getView() {
       return _view;
   }
   
    public void start() {
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
        // Let do the splash screen....
    }
    
    public void onRawPrint(String status) {
       // This should probable been overlayed on ui as a log...
        Toast toast = Toast.makeText(_context, status, Toast.LENGTH_LONG);
        toast.show();
    }
    
    public void onPutStr(int winid,int attr,String status) { }
    
    public void onPrintGlyph(int winid,int x,int y,int glyph) { _wm.handleGlyph( winid, x, y, glyph ); }
   
    public int onGetKey() { return _keyevent.getKey(); }
   
    public int onCreateWindow(int type) {  return _wm.create( type ); }
    
    public void onDisplayWindow(int winid,int flag) { _wm.display( winid, flag ); }
    
    /**
     *  implementation of View.onKeyListener 
     */
    public boolean onKey(View v, int keyCode, KeyEvent event) {
	Log.v("NetHackEngine.onKey","on key "+keyCode);
	_keyevent.addKey(keyCode);
	return true;
    }
}
