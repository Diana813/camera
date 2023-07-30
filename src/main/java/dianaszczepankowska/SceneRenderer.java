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
    private final Camera camera;

    public SceneRenderer(Camera camera) {
        this.camera = camera;
    }

    public void drawTriangles(List<Triangle> scene, Graphics g) {
        float theta = 0;
        Matrix matWorld = createWorldMatrix(theta);
        Matrix matView = handleCameraOrientation(camera);

        List<Triangle> trianglesToDraw = new ArrayList<>();

        for (Triangle tri : scene) {
            Triangle triTransformed = new Triangle(
                    tri.coordinates()[0].multiplyByMatrix(matWorld),
                    tri.coordinates()[1].multiplyByMatrix(matWorld),
                    tri.coordinates()[2].multiplyByMatrix(matWorld),
                    tri.color()
            );

            Coordinates normal, line1, line2;
            line1 = triTransformed.coordinates()[1].subtract(triTransformed.coordinates()[0]);
            line2 = triTransformed.coordinates()[2].subtract(triTransformed.coordinates()[0]);

            normal = line1.crossProduct(line2).normalize();

            Coordinates cameraRay = triTransformed.coordinates()[0].subtract(camera.getPosition());

            if (normal.dotProduct(cameraRay) < 0.0f) {
                Triangle triViewed = new Triangle(
                        triTransformed.coordinates()[0].multiplyByMatrix(matView),
                        triTransformed.coordinates()[1].multiplyByMatrix(matView),
                        triTransformed.coordinates()[2].multiplyByMatrix(matView),
                        tri.color()
                );

                List<Triangle> clippedTriangles = triViewed.clipTriangleAgainstPlane(new Coordinates(0.0f, 0.0f, 0.1f), new Coordinates(0.0f, 0.0f, 1.0f));

                for (Triangle clippedTriangle : clippedTriangles) {
                    Triangle triProjected = projectTriangle(clippedTriangle, camera);
                    trianglesToDraw.add(triProjected);
                }
            }
        }

        draw(g, trianglesToDraw);
    }

    public void clearScreen(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    private void draw(Graphics g, List<Triangle> trianglesToDraw) {
        trianglesToDraw.sort((t1, t2) -> {
            float z1 = (t1.coordinates()[0].z() + t1.coordinates()[1].z() + t1.coordinates()[2].z()) / 3.0f;
            float z2 = (t2.coordinates()[0].z() + t2.coordinates()[1].z() + t2.coordinates()[2].z()) / 3.0f;
            return Float.compare(z2, z1);
        });

        clearScreen(g);
        for (Triangle triangleToRaster : trianglesToDraw) {
            LinkedList<Triangle> listTriangles = new LinkedList<>();
            listTriangles.add(triangleToRaster);
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

    private Matrix createWorldMatrix(float theta) {
        Matrix rotationX = Matrix.createRotationXMatrix(theta);
        Matrix rotationZ = Matrix.createRotationZMatrix(theta);
        Matrix matTrans = Matrix.createTranslationMatrix(0, 0, 5);

        return Matrix.createIdentityMatrix()
                .multiplyMatrix(rotationZ)
                .multiplyMatrix(rotationX)
                .multiplyMatrix(matTrans);
    }

    private Matrix handleCameraOrientation(Camera camera) {
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

        return matCamera.inverse();
    }

    private Triangle projectTriangle(Triangle tri, Camera camera) {
        Triangle triProjected = new Triangle(
                new Coordinates(0, 0, 0),
                new Coordinates(0, 0, 0),
                new Coordinates(0, 0, 0),
                tri.color()
        );

        triProjected.coordinates()[0] = tri.coordinates()[0].multiplyByMatrix(camera.getProjectionMatrix4x4());
        triProjected.coordinates()[1] = tri.coordinates()[1].multiplyByMatrix(camera.getProjectionMatrix4x4());
        triProjected.coordinates()[2] = tri.coordinates()[2].multiplyByMatrix(camera.getProjectionMatrix4x4());

        triProjected = new Triangle(
                new Coordinates(triProjected.coordinates()[0].x() * -1, triProjected.coordinates()[0].y() * -1, triProjected.coordinates()[0].z()),
                new Coordinates(triProjected.coordinates()[1].x() * -1, triProjected.coordinates()[1].y() * -1, triProjected.coordinates()[0].z()),
                new Coordinates(triProjected.coordinates()[2].x() * -1, triProjected.coordinates()[2].y() * -1, triProjected.coordinates()[0].z()),
                triProjected.color()
        );

        Coordinates offsetView = new Coordinates(1, 1, 0);
        triProjected.coordinates()[0] = triProjected.coordinates()[0].add(offsetView);
        triProjected.coordinates()[1] = triProjected.coordinates()[1].add(offsetView);
        triProjected.coordinates()[2] = triProjected.coordinates()[2].add(offsetView);

        triProjected = new Triangle(
                new Coordinates(triProjected.coordinates()[0].x() * 0.5f * (float) SCREEN_WIDTH, triProjected.coordinates()[0].y() * 0.5f * SCREEN_HEIGHT, triProjected.coordinates()[0].z()),
                new Coordinates(triProjected.coordinates()[1].x() * 0.5f * (float) SCREEN_WIDTH, triProjected.coordinates()[1].y() * 0.5f * SCREEN_HEIGHT, triProjected.coordinates()[0].z()),
                new Coordinates(triProjected.coordinates()[2].x() * 0.5f * (float) SCREEN_WIDTH, triProjected.coordinates()[2].y() * 0.5f * SCREEN_HEIGHT, triProjected.coordinates()[0].z()),
                triProjected.color()
        );

        return triProjected;
    }

}