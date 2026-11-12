package cg;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

// Importa os painéis do pacote lab02
import lab02.PainelPrimitivas;
import lab02.PainelReta;
// Importa o display e painéis auxiliares do lab02 para recriar a aba do Lab 01
import lab02.Display;
import javax.swing.JPanel;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JComboBox;
import lab02.TipoNDC;

public class JanelaPrincipal extends JFrame {

    private static final int LARGURA_DISPLAY = 800;
    private static final int ALTURA_DISPLAY = 600;

    // Componentes necessários para recriar a aba do Lab 01 (Copiados do Lab02)
    private Display displayLab01;
    private JLabel lblDispositivo;
    private JLabel lblNdc01;
    private JLabel lblNdc11;
    private JLabel lblMundo;
    private JTextField campoXMin, campoXMax, campoYMin, campoYMax;
    private JComboBox<TipoNDC> comboNdc;
    private JTextField campoMundoX, campoMundoY;

    public JanelaPrincipal() {
        setTitle("Laboratório de Computação Gráfica");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JTabbedPane abas = new JTabbedPane();

        // 1. Recria a aba do Lab 01
        abas.addTab("Lab 01 — Transformações", criarAbaLab01());

        // 2. Adiciona as abas do Lab 02 (importadas de lab02.*)
        abas.addTab("Lab 02 — Primitivas", new PainelPrimitivas(LARGURA_DISPLAY, ALTURA_DISPLAY));
        abas.addTab("Lab 02 — Reta", new PainelReta(LARGURA_DISPLAY, ALTURA_DISPLAY));

        // 3. Adiciona a aba do Lab 03
        MotorTransformacoes motor = new MotorComposicao(op -> switch (op.tipo()) {
            case TRANSLACAO -> TransformacoesBasicas.translacao(op.a(), op.b());
            case ESCALA -> TransformacoesBasicas.escala(op.a(), op.b());
            case ROTACAO -> TransformacoesBasicas.rotacao(op.a());
            default -> throw new IllegalArgumentException("Operação não pertence à Parte 2.");
        });
        abas.addTab("Lab 03 — Transformações 2D", new PainelTransformacoes(LARGURA_DISPLAY, ALTURA_DISPLAY, motor));

        add(abas, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    /**
     * Recria a aba do Lab 01 utilizando os componentes do pacote lab02,
     * exatamente como era feito na JanelaPrincipal original.
     */
    private JPanel criarAbaLab01() {
        displayLab01 = new Display(LARGURA_DISPLAY, ALTURA_DISPLAY);
        
        lblDispositivo = new JLabel("Dispositivo: -");
        lblNdc01 = new JLabel("NDC [0,1]: -");
        lblNdc11 = new JLabel("NDC [-1,1]: -");
        lblMundo = new JLabel("Mundo: -");
        
        campoXMin = new JTextField("-100");
        campoXMax = new JTextField("100");
        campoYMin = new JTextField("-100");
        campoYMax = new JTextField("100");
        
        comboNdc = new JComboBox<>(TipoNDC.values());
        
        campoMundoX = new JTextField("0");
        campoMundoY = new JTextField("0");

        JPanel aba = new JPanel(new BorderLayout());
        aba.add(displayLab01, BorderLayout.CENTER);
        aba.add(criarPainelInformacoes(), BorderLayout.SOUTH);
        aba.add(criarPainelLateral(), BorderLayout.EAST);
        return aba;
    }

    private JPanel criarPainelInformacoes() {
        JPanel painel = new JPanel();
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        painel.setLayout(new GridLayout(4, 1));
        Font fonte = new Font("Monospaced", Font.PLAIN, 14);
        
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

    private JPanel criarPainelLateral() {
        JPanel painel = new JPanel();
        painel.setLayout(new GridLayout(3, 1, 5, 5));
        painel.add(criarPainelJanelaMundo());
        painel.add(criarPainelNdc());
        painel.add(criarPainelConsultaMundo());
        return painel;
    }

    private JPanel criarPainelJanelaMundo() {
        JPanel painel = new JPanel();
        painel.setBorder(BorderFactory.createTitledBorder("Janela do Mundo"));
        painel.setLayout(new GridLayout(5, 2, 5, 2));
        
        painel.add(new JLabel("X mínimo:")); painel.add(campoXMin);
        painel.add(new JLabel("X máximo:")); painel.add(campoXMax);
        painel.add(new JLabel("Y mínimo:")); painel.add(campoYMin);
        painel.add(new JLabel("Y máximo:")); painel.add(campoYMax);
        
        JButton botaoAplicar = new JButton("Aplicar");
        // A lógica do botão aplicar precisaria ser reimplementada aqui se desejado
        painel.add(botaoAplicar);
        return painel;
    }

    private JPanel criarPainelNdc() {
        JPanel painel = new JPanel();
        painel.setBorder(BorderFactory.createTitledBorder("Sistema NDC"));
        painel.setLayout(new GridLayout(2, 1, 5, 5));
        painel.add(new JLabel("Intervalo utilizado:"));
        painel.add(comboNdc);
        return painel;
    }

    private JPanel criarPainelConsultaMundo() {
        JPanel painel = new JPanel();
        painel.setBorder(BorderFactory.createTitledBorder("Consultar Ponto do Mundo"));
        painel.setLayout(new GridLayout(3, 2, 5, 2));
        painel.add(new JLabel("X do mundo:")); painel.add(campoMundoX);
        painel.add(new JLabel("Y do mundo:")); painel.add(campoMundoY);
        
        JButton botaoConsultar = new JButton("Mostrar no display");
        // A lógica do botão consultar precisaria ser reimplementada aqui se desejado
        painel.add(botaoConsultar);
        return painel;
    }
}
