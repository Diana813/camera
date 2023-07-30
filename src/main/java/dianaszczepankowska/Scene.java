package dianaszczepankowska;

import static dianaszczepankowska.Main.SCREEN_HEIGHT;
import static dianaszczepankowska.Main.SCREEN_WIDTH;
import dianaszczepankowska.figures.Coordinates;
import dianaszczepankowska.figures.Figure;
import dianaszczepankowska.figures.Triangle;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

public class Scene extends JPanel implements CameraListener {

    private final Camera camera;
    private final SceneRenderer renderer;
    private final List<Figure> cubes;
    private final BufferedImage backBuffer;
    private final Graphics2D backBufferGraphics;

    public Scene() {
        this.camera = new Camera();
        this.renderer = new SceneRenderer(camera);
        this.cubes = new ArrayList<>();

        camera.setCameraListener(this);

        for (int i = 0; i < 20; i++) {
            Figure cube;
            if (i % 2 == 0) {
                cube = Figure.createCube(0, 0, (float) (1.5 * i));
            } else {
                cube = Figure.createCube(3, 0, (float) (1.5 * (i - 1)));
            }

            cubes.add(cube);
        }

        backBuffer = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_RGB);
        backBufferGraphics = (Graphics2D) backBuffer.getGraphics();

        setFocusable(true);
        addKeyListener(new InputHandler(camera));
    }

    @Override
    public void paint(Graphics g) {
        draw(backBufferGraphics);
        g.drawImage(backBuffer, 0, 0, this);
    }

    @Override
    public void onCameraChange(Coordinates position, Coordinates lookingDirection) {
        this.camera.setPosition(position);
        this.camera.setLookingDirection(lookingDirection);
        repaint();
    }

    public void draw(Graphics g) {
        super.paint(g);
        renderer.clearScreen(g);
        List<Triangle> triangleArrayList = new ArrayList<>();
        cubes.stream().map(Figure::triangles).forEach(triangleArrayList::addAll);
        renderer.drawTriangles(triangleArrayList, g);
    }
}