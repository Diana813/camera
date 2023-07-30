package dianaszczepankowska.figures;


import static dianaszczepankowska.figures.Coordinates.calculatePlaneLineIntersection;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public record Triangle(Coordinates[] coordinates, Color color) {
    public Triangle(Coordinates p0, Coordinates p1, Coordinates p2, Color color) {
        this(new Coordinates[]{p0, p1, p2}, color);
    }

    public List<Triangle> clipTriangleToPlane(Coordinates planePoint, Coordinates planeNormal) {
        planeNormal = planeNormal.normalize();

        List<Coordinates> insidePoints = new ArrayList<>();
        List<Coordinates> outsidePoints = new ArrayList<>();

        float d0 = planeNormal.dot(this.coordinates()[0]) - planeNormal.dot(planePoint);
        float d1 = planeNormal.dot(this.coordinates()[1]) - planeNormal.dot(planePoint);
        float d2 = planeNormal.dot(this.coordinates()[2]) - planeNormal.dot(planePoint);

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

        List<Triangle> outTriangles = new ArrayList<>();

        if (insidePoints.size() == 0) {
            return outTriangles;
        }

        if (insidePoints.size() == 3) {
            outTriangles.add(this);
            return outTriangles;
        }

        if (insidePoints.size() == 1) {
            Color color = this.color;
            Coordinates c0 = insidePoints.get(0);
            Coordinates c1 = calculatePlaneLineIntersection(planePoint, planeNormal, insidePoints.get(0), outsidePoints.get(0));
            Coordinates c2 = calculatePlaneLineIntersection(planePoint, planeNormal, insidePoints.get(0), outsidePoints.get(1));

            outTriangles.add(new Triangle(c0, c1, c2, color));
            return outTriangles;
        }


        Color color = this.color;

        Coordinates c0 = insidePoints.get(0);
        Coordinates c1 = insidePoints.get(1);
        Coordinates c2 = calculatePlaneLineIntersection(planePoint, planeNormal, insidePoints.get(0), outsidePoints.get(0));
        outTriangles.add(new Triangle(c0, c1, c2, color));

        Coordinates c01 = insidePoints.get(1);
        Coordinates c21 = calculatePlaneLineIntersection(planePoint, planeNormal, insidePoints.get(1), outsidePoints.get(0));

        outTriangles.add(new Triangle(c01, c2, c21, color));

        return outTriangles;

    }

}