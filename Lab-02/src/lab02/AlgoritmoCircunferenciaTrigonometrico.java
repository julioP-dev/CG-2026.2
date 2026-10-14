package lab02;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Amostra um octante com x=r cos(theta), y=r sen(theta) e completa
 * a circunferência usando a simetria de ordem oito apresentada nos slides.
 */
public final class AlgoritmoCircunferenciaTrigonometrico implements AlgoritmoCircunferencia {
    private static final double FIM_OCTANTE = Math.PI / 4.0;

    @Override public String getNome() { return "Circunferência — trigonométrico"; }
    @Override public String toString() { return getNome(); }

    @Override
    public List<PassoAlgoritmo> calcular(int centroX, int centroY, int raio) {
        validarRaio(raio);
        List<PassoAlgoritmo> passos = new ArrayList<>();
        if (raio == 0) {
            passos.add(new PassoAlgoritmo("θ = 0; raio = 0; plota o centro.",
                    pixelsSimetricos(centroX, centroY, 0, 0)));
            return passos;
        }

        // Δθ=1/r radiano aproxima um pixel de arco por amostra; inclui θ=π/4.
        double incrementoTheta = 1.0 / raio;
        double theta = 0.0;
        int anteriorX = -1;
        int anteriorY = -1;
        int iteracao = 0;
        while (true) {
            int x = (int) Math.round(raio * Math.cos(theta));
            int y = (int) Math.round(raio * Math.sin(theta));
            if (x != anteriorX || y != anteriorY || passos.isEmpty()) {
                List<Ponto> pixels = pixelsSimetricos(centroX, centroY, x, y);
                String descricao = String.format(
                        "%d: θ=%.6f rad; x=round(%d·cos θ)=%d; y=round(%d·sen θ)=%d; Δθ=%.6f",
                        iteracao, theta, raio, x, raio, y, incrementoTheta);
                passos.add(new PassoAlgoritmo(descricao, pixels));
                anteriorX = x;
                anteriorY = y;
                iteracao++;
            }
            if (theta >= FIM_OCTANTE) break;
            theta = Math.min(theta + incrementoTheta, FIM_OCTANTE);
        }
        return passos;
    }

    static List<Ponto> pixelsSimetricos(int centroX, int centroY, int x, int y) {
        int[][] offsets = {
                {x, y}, {y, x}, {y, -x}, {x, -y},
                {-x, -y}, {-y, -x}, {-y, x}, {-x, y}
        };
        Set<String> unicos = new LinkedHashSet<>();
        List<Ponto> pixels = new ArrayList<>();
        for (int[] offset : offsets) {
            int px = centroX + offset[0];
            int py = centroY + offset[1];
            if (unicos.add(px + "," + py)) pixels.add(new Ponto(px, py));
        }
        return pixels;
    }

    static void validarRaio(int raio) {
        if (raio < 0) throw new IllegalArgumentException("O raio deve ser um inteiro não negativo.");
    }
}
