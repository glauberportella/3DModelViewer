import enterthematrix.{Matrix4x4, Vector4}
import org.scalactic.{Equality, TolerantNumerics}
import org.scalatest.{FunSuite, PrivateMethodTester}

class Matrix4x4Spec extends FunSuite with PrivateMethodTester {
  val epsilon = 1e-4f

  implicit val dblEquality: Equality[Double] = TolerantNumerics.tolerantDoubleEquality(epsilon)

  test("get") {
    val m2 = Matrix4x4(
      4, 2, 1, 0,
      2, 0, 4, 0,
      9, 4, 2, 0,
      0, 0, 0, 0)
    assert (m2.get(0,0) === 4)
    assert (m2.get(0,1) === 2)
    assert (m2.get(1,0) === 2)
    assert (m2.get(2,0) === 9)
    assert (m2.get(0,2) === 1)
  }

  ignore("multiplyInternal") {
    val m1 = Matrix4x4(
      4, 2, 0, 0,
      0, 8, 1, 0,
      0, 1, 0, 0,
      0, 0, 0, 0)
    val m2 = Matrix4x4(
      4, 2, 1, 0,
      2, 0, 4, 0,
      9, 4, 2, 0,
      0, 0, 0, 0)
    val mi = PrivateMethod[Double]('multiplyInternal)
    assert ((Matrix4x4 invokePrivate mi(m1,m2,0,0)) === 20.0f)
    assert ((Matrix4x4 invokePrivate mi(m1,m2,0,1)) === 8.0f)
    assert ((Matrix4x4 invokePrivate mi(m1,m2,0,2)) === 12.0f)
    assert ((Matrix4x4 invokePrivate mi(m1,m2,1,0)) === 25.0f)
  }


  test("multiply") {
    val m1 = Matrix4x4(
      4, 2, 0, 0,
      0, 8, 1, 0,
      0, 1, 0, 0,
      0, 0, 0, 0)
    val m2 = Matrix4x4(
      4, 2, 1, 0,
      2, 0, 4, 0,
      9, 4, 2, 0,
      0, 0, 0, 0)
    val m3 = m1 * m2
    assert (m3 == Matrix4x4(
      20, 8, 12, 0,
      25, 4, 34, 0,
      2, 0, 4, 0,
      0, 0, 0, 0
    ))
  }

  test("multiply by vector, identity") {
    val m1 = Matrix4x4.identity
    val m2 = Vector4(1, 2, 3, 1)
    val m3 = m1 * m2
    assert (m3 == m2)
  }

  test("multiply by vector, scale") {
    val m1 = Matrix4x4.scale(3)
    val m2 = Vector4(1, 2, 3, 1)
    val m3 = m1 * m2
    assert (m3 == Vector4(3, 6, 9, 1))
  }

  test("translate") {
    val m1 = Matrix4x4.translate(11, 12, 33)
    val m2 = Vector4(1, 2, 3, 1)
    val m3 = m1 * m2
    assert (m3 == Vector4(1 + 11, 2 + 12, 3 + 33, 1))
  }

  test("multiply translate scale") {
    val m1 = Matrix4x4.translate(1, 2, 3)
    val m2 = Matrix4x4.scale(2)
    val m3 = m1 * m2
    assert (m3 == Matrix4x4(
      2,0,0,1,
      0,2,0,2,
      0,0,2,3,
      0,0,0,1
    ))
  }

  test("multiply translate scale, multiply by vector") {
    val m1 = Matrix4x4.translate(1, 2, 3)
    val m2 = Matrix4x4.scale(2)
    val v = Vector4(5, 10, 15, 1)
    val m3 = (m1 * m2) * v
    assert (m3 == Vector4(
      2 * 5 + 1,
      2 * 10 + 2,
      2 * 15 + 3,
      1
    ))
  }

  ignore("rotate x axis") {
    val m1 = Matrix4x4.rotateAroundXAxis(90)
    assert (m1.get(0,0) === 1)
//    assert (m1.get(1,2) === 0)
//    assert (m1.get(2,1) === 0)
    assert (m1.get(1,1) === 1)
    assert (m1.get(2,2) === 1)
    assert (m1.get(3,3) === 1)
  }

  ignore("rotate x axis, effect on vector") {
    val m1 = Matrix4x4.rotateAroundXAxis(90)
    val v = Vector4(0.5f, 0.5f, 0f, 1)
    val done = m1 * v
    assert (done == Vector4(0.5f, 0, 0, 1))
  }

  test("rotate rotation vector vertical") {
    val initial = enterthematrix.Vector3(0,-1,0).toVector4 // looking down
    val m1 = Matrix4x4.rotateAroundXAxis(90)

    val result = m1 * initial

    val should = enterthematrix.Vector3(0, 0, -1).toVector4 // looking out of screen
    assert (result.x === should.x)
  assert (2.00001 === 2.0)
  assert (-0.00000000000000006123234 === 0.0)
//    import org.scalactic.Tolerance._
    assert (Math.abs(result.y - should.y) <= 0.0001)
//    assert (result.y === 0.0 +- 0.1)
//    assert (result.y === should.y)
    assert (result.z === should.z)
  }

  test("rotate rotation vector vertical backwards") {
    val initial = enterthematrix.Vector3(0,-1,0).toVector4 // looking down
    val m1 = Matrix4x4.rotateAroundXAxis(-90)

    val result = m1 * initial

    val should = enterthematrix.Vector3(0, 0, 1).toVector4 // looking into screen
    assert (result.x === should.x)
    assert (Math.abs(result.y - should.y) <= 0.0001)
//    assert (result.y === should.y)
    assert (result.z === should.z)
  }
}
