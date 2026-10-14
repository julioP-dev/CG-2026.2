package lab02;

import java.util.List;

/** Contrato comum aos algoritmos de rasterização da circunferência. */
public interface AlgoritmoCircunferencia {
    String getNome();
    List<PassoAlgoritmo> calcular(int centroX, int centroY, int raio);
}
