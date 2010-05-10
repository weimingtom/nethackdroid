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
	
	public static NetHackTileAtlas createFromResource(Context context,  int resid) {
		NetHackTileAtlas atlas=new NetHackTileAtlas();
		atlas.loadFromResource(context,resid);
		return atlas;
	}
	
	private void loadFromResource(Context context,int id) {
		Resources resources = context.getResources();
		Bitmap bmp = BitmapFactory.decodeResource( resources, id );
		Bitmap realbmp = bmp.copy(Config.ARGB_8888, false);
		
		getBitmapData(realbmp);
		bmp.recycle();
		realbmp.recycle();
	}
	
	public void generate(GL10 gl) {
		Log.v( "NetHackTileAtlas", "Generating texture");
		// Allocate empty atlas texture
		_texture = new int[1];
		gl.glGenTextures( 1, _texture, 0); 
		Log.v( "NetHackTileAtlas", "_texture="+_texture[0]);
		
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR); 
		gl.glBindTexture(GL10.GL_TEXTURE_2D, _texture[0] );
		gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, ATLAS_WIDTH, ATLAS_HEIGHT, 0, ATLAS_PIXELFORMAT, GL10.GL_UNSIGNED_BYTE, _bitmapdata);
	}
	
	public void generateTextureCoords( int index, FloatBuffer textureCoords ) {
		int rows = (int) ( index / ( ATLAS_WIDTH / TILE_SIZE) );
		int cols = index - ( rows * ( ATLAS_WIDTH / TILE_SIZE) );
		float xoffset = (1.0f / ( ATLAS_WIDTH / TILE_SIZE ) ) * cols;
		float yoffset = (1.0f / ( ATLAS_HEIGHT / TILE_SIZE ) ) * rows;
		float xsize = (1.0f / ( ATLAS_WIDTH / TILE_SIZE ) );
		float ysize = (1.0f / ( ATLAS_HEIGHT / TILE_SIZE ) );
		// Log.v( "TextureAtlas", "generateTextureCoords() index"+index+" rows: "+rows+" cols: "+cols +" "+xoffset+","+yoffset+" - "+(xoffset+xsize)+","+ (yoffset+ysize));
		
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