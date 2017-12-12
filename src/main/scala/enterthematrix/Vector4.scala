package enterthematrix

case class Vector4(x: Float, y: Float, z: Float, w: Float) {
  def +(v: Float) = Vector4(x + v, y + v, z + v, w + v)
  def -(v: Float) = Vector4(x - v, y - v, z - v, w - v)
  def *(v: Float) = Vector4(x * v, y * v, z * v, w * v)
  def /(v: Float) = Vector4(x / v, y / v, z / v, w / v)
  def negate(): Vector4 = this * -1
  def +(v: Vector4) = Vector4(x + v.x, y + v.y, z + v.z, w + v.w)
  def -(v: Vector4) = Vector4(x - v.x, y - v.y, z - v.z, w - v.w)
  def length: Double = Math.sqrt(x * x + y * y + z * z + w * w)
  def normalize = {
    val len = length.toFloat
    Vector4(x / len, y / len, z / len, w / len)
  }
  def dotProduct(v: Vector4) = {
    val len1 = length
    val len2 = v.length
    val cosineAngleBetweenVectors = (x * v.x + y * v.y + z * v.z + w * v.w) / (len1 * len2)
    len1 * len2 * cosineAngleBetweenVectors
  }
//  def crossProduct(b: Vector4) = Vector(y * b.z - z * b.y, z * b.x - x * b.z, x * b.y - y * b.x)
}

object Vector4 {
  def fill(v: Float) = Vector4(v, v, v, v)
}

