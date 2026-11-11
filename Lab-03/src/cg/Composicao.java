package cg;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Motor matemático da Parte 4: composição e aplicação de matrizes homogêneas.
 * Base: aula G5, slides 21–28. Usa pontos em coluna e pré-multiplicação.
 */
public final class Composicao {
    private Composicao() {}

    /**
     * Aplica uma matriz a cada vértice, preservando sua ordem e suas frações.
     * A lista original não é modificada; o arredondamento fica no Rasterizador.
     *
     * @param m matriz composta ou de uma única transformação
     * @param p polígono em coordenadas do mundo
     * @return novo polígono, com uma nova lista de vértices transformados
     * @throws NullPointerException se algum argumento for nulo
     * @throws IllegalArgumentException se um ponto não puder ser transformado
     */
    public static Poligono aplicar(Matriz3 m, Poligono p) {
        Objects.requireNonNull(m, "A matriz é obrigatória.");
        Objects.requireNonNull(p, "O polígono é obrigatório.");
        List<Ponto> vertices = new ArrayList<>(p.vertices.size());
        for (Ponto vertice : p.vertices) {
            vertices.add(m.aplicar(vertice));
        }
        return new Poligono(vertices);
    }

    /**
     * Compõe matrizes na ordem em que devem agir sobre os pontos.
     * Exemplo: compor(A, B, C) retorna C · B · A, aplicando A primeiro.
     *
     * @param ordemDeAplicacao matrizes em ordem cronológica de aplicação
     * @return nova matriz composta; sem argumentos, uma identidade
     * @throws NullPointerException se o vetor ou alguma matriz for nulo
     */
    public static Matriz3 compor(Matriz3... ordemDeAplicacao) {
        Objects.requireNonNull(ordemDeAplicacao, "A ordem de aplicação é obrigatória.");
        Matriz3 resultado = Matriz3.identidade();
        for (Matriz3 matriz : ordemDeAplicacao) {
            Objects.requireNonNull(matriz, "A composição não aceita matriz nula.");
            resultado = matriz.multiplicar(resultado);
        }
        return resultado;
    }

    /**
     * Escala em relação a (px, py), que permanece fixo (G5, slides 22–25).
     * Usa a forma expandida do produto T(p) · S · T(−p):
     * x' = sx·x + (1−sx)·px e y' = sy·y + (1−sy)·py.
     * Essa expansão mantém a Parte 4 independente da implementação da Parte 2.
     *
     * @param sx fator de escala em x
     * @param sy fator de escala em y
     * @param px coordenada x do ponto fixo
     * @param py coordenada y do ponto fixo
     * @return matriz da escala com ponto fixo
     * @throws IllegalArgumentException se algum parâmetro ou resultado não for finito
     */
    public static Matriz3 escalaPontoFixo(double sx, double sy, double px, double py) {
        validarParametros(sx, sy, px, py);
        return new Matriz3(new double[][] {
            {sx, 0, (1 - sx) * px},
            {0, sy, (1 - sy) * py},
            {0, 0, 1}
        });
    }

    /**
     * Rotaciona no sentido anti-horário em torno de (px, py), sem movê-lo.
     * Converte graus para radianos e usa a expansão de T(p) · R · T(−p)
     * apresentada na aula G5, slides 26–27.
     *
     * @param graus ângulo em graus; positivo no sentido anti-horário
     * @param px coordenada x do ponto fixo
     * @param py coordenada y do ponto fixo
     * @return matriz da rotação com ponto fixo
     * @throws IllegalArgumentException se algum parâmetro ou resultado não for finito
     */
    public static Matriz3 rotacaoPontoFixo(double graus, double px, double py) {
        validarParametros(graus, px, py);
        double radianos = Math.toRadians(graus);
        double c = Math.cos(radianos);
        double s = Math.sin(radianos);
        return new Matriz3(new double[][] {
            {c, -s, (1 - c) * px + s * py},
            {s, c, (1 - c) * py - s * px},
            {0, 0, 1}
        });
    }

    /** Rejeita NaN e infinito antes de calcular as matrizes com ponto fixo. */
    private static void validarParametros(double... parametros) {
        for (double parametro : parametros) {
            if (!Double.isFinite(parametro)) {
                throw new IllegalArgumentException("Os parâmetros devem ser finitos.");
            }
        }
    }
}
