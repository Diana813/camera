package dianaszczepankowska.figures;


import java.awt.Color;

public class Triangle {
    public Vec3d[] p = new Vec3d[3];
    public Color color;

    public Triangle(Vec3d p0, Vec3d p1, Vec3d p2, Color color) {
        p[0] = p0;
        p[1] = p1;
        p[2] = p2;
        this.color = color;
    }
}