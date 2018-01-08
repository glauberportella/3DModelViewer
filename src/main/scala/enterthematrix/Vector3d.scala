package enterthematrix

case class Vector3d(x: Double, y: Double, z: Double) {
  def +(v: Double) = Vector3d(x + v, y + v, z + v)
  def -(v: Double) = Vector3d(x - v, y - v, z - v)
  def *(v: Double) = Vector3d(x * v, y * v, z * v)
  def /(v: Double) = Vector3d(x / v, y / v, z / v)
  def negate(): Vector3d = this * -1
  def +(v: Vector3d) = Vector3d(x + v.x, y + v.y, z + v.z)
  def -(v: Vector3d) = Vector3d(x - v.x, y - v.y, z - v.z)
  def length: Double = Math.sqrt(x * x + y * y + z * z)
  def normalize = {
    val len = length.toDouble
    Vector3d(x / len, y / len, z / len)
  }
  def dotProduct(v: Vector3d) = {
    val len1 = length
    val len2 = v.length
    val cosineAngleBetweenVectors = (x * v.x + y * v.y + z * v.z) / (len1 * len2)
    len1 * len2 * cosineAngleBetweenVectors
  }
  def crossProduct(b: Vector3d) = Vector3d(y * b.z - z * b.y, z * b.x - x * b.z, x * b.y - y * b.x)
  def toVector4 = Vector4d(x, y, z, 1.0f)
  def toVector3 = Vector3(x.toFloat, y.toFloat, z.toFloat)
}

object Vector3d {
  def fill(v: Double) = Vector3d(v, v, v)
}

