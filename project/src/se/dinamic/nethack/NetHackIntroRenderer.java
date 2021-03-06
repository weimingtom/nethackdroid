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

import android.app.Application;
import android.content.res.Resources;
import android.util.Log;
import javax.microedition.khronos.opengles.GL10;
import java.lang.Math;

/** Rendering in two phases, first an intro animation, then static showing progressbar.. */
public class NetHackIntroRenderer implements NetHackRenderer {
	
	/** The time of whole intro defined in millis. */
	private final static float INTRO_LENGTH=2000;
	
	/** Distance for shield to travel under the whole intro time */
	private final static float SHIELD_TRANSLATION_DISTANCE_Z=20.0f;
	
	/** The progress value 0-1 of progressbar. */
	private static float _progress = 0.0f;
	
	private Application _application;
	private boolean _isIntroFinished = false;
	private long _elapsedTime=0;
	private Texture _shieldTexture;
	private float _shieldTranslationZ=0.0f;
	
	public NetHackIntroRenderer(Application application) {
		_application=application;
	}
	
	public static void progress(float add) {
		_progress+=add;
	}
	
	public boolean isIntroFinished() {
		return _isIntroFinished; 
	}
	
	public void preInit() {
		Resources res = _application.getApplicationContext().getResources();
		_shieldTexture = Texture.fromResource(res,R.raw.shield);
		_shieldTranslationZ = -SHIELD_TRANSLATION_DISTANCE_Z;
		res = null;
	}
	
	public void init(GL10 gl) {
		_shieldTexture.finalize(gl);
	}
	
	public void clock(long delta) {
		_elapsedTime += delta;
		if( _elapsedTime >= INTRO_LENGTH )	// Intro has finished..
			_isIntroFinished=true;
		
		if( !_isIntroFinished ) {
			// Make calculation on time delta of animations movements
			_shieldTranslationZ -= ( SHIELD_TRANSLATION_DISTANCE_Z / INTRO_LENGTH ) * delta;
		}
		
		// Pulsate the progressbar one waveform for a second of time..
		_rotation+=(java.lang.Math.PI / 1000.0f)*delta;
	}
	
	private static float _rotation=0;
	
	public void render(GL10 gl) {
		gl.glPushMatrix();
		gl.glTranslatef(0,0,-_shieldTranslationZ);
		
		// The shield...
		//gl.glRotatef(_rotation,0,1,0);
		_shieldTexture.render( gl );
		
		
		if( _isIntroFinished ) {
			// Render progressbar
			// Store Original PROJECTION matrix before changing it
			gl.glMatrixMode( gl.GL_PROJECTION );
			gl.glPushMatrix();
				gl.glDisable( gl.GL_LIGHTING );
				gl.glDisable( gl.GL_DEPTH_TEST);
				gl.glMatrixMode( gl.GL_MODELVIEW );
					gl.glLoadIdentity();
					float ratio=NetHackWindowManager.screenWidth/NetHackWindowManager.screenHeight;
					gl.glOrthof(0, 1, 0, 1 / ratio, 0.0f, 10.0f);
				
					// Render progressbar background
					gl.glScalef(1,0.05f,1);
					gl.glTranslatef(0,2f,0);
					NetHackObjects.renderColoredQuad(gl,1.0f,1.0f,0.95f,(float) (0.4f+ (0.075f * Math.sin(_rotation))) );
			
					// Render progress value
					gl.glScalef( _progress*0.9f, 0.8f, 1.0f);
					NetHackObjects.renderColoredQuad(gl,1.0f,1.0f,0.95f,0.9f);
					
				// Restore original PROJECTION matrix
				gl.glMatrixMode( gl.GL_PROJECTION );
			gl.glPopMatrix();
			gl.glMatrixMode( gl.GL_MODELVIEW );
			
		} 
		
		gl.glPopMatrix();
	}
}