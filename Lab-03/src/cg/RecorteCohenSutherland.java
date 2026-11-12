package cg;

import java.util.ArrayList;
import java.util.List;

/**
 * Recorte de segmentos de reta pela janela, algoritmo de Cohen–Sutherland (aula G5, slides 37 a 46).
 * Cada ponto recebe um código de 4 bits, na ordem do slide 38:
 * bit 1 = acima, bit 2 = abaixo, bit 3 = direita, bit 4 = esquerda.
 */
public final class RecorteCohenSutherland {
    public static final int ACIMA = 0b1000;
    public static final int ABAIXO = 0b0100;
    public static final int DIREITA = 0b0010;
    public static final int ESQUERDA = 0b0001;

    /** Parte do segmento que sobrou dentro da janela. */
    public record Segmento(Ponto a, Ponto b) {}

    /** Segmento recortado (null quando foi rejeitado) e o que aconteceu em cada passo. */
    public record Resultado(Segmento segmento, List<String> passos) {}

    private final double xMin, xMax, yMin, yMax;

    /** @throws IllegalArgumentException se o mínimo não for menor que o máximo */
    public RecorteCohenSutherland(double xMin, double xMax, double yMin, double yMax) {
        if (!(xMin < xMax && yMin < yMax)) {
            throw new IllegalArgumentException("Na janela, o mínimo deve ser menor que o máximo em x e em y.");
        }
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
    }

    /** Código de região (slide 39): cada bit vale 1 quando o ponto está daquele lado de fora. */
    public int codigo(Ponto p) {
        int c = 0;
        if (p.y() > yMax) c |= ACIMA;
        if (p.y() < yMin) c |= ABAIXO;
        if (p.x() > xMax) c |= DIREITA;
        if (p.x() < xMin) c |= ESQUERDA;
        return c;
    }

    /** Recorta o segmento de a até b pela janela. */
    public Resultado recortar(Ponto a, Ponto b) {
        List<String> passos = new ArrayList<>();
        int codigoA = codigo(a);
        int codigoB = codigo(b);
        while (true) {
            passos.add("A=" + texto(a) + " [" + bits(codigoA) + "]  B=" + texto(b) + " [" + bits(codigoB) + "]");
            // Aceitação trivial: os dois extremos dentro da janela.
            if ((codigoA | codigoB) == 0) {
                passos.add("aceito: os dois códigos são 0000");
                return new Resultado(new Segmento(a, b), passos);
            }
            // Rejeição trivial: os dois extremos do mesmo lado de fora (E lógico diferente de 0000).
            if ((codigoA & codigoB) != 0) {
                passos.add("rejeitado: " + bits(codigoA) + " E " + bits(codigoB) + " = "
                        + bits(codigoA & codigoB) + " ≠ 0000");
                return new Resultado(null, passos);
            }
            // Senão, troca um extremo de fora pela interseção com a borda e testa de novo.
            if (codigoA != 0) {
                a = intersecao(a, b, codigoA, "A", passos);
                codigoA = codigo(a);
            } else {
                b = intersecao(b, a, codigoB, "B", passos);
                codigoB = codigo(b);
            }
        }
    }

    /**
     * Interseção da reta de p1 (o extremo de fora) até p2 com a borda do primeiro bit ligado
     * (slides 42 e 43): borda vertical x = a dá t = (a − x1)/(x2 − x1) e y = y1 + t·(y2 − y1);
     * borda horizontal y = b dá t = (b − y1)/(y2 − y1) e x = x1 + t·(x2 − x1).
     */
    private Ponto intersecao(Ponto p1, Ponto p2, int codigo, String nome, List<String> passos) {
        double x;
        double y;
        String borda;
        if ((codigo & ACIMA) != 0) {
            y = yMax;
            double t = (y - p1.y()) / (p2.y() - p1.y());
            x = p1.x() + t * (p2.x() - p1.x());
            borda = "bit 1 (acima), borda y = " + Operacao.numero(yMax);
        } else if ((codigo & ABAIXO) != 0) {
            y = yMin;
            double t = (y - p1.y()) / (p2.y() - p1.y());
            x = p1.x() + t * (p2.x() - p1.x());
            borda = "bit 2 (abaixo), borda y = " + Operacao.numero(yMin);
        } else if ((codigo & DIREITA) != 0) {
            x = xMax;
            double t = (x - p1.x()) / (p2.x() - p1.x());
            y = p1.y() + t * (p2.y() - p1.y());
            borda = "bit 3 (direita), borda x = " + Operacao.numero(xMax);
        } else {
            x = xMin;
            double t = (x - p1.x()) / (p2.x() - p1.x());
            y = p1.y() + t * (p2.y() - p1.y());
            borda = "bit 4 (esquerda), borda x = " + Operacao.numero(xMin);
        }
        Ponto novo = new Ponto(x, y);
        passos.add(nome + ": " + borda + " → novo " + nome + " = " + texto(novo));
        return novo;
    }

    /** Código com 4 dígitos, por exemplo 0101. */
    static String bits(int codigo) {
        return String.format("%4s", Integer.toBinaryString(codigo)).replace(' ', '0');
    }

    private static String texto(Ponto p) {
        return "(" + Operacao.numero(p.x()) + "; " + Operacao.numero(p.y()) + ")";
    }
}
