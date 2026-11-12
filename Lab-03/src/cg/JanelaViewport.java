package cg;

/**
 * Transformação janela–viewport (aula G5, slides 29 a 33): leva a janela do mundo
 * [xMin, xMax] × [yMin, yMax] para a viewport [uMin, uMax] × [vMin, vMax] da tela.
 *
 * <p>M = T(uMin, vMin) · S(sx, sy) · T(−xMin, −yMin), com
 * sx = (uMax − uMin) / (xMax − xMin) e sy = (vMax − vMin) / (yMax − yMin):
 * leva o canto da janela à origem, ajusta o tamanho e leva à posição da viewport.
 */
public final class JanelaViewport {
    private JanelaViewport() {}

    /** @throws IllegalArgumentException se, na janela ou na viewport, o mínimo não for menor que o máximo */
    public static Matriz3 matriz(double xMin, double xMax, double yMin, double yMax,
                                 double uMin, double uMax, double vMin, double vMax) {
        if (!(xMin < xMax && yMin < yMax)) {
            throw new IllegalArgumentException("Na janela, o mínimo deve ser menor que o máximo em x e em y.");
        }
        if (!(uMin < uMax && vMin < vMax)) {
            throw new IllegalArgumentException("Na viewport, o mínimo deve ser menor que o máximo em u e em v.");
        }
        double sx = (uMax - uMin) / (xMax - xMin);
        double sy = (vMax - vMin) / (yMax - yMin);
        // Composicao.compor aplica na ordem dada: T(−xMin, −yMin), depois S(sx, sy), depois T(uMin, vMin).
        return Composicao.compor(
                TransformacoesBasicas.translacao(-xMin, -yMin),
                TransformacoesBasicas.escala(sx, sy),
                TransformacoesBasicas.translacao(uMin, vMin));
    }
}
