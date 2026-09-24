import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.MouseInputAdapter;

/**
 * Janela principal
 *
 * - apresenta o display;
 * - rastreia o mouse (movimento, clique e arraste) e liga o pixel ativo;
 * - permite escolher o sistema de coordenadas NDC utilizado;
 * - permite consultar um ponto do mundo diretamente no display;
 * - executa as transformações;
 * - apresenta as coordenadas calculadas.
 */
public class JanelaPrincipal extends JFrame {

    /**
     * Resolução do display utilizada pelo laboratório.
     */
    private static final int LARGURA_DISPLAY = 800;
    private static final int ALTURA_DISPLAY = 600;

    /**
     * Display gráfico onde o pixel será ativado.
     */
    private final Display display;

    /**
     * Janela atual do mundo.
     */
    private JanelaMundo janelaMundo;

    /**
     * Sistema de coordenadas normalizadas (NDC) atualmente
     * selecionado pelo usuário. Parametriza qual par de
     * transformações (ZERO_A_UM ou MENOS_UM_A_UM) é utilizado
     * no pipeline mundo -> ndc -> dispositivo.
     */
    private TipoNDC ndcAtual = TipoNDC.ZERO_A_UM;

    /**
     * Labels utilizados para apresentar as coordenadas.
     */
    private final JLabel lblDispositivo;
    private final JLabel lblNdc01;
    private final JLabel lblNdc11;
    private final JLabel lblMundo;

    /**
     * Label de alerta exibido quando a coordenada está
     * fora dos limites do display.
     */
    private final JLabel lblAlerta;
    private final JTextField campoXMin;
    private final JTextField campoXMax;
    private final JTextField campoYMin;
    private final JTextField campoYMax;

    /**
     * Seletor do sistema NDC utilizado.
     */
    private final JComboBox<TipoNDC> comboNdc;

    /**
     * Campos para consulta de um ponto do mundo.
     */
    private final JTextField campoMundoX;
    private final JTextField campoMundoY;

    /**
     * Construtor da janela principal.
     */
    public JanelaPrincipal() {

        janelaMundo = new JanelaMundo(
                -100,
                100,
                -100,
                100
        );

        /*
         * Criação do display.
         */
        display = new Display(
                LARGURA_DISPLAY,
                ALTURA_DISPLAY
        );

        /*
         * Labels das informações.
         */
        lblDispositivo =
                new JLabel("Dispositivo: -");

        lblNdc01 =
                new JLabel("NDC [0,1]: -");

        lblNdc11 =
                new JLabel("NDC [-1,1]: -");

        lblMundo =
                new JLabel("Mundo: -");

        lblAlerta =
                new JLabel(" ");

        campoXMin = new JTextField("-100");
        campoXMax = new JTextField("100");
        campoYMin = new JTextField("-100");
        campoYMax = new JTextField("100");

        comboNdc = new JComboBox<>(TipoNDC.values());

        campoMundoX = new JTextField("0");
        campoMundoY = new JTextField("0");

        configurarJanela();
        configurarMouse();
    }

    /**
     * Configura as propriedades básicas da janela.
     */
    private void configurarJanela() {

        setTitle(
                "Laboratório de Computação Gráfica"
        );

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        setLayout(
                new BorderLayout()
        );

        /*
         * O display ocupa a região principal da janela.
         */
        add(
                display,
                BorderLayout.CENTER
        );

        /*
         * As coordenadas ficam fora do display.
         */
        add(
                criarPainelInformacoes(),
                BorderLayout.SOUTH
        );

        add(
                criarPainelLateral(),
                BorderLayout.EAST
        );

        /*
         * Ajusta o tamanho da janela de acordo
         * com seus componentes.
         */
        pack();

        /*
         * Centraliza a janela na tela.
         */
        setLocationRelativeTo(null);

        /*
         * Evita que o usuário altere o tamanho do display.
         */
        setResizable(false);
    }

