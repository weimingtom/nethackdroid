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
import java.util.concurrent.locks.ReentrantLock;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import javax.microedition.khronos.opengles.GL10;

public class NetHackStatusWindow implements NetHackWindow {
	
	public static class PlayerStatus {
		public String rank;
		public int strength;
		public int dexterity;
		public int consitution;
		public int intelligence;
		public int wisdom;
		public int charisma;
		public String alignment;
		public String dungeonLevel;
		public int gold;
		public int totalHitPoints;
		public int currentHitPoints;
		public int totalPower;
		public int currentPower;
		public int armorclass;
		public int experience;
		public int time;
		public String hungerStatus;
	};
	
	private boolean _isDisplayed=false;
	private boolean _isShowingFullStats;
	private boolean _isDataChanged;
	private boolean _isDataInitialized=false;
	
	
	/** The player status object, holds all stats for the current player.. */
	private final PlayerStatus _player = new PlayerStatus();
	
	/** Lock for access to the player data object... */
	private final ReentrantLock _dataLock = new ReentrantLock();
	
	private final LinkedHashMap<String, FontAtlasTexture.String> _strings = new LinkedHashMap<String, FontAtlasTexture.String>();
	
	
	public NetHackStatusWindow() {
		_strings.put( "HITPOINTS",FontAtlasTexture.createString("HP") );
		_strings.put( "POWER",FontAtlasTexture.createString("Pow") );
		_strings.put( "GOLD",FontAtlasTexture.createString("Gold") );
		_strings.put( "EXP",FontAtlasTexture.createString("Exp.") );
	}
	
	public void display(int flag) { _isDisplayed = true; };
	public void destroy() { };
	public void handleGlyph(int x, int y,int glyph) {}
	
		
	/** Parse the string into the player data object... */
		// Goliat the Footpad      St:12 Dx:18 Co:15 In:10 Wi:12 Ch:10  Chaotic
		// Dlvl:1  $:0  HP:11(11) Pw:2(2) AC:7  Exp:1
	public void putStr(int attr,String str) {
		
		Pattern pattern = Pattern.compile("Dlvl:(.+)\\s+[\\$]:(\\d+)\\s+HP:(\\d+)\\((\\d+)\\)\\s+Pw:(\\d+)\\((\\d+)\\)\\s+AC:(\\d+)\\s+Exp:(\\d+)");
		Matcher matcher=pattern.matcher( str );
		if( matcher.matches() ) {
			_dataLock.lock();
			_isDataInitialized=true;
			_player.dungeonLevel = matcher.group(1).trim();	
			_player.gold = Integer.valueOf( matcher.group(2));	
			_player.currentHitPoints = Integer.valueOf( matcher.group(3));	
			_player.totalHitPoints = Integer.valueOf( matcher.group(4));	
			_player.currentPower = Integer.valueOf( matcher.group(5));	
			_player.totalPower = Integer.valueOf( matcher.group(6));	
			_player.armorclass = Integer.valueOf( matcher.group(7));	
			_player.experience = Integer.valueOf( matcher.group(8));	
			_isDataChanged = true;
			_dataLock.unlock();
		} else {
			pattern = Pattern.compile("(.+)\\s+St:(\\d+)\\s+Dx:(\\d+)\\sCo:(\\d+)\\s+In:(\\d+)\\s+Wi:(\\d+)\\s+Ch:(\\d+)\\s+(.+)");
			matcher = pattern.matcher( str );
			if( matcher.matches() ) {
				_dataLock.lock();
				_player.rank=matcher.group(1).trim();
				_isDataChanged = true;
				_dataLock.unlock();
			}
		}
	};
	
	public void init(GL10 gl) { }
	public void preInit() {}
	
	public FontAtlasTexture.String _rankAndLevel;
		
	public void render(GL10 gl) {
		_dataLock.lock();
		if( _isDataChanged ) {
			// Update strings that is to be rendered...
			_rankAndLevel = FontAtlasTexture.createString(_player.rank+" in dungeon level "+_player.dungeonLevel );
			_isDataChanged=false;
		}
		_dataLock.unlock();
		
			
		if(   _isDataInitialized && _isDisplayed ) {
				
			// Render status...
			float tscale=0.055f;
		  
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
				gl.glLoadIdentity();
				
				// Display name and rank top left
				gl.glPushMatrix();
					gl.glTranslatef(0,1,0);
					// Scale rendering of text...
					gl.glScalef((tscale/2.0f),tscale,tscale);
					gl.glTranslatef(0,-1,0);
					if(_rankAndLevel!=null)
						_rankAndLevel.render(gl,0.8f);
				gl.glPopMatrix();
				
					
				// Display health/power/exp/gold right
				tscale=0.035f;
				gl.glPushMatrix();
					gl.glTranslatef(0.75f,1,0);
					
					// First render hpscale then add texts..
					gl.glPushMatrix();
						gl.glScalef(0.25f,tscale,tscale);
						gl.glTranslatef(0,-1,0); 
						gl.glScalef(1,0.8f,1);
						NetHackObjects.renderColoredQuad(gl,0.3f,0.0f,0.0f,0.4f);
						gl.glScalef(_player.currentHitPoints/_player.totalHitPoints,0.9f,1);
						NetHackObjects.renderColoredQuad(gl,0.8f,0.0f,0.0f,0.3f);
					gl.glPopMatrix();
					
					gl.glPushMatrix();
						gl.glScalef(0.25f,tscale,tscale);
						gl.glTranslatef(0,-2,0); 
						gl.glScalef(1,0.8f,1);
						NetHackObjects.renderColoredQuad(gl,0.0f,0.0f,0.3f,0.4f);
						gl.glScalef(_player.currentPower/_player.totalPower,0.9f,1);
						NetHackObjects.renderColoredQuad(gl,0.0f,0.0f,0.8f,0.3f);
					gl.glPopMatrix();
					
					gl.glScalef((tscale/2.0f),tscale,tscale);
					// HP and its bar
					gl.glTranslatef(0,-1,0); _strings.get("HITPOINTS").render(gl,1.0f);
					
					// Power and bar
					gl.glTranslatef(0,-1,0); _strings.get("POWER").render(gl,1.0f);
					
				gl.glPopMatrix();
					
				// Display other stats
				if( _isShowingFullStats ) {
					
				}
			
				// Restore original PROJECTION matrix
				gl.glMatrixMode( gl.GL_PROJECTION );
			gl.glPopMatrix();
			gl.glMatrixMode( gl.GL_MODELVIEW );
			
			
		}
	}
}