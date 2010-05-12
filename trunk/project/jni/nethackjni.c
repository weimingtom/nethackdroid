/*
    This file is part of nethackdroid,
    copyright (c) 2010 Henrik Andersson.

    darktable is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    darktable is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with darktable.  If not, see <http://www.gnu.org/licenses/>.
*/

#include <jni.h>
#include <android/log.h>

#include <sys/stat.h>
#include <pthread.h>
// Nethack includes
#include <hack.h>
#include <dlb.h>

#define  LOG_TAG    "libnethackjni"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

NEARDATA struct window_procs windowprocs;


  
/// Nethack proxy functions
void _nhjni_proxy_init();
void _nhjni_proxy_init_nhwindows(int argc,char **argv);
void _nhjni_proxy_raw_print(const char *str);
void _nhjni_proxy_putstr(winid window,int attr,const char *str);
int _nhjni_proxy_nhgetch();
int _nhjni_proxy_nh_poskey(int *x, int *y, int *mod);
void _nhjni_proxy_print_glyph(int winid,int x,int y,int glyph);
void _nhjni_proxy_display_nhwindow(int winid,int flag);
int _nhjni_proxy_create_nhwindow(int type);
void _nhjni_proxy_destroy_nhwindow(int winid);

void choose_windows(const char *s) {}
void more() {}
void ospeed() {}
  
JNIEnv *_nhjni_env;
jclass _nhjni_cls;
JavaVM *_nhjni_vm;
jstring _nhjni_error;
enum {
  JNI_CALLBACK_INIT_NHWINDOWS=0,
  JNI_CALLBACK_CREATE_NHWINDOW,
  JNI_CALLBACK_DISPLAY_NHWINDOW,
  JNI_CALLBACK_DESTROY_NHWINDOW,
  JNI_CALLBACK_PUTSTR,
  JNI_CALLBACK_PRINTGLYPH,
  JNI_CALLBACK_RAWPRINT,
  JNI_CALLBACK_NHGETCH,
  JNI_CALLBACK_NH_POSKEY,
  
  JNI_CALLBACK_COUNT
};
jmethodID jni_callback_methods[JNI_CALLBACK_COUNT];

  
/** The proxy object for nethack library */
struct window_procs _nhjni_proxy_procs = {
    "Android",
    WC_COLOR|WC_HILITE_PET,
    0L,
    _nhjni_proxy_init_nhwindows,		// nhjni_proxy_init_nhwindows,
    donull,						// nhjni_proxy_player_selection,
    donull,						// nhjni_proxy_askname,
    donull,						// nhjni_proxy_get_nh_event,
    donull,						// nhjni_proxy_exit_nhwindows,
    donull,						// nhjni_proxy_suspend_nhwindows,
    donull,						// nhjni_proxy_resume_nhwindows,
    _nhjni_proxy_create_nhwindow,						// nhjni_proxy_create_nhwindow,
    donull,						// nhjni_proxy_clear_nhwindow,
    _nhjni_proxy_display_nhwindow,	// nhjni_proxy_display_nhwindow,
    _nhjni_proxy_destroy_nhwindow,	       // nhjni_proxy_destroy_nhwindow,
    donull,						// nhjni_proxy_curs,
    _nhjni_proxy_putstr,			// nhjni_proxy_putstr,
    donull,						// nhjni_proxy_display_file,
    donull,						// nhjni_proxy_start_menu,
    donull,						// nhjni_proxy_add_menu,
    donull,						// nhjni_proxy_end_menu,
    donull,						// nhjni_proxy_select_menu,
    donull,						// nhjni_proxy_message_menu,		/* no need for X-specific handling */
    donull,						// nhjni_proxy_update_inventory,
    donull,						// nhjni_proxy_mark_synch,
    donull,						// nhjni_proxy_wait_synch,
#ifdef CLIPPING
    donull,						// nhjni_proxy_cliparound,
#endif
#ifdef POSITIONBAR
    donull,
#endif
    _nhjni_proxy_print_glyph,						// nhjni_proxy_print_glyph,
    _nhjni_proxy_raw_print,						// nhjni_proxy_raw_print,
    _nhjni_proxy_raw_print,						// nhjni_proxy_raw_print_bold,
    _nhjni_proxy_nhgetch,						// nhjni_proxy_nhgetch,
    _nhjni_proxy_nh_poskey,						// nhjni_proxy_nh_poskey,
    donull,						// nhjni_proxy_nhbell,
    donull,						// nhjni_proxy_doprev_message,
    donull,						// nhjni_proxy_yn_function,
    donull,						// nhjni_proxy_getlin,
    donull,						// nhjni_proxy_get_ext_cmd,
    donull,						// nhjni_proxy_number_pad,
    donull,						// nhjni_proxy_delay_output,
#ifdef CHANGE_COLOR	/* only a Mac option currently */
    donull,
    donull,
#endif
    /* other defs that really should go away (they're tty specific) */
    donull,						// nhjni_proxy_start_screen,
    donull,						// nhjni_proxy_end_screen,
    donull,						// nhjni_proxy_outrip,
    donull						// genl_preference_update,
};

