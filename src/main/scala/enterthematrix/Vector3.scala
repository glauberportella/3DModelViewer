package enterthematrix

case class Vector3(x: Float, y: Float, z: Float) {
  def +(v: Float) = Vector3(x + v, y + v, z + v)
  def -(v: Float) = Vector3(x - v, y - v, z - v)
  def *(v: Float) = Vector3(x * v, y * v, z * v)
  def /(v: Float) = Vector3(x / v, y / v, z / v)
  def negate(): Vector3 = this * -1
  def +(v: Vector3) = Vector3(x + v.x, y + v.y, z + v.z)
  def -(v: Vector3) = Vector3(x - v.x, y - v.y, z - v.z)
  def length: Double = Math.sqrt(x * x + y * y + z * z)
  def normalize = {
    val len = length.toFloat
    Vector3(x / len, y / len, z / len)
  }
  def dotProduct(v: Vector3) = {
    val len1 = length
    val len2 = v.length
    val cosineAngleBetweenVectors = (x * v.x + y * v.y + z * v.z) / (len1 * len2)
    len1 * len2 * cosineAngleBetweenVectors
  }
  def crossProduct(b: Vector3) = Vector3(y * b.z - z * b.y, z * b.x - x * b.z, x * b.y - y * b.x)
  def toVector4 = Vector4(x, y, z, 1.0f)
  def toVector3d = Vector3d(x, y, z)
}

object Vector3 {
  def fill(v: Float) = Vector3(v, v, v)
}

