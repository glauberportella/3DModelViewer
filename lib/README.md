# Lib dependencies
These files were hard come-by!

## Building Assimp
The Java lib is available on bintray, but also needs a native library built (a simple JNI wrapper I think around the real assimp C++ lib).  Turns out to be surprisingly hard on Windows.

Good guide:
https://stackoverflow.com/questions/32294531/how-to-set-up-jassimp

Precompiled Windows builds for Assimp itself stopped in 2014.  Now have to build yourself with CMake.

I'm not going to go through the many failed processes I tried over 3 hours, just the final working one.

(Replace any hard-coded paths in these commands with something suitable for your own system).

cd e:\dev
git clone https://github.com/assimp/assimp.git assimp_new
Run CMake GUI (install CMake if needed), open e:\dev\assimp_new.
Set output as E:/dev/assimp_new/make_msvc_64

and generate an MSVC 15 2017 64bit solution with following variables set:
ASSIMP_BUILD_ZLIB=true   (Tells CMake to build with supplied zlib source - may not be necessary for you, but it picked up a strange zlib.lib from somewhere on my system otherwise)
Hit Configure, then Generate.  It should build an MSVC solution into E:/dev/assimp_new/make_msvc_64
Open that in MSVC 2017.  Change config to Release, and build solution.  Should produce:

E:/dev/assimp_new/make_msvc_64/code/Release/assimp-vc140-mt.lib
E:/dev/assimp_new/make_msvc_64/code/Release/assimp-vc140-mt.dll

Copy the dll into this lib folder.

## Building JAssimp
Now the Assimp lib is build, can build the JAssimp native lib wrapper.
Load an "x64 Native Tools Command Prompt for VS 2017"

cd e:/dev/assimp_new/port/jassimp/jassimp-native/src
cl /I "e:/dev/jdk1.8.0_161/include" /I "e:/dev/jdk1.8.0_161/include/win32" /I "e:/dev/assimp_new/include" /I "e:/dev/assimp_new/make_msvc_64/include" jassimp.cpp /link /LIBPATH:e:\dev\assimp_new\make_msvc_64\code\Release assimp-vc140-mt.lib /DLL /OUT:jassimp.dll

Should give jassimp.dll.  Copy into this lib folder.

## Things to avoid
Don't download the Assimp SDK, just use the code direct from github.
I got nowhere with CLion, CMake for MSYS, CMake for mingw.