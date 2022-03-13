package demo.window;

import library.math.Vec2;

public class Camera {
    private final double aspectRatio;
    public double zoom;
    public int width;
    public int height;
    public Vec2 centre;
    private final TestBedWindow panel;

    protected Vec2 pointClicked;

    public Vec2 getPointClicked(){
        return pointClicked;
    }

    public void setPointClicked(Vec2 v){
        pointClicked = v;
    }

    public Camera(int windowWidth, int windowHeight, TestBedWindow testWindow) {
        centre = new Vec2(0, 0);
        zoom = 1.0;
        this.width = windowWidth;
        this.height = windowHeight;
        panel = testWindow;
        aspectRatio = width * 1.0 / height;
    }

    Vec2 upperBound = new Vec2();
    Vec2 lowerBound = new Vec2();

    public Vec2 convertToScreen(Vec2 v) {
        updateViewSize(aspectRatio);
        double boxWidth = (v.getX() - lowerBound.getX()) / (upperBound.getX() - lowerBound.getX());
        double boxHeight = (v.getY() - lowerBound.getY()) / (upperBound.getY() - lowerBound.getY());

        Vec2 output = new Vec2();
        output.setX(boxWidth * panel.getWidth());
        output.setY((1.0 - boxHeight) * (panel.getWidth() / aspectRatio));
        return output;
    }

    public Vec2 convertToWorld(Vec2 vec) {
        updateViewSize(aspectRatio);
        Vec2 output = new Vec2();
        double distAlongWindowXAxis = vec.getX() / panel.getWidth();
        output.setX((1.0 - distAlongWindowXAxis) * lowerBound.getX() + distAlongWindowXAxis * upperBound.getX());

        double aspectHeight = panel.getWidth() / aspectRatio;
        double distAlongWindowYAxis = (aspectHeight - vec.getY()) / aspectHeight;
        output.setY((1.0 - distAlongWindowYAxis) * lowerBound.getY() + distAlongWindowYAxis * upperBound.getY());
        return output;
    }

    private void updateViewSize(double aspectRatio) {
        Vec2 extents = new Vec2(aspectRatio * 200, 200);
        extents = extents.scalar(zoom);
        upperBound = centre.plus(extents);
        lowerBound = centre.minus(extents);
    }

    public double scaleToScreenXValue(double radius) {
        double aspectRatio = width * 1.0 / height;
        Vec2 extents = new Vec2(aspectRatio * 200, 200);
        extents = extents.scalar(zoom);
        Vec2 upperBound = centre.plus(extents);
        Vec2 lowerBound = centre.minus(extents);
        double w = radius / (upperBound.getX() - lowerBound.getX());
        return w * panel.getWidth();
    }

    public void setCentre(Vec2 centre) {
        this.centre = centre;
    }

    public void setZoom(double zoom) {
        assert (zoom > 0);
        this.zoom = zoom;
    }

    public void reset() {
        setCentre(new Vec2());
        setZoom(1.0);
    }
}