package cg;

import java.util.ArrayList;
import java.util.List;

/**
 * Testes numéricos das Partes 3 e 4, executáveis sem JUnit e sem abrir janelas.
 * As matrizes básicas usadas aqui são dados de teste, não uma implementação
 * da Parte 2. Execute {@code java -cp out cg.TesteTransformacoes}.
 */
public final class TesteTransformacoes {
    private static final double EPS = 1e-9;
    private static int verificacoes;

    private TesteTransformacoes() {}

    /** Executa a suíte; qualquer divergência encerra o programa com AssertionError. */
    public static void main(String[] args) {
        testarMultiplicacao();
        testarCoordenadasHomogeneas();
        testarReflexoes();
        testarCisalhamentos();
        testarOrdemEComutatividade();
        testarPontosFixos();
        testarPoligonoEPrecisao();
        testarMotorOficial();
        testarEntradasInvalidas();
        System.out.println("Partes 3 e 4: " + verificacoes + " verificações OK.");
    }

    /** Produto 3x3 geral, incluindo a última linha, identidades e ausência de mutações. */
    private static void testarMultiplicacao() {
        double[][] valores = {{1, 2, 3}, {0, -1, 4}, {2, 0, 1}};
        Matriz3 a = new Matriz3(valores);
        valores[0][0] = 99;
        perto(a.m[0][0], 1, "construtor deve copiar os dados");
        Matriz3 b = new Matriz3(new double[][] {{2, 0, 1}, {3, 1, 0}, {0, 4, 1}});
        matriz(a.multiplicar(b), new Matriz3(new double[][] {
            {8, 14, 4}, {-3, 15, 4}, {4, 4, 3}
        }), "produto linha por coluna");
        matriz(a.multiplicar(Matriz3.identidade()), a, "identidade à direita");
        matriz(Matriz3.identidade().multiplicar(a), a, "identidade à esquerda");
        perto(a.m[0][0], 1, "produto preserva a matriz esquerda");
        perto(b.m[0][0], 2, "produto preserva a matriz direita");
        Matriz3 copia = Composicao.compor(a);
        copia.m[0][0] = -77;
        perto(a.m[0][0], 1, "composição não compartilha a matriz de entrada");
        matriz(Composicao.compor(), Matriz3.identidade(), "composição vazia");
        Matriz3 c = ReflexaoCisalhamento.cisalhamentoY(0.5);
        matriz(a.multiplicar(b).multiplicar(c), a.multiplicar(b.multiplicar(c)),
                "associatividade");
    }

    /** Confere W = 1, W = 2, W negativo e W dependente do ponto. */
    private static void testarCoordenadasHomogeneas() {
        Ponto p = new Ponto(1.25, -2.5);
        ponto(Matriz3.identidade().aplicar(p), p.x(), p.y(), "identidade no ponto");
        Matriz3 wDois = new Matriz3(new double[][] {{2, 0, 4}, {0, 2, 6}, {0, 0, 2}});
        ponto(wDois.aplicar(new Ponto(1, 2)), 3, 5, "normalização por W = 2");
        Matriz3 wNegativo = new Matriz3(new double[][] {{-2, 0, 0}, {0, -2, 0}, {0, 0, -2}});
        ponto(wNegativo.aplicar(p), p.x(), p.y(), "normalização por W negativo");
        Matriz3 projetiva = new Matriz3(new double[][] {{1, 0, 0}, {0, 1, 0}, {0, 1, 1}});
        ponto(projetiva.aplicar(new Ponto(3, 2)), 1, 2.0 / 3, "W depende do ponto");
    }

    /** Exemplos do contrato e propriedades geométricas das quatro reflexões. */
    private static void testarReflexoes() {
        Matriz3[] reflexoes = {ReflexaoCisalhamento.reflexaoX(),
            ReflexaoCisalhamento.reflexaoY(), ReflexaoCisalhamento.reflexaoOrigem(),
            ReflexaoCisalhamento.reflexaoYeX()};
        double[][] esperados = {{2, -3}, {-2, 3}, {-2, -3}, {3, 2}};
        for (int i = 0; i < reflexoes.length; i++) {
            ponto(reflexoes[i].aplicar(new Ponto(2, 3)), esperados[i][0], esperados[i][1],
                    "reflexão " + i);
            matriz(reflexoes[i].multiplicar(reflexoes[i]), Matriz3.identidade(),
                    "duas reflexões iguais desfazem o efeito");
            ponto(reflexoes[i].aplicar(new Ponto(0, 0)), 0, 0, "origem preservada");
        }
        ponto(ReflexaoCisalhamento.reflexaoX().aplicar(new Ponto(-4, 0)), -4, 0,
                "eixo x fixo");
        ponto(ReflexaoCisalhamento.reflexaoY().aplicar(new Ponto(0, -4)), 0, -4,
                "eixo y fixo");
        ponto(ReflexaoCisalhamento.reflexaoYeX().aplicar(new Ponto(-4, -4)), -4, -4,
                "reta y=x fixa");
        matriz(Composicao.compor(reflexoes[0], reflexoes[1]), reflexoes[2],
                "dedução da reflexão na origem");
        matriz(Composicao.compor(Composicao.rotacaoPontoFixo(-45, 0, 0), reflexoes[0],
                Composicao.rotacaoPontoFixo(45, 0, 0)), reflexoes[3],
                "dedução da reflexão em y=x");
    }