static struct win_choices {
    struct window_procs *procs;
    void NDECL((*ini_routine));		
} winchoices[] = { 
  &_nhjni_proxy_procs , _nhjni_proxy_init
};

/**
  *  PROXY FUNCTIONS
  */
void _nhjni_proxy_init() {
  // Initialize windows and gui...
  
  // Behind this point callbacks should be handled...
  iflags.window_inited = 1;
}

void  _nhjni_proxy_init_nhwindows(int argc,char **argv) {
  LOGI("init_nhwindows() dispatched."); 
  (*_nhjni_env)->CallStaticVoidMethod(_nhjni_env,_nhjni_cls, jni_callback_methods[JNI_CALLBACK_INIT_NHWINDOWS]);
  
  // Initialize default player settings , randomize role,race,gend and align
  /// \todo In future, get data from a startupdialog ...
  strcpy(plname,"Goliat");
  flags.initrole = pick_role(flags.initrace, flags.initgend, flags.initalign, PICK_RANDOM);
  flags.initrace = pick_race(flags.initrole, flags.initgend, flags.initalign, PICK_RANDOM);
  flags.initgend = pick_gend(flags.initrole, flags.initrace, flags.initalign, PICK_RANDOM);
  flags.initalign = pick_align(flags.initrole, flags.initrace, flags.initgend, PICK_RANDOM);
}

void _nhjni_proxy_destroy_nhwindow(int winid) {
  (*_nhjni_env)->CallStaticVoidMethod(_nhjni_env,_nhjni_cls, jni_callback_methods[JNI_CALLBACK_DESTROY_NHWINDOW], winid);
}

void _nhjni_proxy_raw_print(const char *str) { 
  //LOGI("raw_print(\"%s\") dispatched.",str); 
  jstring js = (*_nhjni_env)->NewStringUTF(_nhjni_env,str);
  (*_nhjni_env)->CallStaticVoidMethod(_nhjni_env,_nhjni_cls, jni_callback_methods[JNI_CALLBACK_RAWPRINT], js);
}

void _nhjni_proxy_putstr(winid window,int attr,const char *str) {
  //LOGI("putstr(\"%s\") dispatched.",str); 
  jstring js = (*_nhjni_env)->NewStringUTF(_nhjni_env,str);
  (*_nhjni_env)->CallStaticVoidMethod(_nhjni_env,_nhjni_cls, jni_callback_methods[JNI_CALLBACK_PUTSTR],window,attr, js);
}

int _nhjni_proxy_nhgetch() {
  //LOGI("nhgetch() dispatched."); return 0; 
  return (*_nhjni_env)->CallStaticIntMethod(_nhjni_env,_nhjni_cls, jni_callback_methods[JNI_CALLBACK_NHGETCH]);
}

