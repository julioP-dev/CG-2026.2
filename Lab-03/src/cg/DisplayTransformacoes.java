package cg;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

/**
 * Display do Lab-03. Implementa Tela: o Rasterizador acende cada pixel por setPixel.
 * Os polígonos chegam em coordenadas do mundo (origem no centro, y para cima,
 * 1 unidade = 1 pixel) e são convertidos para o dispositivo (origem no canto superior
 * esquerdo, y para baixo) antes de serem rasterizados.
 */
public final class DisplayTransformacoes extends JPanel implements Tela {
    public static final Color COR_ORIGINAL = new Color(0x1F3A68);
    public static final Color COR_ETAPA = new Color(0x9AA3AD);
    public static final Color COR_RESULTADO = new Color(0xB03A2E);

    private static final int PASSO_GRADE = 50;
    private static final Color COR_GRADE = new Color(232, 235, 238);
    private static final Color COR_EIXO = new Color(120, 130, 140);

    private final int largura;
    private final int altura;
    private final BufferedImage imagem;

    public DisplayTransformacoes(int largura, int altura) {
        this.largura = largura;
        this.altura = altura;
        imagem = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_RGB);
        setPreferredSize(new Dimension(largura, altura));
        limpar();
    }

    /** Acende somente o pixel (x,y); o que ficar fora do display é ignorado. */
    @Override
    public void setPixel(int x, int y, Color cor) {
        if (x >= 0 && x < largura && y >= 0 && y < altura) {
            imagem.setRGB(x, y, cor.getRGB());
        }
    }

    /** Apaga o display e redesenha a grade e os eixos. */
    public void limpar() {
        Graphics g = imagem.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, largura, altura);
        g.dispose();
        int origemX = largura / 2;
        int origemY = altura / 2;
        for (int x = origemX % PASSO_GRADE; x < largura; x += PASSO_GRADE) {
            Rasterizador.drawLine(this, x, 0, x, altura - 1, COR_GRADE);
        }
        for (int y = origemY % PASSO_GRADE; y < altura; y += PASSO_GRADE) {
            Rasterizador.drawLine(this, 0, y, largura - 1, y, COR_GRADE);
        }
        Rasterizador.drawLine(this, 0, origemY, largura - 1, origemY, COR_EIXO);
        Rasterizador.drawLine(this, origemX, 0, origemX, altura - 1, COR_EIXO);
        repaint();
    }

    /** Rasteriza o polígono (em coordenadas do mundo) com o algoritmo do ponto médio. */
    public void desenhar(Poligono mundo, Color cor) {
        List<Ponto> vertices = new ArrayList<>();
        for (Ponto p : mundo.vertices) {
            vertices.add(new Ponto(largura / 2 + p.x(), altura / 2 - p.y()));
        }
        Rasterizador.drawPolygon(this, new Poligono(vertices), cor);
        repaint();
    }

    /** Converte um pixel do display (clique) para coordenadas do mundo. */
    public Ponto dispositivoParaMundo(int pixelX, int pixelY) {
        return new Ponto(pixelX - largura / 2, altura / 2 - pixelY);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(imagem, 0, 0, null);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        g.setColor(Color.DARK_GRAY);
        g.drawString("x →", largura - 35, altura / 2 - 6);
        g.drawString("y ↑", largura / 2 + 5, 15);
        g.drawString("origem (0, 0)", largura / 2 + 5, altura / 2 + 15);
        g.drawString("grade de 50 em 50 unidades (1 unidade = 1 pixel)", 5, altura - 7);
        g.setColor(COR_ORIGINAL);
        g.drawString("■ original", 5, 15);
        g.setColor(COR_ETAPA);
        g.drawString("■ etapas", 5, 30);
        g.setColor(COR_RESULTADO);
        g.drawString("■ resultado", 5, 45);
    }
}
