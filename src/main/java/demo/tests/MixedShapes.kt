package demo.tests

import de.chaffic.dynamics.Body
import de.chaffic.dynamics.World
import de.chaffic.geometry.Polygon
import de.chaffic.math.Vec2
import demo.window.TestBedWindow

object MixedShapes {
    @JvmField
    val text = arrayOf("Mixed Shapes:")
    @JvmStatic
    fun load(testBedWindow: TestBedWindow) {
        testBedWindow.world = World(Vec2(.0, -9.81))
        val temp = testBedWindow.world
        testBedWindow.setCamera(Vec2(-.0, 200.0), 2.2)

        //Polygon containers
        run {
            val b1 = Body(Polygon(15.0, 100.0), -300.0, .0)
            b1.density = (0.0)
            temp.addBody(b1)
            val b2 = Body(Polygon(15.0, 100.0), 300.0, .0)
            b2.density = (0.0)
            temp.addBody(b2)
            val b3 = Body(Polygon(315.0, 15.0), .0, -115.0)
            b3.density = (0.0)
            temp.addBody(b3)
        }
        testBedWindow.generateRandomObjects(Vec2(-280.0, -100.0), Vec2(280.0, 800.0), 30, 80)
    }
}