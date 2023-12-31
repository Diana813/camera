package dianaszczepankowska.figures;

import dianaszczepankowska.utils.Matrix;

public record Coordinates(float x, float y, float z, float w) {

    public Coordinates(float x, float y, float z) {
        this(x, y, z, 1);
    }

    public Coordinates multiplyByMatrix(Matrix matrix) {
        float x = this.x * matrix.m()[0][0] + this.y * matrix.m()[1][0] + this.z * matrix.m()[2][0] + matrix.m()[3][0];
        float y = this.x * matrix.m()[0][1] + this.y * matrix.m()[1][1] + this.z * matrix.m()[2][1] + matrix.m()[3][1];
        float z = this.x * matrix.m()[0][2] + this.y * matrix.m()[1][2] + this.z * matrix.m()[2][2] + matrix.m()[3][2];
        float w = this.x * matrix.m()[0][3] + this.y * matrix.m()[1][3] + this.z * matrix.m()[2][3] + matrix.m()[3][3];

        if (w != 0.0f) {
            x /= w;
            y /= w;
            z /= w;
        }

        return new Coordinates(x, y, z, w);
    }

    public Coordinates add(Coordinates position) {
        return new Coordinates(this.x + position.x, this.y + position.y, this.z + position.z);
    }

    public Coordinates subtract(Coordinates position) {
        return new Coordinates(this.x - position.x, this.y - position.y, this.z - position.z);
    }

    public Coordinates multiply(float f) {
        return new Coordinates(this.x * f, this.y * f, this.z * f);
    }

    public Coordinates divide(float f) {
        return new Coordinates(this.x / f, this.y / f, this.z / f);
    }

    public Coordinates normalize() {
        float l = vectorLength();
        return divide(l);
    }

    public float dot(Coordinates coordinates) {
        return this.x * coordinates.x + this.y * coordinates.y + this.z * coordinates.z;
    }

    public Coordinates crossProduct(Coordinates coordinates) {
        float x = this.y * coordinates.z - this.z * coordinates.y;
        float y = this.z * coordinates.x - this.x * coordinates.z;
        float z = this.x * coordinates.y - this.y * coordinates.x;
        return new Coordinates(x, y, z);
    }

    public float vectorLength() {
        return (float) Math.sqrt(this.dot(this));
    }


    public static Coordinates calculatePlaneLineIntersection(Coordinates planePoint, Coordinates planeNormal, Coordinates lineStart, Coordinates lineEnd) {
        planeNormal = planeNormal.normalize();
        float planeD = - planeNormal.dot(planePoint);
        float distanceFromLineStartToPlane = lineStart.dot(planeNormal);
        float distanceFromLineEndToPlane = lineEnd.dot(planeNormal);
        float intersectionParameter = (-planeD - distanceFromLineStartToPlane) / (distanceFromLineEndToPlane - distanceFromLineStartToPlane);
        Coordinates lineStartToEnd = lineEnd.subtract(lineStart);
        Coordinates lineToIntersect = lineStartToEnd.multiply(intersectionParameter);
        return lineStart.add(lineToIntersect);
    }

}

