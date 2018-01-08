package BasicModels

import java.util

import enterthematrix.{Matrix4x4, Vector3}

import scala.collection.immutable.Seq
import scala.collection.mutable.ArrayBuffer

trait BirdDecision {
  def resolve(bird: Bird): Bird
}

case class BirdDecisionHeadForwards(speed: Float) extends BirdDecision {
  override def resolve(bird: Bird): Bird = {
    bird.copy(pos = bird.pos  + (bird.direction * speed))
  }
}

case class BirdDecisionVeerAwayFromZMax(angleDeg: Float) extends BirdDecision {
  override def resolve(bird: Bird): Bird = {
    val newDir = Matrix4x4.rotateAroundYAxis(angleDeg) * bird.direction.toVector4
    bird.copy(direction = newDir.toVector3)
  }
}

case class BirdDecisionVeerAwayFromZMin(angleDeg: Float) extends BirdDecision {
  override def resolve(bird: Bird): Bird = {
    val newDir = Matrix4x4.rotateAroundYAxis(angleDeg) * bird.direction.toVector4
    bird.copy(direction = newDir.toVector3)
  }
}

case class Bird(pos: Vector3, direction: Vector3) {
  def makeDecisions(allBirds: util.List[Bird], constraints: BoundingBox): Seq[BirdDecision] = {
    val decisions = new ArrayBuffer[BirdDecision]()
    val speed = 0.1f
    val tooCloseToEdge = 0.1f
    val maxTurningAngleDeg = 0.1f

    if (constraints.maxZ - pos.z <= tooCloseToEdge) decisions += BirdDecisionVeerAwayFromZMax(maxTurningAngleDeg)
    if (pos.z - constraints.minZ <= tooCloseToEdge) decisions += BirdDecisionVeerAwayFromZMin(maxTurningAngleDeg)
    decisions += new BirdDecisionHeadForwards(speed)

    decisions.toList
  }
}