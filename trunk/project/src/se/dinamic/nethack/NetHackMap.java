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

class NetHackMap {
	/** The max size of map... */
	public static final int SIZE=256;
	
	/** This should be replaced with a more sophisticated type than int... like NetHackMapObject */
	private int _map[][];
	
	protected int _playerX,_playerY;
	
	public NetHackMap () {
		_map=new int[SIZE][SIZE];
	}
	
	public int get(int x,int y) {
		return _map[x][y];
	}
	
	public void handleGlyph(int x, int y,int glyph) {
		if( glyph<=394 ) { // we got a player glyph
			_playerX=x;
			_playerY=y;
		}
		_map[x][y]=glyph;
	}
}