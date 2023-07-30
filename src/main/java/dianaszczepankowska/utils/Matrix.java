package dianaszczepankowska.utils;

import dianaszczepankowska.figures.Coordinates;

public record Matrix(float[][] m) {

    public Matrix() {
        this(new float[4][4]);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                m[i][j] = 0.0f;
            }
        }
    }

    public static Matrix createIdentityMatrix() {
        Matrix matrix4x4 = new Matrix();
        matrix4x4.m[0][0] = 1;
        matrix4x4.m[1][1] = 1;
        matrix4x4.m[2][2] = 1;
        matrix4x4.m[3][3] = 1;
        return matrix4x4;
    }

    public static Matrix createRotationXMatrix(float rotationAngleRad) {
        Matrix rotationX = new Matrix();
        rotationX.m[0][0] = 1.0f;
        rotationX.m[1][1] = (float) Math.cos(rotationAngleRad * 0.5f);
        rotationX.m[1][2] = (float) Math.sin(rotationAngleRad * 0.5f);
        rotationX.m[2][1] = -1.0f * (float) Math.sin(rotationAngleRad * 0.5f);
        rotationX.m[2][2] = (float) Math.cos(rotationAngleRad * 0.5f);
        rotationX.m[3][3] = 1.0f;
        return rotationX;
    }

    public static Matrix createRotationZMatrix(float rotationAngleRad) {
        Matrix rotationZ = new Matrix();
        rotationZ.m[0][0] = (float) Math.cos(rotationAngleRad);
        rotationZ.m[0][1] = (float) Math.sin(rotationAngleRad);
        rotationZ.m[1][0] = -1.0f * (float) Math.sin(rotationAngleRad);
        rotationZ.m[1][1] = (float) Math.cos(rotationAngleRad);
        rotationZ.m[2][2] = 1.0f;
        rotationZ.m[3][3] = 1.0f;
        return rotationZ;
    }

    public static Matrix createRotationYMatrix(float rotationAngleRad) {
        Matrix rotationY = new Matrix();
        rotationY.m[0][0] = (float) Math.cos(rotationAngleRad);
        rotationY.m[0][2] = (float) Math.sin(rotationAngleRad);
        rotationY.m[2][0] = -1.0f * (float) Math.sin(rotationAngleRad);
        rotationY.m[1][1] = 1.0f;
        rotationY.m[2][2] = (float) Math.cos(rotationAngleRad);
        rotationY.m[3][3] = 1.0f;
        return rotationY;
    }

    public static Matrix createTranslationMatrix(float x, float y, float z) {
        Matrix translationMatrix4x4 = new Matrix();
        translationMatrix4x4.m[0][0] = 1;
        translationMatrix4x4.m[1][1] = 1;
        translationMatrix4x4.m[2][2] = 1;
        translationMatrix4x4.m[3][3] = 1;
        translationMatrix4x4.m[3][0] = x;
        translationMatrix4x4.m[3][1] = y;
        translationMatrix4x4.m[3][2] = z;
        return translationMatrix4x4;
    }

    public static Matrix createProjectionMatrix(float fFovDegrees, float fAspectRatio, float fNear, float fFar) {
        Matrix projectionMatrix4x4 = new Matrix();
        float fFovRad = 1.0f / (float) Math.tan(fFovDegrees * 0.5f / 180.0f * 3.14159f);

        projectionMatrix4x4.m[0][0] = fAspectRatio * fFovRad;
        projectionMatrix4x4.m[1][1] = fFovRad;
        projectionMatrix4x4.m[2][2] = fFar / (fFar - fNear);
        projectionMatrix4x4.m[3][2] = (-fFar * fNear) / (fFar - fNear);
        projectionMatrix4x4.m[2][3] = 1.0f;
        projectionMatrix4x4.m[3][3] = 0.0f;

        return projectionMatrix4x4;
    }

    public Matrix multiplyMatrix(Matrix matrix) {
        Matrix result = new Matrix();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result.m[j][i] = this.m[j][0] * matrix.m[0][i] + this.m[j][1] * matrix.m[1][i] + this.m[j][2] * matrix.m[2][i] + this.m[j][3] * matrix.m[3][i];
            }
        }
        return result;
    }


    public static Matrix pointAt(Coordinates position, Coordinates target, Coordinates up) {
        Coordinates newForward = target.subtract(position).normalize();

        Coordinates a = newForward.multiply(up.dot(newForward));
        Coordinates newUp = up.subtract(a).normalize();

        Coordinates newRight = newUp.crossProduct(newForward);

        Matrix matrix = new Matrix();
        matrix.m[0][0] = newRight.x();
        matrix.m[0][1] = newRight.y();
        matrix.m[0][2] = newRight.z();
        matrix.m[0][3] = 0.0f;
        matrix.m[1][0] = newUp.x();
        matrix.m[1][1] = newUp.y();
        matrix.m[1][2] = newUp.z();
        matrix.m[1][3] = 0.0f;
        matrix.m[2][0] = newForward.x();
        matrix.m[2][1] = newForward.y();
        matrix.m[2][2] = newForward.z();
        matrix.m[2][3] = 0.0f;
        matrix.m[3][0] = position.x();
        matrix.m[3][1] = position.y();
        matrix.m[3][2] = position.z();
        matrix.m[3][3] = 1.0f;
        return matrix;
    }

    public Matrix inverse(){
        Matrix matrix = new Matrix();

        matrix.m[0][0] = this.m[0][0];
        matrix.m[0][1] = this.m[1][0];
        matrix.m[0][2] = this.m[2][0];
        matrix.m[0][3] = 0.0f;
        matrix.m[1][0] = this.m[0][1];
        matrix.m[1][1] = this.m[1][1];
        matrix.m[1][2] = this.m[2][1];
        matrix.m[1][3] = 0.0f;
        matrix.m[2][0] = this.m[0][2];
        matrix.m[2][1] = this.m[1][2];
        matrix.m[2][2] = this.m[2][2];
        matrix.m[2][3] = 0.0f;
        matrix.m[3][0] = -(this.m[3][0] * matrix.m[0][0] + this.m[3][1] * matrix.m[1][0] + this.m[3][2] * matrix.m[2][0]);
        matrix.m[3][1] = -(this.m[3][0] * matrix.m[0][1] + this.m[3][1] * matrix.m[1][1] + this.m[3][2] * matrix.m[2][1]);
        matrix.m[3][2] = -(this.m[3][0] * matrix.m[0][2] + this.m[3][1] * matrix.m[1][2] + this.m[3][2] * matrix.m[2][2]);
        matrix.m[3][3] = 1.0f;

       return matrix;
    }

}