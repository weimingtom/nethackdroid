package se.dinamic.nethack;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class NetHackEngine extends LibNetHack 
{
    private Activity _context;
    public NetHackEngine(Activity context) 
   {
	_context=context;
   }	   
      
    /** LibNetHack JNI callback */
    @Override
    public void raw_print(String status) 
    {
	Toast toast = Toast.makeText(_context, status, Toast.LENGTH_LONG);
	toast.show();
    }
    
    @Override
    public int nhgetch()
    {
	while(true) {
		if(false) return 1;
		// Wait before we die...
		try {
			Thread.sleep(10000);
		}
		catch( java.lang.InterruptedException e)
		{
		}
	}
	
    }
    @Override
    public void display_nhwindow(int winid,int flag) {
	
    }
}
