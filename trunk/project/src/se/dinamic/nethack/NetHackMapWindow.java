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
import java.util.Iterator;

import java.nio.ShortBuffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.util.Log;


import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class NetHackMapWindow extends NetHackMap implements NetHackWindow {
	private NetHackTileAtlas _atlas;
	
	private static float PLANE_VERTICES[] = {
		-0.5f, -0.5f,  0.0f,	// Image plane
		 0.5f, -0.5f,  0.0f,
		-0.5f,  0.5f,  0.0f,
		 0.5f,  0.5f,  0.0f
	};
	
	private float PLANE_TEXTURE_COORDS[] = {
		0.0f, 1.0f,
		1.0f, 1.0f,
		0.0f, 0.0f,
		1.0f, 0.0f
	};
	
	public FloatBuffer _planeVertices;
	public FloatBuffer _planeTextureCoords;
	
	public NetHackMapWindow() {
		// Setup tile object
		_planeVertices 		= FloatBuffer.wrap( PLANE_VERTICES, 0, PLANE_VERTICES.length  );
		_planeTextureCoords	= FloatBuffer.wrap( PLANE_TEXTURE_COORDS, 0, PLANE_TEXTURE_COORDS.length );
		
	}
	
	public void setTileset(NetHackTileAtlas atlas) {
		_atlas=atlas;
	}
	
	public void display(int flag) {};
	public void putStr(int attr,String str) {};
	
	public void init(GL10 gl) {}
	
	public void render(GL10 gl) {
		//Log.v(NetHack.LOGTAG,"RENDER()");
		
		// Translate map so its centered on player
		gl.glTranslatef(-_playerX,-_playerY,0);
		
		// Get tile texture coords
		//_atlas.generateTextureCoords(1,_planeTextureCoords);
		
		gl.glPushMatrix();
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glBindTexture(GL10.GL_TEXTURE_2D,_atlas.texture());
		
		// Run thru all entries in map and render tile
		int tile=-1;
		
		lock();	// Lock the map
		Iterator<Entry> it = get().iterator();
		int glyph=0;
		if( it.hasNext() ) {		
			float current_x=0;
			float current_y=0;
			do {
				Entry<NetHackMap.Position,Integer> e = it.next();
				
				glyph = e.getValue();
				
				if( tile != LibNetHack.glyph2tile(glyph) ) {
					tile = LibNetHack.glyph2tile(glyph);
					_atlas.generateTextureCoords(tile,_planeTextureCoords);
				}
				
				// move to map location
				NetHackMap.Position p=e.getKey();
				gl.glTranslatef(p.getX()-current_x,p.getY()-current_y,0);
				
				// Render tile
				gl.glTexCoordPointer(2, gl.GL_FLOAT, 0, _planeTextureCoords);
				gl.glVertexPointer(3, gl.GL_FLOAT, 0, _planeVertices);
				gl.glDrawArrays(gl.GL_TRIANGLE_STRIP, 0, 4 );
				
				current_x=p.getX();
				current_y=p.getY();
		
			} while( it.hasNext() );
		}	
		unlock();	// Unlock the map
		gl.glPopMatrix();
	}
}