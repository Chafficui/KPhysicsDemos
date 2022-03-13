package demo.utils

import demo.window.Camera
import library.dynamics.Body
import library.dynamics.World
import library.explosions.ProximityExplosion
import library.explosions.RayScatter
import library.explosions.RaycastExplosion
import library.geometry.Circle
import library.geometry.Polygon
import library.joints.Joint
import library.joints.JointToBody
import library.joints.JointToPoint
import library.math.Vec2
import library.rays.Ray
import library.rays.ShadowCasting
import library.rays.Slice
import java.awt.Graphics2D
import java.awt.geom.Ellipse2D
import java.awt.geom.Line2D
import java.awt.geom.Path2D

object Painter {

    /**
     * Debug draw method for all polygons generated and rays.
     *
     * @param g             Graphics2D object to draw to
     * @param paintSettings Colour settings to draw the objects to screen with
     * @param camera        Camera class used to convert points from world space to view space
     */
    @JvmStatic
    fun shadowDraw(g: Graphics2D, paintSettings: ColourSettings, camera: Camera, shadowCasting: ShadowCasting) {
        for (i in shadowCasting.rayData.indices) {
            val ray1 = shadowCasting.rayData[i].ray
            val ray2 = shadowCasting.rayData[if (i + 1 == shadowCasting.rayData.size) 0 else i + 1].ray
            g.color = paintSettings.shadow
            val s = Path2D.Double()
            val worldStartPoint = camera.convertToScreen(shadowCasting.startPoint)
            s.moveTo(worldStartPoint.x, worldStartPoint.y)
            if (ray1.rayInformation != null) {
                val point1 = camera.convertToScreen(ray1.rayInformation!!.coordinates)
                s.lineTo(point1.x, point1.y)
            }
            if (ray2.rayInformation != null) {
                val point2 = camera.convertToScreen(ray2.rayInformation!!.coordinates)
                s.lineTo(point2.x, point2.y)
            }
            s.closePath()
            g.fill(s)
        }
    }

    /**
     * Debug draw method for slice object.
     *
     * @param g             Graphics2D object to draw to
     * @param paintSettings Colour settings to draw the objects to screen with
     * @param camera        Camera class used to convert points from world space to view space
     */
    @JvmStatic
    fun sliceDraw(g: Graphics2D, paintSettings: ColourSettings, camera: Camera, slice: Slice) {
        g.color = paintSettings.projectedRay
        val epicenter = camera.convertToScreen(slice.startPoint)
        val endPoint = camera.convertToScreen(slice.direction.scalar(slice.distance).plus(slice.startPoint))
        g.draw(Line2D.Double(epicenter.x, epicenter.y, endPoint.x, endPoint.y))
        g.color = paintSettings.rayToBody
        for (i in slice.intersectingBodiesInfo.indices) {
            if ((i + 1) % 2 == 0) {
                val intersection1 = camera.convertToScreen(slice.intersectingBodiesInfo[i - 1].coordinates)
                val intersection2 = camera.convertToScreen(slice.intersectingBodiesInfo[i].coordinates)
                g.draw(Line2D.Double(intersection2.x, intersection2.y, intersection1.x, intersection1.y))
            }
        }
    }

    /**
     * Debug draw method for ray projection.
     *
     * @param g             Graphics2D object to draw to
     * @param paintSettings Colour settings to draw the objects to screen with
     * @param camera        Camera class used to convert points from world space to view space
     */
    @JvmStatic
    fun rayDraw(g: Graphics2D, paintSettings: ColourSettings, camera: Camera, ray: Ray) {
        g.color = paintSettings.projectedRay
        val epicenter = camera.convertToScreen(ray.startPoint)
        val endPoint = camera.convertToScreen(ray.direction.scalar(ray.distance.toDouble()).plus(ray.startPoint))
        g.draw(Line2D.Double(epicenter.x, epicenter.y, endPoint.x, endPoint.y))
        g.color = paintSettings.rayToBody
        if (ray.rayInformation != null) {
            val intersection = camera.convertToScreen(ray.rayInformation!!.coordinates)
            g.draw(Line2D.Double(epicenter.x, epicenter.y, intersection.x, intersection.y))
            val circleRadius = camera.scaleToScreenXValue(paintSettings.RAY_DOT)
            g.fill(
                Ellipse2D.Double(
                    intersection.x - circleRadius,
                    intersection.y - circleRadius,
                    2.0 * circleRadius,
                    2.0 * circleRadius
                )
            )
        }
    }

