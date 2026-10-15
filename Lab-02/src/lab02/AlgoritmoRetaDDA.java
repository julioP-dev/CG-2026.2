package lab02;

import java.util.ArrayList;
import java.util.List;

/**
 *implementação do algoritmo DDA (Digital Differential Analyzer): o passo é
 * Length = max(|dx|, |dy|), Xinc = dx/Length e Yinc = dy/Length, e cada
 * pixel é obtido arredondando as coordenadas acumuladas.
 */
public final class AlgoritmoRetaDDA implements AlgoritmoReta {
    @Override public String getNome() { return "Reta — DDA"; }
    @Override public String toString() { return getNome(); }

    @Override
    public List<PassoAlgoritmo> calcular(int x1, int y1, int x2, int y2) {
        List<PassoAlgoritmo> passos = new ArrayList<>();
        int dx = x2 - x1;
        int dy = y2 - y1;
        int length = Math.max(Math.abs(dx), Math.abs(dy));

        if (length == 0) {
            passos.add(new PassoAlgoritmo(
                    String.format("Length=0; extremos coincidem; plota (%d, %d)", x1, y1),
                    pixel(x1, y1)));
            return passos;
        }

        double xInc = (double) dx / length;
        double yInc = (double) dy / length;
        double x = x1;
        double y = y1;

        passos.add(new PassoAlgoritmo(
                String.format("Inicial: Length=%d; Xinc=%.4f; Yinc=%.4f; (X,Y)=(%.4f, %.4f) → pixel (%d, %d)",
                        length, xInc, yInc, x, y, Math.round(x), Math.round(y)),
                pixel((int) Math.round(x), (int) Math.round(y))));

        for (int k = 1; k <= length; k++) {
            x += xInc;
            y += yInc;
            int px = (int) Math.round(x);
            int py = (int) Math.round(y);
            passos.add(new PassoAlgoritmo(
                    String.format("%d: X=%.4f; Y=%.4f → pixel (%d, %d)", k, x, y, px, py),
                    pixel(px, py)));
        }
        return passos;
    }

    private static List<Ponto> pixel(int x, int y) {
        List<Ponto> pixels = new ArrayList<>();
        pixels.add(new Ponto(x, y));
        return pixels;
    }
}
