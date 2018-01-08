package enterthematrix

case class Matrix4x4d(r0c0: Double, r0c1: Double, r0c2: Double, r0c3: Double,
                     r1c0: Double, r1c1: Double, r1c2: Double, r1c3: Double,
                     r2c0: Double, r2c1: Double, r2c2: Double, r2c3: Double,
                     r3c0: Double, r3c1: Double, r3c2: Double, r3c3: Double) {
  def get(row: Int, col: Int) = {
    row match {
      case 0 =>
        col match {
          case 0 => r0c0
          case 1 => r0c1
          case 2 => r0c2
          case 3 => r0c3
        }
      case 1 =>
        col match {
          case 0 => r1c0
          case 1 => r1c1
          case 2 => r1c2
          case 3 => r1c3
        }
      case 2 =>
        col match {
          case 0 => r2c0
          case 1 => r2c1
          case 2 => r2c2
          case 3 => r2c3
        }
      case 3 =>
        col match {
          case 0 => r3c0
          case 1 => r3c1
          case 2 => r3c2
          case 3 => r3c3
        }
    }
  }

  def +(v: Double) = Matrix4x4d(
    r0c0 + v, r0c1 + v, r0c2 + v, r0c3 + v,
    r1c0 + v, r1c1 + v, r1c2 + v, r1c3 + v,
    r2c0 + v, r2c1 + v, r2c2 + v, r2c3 + v,
    r3c0 + v, r3c1 + v, r3c2 + v, r3c3 + v)
  def -(v: Double) = Matrix4x4d(
    r0c0 - v, r0c1 - v, r0c2 - v, r0c3 - v,
    r1c0 - v, r1c1 - v, r1c2 - v, r1c3 - v,
    r2c0 - v, r2c1 - v, r2c2 - v, r2c3 - v,
    r3c0 - v, r3c1 - v, r3c2 - v, r3c3 - v)
  def *(v: Double) = Matrix4x4d(
    r0c0 * v, r0c1 * v, r0c2 * v, r0c3 * v,
    r1c0 * v, r1c1 * v, r1c2 * v, r1c3 * v,
    r2c0 * v, r2c1 * v, r2c2 * v, r2c3 * v,
    r3c0 * v, r3c1 * v, r3c2 * v, r3c3 * v)
  def +(v: Matrix4x4d) = Matrix4x4d(
    r0c0 + v.r0c0, r0c1 + v.r0c1, r0c2 + v.r0c2, r0c3 + v.r0c3,
    r1c0 + v.r1c0, r1c1 + v.r1c1, r1c2 + v.r1c2, r1c3 + v.r1c3,
    r2c0 + v.r2c0, r2c1 + v.r2c1, r2c2 + v.r2c2, r2c3 + v.r2c3,
    r3c0 + v.r3c0, r3c1 + v.r3c1, r3c2 + v.r3c2, r3c3 + v.r3c3)
  def -(v: Matrix4x4d) = Matrix4x4d(
    r0c0 - v.r0c0, r0c1 - v.r0c1, r0c2 - v.r0c2, r0c3 - v.r0c3,
    r1c0 - v.r1c0, r1c1 - v.r1c1, r1c2 - v.r1c2, r1c3 - v.r1c3,
    r2c0 - v.r2c0, r2c1 - v.r2c1, r2c2 - v.r2c2, r2c3 - v.r2c3,
    r3c0 - v.r3c0, r3c1 - v.r3c1, r3c2 - v.r3c2, r3c3 - v.r3c3)
  def *(v: Matrix4x4d) = {
    //    // Row 0
    //    val nr0c0 = r0c0 * v.r0c0 + r0c1 * v.r1c0 + r0c2 * v.r2c0 + r0c3 * v.r3c0
    //    val nr0c1 = r0c0 * v.r0c1 + r0c1 * v.r1c1 + r0c2 * v.r2c1 + r0c3 * v.r3c1
    //    val nr0c2 = r0c0 * v.r0c2 + r0c1 * v.r1c2 + r0c2 * v.r2c2 + r0c3 * v.r3c2
    //    val nr0c3 = r0c0 * v.r0c3 + r0c1 * v.r1c3 + r0c2 * v.r2c3 + r0c3 * v.r3c3
    //
    //    // Row 1
    //    val nr1c0 = r1c0 * v.r0c0 + r1c1 * v.r1c0 + r1c2 * v.r2c0 + r1c3 * v.r3c0
    //    val nr1c1 = r1c0 * v.r0c1 + r1c1 * v.r1c1 + r1c2 * v.r2c1 + r1c3 * v.r3c1
    //    val nr1c2 = r1c0 * v.r0c2 + r1c1 * v.r1c2 + r1c2 * v.r2c2 + r1c3 * v.r3c2
    //    val nr1c3 = r1c0 * v.r0c3 + r1c1 * v.r1c3 + r1c2 * v.r2c3 + r1c3 * v.r3c3

    def mi(row: Int, col: Int) = Matrix4x4d.multiplyInternal(this, v, row, col)

    Matrix4x4d(
      mi(0,0),mi(0,1),mi(0,2),mi(0,3),
      mi(1,0),mi(1,1),mi(1,2),mi(1,3),
      mi(2,0),mi(2,1),mi(2,2),mi(2,3),
      mi(3,0),mi(3,1),mi(3,2),mi(3,3))
  }
  def *(v: Vector4d) = {
    //    // Row 0
    //    val nr0c0 = r0c0 * v.r0c0 + r0c1 * v.r1c0 + r0c2 * v.r2c0 + r0c3 * v.r3c0
    //    val nr0c1 = r0c0 * v.r0c1 + r0c1 * v.r1c1 + r0c2 * v.r2c1 + r0c3 * v.r3c1
    //    val nr0c2 = r0c0 * v.r0c2 + r0c1 * v.r1c2 + r0c2 * v.r2c2 + r0c3 * v.r3c2
    //    val nr0c3 = r0c0 * v.r0c3 + r0c1 * v.r1c3 + r0c2 * v.r2c3 + r0c3 * v.r3c3
    //
    //    // Row 1
    //    val nr1c0 = r1c0 * v.r0c0 + r1c1 * v.r1c0 + r1c2 * v.r2c0 + r1c3 * v.r3c0
    //    val nr1c1 = r1c0 * v.r0c1 + r1c1 * v.r1c1 + r1c2 * v.r2c1 + r1c3 * v.r3c1
    //    val nr1c2 = r1c0 * v.r0c2 + r1c1 * v.r1c2 + r1c2 * v.r2c2 + r1c3 * v.r3c2
    //    val nr1c3 = r1c0 * v.r0c3 + r1c1 * v.r1c3 + r1c2 * v.r2c3 + r1c3 * v.r3c3

    Vector4d(
      r0c0 * v.x + r0c1 * v.y + r0c2 * v.z + r0c3 * v.w,
      r1c0 * v.x + r1c1 * v.y + r1c2 * v.z + r1c3 * v.w,
      r2c0 * v.x + r2c1 * v.y + r2c2 * v.z + r2c3 * v.w,
      r3c0 * v.x + r3c1 * v.y + r3c2 * v.z + r3c3 * v.w)
  }

  override def toString: String = {
    s"""$r0c0 $r0c1 $r0c2 $r0c3
       |$r1c0 $r1c1 $r1c2 $r1c3
       |$r2c0 $r2c1 $r2c2 $r2c3
       |$r3c0 $r3c1 $r3c2 $r3c3
     """.stripMargin
  }
}

