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

import android.util.Log;
import android.graphics.Typeface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Bitmap.CompressFormat;
import java.util.LinkedHashMap;
import java.nio.FloatBuffer;
import java.nio.ByteBuffer;
import java.io.FileOutputStream;
import java.lang.Math;
import javax.microedition.khronos.opengles.GL10;
import java.io.File;
import java.io.FileNotFoundException;

public class FontAtlasTexture {
	private static final java.lang.String CHARACTERS="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 ,.!?'-";
	private static final int ATLAS_SIZE = 512;
	private static final int ATLAS_MARGIN = 8;
	
	private static int _texture[];
	/** The max height of a character in atlas */
	private static float _characterHeight;
	private static float _characterWidth;
	private static ByteBuffer _data;
	public static Typeface _typeFace;
	
	/** This is a rendable string. 
		
	*/
	public static class String {
		/** The width of string, in ratio to height which always equals 1 unit. */
		private float _width;
		
		/** FontAtlas texture to bind when rendering this string */
		private int _texture;
		
		/** Count of characters in string */
		private int _stringLength;
		/** Vertext buffer for characters */
		private FloatBuffer _vertices;
		/** Texture coordinate buffer for characters */
		private FloatBuffer _textCoords;
		/** vertex color buffer for characters */
		private FloatBuffer _vertexColors;
		
		/** The alphavalue of string rendering. */
		private float _alpha=1.0f;
		
		private String(int texture,float width,int length, FloatBuffer verticies, FloatBuffer tcords,FloatBuffer vcolors) {
			_texture=texture;
			_width=width;
			_vertices = verticies;
			_textCoords = tcords;
			_vertexColors = vcolors;
			_stringLength = length;
		}
		
		/** Get width of string. */
		public float getWidth() { return _width; };
		
		/** Render string on GL */
		public void render( GL10 gl, float alpha ) {
			gl.glPushMatrix();
			gl.glEnable( GL10.GL_TEXTURE_2D );
			gl.glBindTexture( GL10.GL_TEXTURE_2D, _texture );
			
			// setup vertext colorarray with alpha
			if( alpha != _alpha ) {
				int entries=_vertexColors.capacity();
				for(int e=3;e<entries;e+=4) {
					_vertexColors.put(e,alpha);
				}
				_alpha=alpha;
			}
			
			// lets render string...
			gl.glTexCoordPointer( 2, gl.GL_FLOAT, 0, _textCoords );
			gl.glVertexPointer( 3, gl.GL_FLOAT, 0, _vertices );
			gl.glColorPointer(4, gl.GL_FLOAT, 0, _vertexColors);
			gl.glDrawArrays( gl.GL_TRIANGLES, 0, _stringLength * 6 );
		
			gl.glPopMatrix( );
		}
		
		
	}
	
	private static class AtlasCharacter {
		public float x,y,width,height;
	}
	
	private final static LinkedHashMap<Integer, AtlasCharacter> _map=new LinkedHashMap<Integer, AtlasCharacter>();
	
	public FontAtlasTexture() {
	}
	
