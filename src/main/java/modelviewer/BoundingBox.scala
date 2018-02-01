package BasicModels

import enterthematrix.Vector3d
;

case class BoundingBox(minX: Float, maxX: Float, minY: Float, maxY: Float, minZ: Float, maxZ: Float) {
  def isOutside(pos: Vector3d): Boolean = {
    pos.x > maxX || pos.x < minX || pos.y > maxY || pos.y < minY || pos.z > maxZ || pos.z < minZ
  }

  def getWidth = maxX - minX
}
