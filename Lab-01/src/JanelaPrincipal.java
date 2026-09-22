import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;

/**
 * Janela principal
 *
 * - apresenta o display;
 * - recebe a posição do mouse;
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
     * Labels utilizados para apresentar as coordenadas.
     */
    private final JLabel lblDispositivo;
    private final JLabel lblNdc01;
    private final JLabel lblNdc11;
    private final JLabel lblMundo;

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
                new GridLayout(4, 1)
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

        painel.add(lblDispositivo);
        painel.add(lblNdc01);
        painel.add(lblNdc11);
        painel.add(lblMundo);

        return painel;
    }

    /**
     * Configura o listener responsável por acompanhar
     * o movimento do mouse sobre o display.
     */
    private void configurarMouse() {

        display.addMouseMotionListener(
                new MouseInputAdapter() {

                    /**
                     * Executado sempre que o mouse se movimenta
                     * dentro do display.
                     *
                     * @param evento informações do movimento do mouse
                     */
                    @Override
                    public void mouseMoved(
                            MouseEvent evento) {

                        atualizarCoordenadas(
                                evento.getX(),
                                evento.getY()
                        );
                    }
                }
        );
    }

    /**
     * Calcula todas as representações do ponto
     * atualmente indicado pelo mouse.
     * @param x coordenada X do mouse no display
     * @param y coordenada Y do mouse no display
     */
    private void atualizarCoordenadas(
            int x,
            int y) {

        Ponto input =
                new Ponto(x, y);

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
                Transformacao.ndc01ToUser(
                        ndc01,
                        janelaMundo
                );

        Ponto ndc01Final =
                Transformacao.userToNdc01(
                        mundo,
                        janelaMundo
                );

        Ponto ndc11Final =
                Transformacao.userToNdc11(
                        mundo,
                        janelaMundo
                );

        Ponto dispositivoFinal =
                Transformacao.ndc01ToDc(
                        ndc01Final,
                        LARGURA_DISPLAY,
                        ALTURA_DISPLAY
                );

        int pixelX = (int) Math.round(dispositivoFinal.getX());
        int pixelY = (int) Math.round(dispositivoFinal.getY());

        display.drawPixel(pixelX, pixelY);

        lblDispositivo.setText(
                String.format(
                        "Dispositivo: (%d, %d)",
                        x,
                        y
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