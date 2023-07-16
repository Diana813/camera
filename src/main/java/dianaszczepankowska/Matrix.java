package dianaszczepankowska;

public class Matrix {
    public float[][] m = new float[4][4];

    public Matrix() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                m[i][j] = 0.0f;
            }
        }
    }
}