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
import android.content.res.Resources;
import java.nio.ShortBuffer;
import java.nio.FloatBuffer;
import javax.microedition.khronos.opengles.GL10;
import java.util.Vector;

public class NetHackMessageWindow implements NetHackWindow {
	
	private static float PLANE_VERTICES[] = {// Image plane
		1.0f,1.0f,  0.0f,	
		0.0f, 0.0f,  0.0f,	
		1.0f, 0.0f,  0.0f,
		0.0f,  1.0f,  0.0f
	};
	
	private static short VERTEX_INDICIES[] = {
		0,1,2,
		0,3,1
	};
	
	private static float VERTEX_COLORS[] = {
		0.0f,0.0f,0.0f,0.6f,
		0.0f,0.0f,0.0f,0.6f,
		0.0f,0.0f,0.0f,0.6f,
		0.0f,0.0f,0.0f,0.6f
	};
	
	
	private static ShortBuffer _planeVerticesIndicies =null;
	private static FloatBuffer _planeVertices=null;
	private static FloatBuffer _planeVertexColors=null;
	
	/** Is window displayed flag */
	private boolean _isDisplayed = false;
	
	/** Flag to determine if  more messages should be shown... */
	private boolean _isExpanded = false;
	
	/** Keeping track of reapeatcount so we don't get messagespam */
	private String _lastStringAdded;
	private int _repeatCount=0;
	

	private static final int LOG_ENTRIES=40;
	private static final int DISPLAY_LOG_ENTRIES=5;
	private final java.util.Vector<FontAtlasTexture.String> _log=new Vector<FontAtlasTexture.String>();
	
	public NetHackMessageWindow() {
		// if 3d plane not is initialized lets do it...
		if( _planeVertices == null ) {
			_planeVertices 		= FloatBuffer.wrap( PLANE_VERTICES, 0, PLANE_VERTICES.length  );
			_planeVerticesIndicies	= ShortBuffer.wrap( VERTEX_INDICIES, 0, VERTEX_INDICIES.length );
			_planeVertexColors	= FloatBuffer.wrap( VERTEX_COLORS, 0, VERTEX_COLORS.length );
		}
	}
	
	public void init(GL10 gl) { }
	public void preInit() { }
	
	public void display(int flag) { _isDisplayed = true; };
	public void destroy() {};
	
	public void putStr(int attr,String str) {
		if( str == _lastStringAdded ) { // Message has already be shown...
			_repeatCount++;
			if(_repeatCount>1) // if we are increasing repatecount lets remove last log entry for new count string...
				_log.remove(_log.lastElement());
			_log.add(  FontAtlasTexture.createString("Last message repated "+_repeatCount+" times.") );
		} else { // New message to view
			_repeatCount=0;
			_log.add( FontAtlasTexture.createString(str) );
			_lastStringAdded = str;
		}
		
		// If log is full remove oldest entry..
		if( _log.size() > LOG_ENTRIES )
			_log.remove( _log.firstElement() );

	};
	
	public void handleGlyph(int x,int y,int glyph) {};
		
	public void render(GL10 gl) {
	
		if( _isDisplayed ) {
			// Render string
			float tscale=0.045f;
		  
			// Store Original PROJECTION matrix before changing it
			gl.glMatrixMode( gl.GL_PROJECTION );
			gl.glPushMatrix();
				gl.glDisable( gl.GL_LIGHTING );
				gl.glDisable( gl.GL_DEPTH_TEST);
					
				gl.glLoadIdentity();
				float ratio = NetHackWindowManager.screenWidth / NetHackWindowManager.screenHeight;
				gl.glOrthof(0, 1, 0, 1.0f/ratio , 0.0f, 10.0f);
					
				gl.glMatrixMode( gl.GL_MODELVIEW );
				gl.glPushMatrix();
					gl.glLoadIdentity();
				
					// Place start of messagewindow at bottom 1= top 
					gl.glTranslatef(0,0,0);
					
					// Render background plane...
					// TODO: Fix issue with colored vertexes...
					gl.glPushMatrix();
					gl.glScalef(1,tscale*DISPLAY_LOG_ENTRIES,1);
					gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
					gl.glVertexPointer(3, GL10.GL_FLOAT, 0, _planeVertices);
					gl.glColorPointer(4, GL10.GL_FLOAT, 0, _planeVertexColors);
					gl.glDrawElements(GL10.GL_TRIANGLES, 6, GL10.GL_UNSIGNED_SHORT, _planeVerticesIndicies);
					gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
					gl.glPopMatrix();
				
					// Scale rendering of text...
					gl.glScalef(tscale,tscale,tscale);
					
					for(int i=0; i < DISPLAY_LOG_ENTRIES; i++) {
						if( _log.size() == 0) break;
						int ioffs = (_log.size()-1) - i;
						if( ioffs>0 ) { // Render log entry
							_log.get(ioffs).render(gl, 0, i, 1.0f-((1.0f/DISPLAY_LOG_ENTRIES)*i));
						} else // No more entries to show let's breakout
							break;
					}
				
				gl.glPopMatrix();
			
				// Restore original PROJECTION matrix
				gl.glMatrixMode( gl.GL_PROJECTION );
			gl.glPopMatrix();
			gl.glMatrixMode( gl.GL_MODELVIEW );
		}
	}
}