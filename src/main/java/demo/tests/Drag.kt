package demo.tests

import demo.window.TestBedWindow
import library.dynamics.Body
import library.dynamics.World
import library.geometry.Circle
import library.geometry.Polygon
import library.math.Vec2

object Drag {
    @JvmField
    val text = arrayOf("Drag:")
    @JvmStatic
    fun load(testBedWindow: TestBedWindow) {
        testBedWindow.world = World(Vec2(.0, -9.81))
        val temp = testBedWindow.world
        for (i in 0..12) {
            val b1 = Body(Circle(10.0), (-190 + 30 * i).toDouble(), 100.0)
            b1.linearDampening = 1.0 * i
            temp.addBody(b1)
            b1.restitution = .0
        }
        val b4 = Body(Polygon(200.0, 10.0), .0, -100.0)
        b4.setDensity(0.0)
        b4.restitution = 1.0
        temp.addBody(b4)
    }
}