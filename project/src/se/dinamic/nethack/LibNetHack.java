package se.dinamic.nethack;

public class LibNetHack {

     static {
         System.loadLibrary("nethackjni");
     }

     // Functions
     public static native boolean run();
     public static native String error();
     
     // Callbacks
     public void raw_print(String status) {}
     public void putstr(int winid,int attr,String status) {}
     public int nhgetch() { return 0; }
     public void print_glyph(int winid,int x,int y,int glyph) {}
     public void display_nhwindow(int winid,int flag) {}
}
