package demo.tests

import demo.window.TestBedWindow
import library.dynamics.Body
import library.dynamics.World
import library.geometry.Circle
import library.geometry.Polygon
import library.math.Vec2

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
        plattform.setDensity(.0)

        world.addBody(bouncyBall)
        world.addBody(plattform)
    }
}