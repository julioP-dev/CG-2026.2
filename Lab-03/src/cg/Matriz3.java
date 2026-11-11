package cg;

import java.util.Objects;

/**
 * Matriz 3x3 em coordenadas homogêneas, com ponto em coluna: P' = M · P.
 * Base: aula G5, slides 12–17 e 21. Os cálculos usam double e não arredondam.
 *
 * <p>O campo público segue o contrato do grupo. Quem o editar deve preservar
 * as três linhas de três valores finitos. Multiplicar e aplicar não alteram
 * as matrizes nem os pontos recebidos.
 */
public class Matriz3 {
    /** Elementos indexados por {@code m[linha][coluna]}, conforme o contrato. */
    public final double[][] m = new double[3][3];

    /** Cria uma matriz nula; para uma transformação neutra, use {@link #identidade()}. */
    public Matriz3() {}

    /**
     * Copia os elementos de uma matriz 3x3, sem compartilhar suas linhas.
     *
     * @param valores elementos em ordem de linha e coluna
     * @throws IllegalArgumentException se a dimensão não for 3x3 ou houver NaN/infinito
     */
    public Matriz3(double[][] valores) {
        if (valores == null || valores.length != 3) {
            throw new IllegalArgumentException("A matriz deve ter dimensão 3x3.");
        }
        for (int i = 0; i < 3; i++) {
            if (valores[i] == null || valores[i].length != 3) {
                throw new IllegalArgumentException("A matriz deve ter dimensão 3x3.");
            }
            for (int j = 0; j < 3; j++) {
                if (!Double.isFinite(valores[i][j])) {
                    throw new IllegalArgumentException("Os elementos da matriz devem ser finitos.");
                }
                m[i][j] = valores[i][j];
            }
        }
    }

    /**
     * Retorna uma nova identidade I, tal que I · P = P e I · M = M · I = M.
     *
     * @return matriz com diagonal igual a 1 e demais elementos iguais a 0
     */
    public static Matriz3 identidade() {
        return new Matriz3(new double[][] {{1, 0, 0}, {0, 1, 0}, {0, 0, 1}});
    }

    /**
     * Calcula {@code this · outra}, pela soma dos produtos de linha por coluna.
     * Com pontos em coluna, o produto aplica {@code outra} primeiro.
     *
     * @param outra matriz à direita do produto
     * @return nova matriz, com C[i][j] = soma(this[i][k] · outra[k][j])
     * @throws NullPointerException se outra for nula
     * @throws IllegalArgumentException se o produto contiver um valor não finito
     */
    public Matriz3 multiplicar(Matriz3 outra) {
        Objects.requireNonNull(outra, "A outra matriz é obrigatória.");
        double[][] resultado = new double[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    resultado[i][j] += m[i][k] * outra.m[k][j];
                }
            }
        }
        return new Matriz3(resultado);
    }

    /**
     * Multiplica M por (x, y, 1) e converte (X, Y, W) para (X/W, Y/W).
     * Nas transformações afins da atividade, W = 1. A divisão também permite
     * outras representações homogêneas do mesmo ponto (G5, slides 14–16).
     *
     * @param p ponto cartesiano com coordenadas finitas
     * @return novo ponto cartesiano, sem arredondamento para pixel
     * @throws NullPointerException se o ponto for nulo
     * @throws IllegalArgumentException se houver coordenada não finita ou W = 0
     */
    public Ponto aplicar(Ponto p) {
        Objects.requireNonNull(p, "O ponto é obrigatório.");
        if (!Double.isFinite(p.x()) || !Double.isFinite(p.y())) {
            throw new IllegalArgumentException("As coordenadas do ponto devem ser finitas.");
        }
        double x = m[0][0] * p.x() + m[0][1] * p.y() + m[0][2];
        double y = m[1][0] * p.x() + m[1][1] * p.y() + m[1][2];
        double w = m[2][0] * p.x() + m[2][1] * p.y() + m[2][2];
        if (w == 0 || !Double.isFinite(w)) {
            throw new IllegalArgumentException("W deve ser finito e diferente de zero.");
        }
        x /= w;
        y /= w;
        if (!Double.isFinite(x) || !Double.isFinite(y)) {
            throw new IllegalArgumentException("A transformação produziu coordenadas não finitas.");
        }
        return new Ponto(x, y);
    }
}