    /** Fatores positivos, negativos, nulos e a diferença entre X→Y e Y→X. */
    private static void testarCisalhamentos() {
        ponto(ReflexaoCisalhamento.cisalhamentoX(1).aplicar(new Ponto(1, 2)), 3, 2,
                "cisalhamento x do contrato");
        ponto(ReflexaoCisalhamento.cisalhamentoY(2).aplicar(new Ponto(1, 2)), 1, 4,
                "cisalhamento y");
        ponto(ReflexaoCisalhamento.cisalhamentoX(-0.5).aplicar(new Ponto(1, 2)), 0, 2,
                "cisalhamento negativo e fracionário");
        ponto(ReflexaoCisalhamento.cisalhamentoY(-0.5).aplicar(new Ponto(2, 1)), 2, 0,
                "cisalhamento negativo em y");
        matriz(ReflexaoCisalhamento.cisalhamentoX(0), Matriz3.identidade(), "shx zero");
        matriz(ReflexaoCisalhamento.cisalhamentoY(0), Matriz3.identidade(), "shy zero");
        Matriz3 x = ReflexaoCisalhamento.cisalhamentoX(1);
        Matriz3 y = ReflexaoCisalhamento.cisalhamentoY(1);
        ponto(Composicao.compor(x, y).aplicar(new Ponto(1, 2)), 3, 5, "X depois Y");
        ponto(Composicao.compor(y, x).aplicar(new Ponto(1, 2)), 4, 3, "Y depois X");
        matriz(Composicao.compor(x, ReflexaoCisalhamento.cisalhamentoX(-1)),
                Matriz3.identidade(), "cisalhamento inverso");
    }

    /** Exemplo da não comutatividade e casos especiais da aula G5, slide 28. */
    private static void testarOrdemEComutatividade() {
        Matriz3 t = translacaoDeTeste(2, 0);
        Matriz3 r = new Matriz3(new double[][] {{0, -1, 0}, {1, 0, 0}, {0, 0, 1}});
        Ponto p = new Ponto(1, 0);
        ponto(Composicao.compor(t, r).aplicar(p), 0, 3, "transladar depois girar");
        ponto(Composicao.compor(r, t).aplicar(p), 2, 1, "girar depois transladar");
        Matriz3 a = Composicao.rotacaoPontoFixo(30, 0, 0);
        Matriz3 b = Composicao.rotacaoPontoFixo(-75, 0, 0);
        matriz(Composicao.compor(a, b), Composicao.compor(b, a), "duas rotações na origem");
        Matriz3 s1 = Composicao.escalaPontoFixo(2, 3, 0, 0);
        Matriz3 s2 = Composicao.escalaPontoFixo(-1, 0.5, 0, 0);
        matriz(Composicao.compor(s1, s2), Composicao.compor(s2, s1), "duas escalas na origem");
        Matriz3 composta = Composicao.compor(t, a, s1);
        ponto(composta.aplicar(p), s1.aplicar(a.aplicar(t.aplicar(p))));
    }

