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
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;

import javax.microedition.khronos.opengles.GL10;

import java.nio.IntBuffer;
import java.nio.FloatBuffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.io.InputStream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.WritableByteChannel;
import java.nio.channels.ReadableByteChannel;


import android.util.Log;

public class Texture {
	private GL10 _gl;
	private int _texture[];
	
	private float _textureRatio;
	
	
	private static FloatBuffer _planeVertices=null;
	private static FloatBuffer _planeTextureCoords=null;
	
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
	
	private Texture(GL10 gl) {
		_gl = gl;
		_texture = new int[1];
		
		// if 3d plane not is initialized lets do it...
		if( _planeVertices == null ) {
			_planeVertices 		= FloatBuffer.wrap( PLANE_VERTICES, 0, PLANE_VERTICES.length  );
			_planeTextureCoords	= FloatBuffer.wrap( PLANE_TEXTURE_COORDS, 0, PLANE_TEXTURE_COORDS.length );
		}
		
	}
	
	/** renders the texture on a centered 1x1 plane. */
	public void render(GL10 gl) {
		gl.glPushMatrix();
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glBindTexture(GL10.GL_TEXTURE_2D,_texture[0]);
		
		// scale texture to fit texture ratio width always equals 1 unit
		//gl.glScalef(1.0f,_textureRatio,1.0f);
		
		// Render tile textured plane..
		gl.glTexCoordPointer(2, gl.GL_FLOAT, 0, _planeTextureCoords);
		gl.glVertexPointer(3, gl.GL_FLOAT, 0, _planeVertices);
		gl.glDrawArrays(gl.GL_TRIANGLE_STRIP, 0, 4 );
		gl.glPopMatrix();
	}
	
	public static Texture fromFile( GL10 gl, String file ) {
		int gltexture;
		Texture texture = new Texture( gl );
		// open file as inputstream 
		// texture.createFromStream( stream);
		return texture;
	}
	
	/** Load resource texture from cache. */
	public static ByteBuffer readCache(int resid) {
		File f = new File("/sdcard/nethackdata/cache/texture"+resid+".cache");
		ByteBuffer data;
		if( f.exists() ) {
			Log.d(NetHack.LOGTAG,"Texture.readCache() found cached texture "+f.getName());
			try {
				// Cached texture data exists let's load it
				ReadableByteChannel channel = new FileInputStream( f ).getChannel();
				data = ByteBuffer.allocate((int)f.length());
				channel.read(data);
				return data;
			} catch ( FileNotFoundException e ) {
				return null;
			} catch ( IOException e ) {
				return null;
			}
		} 
		return null;
	}
	
	/** Store resource texture from cache. */
	public static boolean storeCache(int resid,ByteBuffer data) {
		File f = new File("/sdcard/nethackdata/cache/texture"+resid+".cache");
		try {
			Log.d(NetHack.LOGTAG,"Texture.storeCache() storing texture "+f.getName());
			WritableByteChannel channel = new FileOutputStream( f ).getChannel();
			channel.write( data );
		} catch ( FileNotFoundException e ) {
			return false;
		} catch ( IOException e ) {
			return false;
		}		
		return true;
	}
	
	public static Texture fromStream( GL10 gl, InputStream stream) {
		int gltexture;
		Texture texture = new Texture( gl );
		texture.createFromStream( stream );
		return texture;
	}
	
	public static Texture fromBitmap( GL10 gl, Bitmap bmp) {
		int gltexture;
		Texture texture = new Texture( gl );
		texture.createFromBitmap( bmp );
		bmp.recycle();
		return texture;
	}
	
	/** Create texture from resource, if the texture exists in cache lets load it from there ..*/
	public static Texture fromResource( GL10 gl, Resources resources, int id ) {
		Log.d(NetHack.LOGTAG,"Texture.fromResource() Loading and creating texture.");
		
		int gltexture;
		Texture texture = new Texture( gl );
		texture.createFromResources( resources, id );
		Log.d(NetHack.LOGTAG,"Texture.fromResource() finished.");
		return texture;
	}
	
	public int texture() {
		return _texture[0];
	}
	
	
	private void createFromStream( InputStream stream ) {
		Bitmap src = BitmapFactory.decodeStream( stream );
		// Ensure bitmap is 4 chan 8bit pic
		Bitmap bmp = Bitmap.createScaledBitmap( src, 128, 128, true );
		Bitmap realbmp = bmp.copy( Config.ARGB_8888, false );
		
		ByteBuffer bb = Texture.argb2rgba(realbmp); 
		createFromRGBAByteBuffer( bb, realbmp.getWidth(), realbmp.getHeight() );
		bmp.recycle();
		realbmp.recycle();
	}
	
	
	private void createFromResources( Resources resources, int id ) {
		int width=0,height=0;
		ByteBuffer bb=readCache(id);
		
		if( bb == null ) {
			Log.d(NetHack.LOGTAG,"Texture.createFromResources() No cached texture data found, decoding resource.");
		
			// Ensure bitmap is 4 chan 8bit pic
			Bitmap bmp = BitmapFactory.decodeResource( resources, id );
			Bitmap realbmp = bmp.copy(Config.ARGB_8888, false);
		
			bb=Texture.argb2rgba(realbmp); 
		
			createFromRGBAByteBuffer( bb, realbmp.getWidth(), realbmp.getHeight() );
			
			storeCache(id, bb );
			
			bmp.recycle();
			realbmp.recycle();
			
		} else  {
			Log.d(NetHack.LOGTAG,"Texture.createFromResources() Reusing cached texture data.");
			BitmapFactory.Options specs=new BitmapFactory.Options();
			Bitmap bm = BitmapFactory.decodeResource( resources, id, specs);
			createFromRGBAByteBuffer( bb, specs.outWidth, specs.outHeight );
			
		}
			
	}
	
	private void createFromBitmap( Bitmap bmp ) {
		// Ensure bitmap is 4 chan 8bit pic
		//Bitmap realbmp = bmp.copy(Config.ARGB_8888, false);
		ByteBuffer bb = Texture.argb2rgba(bmp); 
		createFromRGBAByteBuffer( bb, bmp.getWidth(), bmp.getHeight() );
		//realbmp.recycle();
	}
	
	private void createFromRGBAByteBuffer( ByteBuffer data, int width, int height ) {
		_gl.glGenTextures( 1, _texture, 0); 
		_gl.glBindTexture(GL10.GL_TEXTURE_2D, _texture[0] );
		_gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, width, height, 0, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, data);
		_gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		_gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR); 
		_textureRatio = (width/height);
	}
	
	
	public static ByteBuffer argb2rgba(Bitmap bmp)
	{
		ByteBuffer bb = ByteBuffer.allocateDirect(bmp.getHeight() * bmp.getWidth() * 4);
		bb.order(ByteOrder.BIG_ENDIAN);
		IntBuffer ib = bb.asIntBuffer();
		for (int y = bmp.getHeight() - 1; y > -1; y--)
		{

			for (int x = 0; x < bmp.getWidth(); x++)
			{
				int pix = bmp.getPixel(x, bmp.getHeight() - y - 1);
				int alpha = ((pix >> 24) & 0xFF);
				int red = ((pix >> 16) & 0xFF);
				int green = ((pix >> 8) & 0xFF);
				int blue = ((pix) & 0xFF);

				ib.put(red << 24 | green << 16 | blue << 8 | alpha);
			}
		}
		bb.position(0);
		return bb;
	} 
	
}