/**
 * Representa a janela de visualização no sistema de coordenadas
 * do mundo/usuário.
 */
public class JanelaMundo {

    private double xMin;
    private double xMax;
    private double yMin;
    private double yMax;

    /**
     * Cria uma janela de mundo.
     *
     * @param xMin limite inferior de X
     * @param xMax limite superior de X
     * @param yMin limite inferior de Y
     * @param yMax limite superior de Y
     *
     * @throws IllegalArgumentException caso algum intervalo
     *         seja inválido
     */
    public JanelaMundo(
            double xMin,
            double xMax,
            double yMin,
            double yMax) {

        if (xMin >= xMax) {
            throw new IllegalArgumentException(
                    "Xmin deve ser menor que Xmax."
            );
        }

        if (yMin >= yMax) {
            throw new IllegalArgumentException(
                    "Ymin deve ser menor que Ymax."
            );
        }

        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
    }

    public double getXMin() {
        return xMin;
    }

    public double getXMax() {
        return xMax;
    }

    public double getYMin() {
        return yMin;
    }

    public double getYMax() {
        return yMax;
    }

    public void setXMin(double xMin) {
        if (xMin >= xMax) {
            throw new IllegalArgumentException(
                    "Xmin deve ser menor que Xmax."
            );
        }

        this.xMin = xMin;
    }

    public void setXMax(double xMax) {
        if (xMax <= xMin) {
            throw new IllegalArgumentException(
                    "Xmax deve ser maior que Xmin."
            );
        }

        this.xMax = xMax;
    }

    public void setYMin(double yMin) {
        if (yMin >= yMax) {
            throw new IllegalArgumentException(
                    "Ymin deve ser menor que Ymax."
            );
        }

        this.yMin = yMin;
    }

    public void setYMax(double yMax) {
        if (yMax <= yMin) {
            throw new IllegalArgumentException(
                    "Ymax deve ser maior que Ymin."
            );
        }

        this.yMax = yMax;
    }
}