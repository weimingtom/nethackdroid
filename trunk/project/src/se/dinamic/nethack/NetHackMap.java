package se.dinamic.nethack;

class NetHackMap {
	/** The max size of map... */
	public static final int SIZE=256;
	
	/** This should be replaced with a more sophisticated type than int... like NetHackMapObject */
	private int _map[][];
	
	protected int _playerX,_playerY;
	
	public NetHackMap () {
		_map=new int[SIZE][SIZE];
	}
	
	public int get(int x,int y) {
		return _map[x][y];
	}
	
	public void handleGlyph(int x, int y,int glyph) {
		if( glyph>=319 && glyph<=340 ) { // we got a player glyph
			_playerX=x;
			_playerY=y;
		}
		_map[x][y]=glyph;
	}
}