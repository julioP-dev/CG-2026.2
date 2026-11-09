package cg;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 * Teste da Parte 1. Primeiro confere o algoritmo numericamente; depois mostra
 * o resultado em uma janela ou, se receber um caminho, salva um PNG.
 *
 * <p>Uso: {@code java -cp Lab-03/out cg.TesteRasterizador [saida.png]}
 */
public final class TesteRasterizador {

    private static final int LARGURA = 900;
    private static final int ALTURA = 520;

    private TesteRasterizador() {}

    public static void main(String[] args) throws IOException {
        testarExemploDaAula();
        testarTodasAsDirecoes();
        testarFechamentoDoPoligono();
        System.out.println("Testes numéricos: OK");

        BufferedImage imagem = desenharCena();
        if (args.length > 0) {
            ImageIO.write(imagem, "png", new File(args[0]));
            System.out.println("Imagem salva em " + args[0]);
        } else {
            SwingUtilities.invokeLater(() -> mostrar(imagem));
        }
    }

    // ---------- testes numéricos ----------

    /** Reta (20,10)–(30,18), tabela do slide 28 da aula G3. */
    private static void testarExemploDaAula() {
        List<String> obtido = new ArrayList<>();
        Rasterizador.drawLine((x, y, c) -> obtido.add(x + "," + y), 20, 10, 30, 18, Color.BLACK);
        List<String> esperado = List.of("20,10", "21,11", "22,12", "23,12", "24,13", "25,14",
                "26,15", "27,16", "28,16", "29,17", "30,18");
        verificar(obtido.equals(esperado), "exemplo da aula: " + obtido);
    }

    /** Todas as retas partindo da origem até um quadrado de raio 40. */
    private static void testarTodasAsDirecoes() {
        for (int x2 = -40; x2 <= 40; x2++) {
            for (int y2 = -40; y2 <= 40; y2++) {
                List<int[]> ida = rasterizar(0, 0, x2, y2);
                String reta = "(0,0)-(" + x2 + "," + y2 + ")";

                verificar(ida.size() == Math.max(Math.abs(x2), Math.abs(y2)) + 1,
                        "quantidade de pixels em " + reta);
                verificar(contem(ida, 0, 0) && contem(ida, x2, y2), "extremos em " + reta);
                verificar(conexa(ida), "continuidade em " + reta);
                verificar(comoConjunto(ida).equals(comoConjunto(rasterizar(x2, y2, 0, 0))),
                        "ida e volta diferentes em " + reta);
                verificar(pertoDaReta(ida, x2, y2), "afastamento da reta ideal em " + reta);
            }
        }
    }

    private static void testarFechamentoDoPoligono() {
        Poligono triangulo = poligono(0, 0, 10, 0, 0, 10);
        Set<String> pixels = new HashSet<>();
        Rasterizador.drawPolygon((x, y, c) -> pixels.add(x + "," + y), triangulo, Color.BLACK);
        verificar(pixels.contains("5,5"), "última aresta do polígono não foi fechada");
        verificar(pixels.size() == 30, "triângulo com " + pixels.size() + " pixels");

        Set<String> arredondado = new HashSet<>();
        Rasterizador.drawPolygon((x, y, c) -> arredondado.add(x + "," + y),
                poligono(1.4, 1.6, 1.4, 1.6), Color.BLACK);
        verificar(arredondado.equals(Set.of("1,2")), "arredondamento: " + arredondado);
    }

    private static List<int[]> rasterizar(int x1, int y1, int x2, int y2) {
        List<int[]> pixels = new ArrayList<>();
        Rasterizador.drawLine((x, y, c) -> pixels.add(new int[] {x, y}), x1, y1, x2, y2, Color.BLACK);
        return pixels;
    }

    private static boolean contem(List<int[]> pixels, int x, int y) {
        return pixels.stream().anyMatch(p -> p[0] == x && p[1] == y);
    }

