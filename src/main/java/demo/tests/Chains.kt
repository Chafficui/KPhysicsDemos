package demo.tests

import demo.window.TestBedWindow
import library.dynamics.Body
import library.dynamics.World
import library.geometry.Circle
import library.geometry.Polygon
import library.joints.Joint
import library.joints.JointToBody
import library.math.Vec2

object Chains {
    @JvmField
    val text = arrayOf("Chains:")
    @JvmStatic
    fun load(testBedWindow: TestBedWindow) {
        testBedWindow.world = World(Vec2(.0, -9.81))
        val temp = testBedWindow.world
        testBedWindow.setCamera(Vec2(.0, -50.0), 1.4)
        val b = Body(Circle(60.0), .0, .0)
        b.setDensity(0.0)
        temp.addBody(b)
        val maxChainLength = 10
        val bodyList = arrayOfNulls<Body>(maxChainLength)
        for (i in 0 until maxChainLength) {
            val b2 = Body(Polygon(20.0, 5.0), -20 + 40.0 * maxChainLength / 2 - 40 * i, 200.0)
            temp.addBody(b2)
            bodyList[i] = b2
            if (i != 0) {
                val j1: Joint =
                    JointToBody(bodyList[i - 1]!!, bodyList[i]!!, 1.0, 200.0, 10.0, true, Vec2(-20.0, .0), Vec2(20.0, .0))
                temp.addJoint(j1)
            }
        }
    }
}