package demo.tests

import de.chaffic.dynamics.Body
import de.chaffic.dynamics.World
import de.chaffic.geometry.Circle
import de.chaffic.joints.Joint
import de.chaffic.joints.JointToPoint
import de.chaffic.math.Vec2
import demo.window.TestBedWindow

object NewtonsCradle {
    @JvmField
    val text = arrayOf("Newtons Cradle:")
    @JvmStatic
    fun load(testBedWindow: TestBedWindow) {
        testBedWindow.world = World(Vec2(.0, -9.81))
        val temp = testBedWindow.world
        testBedWindow.setCamera(Vec2(), 2.0)
        val radius = 40.0
        val noOfCircles = 8
        val spread = (noOfCircles - 1) * 80 / 2.0
        var minX: Double = -spread + 40
        run {
            for (i in 0 until noOfCircles) {
                val x = minX + i * 80
                val b = Body(Circle(radius), x, -100.0)
                setBody(temp, x, b)
            }
        }
        run {
            minX -= 80.0
            val b = Body(Circle(radius), minX - 300, 200.0)
            setBody(temp, minX, b)
        }
    }

    private fun setBody(temp: World, x: Double, b: Body) {
        b.restitution = 1.0
        b.staticFriction = .0
        b.dynamicFriction = .0
        temp.addBody(b)
        val j: Joint = JointToPoint(b, Vec2(x, 200.0), 300.0, 200000.0, 1000.0, true, Vec2())
        temp.addJoint(j)
    }
}