    /** Pixels consecutivos devem ser vizinhos (sem gaps). */
    private static boolean conexa(List<int[]> pixels) {
        for (int i = 1; i < pixels.size(); i++) {
            int[] a = pixels.get(i - 1);
            int[] b = pixels.get(i);
            if (Math.max(Math.abs(a[0] - b[0]), Math.abs(a[1] - b[1])) != 1) {
                return false;
            }
        }
        return true;
    }

    private static Set<String> comoConjunto(List<int[]> pixels) {
        Set<String> conjunto = new HashSet<>();
        for (int[] p : pixels) {
            conjunto.add(p[0] + "," + p[1]);
        }
        return conjunto;
    }

    /** No eixo secundário, cada pixel fica a no máximo meio pixel da reta ideal. */
    private static boolean pertoDaReta(List<int[]> pixels, int x2, int y2) {
        for (int[] p : pixels) {
            // |dy·x − dx·y| ≤ max(|dx|,|dy|)/2, em inteiros
            int erro = Math.abs(y2 * p[0] - x2 * p[1]);
            if (2 * erro > Math.max(Math.abs(x2), Math.abs(y2))) {
                return false;
            }
        }
        return true;
    }

    private static void verificar(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }

    // ---------- teste visual ----------

    private static BufferedImage desenharCena() {
        BufferedImage imagem = new BufferedImage(LARGURA, ALTURA, BufferedImage.TYPE_INT_RGB);
        Tela tela = (x, y, cor) -> {
            if (x >= 0 && x < LARGURA && y >= 0 && y < ALTURA) {
                imagem.setRGB(x, y, cor.getRGB());
            }
        };
        for (int y = 0; y < ALTURA; y++) {
            for (int x = 0; x < LARGURA; x++) {
                tela.setPixel(x, y, Color.WHITE);
            }
        }

        Rasterizador.drawPolygon(tela, poligonoTodasAsDirecoes(130, 40), new Color(0x1F3A68));
        desenharLeque(tela, 700, 130, 110, new Color(0xB03A2E));
        // Irregular e côncavo, com vértices em coordenadas fracionárias.
        Rasterizador.drawPolygon(tela, poligono(
                560.4, 300.2, 830.6, 310.7, 760.3, 390.1, 840.8, 490.5,
                690.2, 430.4, 580.5, 495.9, 620.7, 390.3), new Color(0x1E8449));
        return imagem;
    }

    /** Polígono convexo cujas 16 arestas cobrem os 8 octantes, horizontais e verticais. */
    private static Poligono poligonoTodasAsDirecoes(int xInicial, int yInicial) {
        int[][] arestas = {
            {80, 0}, {80, 30}, {50, 50}, {30, 80}, {0, 80}, {-30, 80}, {-50, 50}, {-80, 30},
            {-80, 0}, {-80, -30}, {-50, -50}, {-30, -80}, {0, -80}, {30, -80}, {50, -50}, {80, -30},
        };
        List<Ponto> vertices = new ArrayList<>();
        int x = xInicial + 60;
        int y = yInicial;
        for (int[] aresta : arestas) {
            vertices.add(new Ponto(x, y));
            x += aresta[0];
            y += aresta[1];
        }
        return new Poligono(vertices);
    }

    private static void desenharLeque(Tela tela, int cx, int cy, int raio, Color cor) {
        for (int graus = 0; graus < 360; graus += 15) {
            double angulo = Math.toRadians(graus);
            Rasterizador.drawLine(tela, cx, cy,
                    (int) Math.round(cx + raio * Math.cos(angulo)),
                    (int) Math.round(cy + raio * Math.sin(angulo)),
                    cor);
        }
    }

    private static Poligono poligono(double... coords) {
        List<Ponto> vertices = new ArrayList<>();
        for (int i = 0; i < coords.length; i += 2) {
            vertices.add(new Ponto(coords[i], coords[i + 1]));
        }
        return new Poligono(vertices);
    }

    private static void mostrar(BufferedImage imagem) {
        JFrame janela = new JFrame("Teste visual — Rasterizador");
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.add(new JLabel(new ImageIcon(imagem)));
        janela.pack();
        janela.setLocationRelativeTo(null);
        janela.setVisible(true);
    }
}
