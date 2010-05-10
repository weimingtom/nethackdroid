package se.dinamic.nethack;

import java.nio.ShortBuffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class NetHackMapRenderer extends NetHackMap implements NetHackRenderer{
	
	private float _tile_v[] = {
	      -0.5f,  0.5f, 0.0f,  // 0, Top Left
	      -0.5f, -0.5f, 0.0f,  // 1, Bottom Left
	       0.5f, -0.5f, 0.0f,  // 2, Bottom Right
	       0.5f,  0.5f, 0.0f,  // 3, Top Right
	};
	
	private short[] _tile_i = { 0, 1, 2, 0, 2, 3 };
	
	public FloatBuffer _tile_varr;
	public ShortBuffer _tile_iarr;
    
	public NetHackMapRenderer() {
		// Setup tile object
		ByteBuffer vbb = ByteBuffer.allocateDirect(_tile_v.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		_tile_varr = vbb.asFloatBuffer();
		_tile_varr.put(_tile_v);
		_tile_varr.position(0);
        
		ByteBuffer ibb = ByteBuffer.allocateDirect(_tile_i.length * 2);
		ibb.order(ByteOrder.nativeOrder());
		_tile_iarr = ibb.asShortBuffer();
		_tile_iarr.put(_tile_i);
		_tile_iarr.position(0);
        
	}
	
	public void render(GL10 gl) {
		
		// Translate map so its centered on player
		gl.glTranslatef(-_playerX,-_playerY,0);
		
		gl.glPushMatrix();
		// Run thru all entries in map and render tile
		for(int y=0;y<NetHackMap.SIZE;y++) {
			// Translate into y 
			gl.glTranslatef(0,1,0);
			for(int x=0;x<NetHackMap.SIZE;x++) {
				gl.glTranslatef(1,0,0);
			
				int glyph=get(x,y);
				if(  glyph != 0 ) {
					// Render the texturemapped tile
					gl.glVertexPointer(3, gl.GL_FLOAT, 0, _tile_varr);
					gl.glDrawElements(GL10.GL_TRIANGLES, _tile_i.length, GL10.GL_UNSIGNED_SHORT, _tile_iarr);
				}
			}
			gl.glTranslatef(-NetHackMap.SIZE,0,0);
		}
		gl.glPopMatrix();
	}
}