package lab02;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/** Display para o Lab-02; cada chamada drawPixel acende um único pixel. */
public final class DisplayPrimitivas extends JPanel {
    private final int largura;
    private final int altura;
    private final BufferedImage imagem;

    public DisplayPrimitivas(int largura, int altura) {
        this.largura = largura;
        this.altura = altura;
        imagem = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_RGB);
        setPreferredSize(new Dimension(largura, altura));
        setBackground(Color.WHITE);
        limpar();
    }

    /** Acende somente o pixel (x,y), sem desenhar segmentos ou formas. */
    public void drawPixel(int x, int y) {
        if (x >= 0 && x < largura && y >= 0 && y < altura) {
            imagem.setRGB(x, y, Color.GREEN.darker().getRGB());
            repaint(x, y, 1, 1);
        }
    }

    public void limpar() {
        Graphics g = imagem.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, largura, altura);
        g.dispose();
        int corEixo = new Color(55, 65, 75).getRGB();
        int origemX = largura / 2;
        int origemY = altura / 2;
        for (int x = 0; x < largura; x++) imagem.setRGB(x, origemY, corEixo);
        for (int y = 0; y < altura; y++) imagem.setRGB(origemX, y, corEixo);
        repaint();
    }

    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // O cruzamento central marca o centro visual do display; as coordenadas
        // de dispositivo continuam com origem no canto superior esquerdo.
        // A geometria do algoritmo continua sendo plotada exclusivamente por drawPixel.
        g.drawImage(imagem, 0, 0, null);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        g.drawString("x →", largura - 35, altura / 2 - 6);
        g.drawString("y ↓", largura / 2 + 5, altura - 7);
        g.drawString("origem dispositivo (0,0)", 5, 15);
        g.drawString("centro da tela (" + largura / 2 + ", " + altura / 2 + ") px",
                largura / 2 + 5, altura / 2 + 15);
    }
}