    /** Compara as expansões com o produto T(p)·S/R·T(−p), usando pivôs distintos. */
    private static void testarPontosFixos() {
        for (Ponto pivo : List.of(new Ponto(0, 0), new Ponto(7, -3), new Ponto(-2.5, 5.25))) {
            Matriz3 ida = translacaoDeTeste(-pivo.x(), -pivo.y());
            Matriz3 volta = translacaoDeTeste(pivo.x(), pivo.y());
            for (double angulo : new double[] {0, 30, 90, -135, 360}) {
                Matriz3 r = Composicao.rotacaoPontoFixo(angulo, pivo.x(), pivo.y());
                ponto(r.aplicar(pivo), pivo.x(), pivo.y(), "pivô da rotação preservado");
                double rad = Math.toRadians(angulo);
                Matriz3 origem = new Matriz3(new double[][] {
                    {Math.cos(rad), -Math.sin(rad), 0},
                    {Math.sin(rad), Math.cos(rad), 0}, {0, 0, 1}
                });
                matriz(r, Composicao.compor(ida, origem, volta), "T(p) R T(-p)");
            }
            for (double sx : new double[] {0, -2, 0.5, 1}) {
                Matriz3 s = Composicao.escalaPontoFixo(sx, 3, pivo.x(), pivo.y());
                ponto(s.aplicar(pivo), pivo.x(), pivo.y(), "pivô da escala preservado");
                Matriz3 origem = new Matriz3(new double[][] {{sx, 0, 0}, {0, 3, 0}, {0, 0, 1}});
                matriz(s, Composicao.compor(ida, origem, volta), "T(p) S T(-p)");
            }
        }
        ponto(Composicao.escalaPontoFixo(2, 2, 2, 1).aplicar(new Ponto(4, 1)), 6, 1,
                "escala com ponto fixo do slide 25");
        ponto(Composicao.rotacaoPontoFixo(90, 2, 1).aplicar(new Ponto(4, 1)), 2, 3,
                "rotação anti-horária em ponto fixo");
    }

    /** Garante nova lista, preservação da entrada e nenhuma quantização intermediária. */
    private static void testarPoligonoEPrecisao() {
        List<Ponto> vertices = new ArrayList<>(List.of(new Ponto(0.25, 0.5),
                new Ponto(1.75, -0.25), new Ponto(-2.25, 1.125)));
        Poligono original = new Poligono(vertices);
        Matriz3 x = ReflexaoCisalhamento.cisalhamentoX(0.25);
        Poligono resultado = Composicao.aplicar(x, original);
        verificar(resultado != original && resultado.vertices != original.vertices,
                "novo polígono e nova lista");
        ponto(resultado.vertices.get(0), 0.375, 0.5, "preserva casas decimais");
        ponto(original.vertices.get(0), 0.25, 0.5, "original preservado");
        for (int i = 0; i < vertices.size(); i++) {
            ponto(resultado.vertices.get(i), x.aplicar(vertices.get(i)));
        }
        resultado.vertices.clear();
        verificar(vertices.size() == 3, "listas independentes");
        verificar(Composicao.aplicar(x, new Poligono(List.of())).vertices.isEmpty(),
                "polígono vazio");
    }

    /** Exercita todas as operações oficiais e a conexão com a futura Parte 2. */
    private static void testarMotorOficial() {
        MotorComposicao motor = new MotorComposicao();
        Poligono p = new Poligono(List.of(new Ponto(1, 2), new Ponto(2, 3), new Ponto(-1, 0)));
        List<Operacao> operacoes = List.of(new Operacao(Operacao.Tipo.CISALHAMENTO_X, 1, 0),
                new Operacao(Operacao.Tipo.REFLEXAO_Y_IGUAL_X, 0, 0));
        ponto(motor.aplicar(p, operacoes).vertices.get(0), 2, 3, "motor oficial: X depois y=x");
        String texto = motor.matrizComposta(operacoes);
        verificar(texto.lines().count() == 3 && texto.contains("1.000"), "matriz textual");
        ponto(motor.aplicar(p, List.of()).vertices.get(0), 1, 2, "lista vazia no motor");
        for (Operacao.Tipo tipo : Operacao.Tipo.values()) {
            boolean basica = tipo == Operacao.Tipo.TRANSLACAO || tipo == Operacao.Tipo.ESCALA
                    || tipo == Operacao.Tipo.ROTACAO;
            verificar(motor.suporta(tipo) != basica, "disponibilidade de " + tipo);
            if (basica) continue;
            // Cada tipo deve ter a mesma matriz quando usado na tela e no núcleo.
            Matriz3 esperado = switch (tipo) {
                case REFLEXAO_X -> ReflexaoCisalhamento.reflexaoX();
                case REFLEXAO_Y -> ReflexaoCisalhamento.reflexaoY();
                case REFLEXAO_ORIGEM -> ReflexaoCisalhamento.reflexaoOrigem();
                case REFLEXAO_Y_IGUAL_X -> ReflexaoCisalhamento.reflexaoYeX();
                case CISALHAMENTO_X -> ReflexaoCisalhamento.cisalhamentoX(0.5);
                case CISALHAMENTO_Y -> ReflexaoCisalhamento.cisalhamentoY(0.5);
                case CISALHAMENTO -> new Matriz3(new double[][] {{1, 0.5, 0}, {-1, 1, 0}, {0, 0, 1}});
                default -> throw new AssertionError(tipo);
            };
            ponto(motor.aplicar(p, List.of(new Operacao(tipo, 0.5, -1))).vertices.get(0),
                    esperado.aplicar(p.vertices.get(0)));
        }
        List<Operacao> recebidas = new ArrayList<>();
        Matriz3 fornecida = translacaoDeTeste(7, -2);
        MotorComposicao integrado = new MotorComposicao(op -> {
            recebidas.add(op);
            return fornecida;
        });
        for (Operacao.Tipo tipo : List.of(Operacao.Tipo.TRANSLACAO, Operacao.Tipo.ESCALA,
                Operacao.Tipo.ROTACAO)) {
            Operacao op = new Operacao(tipo, 2, 3);
            verificar(integrado.suporta(tipo), "Parte 2 conectada: " + tipo);
            ponto(integrado.aplicar(p, List.of(op)).vertices.get(0), 8, 0,
                    "motor usa matriz fornecida pela Parte 2");
            verificar(recebidas.get(recebidas.size() - 1).equals(op), "preserva parâmetros");
        }
        integrado.aplicar(p, operacoes);
        verificar(recebidas.size() == 3, "operações da Parte 3 não chamam a Parte 2");
        ponto(integrado.aplicar(p, List.of(new Operacao(Operacao.Tipo.TRANSLACAO, 0, 0),
                new Operacao(Operacao.Tipo.REFLEXAO_X, 0, 0))).vertices.get(0), 8, 0,
                "composição entre módulos");
    }

