import javax.swing.SwingUtilities;

public class Main {

    /**
     *
     * A interface gráfica é iniciada através do Event
     * Dispatch Thread (EDT), que é a thread utilizada
     * pelo Swing para manipulação de seus componentes.
     *
     */
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JanelaPrincipal janela =
                    new JanelaPrincipal();

            janela.setVisible(true);
        });
    }
}