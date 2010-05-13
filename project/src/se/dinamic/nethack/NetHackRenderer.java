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
import javax.microedition.khronos.opengles.GL10;

public interface NetHackRenderer {
	/** put timeconsuming stuff into here, such as texture generation etc..*/
	public void preInit();
	/** this should be the last touch of a init, non time consuming stuff that needs an GL instance.. */
	public void init(GL10 gl);
	/** the actual rendering of content...*/
	public void render(GL10 gl);
}