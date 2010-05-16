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

import android.media.AudioManager;
import android.media.SoundPool;
import android.content.Context;
import java.util.ArrayList;

public class NetHackSound {
	private static SoundPool _soundPoolEffects;
	private static Context _context;
	private static final ArrayList<SoundEffect> _soundEffects=new ArrayList<SoundEffect>();
		
	private static class SoundEffect {
		private String _regex;
		private SoundPool _soundPoolEffects;
		private int _soundid;
		private float _volume;
		
		public SoundEffect(SoundPool sp, Context ctx,int resid,float volume,String match) {
			_soundid = sp.load(ctx,resid,0);
			_regex=match;
			_soundPoolEffects=sp;
			_volume=volume;
		}
		
		public boolean playEffect(String str) {
			if( str.matches( _regex ) ) {
				_soundPoolEffects.play(_soundid,_volume,_volume, 0, 0, 1.0f);
				return true;
			}
			return false;
		}
	}
	
	public NetHackSound(Context context) {
		_context=context;
		_soundPoolEffects = new SoundPool( 15, AudioManager.STREAM_MUSIC, 0);
	
	
	}
	
	public static void initialize() {
			
		_soundEffects.add( new SoundEffect(_soundPoolEffects, _context, R.raw.lock, 1.0f, "This door is locked." ) );
		//_soundEffects.add( new SoundEffect(_soundPoolEffects, _context, R.raw.door1, 1.0f, "The door resists!" ) );
		_soundEffects.add( new SoundEffect(_soundPoolEffects, _context, R.raw.door2, 1.0f, "The door opens." ) );
		_soundEffects.add( new SoundEffect(_soundPoolEffects, _context, R.raw.door2, 1.0f, "You hear a door open." ) );
		//_soundEffects.add( new SoundEffect(_soundPoolEffects, _context, R.raw.door3, 1.0f, "The door closes." ) );
		
		_soundEffects.add( new SoundEffect(_soundPoolEffects, _context, R.raw.crack, 1.0f, ".*it crashes open.*" ) );
		_soundEffects.add( new SoundEffect(_soundPoolEffects, _context, R.raw.crack, 1.0f, "The door crashes open.*" ) );
		_soundEffects.add( new SoundEffect(_soundPoolEffects, _context, R.raw.crack, 1.0f, ".*The door splinters!.*" ) );
		_soundEffects.add( new SoundEffect(_soundPoolEffects, _context, R.raw.crack, 1.0f, ".*door .* shatters*" ) );
		
		_soundEffects.add( new SoundEffect(_soundPoolEffects, _context, R.raw.creak, 1.0f, "You carefully open the .*" ) );
		_soundEffects.add( new SoundEffect(_soundPoolEffects, _context, R.raw.lock, 1.0f, "Hmmm, it seems to be locked." ) );
		_soundEffects.add( new SoundEffect(_soundPoolEffects, _context, R.raw.slams, 1.0f, ".*slams open.*" ) );
		_soundEffects.add( new SoundEffect(_soundPoolEffects, _context, R.raw.crack, 1.0f, ".*slams open.*" ) );
		_soundEffects.add( new SoundEffect(_soundPoolEffects, _context, R.raw.thud, 1.0f, "THUD!" ) );
		
		_soundEffects.add( new SoundEffect(_soundPoolEffects, _context, R.raw.metal, 1.0f, "Klunk!" ) );
		
		_soundEffects.add( new SoundEffect(_soundPoolEffects, _context, R.raw.coins, 1.0f, ".* gold pieces." ) );
		_soundEffects.add( new SoundEffect(_soundPoolEffects, _context, R.raw.coinsfall, 0.6f, "You hear someone counting money." ) );
		_soundEffects.add( new SoundEffect(_soundPoolEffects, _context, R.raw.coins, 0.3f, "Your purse feels lighter."  ) );
		
		_soundEffects.add( new SoundEffect(_soundPoolEffects, _context, R.raw.chain, 1.0f, ".*punished for your misbehavior.*"  ) );
		
		_soundEffects.add( new SoundEffect(_soundPoolEffects, _context, R.raw.hit1, 1.0f, ".*You hit the.*"  ) );
		_soundEffects.add( new SoundEffect(_soundPoolEffects, _context, R.raw.hit1, 1.0f,  ".*You smite the.*" ) );
		_soundEffects.add( new SoundEffect(_soundPoolEffects, _context, R.raw.hit1, 1.0f, ".*You kick the.*" ) );
		_soundEffects.add( new SoundEffect(_soundPoolEffects, _context, R.raw.hit2, 1.0f, ".*You destroy the.*" ) );
		_soundEffects.add( new SoundEffect(_soundPoolEffects, _context, R.raw.hit2, 1.0f, ".*You kill the.*" ) );
		_soundEffects.add( new SoundEffect(_soundPoolEffects, _context, R.raw.scream1, 1.0f, ".*The .* hits.*" ) );
		//_soundEffects.add( new SoundEffect(_soundPoolEffects, _context, R.raw.growl, 1.0f, "*The * bites*" ) );
		_soundEffects.add( new SoundEffect(_soundPoolEffects, _context, R.raw.hit1, 1.0f, ".*bits the.*" ) );
		//_soundEffects.add( new SoundEffect(_soundPoolEffects, _context, R.raw.growl, 1.0f, "*bites the*" ) );
		_soundEffects.add( new SoundEffect(_soundPoolEffects, _context, R.raw.ouch, 1.0f, ".*Ouch!.*" ) );
		_soundEffects.add( new SoundEffect(_soundPoolEffects, _context, R.raw.spark, 1.0f, ".*You get zapped.*" ) );
		_soundEffects.add( new SoundEffect(_soundPoolEffects, _context, R.raw.swing, 1.0f, ".*You miss the.*" ) );
		_soundEffects.add( new SoundEffect(_soundPoolEffects, _context, R.raw.swing, 1.0f, ".*The .* misses.*" ) );
		//_soundEffects.add( new SoundEffect(_soundPoolEffects, _context, R.raw.slop, 1.0f, "*You are splashed by*" ) );
		//_soundEffects.add( new SoundEffect(_soundPoolEffects, _context, R.raw.evil, 1.0f, "*You die..." ) );
	}
	
	public void putStr(int attr,String str) {
		for(int i=0;i<_soundEffects.size();i++) 
			if( _soundEffects.get(i).playEffect(str) ) break;
	};
}