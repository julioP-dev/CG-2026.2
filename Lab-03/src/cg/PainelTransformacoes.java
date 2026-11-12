package cg;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/** Tela para montar o polígono, compor transformações e acompanhar cada etapa. */
public class PainelTransformacoes extends JPanel {
    /** O Rasterizador não recorta; resultados muito distantes travariam o desenho. */
    private static final double LIMITE_MUNDO = 10_000;
    private static final String TEXTO_INICIAL =
            "Monte o polígono, adicione transformações à composição e aplique para acompanhar cada etapa.\n";
    private static final String INSTRUCAO_CLIQUE = "Clique no plano para adicionar vértices.";

    private final DisplayTransformacoes display;
    private final MotorTransformacoes motor;
    private final List<Ponto> vertices = new ArrayList<>();
    private final List<Operacao> operacoes = new ArrayList<>();

    private final JComboBox<Forma> comboForma = new JComboBox<>(Forma.values());
    private final JLabel instrucaoClique = new JLabel(INSTRUCAO_CLIQUE);
    /** Guarda as operações do motor (Operacao.Tipo) e as duas opções com ponto fixo (PontoFixo). */
    private final JComboBox<Object> comboTipo = new JComboBox<>();
    private final JLabel rotuloA = new JLabel();
    private final JLabel rotuloB = new JLabel();
    private final JTextField campoA = new JTextField(6);
    private final JTextField campoB = new JTextField(6);
    /** Ponto fixo (xf; yf), usado só nas opções com ponto fixo. */
    private final JTextField campoXf = new JTextField(3);
    private final JTextField campoYf = new JTextField(3);
    private final DefaultListModel<String> modeloLista = new DefaultListModel<>();
    private final JList<String> listaOperacoes = new JList<>(modeloLista);
    private final JTextArea historico = new JTextArea();
    private final JButton botaoAplicar = new JButton("Aplicar composição");
    private Timer temporizador;
    /** True enquanto o polígono na tela é uma forma padrão ainda não editada pelo usuário. */
    private boolean formaCarregada;

    /**
     * Transformações com ponto fixo (aula G5, slides 22 a 27). Não existem como operação do motor:
     * cada uma é montada com três operações da lista, T(xf, yf) · M · T(-xf, -yf).
     */
    enum PontoFixo {
        ESCALA("Escala com ponto fixo"),
        ROTACAO("Rotação com ponto fixo");

        private final String nome;

        PontoFixo(String nome) { this.nome = nome; }

        @Override public String toString() { return nome; }
    }

    private enum Forma {

        QUADRADO("Quadrado", -75, -75, 75, -75, 75, 75, -75, 75),
        // Forma em L de área 15000 e centroide (0, 0), assimétrica nas reflexões.
        IRREGULAR("Irregular côncavo", -75, -50, 125, -50, 125, 0,
                -25, 0, -25, 100, -75, 100);

        private final String nome;
        private final double[] coordenadas;

        Forma(String nome, double... coordenadas) {
            this.nome = nome;
            this.coordenadas = coordenadas;
        }

        List<Ponto> vertices() {
            List<Ponto> lista = new ArrayList<>();
            for (int i = 0; i < coordenadas.length; i += 2) {
                lista.add(new Ponto(coordenadas[i], coordenadas[i + 1]));
            }
            return lista;
        }

        @Override public String toString() { return nome; }
    }

    public PainelTransformacoes(int largura, int altura, MotorTransformacoes motor) {
        super(new BorderLayout(8, 8));
        this.motor = motor;
        for (Operacao.Tipo tipo : Operacao.Tipo.values()) {
            // O tipo simultâneo é mantido na API por compatibilidade.
            // Na tela, os dois cisalhamentos são adicionados separadamente.
            if (tipo != Operacao.Tipo.CISALHAMENTO && motor.suporta(tipo)) {
                comboTipo.addItem(tipo);
            }
        }
        // As opções com ponto fixo usam translação, escala e rotação, então dependem das três.
        if (motor.suporta(Operacao.Tipo.TRANSLACAO) && motor.suporta(Operacao.Tipo.ESCALA)
                && motor.suporta(Operacao.Tipo.ROTACAO)) {
            for (PontoFixo opcao : PontoFixo.values()) {
                comboTipo.addItem(opcao);
            }
        }
        display = new DisplayTransformacoes(largura, altura);
        add(display, BorderLayout.CENTER);
        add(criarPainelLateral(), BorderLayout.EAST);
        configurarCliques();
        atualizarCampos();
        carregarForma();
        // Só depois de carregar a primeira forma, para trocar a forma ao escolher no combo.
        comboForma.addActionListener(e -> carregarForma());
    }

