#include <jni.h>
#include <android/log.h>

#include <sys/stat.h>

// Nethack includes
#include <hack.h>
#include <dlb.h>

#define  LOG_TAG    "libnethackjni"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

NEARDATA struct window_procs windowprocs;

JNIEnv *_nhjni_env;
JavaVM *_nhjni_vm;
jstring _nhjni_error;
  
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


void choose_windows(const char *s) {}
void more() {}
void ospeed() {}
  
/** The proxy object for nethack library */
struct window_procs _nhjni_proxy_procs = {
    "nhjni_proxy",
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
    _nhjni_proxy_display_nhwindow,						// nhjni_proxy_display_nhwindow,
    donull,						// nhjni_proxy_destroy_nhwindow,
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
  // Initialize default player settings , randomize role,race,gend and align
  strcpy(plname,"Goliat");
  flags.initrole = pick_role(flags.initrace, flags.initgend, flags.initalign, PICK_RANDOM);
  flags.initrace = pick_race(flags.initrole, flags.initgend, flags.initalign, PICK_RANDOM);
  flags.initgend = pick_gend(flags.initrole, flags.initrace, flags.initalign, PICK_RANDOM);
  flags.initalign = pick_align(flags.initrole, flags.initrace, flags.initgend, PICK_RANDOM);
}

void _nhjni_proxy_raw_print(const char *str) { 
  LOGI("raw_print(\"%s\") dispatched.",str); 
  (*_nhjni_vm)->AttachCurrentThread(_nhjni_vm,&_nhjni_env, NULL );
  jclass cls = (*_nhjni_env)->FindClass(_nhjni_env, "se/dinamic/nethack/LibNetHack" );
  jmethodID mid = (*_nhjni_env)->GetMethodID(_nhjni_env,cls, "raw_print", "(Ljava/lang/String;)V");
  jstring js = (*_nhjni_env)->NewStringUTF(_nhjni_env,str);
  (*_nhjni_env)->CallVoidMethod(_nhjni_env,cls, mid, js);
}

void _nhjni_proxy_putstr(winid window,int attr,const char *str) {
  LOGI("putstr(\"%s\") dispatched.",str); 
 (*_nhjni_vm)->AttachCurrentThread(_nhjni_vm,&_nhjni_env, NULL );
  jclass cls = (*_nhjni_env)->FindClass(_nhjni_env, "se/dinamic/nethack/LibNetHack" );
  jmethodID mid = (*_nhjni_env)->GetMethodID(_nhjni_env,cls, "putstr", "(IILjava/lang/String;)V");
  jstring js = (*_nhjni_env)->NewStringUTF(_nhjni_env,str);
  (*_nhjni_env)->CallVoidMethod(_nhjni_env,cls, mid, window, attr, js);

}

int _nhjni_proxy_nhgetch() {
  LOGI("nhgetch() dispatched."); return 0; 
  (*_nhjni_vm)->AttachCurrentThread(_nhjni_vm,&_nhjni_env, NULL );
  jclass cls = (*_nhjni_env)->FindClass(_nhjni_env, "se/dinamic/nethack/LibNetHack" );
  jmethodID mid = (*_nhjni_env)->GetMethodID(_nhjni_env,cls, "nhgetch", "(V)I");
  return (*_nhjni_env)->CallIntMethod(_nhjni_env,cls, mid);
}

int _nhjni_proxy_nh_poskey(int *x, int *y, int *mod) {
  LOGI("nh_poskey(%d,%d,%d) not implemented.",x,y,mod);
  return 0;
}

void _nhjni_proxy_print_glyph(int winid,int x,int y,int glyph) {
  LOGI("print_glyph(%d,%d,%d,%d) dispatched.",winid,x,y,glyph);
  (*_nhjni_vm)->AttachCurrentThread(_nhjni_vm,&_nhjni_env, NULL );
  jclass cls = (*_nhjni_env)->FindClass(_nhjni_env, "se/dinamic/nethack/LibNetHack" );
  jmethodID mid = (*_nhjni_env)->GetMethodID(_nhjni_env,cls, "print_glyph", "(IIII)V");
  (*_nhjni_env)->CallVoidMethod(_nhjni_env,cls, mid,winid,x,y,glyph);
}


void _nhjni_proxy_display_nhwindow(int winid,int flag) {
  LOGI("display_nhwindow(%d,%d) dispatched.",winid,flag);
  (*_nhjni_vm)->AttachCurrentThread(_nhjni_vm,&_nhjni_env, NULL );
  jclass cls = (*_nhjni_env)->FindClass(_nhjni_env, "se/dinamic/nethack/LibNetHack" );
  jmethodID mid = (*_nhjni_env)->GetMethodID(_nhjni_env,cls, "display_nhwindow", "(II)V");
  jvalue values[2];
  values[0].i=winid;
  values[1].i=flag;
 // (*_nhjni_env)->CallVoidMethodA(_nhjni_env,cls, mid,values);
  LOGI("display_nhwindow(%d,%d) finished.",winid,flag);
}

int _g_StatusWinID=0;
int _nhjni_proxy_create_nhwindow(int type) {
  int id = 1+(rand()%50);
  char *win="";
  switch(type) {
    case NHW_MESSAGE:
      win="NHW_MESSAGE";
    break;
    
    case NHW_STATUS:
      _g_StatusWinID=id;
      win="NHW_STATUS";
    break;
    
    case NHW_MAP:
      win="NHW_MAP";
    break;
    
    case NHW_MENU:
      win="NHW_MENU";
    break;
    
    case NHW_TEXT:
      win="NHW_TEXT";
    break;
    
    default:
      win="Unknown type";
    break;
  }
  LOGI("Create window type '%s' id %d\n",win,id);
  return id;
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

jboolean Java_se_dinamic_nethack_LibNetHack_run( JNIEnv*  env, jobject  obj ) {
  LOGD("Starting nethack session...");
  (*env)->GetJavaVM(env,&_nhjni_vm );

  // Let's check if nhdat is installed, if not do it...
  // "/data/data/se.dinamic.nethack/libs/libnhdat.so"
  struct stat s;
  if(stat(HACKDIR"/nhdat",&s ) != 0) mkdir(HACKDIR,0777);
  if( _nhji_copy_on_verify_fail(env,"/data/data/se.dinamic.nethack/lib/libnhdat.so",HACKDIR"/nhdat") !=0 ) return JNI_FALSE;
    
  
  return nhjni_run();
}