    /**
     * Cria o painel responsável pela apresentação
     * das coordenadas.
     *
     * @return painel de informações
     */
    private JPanel criarPainelInformacoes() {

        JPanel painel = new JPanel();

        painel.setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        10,
                        10,
                        10
                )
        );

        painel.setLayout(
                new GridLayout(5, 1)
        );

        Font fonte =
                new Font(
                        "Monospaced",
                        Font.PLAIN,
                        14
                );

        lblDispositivo.setFont(fonte);
        lblNdc01.setFont(fonte);
        lblNdc11.setFont(fonte);
        lblMundo.setFont(fonte);

        lblAlerta.setFont(fonte.deriveFont(Font.BOLD));
        lblAlerta.setForeground(Color.RED);

        painel.add(lblDispositivo);
        painel.add(lblNdc01);
        painel.add(lblNdc11);
        painel.add(lblMundo);
        painel.add(lblAlerta);

        return painel;
    }

    /**
     * Agrupa, em uma única coluna à direita, o painel da
     * janela do mundo, o seletor de sistema NDC e o painel
     * de consulta de ponto do mundo.
     *
     * @return painel lateral completo
     */
    private JPanel criarPainelLateral() {

        JPanel painel = new JPanel();

        painel.setLayout(
                new GridLayout(3, 1, 5, 5)
        );

        painel.add(criarPainelJanelaMundo());
        painel.add(criarPainelNdc());
        painel.add(criarPainelConsultaMundo());

        return painel;
    }

    private JPanel criarPainelJanelaMundo() {

        JPanel painel = new JPanel();

        painel.setBorder(
                BorderFactory.createTitledBorder(
                        "Janela do Mundo"
                )
        );

        painel.setLayout(
                new GridLayout(5, 2, 5, 2)
        );

        painel.add(new JLabel("X mínimo:"));
        painel.add(campoXMin);

        painel.add(new JLabel("X máximo:"));
        painel.add(campoXMax);

        painel.add(new JLabel("Y mínimo:"));
        painel.add(campoYMin);

        painel.add(new JLabel("Y máximo:"));
        painel.add(campoYMax);

        JButton botaoAplicar =
                new JButton("Aplicar");

        botaoAplicar.addActionListener(
                e -> atualizarJanelaMundo()
        );

        painel.add(botaoAplicar);

        return painel;
    }

    /**
     * Cria o painel responsável por selecionar o sistema
     * de coordenadas normalizadas do dispositivo (NDC)
     * utilizado no pipeline de transformação.
     *
     * @return painel do seletor de NDC
     */
    private JPanel criarPainelNdc() {

        JPanel painel = new JPanel();

        painel.setBorder(
                BorderFactory.createTitledBorder(
                        "Sistema NDC"
                )
        );

        painel.setLayout(
                new GridLayout(2, 1, 5, 5)
        );

        painel.add(new JLabel("Intervalo utilizado:"));
        painel.add(comboNdc);

        /*
         * Sempre que o usuário troca o sistema NDC, apenas
         * o intervalo utilizado pelo pipeline é atualizado.
         * Não disparamos nenhuma nova consulta aqui: os
         * campos de "Consultar Ponto do Mundo" podem estar
         * com um valor antigo (ou o padrão "0,0"), e usá-los
         * automaticamente faria o pixel pular para um ponto
         * que o usuário não pediu. A próxima interação
         * (mouse ou botão "Mostrar no display") já vai usar
         * o novo sistema.
         */
        comboNdc.addActionListener(
                e -> ndcAtual = (TipoNDC) comboNdc.getSelectedItem()
        );

        return painel;
    }

    /**
     * Cria o painel responsável por consultar, no display,
     * o pixel correspondente a um ponto do mundo digitado
     * pelo usuário.
     *
     * @return painel de consulta de ponto do mundo
     */
    private JPanel criarPainelConsultaMundo() {

        JPanel painel = new JPanel();

        painel.setBorder(
                BorderFactory.createTitledBorder(
                        "Consultar Ponto do Mundo"
                )
        );

        painel.setLayout(
                new GridLayout(3, 2, 5, 2)
        );

        painel.add(new JLabel("X do mundo:"));
        painel.add(campoMundoX);

        painel.add(new JLabel("Y do mundo:"));
        painel.add(campoMundoY);

        JButton botaoConsultar =
                new JButton("Mostrar no display");

        botaoConsultar.addActionListener(
                e -> consultarPontoMundo()
        );

        painel.add(botaoConsultar);

        return painel;
    }

    private void atualizarJanelaMundo() {

        try {

            double xMin =
                    Double.parseDouble(
                            campoXMin.getText()
                    );

            double xMax =
                    Double.parseDouble(
                            campoXMax.getText()
                    );

            double yMin =
                    Double.parseDouble(
                            campoYMin.getText()
                    );

            double yMax =
                    Double.parseDouble(
                            campoYMax.getText()
                    );

            janelaMundo =
                    new JanelaMundo(
                            xMin,
                            xMax,
                            yMin,
                            yMax
                    );

        } catch (NumberFormatException erro) {

            JOptionPane.showMessageDialog(
                    this,
                    "Digite apenas valores numéricos.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (IllegalArgumentException erro) {

            JOptionPane.showMessageDialog(
                    this,
                    erro.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Lê o ponto do mundo digitado pelo usuário, converte
     * para coordenadas de dispositivo (usando o sistema NDC
     * atualmente selecionado) e ativa o pixel correspondente
     * no display.
     */
    private void consultarPontoMundo() {

        try {

            double x =
                    Double.parseDouble(
                            campoMundoX.getText()
                    );

            double y =
                    Double.parseDouble(
                            campoMundoY.getText()
                    );

            Ponto mundo = new Ponto(x, y);

            Ponto dispositivo = mundoParaDispositivo(mundo);

            int pixelX = (int) Math.round(dispositivo.getX());
            int pixelY = (int) Math.round(dispositivo.getY());

            /*
             * Ponto fora da janela do mundo cai fora do
             * display: não exibe conversões inválidas.
             */
            if (!dentroDoDisplay(pixelX, pixelY)) {

                String mensagem = String.format(
                        "O ponto (%s, %s) está fora da janela do mundo "
                                + "[%s, %s] x [%s, %s].",
                        campoMundoX.getText(),
                        campoMundoY.getText(),
                        campoXMin.getText(),
                        campoXMax.getText(),
                        campoYMin.getText(),
                        campoYMax.getText()
                );

                mostrarForaDosLimites(mensagem);

                JOptionPane.showMessageDialog(
                        this,
                        mensagem,
                        "Fora dos limites",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            display.drawPixel(pixelX, pixelY);

            atualizarLabels(pixelX, pixelY, mundo);

        } catch (NumberFormatException erro) {

            JOptionPane.showMessageDialog(
                    this,
                    "Digite apenas valores numéricos para o ponto do mundo.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Configura o listener responsável por rastrear o
     * mouse sobre o display o tempo todo (movimento,
     * clique e arraste), convertendo as coordenadas e
     * ligando o pixel correspondente automaticamente.
     */
    private void configurarMouse() {

        MouseInputAdapter rastreador =
                new MouseInputAdapter() {

                    /**
                     * Executado quando o usuário pressiona
                     * o botão dentro do display.
                     *
                     * @param evento informações do mouse
                     */
                    @Override
                    public void mousePressed(
                            MouseEvent evento) {

                        rastrearMouse(evento);
                    }

                    /**
                     * Executado sempre que o mouse se move
                     * sobre o display sem botão pressionado.
                     *
                     * @param evento informações do mouse
                     */
                    @Override
                    public void mouseMoved(
                            MouseEvent evento) {

                        rastrearMouse(evento);
                    }

                    /**
                     * Executado sempre que o mouse é arrastado
                     * (movido com botão pressionado) no display.
                     *
                     * @param evento informações do mouse
                     */
                    @Override
                    public void mouseDragged(
                            MouseEvent evento) {

                        rastrearMouse(evento);
                    }
                };

        /*
         * mousePressed é entregue pelo MouseListener;
         * mouseMoved e mouseDragged só são entregues pelo
         * MouseMotionListener. Por isso o adaptador precisa
         * ser registrado nos dois.
         */
        display.addMouseListener(rastreador);
        display.addMouseMotionListener(rastreador);
    }

    /**
     * Converte a posição do mouse. Durante o arraste o
     * Swing continua enviando eventos mesmo fora do
     * display; nesse caso exibe um alerta em vez de
     * conversões inválidas.
     *
     * @param evento informações do mouse
     */
    private void rastrearMouse(MouseEvent evento) {

        int x = evento.getX();
        int y = evento.getY();

        if (!dentroDoDisplay(x, y)) {

            mostrarForaDosLimites(
                    String.format(
                            "Coordenada (%d, %d) fora dos limites do display %dx%d.",
                            x,
                            y,
                            LARGURA_DISPLAY,
                            ALTURA_DISPLAY
                    )
            );
            return;
        }

        ativarPixelPorClique(x, y);
    }

    /**
     * @param x coordenada X de dispositivo
     * @param y coordenada Y de dispositivo
     * @return true se a coordenada está dentro do display
     */
    private boolean dentroDoDisplay(int x, int y) {

        return x >= 0 && x < LARGURA_DISPLAY
                && y >= 0 && y < ALTURA_DISPLAY;
    }

    /**
     * Apaga o pixel ativo e substitui as conversões por
     * uma mensagem de alerta.
     *
     * @param mensagem texto do alerta
     */
    private void mostrarForaDosLimites(String mensagem) {

        display.limpar();
        display.repaint();

        lblDispositivo.setText("Dispositivo: fora dos limites");
        lblNdc01.setText("NDC [0,1]: fora dos limites");
        lblNdc11.setText("NDC [-1,1]: fora dos limites");
        lblMundo.setText("Mundo: fora dos limites");

        lblAlerta.setText("Alerta: " + mensagem);
    }

    /**
     * Trata o clique do mouse no display: converte a
     * coordenada de dispositivo clicada para o mundo e,
     * em seguida, de volta para dispositivo usando o
     * sistema NDC selecionado, mantendo o pixel ativo
     * coerente com o intervalo NDC escolhido pelo usuário.
     *
     * @param x coordenada X do clique no display
     * @param y coordenada Y do clique no display
     */
    private void ativarPixelPorClique(int x, int y) {

        Ponto input = new Ponto(x, y);

        Ponto ndc01 =
                Transformacao.inpToNdc01(
                        input,
                        LARGURA_DISPLAY,
                        ALTURA_DISPLAY
                );

        Ponto ndc11 =
                Transformacao.inpToNdc11(
                        input,
                        LARGURA_DISPLAY,
                        ALTURA_DISPLAY
                );

        Ponto mundo =
                (ndcAtual == TipoNDC.ZERO_A_UM)
                        ? Transformacao.ndc01ToUser(ndc01, janelaMundo)
                        : Transformacao.ndc11ToUser(ndc11, janelaMundo);

        Ponto dispositivoFinal = mundoParaDispositivo(mundo);

        int pixelX = (int) Math.round(dispositivoFinal.getX());
        int pixelY = (int) Math.round(dispositivoFinal.getY());

        display.drawPixel(pixelX, pixelY);

        atualizarLabels(x, y, mundo, ndc01, ndc11);
    }

    /**
     * Converte um ponto do mundo para coordenadas de
     * dispositivo, respeitando o sistema NDC atualmente
     * selecionado pelo usuário.
     *
     * @param mundo ponto em coordenadas do mundo
     * @return ponto em coordenadas de dispositivo
     */
    private Ponto mundoParaDispositivo(Ponto mundo) {

        if (ndcAtual == TipoNDC.ZERO_A_UM) {

            Ponto ndc =
                    Transformacao.userToNdc01(mundo, janelaMundo);

            return Transformacao.ndc01ToDc(
                    ndc,
                    LARGURA_DISPLAY,
                    ALTURA_DISPLAY
            );
        }

        Ponto ndc =
                Transformacao.userToNdc11(mundo, janelaMundo);

        return Transformacao.ndc11ToDc(
                ndc,
                LARGURA_DISPLAY,
                ALTURA_DISPLAY
        );
    }

    /**
     * Atualiza os labels a partir de um ponto do mundo
     * digitado (recalcula os dois sistemas NDC para fins
     * de exibição).
     */
    private void atualizarLabels(int pixelX, int pixelY, Ponto mundo) {

        Ponto ndc01 =
                Transformacao.userToNdc01(mundo, janelaMundo);

        Ponto ndc11 =
                Transformacao.userToNdc11(mundo, janelaMundo);

        atualizarLabels(pixelX, pixelY, mundo, ndc01, ndc11);
    }

    /**
     * Atualiza todos os labels de coordenadas exibidos
     * na parte inferior da janela.
     */
    private void atualizarLabels(
            int pixelX,
            int pixelY,
            Ponto mundo,
            Ponto ndc01,
            Ponto ndc11) {

        lblAlerta.setText(" ");

        lblDispositivo.setText(
                String.format(
                        "Dispositivo: (%d, %d)",
                        pixelX,
                        pixelY
                )
        );

        lblNdc01.setText(
                String.format(
                        "NDC [0,1]: (%.3f, %.3f)",
                        ndc01.getX(),
                        ndc01.getY()
                )
        );

        lblNdc11.setText(
                String.format(
                        "NDC [-1,1]: (%.3f, %.3f)",
                        ndc11.getX(),
                        ndc11.getY()
                )
        );

        lblMundo.setText(
                String.format(
                        "Mundo: (%.2f, %.2f)",
                        mundo.getX(),
                        mundo.getY()
                )
        );
    }
}
