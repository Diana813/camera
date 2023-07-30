package dianaszczepankowska;

import dianaszczepankowska.figures.Coordinates;

public interface CameraListener {
    void onCameraChange(Coordinates camera, Coordinates lookingDirection);
}