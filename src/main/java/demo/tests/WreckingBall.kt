package demo.tests

import de.chaffic.dynamics.Body
import de.chaffic.dynamics.World
import de.chaffic.geometry.Circle
import de.chaffic.geometry.Polygon
import de.chaffic.joints.Joint
import de.chaffic.joints.JointToPoint
import de.chaffic.math.Vec2
import demo.window.TestBedWindow

object WreckingBall {
    @JvmField
    val text = arrayOf("Wrecking Ball")
    @JvmStatic
    fun load(testBedWindow: TestBedWindow) {
        testBedWindow.world = World(Vec2(.0, -9.81))
        val temp = testBedWindow.world
        testBedWindow.setCamera(Vec2(.0, 100.0), 1.7)
        run {
            for (x in 0..9) {
                for (y in 0..9) {
                    val b = Body(Polygon(10.0, 10.0), (110 + x * 20).toDouble(), (y * 20).toDouble())
                    temp.addBody(b)
                }
            }
            val b = Body(Polygon(100.0, 10.0), 200.0, -20.0)
            b.density = (0.0)
            temp.addBody(b)
        }
        run {
            val b2 = Body(Circle(40.0), -250.0, 320.0)
            b2.density = (2.0)
            temp.addBody(b2)
            val j: Joint = JointToPoint(b2, Vec2(.0, 320.0), 250.0, 200.0, 100.0, true, Vec2())
            temp.addJoint(j)
        }
    }
}