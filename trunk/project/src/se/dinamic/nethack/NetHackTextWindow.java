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
import android.content.res.Resources;
import java.nio.FloatBuffer;
import javax.microedition.khronos.opengles.GL10;

public class NetHackTextWindow implements NetHackWindow {
	private boolean _isDisplayed=false;
	
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
	
	private FloatBuffer _planeVertices;
	private FloatBuffer _planeTextureCoords;
	
	private  static Texture _paperBackground;
	private  static Resources _resources;
	
	public static void initialize(GL10 gl,Resources resources) {
		_resources = resources;
		_paperBackground = Texture.fromResource(gl,resources,R.drawable.paper);
	}
	
	
	public NetHackTextWindow () {
		_planeVertices 		= FloatBuffer.wrap( PLANE_VERTICES, 0, PLANE_VERTICES.length  );
		_planeTextureCoords	= FloatBuffer.wrap( PLANE_TEXTURE_COORDS, 0, PLANE_TEXTURE_COORDS.length );
	}
	
	public void display(int flag) { _isDisplayed = true; };
	public void destroy() {};
	public void putStr(int attr,String str) {};
	public void handleGlyph(int x,int y,int glyph) {};
	
	public void init(GL10 gl) { }
	
	public void render(GL10 gl) {
		if( _isDisplayed ) {
			gl.glPushMatrix();
				gl.glBindTexture(GL10.GL_TEXTURE_2D,_paperBackground.texture());
				gl.glTexCoordPointer(2, gl.GL_FLOAT, 0, _planeTextureCoords);
				gl.glVertexPointer(3, gl.GL_FLOAT, 0, _planeVertices);
				gl.glDrawArrays(gl.GL_TRIANGLE_STRIP, 0, 4 );
			gl.glPopMatrix();
		}
	}
}