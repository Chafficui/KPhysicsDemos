package demo.input;

import demo.tests.LineOfSight;
import demo.tests.ProximityExplosionTest;
import demo.tests.RaycastExplosionTest;
import demo.tests.SliceObjects;
import demo.window.TestBedWindow;
import library.explosions.ProximityExplosion;
import library.math.Vec2;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class MouseMovement extends TestbedControls implements MouseMotionListener {
    public MouseMovement(TestBedWindow testBedWindow) {
        super(testBedWindow);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            Vec2 pw = CAMERA.convertToWorld(new Vec2(e.getX(), e.getY()));
            Vec2 diff = pw.minus(CAMERA.getPointClicked());
            CAMERA.setCentre(CAMERA.centre.minus(diff));
        } else {
            Vec2 v = CAMERA.convertToWorld(new Vec2(e.getX(), e.getY()));
            if (ProximityExplosionTest.active) {
                ProximityExplosion p = (ProximityExplosion) TESTBED.getRayExplosions().get(0);
                p.setEpicentre(v);
            } else if (RaycastExplosionTest.active) {
                RaycastExplosionTest.r.setEpicentre(v);
            } else if (LineOfSight.active) {
                LineOfSight.b.setStartPoint(v);
            } else if (TESTBED.getSlicesSize() == 1 && !SwingUtilities.isRightMouseButton(e) && SliceObjects.active) {
                TESTBED.getSlices().get(0).setDirection(v);
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (!SwingUtilities.isRightMouseButton(e)) {
            Vec2 v = CAMERA.convertToWorld(new Vec2(e.getX(), e.getY()));
            if (ProximityExplosionTest.active) {
                ProximityExplosion p = (ProximityExplosion) TESTBED.getRayExplosions().get(0);
                p.setEpicentre(v);
            } else if (RaycastExplosionTest.active) {
                RaycastExplosionTest.r.setEpicentre(v);
            } else if (LineOfSight.active) {
                LineOfSight.b.setStartPoint(v);
            } else if (TESTBED.getSlicesSize() == 1 && !SwingUtilities.isRightMouseButton(e) && SliceObjects.active) {
                TESTBED.getSlices().get(0).setDirection(v);
            }
        }
    }
}
