package cg;

import java.text.DecimalFormat;

/**
 * Transformação pedida pela interface: o tipo e até dois parâmetros (a, b).
 * Convenções, em coordenadas do mundo (origem no centro, x para a direita, y para cima):
 * <ul>
 *   <li>TRANSLACAO: a = Δx, b = Δy;</li>
 *   <li>ESCALA: a = Sx, b = Sy, em relação à origem;</li>
 *   <li>ROTACAO: a = ângulo em graus, anti-horário, em torno da origem (b não é usado);</li>
 *   <li>REFLEXAO_X: (x, y) → (x, −y); REFLEXAO_Y: (x, y) → (−x, y);
 *       REFLEXAO_ORIGEM: (x, y) → (−x, −y) (a e b não são usados);</li>
 *   <li>CISALHAMENTO: a = Shx, b = Shy, com x' = x + Shx·y e y' = y + Shy·x.</li>
 * </ul>
 */
public record Operacao(Tipo tipo, double a, double b) {

    public enum Tipo {
        TRANSLACAO("Translação"),
        ESCALA("Escala"),
        ROTACAO("Rotação"),
        REFLEXAO_X("Reflexão no eixo X"),
        REFLEXAO_Y("Reflexão no eixo Y"),
        REFLEXAO_ORIGEM("Reflexão na origem"),
        CISALHAMENTO("Cisalhamento");

        private final String nome;

        Tipo(String nome) { this.nome = nome; }

        @Override public String toString() { return nome; }
    }

    /** Texto usado na lista de composição e no passo a passo. */
    public String descricao() {
        return switch (tipo) {
            case TRANSLACAO -> "Translação (Δx=" + numero(a) + "; Δy=" + numero(b) + ")";
            case ESCALA -> "Escala (Sx=" + numero(a) + "; Sy=" + numero(b) + ")";
            case ROTACAO -> "Rotação (" + numero(a) + "°, anti-horário)";
            case CISALHAMENTO -> "Cisalhamento (Shx=" + numero(a) + "; Shy=" + numero(b) + ")";
            default -> tipo.toString();
        };
    }

    @Override public String toString() { return descricao(); }

    /** Formata com até 3 casas decimais e sem "-0". */
    static String numero(double valor) {
        double arredondado = Math.round(valor * 1000.0) / 1000.0;
        return new DecimalFormat("0.###").format(arredondado);
    }
}
