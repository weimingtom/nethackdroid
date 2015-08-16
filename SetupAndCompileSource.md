# Compile source #

## Requirements ##
You need a working Android SDK and NDK setup.

## Setup source ##
Change directory to $(ANDROID\_NDK)/apps and checkout the code from svn.
Enter nethackdroid/project/jni and run the bootstrap.sh script to download, setup and build NetHack source tools that produces a nhdat file which is the nethack data file.

## Compile source ##
The sources is devided in two parts one JNI C interface and the actual android application.

### Compile NDK part ###
First you need to build the JNI interface and it's native library libnethackjni.so.
Doing that by change directory to Android NDK top-level directory and run command:
```
APP="nethackdroid" make -f GNUMakefile
```
this does build 2 libraries, libnethack (static library) and libnethackjni.so.

### Build Android application ###
Change directory to apps/nethackdroid/project, if it's the first time you compile the application you need to issue the command:
```
android update project -p .
```
to update the source for your suite...

Then you can build the application...