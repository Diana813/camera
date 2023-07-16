package dianaszczepankowska;

import dianaszczepankowska.figures.Mesh;
import dianaszczepankowska.figures.Triangle;
import dianaszczepankowska.figures.Vec3d;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Scene extends JPanel {
    private final Mesh meshCube;
    private final Mat4x4 matProj;
    private float fTheta;
    private final BufferedImage backBuffer;
    private final Graphics2D backBufferGraphics;

    private final int screenWidth;
    private final int screenHeight;

    public Scene(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        meshCube = new Mesh();
        matProj = new Mat4x4();
        fTheta = 0.0f;

        // Create the cube mesh
        meshCube.add(new Triangle(new Vec3d(0, 0, 0), new Vec3d(0, 1, 0), new Vec3d(1, 1, 0), new Color(0, 0, 255, 128)));
        meshCube.add(new Triangle(new Vec3d(0, 0, 0), new Vec3d(1, 1, 0), new Vec3d(1, 0, 0), new Color(0, 255, 0, 128)));

        meshCube.add(new Triangle(new Vec3d(1, 0, 0), new Vec3d(1, 1, 0), new Vec3d(1, 1, 1), new Color(0, 255, 255, 128)));
        meshCube.add(new Triangle(new Vec3d(1, 0, 0), new Vec3d(1, 1, 1), new Vec3d(1, 0, 1), new Color(255, 0, 0, 128)));

        meshCube.add(new Triangle(new Vec3d(1, 0, 1), new Vec3d(1, 1, 1), new Vec3d(0, 1, 1), new Color(255, 0, 255, 128)));
        meshCube.add(new Triangle(new Vec3d(1, 0, 1), new Vec3d(0, 1, 1), new Vec3d(0, 0, 1), new Color(255, 255, 0, 128)));

        meshCube.add(new Triangle(new Vec3d(0, 0, 1), new Vec3d(0, 1, 1), new Vec3d(0, 1, 0), new Color(255, 255, 255, 128)));
        meshCube.add(new Triangle(new Vec3d(0, 0, 1), new Vec3d(0, 1, 0), new Vec3d(0, 0, 0), new Color(128, 128, 128, 128)));

        meshCube.add(new Triangle(new Vec3d(0, 1, 0), new Vec3d(0, 1, 1), new Vec3d(1, 1, 1), new Color(192, 192, 192, 128)));
        meshCube.add(new Triangle(new Vec3d(0, 1, 0), new Vec3d(1, 1, 1), new Vec3d(1, 1, 0), new Color(64, 64, 64, 128)));

        meshCube.add(new Triangle(new Vec3d(0, 0, 0), new Vec3d(1, 0, 0), new Vec3d(1, 0, 1), new Color(128, 128, 255, 128)));
        meshCube.add(new Triangle(new Vec3d(0, 0, 0), new Vec3d(1, 0, 1), new Vec3d(0, 0, 1), new Color(128, 255, 128, 128)));

        // Start the update loop
        Timer timer = new Timer(16, e -> {
            fTheta += 0.02f;
            repaint();
        });
        timer.start();

        // Projection Matrix
        float fNear = 0.1f;
        float fFar = 1000.0f;
        float fFov = 90.0f;
        float fAspectRatio = (float) screenHeight / (float) screenWidth;
        float fFovRad = 1.0f / (float) Math.tan(fFov * 0.5f / 180.0f * 3.14159f);

        matProj.m[0][0] = fAspectRatio * fFovRad;
        matProj.m[1][1] = fFovRad;
        matProj.m[2][2] = fFar / (fFar - fNear);
        matProj.m[3][2] = (-fFar * fNear) / (fFar - fNear);
        matProj.m[2][3] = 1.0f;
        matProj.m[3][3] = 0.0f;

        backBuffer = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_RGB);
        backBufferGraphics = (Graphics2D) backBuffer.getGraphics();
    }

    @Override
    public void paint(Graphics g) {
        draw(backBufferGraphics);
        g.drawImage(backBuffer, 0, 0, this);
    }


    public void draw(Graphics g) {
        super.paint(g);

        clearScreen(g);

        // Set up rotation matrices
        Mat4x4 matRotZ = new Mat4x4();
        Mat4x4 matRotX = new Mat4x4();

        // Rotation Z
        matRotZ.m[0][0] = (float) Math.cos(fTheta);
        matRotZ.m[0][1] = (float) Math.sin(fTheta);
        matRotZ.m[1][0] = -1.0f * (float) Math.sin(fTheta);
        matRotZ.m[1][1] = (float) Math.cos(fTheta);
        matRotZ.m[2][2] = 1.0f;
        matRotZ.m[3][3] = 1.0f;

        // Rotation X
        matRotX.m[0][0] = 1.0f;
        matRotX.m[1][1] = (float) Math.cos(fTheta * 0.5f);
        matRotX.m[1][2] = (float) Math.sin(fTheta * 0.5f);
        matRotX.m[2][1] = -1.0f * (float) Math.sin(fTheta * 0.5f);
        matRotX.m[2][2] = (float) Math.cos(fTheta * 0.5f);
        matRotX.m[3][3] = 1.0f;

        // Draw Triangles
        for (Triangle tri : meshCube.tris) {
            Triangle triProjected = new Triangle(
                    new Vec3d(0, 0, 0),
                    new Vec3d(0, 0, 0),
                    new Vec3d(0, 0, 0),
                    tri.color
            );

            Triangle triRotatedZ = new Triangle(
                    new Vec3d(0, 0, 0),
                    new Vec3d(0, 0, 0),
                    new Vec3d(0, 0, 0),
                    tri.color
            );
            Triangle triRotatedZX = new Triangle(
                    new Vec3d(0, 0, 0),
                    new Vec3d(0, 0, 0),
                    new Vec3d(0, 0, 0),
                    tri.color
            );

            // Rotate in Z-Axis
            triRotatedZ.p[0] = tri.p[0].MultiplyMatrixVector(matRotZ);
            triRotatedZ.p[1] = tri.p[1].MultiplyMatrixVector(matRotZ);
            triRotatedZ.p[2] = tri.p[2].MultiplyMatrixVector(matRotZ);

            // Rotate in X-Axis
            triRotatedZX.p[0] = triRotatedZ.p[0].MultiplyMatrixVector(matRotX);
            triRotatedZX.p[1] = triRotatedZ.p[1].MultiplyMatrixVector(matRotX);
            triRotatedZX.p[2] = triRotatedZ.p[2].MultiplyMatrixVector(matRotX);

            // Offset into the screen
            triRotatedZX.p[0].z = triRotatedZX.p[0].z + 3.0f;
            triRotatedZX.p[1].z = triRotatedZX.p[1].z + 3.0f;
            triRotatedZX.p[2].z = triRotatedZX.p[2].z + 3.0f;

            // Project triangles from 3D --> 2D
            triProjected.p[0] = triRotatedZX.p[0].MultiplyMatrixVector(matProj);
            triProjected.p[1] = triRotatedZX.p[1].MultiplyMatrixVector(matProj);
            triProjected.p[2] = triRotatedZX.p[2].MultiplyMatrixVector(matProj);

            // Scale into view
            triProjected.p[0].x += 1.0f;
            triProjected.p[0].y += 1.0f;
            triProjected.p[1].x += 1.0f;
            triProjected.p[1].y += 1.0f;
            triProjected.p[2].x += 1.0f;
            triProjected.p[2].y += 1.0f;
            triProjected.p[0].x *= 0.5f * (float) screenWidth;
            triProjected.p[0].y *= 0.5f * (float) screenHeight;
            triProjected.p[1].x *= 0.5f * (float) screenWidth;
            triProjected.p[1].y *= 0.5f * (float) screenHeight;
            triProjected.p[2].x *= 0.5f * (float) screenWidth;
            triProjected.p[2].y *= 0.5f * (float) screenHeight;

            // Rasterize triangle
            g.setColor(tri.color);
            int[] xPoints = {(int) triProjected.p[0].x, (int) triProjected.p[1].x, (int) triProjected.p[2].x};
            int[] yPoints = {(int) triProjected.p[0].y, (int) triProjected.p[1].y, (int) triProjected.p[2].y};
            g.fillPolygon(xPoints, yPoints, 3);
        }
    }

    private void clearScreen(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
    }
}