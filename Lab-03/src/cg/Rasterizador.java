package cg;

import java.awt.Color;
import java.util.List;

/** Rasterização de retas e polígonos pelo algoritmo do ponto médio (aula G3). */
public final class Rasterizador {

    private Rasterizador() {}

    /**
     * Ponto médio (Bresenham) para os 8 octantes. Só inteiros.
     *
     * <p>A aula deduz o algoritmo para o 1º octante; os demais são reduzidos a ele:
     * <ul>
     *   <li>|m| &gt; 1: troca os papéis de x e y;</li>
     *   <li>dx &lt; 0: troca os extremos (por isso A→B e B→A geram os mesmos pixels);</li>
     *   <li>dy &lt; 0: o passo em y é −1.</li>
     * </ul>
     * Retas horizontais e verticais não precisam de caso especial: depois da
     * redução elas têm dy = 0, então d ≤ 0 sempre e o algoritmo só escolhe E.
     */
    public static void drawLine(Tela t, int x1, int y1, int x2, int y2, Color cor) {
        boolean ingreme = Math.abs(y2 - y1) > Math.abs(x2 - x1);
        if (ingreme) {
            int aux = x1; x1 = y1; y1 = aux;
            aux = x2; x2 = y2; y2 = aux;
        }
        if (x1 > x2) {
            int aux = x1; x1 = x2; x2 = aux;
            aux = y1; y1 = y2; y2 = aux;
        }

        int dx = x2 - x1;
        int dy = Math.abs(y2 - y1);
        int passoY = (y2 >= y1) ? 1 : -1;

        int d = 2 * dy - dx;
        int incE = 2 * dy;
        int incNE = 2 * (dy - dx);

        int x = x1;
        int y = y1;
        plotar(t, x, y, ingreme, cor);
        while (x < x2) {
            if (d <= 0) {
                d += incE;      // escolhe E
            } else {
                d += incNE;     // escolhe NE
                y += passoY;
            }
            x++;
            plotar(t, x, y, ingreme, cor);
        }
    }

    /** Liga vértices consecutivos e fecha o polígono. Arredonda coords. */
    public static void drawPolygon(Tela t, Poligono p, Color cor) {
        List<Ponto> v = p.vertices;
        for (int i = 0; i < v.size(); i++) {
            Ponto a = v.get(i);
            Ponto b = v.get((i + 1) % v.size());
            drawLine(t,
                    (int) Math.round(a.x()), (int) Math.round(a.y()),
                    (int) Math.round(b.x()), (int) Math.round(b.y()),
                    cor);
        }
    }

    /** Desfaz a troca de eixos antes de escrever o pixel. */
    private static void plotar(Tela t, int x, int y, boolean ingreme, Color cor) {
        if (ingreme) {
            t.setPixel(y, x, cor);
        } else {
            t.setPixel(x, y, cor);
        }
    }
}
