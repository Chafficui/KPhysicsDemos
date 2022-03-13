package demo.tests

import demo.window.TestBedWindow
import library.dynamics.World
import library.explosions.RaycastExplosion
import library.math.Vec2

object RaycastExplosionTest {
    @JvmField
    val text = arrayOf("Raycast Explosions:", "Left click: casts an explosion")
    @JvmField
    var active = false
    @JvmField
    var r: RaycastExplosion? = null
    @JvmStatic
    fun load(testBedWindow: TestBedWindow) {
        testBedWindow.world = World(Vec2(.0, -9.81))
        testBedWindow.setCamera(Vec2(.0, 300.0), 2.0)
        val temp = testBedWindow.world
        active = true
        testBedWindow.buildExplosionDemo()
        r = RaycastExplosion(Vec2(.0, 1.0), 100, 1000, temp.bodies)
        testBedWindow.add(r)
    }
}