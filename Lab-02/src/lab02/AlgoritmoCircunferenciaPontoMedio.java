package lab02;

import java.util.ArrayList;
import java.util.List;

/** Algoritmo incremental do ponto médio conforme as equações dos slides. */
public final class AlgoritmoCircunferenciaPontoMedio implements AlgoritmoCircunferencia {
    @Override public String getNome() { return "Circunferência — ponto médio"; }
    @Override public String toString() { return getNome(); }

    @Override
    public List<PassoAlgoritmo> calcular(int centroX, int centroY, int raio) {
        AlgoritmoCircunferenciaTrigonometrico.validarRaio(raio);
        List<PassoAlgoritmo> passos = new ArrayList<>();
        int x = 0;
        int y = raio;
        int d = 1 - raio; // Forma inteira de d inicial = 5/4 - R.
        passos.add(new PassoAlgoritmo(
                String.format("Inicial: x=0; y=R=%d; d=1-R=%d", raio, d),
                AlgoritmoCircunferenciaTrigonometrico.pixelsSimetricos(centroX, centroY, x, y)));

        int iteracao = 1;
        while (y > x) {
            int xAnterior = x;
            int yAnterior = y;
            int dAnterior = d;
            String decisao;
            int incrementoD;
            if (d < 0) {
                decisao = "E";
                incrementoD = 2 * xAnterior + 3;
                d += incrementoD;
            } else {
                decisao = "SE";
                incrementoD = 2 * (xAnterior - yAnterior) + 5;
                d += incrementoD;
                y--;
            }
            x++;
            String descricao = String.format(
                    "%d: d=%d; escolhe %s; Δd=%d; novo d=%d; x=%d→%d; y=%d→%d",
                    iteracao, dAnterior, decisao, incrementoD, d,
                    xAnterior, x, yAnterior, y);
            passos.add(new PassoAlgoritmo(descricao,
                    AlgoritmoCircunferenciaTrigonometrico.pixelsSimetricos(centroX, centroY, x, y)));
            iteracao++;
        }
        return passos;
    }
}
