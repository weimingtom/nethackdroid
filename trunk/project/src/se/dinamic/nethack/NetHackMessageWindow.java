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
import java.nio.FloatBuffer;
import javax.microedition.khronos.opengles.GL10;
import java.util.Vector;

public class NetHackMessageWindow implements NetHackWindow {
    private boolean _isDisplayed = false;
    private static final int LOG_ENTRIES=40;
    private static final int DISPLAY_LOG_ENTRIES=6;
    private final java.util.Vector<FontAtlasTexture.String> _log=new Vector<FontAtlasTexture.String>();
    
    public NetHackMessageWindow() {
    }
    
    public void init(GL10 gl) { }
    
    public void display(int flag) { _isDisplayed = true; };
    public void destroy() {};
    
    public void putStr(int attr,String str) {
        _log.add( FontAtlasTexture.createString(str) );
        if( _log.size() > LOG_ENTRIES )
            _log.remove( _log.firstElement() );
    };
    
    public void handleGlyph(int x,int y,int glyph) {};
        
    public void render(GL10 gl) {
        if( _isDisplayed ) {
            // Render string
            float tscale=0.045f;
            //float tscale=0.055f;
            // Store Original PROJECTION matrix before changing it
            gl.glMatrixMode( gl.GL_PROJECTION );
            gl.glPushMatrix();
                gl.glDisable( gl.GL_LIGHTING );
                gl.glDisable( gl.GL_DEPTH_TEST);
            
                gl.glLoadIdentity();
                // gl.glOrthof(0.0f,parent.getWidth(),0.0f,-parent.getHeight(),0.0f,10.0f);
                float ratio=NetHackWindowManager.screenWidth/NetHackWindowManager.screenHeight;
		if(ratio > 1.0 ) // Landscape
			gl.glOrthof(0, 1, 0, 1 / ratio, 0.0f, 10.0f);
		else // Portrait
			gl.glOrthof(0, 1 / ratio, 0, 1, 0.0f, 10.0f);
            
                gl.glMatrixMode( gl.GL_MODELVIEW );
                gl.glPushMatrix();
                    gl.glLoadIdentity();
                    gl.glTranslatef(0,1,0);
                
                    gl.glScalef((tscale/2.0f),tscale,tscale);
                    // Go down one row
                    gl.glTranslatef(0,-1,0);
                    for(int i=0; i < DISPLAY_LOG_ENTRIES; i++) {
                        
                        if( i>=_log.size()) 
                            break;
                        
                        int ioffs=i;
                        if( _log.size() > DISPLAY_LOG_ENTRIES ) 
                            ioffs = _log.size()-DISPLAY_LOG_ENTRIES + i;
                        _log.get(ioffs).render(gl);
                        gl.glTranslatef(0,-1,0);
                    }
                
                gl.glPopMatrix();
            
                // Restore original PROJECTION matrix
                gl.glMatrixMode( gl.GL_PROJECTION );
            gl.glPopMatrix();
            gl.glMatrixMode( gl.GL_MODELVIEW );
        }
    }
}