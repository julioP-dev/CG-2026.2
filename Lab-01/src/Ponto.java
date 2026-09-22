/**
 * Representa um ponto X e Y.
 *
 * A classe é utilizada para representar pontos nos diferentes
 * sistemas de coordenadas utilizados pelo laboratório
 */
public class Ponto {

    private double x;
    private double y;

    /**
     * Cria um ponto com as coordenadas informadas.
     *
     * @param x coordenada X do ponto
     * @param y coordenada Y do ponto
     */
    public Ponto(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @return coordenada X
     */
    public double getX() {
        return x;
    }

    /**
     * @return coordenada Y
     */
    public double getY() {
        return y;
    }

    /**
     * @param x nova coordenada X
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * @param y nova coordenada Y
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * @return ponto no formato (x, y)
     */
    @Override
    public String toString() {
        return String.format("(%.3f, %.3f)", x, y);
    }
}