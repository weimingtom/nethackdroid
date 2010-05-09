package se.dinamic.nethack;
import  java.lang.InterruptedException;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;
import android.util.Log;
import android.os.Debug;
import android.view.Window;
import android.view.WindowManager;

public class NetHack extends Activity 
{
    private NetHackEngine  _nethack;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN ); 	    
    
        _nethack = new NetHackEngine( this );
        setContentView( _nethack.getView() );
        
        _nethack.start();
    }
    
    @Override
    public void onDestroy( )
    {
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