    /** Erros de entrada devem produzir exceções explícitas, sem NaN silencioso. */
    private static void testarEntradasInvalidas() {
        falha(() -> new Matriz3(new double[2][3]), "dimensão inválida");
        falha(() -> new Matriz3(new double[][] {{1, 0, 0}, {0}, {0, 0, 1}}), "linha inválida");
        falha(() -> new Matriz3().aplicar(new Ponto(1, 1)), "W zero");
        falha(() -> Matriz3.identidade().aplicar(new Ponto(Double.NaN, 0)), "ponto NaN");
        falha(() -> ReflexaoCisalhamento.cisalhamentoX(Double.NaN), "shx NaN");
        falha(() -> ReflexaoCisalhamento.cisalhamentoY(Double.POSITIVE_INFINITY), "shy infinito");
        falha(() -> Composicao.rotacaoPontoFixo(Double.NaN, 0, 0), "ângulo NaN");
        falha(() -> Composicao.escalaPontoFixo(1, 1, Double.POSITIVE_INFINITY, 0), "pivô infinito");
        falha(() -> new MotorComposicao().aplicar(new Poligono(List.of()),
                List.of(new Operacao(Operacao.Tipo.ROTACAO, 90, 0))), "Parte 2 ausente");
    }

    /** Dado de teste para validar a composição sem depender da classe da Parte 2. */
    private static Matriz3 translacaoDeTeste(double tx, double ty) {
        return new Matriz3(new double[][] {{1, 0, tx}, {0, 1, ty}, {0, 0, 1}});
    }

    /** Compara os nove elementos das matrizes com tolerância de ponto flutuante. */
    private static void matriz(Matriz3 obtido, Matriz3 esperado, String caso) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                perto(obtido.m[i][j], esperado.m[i][j], caso + " [" + i + "][" + j + "]");
            }
        }
    }

    /** Compara um ponto com outro, usando tolerância em cada coordenada. */
    private static void ponto(Ponto obtido, Ponto esperado) {
        ponto(obtido, esperado.x(), esperado.y(), "aplicação sequencial e composta");
    }

    /** Compara as coordenadas com os valores esperados e identifica o caso de falha. */
    private static void ponto(Ponto obtido, double x, double y, String caso) {
        perto(obtido.x(), x, caso + " (x)");
        perto(obtido.y(), y, caso + " (y)");
    }

    /** Aceita apenas valores finitos que diferem no máximo por EPS. */
    private static void perto(double obtido, double esperado, String caso) {
        verificar(Double.isFinite(obtido) && Math.abs(obtido - esperado) <= EPS,
                caso + ": esperado " + esperado + ", obtido " + obtido);
    }

    /** Verifica a rejeição de um argumento inválido com IllegalArgumentException. */
    private static void falha(Runnable acao, String caso) {
        try {
            acao.run();
        } catch (IllegalArgumentException esperado) {
            verificacoes++;
            return;
        }
        throw new AssertionError("Deveria rejeitar: " + caso);
    }

    /** Conta a verificação e lança uma falha independente da opção -ea da JVM. */
    private static void verificar(boolean condicao, String mensagem) {
        verificacoes++;
        if (!condicao) throw new AssertionError(mensagem);
    }
}
