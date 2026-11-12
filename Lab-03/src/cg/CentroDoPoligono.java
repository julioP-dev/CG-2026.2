package cg;

import java.util.ArrayList;
import java.util.List;

/**
 * O enunciado pede o polígono na origem do sistema cartesiano. Aqui isso quer dizer:
 * o centroide (centro da área) do polígono fica em (0, 0).
 */
public final class CentroDoPoligono {
    /** Diferenças menores que isto são só arredondamento: o polígono já está na origem. */
    private static final double TOLERANCIA = 1e-6;

    private CentroDoPoligono() {}

    /**
     * Centroide da área de um polígono simples, pela fórmula do cadarço:
     * com ci = xi·y(i+1) − x(i+1)·yi e A = (Σ ci) / 2,
     * Cx = Σ (xi + x(i+1))·ci / (6A) e Cy = Σ (yi + y(i+1))·ci / (6A).
     *
     * @throws IllegalArgumentException se o polígono não tiver área
     */
    public static Ponto centroide(List<Ponto> v) {
        double somaC = 0;   // soma dos ci, que vale 2A
        double somaX = 0;
        double somaY = 0;
        for (int i = 0; i < v.size(); i++) {
            Ponto p = v.get(i);
            Ponto q = v.get((i + 1) % v.size());
            double c = p.x() * q.y() - q.x() * p.y();
            somaC += c;
            somaX += (p.x() + q.x()) * c;
            somaY += (p.y() + q.y()) * c;
        }
        if (somaC == 0) {
            throw new IllegalArgumentException("Um polígono sem área não tem centroide.");
        }
        // 6A = 3 · (2A) = 3 · somaC
        return new Ponto(somaX / (3 * somaC), somaY / (3 * somaC));
    }

    /** O centroide está em (0, 0)? */
    public static boolean estaNaOrigem(List<Ponto> v) {
        Ponto c = centroide(v);
        return Math.abs(c.x()) < TOLERANCIA && Math.abs(c.y()) < TOLERANCIA;
    }

    /** Aplica a translação T(−Cx, −Cy) a todos os vértices: o centroide passa a ser a origem. */
    public static List<Ponto> levarParaOrigem(List<Ponto> v) {
        Ponto c = centroide(v);
        Matriz3 translacao = TransformacoesBasicas.translacao(-c.x(), -c.y());
        List<Ponto> naOrigem = new ArrayList<>();
        for (Ponto p : v) {
            naOrigem.add(translacao.aplicar(p));
        }
        return naOrigem;
    }
}