int _nhjni_proxy_nh_poskey(int *x, int *y, int *mod) {
  // LOGI("nh_poskey(%d,%d,%d) not implemented.",x,y,mod);
  return (*_nhjni_env)->CallStaticIntMethod(_nhjni_env,_nhjni_cls, jni_callback_methods[JNI_CALLBACK_NH_POSKEY]);
  return 0;
}

void _nhjni_proxy_print_glyph(int winid,int x,int y,int glyph) {
  //LOGI("print_glyph(%d,%d,%d,%d) dispatched.",winid,x,y,glyph);
  (*_nhjni_env)->CallStaticVoidMethod(_nhjni_env,_nhjni_cls, jni_callback_methods[JNI_CALLBACK_PRINTGLYPH],winid,x,y,glyph);
}


void _nhjni_proxy_display_nhwindow(int winid,int flag) {
  //LOGI("display_nhwindow(%d,%d) dispatched.",winid,flag);
  (*_nhjni_env)->CallStaticVoidMethod(_nhjni_env,_nhjni_cls,  jni_callback_methods[JNI_CALLBACK_DISPLAY_NHWINDOW],winid,flag);
}

int _nhjni_proxy_create_nhwindow(int type) {	
  //LOGI("create_nhwindow(%d) dispatched.",type);
  return (*_nhjni_env)->CallStaticIntMethod(_nhjni_env,_nhjni_cls, jni_callback_methods[JNI_CALLBACK_CREATE_NHWINDOW],type);
}

jboolean nhjni_run() {
  windowprocs = _nhjni_proxy_procs;
  _nhjni_proxy_init();
  int fd = create_levelfile(0, (char *)0);
  if (fd < 0) {
    raw_print("Cannot create lock file");
  } else {
    hackpid = 1;
    write(fd, (genericptr_t) &hackpid, sizeof(hackpid));
    close(fd);
  }
  
  iflags.news = TRUE;
  int argc=1;
  char *argv[]={"nethack",NULL};
  
  initoptions();
  init_nhwindows(&argc,argv);
  dlb_init();
  vision_init();
  display_gamewindows();
  
  if ((fd = restore_saved_game()) >= 0) {
    const char *fq_save = fqname(SAVEF, SAVEPREFIX, 1);

    pline("Restoring save file...");
    mark_synch();	/* flush output */
    if(!dorecover(fd))
      goto not_recovered;

    check_special_room(FALSE);
    //wd_message();

    if (discover || wizard) {
      if(yn("Do you want to keep the save file?") == 'n')
          (void) delete_savefile();
      else {
          (void) chmod(fq_save,FCMASK); /* back to readable */
          compress(fq_save);
      }
    }
    flags.move = 0;
  } else {
not_recovered:
    player_selection();
    newgame();
    flags.move = 0;
    set_wear();
    (void) pickup(1);
  }

  moveloop();
  
  return JNI_TRUE;
}

jstring Java_se_dinamic_nethack_LibNetHack_error( JNIEnv*  env, jobject  obj ) {
  return _nhjni_error;
}

int _nhji_copy_file(char *src, char *dest) 
{
  FILE *from,*to;
  char ch;
  if((from=fopen(src,"rb"))==NULL) {
    LOGE("_nhji_copy_file() Failed to open source file %s for copy",src);
    return -1;
  }
   if((to=fopen(dest,"wb"))==NULL) {
    LOGE("_nhji_copy_file() Failed to open destination file %s for copy",dest);
    return -1;
  }
   while(!feof(from)) {
    ch = fgetc(from);
     if(!feof(from)) fputc(ch, to);
   }
   fclose(from);
   fclose(to);
  
  return 0;
}

int _nhji_copy_on_verify_fail( JNIEnv*  env,char *src, char *dest)
{
  int verified=1,res=0;
  struct stat s;
  if(stat(dest,&s ) != 0) verified=0;
  if(verified==0) {
    res=_nhji_copy_file(src,dest);
    _nhjni_error = (*env)->NewStringUTF(env,"Failed to copy nethack data to "HACKDIR);
  }
  return res;
}

