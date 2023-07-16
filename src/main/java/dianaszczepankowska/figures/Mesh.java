package dianaszczepankowska.figures;


import java.util.ArrayList;

public class Mesh {
    public ArrayList<Triangle> tris = new ArrayList<>();

    public void add(Triangle t) {
        tris.add(t);
    }
}