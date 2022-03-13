package demo.tests

import demo.window.TestBedWindow
import library.collision.Arbiter.Companion.isPointInside
import library.dynamics.World
import library.math.Mat2
import library.math.Vec2
import library.rays.Ray

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