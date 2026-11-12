package cg;

/**
 * Transformação do gato de Arnold (aula G5, slides 34 a 36): Γ(x, y) = (x + y, x + 2y) mod 1.
 * Numa imagem de N × N pixels, com i e j de 0 a N − 1, a mesma conta fica
 * Γ(i, j) = ((i + j) mod N, (i + 2j) mod N): a matriz [[1, 1], [1, 2]] seguida do módulo.
 */
public final class GatoDeArnold {
    private GatoDeArnold() {}

    /** Uma aplicação de Γ ao pixel (i, j) de uma imagem N × N. */
    public static int[] aplicar(int i, int j, int n) {
        return new int[] {(i + j) % n, (i + 2 * j) % n};
    }

    /**
     * Período: o menor k > 0 em que aplicar Γ k vezes devolve a imagem original, isto é,
     * [[1, 1], [1, 2]]^k ≡ I (mod N). Ele existe porque o determinante é 1:
     * Γ só troca os N² pixels de lugar (é uma permutação), e toda permutação acaba se repetindo.
     */
    public static int periodo(int n) {
        long a = 1, b = 0, c = 0, d = 1;   // potência acumulada [[a, b], [c, d]], começa na identidade
        int k = 0;
        do {
            // [[1, 1], [1, 2]] · [[a, b], [c, d]] = [[a + c, b + d], [a + 2c, b + 2d]], tudo mod N
            long novoA = (a + c) % n;
            long novoB = (b + d) % n;
            long novoC = (a + 2 * c) % n;
            long novoD = (b + 2 * d) % n;
            a = novoA;
            b = novoB;
            c = novoC;
            d = novoD;
            k++;
        } while (!(a == 1 % n && b == 0 && c == 0 && d == 1 % n));
        return k;
    }
}
