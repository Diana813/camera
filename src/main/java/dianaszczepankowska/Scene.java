package dianaszczepankowska;

import dianaszczepankowska.figures.Figure;
import dianaszczepankowska.figures.Triangle;
import dianaszczepankowska.figures.Coordinates;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Scene extends JPanel {
    private final Figure figureCube;
    private final Matrix matProj;
    private float fTheta;
    private final BufferedImage backBuffer;
    private final Graphics2D backBufferGraphics;

    private final int screenWidth;
    private final int screenHeight;

    public Scene(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        figureCube = new Figure();
        matProj = new Matrix();
        fTheta = 0.0f;

        // Create the cube mesh
        figureCube.add(new Triangle(new Coordinates(0, 0, 0), new Coordinates(0, 1, 0), new Coordinates(1, 1, 0), new Color(0, 0, 255, 128)));
        figureCube.add(new Triangle(new Coordinates(0, 0, 0), new Coordinates(1, 1, 0), new Coordinates(1, 0, 0), new Color(0, 255, 0, 128)));

        figureCube.add(new Triangle(new Coordinates(1, 0, 0), new Coordinates(1, 1, 0), new Coordinates(1, 1, 1), new Color(0, 255, 255, 128)));
        figureCube.add(new Triangle(new Coordinates(1, 0, 0), new Coordinates(1, 1, 1), new Coordinates(1, 0, 1), new Color(255, 0, 0, 128)));

        figureCube.add(new Triangle(new Coordinates(1, 0, 1), new Coordinates(1, 1, 1), new Coordinates(0, 1, 1), new Color(255, 0, 255, 128)));
        figureCube.add(new Triangle(new Coordinates(1, 0, 1), new Coordinates(0, 1, 1), new Coordinates(0, 0, 1), new Color(255, 255, 0, 128)));

        figureCube.add(new Triangle(new Coordinates(0, 0, 1), new Coordinates(0, 1, 1), new Coordinates(0, 1, 0), new Color(255, 255, 255, 128)));
        figureCube.add(new Triangle(new Coordinates(0, 0, 1), new Coordinates(0, 1, 0), new Coordinates(0, 0, 0), new Color(128, 128, 128, 128)));

        figureCube.add(new Triangle(new Coordinates(0, 1, 0), new Coordinates(0, 1, 1), new Coordinates(1, 1, 1), new Color(192, 192, 192, 128)));
        figureCube.add(new Triangle(new Coordinates(0, 1, 0), new Coordinates(1, 1, 1), new Coordinates(1, 1, 0), new Color(64, 64, 64, 128)));

        figureCube.add(new Triangle(new Coordinates(0, 0, 0), new Coordinates(1, 0, 0), new Coordinates(1, 0, 1), new Color(128, 128, 255, 128)));
        figureCube.add(new Triangle(new Coordinates(0, 0, 0), new Coordinates(1, 0, 1), new Coordinates(0, 0, 1), new Color(128, 255, 128, 128)));

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
        Matrix matRotZ = new Matrix();
        Matrix matRotX = new Matrix();

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
        for (Triangle tri : figureCube.tris()) {
            Triangle triProjected = new Triangle(
                    new Coordinates(0, 0, 0),
                    new Coordinates(0, 0, 0),
                    new Coordinates(0, 0, 0),
                    tri.color()
            );

            Triangle triRotatedZ = new Triangle(
                    new Coordinates(0, 0, 0),
                    new Coordinates(0, 0, 0),
                    new Coordinates(0, 0, 0),
                    tri.color()
            );
            Triangle triRotatedZX = new Triangle(
                    new Coordinates(0, 0, 0),
                    new Coordinates(0, 0, 0),
                    new Coordinates(0, 0, 0),
                    tri.color()
            );

            // Rotate in Z-Axis
            triRotatedZ.coordinates()[0] = tri.coordinates()[0].multiplyByMatrix(matRotZ);
            triRotatedZ.coordinates()[1] = tri.coordinates()[1].multiplyByMatrix(matRotZ);
            triRotatedZ.coordinates()[2] = tri.coordinates()[2].multiplyByMatrix(matRotZ);

            // Rotate in X-Axis
            triRotatedZX.coordinates()[0] = triRotatedZ.coordinates()[0].multiplyByMatrix(matRotX);
            triRotatedZX.coordinates()[1] = triRotatedZ.coordinates()[1].multiplyByMatrix(matRotX);
            triRotatedZX.coordinates()[2] = triRotatedZ.coordinates()[2].multiplyByMatrix(matRotX);

            // Offset into the screen
            triRotatedZX = new Triangle(
                    new Coordinates(triRotatedZX.coordinates()[0].x(), triRotatedZX.coordinates()[0].y(), triRotatedZX.coordinates()[0].z() + 3.0f),
                    new Coordinates(triRotatedZX.coordinates()[1].x(), triRotatedZX.coordinates()[1].y(), triRotatedZX.coordinates()[1].z() + 3.0f),
                    new Coordinates(triRotatedZX.coordinates()[2].x(), triRotatedZX.coordinates()[2].y(), triRotatedZX.coordinates()[2].z() + 3.0f),
                    triRotatedZX.color()
            );

            // Project triangles from 3D --> 2D
            triProjected.coordinates()[0] = triRotatedZX.coordinates()[0].multiplyByMatrix(matProj);
            triProjected.coordinates()[1] = triRotatedZX.coordinates()[1].multiplyByMatrix(matProj);
            triProjected.coordinates()[2] = triRotatedZX.coordinates()[2].multiplyByMatrix(matProj);

            // Scale into view
            triProjected = new Triangle(
                    new Coordinates(triProjected.coordinates()[0].x() + 1.0f, triProjected.coordinates()[0].y() + 1.0f, triProjected.coordinates()[0].z()),
                    new Coordinates(triProjected.coordinates()[1].x() + 1.0f, triProjected.coordinates()[1].y() + 1.0f, triProjected.coordinates()[1].z()),
                    new Coordinates(triProjected.coordinates()[2].x() + 1.0f, triProjected.coordinates()[2].y() + 1.0f, triProjected.coordinates()[2].z()),
                    triProjected.color()
            );

            triProjected = new Triangle(
                    new Coordinates(triProjected.coordinates()[0].x() * 0.5f * (float) screenWidth, triProjected.coordinates()[0].y() * 0.5f * (float) screenHeight, triProjected.coordinates()[0].z()),
                    new Coordinates(triProjected.coordinates()[1].x() * 0.5f * (float) screenWidth, triProjected.coordinates()[1].y() * 0.5f * (float) screenHeight, triProjected.coordinates()[1].z()),
                    new Coordinates(triProjected.coordinates()[2].x() * 0.5f * (float) screenWidth, triProjected.coordinates()[2].y() * 0.5f * (float) screenHeight, triProjected.coordinates()[2].z()),
                    triProjected.color()
            );


            // Rasterize triangle
            g.setColor(tri.color());
            int[] xPoints = {(int) triProjected.coordinates()[0].x(), (int) triProjected.coordinates()[1].x(), (int) triProjected.coordinates()[2].x()};
            int[] yPoints = {(int) triProjected.coordinates()[0].y(), (int) triProjected.coordinates()[1].y(), (int) triProjected.coordinates()[2].y()};
            g.fillPolygon(xPoints, yPoints, 3);
        }
    }

    private void clearScreen(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
    }
}