# 3d Model Viewer
This model viewer can load and display pretty much any valid 3D model (3DS, Wavefront OBJ, STL, etc.).

It's mainly an experiment to see if I can write a functionally pure (immutable) high performance rendering engine with OpenGL and Java.  So far it's not going well!  OpenGL's finite state machine model isn't easy to build a safe system around, and Java isn't particular FP-friendly.

Still, the viewer has some useful features, especially for Java OpenGL developers:

* It's a good demo of a standard OpenGL model renderer, including support for many of the basics: directional and point-based lighting, shadow-mapping and anti-aliasing.
* Loads almost any valid model format (I can't take too much credit for this, the Assimp model library handles most of it).
* It's a rare example of JAssimp, the Java Assimp wrapper, in action.  Plus it includes Windows binaries of the Assimp libraryitself in dll form, which were non-trivial to build.
* Includes a JavaFX-based UI that lets you adjust many of the low level settings.  This is useful for, e.g., seeing what effect the projection matrix settings have on the render.
* You can view the shadow framebuffer (e.g. the scene from a light's perspective), and adjust the orthographic projection that creates it.
* Automatically scales and translate the model to fit inside a cube with dimensions -1.0f to 1.0f, so all models render at sensible proportions out of the box.

Developers should also check out [Enter the Matrix](https://github.com/gropple/EnterTheMatrix), an easy-to-use immutable matrix & vector maths library I wrote for the project in Scala.

## Running
Clone this project.
Open the build.sbt file in IntelliJ or similar.
Build.  It should pull in dependencies and build cleanly.
Run the MainGui.main method.

It will open one of the included model samples automatically.  Click the 'Load' button to open another (the JavaFX UI is often hidden behind the OpenGL window initially).

If you have any problems during startup then delete the settings.db file.  This persists all the UI settings and can be safely removed.

## Controls
The camera stays looking at the origin (centre of the model), and rotates around it.

'W' and 'S' keys to zoom in and out.
'A' and 'D' to rotate left and right.
'R' and 'F' to move up and down.

## Supported Models
This app supports the same models as the Assimp library.

Note that, from my experience during testing, many of the free 3D models you'll find online are broken in various ways.  For example, the very common OBJ format often has image paths hardcoded to files on the creator's machine.  So if a file isn't loading in this viewer, try loading it with the official Assimp image viewer and see if that can handle it - you'll probably find that it fails there too, indicating somethng severely wrong with the model file.