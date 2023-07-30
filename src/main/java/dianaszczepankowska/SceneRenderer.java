package dianaszczepankowska;

import static dianaszczepankowska.Main.SCREEN_HEIGHT;
import static dianaszczepankowska.Main.SCREEN_WIDTH;
import dianaszczepankowska.figures.Coordinates;
import dianaszczepankowska.figures.Triangle;
import dianaszczepankowska.utils.Matrix;
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

        for (Triangle triangle : scene) {
            Triangle triangleTransformed = new Triangle(
                    triangle.coordinates()[0].multiplyByMatrix(matWorld),
                    triangle.coordinates()[1].multiplyByMatrix(matWorld),
                    triangle.coordinates()[2].multiplyByMatrix(matWorld),
                    triangle.color()
            );

            Coordinates normal, line1, line2;
            line1 = triangleTransformed.coordinates()[1].subtract(triangleTransformed.coordinates()[0]);
            line2 = triangleTransformed.coordinates()[2].subtract(triangleTransformed.coordinates()[0]);

            normal = line1.crossProduct(line2).normalize();

            Coordinates cameraRay = triangleTransformed.coordinates()[0].subtract(camera.getPosition());

            if (normal.dot(cameraRay) < 0.0f) {
                Triangle triViewed = new Triangle(
                        triangleTransformed.coordinates()[0].multiplyByMatrix(matView),
                        triangleTransformed.coordinates()[1].multiplyByMatrix(matView),
                        triangleTransformed.coordinates()[2].multiplyByMatrix(matView),
                        triangle.color()
                );

                List<Triangle> clippedTriangles = triViewed.clipTriangleToPlane(new Coordinates(0.0f, 0.0f, 0.1f), new Coordinates(0.0f, 0.0f, 1.0f));

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
                                triangles = triangleToTest.clipTriangleToPlane(new Coordinates(0.0f, 0.0f, 0.0f), new Coordinates(0.0f, 1.0f, 0.0f));
                        case 1 ->
                                triangles = triangleToTest.clipTriangleToPlane(new Coordinates(0.0f, (float) SCREEN_HEIGHT - 1, 0.0f), new Coordinates(0.0f, -1.0f, 0.0f));
                        case 2 ->
                                triangles = triangleToTest.clipTriangleToPlane(new Coordinates(0.0f, 0.0f, 0.0f), new Coordinates(1.0f, 0.0f, 0.0f));
                        case 3 ->
                                triangles = triangleToTest.clipTriangleToPlane(new Coordinates((float) SCREEN_WIDTH- 1, 0.0f, 0.0f), new Coordinates(-1.0f, 0.0f, 0.0f));
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

    private Triangle projectTriangle(Triangle triangle, Camera camera) {
        Triangle triangleProjected = new Triangle(
                new Coordinates(0, 0, 0),
                new Coordinates(0, 0, 0),
                new Coordinates(0, 0, 0),
                triangle.color()
        );

        triangleProjected.coordinates()[0] = triangle.coordinates()[0].multiplyByMatrix(camera.getProjectionMatrix4x4());
        triangleProjected.coordinates()[1] = triangle.coordinates()[1].multiplyByMatrix(camera.getProjectionMatrix4x4());
        triangleProjected.coordinates()[2] = triangle.coordinates()[2].multiplyByMatrix(camera.getProjectionMatrix4x4());

        triangleProjected = new Triangle(
                new Coordinates(triangleProjected.coordinates()[0].x() * -1, triangleProjected.coordinates()[0].y() * -1, triangleProjected.coordinates()[0].z()),
                new Coordinates(triangleProjected.coordinates()[1].x() * -1, triangleProjected.coordinates()[1].y() * -1, triangleProjected.coordinates()[0].z()),
                new Coordinates(triangleProjected.coordinates()[2].x() * -1, triangleProjected.coordinates()[2].y() * -1, triangleProjected.coordinates()[0].z()),
                triangleProjected.color()
        );

        Coordinates offsetView = new Coordinates(1, 1, 0);
        triangleProjected.coordinates()[0] = triangleProjected.coordinates()[0].add(offsetView);
        triangleProjected.coordinates()[1] = triangleProjected.coordinates()[1].add(offsetView);
        triangleProjected.coordinates()[2] = triangleProjected.coordinates()[2].add(offsetView);

        triangleProjected = new Triangle(
                new Coordinates(triangleProjected.coordinates()[0].x() * 0.5f * (float) SCREEN_WIDTH, triangleProjected.coordinates()[0].y() * 0.5f * SCREEN_HEIGHT, triangleProjected.coordinates()[0].z()),
                new Coordinates(triangleProjected.coordinates()[1].x() * 0.5f * (float) SCREEN_WIDTH, triangleProjected.coordinates()[1].y() * 0.5f * SCREEN_HEIGHT, triangleProjected.coordinates()[0].z()),
                new Coordinates(triangleProjected.coordinates()[2].x() * 0.5f * (float) SCREEN_WIDTH, triangleProjected.coordinates()[2].y() * 0.5f * SCREEN_HEIGHT, triangleProjected.coordinates()[0].z()),
                triangleProjected.color()
        );

        return triangleProjected;
    }

}