package dianaszczepankowska;

import static dianaszczepankowska.Main.SCREEN_HEIGHT;
import static dianaszczepankowska.Main.SCREEN_WIDTH;
import dianaszczepankowska.figures.Coordinates;


public class Camera {

    private static final float ELAPSED_TIME = 0.01f;

    private static final float NEAR = 0.1f;
    private static final float FAR = 1000.0f;
    private Coordinates position;
    private Coordinates lookingDirection;
    private float rotationY;
    private float rotationZ;
    private float rotationX;
    private float fieldOfViewDegrees;
    private Matrix projectionMatrix4x4;
    private CameraListener listener;


    public Camera() {
        this.position = new Coordinates(0, 0, 0);
        this.lookingDirection = new Coordinates(0, 0, 0);
        this.rotationY = 0;
        this.rotationX = 0;
        this.rotationZ = 0;
        this.fieldOfViewDegrees = 90.0f;

        float aspectRatio = (float) SCREEN_HEIGHT / (float) SCREEN_WIDTH;
        this.projectionMatrix4x4 = Matrix.createProjectionMatrix(fieldOfViewDegrees, aspectRatio, NEAR, FAR);
    }

    public void moveLeft() {
        float x = position.x() + (8.0f * ELAPSED_TIME);
        position = new Coordinates(x, position.y(), position.z());
        if (listener != null) {
            listener.onCameraChange(position, lookingDirection);
        }
    }

    public void moveRight() {
        float x = position.x() - (8.0f * ELAPSED_TIME);
        position = new Coordinates(x, position.y(), position.z());
        if (listener != null) {
            listener.onCameraChange(position, lookingDirection);
        }
    }

    public void moveUp() {
        float y = position.y() + (8.0f * ELAPSED_TIME);
        position = new Coordinates(position.x(), y, position.z());
        if (listener != null) {
            listener.onCameraChange(position, lookingDirection);
        }
    }

    public void moveDown() {
        float y = position.y() - (8.0f * ELAPSED_TIME);
        position = new Coordinates(position.x(), y, position.z());
        if (listener != null) {
            listener.onCameraChange(position, lookingDirection);
        }
    }

    public void moveForward() {
        Coordinates vForward = lookingDirection.multiply(8.0f * ELAPSED_TIME);
        position = position.add(vForward);
        if (listener != null) {
            listener.onCameraChange(position, lookingDirection);
        }
    }

    public void moveBackward() {
        Coordinates vBackward = lookingDirection.multiply(-8.0f * ELAPSED_TIME);
        position = position.add(vBackward);
        if (listener != null) {
            listener.onCameraChange(position, lookingDirection);
        }
    }

    public void lookLeft() {
        rotationY -= 2.0f * ELAPSED_TIME;
        if (listener != null) {
            listener.onCameraChange(position, lookingDirection);
        }
    }

    public void lookRight() {
        rotationY += 2.0f * ELAPSED_TIME;
        if (listener != null) {
            listener.onCameraChange(position, lookingDirection);
        }
    }

    public void lookDown() {
        rotationX -= 2 * ELAPSED_TIME;
        if (listener != null) {
            listener.onCameraChange(position, lookingDirection);
        }
    }

    public void lookUp() {
        rotationX += 2 * ELAPSED_TIME;
        if (listener != null) {
            listener.onCameraChange(position, lookingDirection);
        }
    }

    public void rotateZ(float angle) {
        rotationZ += angle * ELAPSED_TIME;
        if (listener != null) {
            listener.onCameraChange(position, lookingDirection);
        }
    }

    public void zoomIn() {
        float zoomFactor = 1.1f;
        fieldOfViewDegrees /= zoomFactor;
        updateProjectionMatrix();
        if (listener != null) {
            listener.onCameraChange(position, lookingDirection);
        }
    }

    public void zoomOut() {
        float zoomFactor = 0.9f;
        fieldOfViewDegrees /= zoomFactor;
        updateProjectionMatrix();
        if (listener != null) {
            listener.onCameraChange(position, lookingDirection);
        }
    }

    public void resetCamera() {
        position = new Coordinates(0, 0, 0);
        lookingDirection = new Coordinates(0, 0, 1);
        rotationY = 0;
        rotationX = 0;
        rotationZ = 0;
        fieldOfViewDegrees = 90.0f;
        updateProjectionMatrix();
        if (listener != null) {
            listener.onCameraChange(position, lookingDirection);
        }
    }

    private void updateProjectionMatrix() {
        float aspectRatio = (float) SCREEN_HEIGHT / (float) SCREEN_WIDTH;
        this.projectionMatrix4x4 = Matrix.createProjectionMatrix(fieldOfViewDegrees, aspectRatio, NEAR, FAR);
    }

    public Coordinates getPosition() {
        return position;
    }

    public Coordinates getLookingDirection() {
        return lookingDirection;
    }

    public void setLookingDirection(Coordinates lookingDirection) {
        this.lookingDirection = lookingDirection;
    }

    public float getRotationY() {
        return rotationY;
    }


    public float getRotationZ() {
        return rotationZ;
    }

    public float getRotationX() {
        return rotationX;
    }


    public Matrix getProjectionMatrix4x4() {
        return projectionMatrix4x4;
    }

    public void setCameraListener(CameraListener listener) {
        this.listener = listener;
    }

    public void setPosition(Coordinates position) {
        this.position = position;
    }
}