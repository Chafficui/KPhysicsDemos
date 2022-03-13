package demo.tests

import demo.window.TestBedWindow
import library.dynamics.Body
import library.dynamics.World
import library.geometry.Polygon
import library.math.Vec2

object Friction {
    @JvmField
    val text = arrayOf("Friction:")
    @JvmStatic
    fun load(testBedWindow: TestBedWindow) {
        testBedWindow.world = World(Vec2(.0, -9.81))
        val world = testBedWindow.world
        testBedWindow.setCamera(Vec2(.0, .0), 1.4)
        run {
            for (i in 0..2) {
                val ramp =
                    world.addBody(Body(Polygon(200.0, 10.0), (-200 + 200 * i).toDouble(), (200 - 180 * i).toDouble()))
                ramp.orientation = -0.2
                ramp.setDensity(0.0)
            }
            for (i in 0..2) {
                val box =
                    world.addBody(Body(Polygon(20.0, 20.0), (-290 + 200 * i).toDouble(), (250 - 180 * i).toDouble()))
                box.orientation = -0.2
                box.staticFriction = 0.5 - i * 0.1
                box.dynamicFriction = 0.3 - i * 0.1
                box.setDensity(1.0)
            }
        }
    }
}