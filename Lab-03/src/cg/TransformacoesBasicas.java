package cg;

/**
 * Parte 2: transformações básicas (aula G5).
 * Cada método devolve uma nova Matriz3 para usar com ponto em coluna: P' = M · P,
 * onde P = (x, y, 1). As matrizes são as do slide 17 (e do livro, Eq. 17 a 22).
 */
public final class TransformacoesBasicas {
    private TransformacoesBasicas() {}

    /**
     * Translação: x' = x + tx e y' = y + ty.
     *
     * @param tx deslocamento em x
     * @param ty deslocamento em y
     */
    public static Matriz3 translacao(double tx, double ty) {
        return new Matriz3(new double[][] {
            {1, 0, tx},
            {0, 1, ty},
            {0, 0, 1}
        });
    }

    /**
     * Escala em relação à origem: x' = sx · x e y' = sy · y.
     *
     * @param sx fator em x
     * @param sy fator em y
     */
    public static Matriz3 escala(double sx, double sy) {
        return new Matriz3(new double[][] {
            {sx, 0, 0},
            {0, sy, 0},
            {0, 0, 1}
        });
    }

    /**
     * Rotação em torno da origem:
     * x' = x·cos θ − y·sen θ e y' = x·sen θ + y·cos θ.
     * Ângulo positivo gira no sentido anti-horário (com y para cima).
     *
     * @param graus ângulo θ em graus
     */
    public static Matriz3 rotacao(double graus) {
        double radianos = Math.toRadians(graus);
        double cos = Math.cos(radianos);
        double sen = Math.sin(radianos);
        return new Matriz3(new double[][] {
            {cos, -sen, 0},
            {sen, cos, 0},
            {0, 0, 1}
        });
    }
}
