package demo.tests

import de.chaffic.dynamics.Body
import de.chaffic.dynamics.World
import de.chaffic.geometry.Circle
import de.chaffic.geometry.Polygon
import de.chaffic.math.Vec2
import demo.window.TestBedWindow

object BouncingBall {
    @JvmField
    val text = arrayOf("Bouncing Balls:")
    @JvmStatic
    fun load(testBedWindow: TestBedWindow) {
        testBedWindow.world = World(Vec2(.0, -9.81))
        val temp = testBedWindow.world
        testBedWindow.setCamera(Vec2(.0, 200.0), 1.3)
        run {
            for (i in 0..9) {
                val b1 = Body(Circle(5.0), -81.0 + i * 17, 410.0)
                temp.addBody(b1)
            }
        }
        run {
            for (y in 0..19) {
                for (x in 0..9) {
                    var b: Body
                    b = if (y % 2 == 1) {
                        Body(Circle(2.0), (-10 + 5 * 20 - x * 20).toDouble(), (10 + y * 20).toDouble())
                    } else {
                        Body(Circle(2.0), (5 * 20 - x * 20).toDouble(), (10 + y * 20).toDouble())
                    }
                    b.density = 0.0
                    temp.addBody(b)
                }
            }
        }
        val b1 = Body(Polygon(5.0, 190.0), -100.0, 190.0)
        b1.density = (0.0)
        temp.addBody(b1)
        val b2 = Body(Polygon(5.0, 190.0), 100.0, 190.0)
        b2.density = (0.0)
        temp.addBody(b2)
        val b3 = Body(Polygon(105.0, 5.0), .0, -5.0)
        b3.density = (0.0)
        temp.addBody(b3)
    }
}