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

import javax.microedition.khronos.opengles.GL10;
import java.nio.FloatBuffer;
public class NetHackObjects {
	
	private static float PLANE_VERTICES[] = {
		0.0f, 0.0f,  0.0f,	// Image plane
		1.0f, 0.0f,  0.0f,
		0.0f,  1.0f,  0.0f,
		1.0f,  1.0f,  0.0f
	};
	
	private static float PLANE_TEXTURE_COORDS[] = {
		0.0f, 1.0f,
		1.0f, 1.0f,
		0.0f, 0.0f,
		1.0f, 0.0f
	};
	
	private static FloatBuffer _planeVertices;
	private static FloatBuffer _planeTextureCoords;
	
	public static void initialize() {
		_planeVertices 		= FloatBuffer.wrap( PLANE_VERTICES, 0, PLANE_VERTICES.length  );
		_planeTextureCoords	= FloatBuffer.wrap( PLANE_TEXTURE_COORDS, 0, PLANE_TEXTURE_COORDS.length );
	}
	
	public static void renderColoredQuad(GL10 gl,float red,float green,float blue, float alpha) {
		// Disable texture and color array
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		gl.glColor4f(red,green,blue,alpha);
		gl.glVertexPointer(3, gl.GL_FLOAT, 0, _planeVertices);
		gl.glDrawArrays(gl.GL_TRIANGLE_STRIP, 0, 4 );
		
		// go back to default enabled...
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}
	
}