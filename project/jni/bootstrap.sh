#/bin/bash
NETHACK_ARCHIVE="nethack-343-src.tgz"
NETHACK_DIRECTORY="nethack-3.4.3"

echo -n "Downloading source: "
if [ ! -f $NETHACK_ARCHIVE ];
then
	wget http://downloads.sourceforge.net/project/nethack/nethack/3.4.3/nethack-343-src.tgz?use_mirror=heanet
	echo "done"
else
	echo "skipped, tar archive exists."
fi

echo -n "Remove old sourcetree: "
if [ -d $NETHACK_DIRECTORY ];
then
	rm -R $NETHACK_DIRECTORY
	echo "done"
else
	echo "skipped, no nethack-3.4.3 source tree found."
fi
echo "Extracting NetHack sourcetree."
tar -xf $NETHACK_ARCHIVE

echo "Setting up NetHack makefiles."
cd $NETHACK_DIRECTORY/sys/unix && chmod +x setup.sh
./setup.sh

echo "Applying DLB patch"
cd ../../include && cp config.h config.h.orginal
cat config.h.orginal | sed 's/\/\* #define DLB \*\//#define DLB/g' > config.h

echo "Generating NetHack data file..."
cd ../util/ && make makedefs tileutils
./makedefs -odemvpqrhz
./tilemap

echo "Building NetHack data file..."
cd ../dat && make
cd ../ && make dlb
cp dat/nhdat ../../libs/armeabi/libnhdat.so