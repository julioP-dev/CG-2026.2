package cg;

/**
 * Matrizes da Parte 3, conforme a aula G5, slides 18–20.
 * Todos os métodos criam uma nova matriz para P' = M · (x, y, 1).
 * A aplicação aos vértices é responsabilidade de {@link Composicao}.
 */
public final class ReflexaoCisalhamento {
    private ReflexaoCisalhamento() {}

    /** @return reflexão no eixo x: mantém x e troca o sinal de y, (x, y) → (x, −y) */
    public static Matriz3 reflexaoX() {
        return new Matriz3(new double[][] {{1, 0, 0}, {0, -1, 0}, {0, 0, 1}});
    }

    /** @return reflexão no eixo y: troca o sinal de x e mantém y, (x, y) → (−x, y) */
    public static Matriz3 reflexaoY() {
        return new Matriz3(new double[][] {{-1, 0, 0}, {0, 1, 0}, {0, 0, 1}});
    }

    /**
     * Inverte ambas as coordenadas, como uma rotação de 180° na origem.
     * Dedução: compor as reflexões nos eixos x e y produz diagonal (−1, −1, 1).
     *
     * @return matriz que leva (x, y) a (−x, −y)
     */
    public static Matriz3 reflexaoOrigem() {
        return new Matriz3(new double[][] {{-1, 0, 0}, {0, -1, 0}, {0, 0, 1}});
    }

    /**
     * Espelha na reta y = x. A troca de coordenadas mantém os pontos dessa
     * reta fixos e inverte a direção perpendicular a ela.
     * Equivale a R(45°) · reflexaoX() · R(−45°).
     *
     * @return matriz que leva (x, y) a (y, x)
     */
    public static Matriz3 reflexaoYeX() {
        return new Matriz3(new double[][] {{0, 1, 0}, {1, 0, 0}, {0, 0, 1}});
    }

    /**
     * Desloca x proporcionalmente à distância ao eixo x, mantendo y.
     *
     * @param shx fator real finito; x' = x + shx · y
     * @return matriz do cisalhamento na direção x
     * @throws IllegalArgumentException se o fator não for finito
     */
    public static Matriz3 cisalhamentoX(double shx) {
        return new Matriz3(new double[][] {{1, shx, 0}, {0, 1, 0}, {0, 0, 1}});
    }

    /**
     * Desloca y proporcionalmente à distância ao eixo y, mantendo x.
     *
     * @param shy fator real finito; y' = y + shy · x
     * @return matriz do cisalhamento na direção y
     * @throws IllegalArgumentException se o fator não for finito
     */
    public static Matriz3 cisalhamentoY(double shy) {
        return new Matriz3(new double[][] {{1, 0, 0}, {shy, 1, 0}, {0, 0, 1}});
    }
}
