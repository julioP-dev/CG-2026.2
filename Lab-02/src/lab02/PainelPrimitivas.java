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

/** Tela para entrada, execução e acompanhamento dos algoritmos de circunferência. */
public class PainelPrimitivas extends JPanel {
    private final DisplayPrimitivas display;
    private final JTextField campoX = new JTextField("400", 6);
    private final JTextField campoY = new JTextField("300", 6);
    private final JTextField campoRaio = new JTextField("100", 6);
    private final JLabel instrucaoClique = new JLabel(
            "Clique no plano para definir o centro; clique outra vez para o raio.");
    private final JTextArea historico = new JTextArea();
    private final JButton botaoExecutar = new JButton("Executar algoritmo");
    private final JComboBox<AlgoritmoCircunferencia> comboAlgoritmo = new JComboBox<>(
            new AlgoritmoCircunferencia[]{
                    new AlgoritmoCircunferenciaTrigonometrico(),
                    new AlgoritmoCircunferenciaPontoMedio()
            });
    private Ponto centroDefinidoPorClique;
    private Timer temporizador;

    public PainelPrimitivas(int largura, int altura) {
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
        controles.setBorder(BorderFactory.createTitledBorder("Circunferência — coordenadas do dispositivo"));
        JPanel campos = new JPanel(new java.awt.GridLayout(4, 2, 5, 5));
        campos.add(new JLabel("Algoritmo:"));
        campos.add(comboAlgoritmo);
        campos.add(new JLabel("Centro X (px):")); campos.add(campoX);
        campos.add(new JLabel("Centro Y (px):")); campos.add(campoY);
        campos.add(new JLabel("Raio (px):")); campos.add(campoRaio);
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
            centroDefinidoPorClique = null;
            instrucaoClique.setText("Clique no plano para definir o centro; clique outra vez para o raio.");
        });
        return lateral;
    }

    private void configurarCliques() {
        display.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent evento) {
                if (temporizador != null && temporizador.isRunning()) return;
                int x = evento.getX();
                int y = evento.getY();
                if (centroDefinidoPorClique == null) {
                    centroDefinidoPorClique = new Ponto(x, y);
                    campoX.setText(Integer.toString(x));
                    campoY.setText(Integer.toString(y));
                    instrucaoClique.setText("Centro (" + x + ", " + y + "); clique para definir o raio.");
                } else {
                    int raio = (int) Math.round(Math.hypot(
                            x - centroDefinidoPorClique.getX(),
                            y - centroDefinidoPorClique.getY()));
                    campoRaio.setText(Integer.toString(raio));
                    instrucaoClique.setText("Raio definido: " + raio + " px.");
                    centroDefinidoPorClique = null;
                }
            }
        });
    }

    private void executar() {
        try {
            int x = Integer.parseInt(campoX.getText().trim());
            int y = Integer.parseInt(campoY.getText().trim());
            int raio = Integer.parseInt(campoRaio.getText().trim());
            if (x < 0 || x >= display.getWidth() || y < 0 || y >= display.getHeight()) {
                throw new IllegalArgumentException("O centro deve estar dentro dos limites do plano.");
            }
            if (raio < 0) throw new IllegalArgumentException("O raio deve ser um inteiro não negativo.");
            int raioMaximoVisivel = (int) Math.ceil(Math.hypot(
                    display.getWidth() - 1, display.getHeight() - 1));
            if (raio > raioMaximoVisivel) {
                throw new IllegalArgumentException("O raio deve ser menor ou igual à diagonal do display ("
                        + raioMaximoVisivel + " px).");
            }

            AlgoritmoCircunferencia algoritmo =
                    (AlgoritmoCircunferencia) comboAlgoritmo.getSelectedItem();
            List<PassoAlgoritmo> passos = algoritmo.calcular(x, y, raio);
            display.limpar();
            historico.setText("Coordenadas de dispositivo: centro=(" + x + ", " + y
                    + "), raio=" + raio + " px.\n");
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
                    "Centro e raio devem ser inteiros em pixels.", "Entrada inválida",
                    JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException erro) {
            JOptionPane.showMessageDialog(this, erro.getMessage(), "Entrada inválida",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparDesenho() {
        if (temporizador != null) temporizador.stop();
        display.limpar();
        historico.setText("");
        botaoExecutar.setEnabled(true);
    }

}
