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

import javax.microedition.khronos.opengles.GL10;


/** Rendering in two phases, first an intro animation, then static showing progressbar.. */
public class NetHackIntroRenderer implements NetHackRenderer {
	private Resources _resources;
	private boolean _isIntroFinished = false;
	private static float _progress = 0.0f;
	
	private Texture _shieldTexture;
	
	public NetHackIntroRenderer(Resources resources) {
		_resources=resources;
	}
	
	public static void progress(float add) {
		_progress+=add;
	}
	
	public void init(GL10 gl) {
		_shieldTexture = Texture.fromResource(gl,_resources,R.drawable.shield);
	}
	
	public void render(GL10 gl) {
		gl.glPushMatrix();
		gl.glTranslatef(0,0,-1);
		
		// The shield...
		_shieldTexture.render( gl );
		
		if( _isIntroFinished ) {
			// Render progressbar
		}
		
		gl.glPopMatrix();
	}
}