object Matrix4x4d {
  def identity = Matrix4x4d(
    1, 0, 0, 0,
    0, 1, 0, 0,
    0, 0, 1, 0,
    0, 0, 0, 1)

  // Used to scale a Vector4 equally in 3 dimensions
  def scale(v: Double) = Matrix4x4d(
    v, 0, 0, 0,
    0, v, 0, 0,
    0, 0, v, 0,
    0, 0, 0, 1)

  // Used to move a Vector4 in 3 dimensions
  def translate(x: Double, y: Double, z: Double) = Matrix4x4d(
    1, 0, 0, x,
    0, 1, 0, y,
    0, 0, 1, z,
    0, 0, 0, 1)
  def translate(v: Vector4) = Matrix4x4d(
    1, 0, 0, v.x,
    0, 1, 0, v.y,
    0, 0, 1, v.z,
    0, 0, 0, v.w)

  def rotateAroundXAxis(angleDegrees: Double): Matrix4x4d = {
    val angleInRadians = angleDegrees * (Math.PI / 180.0f)
    val cos = Math.cos(angleInRadians).toDouble
    val sin = Math.sin(angleInRadians).toDouble
    Matrix4x4d(
      1, 0, 0, 0,
      0, cos, -sin, 0,
      0, sin, cos, 0,
      0, 0, 0, 1
    )
  }

  def rotateAroundYAxis(angleDegrees: Double): Matrix4x4d = {
    val angleInRadians = angleDegrees * (Math.PI / 180.0f)
    val cos = Math.cos(angleInRadians).toDouble
    val sin = Math.sin(angleInRadians).toDouble
    Matrix4x4d(
      Math.cos(angleDegrees).toDouble, 0, Math.sin(angleDegrees).toDouble, 0,
      0, 1, 0, 0,
      -Math.sin(angleDegrees).toDouble, 0, Math.cos(angleDegrees).toDouble, 0,
      0, 0, 0, 1
    )
  }

