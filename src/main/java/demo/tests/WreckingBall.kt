package demo.tests

import demo.window.TestBedWindow
import library.dynamics.Body
import library.dynamics.World
import library.geometry.Circle
import library.geometry.Polygon
import library.joints.Joint
import library.joints.JointToPoint
import library.math.Vec2

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
            b.setDensity(0.0)
            temp.addBody(b)
        }
        run {
            val b2 = Body(Circle(40.0), -250.0, 320.0)
            b2.setDensity(2.0)
            temp.addBody(b2)
            val j: Joint = JointToPoint(b2, Vec2(.0, 320.0), 250.0, 200.0, 100.0, true, Vec2())
            temp.addJoint(j)
        }
    }
}