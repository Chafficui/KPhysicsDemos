package demo.tests

import demo.window.TestBedWindow
import library.dynamics.Body
import library.dynamics.World
import library.geometry.Circle
import library.geometry.Polygon
import library.joints.Joint
import library.joints.JointToBody
import library.joints.JointToPoint
import library.math.Vec2

object Trebuchet {
    @JvmField
    val text = arrayOf("Trebuchet", "B: break tether to payload")
    @JvmField
    var active = false
    @JvmStatic
    fun load(testBedWindow: TestBedWindow) {
        testBedWindow.world = World(Vec2(.0, -9.81))
        val temp = testBedWindow.world
        testBedWindow.setCamera(Vec2(100.0, 200.0), 2.0)
        active = true
        val ground = Body(Polygon(10000.0, 2000.0), .0, -2040.0)
        ground.setDensity(0.0)
        temp.addBody(ground)
        val arm = Body(Polygon(50.0, 2.0), .0, .0)
        arm.orientation = 0.78
        arm.setDensity(2.0)
        temp.addBody(arm)
        val j1: Joint = JointToPoint(arm, Vec2(20.469, 20.469), .0, 1000.0, 100.0, true, Vec2(28.947, .0))
        temp.addJoint(j1)
        val counterWeight = Body(Circle(5.0), 35.355, 21.0)
        counterWeight.setDensity(133.0)
        temp.addBody(counterWeight)
        val j2: Joint = JointToBody(arm, counterWeight, 20.0, 7000.0, 10.0, false, Vec2(50.0, .0), Vec2(.0, .0))
        temp.addJoint(j2)
        val payload = Body(Circle(5.0), 43.592, -35.0)
        payload.dynamicFriction = .0
        payload.staticFriction = .0
        payload.setDensity(1.0)
        temp.addBody(payload)
        val j3: Joint = JointToBody(arm, payload, 79.0, 100.0, 1.0, true, Vec2(-50.0, .0), Vec2())
        temp.addJoint(j3)
        testBedWindow.createPyramid(10, 1500, -40)
        TestBedWindow.HERTZ = 400.0
    }
}