    private JPanel criarPainelLateral() {
        JPanel lateral = new JPanel(new BorderLayout(6, 10));
        lateral.setPreferredSize(new Dimension(360, 600));
        lateral.setBorder(BorderFactory.createEmptyBorder(8, 4, 8, 8));

        JPanel controles = new JPanel();
        controles.setLayout(new BoxLayout(controles, BoxLayout.Y_AXIS));
        controles.add(criarGrupoPoligono());
        controles.add(Box.createVerticalStrut(6));
        controles.add(criarGrupoTransformacao());
        controles.add(Box.createVerticalStrut(6));
        controles.add(criarGrupoComposicao());
        controles.add(Box.createVerticalStrut(6));
        controles.add(criarBotoesPrincipais());
        lateral.add(controles, BorderLayout.NORTH);

        JPanel painelHistorico = new JPanel(new BorderLayout());
        painelHistorico.setBorder(BorderFactory.createTitledBorder("Passo a passo"));
        historico.setEditable(false);
        historico.setLineWrap(true);
        historico.setWrapStyleWord(true);
        historico.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        historico.setText(TEXTO_INICIAL);
        painelHistorico.add(new JScrollPane(historico), BorderLayout.CENTER);
        lateral.add(painelHistorico, BorderLayout.CENTER);
        return lateral;
    }

    private JPanel criarGrupoPoligono() {
        JPanel grupo = new JPanel(new BorderLayout(5, 6));
        grupo.setBorder(BorderFactory.createTitledBorder("Polígono — coordenadas do mundo"));
        JPanel linhaForma = new JPanel(new BorderLayout(5, 0));
        linhaForma.add(new JLabel("Forma:"), BorderLayout.WEST);
        linhaForma.add(comboForma, BorderLayout.CENTER);
        grupo.add(linhaForma, BorderLayout.NORTH);
        grupo.add(instrucaoClique, BorderLayout.CENTER);

        JPanel botoes = new JPanel(new GridLayout(1, 2, 5, 0));
        JButton carregar = new JButton("Carregar forma");
        JButton novo = new JButton("Novo polígono");
        botoes.add(carregar);
        botoes.add(novo);
        grupo.add(botoes, BorderLayout.SOUTH);
        carregar.addActionListener(e -> carregarForma());
        novo.addActionListener(e -> novoPoligono());
        return grupo;
    }

    private JPanel criarGrupoTransformacao() {
        JPanel grupo = new JPanel(new BorderLayout(5, 6));
        grupo.setBorder(BorderFactory.createTitledBorder("Transformação"));
        JPanel campos = new JPanel(new GridLayout(4, 2, 5, 5));
        campos.add(new JLabel("Tipo:"));
        campos.add(comboTipo);
        campos.add(rotuloA);
        campos.add(campoA);
        campos.add(rotuloB);
        campos.add(campoB);
        campos.add(new JLabel("Ponto fixo (xf; yf):"));
        JPanel pontoFixo = new JPanel(new GridLayout(1, 2, 5, 0));
        pontoFixo.add(campoXf);
        pontoFixo.add(campoYf);
        campos.add(pontoFixo);
        grupo.add(campos, BorderLayout.NORTH);

        JButton adicionar = new JButton("Adicionar à composição");
        grupo.add(adicionar, BorderLayout.SOUTH);
        comboTipo.addActionListener(e -> atualizarCampos());
        adicionar.addActionListener(e -> adicionarOperacao());
        return grupo;
    }

    private JPanel criarGrupoComposicao() {
        JPanel grupo = new JPanel(new BorderLayout(5, 6));
        grupo.setBorder(BorderFactory.createTitledBorder("Composição — ordem de aplicação"));
        listaOperacoes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaOperacoes.setVisibleRowCount(4);
        grupo.add(new JScrollPane(listaOperacoes), BorderLayout.CENTER);

        JPanel botoes = new JPanel(new GridLayout(1, 2, 5, 0));
        JButton remover = new JButton("Remover");
        JButton limparLista = new JButton("Limpar lista");
        botoes.add(remover);
        botoes.add(limparLista);
        grupo.add(botoes, BorderLayout.SOUTH);
        remover.addActionListener(e -> removerOperacao());
        limparLista.addActionListener(e -> {
            operacoes.clear();
            atualizarLista();
        });
        return grupo;
    }

    private JPanel criarBotoesPrincipais() {
        JPanel botoes = new JPanel(new GridLayout(1, 2, 5, 0));
        botoes.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        JButton botaoLimpar = new JButton("Limpar tudo");
        botoes.add(botaoAplicar);
        botoes.add(botaoLimpar);
        botaoAplicar.addActionListener(e -> aplicar());
        botaoLimpar.addActionListener(e -> limparTudo());
        return botoes;
    }

