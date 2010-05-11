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
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;
import java.util.LinkedHashMap;
import java.util.Random;
import javax.microedition.khronos.opengles.GL10;
import android.content.res.Resources;
import java.util.concurrent.locks.ReentrantLock;

public class NetHackWindowManager implements NetHackRenderer {
	/** NetHack message window type... */
	public static final int NHW_MESSAGE=1;
	/** NetHack status window type... */
	public static final int NHW_STATUS=2;
	/** NetHack map window type... */
	public static final int NHW_MAP=3;
	/** NetHack menu window type... */
	public static final int NHW_MENU=4;
	/** NetHack text window type... */
	public static final int NHW_TEXT=5;

	/** Internal Window class */
	private class Window {
		public final int id;
		public final int type;
		public final NetHackWindow window;
		
		public Window(int winid, int wintype, NetHackWindow wwindow) {
			id=winid;
			type=wintype;
			window=wwindow;
		}
	}
	
	
	/** Internal storage of windows */
	private LinkedHashMap<Integer,Window> _windows;
	private java.util.Random _random;
	private Resources _resources;
	
	private final ReentrantLock _modalLock = new ReentrantLock();
	private Window _modalWindow = null;
	
	/** Preloaded tileset form resource.. */
	private NetHackTileAtlas _tileset;
	
	/** The one and only instance of a map window.. */
	private NetHackMapWindow _mapWindow;
	
	
	public NetHackWindowManager (Resources resources) {
		_resources=resources;
		_random = new java.util.Random();
		_windows = new LinkedHashMap<Integer,Window>();
		
	}
	
	public void putStr(int winid,int attr, String str) {
		//_windows.get(winid).window.putStr(attr,str);
		Log.d(NetHack.LOGTAG,"NetHackWindowManager.putStr() window "+winid+" attributes "+attr+": '"+str+"'");
	}
	
	public void handleGlyph(int winid, int x, int y, int glyph) {
		if( _windows.containsKey(winid) )
			_windows.get(winid).window.handleGlyph(x,y,glyph);
	}
	
	public void display(int winid, int flag ) {
		//Log.d(NetHack.LOGTAG,"NetHackWindowManager.display() display window "+winid+" flag "+flag+".");
		if( _windows.containsKey(winid) )
			_windows.get(winid).window.display(flag);
	}
	
	public void destroy(int winid) {
		if( _windows.containsKey(winid) ) {
			_windows.get(winid).window.destroy();
			_windows.remove(_windows.get(winid));
		}
	}
	
	public int create( int type ) {
		int winid = generateWindowID();
		Log.d(NetHack.LOGTAG,"NetHackWindowManager.create() new window"+winid);
		switch(type) {
			case NHW_MAP:
			{
				Log.d(NetHack.LOGTAG,"NetHackWindowManager.create() creating map window.");
				// Intiialize map renderer
				_mapWindow = new NetHackMapWindow();
				_windows.put(winid,new Window(winid,type,_mapWindow));
				
			} break;
			
			case NHW_MESSAGE: 
			{
				Log.d(NetHack.LOGTAG,"NetHackWindowManager.create() creating message window.");
			} break;
			case NHW_STATUS:
			{
				Log.d(NetHack.LOGTAG,"NetHackWindowManager.create() creating status window.");
			} break;
			case NHW_MENU:
			{
				Log.d(NetHack.LOGTAG,"NetHackWindowManager.create() creating menu window.");
			} break;
			case NHW_TEXT:
			{
				Log.d(NetHack.LOGTAG,"NetHackWindowManager.create() creating text window.");
				NetHackTextWindow tw=new NetHackTextWindow(_resources);
				doModalWindow( new Window(winid,type,tw) );
			} break;
			default:
				
			break;
		}
		return winid;
	}
	
	private void doModalWindow(Window w) {
		_modalLock.lock();
		_modalWindow = w;
		while( isModalWindow() ) {
			try {
				Thread.sleep(500);
			} catch (java.lang.InterruptedException e) {
			}
		}
	}
	
	public void endModalWindow() {
		if( isModalWindow() ) {
			_modalLock.unlock();
		}
	}
	
	public boolean isModalWindow() {
		return _modalLock.isLocked();
	}
	
	
	/** Check if winid exists */
	private boolean exists(int winid) {
		return _windows.containsKey(winid);
	}
	
	/** Generate a new winid */
	private int generateWindowID() {
		int id=1+(_random.nextInt()%50);
		while( exists(id) ) id=1+(_random.nextInt()%50);
		return id;
	}
	
	
	public void init(GL10 gl) {
		Log.d(NetHack.LOGTAG,"NetHackWindowManager.init() Intialize window manager renderer.");
		_tileset = NetHackTileAtlas.createFromResource(_resources,R.drawable.absurd16);
		_tileset.generate( gl );
		_mapWindow.setTileset(_tileset);
	}
	
	public void render(GL10 gl) {
		// Check if we got a modal window showing
		if( isModalWindow() ) {
			// We are showing a modal window lets render it..
			_modalWindow.window.render( gl );
		} else {
			// Run thru all window and render them...
			Set ws=_windows.entrySet();
			Iterator<Entry> it = ws.iterator();
			if( it.hasNext() ) {			
				do {
					Entry<Integer,Window> e = it.next();
					Window w = e.getValue();
					// render the window..
					w.window.render( gl );
				} while( it.hasNext() );
			}			
		}
	}
	
}