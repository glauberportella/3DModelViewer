import enterthematrix.{Matrix4x4, Vector4}
import org.scalatest.{FunSuite, PrivateMethodTester}

class Matrix4x4Spec extends FunSuite with PrivateMethodTester {
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

  test("multiplyInternal") {
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
    assert ((Matrix4x4 invokePrivate mi(m1,m2,0,0)) === 20)
    assert ((Matrix4x4 invokePrivate mi(m1,m2,0,1)) === 8)
    assert ((Matrix4x4 invokePrivate mi(m1,m2,0,2)) === 12)
    assert ((Matrix4x4 invokePrivate mi(m1,m2,1,0)) === 25)
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
}