	public static void finalize( GL10 gl) {
		_texture = new int[1];
		gl.glGenTextures( 1, _texture, 0); 
		gl.glBindTexture(GL10.GL_TEXTURE_2D, _texture[0] );
		gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, ATLAS_SIZE, ATLAS_SIZE, 0, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, _data);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR); 
	}
	
	public static void initialize() {
		Log.d(NetHack.LOGTAG,"FontAtlasTexture.intialize()  generating font atlas.");
		_data=null;
		boolean _gotCachedBitmap=false;
		
		// Lets check if we have a cached fontmap texture of font/size
		File f = new File("/sdcard/nethackdata/cache/fontmap.cache");
		_data = Texture.readCache(f);
		if( _data != null ) _gotCachedBitmap=true;
		
		// Generate the character atlas
		Bitmap bm=null;
		Canvas bmc=null;
		if( !_gotCachedBitmap) {
			bm = Bitmap.createBitmap(ATLAS_SIZE, ATLAS_SIZE, Bitmap.Config.ARGB_8888);
			bmc = new Canvas(bm);
		}
		
		Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
		p.setTypeface(_typeFace);
		p.setTextSize(58);
		
		// Get the font metrics
		Paint.FontMetrics fm = p.getFontMetrics();
		_characterHeight=Math.abs(fm.ascent)+Math.abs(fm.descent)+Math.abs(fm.leading);
		Log.d(NetHack.LOGTAG,"FontAtlasTexture.intialize()  fontmetric height "+_characterHeight);
		
		p.setARGB(0xff, 0xff,0xff,0xff);
		
		// Let's render all characters in CHARACTERS into the canvas
		float x=ATLAS_MARGIN,y=ATLAS_MARGIN+_characterHeight;
		for(int i=0;i<CHARACTERS.length();i++) {
			AtlasCharacter ac=new AtlasCharacter();
			ac.width = p.measureText(CHARACTERS,i,i+1);	
			if( x+ac.width+ATLAS_MARGIN > ATLAS_SIZE ) { 
				// Character does not fit in the current row
				// let increase y
				y+=_characterHeight+ATLAS_MARGIN;
				x=ATLAS_MARGIN;
			}
			
			// Draw character into bitmap
			if( ! _gotCachedBitmap )
				bmc.drawText(CHARACTERS,i ,i+1,x ,y ,p );
			
			// Set AtlasCharacter values and store it with char key.
			ac.x=x/ATLAS_SIZE;
			ac.y=(y-_characterHeight+ATLAS_MARGIN)/ATLAS_SIZE;
			
			x+=ac.width;
			x+=ATLAS_MARGIN;
			
			ac.width=ac.width/ATLAS_SIZE;
			ac.height=(_characterHeight+ATLAS_MARGIN)/ATLAS_SIZE;
			
			_map.put((int)CHARACTERS.charAt(i), ac);
			
			
			if(_characterWidth<ac.width)
				_characterWidth=ac.width;
		}
		
		// Let's convert bitmap argb to rgba bytebuffer
		if( !_gotCachedBitmap )
			_data = Texture.argb2rgba(bm);
		
		
		
		// Debug save it to file..
		if( ! _gotCachedBitmap ) {
			try {
				FileOutputStream stream = new FileOutputStream("/sdcard/fontatlas.png"); 
				bm.compress(CompressFormat.PNG, 100, stream); 
				stream.flush(); 
				stream.close(); 
				
				 Texture.storeCache( f, _data );
			} catch (java.io.FileNotFoundException e) {
			} catch (java.io.IOException e) {
			}
		
			bm.recycle();
		}
		Log.d(NetHack.LOGTAG,"FontAtlasTexture.intialize()  "+ _map.size() +" characters generated in atlas.");
			
	}
	
	/** Creates a renderable string of the current fontatlas */
	public static FontAtlasTexture.String createString(java.lang.String text) {
		
		
		// Build u vertex and textcoord arrays to render,
		FloatBuffer varr = FloatBuffer.allocate((text.length()*9)*3);
		FloatBuffer tcarr = FloatBuffer.allocate((text.length()*9)*2);
		FloatBuffer vcarr = FloatBuffer.allocate((text.length()*9)*4);
		float x=0;
		float y=0;
		int ti=0;
		int vi=0;
		int ci=0;
		float width=0;
		
		for(int i=0;i<text.length();i++) {
			if( _map.containsKey((int)text.charAt(i)) ) 
			{
				AtlasCharacter ac =_map.get((int)text.charAt(i));
				width+=ac.width;
			
				float cw = 1.0f * (ac.width / _characterWidth);
				varr.put(vi,x);  varr.put(vi+1,1.0f);  varr.put(vi+2,0.0f);
				varr.put(vi+3,x);  varr.put(vi+4,0.0f);  varr.put(vi+5,0.0f);
				varr.put(vi+6,x+cw);  varr.put(vi+7,0.0f);  varr.put(vi+8,0.0f);
				
				varr.put(vi+9,x+cw);  varr.put(vi+10,0.0f);  varr.put(vi+11,0.0f);
				varr.put(vi+12,x+cw);  varr.put(vi+13,1.0f);  varr.put(vi+14,0.0f);
				varr.put(vi+15,x);  varr.put(vi+16,1.0f);  varr.put(vi+17,0.0f);
				vi+=18;
				
				tcarr.put( ti+0 , ac.x );  tcarr.put( ti+1 , ac.y );  
				tcarr.put( ti+2 , ac.x );  tcarr.put( ti+3 , ac.y+ac.height ); 
				tcarr.put( ti+4 , ac.x+ac.width );  tcarr.put( ti+5 , ac.y+ac.height ); 
				
				tcarr.put( ti+6   , ac.x+ac.width );  tcarr.put( ti+7   , ac.y+ac.height ); 
				tcarr.put( ti+8   , ac.x+ac.width );  tcarr.put( ti+9   , ac.y ); 
				tcarr.put( ti+10 , ac.x );  tcarr.put( ti+11 , ac.y ); 
				
				// Setup initial color array
				for(int v=0;v<6;v++) {
					for(int c=0;c<4;c++) 
						vcarr.put(ci+c,1.0f);
					ci+=4;
				}
				
				ti+=12;
				
				x+=cw;
			}
		}
		return new FontAtlasTexture.String( _texture[0], width, text.length(), varr, tcarr ,vcarr);
	}
	
}