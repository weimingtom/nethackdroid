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

import java.nio.FloatBuffer;
import android.util.Log;
import android.app.Application;
import android.content.res.Resources;
import javax.microedition.khronos.opengles.GL10;

/** This renderer takes care about the overlayed UI of the game.
	Like navigation button fram and action buttons...
*/
public class NetHackUserInterfaceRenderer implements NetHackRenderer {
	private static Texture _stoneTexture;
	private static FloatBuffer _vertexColors=null;
	private static FloatBuffer _cornerVertices=null;
	private static FloatBuffer _topLeftCornerTextureCoords=null;
	
	private static float CORNER_VERTICES[] = {
		0,0,0,	
		1,0,0,
		1,1,0,
		-1,1,0,
		-1,-1,0,
		0,-1,0
		
		/*0.0f, 0.0f,  0.0f,	
		0.0f, -1.0f,  0.0f,
		-1.0f,  -1.0f,  0.0f,
		-1.0f,  1.0f,  0.0f,
		1.0f,  1.0f,  0.0f,
		1.0f,  0.0f,  0.0f
		*/	
	};
	
	private static float VERTEX_COLORS[] = {
		1,1,1,1,
		1,1,1,1,
		1,1,1,1,
		1,1,1,1,
		1,1,1,1,
		1,1,1,1
	};
	
	private static float TOP_LEFT_CORNER_TEXTURE_COORDS[] = {
		0.25f,0.75f,
		0.5f,0.75f,
		0.5f,1.0f,
		0.0f,1.0f,
		0.0f,0.5f,
		0.25f,0.5f
		
	};
	
	
	private boolean _isTextureInitialized=false;
	
	public static void initialize(Application application) {
		Resources res = application.getApplicationContext().getResources();
		_stoneTexture = Texture.fromResource(res,R.raw.stone);
		_cornerVertices = FloatBuffer.wrap( CORNER_VERTICES, 0, CORNER_VERTICES.length  );
		_vertexColors = FloatBuffer.wrap( VERTEX_COLORS, 0, VERTEX_COLORS.length  );
		_topLeftCornerTextureCoords = FloatBuffer.wrap( TOP_LEFT_CORNER_TEXTURE_COORDS, 0, TOP_LEFT_CORNER_TEXTURE_COORDS.length );
		res = null;
	}
	
	public void preInit() {}
	
	public void init(GL10 gl) { }
	public void clock(long time) {}
		
	public void render(GL10 gl) {
		if( ! _isTextureInitialized ) {
			Log.d(NetHack.LOGTAG,"NetHackUserInterfaceRenderer.render() Finalizes the internal textures...");
			_stoneTexture.finalize(gl);
			_isTextureInitialized=true;
		}
		/*
		gl.glPushMatrix();
		gl.glDisable( gl.GL_LIGHTING );
		gl.glDisable( gl.GL_DEPTH_TEST);
		gl.glBindTexture(GL10.GL_TEXTURE_2D,_stoneTexture.texture());
		
		gl.glTranslatef(0,0,-4);
		float bs=0.5f;
		gl.glScalef(bs,bs,bs);
	
		// Top left
		gl.glTranslatef(0,0,0);
		gl.glTexCoordPointer(2, gl.GL_FLOAT, 0, _topLeftCornerTextureCoords);
		gl.glVertexPointer(3, gl.GL_FLOAT, 0, _cornerVertices);
		gl.glColorPointer(4, gl.GL_FLOAT, 0, _vertexColors);
		gl.glDrawArrays(gl.GL_TRIANGLE_FAN, 0, 6 );
		
		// Top right
		gl.glTranslatef(2,0,0);
		gl.glRotatef(-90,0,0,1);
		gl.glTexCoordPointer(2, gl.GL_FLOAT, 0, _topLeftCornerTextureCoords);
		gl.glDrawArrays(gl.GL_TRIANGLE_FAN, 0, 6 );
		
		// bottom right
		gl.glTranslatef(0,2,0);
		gl.glRotatef(-90,0,0,1);
		gl.glTexCoordPointer(2, gl.GL_FLOAT, 0, _topLeftCornerTextureCoords);
		gl.glDrawArrays(gl.GL_TRIANGLE_FAN, 0, 6 );
		
		// bottom left
		gl.glTranslatef(-2,0,0);
		gl.glRotatef(-90,0,0,1);
		gl.glTexCoordPointer(2, gl.GL_FLOAT, 0, _topLeftCornerTextureCoords);
		gl.glDrawArrays(gl.GL_TRIANGLE_FAN, 0, 6 );
		
		gl.glPopMatrix();
		*/
	}
	
}