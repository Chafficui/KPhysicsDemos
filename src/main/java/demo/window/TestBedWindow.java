package demo.window;

import demo.input.*;
import demo.tests.Chains;
import demo.tests.Raycast;
import demo.utils.ColourSettings;
import demo.utils.Painter;
import demo.utils.Trail;
import library.collision.AxisAlignedBoundingBox;
import library.dynamics.Body;
import library.dynamics.World;
import library.explosions.Explosion;
import library.explosions.ParticleExplosion;
import library.explosions.ProximityExplosion;
import library.explosions.RaycastExplosion;
import library.geometry.Circle;
import library.geometry.Polygon;
import library.joints.Joint;
import library.math.Vec2;
import library.rays.Ray;
import library.rays.ShadowCasting;
import library.rays.Slice;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class TestBedWindow extends JPanel implements Runnable {
    private final Camera CAMERA;
    public static double HERTZ = 60;

    public void setCamera(Vec2 centre, double zoom) {
        CAMERA.setCentre(centre);
        CAMERA.setZoom(zoom);
    }

    public Camera getCamera() {
        return CAMERA;
    }

    private final boolean ANTIALIASING;
    private final Thread PHYSICS_THREAD;

    public TestBedWindow(boolean antiAliasing) {
        this.ANTIALIASING = antiAliasing;

        PHYSICS_THREAD = new Thread(this);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        CAMERA = new Camera((int) screenSize.getWidth(), (int) screenSize.getHeight(), this);

        MouseInput MOUSE_INPUT = new MouseInput(this);
        addMouseListener(MOUSE_INPUT);

        //Input handler classes
        KeyBoardInput KEY_INPUT = new KeyBoardInput(this);
        addKeyListener(KEY_INPUT);

        MouseScroll MOUSE_SCROLL_INPUT = new MouseScroll(this);
        addMouseWheelListener(MOUSE_SCROLL_INPUT);

        MouseMotionListener MOUSE_MOTION_INPUT = new MouseMovement(this);
        addMouseMotionListener(MOUSE_MOTION_INPUT);

        Chains.load(this);
    }

    public void startThread() {
        PHYSICS_THREAD.start();
    }

    private final ArrayList<Ray> rays = new ArrayList<>();

    public void add(Ray ray) {
        rays.add(ray);
    }

    private final ArrayList<Slice> slices = new ArrayList<>();

    public void add(Slice s) {
        slices.add(s);
    }

    public int getSlicesSize() {
        return slices.size();
    }

    public ArrayList<Slice> getSlices() {
        return slices;
    }

    private final ArrayList<Explosion> explosionObj = new ArrayList<>();

    public ArrayList<Explosion> getRayExplosions() {
        return explosionObj;
    }

    public void add(Explosion ex) {
        explosionObj.add(ex);
    }

    private final ArrayList<ParticleExplosion> particles = new ArrayList<>();

    public void add(ParticleExplosion p, double lifespan) {
        particles.add(p);
        for (Body b : p.getParticles()) {
            trailsToBodies.add(new Trail(1000, 1, b, lifespan));
        }
    }

    private final ArrayList<ShadowCasting> shadowCastings = new ArrayList<>();

    public void add(ShadowCasting shadowCasting) {
        shadowCastings.add(shadowCasting);
    }

    private World world = new World();

    public void setWorld(World world) {
        this.world = world;
    }

    public World getWorld() {
        return world;
    }

    private final ArrayList<Trail> trailsToBodies = new ArrayList<>();

    public void add(Trail trail) {
        trailsToBodies.add(trail);
    }

    private boolean running = true;
    private volatile boolean paused = false;
    private final Object pauseLock = new Object();

    public void stop() {
        running = false;
        PHYSICS_THREAD.interrupt();
    }

    public void pause() {
        paused = true;
    }

    public boolean isPaused() {
        return paused;
    }

    public void resume() {
        synchronized (pauseLock) {
            paused = false;
            pauseLock.notifyAll();
        }
    }

    private void updateRays() {
        for (Ray r : rays) {
            if (Raycast.active) {
                Raycast.action(r);
            }
            r.updateProjection(world.getBodies());
        }
        for (Explosion p : explosionObj) {
            p.update(world.getBodies());
        }
        for (ShadowCasting s : shadowCastings) {
            s.updateProjections(world.getBodies());
        }
        for (Slice s : slices) {
            s.updateProjection(world.getBodies());
        }
    }

    private void updateTrails() {
        for (Trail t : trailsToBodies) {
            t.updateTrail();
        }
    }

    @Override
    public void run() {
        while (running) {
            synchronized (pauseLock) {
                if (!running) {
                    break;
                }
                if (paused) {
                    try {
                        synchronized (pauseLock) {
                            pauseLock.wait();
                        }
                    } catch (InterruptedException e) {
                        break;
                    }
                    if (!running) {
                        break;
                    }
                }
            }
            repaint();
        }
    }

    private void update() {
        double dt = TestBedWindow.HERTZ > 0.0 ? 1.0 / TestBedWindow.HERTZ : 0.0;
        world.step(dt);
        updateTrails();
        updateRays();
        checkParticleLifetime(dt);
    }

    private void checkParticleLifetime(double timePassed) {
        ArrayList<Body> bodiesToRemove = new ArrayList<>();
        Iterator<Trail> i = trailsToBodies.iterator();
        while (i.hasNext()) {
            Trail s = i.next();
            if (s.checkLifespan(timePassed)) {
                bodiesToRemove.add(s.getBody());
                i.remove();
            }
        }
        Iterator<ParticleExplosion> p = particles.iterator();
        while (p.hasNext()) {
            Body[] s = p.next().getParticles().toArray(new Body[0]);
            if (containsBody(s, bodiesToRemove)) {
                removeParticlesFromWorld(s);
                p.remove();
            }
        }
    }

    private void removeParticlesFromWorld(Body[] s) {
        for (Body b : s) {
            world.removeBody(b);
        }
    }

    private boolean containsBody(Body[] s, ArrayList<Body> bodiesToRemove) {
        for (Body a : s) {
            if (bodiesToRemove.contains(a)) {
                return true;
            }
        }
        return false;
    }

    public void clearTestbedObjects() {
        CAMERA.reset();
        world.clearWorld();
        trailsToBodies.clear();
        rays.clear();
        explosionObj.clear();
        shadowCastings.clear();
        slices.clear();
        repaint();
    }


    public final ColourSettings PAINT_SETTINGS = new ColourSettings();

    public ColourSettings getPAINT_SETTINGS() {
        return PAINT_SETTINGS;
    }

    private int currentDemo = 0;

    public void setCurrentDemo(int i) {
        currentDemo = i;
    }
    public boolean followPayload = false;

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        if (ANTIALIASING) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        setBackground(PAINT_SETTINGS.background);
        update();
        if (followPayload){
            setCamera(new Vec2(world.getBodies().get(3).getPosition().getX(), getCamera().centre.getY()) , 2.0);
        }
        if (PAINT_SETTINGS.getDrawGrid()) {
            drawGridMethod(g2d);
        }
        for (ShadowCasting s : shadowCastings) {
            Painter.shadowDraw(g2d, PAINT_SETTINGS, CAMERA, s);
        }
        drawTrails(g2d);
        for (Body b : world.getBodies()) {
            if (PAINT_SETTINGS.getDrawShapes()) {
                if(b.getShape() instanceof Circle) Painter.circleDraw(g2d, PAINT_SETTINGS, CAMERA, b);
                else if(b.getShape() instanceof Polygon) Painter.polygonDraw(g2d, PAINT_SETTINGS, CAMERA, b);
            }
            if (PAINT_SETTINGS.getDrawAABBs()) {
                Painter.drawAABB(g2d, PAINT_SETTINGS, CAMERA, b);
            }
            if (PAINT_SETTINGS.getDrawCOMs()) {
                Painter.drawCOMS(g2d, PAINT_SETTINGS, CAMERA, b);
            }
        }
        if (PAINT_SETTINGS.getDrawContacts()) {
            Painter.worldDrawContact(g2d, PAINT_SETTINGS, CAMERA, world);
        }
        if (PAINT_SETTINGS.getDrawJoints()) {
            for (Joint j : world.joints) {
                Painter.drawJoint(g2d, PAINT_SETTINGS, CAMERA, j);
            }
        }
        for (Explosion p : explosionObj) {
            if(p instanceof RaycastExplosion) Painter.rayExplosionDraw(g2d, PAINT_SETTINGS, CAMERA, (RaycastExplosion) p);
            else if(p instanceof ProximityExplosion) Painter.explosionDraw(g2d, PAINT_SETTINGS, CAMERA, (ProximityExplosion) p);
        }
        for (Ray r : rays) {
            Painter.rayDraw(g2d, PAINT_SETTINGS, CAMERA, r);
        }
        for (Slice s : slices) {
            Painter.sliceDraw(g2d, PAINT_SETTINGS, CAMERA, s);
        }
        DemoText.draw(g2d, PAINT_SETTINGS, currentDemo);
    }

    private void drawGridMethod(Graphics2D g2d) {
        int projection = 20000;
        int spacing = 10;
        int minXY = -projection;
        int totalProjectionDistance = projection + projection;
        g2d.setColor(PAINT_SETTINGS.gridLines);
        for (int i = 0; i <= totalProjectionDistance; i += spacing) {
            if (i == projection) {
                g2d.setStroke(PAINT_SETTINGS.axisStrokeWidth);
                g2d.setColor(PAINT_SETTINGS.gridAxis);
            }

            Vec2 currentMinY = CAMERA.convertToScreen(new Vec2(minXY + i, minXY));
            Vec2 currentMaxY = CAMERA.convertToScreen(new Vec2(minXY + i, projection));
            g2d.draw(new Line2D.Double(currentMinY.getX(), currentMinY.getY(), currentMaxY.getX(), currentMaxY.getY()));

            Vec2 currentMinX = CAMERA.convertToScreen(new Vec2(minXY, minXY + i));
            Vec2 currentMaxX = CAMERA.convertToScreen(new Vec2(projection, minXY + i));
            g2d.draw(new Line2D.Double(currentMinX.getX(), currentMinX.getY(), currentMaxX.getX(), currentMaxX.getY()));

            if (i == projection) {
                g2d.setStroke(PAINT_SETTINGS.defaultStrokeWidth);
                g2d.setColor(PAINT_SETTINGS.gridLines);
            }
        }
    }

    private void drawTrails(Graphics2D g) {
        g.setColor(PAINT_SETTINGS.trail);
        for (Trail t : trailsToBodies) {
            Path2D.Double s = new Path2D.Double();
            for (int i = 0; i < t.getTrailPoints().length; i++) {
                Vec2 v = t.getTrailPoints()[i];
                if (v == null) {
                    break;
                } else {
                    v = CAMERA.convertToScreen(v);
                    if (i == 0) {
                        s.moveTo(v.getX(), v.getY());
                    } else {
                        s.lineTo(v.getX(), v.getY());
                    }
                }
            }
            g.draw(s);
        }
    }

    public static void showWindow(TestBedWindow gameScreen, String title, int windowWidth, int windowHeight) {
        if (gameScreen != null) {
            JFrame window = new JFrame(title);
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.add(gameScreen);
            window.setMinimumSize(new Dimension(800, 600));
            window.setPreferredSize(new Dimension(windowWidth, windowHeight));
            window.pack();
            window.setLocationRelativeTo(null);
            gameScreen.setFocusable(true);
            gameScreen.setOpaque(true);
            gameScreen.setBackground(gameScreen.PAINT_SETTINGS.background);

            JMenuBar menuBar = new JMenuBar();
            menuBar.add(createTestMenu(gameScreen));
            menuBar.add(createColourSchemeMenu(gameScreen));
            menuBar.add(createFrequencyMenu());
            menuBar.add(createDisplayMenu(gameScreen));
            window.setJMenuBar(menuBar);

            window.setVisible(true);
        }
    }

    private static Component createDisplayMenu(TestBedWindow gameScreen) {
        JMenu drawOptions = new JMenu("Graphics Options");

        JMenuItem showGrid = new JMenuItem("Display Grid");
        drawOptions.add(showGrid);
        showGrid.addActionListener(new ColourMenuInput(gameScreen));

        JMenuItem displayShapes = new JMenuItem("Display Shapes");
        drawOptions.add(displayShapes);
        displayShapes.addActionListener(new ColourMenuInput(gameScreen));

        JMenuItem displayJoints = new JMenuItem("Display Joints");
        drawOptions.add(displayJoints);
        displayJoints.addActionListener(new ColourMenuInput(gameScreen));

        JMenuItem displayAABBs = new JMenuItem("Display AABBs");
        drawOptions.add(displayAABBs);
        displayAABBs.addActionListener(new ColourMenuInput(gameScreen));

        JMenuItem displayContactPoints = new JMenuItem("Display Contacts");
        drawOptions.add(displayContactPoints);
        displayContactPoints.addActionListener(new ColourMenuInput(gameScreen));

        JMenuItem displayCOMs = new JMenuItem("Display COMs");
        drawOptions.add(displayCOMs);
        displayCOMs.addActionListener(new ColourMenuInput(gameScreen));

        return drawOptions;
    }

    private static Component createFrequencyMenu() {
        JMenu hertzMenu = new JMenu("Hertz");
        int number = 30;
        for (int i = 1; i < 5; i++) {
            JMenuItem hertzMenuItem = new JMenuItem("" + number * i);
            hertzMenu.add(hertzMenuItem);
            hertzMenuItem.addActionListener(e -> {
                switch (e.getActionCommand()) {
                    case "30" -> TestBedWindow.HERTZ = 30;
                    case "60" -> TestBedWindow.HERTZ = 60;
                    case "90" -> TestBedWindow.HERTZ = 90;
                    case "120" -> TestBedWindow.HERTZ = 120;
                }
            });
        }
        return hertzMenu;
    }

    private static JMenu createColourSchemeMenu(TestBedWindow gameScreen) {
        JMenu colourScheme = new JMenu("Colour schemes");

        JMenuItem defaultScheme = new JMenuItem("Default");
        colourScheme.add(defaultScheme);
        defaultScheme.addActionListener(new ColourMenuInput(gameScreen));

        JMenuItem box2dScheme = new JMenuItem("Box2d");
        colourScheme.add(box2dScheme);
        box2dScheme.addActionListener(new ColourMenuInput(gameScreen));

        JMenuItem monochromaticScheme = new JMenuItem("Monochromatic");
        colourScheme.add(monochromaticScheme);
        monochromaticScheme.addActionListener(new ColourMenuInput(gameScreen));

        return colourScheme;
    }

    private static JMenu createTestMenu(TestBedWindow gameScreen) {
        JMenu testMenu = new JMenu("Demos");
        testMenu.setMnemonic(KeyEvent.VK_M);

        JMenuItem chains = new JMenuItem("Chains");
        chains.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, InputEvent.ALT_DOWN_MASK));
        testMenu.add(chains);
        chains.addActionListener(new KeyBoardInput(gameScreen));

        JMenuItem lineOfSight = new JMenuItem("Line of sight");
        lineOfSight.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, InputEvent.ALT_DOWN_MASK));
        testMenu.add(lineOfSight);
        lineOfSight.addActionListener(new KeyBoardInput(gameScreen));

        JMenuItem particleExplosion = new JMenuItem("Particle explosion");
        particleExplosion.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, InputEvent.ALT_DOWN_MASK));
        testMenu.add(particleExplosion);
        particleExplosion.addActionListener(new KeyBoardInput(gameScreen));

        JMenuItem proximityExplosion = new JMenuItem("Proximity explosion");
        proximityExplosion.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4, InputEvent.ALT_DOWN_MASK));
        testMenu.add(proximityExplosion);
        proximityExplosion.addActionListener(new KeyBoardInput(gameScreen));

        JMenuItem raycastExplosion = new JMenuItem("Raycast explosion");
        raycastExplosion.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_5, InputEvent.ALT_DOWN_MASK));
        testMenu.add(raycastExplosion);
        raycastExplosion.addActionListener(new KeyBoardInput(gameScreen));

        JMenuItem raycast = new JMenuItem("Raycast");
        raycast.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_6, InputEvent.ALT_DOWN_MASK));
        testMenu.add(raycast);
        raycast.addActionListener(new KeyBoardInput(gameScreen));

        JMenuItem trebuchet = new JMenuItem("Trebuchet");
        trebuchet.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_7, InputEvent.ALT_DOWN_MASK));
        testMenu.add(trebuchet);
        trebuchet.addActionListener(new KeyBoardInput(gameScreen));

        JMenuItem sliceObjects = new JMenuItem("Slice objects");
        sliceObjects.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_8, InputEvent.ALT_DOWN_MASK));
        testMenu.add(sliceObjects);
        sliceObjects.addActionListener(new KeyBoardInput(gameScreen));

        JMenuItem bouncingBall = new JMenuItem("Bouncing ball");
        bouncingBall.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_9, InputEvent.ALT_DOWN_MASK));
        testMenu.add(bouncingBall);
        bouncingBall.addActionListener(new KeyBoardInput(gameScreen));

        JMenuItem mixedShapes = new JMenuItem("Mixed shapes");
        mixedShapes.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.ALT_DOWN_MASK));
        testMenu.add(mixedShapes);
        mixedShapes.addActionListener(new KeyBoardInput(gameScreen));

        JMenuItem newtonsCradle = new JMenuItem("Newtons cradle");
        newtonsCradle.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.ALT_DOWN_MASK));
        testMenu.add(newtonsCradle);
        newtonsCradle.addActionListener(new KeyBoardInput(gameScreen));

        JMenuItem wreckingBall = new JMenuItem("Wrecking ball");
        wreckingBall.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.ALT_DOWN_MASK));
        testMenu.add(wreckingBall);
        wreckingBall.addActionListener(new KeyBoardInput(gameScreen));

        JMenuItem friction = new JMenuItem("Friction");
        friction.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.ALT_DOWN_MASK));
        testMenu.add(friction);
        friction.addActionListener(new KeyBoardInput(gameScreen));

        JMenuItem drag = new JMenuItem("Drag");
        drag.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.ALT_DOWN_MASK));
        testMenu.add(drag);
        drag.addActionListener(new KeyBoardInput(gameScreen));

        JMenuItem restitution = new JMenuItem("Restitution");
        restitution.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.ALT_DOWN_MASK));
        testMenu.add(restitution);
        restitution.addActionListener(new KeyBoardInput(gameScreen));

        JMenuItem stackedObjects = new JMenuItem("Stacked objects");
        stackedObjects.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.ALT_DOWN_MASK));
        testMenu.add(stackedObjects);
        stackedObjects.addActionListener(new KeyBoardInput(gameScreen));

        JMenuItem bouncyBall = new JMenuItem("Bouncy Ball");
        bouncyBall.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.ALT_DOWN_MASK));
        testMenu.add(bouncyBall);
        bouncyBall.addActionListener(new KeyBoardInput(gameScreen));

        return testMenu;
    }

    public void generateRandomObjects(Vec2 lowerBound, Vec2 upperBound, int totalObjects, int maxRadius) {
        while (totalObjects > 0) {
            Body b = createRandomObject(lowerBound, upperBound, maxRadius);
            if (overlap(b)) {
                world.addBody(b);
                totalObjects--;
            }
        }
    }

    public void generateBoxOfObjects() {
        {
            Body top = new Body(new Polygon(900.0, 20.0), -20, 500);
            top.setDensity(0);
            world.addBody(top);

            Body right = new Body(new Polygon(500.0, 20.0), 900, 20);
            right.setOrientation(1.5708);
            right.setDensity(0);
            world.addBody(right);

            Body bottom = new Body(new Polygon(900.0, 20.0), 20, -500);
            bottom.setDensity(0);
            world.addBody(bottom);

            Body left = new Body(new Polygon(500.0, 20.0), -900, -20);
            left.setOrientation(1.5708);
            left.setDensity(0);
            world.addBody(left);
        }

        {
            generateRandomObjects(new Vec2(-880, -480), new Vec2(880, 480), 30, 100);
            setStaticWorldBodies();
        }
    }

    private boolean overlap(Body b) {
        for (Body a : world.getBodies()) {
            if (AxisAlignedBoundingBox.aabbOverlap(a, b)) {
                return false;
            }
        }
        return true;
    }

    private Body createRandomObject(Vec2 lowerBound, Vec2 upperBound, int maxRadius) {
        int objectType = random(1, 2);
        Body b = null;
        int radius = random(5, maxRadius);
        double x = random((int) (lowerBound.getX() + radius), (int) (upperBound.getX() - radius));
        double y = random((int) (lowerBound.getY() + radius), (int) (upperBound.getY() - radius));
        double rotation = random(0, (int) 7.0);
        switch (objectType) {
            case 1 -> {
                b = new Body(new Circle(radius), x, y);
                b.setOrientation(rotation);
            }
            case 2 -> {
                int sides = random(3, 10);
                b = new Body(new Polygon(radius, sides), x, y);
                b.setOrientation(rotation);
            }
        }
        return b;
    }

    public void setStaticWorldBodies() {
        for (Body b : world.getBodies()) {
            b.setDensity(0);
        }
    }

    public void buildExplosionDemo() {
        {
            buildShelf(50.0, 300.0);
            buildShelf(450.0, 400.0);
        }

        Body floor = new Body(new Polygon(20000.0, 2000.0), 0, -2000);
        floor.setDensity(0);
        world.addBody(floor);

        Body reflect = new Body(new Polygon(40.0, 5.0), -100, 330);
        reflect.setOrientation(0.785398);
        reflect.setDensity(0);
        world.addBody(reflect);

        {
            Body top = new Body(new Polygon(120.0, 10.0), 450, 210);
            top.setDensity(0);
            world.addBody(top);

            Body side1 = new Body(new Polygon(100.0, 10.0), 340, 100);
            side1.setOrientation(1.5708);
            side1.setDensity(0);
            world.addBody(side1);

            Body side2 = new Body(new Polygon(100.0, 10.0), 560, 100);
            side2.setOrientation(1.5708);
            side2.setDensity(0);
            world.addBody(side2);

            for (int i = 0; i < 4; i++) {
                Body box = new Body(new Polygon(20.0, 20.0), 450, 20 + (i * 40));
                world.addBody(box);
            }
        }

        for (int k = 0; k < 2; k++) {
            for (int i = 0; i < 5; i++) {
                Body box = new Body(new Polygon(20.0, 20.0), -600 + (k * 200), 20 + (i * 40));
                world.addBody(box);
            }
        }
    }

    public void buildShelf(double x, double y) {
        Body shelf = new Body(new Polygon(100.0, 10.0), x, y);
        shelf.setDensity(0);
        world.addBody(shelf);

        int boxes = 4;
        for (int i = 0; i < boxes; i++) {
            Body box = new Body(new Polygon(10.0, 20.0), x, y + 30 + (i * 40));
            world.addBody(box);
        }
    }

    public void createPyramid(int noOfPillars, int x, int y) {
        double height = 30.0;
        double width = 5.0;
        x += width;

        double widthOfTopPillar = height + height;
        for (int k = 0; k < noOfPillars; k++) {
            x += height;

            Body initialPillar = new Body(new Polygon(width + 2, height), x, y + height);
            addPillar(initialPillar);

            for (int i = 0; i < noOfPillars - k; i++) {
                Body rightPillar = new Body(new Polygon(width + 2, height), x + widthOfTopPillar + (widthOfTopPillar * i), y + height);
                addPillar(rightPillar);

                Body topPillar = new Body(new Polygon(height, width), x + height + (i * widthOfTopPillar), y + widthOfTopPillar + width);
                addPillar(topPillar);
            }
            y += widthOfTopPillar + width + width;
        }
    }

    public void createTower(int floors, int x, int y) {
        double height = 30.0;
        double width = 5.0;
        x += width;

        double heightOfPillar = height + height;
        double widthOfPillar = width + width;
        for (int k = 0; k < floors; k++) {
            Body leftPillar = new Body(new Polygon(width, height), x, y + height);
            addPillar(leftPillar);

            Body rightPillar = new Body(new Polygon(width, height), x + heightOfPillar - widthOfPillar, y + height);
            addPillar(rightPillar);

            Body topPillar = new Body(new Polygon(height, width), x + height - width, y + heightOfPillar + width);
            addPillar(topPillar);
            y += heightOfPillar + width + width;
        }
    }

    //Removing some boiler plate for create tower and Pyramid
    private void addPillar(Body b) {
        b.setRestitution(0.2);
        b.setDensity(0.2);
        world.addBody(b);
    }

    //Removes friction from the world
    public void setWorldIce() {
        for (Body b : world.getBodies()) {
            b.setStaticFriction(0.0);
            b.setDynamicFriction(0.0);
        }
    }

    // Scaled friction by a passed ratio
    public void scaleWorldFriction(double ratio) {
        for (Body b : world.getBodies()) {
            b.setStaticFriction(b.getStaticFriction() * ratio);
            b.setDynamicFriction(b.getDynamicFriction() * ratio);
        }
    }

    /**
     * Generates a random number within the desired range.
     * @param min Minimum int value that the range can fall inside
     * @param max Maximum int value that the range can fall inside
     * @return int value inside the range of min and max supplied
     */
    private int random(int min, int max) {
        Random rand = new Random();
        return rand.nextInt(max - min + 1) + min;
    }
}