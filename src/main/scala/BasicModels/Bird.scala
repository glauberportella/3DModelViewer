package BasicModels

import java.util

import enterthematrix.{Matrix4x4d, Vector3d}

import scala.collection.immutable.Seq
import scala.collection.mutable.ArrayBuffer

trait BirdDecision {
  def resolve(bird: Bird): Bird
}

case class BirdDecisionHeadForwards(speed: Double) extends BirdDecision {
  override def resolve(bird: Bird): Bird = {
    bird.copy(pos = bird.pos + (bird.direction * speed))
  }
}

case class BirdDecisionVeerAwayFromZ(angleDeg: Double) extends BirdDecision {
  override def resolve(bird: Bird): Bird = {
    val newDir = Matrix4x4d.rotateAroundYAxis(angleDeg) * bird.direction.toVector4
    bird.copy(direction = newDir.toVector3)
  }
}

case class BirdDecisionVeerAwayFromY(angleDeg: Double) extends BirdDecision {
  override def resolve(bird: Bird): Bird = {
    val newDir = Matrix4x4d.rotateAroundXAxis(angleDeg) * bird.direction.toVector4
    bird.copy(direction = newDir.toVector3)
  }
}

case class BirdDecisionVeerAwayFromX(angleDeg: Double) extends BirdDecision {
  override def resolve(bird: Bird): Bird = {
    val newDir = Matrix4x4d.rotateAroundZAxis(angleDeg) * bird.direction.toVector4
    bird.copy(direction = newDir.toVector3)
  }
}

//case class BirdDecisionVeerAwayFromZMin(angleDeg: Double) extends BirdDecision {
//  override def resolve(bird: Bird): Bird = {
//    val newDir = Matrix4x4.rotateAroundYAxis(angleDeg) * bird.direction.toVector4
//    bird.copy(direction = newDir.toVector3)
//  }
//}

case class Bird(pos: Vector3d, direction: Vector3d) {
  def makeDecisions(allBirds: util.List[Bird], constraints: BoundingBox): Seq[BirdDecision] = {
    val decisions = new ArrayBuffer[BirdDecision]()
    val speed = 0.1f
    val tooCloseToEdge = 0.1f
    val maxTurningAngleDeg = 10f

//    if (constraints.maxZ - pos.z <= tooCloseToEdge) decisions += BirdDecisionVeerAwayFromZ(maxTurningAngleDeg)
//    if (pos.z - constraints.minZ <= tooCloseToEdge) decisions += BirdDecisionVeerAwayFromZ(maxTurningAngleDeg)
    if (constraints.maxX - pos.x <= tooCloseToEdge) decisions += BirdDecisionVeerAwayFromX(maxTurningAngleDeg)
    if (pos.x - constraints.minX <= tooCloseToEdge) decisions += BirdDecisionVeerAwayFromX(maxTurningAngleDeg)
//    if (constraints.maxY - pos.y <= tooCloseToEdge) decisions += BirdDecisionVeerAwayFromY(maxTurningAngleDeg)
//    if (pos.y - constraints.minY <= tooCloseToEdge) decisions += BirdDecisionVeerAwayFromY(maxTurningAngleDeg)

    decisions += new BirdDecisionHeadForwards(speed)

    decisions.toList
  }
}