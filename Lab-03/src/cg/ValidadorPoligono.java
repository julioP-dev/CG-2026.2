package cg;

import java.util.List;

/**
 * Confere se uma lista de vértices forma um polígono simples:
 * pelo menos 3 vértices, nenhum vértice repetido, com área, e sem arestas que se cruzem ou se toquem.
 * Polígonos côncavos são permitidos; polígonos com arestas cruzadas (como um "laço") não.
 */
public final class ValidadorPoligono {
    private ValidadorPoligono() {}

    /** @throws IllegalArgumentException com a explicação do problema, se não for um polígono simples */
    public static void validar(List<Ponto> v) {
        int n = v.size();
        if (n < 3) {
            throw new IllegalArgumentException("Defina um polígono com ao menos 3 vértices.");
        }
        // Nenhum vértice pode se repetir, esteja ele perto ou longe do outro na lista.
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (v.get(i).equals(v.get(j))) {
                    throw new IllegalArgumentException("Os vértices " + (i + 1) + " e " + (j + 1)
                            + " são iguais. Um polígono não pode ter vértices repetidos.");
                }
            }
        }
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                boolean vizinhas = j == i + 1 || (i == 0 && j == n - 1);
                Ponto a = v.get(i), b = v.get((i + 1) % n);
                Ponto c = v.get(j), d = v.get((j + 1) % n);
                if (vizinhas) {
                    if (voltaSobreSi(a, b, c, d, j == i + 1)) {
                        throw new IllegalArgumentException("Duas arestas seguidas se sobrepõem (aresta "
                                + (i + 1) + " e aresta " + (j + 1) + ").");
                    }
                } else if (seTocam(a, b, c, d)) {
                    throw new IllegalArgumentException("As arestas " + (i + 1) + " e " + (j + 1)
                            + " se cruzam. O polígono não pode ter arestas cruzadas.");
                }
            }
        }
        // Por último: um laço simétrico também tem área com sinal 0, mas já foi recusado acima.
        if (area(v) == 0) {
            throw new IllegalArgumentException("O polígono tem área zero (vértices alinhados).");
        }
    }

    /** Área com sinal pela fórmula do cadarço (0 quando os vértices estão todos alinhados). */
    static double area(List<Ponto> v) {
        double soma = 0;
        for (int i = 0; i < v.size(); i++) {
            Ponto a = v.get(i), b = v.get((i + 1) % v.size());
            soma += a.x() * b.y() - b.x() * a.y();
        }
        return soma / 2;
    }

    /**
     * Calcula u.x·w.y - u.y·w.x, com u = (b - a) e w = (c - a).
     * Escrevendo u e w por módulo e ângulo, u = |u|(cos α, sen α) e w = |w|(cos β, sen β), o número
     * vale |u||w|·(cos α·sen β - sen α·cos β) = |u||w|·sen(β - α) (identidade da aula G5, slide 9).
     * β - α é quanto se gira u, no sentido anti-horário, para ele apontar na direção de w.
     * Logo o sinal indica o sentido: positivo = anti-horário, negativo = horário,
     * zero = a, b e c alinhados. (É o determinante da matriz 2x2 [u; w] e o "produto vetorial 2D".)
     * Exemplo: a=(0,0), b=(10,0), c=(5,5) dá 10·5 - 0·5 = 50 (anti-horário).
     */
    private static double orientacao(Ponto a, Ponto b, Ponto c) {
        return (b.x() - a.x()) * (c.y() - a.y()) - (b.y() - a.y()) * (c.x() - a.x());
    }

    /** Para c colinear com a→b: c está dentro do retângulo do segmento? */
    private static boolean dentro(Ponto a, Ponto b, Ponto c) {
        return Math.min(a.x(), b.x()) <= c.x() && c.x() <= Math.max(a.x(), b.x())
                && Math.min(a.y(), b.y()) <= c.y() && c.y() <= Math.max(a.y(), b.y());
    }

    /** Os segmentos a-b e c-d têm algum ponto em comum (cruzam ou se encostam)? */
    private static boolean seTocam(Ponto a, Ponto b, Ponto c, Ponto d) {
        double o1 = orientacao(a, b, c), o2 = orientacao(a, b, d);
        double o3 = orientacao(c, d, a), o4 = orientacao(c, d, b);
        if (o1 * o2 < 0 && o3 * o4 < 0) return true;
        return (o1 == 0 && dentro(a, b, c)) || (o2 == 0 && dentro(a, b, d))
                || (o3 == 0 && dentro(c, d, a)) || (o4 == 0 && dentro(c, d, b));
    }

    /**
     * Arestas vizinhas já compartilham um vértice; o problema é só quando a segunda volta
     * por cima da primeira (ângulo de 180° no vértice comum).
     */
    private static boolean voltaSobreSi(Ponto a, Ponto b, Ponto c, Ponto d, boolean sequencia) {
        // Vértice comum: b = c (arestas i e i+1) ou a = d (última e primeira aresta).
        Ponto comum = sequencia ? b : a;
        Ponto p = sequencia ? a : b;   // outro extremo da primeira aresta
        Ponto q = sequencia ? d : c;   // outro extremo da segunda aresta
        double cruz = (p.x() - comum.x()) * (q.y() - comum.y()) - (p.y() - comum.y()) * (q.x() - comum.x());
        double escalar = (p.x() - comum.x()) * (q.x() - comum.x()) + (p.y() - comum.y()) * (q.y() - comum.y());
        return cruz == 0 && escalar > 0;
    }
}
