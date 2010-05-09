package se.dinamic.nethack;

public abstract class LibNetHack {
    
    private static LibNetHack  _instance=null;
    
    static {
        System.loadLibrary("nethackjni");
    }
     
    public LibNetHack() {
        _instance=this;
    }
    
     // Functions
     public static native boolean run();
     public static native String error();
    
    // Callbacks
    abstract void onInitWindows();
    abstract void onRawPrint(String status);
    abstract void onPutStr(int winid,int attr,String status);
    abstract int onGetKey();
    abstract void onPrintGlyph(int winid,int x,int y,int glyph);
    abstract int onCreateWindow(int type);
    abstract void onDisplayWindow(int winid,int flag);
    
     // Dispatch callbacks
     public static void dispatch_init_nhwindows() { if(_instance!=null) _instance.onInitWindows(); }
     public static void dispatch_raw_print(String status) { if(_instance!=null) _instance.onRawPrint(status); }
     public static void dispatch_putstr(int winid,int attr,String status) { if(_instance!=null) _instance.onPutStr(winid,attr,status); }
     public static int dispatch_nhgetch() { if(_instance!=null) return _instance.onGetKey(); return 0;}
     public static void dispatch_print_glyph(int winid,int x,int y,int glyph) { if(_instance!=null) _instance.onPrintGlyph(winid,x,y,glyph);}
     public static int dispatch_create_nhwindow(int type) {if(_instance!=null) return _instance.onCreateWindow(type); return 0; }
     public static void dispatch_display_nhwindow(int winid,int flag) { if(_instance!=null) _instance.onDisplayWindow(winid,flag); }
}
