package se.dinamic.nethack;

import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import android.opengl.GLSurfaceView;


public class NetHackEngine extends LibNetHack 
{
    private Activity _context;
    private NetHackView _view;
    private java.util.Random _random;

    public NetHackEngine(Activity context) 
   {
        _context=context;
        _view=new NetHackView(context);
        _random=new java.util.Random();
   }	   
    
   public GLSurfaceView getView() {
       return _view;
   }
   
    public void start() {
       run();
    }
    
    public void onPause() {
        _view.onPause();
    }
    
    public void onResume() {
        // Also pause/resume game when we find a way...
        _view.onResume();
    }
   
   /** 
    *   Implementations of libnethack abstract functions
    */
    public void onInitWindows() {
        // Let do the splash screen....
    }
    
    public void onRawPrint(String status) {
       // This should probable been overlayed on ui as a log...
        Toast toast = Toast.makeText(_context, status, Toast.LENGTH_LONG);
        toast.show();
    }
    public void onPutStr(int winid,int attr,String status) {
    }
    public void onPrintGlyph(int winid,int x,int y,int glyph) {
        
    }
   
    public int onGetKey() {
        while(true) {
            if(false) return 1;
            // Wait before we die...
            try {
                Thread.sleep(1000);
            }
            catch( java.lang.InterruptedException e)
            {
            }
        }
    }
   
    public int onCreateWindow(int type) {
        return 1+(_random.nextInt()%50);
    }
    
    public void onDisplayWindow(int winid,int flag) {
    }
    
}
