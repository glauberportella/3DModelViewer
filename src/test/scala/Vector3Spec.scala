import enterthematrix.Vector3
import org.scalactic.TolerantNumerics
import org.scalatest.FunSuite

class Vector3Spec extends FunSuite {
  val epsilon = 1e-4f

  implicit val doubleEq = TolerantNumerics.tolerantDoubleEquality(epsilon)

  test("dot product") {
    assert (Vector3(0.6f, -0.8f, 0).dotProduct(Vector3(0, 1, 0)) === -0.8)
//    assert (Vector3(0, 0, -1).dotProduct(Vector3(0, 1, 0)) === -0.8)
  }

  test("right angle") {
    assert (Vector3(0.6f, -0.8f, 0).dotProduct(Vector3(0, 1, 0)) === -0.8)
//        assert (Vector3(0, 1, 0).dotProduct(Vector3(1, 0, 0)) === 90)
  }

  test("fill") {
    assert (Vector3.fill(1) == Vector3(1, 1, 1))
  }
}
