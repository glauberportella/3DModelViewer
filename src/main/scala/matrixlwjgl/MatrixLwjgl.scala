package matrixlwjgl

/**
  * This package holds interactions between LWJGL and the matrix libs, to keep the latter clean
  */

import java.nio.FloatBuffer

import enterthematrix.Matrix4x4
import org.lwjgl.BufferUtils


object MatrixLwjgl {

  def convertMatrixToBuffer(m: Matrix4x4): FloatBuffer = {
    val buffer = BufferUtils.createFloatBuffer(16)
    buffer.put(m.r0c0).put(m.r1c0).put(m.r2c0).put(m.r3c0)
    buffer.put(m.r0c1).put(m.r1c1).put(m.r2c1).put(m.r3c1)
    buffer.put(m.r0c2).put(m.r1c2).put(m.r2c2).put(m.r3c2)
    buffer.put(m.r0c3).put(m.r1c3).put(m.r2c3).put(m.r3c3)
    buffer.flip
    buffer
  }

}
