package cg;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;

/**
 * Motor oficial que adapta as operações da interface aos módulos do contrato.
 * Delega a composição a {@link Composicao} e as reflexões e cisalhamentos a
 * {@link ReflexaoCisalhamento}. A Parte 2 pode fornecer suas matrizes pelo
 * construtor com função, sem duplicar suas fórmulas neste motor.
 */
public final class MotorComposicao implements MotorTransformacoes {
    private final Function<Operacao, Matriz3> transformacoesBasicas;

    /** Cria o motor das Partes 3 e 4, sem habilitar operações da Parte 2. */
    public MotorComposicao() {
        transformacoesBasicas = null;
    }

    /**
     * Conecta o módulo da Parte 2 ao motor oficial.
     * A função é chamada somente para TRANSLACAO, ESCALA e ROTACAO.
     * Deve retornar a matriz correspondente à operação, usando ângulos em graus.
     *
     * @param transformacoesBasicas função que delega aos métodos da Parte 2
     * @throws NullPointerException se a função for nula
     */
    public MotorComposicao(Function<Operacao, Matriz3> transformacoesBasicas) {
        this.transformacoesBasicas = Objects.requireNonNull(transformacoesBasicas,
                "O fornecedor de matrizes da Parte 2 é obrigatório.");
    }

    /**
     * Informa quais operações podem ser apresentadas pela interface.
     *
     * @param tipo tipo de operação a consultar
     * @return false para operações básicas enquanto a Parte 2 não estiver conectada
     */
    @Override
    public boolean suporta(Operacao.Tipo tipo) {
        Objects.requireNonNull(tipo, "O tipo da operação é obrigatório.");
        return switch (tipo) {
            case TRANSLACAO, ESCALA, ROTACAO -> transformacoesBasicas != null;
            default -> true;
        };
    }

    /**
     * Compõe as operações e transforma o polígono original uma única vez.
     *
     * @param poligono polígono em coordenadas do mundo, que permanece inalterado
     * @param operacoes operações em ordem de aplicação
     * @return novo polígono transformado, sem arredondamento
     * @throws IllegalArgumentException se uma operação não estiver disponível
     */
    @Override
    public Poligono aplicar(Poligono poligono, List<Operacao> operacoes) {
        return Composicao.aplicar(compor(operacoes), poligono);
    }

    /**
     * Retorna a matriz efetivamente usada no cálculo, com três casas decimais.
     * A formatação é apenas visual e não modifica os valores da transformação.
     *
     * @param operacoes operações em ordem de aplicação
     * @return três linhas de texto com os elementos da matriz composta
     */
    @Override
    public String matrizComposta(List<Operacao> operacoes) {
        StringBuilder texto = new StringBuilder();
        for (double[] linha : compor(operacoes).m) {
            texto.append(String.format(Locale.ROOT, "[%10.3f %10.3f %10.3f]%n",
                    semZeroNegativo(linha[0]), semZeroNegativo(linha[1]),
                    semZeroNegativo(linha[2])));
        }
        return texto.toString();
    }

    /** Traduz as operações e delega a ordem da multiplicação à classe Composicao. */
    private Matriz3 compor(List<Operacao> operacoes) {
        Objects.requireNonNull(operacoes, "A lista de operações é obrigatória.");
        Matriz3[] matrizes = new Matriz3[operacoes.size()];
        for (int i = 0; i < operacoes.size(); i++) {
            matrizes[i] = matriz(Objects.requireNonNull(operacoes.get(i),
                    "A lista não pode conter operação nula."));
        }
        return Composicao.compor(matrizes);
    }

    /** Obtém a matriz do módulo responsável, preservando as convenções de Operacao. */
    private Matriz3 matriz(Operacao operacao) {
        if (!suporta(operacao.tipo())) {
            throw new IllegalArgumentException("A operação " + operacao.tipo()
                    + " depende da integração das matrizes da Parte 2.");
        }
        return switch (operacao.tipo()) {
            case TRANSLACAO, ESCALA, ROTACAO -> Objects.requireNonNull(
                    transformacoesBasicas.apply(operacao), "A Parte 2 deve retornar uma matriz.");
            case REFLEXAO_X -> ReflexaoCisalhamento.reflexaoX();
            case REFLEXAO_Y -> ReflexaoCisalhamento.reflexaoY();
            case REFLEXAO_ORIGEM -> ReflexaoCisalhamento.reflexaoOrigem();
            case REFLEXAO_Y_IGUAL_X -> ReflexaoCisalhamento.reflexaoYeX();
            case CISALHAMENTO_X -> ReflexaoCisalhamento.cisalhamentoX(operacao.a());
            case CISALHAMENTO_Y -> ReflexaoCisalhamento.cisalhamentoY(operacao.a());
            // Compatibilidade com o tipo anterior: usa as coordenadas originais
            // em ambas as equações, portanto não equivale a compor X e Y.
            case CISALHAMENTO -> new Matriz3(new double[][] {
                {1, operacao.a(), 0}, {operacao.b(), 1, 0}, {0, 0, 1}
            });
        };
    }

    /** Evita exibir -0.000 sem arredondar a matriz usada nos cálculos. */
    private static double semZeroNegativo(double valor) {
        return Math.abs(valor) < 0.0005 ? 0.0 : valor;
    }
}
