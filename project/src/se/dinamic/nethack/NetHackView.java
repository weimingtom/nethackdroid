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

import android.opengl.GLSurfaceView;
import android.content.Context;
import android.util.Log;
import android.app.Application;
import android.graphics.Typeface;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.util.Vector;

class NetHackView extends GLSurfaceView implements GLSurfaceView.Renderer 
{
  private GL10 _gl;
  private Vector _renderers;
  private Application _application;
  private int _viewState;
  
  private NetHackIntroRenderer _introRenderer;
  private NetHackUserInterfaceRenderer _userInterfaceRenderer;
  
  public final static int STATE_INITIALIZE_GAME=1;
  public final static int STATE_GAME_RUN=2;
  
    
  public NetHackView(Application application) {
    super(application.getApplicationContext());
    _application=application;
    _renderers=new Vector<NetHackRenderer>();
    _viewState=STATE_INITIALIZE_GAME;
    
    // Get key and touch events...
    setFocusable( true ); 
    setFocusableInTouchMode( true );

    // Set default typeface in FontAtlasTexture
    //FontAtlasTexture._typeFace = Typeface.createFromAsset(context.getAssets(), "fonts/Isabella.ttf") ;
    FontAtlasTexture._typeFace = Typeface.create(Typeface.SANS_SERIF,Typeface.NORMAL);

    // Initialize helper class for 3d obejct rendering..
    NetHackObjects.initialize();
        
      
    // Create the intro renderer
    _introRenderer = new NetHackIntroRenderer( _application );
    _introRenderer.preInit( );
      
    RendererClockThread job=new RendererClockThread(_introRenderer,_renderers);
    job.start();
      
    
      
    // Fire off setting the GL renderer
    setRenderer( this );
  }
    
  
  public void addRenderer(NetHackRenderer r) {
      _renderers.add(r);
  }
  
  public void setState( int state ) {
      switch(state) {
        case STATE_INITIALIZE_GAME:
        {
            // Thread to initialize all renderers and timeconsuming loading assigned 
            // to the  this view etc.. during intro anim....
            InitializeRenderersJob job=new InitializeRenderersJob(_application, _introRenderer,_renderers);
            job.start();
        }break;
        
        case STATE_GAME_RUN:
        {
            // Create the nethack userinterface renderer
            addRenderer( (_userInterfaceRenderer = new NetHackUserInterfaceRenderer()) );
        }break;
      }
      Log.d(NetHack.LOGTAG,"NetHackView.setState() Change view state too "+state);
      _viewState = state;
  }
  
  public void onDrawFrame( GL10 gl ) {
      // Clear gl buffer
      gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT |  GL10.GL_STENCIL_BUFFER_BIT);
      
