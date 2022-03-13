package demo.tests

import demo.window.TestBedWindow
import library.dynamics.World
import library.math.Vec2
import library.rays.ShadowCasting
import java.awt.Graphics2D

object LineOfSight {
    @JvmField
    val text = arrayOf("Line of sight:", "Mouse: Move mouse to change position of raycast")
    @JvmField
    var active = false
    @JvmField
    var b: ShadowCasting? = null
    @JvmStatic
    fun load(testBedWindow: TestBedWindow) {
        testBedWindow.world = World(Vec2(.0, -9.81))
        testBedWindow.setCamera(Vec2(-120.0, 20.0), 3.3)
        active = true
        testBedWindow.generateBoxOfObjects()
        b = ShadowCasting(Vec2(-1000.0, .0), 11000)
        testBedWindow.add(b)
    }

    @JvmStatic
    fun drawInfo(g: Graphics2D, x: Int, y: Int) {
        g.drawString("No of rays: " + b!!.noOfRays, x, y)
    }
}