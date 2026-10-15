package lab02;

import java.util.List;

/** Contrato comum aos algoritmos de rasterização de segmentos de reta. */
public interface AlgoritmoReta {
    String getNome();
    List<PassoAlgoritmo> calcular(int x1, int y1, int x2, int y2);
}
