package demo.tests

import de.chaffic.collision.Arbiter.Companion.isPointInside
import de.chaffic.collision.bodies.CollisionBodyInterface
import de.chaffic.dynamics.World
import de.chaffic.dynamics.bodies.PhysicalBodyInterface
import de.chaffic.math.Mat2
import de.chaffic.math.Vec2
import de.chaffic.rays.Ray
import demo.window.TestBedWindow

object Raycast {
    @JvmField
    val text = arrayOf("Raycast:")
    @JvmField
    var active = false
    @JvmStatic
    fun load(testBedWindow: TestBedWindow) {
        testBedWindow.world = World(Vec2(.0, -9.81))
        testBedWindow.setCamera(Vec2(-100.0, -20.0), 3.3)
        active = true
        var isValid = false
        while (!isValid) {
            isValid = true
            testBedWindow.generateBoxOfObjects()
            for (b in testBedWindow.world.bodies) {
                if(b !is CollisionBodyInterface || b !is PhysicalBodyInterface) continue
                if (isPointInside(b, Vec2())) {
                    isValid = false
                    testBedWindow.world.clearWorld()
                    break
                }
            }
        }
        val r = Ray(Vec2(), Vec2(.0, 1.0), 1000)
        testBedWindow.add(r)
    }

    @JvmStatic
    fun action(r: Ray) {
        val u = Mat2()
        u.set(-0.0006)
        u.mul(r.direction)
    }
}