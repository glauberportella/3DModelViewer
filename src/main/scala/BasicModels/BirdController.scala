package BasicModels

import java.util

import enterthematrix.{Matrix4x4, Vector3}

import scala.collection.immutable.Seq

class BirdController(val view: BirdView, val initialPos: Vector3, val initialDir: Vector3) {
  private var model = Bird(initialPos.toVector3d, initialDir.toVector3d)

  def getModel: Bird = model
  def getView: BirdView = view

  def draw(projectionMatrix: Matrix4x4, cameraTranslate: Matrix4x4, shader: Shader): Unit = view.draw(projectionMatrix, cameraTranslate, shader)

  def doOneTick(allBirds: util.List[Bird], constraints: BoundingBox): Seq[BirdDecision] = {
    val decisions = model.makeDecisions(allBirds, constraints)
    resolveDecisions(decisions, constraints)
    decisions
  }

  private def resolveDecisions(decisions: Seq[BirdDecision], constraints: BoundingBox) = {
    for (decision <- decisions) {
      model = decision.resolve(model)
    }
    view.setPos(model.pos.toVector3)
  }

}