package se.dinamic.nethack;


import android.opengl.GLSurfaceView;
import android.content.Context;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

class NetHackView extends GLSurfaceView implements GLSurfaceView.Renderer 
{
    private GL10 _gl;
    
    public NetHackView(Context context) {
        super(context);
        setRenderer( this );
    }
    
    public void onDrawFrame( GL10 gl ) {
        // Clear gl buffer
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT |  GL10.GL_STENCIL_BUFFER_BIT);
		
		// Setup camera/viewpoint
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glTranslatef(0,0,-4);
        
    }
    
    public void onSurfaceCreated( GL10 gl, EGLConfig config ) {
		gl.glDisable( GL10.GL_DITHER );
		gl.glHint( GL10.GL_PERSPECTIVE_CORRECTION_HINT , GL10.GL_NICEST );
		gl.glClearColor( 0.0f, 0.0f, 0.0f, 0.0f);
		gl.glFrontFace(gl.GL_CCW);
		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glShadeModel(GL10.GL_FLAT);
		
        gl.glBlendFunc(gl.GL_SRC_ALPHA, gl.GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable(GL10.GL_BLEND);
		
		gl.glEnable(GL10.GL_TEXTURE_2D);
		
		// All objects rendered have varr, carr and textcoordarr soo this is put here
		// for performance
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

	} 
    
    public void onSurfaceChanged( GL10 gl, int width, int height ) {
		// Configure new viewportsize 
		_gl = gl;
		gl.glViewport(0, 0, width, height);
		
		// Calculate new projection matrix on viewport dimension change
		float ratio = (float) width / height;
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glFrustumf(-ratio, ratio, -1, 1, 1, 40);
	}
}