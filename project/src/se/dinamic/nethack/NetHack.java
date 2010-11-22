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
import  java.lang.InterruptedException;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;
import android.util.Log;
import android.os.Debug;
import android.view.Window;
import android.view.WindowManager;
import android.content.pm.ActivityInfo;
public class NetHack extends Activity 
{
    public final static String LOGTAG="NetHackDroid";
    private NetHackEngine  _nethack;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
    super.onCreate(savedInstanceState);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN ); 	    
    
        _nethack = new NetHackEngine( this.getApplication() );
        setContentView( _nethack.getView() );
        
    }
    
    @Override
    public void onDestroy( )
    {
        _nethack = null;
        super.onDestroy( );
    }

    @Override
    protected void onResume( ) {
        super.onResume( );
        _nethack.onResume( );
    }

    @Override
    protected void onPause( ) {
        super.onPause( );
        _nethack.onPause( );
    }
}
