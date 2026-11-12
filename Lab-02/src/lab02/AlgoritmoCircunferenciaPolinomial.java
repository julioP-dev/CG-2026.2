package lab02;
 
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
 
/**
 * Método polinomial (equação explícita y = √(R² − x²), aula G4, slides 6 e 7), feito para
 * mostrar suas falhas, a pedido do professor.
 *
 * <p>x varre o quadrante inteiro, de 0 até R, e cada ponto é espelhado só nos eixos
 * (simetria de ordem 4). Não se usa o octante com simetria de ordem 8 porque ele esconde o
 * problema: ali a inclinação da curva é no máximo 1, e o resto da circunferência sairia da
 * troca de x por y. Perto de x = R a tangente é vertical, y cai de vários pixels a cada passo
 * de x e ficam gaps entre os pontos, como mostra o slide 6.
 */
public final class AlgoritmoCircunferenciaPolinomial implements AlgoritmoCircunferencia {
    @Override public String getNome() { return "Circunferência — polinomial"; }
    @Override public String toString() { return getNome(); }
 
    @Override
    public List<PassoAlgoritmo> calcular(int centroX, int centroY, int raio) {
        AlgoritmoCircunferenciaTrigonometrico.validarRaio(raio);
        List<PassoAlgoritmo> passos = new ArrayList<>();
        double xFinal = raio;                  // até o fim do quadrante (o slide usa R/√2 com o octante)
        int incremento = 1;
        int x = 0;
        int iteracao = 0;
        int yAnterior = raio;

        while (x <= xFinal) {
            double yExato = Math.sqrt(raio * raio - x * x);
            int y = (int) Math.round(yExato);
            String descricao = String.format(
                    "%d: x=%d; y=√(R²−x²)=%.6f; arredondado: y=%d",
                    iteracao, x, yExato, y);
            // Quando y cai mais de 1 pixel num passo de x, ficam pixels vazios entre os pontos.
            int salto = yAnterior - y;
            if (salto > 1) {
                descricao += String.format("; GAP: y caiu %d pixels (faltam %d)", salto, salto - 1);
            }
            passos.add(new PassoAlgoritmo(descricao, pixelsQuadrante(centroX, centroY, x, y)));
            yAnterior = y;
            x += incremento;
            iteracao++;
        }
        return passos;
    }

    /** Espelha (x, y) só nos eixos: simetria de ordem 4, sem trocar x por y. */
    private static List<Ponto> pixelsQuadrante(int centroX, int centroY, int x, int y) {
        int[][] offsets = {{x, y}, {-x, y}, {x, -y}, {-x, -y}};
        Set<String> unicos = new LinkedHashSet<>();
        List<Ponto> pixels = new ArrayList<>();
        for (int[] offset : offsets) {
            int px = centroX + offset[0];
            int py = centroY + offset[1];
            if (unicos.add(px + "," + py)) pixels.add(new Ponto(px, py));
        }
        return pixels;
    }
}
