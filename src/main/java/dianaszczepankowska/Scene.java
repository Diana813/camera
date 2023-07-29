package dianaszczepankowska;

import static dianaszczepankowska.Matrix.createProjectionMatrix;
import dianaszczepankowska.figures.Coordinates;
import dianaszczepankowska.figures.Figure;
import static dianaszczepankowska.figures.Figure.createCube;
import dianaszczepankowska.figures.Triangle;
import static dianaszczepankowska.figures.Triangle.clipTriangleAgainstPlane;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JPanel;

public class Scene extends JPanel {

    private final List<Figure> cubes;
    private final Matrix projectionMatrix4x4;
    private final float fTheta;
    private final BufferedImage backBuffer;
    private final Graphics2D backBufferGraphics;

    private final int screenWidth;
    private final int screenHeight;

    private Coordinates camera;

    private Coordinates lookingDirection;

    private float cameraRotationInYZ =0;

    private static final int OFFSET = 0;

    public Scene(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.camera = new Coordinates(0, 0, 0);
        this.lookingDirection = new Coordinates(0, 0, 1);

        fTheta = 0.0f;

        cubes = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            Figure cube;
            if (i % 2 == 0) {
                cube = createCube(0, 0, (float) (1.5 * i));
            } else {
                cube = createCube(3, 0, (float) (1.5 * (i - 1)));
            }

            cubes.add(cube);
        }

      /*  // Start the update loop
        Timer timer = new Timer(16, e -> {
            fTheta += 0.02f;
            repaint();
        });
        timer.start();*/

        float near = 0.1f;
        float far = 1000.0f;
        float fieldOfViewDegrees = 90.0f;
        float aspectRatio = (float) screenHeight / (float) screenWidth;

        projectionMatrix4x4 = createProjectionMatrix(fieldOfViewDegrees, aspectRatio, near, far);

