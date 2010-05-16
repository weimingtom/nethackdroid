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

import java.util.Set;
import java.util.Map.Entry;
import java.util.Iterator;

import java.nio.ShortBuffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.util.Log;
import android.content.res.Resources;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class NetHackMapWindow extends NetHackMap implements NetHackWindow {
	private static NetHackTileAtlas _atlas;
	private boolean _isDisplayed=false;
	private static boolean _isTextureInitialized=false;
	private float _zoom=-5.0f;
	
	private static float PLANE_VERTICES[] = {
		-0.5f, -0.5f,  0.0f,	// Image plane
		 0.5f, -0.5f,  0.0f,
		-0.5f,  0.5f,  0.0f,
		 0.5f,  0.5f,  0.0f
	};
	
	private static float VERTEX_COLORS[] = {
		1,1,1,1,
		1,1,1,1,
		1,1,1,1,
		1,1,1,1
	};
	
	private float PLANE_TEXTURE_COORDS[] = {
		0.0f, 1.0f,
		1.0f, 1.0f,
		0.0f, 0.0f,
		1.0f, 0.0f
	};
	
	public FloatBuffer _planeVertices;
	public FloatBuffer _planeTextureCoords;
	public FloatBuffer _planeVertexColors;
	
	public NetHackMapWindow() {
		// Setup tile object
		_planeVertices 		= FloatBuffer.wrap( PLANE_VERTICES, 0, PLANE_VERTICES.length  );
		_planeTextureCoords	= FloatBuffer.wrap( PLANE_TEXTURE_COORDS, 0, PLANE_TEXTURE_COORDS.length );
		_planeVertexColors	= FloatBuffer.wrap( VERTEX_COLORS, 0, VERTEX_COLORS.length );
	}
	
	public static void initialize(Resources resources) {
		_atlas = NetHackTileAtlas.createFromResource(resources,R.drawable.absurd32);
		
	}
	
	public void zoomIn() { _zoom+=1.0f; }
	public void zoomOut() { _zoom+=-1.0f; }
	
	public void display(int flag) { _isDisplayed = true; };
	public void destroy() { };
	public void handleGlyph(int x, int y,int glyph) { super.handleGlyph(x,y,glyph); }
	public void putStr(int attr,String str) {};
	
	public void init(GL10 gl) {}
	public void preInit() {}
	
	public void render(GL10 gl) {
		if( ! _isTextureInitialized ) {
			_atlas.generate(gl);
			_isTextureInitialized = true;
		}
		
		if( _isDisplayed ) {
			
			gl.glPushMatrix();
			// Move camera back 8 units..
			gl.glTranslatef(0,0,_zoom);

			//gl.glRotatef(-25,1,0,0);
			
			// Translate map so its centered on know player position
			gl.glTranslatef(-_playerX,-_playerY,0);

			// Ensure texture is enabled and bound
			gl.glEnable(GL10.GL_TEXTURE_2D);
			gl.glBindTexture(GL10.GL_TEXTURE_2D,_atlas.texture());
			
			// Run thru all entries in map and render tile
			int tile=-1;
			
			// Lock the map, prevents updates change
			lock();	

			// Let's get all map items and render them
			Iterator<Entry> it = get().iterator();
			int glyph=0;
			if( it.hasNext() ) {		
			
				do {
					Entry<NetHackMap.Position,Integer> e = it.next();
					
					// Get map glyph and convert it to tile
					glyph = e.getValue();
					if( tile != LibNetHack.glyph2tile(glyph) ) {
						tile = LibNetHack.glyph2tile(glyph);
						// New tile let's get texture coordinates from tileset atlas
						_atlas.generateTextureCoords(tile,_planeTextureCoords);
					}
					
					// Use the tracked translation to calculate a translation offset
					// to this map object..
					
					NetHackMap.Position p=e.getKey();
					
					gl.glPushMatrix();
			
						gl.glTranslatef(p.getX(),p.getY(),0);
						
						// Render tile textured plane..
						gl.glTexCoordPointer(2, gl.GL_FLOAT, 0, _planeTextureCoords);
						gl.glVertexPointer(3, gl.GL_FLOAT, 0, _planeVertices);
						gl.glColorPointer(4, gl.GL_FLOAT, 0, _planeVertexColors);
						gl.glDrawArrays(gl.GL_TRIANGLE_STRIP, 0, 4 );
						
					gl.glPopMatrix();
				
				} while( it.hasNext() );
			}	
			
			// Unlock the map
			unlock();
			
			gl.glPopMatrix();
		}
	}
}