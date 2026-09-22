import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * Representa o display utilizado pelo laboratório.
 * O display é representado por uma imagem com resolução
 * fixa em pixels.
 * Cada posição da BufferedImage corresponde diretamente
 * a um pixel do display.
 * O pixel ativo é desenhado na cor verde pura RGB (#00FF00).
 */
public class Display extends JPanel {

    private final int largura;
    private final int altura;

    private final BufferedImage imagem;

    /**
     * Pixel atualmente ativo.
     */
    private Ponto pixelAtivo;

    /**
     * Cria um display.
     * @param largura largura em pixels
     * @param altura altura em pixels
     */
    public Display(int largura, int altura) {

        this.largura = largura;
        this.altura = altura;

        imagem = new BufferedImage(
                largura,
                altura,
                BufferedImage.TYPE_INT_RGB
        );

        limpar();

        setPreferredSize(
                new Dimension(largura, altura)
        );
    }

    /**
     * Ativa um único pixel do display.
     *
     * @param x coordenada X do pixel
     * @param y coordenada Y do pixel
     */
    public void drawPixel(int x, int y) {

        limpar();

        /*
         * Verifica se o pixel está dentro dos limites
         * físicos do display.
         */
        if (x < 0 || x >= largura ||
                y < 0 || y >= altura) {

            repaint();
            return;
        }

        /*
         * Guarda a posição do pixel ativo.
         */
        pixelAtivo = new Ponto(x, y);

        /*
         * Ativa um pixel da imagem.
         */
        imagem.setRGB(
                x,
                y,
                Color.GREEN.getRGB()
        );

        /*
         * Solicita que o Swing redesenhe o componente.
         */
        repaint();
    }

    /**
     * Limpa o display.
     *
     * Todos os pixels voltam para preto
     */
    public void limpar() {

        Graphics graphics = imagem.getGraphics();

        graphics.setColor(Color.BLACK);

        graphics.fillRect(
                0,
                0,
                largura,
                altura
        );

        graphics.dispose();

        pixelAtivo = null;
    }

    /**
     * Responsável pela renderização da imagem no JPanel.
     *
     * O método é chamado automaticamente pelo Swing
     * quando o componente precisa ser redesenhado.
     *
     * @param graphics contexto gráfico fornecido pelo Swing
     */
    @Override
    protected void paintComponent(Graphics graphics) {

        super.paintComponent(graphics);

        graphics.drawImage(
                imagem,
                0,
                0,
                null
        );
    }

    /**
     * @return pixel ativo ou null
     */
    public Ponto getPixelAtivo() {
        return pixelAtivo;
    }

    /**
     * @return largura em pixels
     */
    public int getLargura() {
        return largura;
    }

    /**
     * @return altura em pixels
     */
    public int getAltura() {
        return altura;
    }
}