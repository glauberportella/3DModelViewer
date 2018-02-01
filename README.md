# 3d Model Viewer
This Java model viewer can load and display any 3D model (3DS, Wavefront OBJ, STL, etc.).  

It was mainly an experiment to see if I could write a function-programming/immutable high performance rendering engine with OpenGL and Java.  I failed!  OpenGL's finite state machine model didn't lend itself to immutability at all, and Java isn't particular FP-friendly. 

Features:

* Load any model format (I can't take too  much credit for this, the Assimp model library handles most of this)
* A basic OpenGL model renderer, including support for directional and point-based lighting, shadows and anti-aliasing.
* An immutable matrix & vector maths library, written in Scala.


