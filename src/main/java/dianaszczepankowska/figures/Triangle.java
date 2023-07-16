package dianaszczepankowska.figures;


import java.awt.Color;

public record Triangle(Coordinates[] coordinates, Color color) {
    public Triangle(Coordinates p0, Coordinates p1, Coordinates p2, Color color) {
        this(new Coordinates[]{p0, p1, p2}, color);
    }
}