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

import android.opengl.GLSurfaceView;
import android.content.Context;

import android.graphics.Typeface;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.util.Vector;

class NetHackView extends GLSurfaceView implements GLSurfaceView.Renderer 
{
    private GL10 _gl;
    private Vector _renderers;
    private Context _context;
    
    public NetHackView(Context context) {
        super(context);
        _context=context;
        _renderers=new Vector<NetHackRenderer>();
        setFocusable( true ); 
        setFocusableInTouchMode( true );
        setRenderer( this );
	    
	//FontAtlasTexture._typeFace = Typeface.createFromAsset(context.getAssets(), "fonts/Isabella.ttf") ;
	FontAtlasTexture._typeFace = Typeface.create(Typeface.SANS_SERIF,Typeface.NORMAL);
    }
    
    
    public void addRenderer(NetHackRenderer r) {
        _renderers.add(r);
    }
    
    public void onDrawFrame( GL10 gl ) {
        // Clear gl buffer
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT |  GL10.GL_STENCIL_BUFFER_BIT);
        
        // Setup viewpoint and camera
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        
        // Run thru all renderers to render the scene
        for(int i=0;i<_renderers.size();i++) {
            NetHackRenderer r=(NetHackRenderer)_renderers.get(i);
            r.render( gl );
        }
    }
    
    public void onSurfaceCreated( GL10 gl, EGLConfig config ) {
	_gl=gl;
	    
	// Pass initialize to all renderers...
	for(int i=0;i<_renderers.size();i++) {
            NetHackRenderer r=(NetHackRenderer)_renderers.get(i);
            r.init( gl );
        }
	
        gl.glDisable( GL10.GL_DITHER );
        gl.glHint( GL10.GL_PERSPECTIVE_CORRECTION_HINT , GL10.GL_NICEST );
        gl.glClearColor( 0.0f, 0.0f, 0.0f, 0.0f);
        gl.glFrontFace(gl.GL_CCW);
        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glCullFace(GL10.GL_BACK);
        gl.glShadeModel(GL10.GL_FLAT);
        
        gl.glBlendFunc(gl.GL_SRC_ALPHA, gl.GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable(GL10.GL_BLEND);
        
        gl.glEnable(GL10.GL_TEXTURE_2D);
        
        // All objects rendered have varr, carr and textcoordarr soo this is put here
        // for performance
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        //gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
	gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

	
    } 
    
    public void onSurfaceChanged( GL10 gl, int width, int height ) {
        // Configure new viewportsize 
        _gl = gl;
        gl.glViewport(0, 0, width, height);
        
	NetHackWindowManager.screenWidth = width;
	NetHackWindowManager.screenHeight = height;
	    
        // Calculate new projection matrix on viewport dimension change
        float ratio = (float) width / height;
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glFrustumf(-ratio, ratio, -1, 1, 1, 40);
    }
}