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
import android.graphics.drawable.BitmapDrawable;
import android.content.Context;
import android.util.Log;



import java.nio.IntBuffer;
import java.nio.FloatBuffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


import javax.microedition.khronos.opengles.GL10;

public class NetHackTileAtlas {
	
	private final static int TILE_SIZE=32;
	private final static int ATLAS_WIDTH=1280;
	private final static int ATLAS_HEIGHT=960;
	private final static int ATLAS_BYTES_PER_PIXEL=4;
	private final static int ATLAS_PIXELFORMAT = GL10.GL_RGBA;
	
	
	private int _texture[];
	private ByteBuffer _bitmapdata;
	
	/** Create tileset atlas texture from resources */
	public static NetHackTileAtlas createFromResource(Resources resources,  int resid) {
		Log.d(NetHack.LOGTAG,"NetHackTileAtlas.createFromResource() Create atlas from resource.");
		NetHackTileAtlas atlas=new NetHackTileAtlas();
		atlas.loadFromResource(resources,resid);
		return atlas;
	}
	
	private void loadFromResource(Resources resources,int id) {
		Log.d(NetHack.LOGTAG,"NetHackTileAtlas.loadFromResource() Decode and get bitmapdata.");
		
		// Check if we can get texture from cache
		_bitmapdata = Texture.readCache(id);
		
		if( _bitmapdata == null) { // If we didnt get any buffer from cache lets crate from resource
		
			Bitmap bmp = BitmapFactory.decodeResource( resources, id );
			Bitmap realbmp = bmp.copy(Config.ARGB_8888, false);
		
			getBitmapData(realbmp);
			
			// Store cached texture
			Texture.storeCache(id, _bitmapdata);
			bmp.recycle();
			realbmp.recycle();
		}
		
		Log.d(NetHack.LOGTAG,"NetHackTileAtlas.loadFromResource() finished.");
	}
	
	public void generate(GL10 gl) {
		Log.d(NetHack.LOGTAG,"NetHackTileAtlas.generate() Generate GL texture of bitmapdata.");
		// Allocate empty atlas texture
		_texture = new int[1];
		gl.glGenTextures( 1, _texture, 0); 
		
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR); 
		gl.glBindTexture(GL10.GL_TEXTURE_2D, _texture[0] );
		gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, ATLAS_WIDTH, ATLAS_HEIGHT, 0, ATLAS_PIXELFORMAT, GL10.GL_UNSIGNED_BYTE, _bitmapdata);
		Log.d(NetHack.LOGTAG,"NetHackTileAtlas.generate() finished.");
	}
	
	public void generateTextureCoords( int index, FloatBuffer textureCoords ) {
		int rows = (int) ( index / ( ATLAS_WIDTH / TILE_SIZE) );
		int cols = index - ( rows * ( ATLAS_WIDTH / TILE_SIZE) );
		float xoffset = (1.0f / ( ATLAS_WIDTH / TILE_SIZE ) ) * cols;
		float yoffset = (1.0f / ( ATLAS_HEIGHT / TILE_SIZE ) ) * rows;
		float xsize = (1.0f / ( ATLAS_WIDTH / TILE_SIZE ) );
		float ysize = (1.0f / ( ATLAS_HEIGHT / TILE_SIZE ) );
		// Log.d( "TextureAtlas", "generateTextureCoords() index"+index+" rows: "+rows+" cols: "+cols +" "+xoffset+","+yoffset+" - "+(xoffset+xsize)+","+ (yoffset+ysize));
		
		textureCoords.put( 0, xoffset);			textureCoords.put( 1, yoffset + ysize ); 
		textureCoords.put( 2, xoffset + xsize );		textureCoords.put( 3, yoffset + ysize ); 
		textureCoords.put( 4, xoffset); 			textureCoords.put( 5, yoffset ); 
		textureCoords.put( 6, xoffset + xsize ); 	textureCoords.put( 7, yoffset ); 
		
	}
	
	
	
	private void loadFromFile(String filename) {
		
	}
	
	public int texture() {
		return _texture[0];
	}
	
	public void getBitmapData(Bitmap bmp)
	{
		_bitmapdata = ByteBuffer.allocateDirect(bmp.getHeight() * bmp.getWidth() * 4);
		_bitmapdata.order(ByteOrder.BIG_ENDIAN);
		IntBuffer ib = _bitmapdata.asIntBuffer();
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
		_bitmapdata.position(0);
	} 
	
	private NetHackTileAtlas() {
		
	}
}