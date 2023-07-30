package dianaszczepankowska;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


public class InputHandler extends KeyAdapter {
    private final Camera camera;

    public InputHandler(Camera camera) {
        this.camera = camera;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_LEFT -> camera.moveLeft();
            case KeyEvent.VK_RIGHT -> camera.moveRight();
            case KeyEvent.VK_UP -> camera.moveUp();
            case KeyEvent.VK_DOWN -> camera.moveDown();
            case KeyEvent.VK_W -> camera.moveForward();
            case KeyEvent.VK_S -> camera.moveBackward();
            case KeyEvent.VK_A -> camera.lookLeft();
            case KeyEvent.VK_D -> camera.lookRight();
            case KeyEvent.VK_Z -> camera.zoomIn();
            case KeyEvent.VK_X -> camera.zoomOut();
            case KeyEvent.VK_O -> camera.resetCamera();
            case KeyEvent.VK_U -> camera.lookUp();
            case KeyEvent.VK_B -> camera.lookDown();
            case KeyEvent.VK_I -> camera.rotateZ(2);
            case KeyEvent.VK_K -> camera.rotateZ(-2);
        }
    }
}