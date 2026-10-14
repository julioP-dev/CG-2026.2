package lab02;
 
import java.util.ArrayList;
import java.util.List;
 

public final class AlgoritmoCircunferenciaPolinomial implements AlgoritmoCircunferencia {
    @Override public String getNome() { return "Circunferência — polinomial"; }
    @Override public String toString() { return getNome(); }
 
    @Override
    public List<PassoAlgoritmo> calcular(int centroX, int centroY, int raio) {
        AlgoritmoCircunferenciaTrigonometrico.validarRaio(raio);
        List<PassoAlgoritmo> passos = new ArrayList<>();
        double xFinal = raio / Math.sqrt(2.0); 
        int incremento = 1;                    
        int x = 0;
        int iteracao = 0;

        while (x <= xFinal) {
            double yExato = Math.sqrt(raio * raio - x * x);
            int y = (int) Math.round(yExato);
            String descricao = String.format(
                    "%d: x=%d; y=√(R²−x²)=%.6f; arredondado: y=%d",
                    iteracao, x, yExato, y);
            passos.add(new PassoAlgoritmo(descricao,
                    AlgoritmoCircunferenciaTrigonometrico.pixelsSimetricos(centroX, centroY, x, y)));
            x += incremento;
            iteracao++;
        }
        return passos;
    }
}
 