    private void configurarCliques() {
        display.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent evento) {
                if (!SwingUtilities.isLeftMouseButton(evento)) return;
                // O 1º clique descarta a forma padrão: o polígono passa a ser só o dos cliques.
                if (formaCarregada) {
                    vertices.clear();
                    formaCarregada = false;
                }
                Ponto ponto = display.dispositivoParaMundo(evento.getX(), evento.getY());
                vertices.add(ponto);
                redesenhar();
                instrucaoClique.setText("Vértice " + vertices.size() + ": ("
                        + Operacao.numero(ponto.x()) + "; " + Operacao.numero(ponto.y()) + ").");
            }
        });
    }

    private void carregarForma() {
        Forma forma = (Forma) comboForma.getSelectedItem();
        vertices.clear();
        vertices.addAll(forma.vertices());
        formaCarregada = true;
        redesenhar();
        instrucaoClique.setText("Forma carregada: clique para redesenhar.");
    }

    private void novoPoligono() {
        formaCarregada = false;
        vertices.clear();
        redesenhar();
        instrucaoClique.setText(INSTRUCAO_CLIQUE);
    }

    /** Ajusta rótulos e valores iniciais dos campos conforme o tipo de transformação. */
    private void atualizarCampos() {
        Object escolhido = comboTipo.getSelectedItem();
        boolean comPontoFixo = escolhido instanceof PontoFixo;
        campoXf.setEnabled(comPontoFixo);
        campoYf.setEnabled(comPontoFixo);
        campoXf.setText(comPontoFixo ? "0" : "");
        campoYf.setText(comPontoFixo ? "0" : "");
        if (comPontoFixo) {
            if (escolhido == PontoFixo.ESCALA) {
                configurarCampos("Sx:", "1", "Sy:", "1");
            } else {
                configurarCampos("Ângulo (°):", "0", null, null);
            }
            return;
        }
        switch ((Operacao.Tipo) escolhido) {
            case TRANSLACAO -> configurarCampos("Δx:", "0", "Δy:", "0");
            case ESCALA -> configurarCampos("Sx:", "1", "Sy:", "1");
            case ROTACAO -> configurarCampos("Ângulo (°):", "0", null, null);
            case CISALHAMENTO_X -> configurarCampos("Shx:", "0", null, null);
            case CISALHAMENTO_Y -> configurarCampos("Shy:", "0", null, null);
            case CISALHAMENTO -> configurarCampos("Shx:", "0", "Shy:", "0");
            default -> configurarCampos(null, null, null, null);
        }
    }

    private void configurarCampos(String textoA, String padraoA, String textoB, String padraoB) {
        configurarCampo(rotuloA, campoA, textoA, padraoA);
        configurarCampo(rotuloB, campoB, textoB, padraoB);
    }

    private static void configurarCampo(JLabel rotulo, JTextField campo, String texto, String padrao) {
        boolean usado = texto != null;
        rotulo.setText(usado ? texto : " ");
        campo.setEnabled(usado);
        campo.setText(usado ? padrao : "");
    }

    private void adicionarOperacao() {
        try {
            Object escolhido = comboTipo.getSelectedItem();
            double a = campoA.isEnabled() ? ler(campoA) : 0;
            double b = campoB.isEnabled() ? ler(campoB) : 0;
            if (escolhido instanceof PontoFixo comPontoFixo) {
                operacoes.addAll(expandirPontoFixo(comPontoFixo, a, b, ler(campoXf), ler(campoYf)));
            } else {
                Operacao.Tipo tipo = (Operacao.Tipo) escolhido;
                operacoes.add(new Operacao(tipo, a, b));
            }
            atualizarLista();
        } catch (NumberFormatException erro) {
            JOptionPane.showMessageDialog(this,
                    "Os parâmetros devem ser números (aceita ponto ou vírgula decimal).",
                    "Entrada inválida", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Monta a transformação com ponto fixo (xf, yf) em três passos (slides 24 e 26):
     * 1) translada o ponto fixo para a origem; 2) escala (a = Sx, b = Sy) ou gira (a = ângulo);
     * 3) translada de volta. O ponto (xf, yf) fica no mesmo lugar.
     */
    static List<Operacao> expandirPontoFixo(PontoFixo tipo, double a, double b, double xf, double yf) {
        Operacao transformacao = (tipo == PontoFixo.ESCALA)
                ? new Operacao(Operacao.Tipo.ESCALA, a, b)
                : new Operacao(Operacao.Tipo.ROTACAO, a, 0);
        List<Operacao> passos = new ArrayList<>();
        passos.add(new Operacao(Operacao.Tipo.TRANSLACAO, -xf, -yf));
        passos.add(transformacao);
        passos.add(new Operacao(Operacao.Tipo.TRANSLACAO, xf, yf));
        return passos;
    }

    private static double ler(JTextField campo) {
        double valor = Double.parseDouble(campo.getText().trim().replace(',', '.'));
        if (!Double.isFinite(valor)) throw new NumberFormatException("valor não finito");
        return valor;
    }

    private void removerOperacao() {
        int indice = listaOperacoes.getSelectedIndex();
        if (indice < 0) indice = operacoes.size() - 1;
        if (indice >= 0) {
            operacoes.remove(indice);
            atualizarLista();
        }
    }

    private void atualizarLista() {
        modeloLista.clear();
        for (int i = 0; i < operacoes.size(); i++) {
            modeloLista.addElement((i + 1) + ". " + operacoes.get(i));
        }
    }

    private void limparTudo() {
        formaCarregada = false;
        vertices.clear();
        operacoes.clear();
        atualizarLista();
        redesenhar();
        historico.setText(TEXTO_INICIAL);
        instrucaoClique.setText(INSTRUCAO_CLIQUE);
    }

    /** Cancela a animação e mostra só a grade, os eixos e o polígono original. */
    private void redesenhar() {
        if (temporizador != null) temporizador.stop();
        botaoAplicar.setEnabled(true);
        display.limpar();
        display.desenhar(poligonoAtual(), DisplayTransformacoes.COR_ORIGINAL);
    }

    private Poligono poligonoAtual() {
        return new Poligono(new ArrayList<>(vertices));
    }

    /**
     * Calcula, com o motor, o resultado de cada prefixo da composição (1 operação, 2 operações, ...)
     * e revela uma etapa por vez no display e no passo a passo.
     */
    private void aplicar() {
        try {
            if (vertices.size() < 3) {
                throw new IllegalArgumentException("Defina um polígono com ao menos 3 vértices.");
            }
            if (operacoes.isEmpty()) {
                throw new IllegalArgumentException("Adicione ao menos uma transformação à composição.");
            }
            Poligono original = poligonoAtual();
            List<Operacao> composicao = List.copyOf(operacoes);
            List<Poligono> etapas = new ArrayList<>();
            for (int i = 1; i <= composicao.size(); i++) {
                Poligono etapa = motor.aplicar(original, composicao.subList(0, i));
                validarLimites(etapa, i);
                etapas.add(etapa);
            }

            redesenhar();
            historico.setText("Coordenadas do mundo (origem no centro, y para cima).\n"
                    + "Original: " + descrever(original) + "\n");
            String matriz = motor.matrizComposta(composicao);
            if (matriz != null) {
                historico.append("Matriz composta (M = Mn·…·M1):\n" + matriz);
            }
            botaoAplicar.setEnabled(false);
            final int[] indice = {0};
            temporizador = new Timer(700, evento -> {
                if (indice[0] >= etapas.size()) {
                    temporizador.stop();
                    historico.append("Concluído: " + etapas.size() + " transformação(ões).\n");
                    botaoAplicar.setEnabled(true);
                    return;
                }
                int i = indice[0]++;
                boolean ultima = i == etapas.size() - 1;
                display.desenhar(etapas.get(i),
                        ultima ? DisplayTransformacoes.COR_RESULTADO : DisplayTransformacoes.COR_ETAPA);
                historico.append((i + 1) + ". " + composicao.get(i) + " → " + descrever(etapas.get(i)) + "\n");
                historico.setCaretPosition(historico.getDocument().getLength());
            });
            temporizador.setInitialDelay(0);
            temporizador.start();
        } catch (IllegalArgumentException erro) {
            JOptionPane.showMessageDialog(this, erro.getMessage(), "Entrada inválida",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void validarLimites(Poligono poligono, int etapa) {
        for (Ponto v : poligono.vertices) {
            // A forma negada também barra NaN.
            if (!(Math.abs(v.x()) <= LIMITE_MUNDO && Math.abs(v.y()) <= LIMITE_MUNDO)) {
                throw new IllegalArgumentException("A etapa " + etapa + " passa de ±"
                        + (int) LIMITE_MUNDO + " unidades; reduza os parâmetros.");
            }
        }
    }

    private static String descrever(Poligono poligono) {
        StringBuilder texto = new StringBuilder();
        for (Ponto v : poligono.vertices) {
            if (texto.length() > 0) texto.append(' ');
            texto.append('(').append(Operacao.numero(v.x())).append("; ")
                    .append(Operacao.numero(v.y())).append(')');
        }
        return texto.toString();
    }
}
