package enterthematrix

case class Vector4d(x: Double, y: Double, z: Double, w: Double) {
  def +(v: Double) = Vector4d(x + v, y + v, z + v, w + v)
  def -(v: Double) = Vector4d(x - v, y - v, z - v, w - v)
  def *(v: Double) = Vector4d(x * v, y * v, z * v, w * v)
  def /(v: Double) = Vector4d(x / v, y / v, z / v, w / v)
  def negate(): Vector4d = this * -1
  def +(v: Vector4d) = Vector4d(x + v.x, y + v.y, z + v.z, w + v.w)
  def -(v: Vector4d) = Vector4d(x - v.x, y - v.y, z - v.z, w - v.w)
//  def *(v: Vector4) = Vector4(x * v.x + y * v.y + z * v.z + w * v.w)
  def length: Double = Math.sqrt(x * x + y * y + z * z + w * w)
  def normalize = {
    val len = length.toDouble
    Vector4d(x / len, y / len, z / len, w / len)
  }
  def dotProduct(v: Vector4d) = {
    // Not sure which is correct definition, have found both defined
//    x * v.x + y * v.y + z * v.z + w * v.w

    val len1 = length
    val len2 = v.length
    val cosineAngleBetweenVectors = (x * v.x + y * v.y + z * v.z + w * v.w) / (len1 * len2)
    len1 * len2 * cosineAngleBetweenVectors
  }
  def crossProduct(b: Vector4d) = Vector4d(y * b.z - z * b.y, z * b.x - x * b.z, x * b.y - y * b.x, 1)
  def toVector3 = Vector3d(x, y, z)
}

object Vector4d {
  def fill(v: Double) = Vector4d(v, v, v, 1)
}

