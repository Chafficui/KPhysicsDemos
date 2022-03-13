package demo.utils;

import library.dynamics.Body;
import library.math.Vec2;

public class Trail {
    private final Vec2[] trailPoints;
    private final int arrayEndPos;
    private final int skipInterval;
    private int counter;
    private final double lifeSpan;
    private final Body body;
    private double timeActive;

    public Body getBody() {
        return body;
    }

    public Trail(int noOfTrailPoints, int skipInterval, Body b, double life) {
        trailPoints = Vec2.createArray(noOfTrailPoints);
        arrayEndPos = noOfTrailPoints - 1;
        this.skipInterval = skipInterval;
        counter = 0;
        body = b;
        lifeSpan = life;
        timeActive = 0.0;
    }

    private int trailEndPointIndex = 0;

    public void updateTrail() {
        if (counter >= skipInterval) {
            if (trailEndPointIndex <= arrayEndPos) {
                trailPoints[trailEndPointIndex] = body.getPosition().copy();
                trailEndPointIndex++;
            } else {
                System.arraycopy(trailPoints, 1, trailPoints, 0, arrayEndPos);
                trailPoints[arrayEndPos] = body.getPosition().copy();
            }
            counter = 0;
        } else {
            counter++;
        }
    }

    public Vec2[] getTrailPoints() {
        return trailPoints;
    }

    public boolean checkLifespan(double p) {
        if (lifeSpan == 0) return false;
        timeActive += p;
        return timeActive > lifeSpan;
    }
}