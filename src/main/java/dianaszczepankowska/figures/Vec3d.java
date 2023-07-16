package dianaszczepankowska.figures;

import dianaszczepankowska.Mat4x4;

public class Vec3d {
    public float x, y, z;

    public Vec3d(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3d MultiplyMatrixVector(Mat4x4 matrix) {
        float x = this.x * matrix.m[0][0] + this.y * matrix.m[1][0] + this.z * matrix.m[2][0] + matrix.m[3][0];
        float y = this.x * matrix.m[0][1] + this.y * matrix.m[1][1] + this.z * matrix.m[2][1] + matrix.m[3][1];
        float z = this.x * matrix.m[0][2] + this.y * matrix.m[1][2] + this.z * matrix.m[2][2] + matrix.m[3][2];
        float w = this.x * matrix.m[0][3] + this.y * matrix.m[1][3] + this.z * matrix.m[2][3] + matrix.m[3][3];

        if (w != 0.0f) {
            x /= w;
            y /= w;
            z /= w;
        }

        return new Vec3d(x, y, z);
    }
}

