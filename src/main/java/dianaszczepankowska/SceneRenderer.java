package dianaszczepankowska;

import static dianaszczepankowska.Main.SCREEN_HEIGHT;
import static dianaszczepankowska.Main.SCREEN_WIDTH;
import dianaszczepankowska.figures.Coordinates;
import dianaszczepankowska.figures.Triangle;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SceneRenderer {
    private static final int OFFSET = 0;

    private final Camera camera;

    public SceneRenderer(Camera camera) {
        this.camera = camera;
    }


    void drawTriangles(List<Triangle> scene, Graphics g) {

        float theta = 0;
        Matrix rotationX = Matrix.createRotationXMatrix(theta);
        Matrix rotationZ = Matrix.createRotationZMatrix(theta);

        Matrix matTrans = Matrix.createTranslationMatrix(0, 0, 5);

        Matrix matWorld = Matrix.createIdentityMatrix().multiplyMatrix(rotationZ).multiplyMatrix(rotationX).multiplyMatrix(matTrans);

        Coordinates up = new Coordinates(0, 1, 0);
        Coordinates target = new Coordinates(0, 0, 1);
        Matrix rotationYMatrix = Matrix.createRotationYMatrix(camera.getRotationY());
        Matrix rotationXMatrix = Matrix.createRotationXMatrix(camera.getRotationX());

        camera.setLookingDirection(target.multiply(0.8f * 1.5f));

        if(camera.getRotationY() != 0) {
            camera.setLookingDirection(camera.getLookingDirection().multiplyByMatrix(rotationYMatrix));
        }
        if (camera.getRotationX() != 0) {
            camera.setLookingDirection(camera.getLookingDirection().multiplyByMatrix(rotationXMatrix));
        }


        Matrix rotationZMatrix = Matrix.createRotationZMatrix(camera.getRotationZ());


        target = camera.getPosition().add(camera.getLookingDirection());
        Matrix matCamera = Matrix.pointAt(camera.getPosition(), target, up).multiplyMatrix(rotationZMatrix);

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

            Coordinates cameraRay = triTransformed.coordinates()[0].subtract(camera.getPosition());


            if (normal.dotProduct(cameraRay) < 0.0f) {
                triViewed.coordinates()[0] = triTransformed.coordinates()[0].multiplyByMatrix(matView);
                triViewed.coordinates()[1] = triTransformed.coordinates()[1].multiplyByMatrix(matView);
                triViewed.coordinates()[2] = triTransformed.coordinates()[2].multiplyByMatrix(matView);


                List<Triangle> clippedTriangles = triViewed.clipTriangleAgainstPlane(new Coordinates(0.0f, 0.0f, 0.1f), new Coordinates(0.0f, 0.0f, 1.0f));

                for (Triangle clippedTriangle : clippedTriangles) {
                    // Project triangles from 3D --> 2D
                    triProjected.coordinates()[0] = clippedTriangle.coordinates()[0].multiplyByMatrix(camera.getProjectionMatrix4x4());
                    triProjected.coordinates()[1] = clippedTriangle.coordinates()[1].multiplyByMatrix(camera.getProjectionMatrix4x4());
                    triProjected.coordinates()[2] = clippedTriangle.coordinates()[2].multiplyByMatrix(camera.getProjectionMatrix4x4());

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
                            new Coordinates(triProjected.coordinates()[0].x() * 0.5f * (float) SCREEN_WIDTH, triProjected.coordinates()[0].y() * 0.5f * SCREEN_HEIGHT, triProjected.coordinates()[0].z()),
                            new Coordinates(triProjected.coordinates()[1].x() * 0.5f * (float) SCREEN_WIDTH, triProjected.coordinates()[1].y() * 0.5f * SCREEN_HEIGHT, triProjected.coordinates()[0].z()),
                            new Coordinates(triProjected.coordinates()[2].x() * 0.5f * (float) SCREEN_WIDTH, triProjected.coordinates()[2].y() * 0.5f * SCREEN_HEIGHT, triProjected.coordinates()[0].z()),
                            triProjected.color()
                    );

                    trianglesToDraw.add(triProjected);
                }

            }
        }

        draw(g, trianglesToDraw);

    }

    private Triangle offsetIntoScreen(Triangle triRotatedZX) {
        return new Triangle(
                new Coordinates(triRotatedZX.coordinates()[0].x(), triRotatedZX.coordinates()[0].y(), triRotatedZX.coordinates()[0].z() + OFFSET),
                new Coordinates(triRotatedZX.coordinates()[1].x(), triRotatedZX.coordinates()[1].y(), triRotatedZX.coordinates()[1].z() + OFFSET),
                new Coordinates(triRotatedZX.coordinates()[2].x(), triRotatedZX.coordinates()[2].y(), triRotatedZX.coordinates()[2].z() + OFFSET),
                triRotatedZX.color()
        );
    }


    private void draw(Graphics g, List<Triangle> trianglesToDraw) {
        trianglesToDraw.sort((t1, t2) -> {
            float z1 = (t1.coordinates()[0].z() + t1.coordinates()[1].z() + t1.coordinates()[2].z()) / 3.0f;
            float z2 = (t2.coordinates()[0].z() + t2.coordinates()[1].z() + t2.coordinates()[2].z()) / 3.0f;
            return Float.compare(z2, z1);
        });

        clearScreen(g);
        for (Triangle triToRaster : trianglesToDraw) {
            LinkedList<Triangle> listTriangles = new LinkedList<>();
            listTriangles.add(triToRaster);
            int newTriangles = 1;

            for (int p = 0; p < 4; p++) {
                List<Triangle> triangles = new ArrayList<>();
                while (newTriangles > 0) {
                    Triangle triangleToTest = listTriangles.removeLast();
                    newTriangles--;

                    switch (p) {
                        case 0 ->
                                triangles = triangleToTest.clipTriangleAgainstPlane(new Coordinates(0.0f, 0.0f, 0.0f), new Coordinates(0.0f, 1.0f, 0.0f));
                        case 1 ->
                                triangles = triangleToTest.clipTriangleAgainstPlane(new Coordinates(0.0f, (float) SCREEN_HEIGHT - 1, 0.0f), new Coordinates(0.0f, -1.0f, 0.0f));
                        case 2 ->
                                triangles = triangleToTest.clipTriangleAgainstPlane(new Coordinates(0.0f, 0.0f, 0.0f), new Coordinates(1.0f, 0.0f, 0.0f));
                        case 3 ->
                                triangles = triangleToTest.clipTriangleAgainstPlane(new Coordinates((float) SCREEN_WIDTH- 1, 0.0f, 0.0f), new Coordinates(-1.0f, 0.0f, 0.0f));
                    }

                    listTriangles.addAll(triangles);
                }
                newTriangles = listTriangles.size();
            }

            listTriangles.forEach(it -> {
                g.setColor(it.color());
                int[] xPoints = {(int) it.coordinates()[0].x(), (int) it.coordinates()[1].x(), (int) it.coordinates()[2].x()};
                int[] yPoints = {(int) it.coordinates()[0].y(), (int) it.coordinates()[1].y(), (int) it.coordinates()[2].y()};
                g.fillPolygon(xPoints, yPoints, 3);
            });
        }
    }

    void clearScreen(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
    }

}