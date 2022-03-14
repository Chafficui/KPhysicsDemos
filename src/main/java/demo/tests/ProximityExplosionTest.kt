package demo.tests

import de.chaffic.dynamics.World
import de.chaffic.explosions.ProximityExplosion
import de.chaffic.math.Vec2
import demo.window.TestBedWindow

object ProximityExplosionTest {
    @JvmField
    val text = arrayOf("Proximity Explosions:", "Left click: casts an explosion")
    @JvmField
    var active = false
    @JvmField
    var p: ProximityExplosion? = null
    @JvmStatic
    fun load(testBedWindow: TestBedWindow) {
        active = true
        testBedWindow.world = World(Vec2(.0, -9.81))
        testBedWindow.setCamera(Vec2(.0, 300.0), 2.0)
        testBedWindow.buildExplosionDemo()
        p = ProximityExplosion(Vec2(), 200)
        testBedWindow.add(p)
    }
}