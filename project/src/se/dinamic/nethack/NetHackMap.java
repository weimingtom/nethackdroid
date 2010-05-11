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

import java.util.Set;
import java.util.Map.Entry;
import java.util.LinkedHashMap;
import java.util.concurrent.locks.ReentrantLock;

import android.util.Log;

class NetHackMap {
	
	static public class Position extends Object{
		private short _X;
		private short _Y;
		public int getX() { return _X; }
		public int getY() { return _Y; }
		public Position( short x,short y) { _X=x; _Y=y; }
		public int hashCode() { return _X << 16 <<_Y;}
		public boolean equals(Object p) {
			if( p.hashCode() == hashCode() ) return true;
			return false;
		}
	}
	
	private LinkedHashMap<Position, Integer > _map;
	private String _levelName;
	private final ReentrantLock _lock = new ReentrantLock();
	/** The max size of map... */
	//public static final int SIZE=256;
	
	/** This should be replaced with a more sophisticated type than int... like NetHackMapObject */
	//private int _map[][];
	
	protected int _playerX,_playerY;
	
	public NetHackMap () {
		//_map=new int[SIZE][SIZE];
		_map=new LinkedHashMap<Position,Integer>();
	}
	
	/** this clears the map, set's the new levels name.. */
	public void newLevel(String name) {
		_map.clear();
		_levelName=name;
	}
	
	public Set get() {
		return _map.entrySet();
	}
	
	public void lock() {
		_lock.lock();
	}
	
	public void unlock() {
		_lock.unlock();
	}
		
	public void handleGlyph(int x, int y,int glyph) {
		if( glyph<=394 ) { // we got a player glyph
			_playerX=x;
			_playerY=y;
		}
		Position pos=new Position((short)x,(short)y);
		lock();
		_map.put(pos,glyph);
		Log.v(NetHack.LOGTAG,"NetHackMap.handleGlyph() update map pos "+x+"x"+y+" with glyph "+glyph+", mapsize "+_map.size());
		unlock();
	}
}