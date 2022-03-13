package demo.tests

import demo.window.TestBedWindow
import library.dynamics.Body
import library.dynamics.World
import library.geometry.Polygon
import library.math.Vec2

object Restitution {
    @JvmField
    val text = arrayOf("Restitution:")
    @JvmStatic
    fun load(testBedWindow: TestBedWindow) {
        testBedWindow.world = World(Vec2(.0, -9.81))
        val temp = testBedWindow.world

        //Three squares fall onto a a static platform
        run {
            val b = temp.addBody(Body(Polygon(200.0, 10.0), .0, -100.0))
            b.setDensity(0.0)
            for (i in 0..2) {
                val b1 = temp.addBody(Body(Polygon(30.0, 30.0), (-100 + i * 100).toDouble(), 100.0))
                b1.restitution = i / 3.0
            }
        }
    }
}