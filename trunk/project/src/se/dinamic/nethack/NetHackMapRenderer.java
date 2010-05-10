package se.dinamic.nethack;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class NetHackMapRenderer extends NetHackMap implements NetHackRenderer{
	public void render(GL10 gl) {
		gl.glPushMatrix();
		gl.glRotatef(-90.0f,1.0f,0.0f,0.0f);
		// Run thru all entries in map and render tile
		for(int y=0;y<NetHackMap.SIZE;y++) {
			// Translate into y 
			gl.glTranslatef(0,1,0);
			for(int x=0;x<NetHackMap.SIZE;x++) {
				gl.glTranslatef(1,0,0);
			
				int glyph=get(x,y);
				if(  glyph != 0 ) {
					// Render the texturemapped tile
				}
			}
			gl.glTranslatef(-NetHackMap.SIZE,0,0);
		}
		gl.glPopMatrix();
	}
}