package demo.tests

import de.chaffic.dynamics.Body
import de.chaffic.dynamics.World
import de.chaffic.geometry.Circle
import de.chaffic.geometry.Polygon
import de.chaffic.joints.Joint
import de.chaffic.joints.JointToBody
import de.chaffic.math.Vec2
import demo.window.TestBedWindow

object Chains {
    @JvmField
    val text = arrayOf("Chains:")
    @JvmStatic
    fun load(testBedWindow: TestBedWindow) {
        testBedWindow.world = World(Vec2(.0, -9.81))
        val temp = testBedWindow.world
        testBedWindow.setCamera(Vec2(.0, -50.0), 1.4)
        val b = Body(Circle(60.0), .0, .0)
        b.density = (0.0)
        temp.addBody(b)
        val maxChainLength = 20
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