  def rotateAroundZAxis(angleDegrees: Double): Matrix4x4d = {
    val angleInRadians = angleDegrees * (Math.PI / 180.0f)
    val cos = Math.cos(angleInRadians).toDouble
    val sin = Math.sin(angleInRadians).toDouble
    Matrix4x4d(
      cos, -sin, 0, 0,
      sin, cos, 0, 0,
      0, 0, 1, 0,
      0, 0, 0, 1
    )
  }


  def rotateAroundAnyAxis(x: Double, y: Double, z: Double, angleDegrees: Double): Matrix4x4d = {
    val angleInRadians = angleDegrees * (Math.PI / 180.0f)
    val cos = Math.cos(angleInRadians).toDouble
    val sin = Math.sin(angleInRadians).toDouble
    Matrix4x4d(
      cos + (x * x * (1 - cos)), (x * y * (1 - cos)) - (z * sin), (x * z * (1 - cos)) + (y * sin), 0,
      (y * x * (1 - cos)) + (z * sin), cos + (y * y * (1 - cos)), (y * z * (1 - cos)) - (x * sin), 0,
      (z * x * (1 - cos)) - (y * sin), (z * y * (1 - cos)) + (x * sin), cos + (z * z * (1 - cos)), 0,
      0, 0, 0, 1
    )
  }
  def rotateAroundAnyAxis(v: Vector4, angleDegrees: Double): Matrix4x4d = {
    rotateAroundAnyAxis(v.x, v.y, v.z, angleDegrees)
  }

  private def multiplyInternal(m1: Matrix4x4d, m2: Matrix4x4d, row: Int, col: Int): Double = {
    var sum: Double = 0.0f
    //    for(r <- 0 until 4) {
    //      var rowSum = 0.0f;
    for (c <- 0 until 4) {
      val x = m1.get(row, c)
      val y = m2.get(c, col)
      sum += x * y
    }
    //    }
    sum
  }

  def lookAt(eye: Vector4, target: Vector4, verticallyUp: Vector4): Matrix4x4d = {
    //lookAt(eye.toVector3, target.toVector3, verticallyUp.toVector3)
    {
      // This is really the reverse of where the camera is pointing
      val cameraDirection = (eye - target).normalize
      // https://learnopengl.com/#!Getting-started/Camera
      val right = verticallyUp.crossProduct(cameraDirection).normalize
      val up = cameraDirection.crossProduct(right)

      // Create a 4x4 orientation matrix from the right, up, and forward vectors
      // This is transposed which is equivalent to performing an inverse
      // if the matrix is orthonormalized (in this case, it is).
      //    val orientation = Matrix4x4(
      //      right.x, up.x, cameraDirection.x, 0,
      //      right.y, up.y, cameraDirection.y, 0,
      //      right.z, up.z, cameraDirection.z, 0,
      //      0, 0, 0, 1)
      val orientation = Matrix4x4d(
        right.x, right.y, right.z, 0,
        up.x, up.y, up.z, 0,
        cameraDirection.x, cameraDirection.y, cameraDirection.z, 0,
        0, 0, 0, 1
      )

      // Create a 4x4 translation matrix.
      // The eye position is negated which is equivalent
      // to the inverse of the translation matrix.
      // T(v)^-1 == T(-v)
      val translation = Matrix4x4d(
        1, 0, 0, -eye.x,
        0, 1, 0, -eye.y,
        0, 0, 1, -eye.z,
        0, 0, 0, 1)

      // Combine the orientation and translation to compute
      // the final view matrix
      orientation * translation
    }
  }

  def lookAt(eye: Vector3, target: Vector3, verticallyUp: Vector3): Matrix4x4d = {
    // This is really the reverse of where the camera is pointing
    val cameraDirection = (eye - target).normalize
    // https://learnopengl.com/#!Getting-started/Camera
    val right = verticallyUp.crossProduct(cameraDirection).normalize
    val up = cameraDirection.crossProduct(right)

    // Create a 4x4 orientation matrix from the right, up, and forward vectors
    // This is transposed which is equivalent to performing an inverse
    // if the matrix is orthonormalized (in this case, it is).
    //    val orientation = Matrix4x4(
    //      right.x, up.x, cameraDirection.x, 0,
    //      right.y, up.y, cameraDirection.y, 0,
    //      right.z, up.z, cameraDirection.z, 0,
    //      0, 0, 0, 1)
    val orientation = Matrix4x4d(
      right.x, right.y, right.z, 0,
      up.x, up.y, up.z, 0,
      cameraDirection.x, cameraDirection.y, cameraDirection.z, 0,
      0, 0, 0, 1
    )

    // Create a 4x4 translation matrix.
    // The eye position is negated which is equivalent
    // to the inverse of the translation matrix.
    // T(v)^-1 == T(-v)
    val translation = Matrix4x4d(
      1, 0, 0, -eye.x,
      0, 1, 0, -eye.y,
      0, 0, 1, -eye.z,
      0, 0, 0, 1)

    // Combine the orientation and translation to compute
    // the final view matrix
    orientation * translation
  }
}