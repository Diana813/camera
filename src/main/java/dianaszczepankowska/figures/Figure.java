package dianaszczepankowska.figures;


import java.util.ArrayList;

public record Figure(ArrayList<Triangle> tris) {
    public Figure(){
        this(new ArrayList<>());
    }

    public void add(Triangle t) {
        tris.add(t);
    }
}