        backBuffer = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_RGB);
        backBufferGraphics = (Graphics2D) backBuffer.getGraphics();

        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPressed(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        draw(backBufferGraphics);
        g.drawImage(backBuffer, 0, 0, this);
    }

    public void draw(Graphics g) {
        super.paint(g);
        clearScreen(g);
        List<Triangle> triangleArrayList = new ArrayList<>();
        cubes.stream().map(Figure::tris).forEach(triangleArrayList::addAll);
        drawTriangles(triangleArrayList, g);
    }

    private void clearScreen(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    private void drawTriangles(List<Triangle> scene, Graphics g) {

        Matrix rotationX = Matrix.createRotationXMatrix(fTheta);
        Matrix rotationZ = Matrix.createRotationZMatrix(fTheta);

        Matrix matTrans = Matrix.createTranslationMatrix(0, 0, 5);

        Matrix matWorld = Matrix.createIdentityMatrix().multiplyMatrix(rotationZ).multiplyMatrix(rotationX).multiplyMatrix(matTrans);

        Coordinates up = new Coordinates(0, 1, 0);
        Coordinates target = new Coordinates(0, 0, 1);
        Matrix matCameraRot = Matrix.createRotationYMatrix(cameraRotationInYZ);
        lookingDirection = target.multiplyByMatrix(matCameraRot).multiply(0.8f * 1.5f);
        target = camera.add(lookingDirection);
        Matrix matCamera = Matrix.pointAt(camera, target, up);

        Matrix matView = matCamera.inverse();
        List<Triangle> trianglesToDraw = new ArrayList<>();

        for (Triangle tri : scene) {
            Triangle triProjected = new Triangle(
                    new Coordinates(0, 0, 0),
                    new Coordinates(0, 0, 0),
                    new Coordinates(0, 0, 0),
                    tri.color()
            );

            Triangle triTransformed = new Triangle(
                    new Coordinates(0, 0, 0),
                    new Coordinates(0, 0, 0),
                    new Coordinates(0, 0, 0),
                    tri.color()
            );
            Triangle triViewed = new Triangle(
                    new Coordinates(0, 0, 0),
                    new Coordinates(0, 0, 0),
                    new Coordinates(0, 0, 0),
                    tri.color()
            );


            triTransformed.coordinates()[0] = tri.coordinates()[0].multiplyByMatrix(matWorld);
            triTransformed.coordinates()[1] = tri.coordinates()[1].multiplyByMatrix(matWorld);
            triTransformed.coordinates()[2] = tri.coordinates()[2].multiplyByMatrix(matWorld);

            triTransformed = offsetIntoScreen(triTransformed);

            Coordinates normal, line1, line2;
            line1 = triTransformed.coordinates()[1].subtract(triTransformed.coordinates()[0]);
            line2 = triTransformed.coordinates()[2].subtract(triTransformed.coordinates()[0]);

            normal = line1.crossProduct(line2).normalize();

            Coordinates cameraRay = triTransformed.coordinates()[0].subtract(camera);


            if (normal.dotProduct(cameraRay) < 0.0f) {
                triViewed.coordinates()[0] = triTransformed.coordinates()[0].multiplyByMatrix(matView);
                triViewed.coordinates()[1] = triTransformed.coordinates()[1].multiplyByMatrix(matView);
                triViewed.coordinates()[2] = triTransformed.coordinates()[2].multiplyByMatrix(matView);


                List<Triangle> clippedTriangles = clipTriangleAgainstPlane(new Coordinates(0.0f, 0.0f, 0.1f), new Coordinates(0.0f, 0.0f, 1.0f), triViewed);

                for (Triangle clippedTriangle : clippedTriangles) {
                    // Project triangles from 3D --> 2D
                    triProjected.coordinates()[0] = clippedTriangle.coordinates()[0].multiplyByMatrix(projectionMatrix4x4);
                    triProjected.coordinates()[1] = clippedTriangle.coordinates()[1].multiplyByMatrix(projectionMatrix4x4);
                    triProjected.coordinates()[2] = clippedTriangle.coordinates()[2].multiplyByMatrix(projectionMatrix4x4);
                    //scaleIntoView(triProjected);

                    triProjected = new Triangle(
                            new Coordinates(triProjected.coordinates()[0].x() * -1, triProjected.coordinates()[0].y() * -1, triProjected.coordinates()[0].z()),
                            new Coordinates(triProjected.coordinates()[1].x() * -1, triProjected.coordinates()[1].y() * -1, triProjected.coordinates()[0].z()),
                            new Coordinates(triProjected.coordinates()[2].x() * -1, triProjected.coordinates()[2].y() * -1, triProjected.coordinates()[0].z()),
                            triProjected.color()
                    );

                    Coordinates vOffsetView = new Coordinates(1, 1, 0);
                    triProjected.coordinates()[0] = triProjected.coordinates()[0].add(vOffsetView);
                    triProjected.coordinates()[1] = triProjected.coordinates()[1].add(vOffsetView);
                    triProjected.coordinates()[2] = triProjected.coordinates()[2].add(vOffsetView);

                    triProjected = new Triangle(
                            new Coordinates(triProjected.coordinates()[0].x() * 0.5f * (float) screenWidth, triProjected.coordinates()[0].y() * 0.5f * screenHeight, triProjected.coordinates()[0].z()),
                            new Coordinates(triProjected.coordinates()[1].x() * 0.5f * (float) screenWidth, triProjected.coordinates()[1].y() * 0.5f * screenHeight, triProjected.coordinates()[0].z()),
                            new Coordinates(triProjected.coordinates()[2].x() * 0.5f * (float) screenWidth, triProjected.coordinates()[2].y() * 0.5f * screenHeight, triProjected.coordinates()[0].z()),
                            triProjected.color()
                    );

                    trianglesToDraw.add(triProjected);
                }

            }
        }

        trianglesToDraw.sort((t1, t2) -> {
            float z1 = (t1.coordinates()[0].z() + t1.coordinates()[1].z() + t1.coordinates()[2].z()) / 3.0f;
            float z2 = (t2.coordinates()[0].z() + t2.coordinates()[1].z() + t2.coordinates()[2].z()) / 3.0f;
            return Float.compare(z2, z1);
        });

        clearScreen(g);

        for (Triangle triToRaster : trianglesToDraw) {

            LinkedList<Triangle> listTriangles = new LinkedList<>();

            listTriangles.add(triToRaster);
            int nNewTriangles = 1;

            for (int p = 0; p < 4; p++) {
                List<Triangle> nTrisToAdd = new ArrayList<>();
                while (nNewTriangles > 0) {
                    Triangle test = listTriangles.removeLast();
                    nNewTriangles--;

                    switch (p) {
                        case 0 ->
                                nTrisToAdd = clipTriangleAgainstPlane(new Coordinates(0.0f, 0.0f, 0.0f), new Coordinates(0.0f, 1.0f, 0.0f), test);
                        case 1 ->
                                nTrisToAdd = clipTriangleAgainstPlane(new Coordinates(0.0f, (float) screenHeight - 1, 0.0f), new Coordinates(0.0f, -1.0f, 0.0f), test);
                        case 2 ->
                                nTrisToAdd = clipTriangleAgainstPlane(new Coordinates(0.0f, 0.0f, 0.0f), new Coordinates(1.0f, 0.0f, 0.0f), test);
                        case 3 ->
                                nTrisToAdd = clipTriangleAgainstPlane(new Coordinates((float) screenWidth - 1, 0.0f, 0.0f), new Coordinates(-1.0f, 0.0f, 0.0f), test);
                    }

                    listTriangles.addAll(nTrisToAdd);
                }
                nNewTriangles = listTriangles.size();
            }

            listTriangles.forEach(it -> {
                g.setColor(it.color());
                int[] xPoints = {(int) it.coordinates()[0].x(), (int) it.coordinates()[1].x(), (int) it.coordinates()[2].x()};
                int[] yPoints = {(int) it.coordinates()[0].y(), (int) it.coordinates()[1].y(), (int) it.coordinates()[2].y()};
                g.fillPolygon(xPoints, yPoints, 3);
            });
        }
    }

    private Triangle offsetIntoScreen(Triangle triRotatedZX) {
        return new Triangle(
                new Coordinates(triRotatedZX.coordinates()[0].x(), triRotatedZX.coordinates()[0].y(), triRotatedZX.coordinates()[0].z() + OFFSET),
                new Coordinates(triRotatedZX.coordinates()[1].x(), triRotatedZX.coordinates()[1].y(), triRotatedZX.coordinates()[1].z() + OFFSET),
                new Coordinates(triRotatedZX.coordinates()[2].x(), triRotatedZX.coordinates()[2].y(), triRotatedZX.coordinates()[2].z() + OFFSET),
                triRotatedZX.color()
        );
    }

    public void moveLeft() {
        float x = camera.x() + (8.0f * 0.01f);
        camera = new Coordinates(x, camera.y(), camera.z());
        repaint();
    }

    public void moveRight() {
        float x = camera.x() - (8.0f * 0.01f);
        camera = new Coordinates(x, camera.y(), camera.z());
        repaint();
    }

    public void moveUp() {
        float y = camera.y() - (8.0f * 0.01f);
        camera = new Coordinates(camera.x(), y, camera.z());
        repaint();
    }

    public void moveDown() {
        float y = camera.y() + (8.0f * 0.01f);
        camera = new Coordinates(camera.x(), y, camera.z());
        repaint();
    }

    public void moveForward() {
        float fElapsedTime = 0.01f;

        Coordinates vForward = lookingDirection.multiply(8.0f * fElapsedTime);
        camera = camera.add(vForward);
        repaint();
    }

    public void moveBackward() {
        float fElapsedTime = 0.01f;

        Coordinates vBackward = lookingDirection.multiply(-8.0f * fElapsedTime);
        camera = camera.add(vBackward);
        repaint();
    }

    public void turnLeft() {
        float fElapsedTime = 0.01f;

        cameraRotationInYZ -= 2.0f * fElapsedTime;
        lookingDirection = new Coordinates((float) Math.sin(cameraRotationInYZ), 0, (float) Math.cos(cameraRotationInYZ)).normalize();
        repaint();
    }

    public void turnRight() {
        float fElapsedTime = 0.01f;

        cameraRotationInYZ += 2.0f * fElapsedTime;
        lookingDirection = new Coordinates((float) Math.sin(cameraRotationInYZ), 0, (float) Math.cos(cameraRotationInYZ)).normalize();
        repaint();
    }

    public void handleKeyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_LEFT -> moveLeft();
            case KeyEvent.VK_RIGHT -> moveRight();
            case KeyEvent.VK_UP -> moveUp();
            case KeyEvent.VK_DOWN -> moveDown();
            case KeyEvent.VK_W -> moveForward();
            case KeyEvent.VK_S -> moveBackward();
            case KeyEvent.VK_A -> turnLeft();
            case KeyEvent.VK_D -> turnRight();
        }
    }


}