      // Setup viewpoint and camera
      gl.glMatrixMode(GL10.GL_MODELVIEW);
      gl.glLoadIdentity();
      switch( _viewState ) {
          case STATE_INITIALIZE_GAME:
          {
              // Show intro screen and progressbar...
              _introRenderer.render( gl );
              
          } break;
          
          case STATE_GAME_RUN:
          {
              // Run thru all renderers to render the scene
              for(int i=0;i<_renderers.size();i++) {
                  gl.glPushMatrix();
                  NetHackRenderer r=(NetHackRenderer)_renderers.get(i);
                  r.render( gl );
                  gl.glPopMatrix();
              }
          }break;
      }
  }
  
  /** This is rendering clock thread, providing animation calulation ticks.. 
    @todo In future this delta clock might be placed within render loop onDrawSurface()
  */
  private static class RendererClockThread extends Thread {
    NetHackIntroRenderer _introRenderer;
    private Vector<NetHackRenderer> _renderers;
    private long _previous=0;
      
    public RendererClockThread( NetHackIntroRenderer introRenderer, Vector<NetHackRenderer> renderers ) {
        _renderers = renderers;
        _introRenderer=introRenderer;
        _previous = java.lang.System.currentTimeMillis();
        }
    
    public void run() {
        while(true) {
            try {
                Thread.sleep(10);
            } catch(InterruptedException e) {
            }
            
            long now = java.lang.System.currentTimeMillis();
            long delta = now-_previous;
            _introRenderer.clock( delta );
            
            for(int i=0;i<_renderers.size();i++) {
                NetHackRenderer r=(NetHackRenderer)_renderers.get(i);
                r.clock( delta );
            }
            
            _previous=now;
        }
    }
  }
  
  private static class InitializeRenderersJob extends Thread {
      private Vector<NetHackRenderer> _renderers;
      private Application _application;
      NetHackIntroRenderer _introRenderer;
      private  GL10 _gl;
      
      public InitializeRenderersJob(Application application,NetHackIntroRenderer introRenderer, Vector<NetHackRenderer> renderers ) {
        _application = application;
        _renderers = renderers;
        _introRenderer=introRenderer;
      }
      
      public void run() {
        // Load sound 
        NetHackSound.initialize();
        NetHackIntroRenderer.progress(0.25f);
          
        // intialize the fontatlastexture 
        FontAtlasTexture.initialize();
        NetHackIntroRenderer.progress(0.25f);
          
        // Initialize userinterface renderer...
        NetHackUserInterfaceRenderer.initialize( _application );
        NetHackIntroRenderer.progress(0.25f);
        
        Log.d(NetHack.LOGTAG,"NetHackView.InitializeRenderersJob.run() Running pre-initialization of renderers job.");
        for(int i=0;i<_renderers.size();i++) {
            NetHackRenderer r=(NetHackRenderer)_renderers.get(i);
            r.preInit( );
            NetHackIntroRenderer.progress(0.25f/_renderers.size());
        }
        
        // Everything is initialized at this point lets
        // wait for intro to finish then start nethack game...
        while( ! _introRenderer.isIntroFinished() ) {
          try {
            Thread.sleep(500);
          } catch(InterruptedException e) {
          }
        }
        NetHackEngine.startGame();
      }
  }
  
  public void onSurfaceCreated( GL10 gl, EGLConfig config ) {
    _gl=gl;
    
    // Pass initialize all renderers
    _introRenderer.init( gl );
    
    // Last init pass that need the actual GL instance.. (actual texture creations)
    for(int i=0;i<_renderers.size();i++) {
      NetHackRenderer r=(NetHackRenderer)_renderers.get(i);
      r.init( _gl );
    }

    gl.glDisable( GL10.GL_DITHER );
    gl.glHint( GL10.GL_PERSPECTIVE_CORRECTION_HINT , GL10.GL_NICEST );
    gl.glClearColor( 0.0f, 0.0f, 0.0f, 0.0f);
    gl.glFrontFace(gl.GL_CCW);
    gl.glEnable(GL10.GL_CULL_FACE);
    gl.glCullFace(GL10.GL_BACK);
    gl.glShadeModel(GL10.GL_FLAT);
    
    gl.glEnable(GL10.GL_BLEND);
    gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
    gl.glEnable(GL10.GL_COLOR_MATERIAL);
    gl.glEnable(GL10.GL_TEXTURE_2D);
    
    // All objects rendered have varr, carr and textcoordarr soo this is put here
    // for performance
    gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
    gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
    gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

  } 
  
  public void onSurfaceChanged( GL10 gl, int width, int height ) {
      // Configure new viewportsize 
      _gl = gl;
      gl.glViewport(0, 0, width, height);
      
      NetHackWindowManager.screenWidth = width;
      NetHackWindowManager.screenHeight = height;
      
      // Calculate new projection matrix on viewport dimension change
      float ratio = (float) width / height;
      gl.glMatrixMode(GL10.GL_PROJECTION);
      gl.glLoadIdentity();
      gl.glFrustumf(-ratio, ratio, -1, 1, 1, 40);
  }
}