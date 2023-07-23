package dianaszczepankowska.figures;


import java.awt.Color;
import java.util.ArrayList;

public record Figure(ArrayList<Triangle> tris) {
    public Figure(){
        this(new ArrayList<>());
    }

    public void add(Triangle t) {
        tris.add(t);
    }

    public static Figure createCube(float x, float y, float z) {
        Figure figureCube = new Figure();

        figureCube.add(new Triangle(new Coordinates(x + 0, y + 0, z + 0), new Coordinates(x + 0, y + 1, z + 0), new Coordinates(x + 1, y + 1, z + 0), new Color(200, 200, 200, 255)));
        figureCube.add(new Triangle(new Coordinates(x + 0, y + 0, z + 0), new Coordinates(x + 1, y + 1, z + 0), new Coordinates(x + 1, y + 0, z + 0), new Color(200, 200, 200, 255)));

        figureCube.add(new Triangle(new Coordinates(x + 1, y + 0, z + 0), new Coordinates(x + 1, y + 1, z + 0), new Coordinates(x + 1, y + 1, z + 1), new Color(255, 240, 200, 255)));
        figureCube.add(new Triangle(new Coordinates(x + 1, y + 0, z + 0), new Coordinates(x + 1, y + 1, z + 1), new Coordinates(x + 1, y + 0, z + 1), new Color(255, 240, 200, 255)));

        figureCube.add(new Triangle(new Coordinates(x + 1, y + 0, z + 1), new Coordinates(x + 1, y + 1, z + 1), new Coordinates(x + 0, y + 1, z + 1), new Color(255, 200, 200, 255)));
        figureCube.add(new Triangle(new Coordinates(x + 1, y + 0, z + 1), new Coordinates(x + 0, y + 1, z + 1), new Coordinates(x + 0, y + 0, z + 1), new Color(255, 200, 200, 255)));

        figureCube.add(new Triangle(new Coordinates(x + 0, y + 0, z + 1), new Coordinates(x + 0, y + 1, z + 1), new Coordinates(x + 0, y + 1, z + 0), new Color(255, 255, 200, 255)));
        figureCube.add(new Triangle(new Coordinates(x + 0, y + 0, z + 1), new Coordinates(x + 0, y + 1, z + 0), new Coordinates(x + 0, y + 0, z + 0), new Color(255, 255, 200, 255)));

        figureCube.add(new Triangle(new Coordinates(x + 0, y + 1, z + 0), new Coordinates(x + 0, y + 1, z + 1), new Coordinates(x + 1, y + 1, z + 1), new Color(200, 255, 200, 255)));
        figureCube.add(new Triangle(new Coordinates(x + 0, y + 1, z + 0), new Coordinates(x + 1, y + 1, z + 1), new Coordinates(x + 1, y + 1, z + 0), new Color(200, 255, 200, 255)));

        figureCube.add(new Triangle(new Coordinates(x + 0, y + 0, z + 0), new Coordinates(x + 1, y + 0, z + 0), new Coordinates(x + 1, y + 0, z + 1), new Color(245, 245, 220, 255)));
        figureCube.add(new Triangle(new Coordinates(x + 0, y + 0, z + 0), new Coordinates(x + 1, y + 0, z + 1), new Coordinates(x + 0, y + 0, z + 1), new Color(245, 245, 220, 255)));

        return figureCube;
    }
}