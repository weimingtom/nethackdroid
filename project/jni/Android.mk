NETHACK_SOURCES=nethack-3.4.3

LOCAL_PATH:= $(call my-dir)

#
# Build static nethack library
#
NETHACK_CFLAGS:=  -DHACKDIR="\"/sdcard/nethackdata\"" -DPORT_ID="\"Android\"" -DUSE_TILES -DPREFIXES_IN_USE
include $(CLEAR_VARS)
LOCAL_C_INCLUDES := $(LOCAL_PATH)/$(NETHACK_SOURCES)/include
LOCAL_CFLAGS    := $(NETHACK_CFLAGS)
LOCAL_MODULE    := libnethack
LOCAL_SRC_FILES := $(NETHACK_SOURCES)/src/allmain.c $(NETHACK_SOURCES)/src/alloc.c $(NETHACK_SOURCES)/src/apply.c $(NETHACK_SOURCES)/src/artifact.c $(NETHACK_SOURCES)/src/attrib.c $(NETHACK_SOURCES)/src/ball.c $(NETHACK_SOURCES)/src/bones.c \
	   $(NETHACK_SOURCES)/src/botl.c $(NETHACK_SOURCES)/src/cmd.c $(NETHACK_SOURCES)/src/dbridge.c $(NETHACK_SOURCES)/src/decl.c $(NETHACK_SOURCES)/src/detect.c $(NETHACK_SOURCES)/src/dig.c $(NETHACK_SOURCES)/src/display.c $(NETHACK_SOURCES)/src/dlb.c $(NETHACK_SOURCES)/src/do.c \
	   $(NETHACK_SOURCES)/src/do_name.c $(NETHACK_SOURCES)/src/do_wear.c $(NETHACK_SOURCES)/src/dog.c $(NETHACK_SOURCES)/src/dogmove.c $(NETHACK_SOURCES)/src/dokick.c $(NETHACK_SOURCES)/src/dothrow.c $(NETHACK_SOURCES)/src/drawing.c \
	   $(NETHACK_SOURCES)/src/dungeon.c $(NETHACK_SOURCES)/src/eat.c $(NETHACK_SOURCES)/src/end.c $(NETHACK_SOURCES)/src/engrave.c $(NETHACK_SOURCES)/src/exper.c $(NETHACK_SOURCES)/src/explode.c $(NETHACK_SOURCES)/src/extralev.c \
	   $(NETHACK_SOURCES)/src/files.c $(NETHACK_SOURCES)/src/fountain.c $(NETHACK_SOURCES)/src/hack.c $(NETHACK_SOURCES)/src/hacklib.c $(NETHACK_SOURCES)/src/invent.c $(NETHACK_SOURCES)/src/light.c $(NETHACK_SOURCES)/src/lock.c \
	   $(NETHACK_SOURCES)/src/mail.c $(NETHACK_SOURCES)/src/makemon.c $(NETHACK_SOURCES)/src/mapglyph.c $(NETHACK_SOURCES)/src/mcastu.c $(NETHACK_SOURCES)/src/mhitm.c $(NETHACK_SOURCES)/src/mhitu.c $(NETHACK_SOURCES)/src/minion.c \
	   $(NETHACK_SOURCES)/src/mklev.c $(NETHACK_SOURCES)/src/mkmap.c \
	   $(NETHACK_SOURCES)/src/mkmaze.c $(NETHACK_SOURCES)/src/mkobj.c $(NETHACK_SOURCES)/src/mkroom.c $(NETHACK_SOURCES)/src/mon.c $(NETHACK_SOURCES)/src/mondata.c $(NETHACK_SOURCES)/src/monmove.c $(NETHACK_SOURCES)/src/monst.c \
	   $(NETHACK_SOURCES)/src/mplayer.c $(NETHACK_SOURCES)/src/mthrowu.c $(NETHACK_SOURCES)/src/muse.c $(NETHACK_SOURCES)/src/music.c $(NETHACK_SOURCES)/src/o_init.c $(NETHACK_SOURCES)/src/objects.c $(NETHACK_SOURCES)/src/objnam.c \
	   $(NETHACK_SOURCES)/src/options.c $(NETHACK_SOURCES)/src/pager.c $(NETHACK_SOURCES)/src/pickup.c $(NETHACK_SOURCES)/src/pline.c $(NETHACK_SOURCES)/src/polyself.c $(NETHACK_SOURCES)/src/potion.c $(NETHACK_SOURCES)/src/pray.c \
	   $(NETHACK_SOURCES)/src/priest.c $(NETHACK_SOURCES)/src/quest.c $(NETHACK_SOURCES)/src/questpgr.c $(NETHACK_SOURCES)/src/read.c $(NETHACK_SOURCES)/src/rect.c $(NETHACK_SOURCES)/src/region.c $(NETHACK_SOURCES)/src/restore.c $(NETHACK_SOURCES)/src/rip.c \
	   $(NETHACK_SOURCES)/src/rnd.c $(NETHACK_SOURCES)/src/role.c $(NETHACK_SOURCES)/src/rumors.c $(NETHACK_SOURCES)/src/save.c $(NETHACK_SOURCES)/src/shk.c $(NETHACK_SOURCES)/src/shknam.c $(NETHACK_SOURCES)/src/sit.c $(NETHACK_SOURCES)/src/sounds.c $(NETHACK_SOURCES)/src/sp_lev.c \
	   $(NETHACK_SOURCES)/src/spell.c $(NETHACK_SOURCES)/src/steal.c $(NETHACK_SOURCES)/src/steed.c $(NETHACK_SOURCES)/src/teleport.c $(NETHACK_SOURCES)/src/timeout.c $(NETHACK_SOURCES)/src/topten.c $(NETHACK_SOURCES)/src/track.c $(NETHACK_SOURCES)/src/trap.c \
	   $(NETHACK_SOURCES)/src/u_init.c $(NETHACK_SOURCES)/src/uhitm.c $(NETHACK_SOURCES)/src/vault.c $(NETHACK_SOURCES)/src/version.c $(NETHACK_SOURCES)/src/vision.c $(NETHACK_SOURCES)/src/weapon.c $(NETHACK_SOURCES)/src/were.c $(NETHACK_SOURCES)/src/wield.c \
	   $(NETHACK_SOURCES)/src/wizard.c $(NETHACK_SOURCES)/src/worm.c $(NETHACK_SOURCES)/src/worn.c $(NETHACK_SOURCES)/src/write.c $(NETHACK_SOURCES)/src/zap.c $(NETHACK_SOURCES)/src/tile.c \
	   \
	   $(NETHACK_SOURCES)/src/monstr.c \
	   \
	   $(NETHACK_SOURCES)/sys/unix/unixunix.c \
	   $(NETHACK_SOURCES)/sys/share/unixtty.c \
	   $(NETHACK_SOURCES)/sys/share/ioctl.c 
	
include $(BUILD_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_C_INCLUDES := $(LOCAL_PATH)/$(NETHACK_SOURCES)/include
LOCAL_CFLAGS    := $(NETHACK_CFLAGS)
LOCAL_LDLIBS    := -llog 
LOCAL_MODULE    := libnethackjni
LOCAL_SRC_FILES := nethackjni.c
LOCAL_STATIC_LIBRARIES := libnethack

include $(BUILD_SHARED_LIBRARY)

