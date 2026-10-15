package lab02;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;

/** Tela para entrada, execução e acompanhamento dos algoritmos de reta. */
public class PainelReta extends JPanel {
    private static final String INSTRUCAO_INICIAL =
            "Clique no plano para definir P1; clique outra vez para P2.";

    private final DisplayPrimitivas display;
    private final JTextField campoX1 = new JTextField("100", 6);
    private final JTextField campoY1 = new JTextField("100", 6);
    private final JTextField campoX2 = new JTextField("600", 6);
    private final JTextField campoY2 = new JTextField("400", 6);
    private final JLabel instrucaoClique = new JLabel(INSTRUCAO_INICIAL);
    private final JTextArea historico = new JTextArea();
    private final JButton botaoExecutar = new JButton("Executar algoritmo");
    private final JComboBox<AlgoritmoReta> comboAlgoritmo = new JComboBox<>(
            new AlgoritmoReta[]{
                new AlgoritmoRetaDDA(),
                new AlgoritmoRetaPontoMedio()
            });
    private Ponto p1DefinidoPorClique;
    private Timer temporizador;

    public PainelReta(int largura, int altura) {
        super(new BorderLayout(8, 8));
        display = new DisplayPrimitivas(largura, altura);
        add(display, BorderLayout.CENTER);
        add(criarPainelLateral(), BorderLayout.EAST);
        configurarCliques();
    }

    private JPanel criarPainelLateral() {
        JPanel lateral = new JPanel(new BorderLayout(6, 10));
        lateral.setPreferredSize(new Dimension(350, 600));
        lateral.setBorder(BorderFactory.createEmptyBorder(8, 4, 8, 8));

        JPanel controles = new JPanel(new BorderLayout(5, 8));
        controles.setBorder(BorderFactory.createTitledBorder("Reta — coordenadas do dispositivo"));
        JPanel campos = new JPanel(new java.awt.GridLayout(5, 2, 5, 5));
        campos.add(new JLabel("Algoritmo:"));
        campos.add(comboAlgoritmo);
        campos.add(new JLabel("X1 (px):")); campos.add(campoX1);
        campos.add(new JLabel("Y1 (px):")); campos.add(campoY1);
        campos.add(new JLabel("X2 (px):")); campos.add(campoX2);
        campos.add(new JLabel("Y2 (px):")); campos.add(campoY2);
        controles.add(campos, BorderLayout.NORTH);

        JPanel acoes = new JPanel(new BorderLayout(4, 5));
        acoes.add(instrucaoClique, BorderLayout.NORTH);
        JPanel botoes = new JPanel(new java.awt.GridLayout(2, 1, 5, 5));
        JPanel botoesAuxiliares = new JPanel(new java.awt.GridLayout(1, 2, 5, 0));
        JButton reiniciarClique = new JButton("Novo clique");
        JButton limpar = new JButton("Limpar");
        botoesAuxiliares.add(reiniciarClique);
        botoesAuxiliares.add(limpar);
        botoes.add(botoesAuxiliares);
        botoes.add(botaoExecutar);
        acoes.add(botoes, BorderLayout.SOUTH);
        controles.add(acoes, BorderLayout.CENTER);
        lateral.add(controles, BorderLayout.NORTH);

        JPanel painelHistorico = new JPanel(new BorderLayout());
        painelHistorico.setBorder(BorderFactory.createTitledBorder("Passo a passo"));
        historico.setEditable(false);
        historico.setLineWrap(true);
        historico.setWrapStyleWord(true);
        historico.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        historico.setText("Escolha um algoritmo e execute para acompanhar cada iteração.\n");
        painelHistorico.add(new JScrollPane(historico), BorderLayout.CENTER);
        lateral.add(painelHistorico, BorderLayout.CENTER);

        botaoExecutar.addActionListener(e -> executar());
        limpar.addActionListener(e -> limparDesenho());
        reiniciarClique.addActionListener(e -> {
            p1DefinidoPorClique = null;
            instrucaoClique.setText(INSTRUCAO_INICIAL);
        });
        return lateral;
    }

    private void configurarCliques() {
        display.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent evento) {
                if (temporizador != null && temporizador.isRunning()) return;
                int x = evento.getX();
                int y = evento.getY();
                if (p1DefinidoPorClique == null) {
                    p1DefinidoPorClique = new Ponto(x, y);
                    campoX1.setText(Integer.toString(x));
                    campoY1.setText(Integer.toString(y));
                    instrucaoClique.setText("P1 (" + x + ", " + y + "); clique para definir P2.");
                } else {
                    campoX2.setText(Integer.toString(x));
                    campoY2.setText(Integer.toString(y));
                    instrucaoClique.setText("P2 definido: (" + x + ", " + y + ").");
                    p1DefinidoPorClique = null;
                }
            }
        });
    }

    private void executar() {
        try {
            int x1 = Integer.parseInt(campoX1.getText().trim());
            int y1 = Integer.parseInt(campoY1.getText().trim());
            int x2 = Integer.parseInt(campoX2.getText().trim());
            int y2 = Integer.parseInt(campoY2.getText().trim());
            validarDentroDoPlano(x1, y1, "P1");
            validarDentroDoPlano(x2, y2, "P2");

            AlgoritmoReta algoritmo = (AlgoritmoReta) comboAlgoritmo.getSelectedItem();
            List<PassoAlgoritmo> passos = algoritmo.calcular(x1, y1, x2, y2);
            if (temporizador != null) temporizador.stop();
            display.limpar();
            historico.setText("Coordenadas de dispositivo: P1=(" + x1 + ", " + y1
                    + "), P2=(" + x2 + ", " + y2 + ").\n");
            botaoExecutar.setEnabled(false);
            final int[] indice = {0};
            temporizador = new Timer(35, evento -> {
                if (indice[0] >= passos.size()) {
                    temporizador.stop();
                    historico.append("Concluído: " + passos.size() + " passos.\n");
                    botaoExecutar.setEnabled(true);
                    return;
                }
                PassoAlgoritmo passo = passos.get(indice[0]++);
                for (Ponto pixel : passo.getPixels()) {
                    display.drawPixel((int) pixel.getX(), (int) pixel.getY());
                }
                historico.append(passo.getDescricao() + "\n");
                historico.setCaretPosition(historico.getDocument().getLength());
            });
            temporizador.start();
        } catch (NumberFormatException erro) {
            JOptionPane.showMessageDialog(this,
                    "As coordenadas devem ser inteiros em pixels.", "Entrada inválida",
                    JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException erro) {
            JOptionPane.showMessageDialog(this, erro.getMessage(), "Entrada inválida",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void validarDentroDoPlano(int x, int y, String nome) {
        if (x < 0 || x >= display.getWidth() || y < 0 || y >= display.getHeight()) {
            throw new IllegalArgumentException(
                    nome + " deve estar dentro dos limites do plano.");
        }
    }

    private void limparDesenho() {
        if (temporizador != null) temporizador.stop();
        display.limpar();
        historico.setText("");
        botaoExecutar.setEnabled(true);
    }
}