    @JvmStatic
    fun drawJoint(g: Graphics2D, paintSettings: ColourSettings, camera: Camera, joint: Joint) {
        val obj1 = joint.object1AttachmentPoint
        val obj2: Vec2 = if(joint is JointToBody) joint.object2AttachmentPoint else (joint as JointToPoint).pointAttachedTo

        g.color = paintSettings.joints
        val obj1Pos = camera.convertToScreen(obj1)
        val obj2Pos = camera.convertToScreen(obj2)
        g.draw(Line2D.Double(obj1Pos.x, obj1Pos.y, obj2Pos.x, obj2Pos.y))
    }

    /**
     * Debug draw method for AABB.
     *
     * @param g             Graphics2D object to draw to
     * @param paintSettings Colour settings to draw the objects to screen with
     * @param camera        Camera class used to convert points from world space to view space
     */
    @JvmStatic
    fun drawAABB(g: Graphics2D, paintSettings: ColourSettings, camera: Camera, body: Body) {
        g.color = paintSettings.aabb
        val polyBB: Path2D = Path2D.Double()
        val min = camera.convertToScreen(body.aabb.min.plus(body.position))
        val max = camera.convertToScreen(body.aabb.max.plus(body.position))
        polyBB.moveTo(min.x, min.y)
        polyBB.lineTo(min.x, max.y)
        polyBB.lineTo(max.x, max.y)
        polyBB.lineTo(max.x, min.y)
        polyBB.closePath()
        g.draw(polyBB)
    }

    /**
     * Debug draw method for center of mass.
     *
     * @param g             Graphics2D object to draw to
     * @param paintSettings Colour settings to draw the objects to screen with
     * @param camera        Camera class used to convert points from world space to view space
     */
    @JvmStatic
    fun drawCOMS(g: Graphics2D, paintSettings: ColourSettings, camera: Camera, body: Body) {
        g.color = paintSettings.centreOfMass
        val centre = body.position
        val line = Vec2(paintSettings.COM_RADIUS.toDouble(), .0)
        body.shape.orientation.mul(line)
        var beginningOfLine = camera.convertToScreen(centre.plus(line))
        var endOfLine = camera.convertToScreen(centre.minus(line))
        val lin1: Line2D = Line2D.Double(beginningOfLine.x, beginningOfLine.y, endOfLine.x, endOfLine.y)
        g.draw(lin1)
        beginningOfLine = camera.convertToScreen(centre.plus(line.normal()))
        endOfLine = camera.convertToScreen(centre.minus(line.normal()))
        val lin2: Line2D = Line2D.Double(beginningOfLine.x, beginningOfLine.y, endOfLine.x, endOfLine.y)
        g.draw(lin2)
    }

    /**
     * Debug draw method for a polygon.
     *
     * @param g             Graphics2D object to draw to
     * @param paintSettings Colour settings to draw the objects to screen with
     * @param camera        Camera class used to convert points from world space to view space
     */
    @JvmStatic
    fun polygonDraw(g: Graphics2D, paintSettings: ColourSettings, camera: Camera, body: Body) {
        val polygon = body.shape as Polygon
        val s = Path2D.Double()
        for (i in polygon.vertices.indices) {
            var v = Vec2(polygon.vertices[i])
            polygon.orientation.mul(v)
            v.add(body.position)
            v = camera.convertToScreen(v)
            if (i == 0) {
                s.moveTo(v.x, v.y)
            } else {
                s.lineTo(v.x, v.y)
            }
        }
        s.closePath()
        if (body.mass == 0.0) {
            g.color = paintSettings.staticFill
            g.fill(s)
            g.color = paintSettings.staticOutLine
        } else {
            g.color = paintSettings.shapeFill
            g.fill(s)
            g.color = paintSettings.shapeOutLine
        }
        g.draw(s)
    }

