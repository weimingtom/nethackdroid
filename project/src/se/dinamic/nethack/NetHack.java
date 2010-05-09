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
	private NetHackThread  _nethack;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
	
	getWindow().requestFeature(Window.FEATURE_NO_TITLE);
	getWindow().addFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN ); 	    
	
        _nethack=new NetHackThread(this);
	setContentView( _nethack.getEngine().getView());
        
	Log.v("NetHack","Running nethack thread...");
	_nethack.run();
    }
    
	@Override
	public void onDestroy( )
	{
		super.onDestroy( );
	}

	@Override
	protected void onResume( ) {
		super.onResume( );
		_nethack.getEngine().getView().onResume( );
	}

	@Override
	protected void onPause( ) {
		super.onPause( );
		_nethack.getEngine().getView().onPause( );
	}
    
    private class NetHackThread extends Thread {
        private NetHackEngine _engine;
        private Activity _activity;
        public NetHackThread(Activity activity) {
            _activity=activity;
	    _engine = new NetHackEngine(_activity);
	   
        }
        public NetHackEngine getEngine() {
		return _engine;
	}
	
        public void run() {
            if( !_engine.run() ) {
                Toast toast = Toast.makeText(_activity, "Failed to initialize nethack with reason:\n "+_engine.error(), Toast.LENGTH_LONG);
                toast.show();
              //  _activity.finish();
            }
            // Nethack game has exited..
	    
	    // Let's clean up and exit application...
        }
    }
    
}
