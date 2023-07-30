package dianaszczepankowska.figures;


import static dianaszczepankowska.figures.Coordinates.intersectPlane;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public record Triangle(Coordinates[] coordinates, Color color) {
    public Triangle(Coordinates p0, Coordinates p1, Coordinates p2, Color color) {
        this(new Coordinates[]{p0, p1, p2}, color);
    }

    public List<Triangle> clipTriangleAgainstPlane(Coordinates plane_p, Coordinates plane_n) {
        plane_n = plane_n.normalize();

        List<Coordinates> insidePoints = new ArrayList<>();
        List<Coordinates> outsidePoints = new ArrayList<>();

        float d0 = plane_n.dotProduct(this.coordinates()[0]) - plane_n.dotProduct(plane_p);
        float d1 = plane_n.dotProduct(this.coordinates()[1]) - plane_n.dotProduct(plane_p);
        float d2 = plane_n.dotProduct(this.coordinates()[2]) - plane_n.dotProduct(plane_p);

        if (d0 >= 0) {
            insidePoints.add(this.coordinates()[0]);
        } else {
            outsidePoints.add(this.coordinates()[0]);
        }
        if (d1 >= 0) {
            insidePoints.add(this.coordinates()[1]);
        } else {
            outsidePoints.add(this.coordinates()[1]);
        }
        if (d2 >= 0) {
            insidePoints.add(this.coordinates()[2]);
        } else {
            outsidePoints.add(this.coordinates()[2]);
        }

        List<Triangle> out_triangles = new ArrayList<>();

        if (insidePoints.size() == 0) {
            return out_triangles;
        }

        if (insidePoints.size() == 3) {
            out_triangles.add(this);
            return out_triangles;
        }

        if (insidePoints.size() == 1) {
            Color color = this.color;
            Coordinates c0 = insidePoints.get(0);
            Coordinates c1 = intersectPlane(plane_p, plane_n, insidePoints.get(0), outsidePoints.get(0));
            Coordinates c2 = intersectPlane(plane_p, plane_n, insidePoints.get(0), outsidePoints.get(1));

            out_triangles.add(new Triangle(c0, c1, c2, color));
            return out_triangles;
        }


        Color color = this.color;

        Coordinates c0 = insidePoints.get(0);
        Coordinates c1 = insidePoints.get(1);
        Coordinates c2 = intersectPlane(plane_p, plane_n, insidePoints.get(0), outsidePoints.get(0));
        out_triangles.add(new Triangle(c0, c1, c2, color));

        Coordinates c01 = insidePoints.get(1);
        Coordinates c21 = intersectPlane(plane_p, plane_n, insidePoints.get(1), outsidePoints.get(0));

        out_triangles.add(new Triangle(c01, c2, c21, color));

        return out_triangles;

    }

}