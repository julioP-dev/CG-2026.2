/**
 * Contém as transformações entre os diferentes sistemas
 * de coordenadas utilizados no laboratório.
 *
 */
public class Transformacao {

    /**
     * Converte coordenadas do dispositivo para NDC [0,1].
     *
     * @param dispositivo ponto em coordenadas do dispositivo
     * @param largura largura do display em pixels
     * @param altura altura do display em pixels
     * @return ponto em NDC [0,1]
     */
    public static Ponto dcToNdc01(
            Ponto dispositivo,
            int largura,
            int altura) {

        validarDimensoes(largura, altura);

        double x =
                dispositivo.getX()
                        / (largura - 1);

        double y =
                dispositivo.getY()
                        / (altura - 1);

        return new Ponto(x, y);
    }

    /**
     * Converte NDC [0,1] para coordenadas do dispositivo.
     *
     * @param ndc ponto em NDC [0,1]
     * @param largura largura do display em pixels
     * @param altura altura do display em pixels
     * @return ponto em coordenadas do dispositivo
     */
    public static Ponto ndc01ToDc(
            Ponto ndc,
            int largura,
            int altura) {

        validarDimensoes(largura, altura);

        double x =
                ndc.getX()
                        * (largura - 1);

        double y =
                ndc.getY()
                        * (altura - 1);

        return new Ponto(x, y);
    }

    /**
     * Converte coordenadas do dispositivo para NDC [-1,1].
     *
     * @param dispositivo ponto em coordenadas do dispositivo
     * @param largura largura do display em pixels
     * @param altura altura do display em pixels
     * @return ponto em NDC [-1,1]
     */
    public static Ponto dcToNdc11(
            Ponto dispositivo,
            int largura,
            int altura) {

        validarDimensoes(largura, altura);

        double x =
                2.0 * dispositivo.getX()
                        / (largura - 1)
                        - 1.0;

        double y =
                2.0 * dispositivo.getY()
                        / (altura - 1)
                        - 1.0;

        return new Ponto(x, y);
    }

    /**
     * Converte NDC [-1,1] para coordenadas do dispositivo.
     *
     * @param ndc ponto em NDC [-1,1]
     * @param largura largura do display em pixels
     * @param altura altura do display em pixels
     * @return ponto em coordenadas do dispositivo
     */
    public static Ponto ndc11ToDc(
            Ponto ndc,
            int largura,
            int altura) {

        validarDimensoes(largura, altura);

        double x =
                ((ndc.getX() + 1.0) / 2.0)
                        * (largura - 1);

        double y =
                ((ndc.getY() + 1.0) / 2.0)
                        * (altura - 1);

        return new Ponto(x, y);
    }

    /**
     * Converte NDC [0,1] para coordenadas do mundo.
     *
     * @param ndc ponto em NDC [0,1]
     * @param janela janela definida no sistema de coordenadas
     *               do mundo
     * @return ponto em coordenadas do mundo
     */
    public static Ponto ndc01ToUser(
            Ponto ndc,
            JanelaMundo janela) {

        double x =
                janela.getXMin()
                        + ndc.getX()
                        * (janela.getXMax() - janela.getXMin());

        double y =
                janela.getYMin()
                        + ndc.getY()
                        * (janela.getYMax() - janela.getYMin());

        return new Ponto(x, y);
    }

    /**
     * Converte coordenadas do mundo para NDC [0,1].
     *
     * @param mundo ponto em coordenadas do mundo
     * @param janela janela do mundo
     * @return ponto em NDC [0,1]
     */
    public static Ponto userToNdc01(
            Ponto mundo,
            JanelaMundo janela) {

        double x =
                (mundo.getX() - janela.getXMin())
                        / (janela.getXMax() - janela.getXMin());

        double y =
                (mundo.getY() - janela.getYMin())
                        / (janela.getYMax() - janela.getYMin());

        return new Ponto(x, y);
    }

    /**
     * Converte NDC [-1,1] para coordenadas do mundo.
     *
     * Primeiro, o NDC [-1,1] é convertido para [0,1]
     *
     * Depois é aplicada a transformação para o mundo.
     *
     * @param ndc ponto em NDC [-1,1]
     * @param janela janela do mundo
     * @return ponto em coordenadas do mundo
     */
    public static Ponto ndc11ToUser(
            Ponto ndc,
            JanelaMundo janela) {

        double x =
                janela.getXMin()
                        + ((ndc.getX() + 1.0) / 2.0)
                        * (janela.getXMax() - janela.getXMin());

        double y =
                janela.getYMin()
                        + ((ndc.getY() + 1.0) / 2.0)
                        * (janela.getYMax() - janela.getYMin());

        return new Ponto(x, y);
    }

    /**
     * Converte coordenadas do mundo para NDC [-1,1].
     *
     * @param mundo ponto em coordenadas do mundo
     * @param janela janela do mundo
     * @return ponto em NDC [-1,1]
     */
    public static Ponto userToNdc11(
            Ponto mundo,
            JanelaMundo janela) {

        double x =
                2.0
                        * (mundo.getX() - janela.getXMin())
                        / (janela.getXMax() - janela.getXMin())
                        - 1.0;

        double y =
                2.0
                        * (mundo.getY() - janela.getYMin())
                        / (janela.getYMax() - janela.getYMin())
                        - 1.0;

        return new Ponto(x, y);
    }

    /**
     * Verifica se as dimensões do display são válidas.
     *
     * @param largura largura do display
     * @param altura altura do display
     */
    private static void validarDimensoes(
            int largura,
            int altura) {

        if (largura < 2 || altura < 2) {
            throw new IllegalArgumentException(
                    "Largura e altura devem ser maiores que 1."
            );
        }
    }
}