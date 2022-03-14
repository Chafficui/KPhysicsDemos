package demo.tests

import de.chaffic.dynamics.Body
import de.chaffic.dynamics.World
import de.chaffic.geometry.Polygon
import de.chaffic.math.Vec2
import demo.window.TestBedWindow

object SliceObjects {
    @JvmField
    val text = arrayOf("Slice Objects:", "Left click: Click two points on the demo window to slice objects")
    @JvmField
    var active = false
    @JvmStatic
    fun load(testBedWindow: TestBedWindow) {
        active = true
        testBedWindow.world = World(Vec2(.0, -9.81))
        val temp = testBedWindow.world
        testBedWindow.setCamera(Vec2(.0, 100.0), 1.3)
        val ground = Body(Polygon(10000.0, 2000.0), .0, -2040.0)
        ground.density = (0.0)
        temp.addBody(ground)
        testBedWindow.createTower(5, 0, -40)
        testBedWindow.scaleWorldFriction(0.4)
    }
}