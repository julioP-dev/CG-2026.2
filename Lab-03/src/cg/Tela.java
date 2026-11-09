package cg;

import java.awt.Color;

/** Superfície de desenho: único ponto de saída de pixels. */
public interface Tela {
    void setPixel(int x, int y, Color cor);
}
