package cg;

import java.util.List;

/** Polígono: vértices em ordem; a última aresta liga o último ao primeiro. */
public class Poligono {
    public final List<Ponto> vertices;

    public Poligono(List<Ponto> vertices) {
        this.vertices = vertices;
    }
}