void * _nethack_thread_proc(void *data) {
 
  if( (*_nhjni_vm)->AttachCurrentThread(_nhjni_vm,&_nhjni_env, NULL) == JNI_OK) {
    // Setup JNI dispatch method callbacks cache
    //int verified=1;
    jni_callback_methods[JNI_CALLBACK_INIT_NHWINDOWS] = (*_nhjni_env)->GetStaticMethodID(_nhjni_env,_nhjni_cls, "dispatch_init_nhwindows", "()V");
    jni_callback_methods[JNI_CALLBACK_CREATE_NHWINDOW] = (*_nhjni_env)->GetStaticMethodID(_nhjni_env,_nhjni_cls, "dispatch_create_nhwindow", "(I)I");
    jni_callback_methods[JNI_CALLBACK_DISPLAY_NHWINDOW] = (*_nhjni_env)->GetStaticMethodID(_nhjni_env,_nhjni_cls, "dispatch_display_nhwindow", "(II)V");
    jni_callback_methods[JNI_CALLBACK_DESTROY_NHWINDOW] = (*_nhjni_env)->GetStaticMethodID(_nhjni_env,_nhjni_cls, "dispatch_destroy_nhwindow", "(I)V");
    jni_callback_methods[JNI_CALLBACK_PUTSTR] = (*_nhjni_env)->GetStaticMethodID(_nhjni_env,_nhjni_cls, "dispatch_putstr", "(IILjava/lang/String;)V");
    jni_callback_methods[JNI_CALLBACK_PRINTGLYPH] = (*_nhjni_env)->GetStaticMethodID(_nhjni_env,_nhjni_cls, "dispatch_print_glyph", "(IIII)V");
    jni_callback_methods[JNI_CALLBACK_RAWPRINT] = (*_nhjni_env)->GetStaticMethodID(_nhjni_env,_nhjni_cls, "dispatch_raw_print", "(Ljava/lang/String;)V");
    jni_callback_methods[JNI_CALLBACK_NH_POSKEY] = jni_callback_methods[JNI_CALLBACK_NHGETCH] =  (*_nhjni_env)->GetStaticMethodID(_nhjni_env,_nhjni_cls, "dispatch_nhgetch", "()I");

    /*
    for( i=0;i<JNI_CALLBACK_COUNT;i++)
      if( jni_callback_methods[i]==0) verified=0;

    if( verified== 0 ) {
      _nhjni_error = (*env)->NewStringUTF(env,"Failed to get complete nethack callback interface");
      return JNI_FALSE;
    }*/

    
    // Lets run nethack
    nhjni_run();
    
    // Nethack ended lets cleanup and exit thread...
    (*_nhjni_env)->DeleteGlobalRef(_nhjni_env, _nhjni_cls);
    (*_nhjni_vm)->DetachCurrentThread(_nhjni_vm);
  }
}

jboolean Java_se_dinamic_nethack_LibNetHack_run( JNIEnv*  env, jobject  obj ) {
  LOGD("Starting nethack session...");
  int i;
  (*env)->GetJavaVM(env,&_nhjni_vm );
  jclass localcls = (*env)->FindClass(env, "se/dinamic/nethack/LibNetHack" );
  _nhjni_cls =  (*env)->NewGlobalRef(env,localcls);
  
  // Let's check if nethack data is installed into sdcard, if not do it...
  struct stat s;
  if(stat(HACKDIR"/nhdat",&s ) != 0) mkdir(HACKDIR,0777); // This would probabley want to be recursive HACKDIR changes.. but it suits for now..
  if(stat(HACKDIR"/cache",&s ) != 0) mkdir(HACKDIR"/cache",0777); 
	
  if( _nhji_copy_on_verify_fail(env,"/data/data/se.dinamic.nethack/lib/libnhdat.so",HACKDIR"/nhdat") !=0 ) return JNI_FALSE;
  chdir(HACKDIR);
  
  // This start nethack inner loop thread...
  pthread_t thread = 0;
  pthread_create(&thread, NULL, _nethack_thread_proc, NULL);
  
  return JNI_TRUE;
}
