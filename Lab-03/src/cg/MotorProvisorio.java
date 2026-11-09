package cg;

import java.util.ArrayList;
import java.util.List;

/**
 * Motor provisório: compõe matrizes 3×3 em coordenadas homogêneas.
 * Existe só para a interface funcionar até os módulos reais ficarem prontos;
 * pode ser apagado depois que MotorTransformacoes for implementado com eles.
 */
public final class MotorProvisorio implements MotorTransformacoes {

    @Override
    public Poligono aplicar(Poligono poligono, List<Operacao> operacoes) {
        double[][] m = compor(operacoes);
        List<Ponto> vertices = new ArrayList<>();
        for (Ponto p : poligono.vertices) {
            vertices.add(new Ponto(
                    m[0][0] * p.x() + m[0][1] * p.y() + m[0][2],
                    m[1][0] * p.x() + m[1][1] * p.y() + m[1][2]));
        }
        return new Poligono(vertices);
    }

    @Override
    public String matrizComposta(List<Operacao> operacoes) {
        StringBuilder texto = new StringBuilder();
        for (double[] linha : compor(operacoes)) {
            texto.append(String.format("[%8s %8s %8s]\n",
                    numero(linha[0]), numero(linha[1]), numero(linha[2])));
        }
        return texto.toString();
    }

    /** M = Mn · ... · M2 · M1: a primeira operação da lista é a primeira aplicada ao ponto. */
    private static double[][] compor(List<Operacao> operacoes) {
        double[][] m = {{1, 0, 0}, {0, 1, 0}, {0, 0, 1}};
        for (Operacao operacao : operacoes) {
            m = multiplicar(matriz(operacao), m);
        }
        return m;
    }

    private static double[][] matriz(Operacao operacao) {
        double a = operacao.a();
        double b = operacao.b();
        return switch (operacao.tipo()) {
            case TRANSLACAO -> new double[][] {{1, 0, a}, {0, 1, b}, {0, 0, 1}};
            case ESCALA -> new double[][] {{a, 0, 0}, {0, b, 0}, {0, 0, 1}};
            case ROTACAO -> {
                double c = Math.cos(Math.toRadians(a));
                double s = Math.sin(Math.toRadians(a));
                yield new double[][] {{c, -s, 0}, {s, c, 0}, {0, 0, 1}};
            }
            case REFLEXAO_X -> new double[][] {{1, 0, 0}, {0, -1, 0}, {0, 0, 1}};
            case REFLEXAO_Y -> new double[][] {{-1, 0, 0}, {0, 1, 0}, {0, 0, 1}};
            case REFLEXAO_ORIGEM -> new double[][] {{-1, 0, 0}, {0, -1, 0}, {0, 0, 1}};
            case CISALHAMENTO -> new double[][] {{1, a, 0}, {b, 1, 0}, {0, 0, 1}};
        };
    }

    private static double[][] multiplicar(double[][] x, double[][] y) {
        double[][] resultado = new double[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    resultado[i][j] += x[i][k] * y[k][j];
                }
            }
        }
        return resultado;
    }

    /** Até 3 casas decimais, sem "-0". */
    private static String numero(double valor) {
        return String.format("%.3f", Math.round(valor * 1000.0) / 1000.0);
    }
}
