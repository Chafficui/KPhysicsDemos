package demo.tests

import de.chaffic.dynamics.Body
import de.chaffic.dynamics.World
import de.chaffic.geometry.Circle
import de.chaffic.geometry.Polygon
import de.chaffic.math.Vec2
import demo.window.TestBedWindow

object BouncyBall {
    @JvmField
    val text = arrayOf("Bouncy Ball:")

    @JvmStatic
    fun load(testBedWindow: TestBedWindow) {
        testBedWindow.world = World(Vec2(.0, -9.81))
        val world = testBedWindow.world
        testBedWindow.setCamera(Vec2(.0, .0), 2.0)

        val bouncyBall = Body(Circle(20.0), .0, 200.0)
        bouncyBall.restitution =1.0

        val plattform = Body(Polygon(600.0, 20.0), .0, -300.0)
        plattform.density = (.0)

        world.addBody(bouncyBall)
        world.addBody(plattform)
    }
}