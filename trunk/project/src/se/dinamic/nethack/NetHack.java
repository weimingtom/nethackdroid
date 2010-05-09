package se.dinamic.nethack;
import  java.lang.InterruptedException;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;
import 	android.os.Debug;
public class NetHack extends Activity 
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // Start nethack thread and run
        NetHackThread nethack=new NetHackThread(this);
        nethack.run();
    }
    
    
    private class NetHackThread extends Thread {
        private NetHackEngine _engine;
        private Activity _activity;
        public NetHackThread(Activity activity) {
            _activity=activity;
        }
        
        public void run() {
            _engine=new NetHackEngine(_activity);
            if( !_engine.run() ) {
                Toast toast = Toast.makeText(_activity, "Failed to initialize nethack with reason:\n "+_engine.error(), Toast.LENGTH_LONG);
                toast.show();
              //  _activity.finish();
            }
            // Nethack game has exited..
        }
    }
    
}