    /**
     * Debug draw method for a circle.
     *
     * @param g             Graphics2D object to draw to
     * @param paintSettings Colour settings to draw the objects to screen with
     * @param camera        Camera class used to convert points from world space to view space
     */
    @JvmStatic
    fun circleDraw(g: Graphics2D, paintSettings: ColourSettings, camera: Camera, body: Body) {
        if (body.mass == 0.0) {
            g.color = paintSettings.staticFill
        } else {
            g.color = paintSettings.shapeFill
        }
        val circlePotion = camera.convertToScreen(body.position)
        val drawnRadius = camera.scaleToScreenXValue((body.shape as Circle).radius)
        g.fill(
            Ellipse2D.Double(
                circlePotion.x - drawnRadius,
                circlePotion.y - drawnRadius,
                2 * drawnRadius,
                2 * drawnRadius
            )
        )
        if (body.mass == 0.0) {
            g.color = paintSettings.staticOutLine
        } else {
            g.color = paintSettings.shapeOutLine
        }
        g.draw(
            Ellipse2D.Double(
                circlePotion.x - drawnRadius,
                circlePotion.y - drawnRadius,
                2 * drawnRadius,
                2 * drawnRadius
            )
        )
    }

    /**
     * Debug draw method for all rays projected.
     *
     * @param g             Graphics2D object to draw to
     * @param paintSettings Colour settings to draw the objects to screen with
     * @param camera        Camera class used to convert points from world space to view space
     */
    @JvmStatic
    fun rayExplosionDraw(g: Graphics2D, paintSettings: ColourSettings, camera: Camera, raycastExplosion: RaycastExplosion) {
        rayScatterDraw(g, paintSettings, camera, raycastExplosion.rayScatter)
    }

    /**
     * Debug draw method for rays and intersections.
     *
     * @param g             Graphics2D object to draw to
     * @param paintSettings Colour settings to draw the objects to screen with
     * @param camera        Camera class used to convert points from world space to view space
     */
    private fun rayScatterDraw(g: Graphics2D, paintSettings: ColourSettings, camera: Camera, rayScatter: RayScatter) {
        for (ray in rayScatter.rays) {
            rayDraw(g, paintSettings, camera, ray)
        }
    }

    /**
     * Debug draw method for proximity and effected objects.
     *
     * @param g             Graphics2D object to draw to
     * @param paintSettings Colour settings to draw the objects to screen with
     * @param camera        Camera class used to convert points from world space to view space
     */
    @JvmStatic
    fun explosionDraw(g: Graphics2D, paintSettings: ColourSettings, camera: Camera, proximityExplosion: ProximityExplosion) {
        g.color = paintSettings.proximity
        val circlePotion = camera.convertToScreen(proximityExplosion.getEpicentre())
        val proximityRadius = camera.scaleToScreenXValue(proximityExplosion.proximity.toDouble())
        g.draw(
            Ellipse2D.Double(
                circlePotion.x - proximityRadius,
                circlePotion.y - proximityRadius,
                2 * proximityRadius,
                2 * proximityRadius
            )
        )
        proximityExplosion.updateLinesToBody()
        for (p in proximityExplosion.linesToBodies) {
            g.color = paintSettings.linesToObjects
            val worldCoord = camera.convertToScreen(p)
            g.draw(Line2D.Double(circlePotion.x, circlePotion.y, worldCoord.x, worldCoord.y))
            val lineToRadius = camera.scaleToScreenXValue(paintSettings.CIRCLE_RADIUS.toDouble())
            g.fill(
                Ellipse2D.Double(
                    worldCoord.x - lineToRadius,
                    worldCoord.y - lineToRadius,
                    2 * lineToRadius,
                    2 * lineToRadius
                )
            )
        }
    }

    /**
     * Debug draw method for world objects.
     *
     * @param g             Graphics2D object to draw to
     * @param paintSettings Colour settings to draw the objects to screen with
     * @param camera        Camera class used to convert points from world space to view space
     */
    @JvmStatic
    fun worldDrawContact(g: Graphics2D, paintSettings: ColourSettings, camera: Camera, world: World) {
        for (contact in world.contacts) {
            val point = contact.contacts[0]
            g.color = paintSettings.contactPoint
            var line: Vec2 = contact.contactNormal.normal().scalar(paintSettings.TANGENT_LINE_SCALAR)
            var beginningOfLine: Vec2 = camera.convertToScreen(point.plus(line))
            var endOfLine: Vec2 = camera.convertToScreen(point.minus(line))
            g.draw(Line2D.Double(beginningOfLine.x, beginningOfLine.y, endOfLine.x, endOfLine.y))
            line = contact.contactNormal.scalar(paintSettings.NORMAL_LINE_SCALAR)
            beginningOfLine = camera.convertToScreen(point.plus(line))
            endOfLine = camera.convertToScreen(point.minus(line))
            g.draw(Line2D.Double(beginningOfLine.x, beginningOfLine.y, endOfLine.x, endOfLine.y))
        }
    }
}