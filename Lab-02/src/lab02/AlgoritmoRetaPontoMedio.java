package lab02;
import java.util.ArrayList;
import java.util.List;
/**
 * implementação do algoritmo do Ponto Médio (Bresenham's Line Algorithm)
 * para todos os octantes
 */
public final class AlgoritmoRetaPontoMedio implements AlgoritmoReta {
	@Override public String getNome() {
		return "Reta — Ponto Médio";
	}
	@Override public String toString() {
		return getNome();
	}
	@Override
	public List < PassoAlgoritmo > calcular(int x1, int y1, int x2, int y2) {
		List < PassoAlgoritmo > passos = new ArrayList < > ();
		int dx, dy, incE, incNE, d, x, y;


        // Encontrar octante
        dx = x2 - x1;
		dy = y2 - y1;
		boolean inverteDeltaX = dx < 0;
		boolean inverteDeltaY = dy < 0;
		boolean trocaDeltas = Math.abs(dy) > Math.abs(dx);

		// Trazer o ponto para o 1º octante, caso necessário
        if (inverteDeltaX) dx = -dx;
        if (inverteDeltaY) dy = -dy;
        if (trocaDeltas){
            int dxAntigo = dx;
            dx = dy;
            dy = dxAntigo;
        }

        d = 2 * dy - dx; /* Valor inicial de d */
		incE = 2 * dy; /* Incremento de E */
		incNE = 2 * (dy - dx); /* Incremento de NE */
		x = 0;
		y = 0;

        // Laço principal
		passos.add(new PassoAlgoritmo(String.format(
				"Inicial: dx=%d; dy=%d; inverte x=%b; inverte y=%b; troca x↔y=%b; dx'=%d; dy'=%d; d=%d; incE=%d; incNE=%d; local=(%d, %d)",
				x2 - x1, y2 - y1, inverteDeltaX, inverteDeltaY, trocaDeltas,
				dx, dy, d, incE, incNE, x, y), pixel(x1, y1)));
		int iteracao = 1;
		while(x < dx) {
			int xAnterior = x;
			int yAnterior = y;
			int dAnterior = d;
			String decisao;
			int incD;

			if(d <= 0) {
				/* Escolhe E */
				decisao = "E";
				incD = incE;
				x = x + 1;
			} else {
				/* Escolhe NE */
				decisao = "NE";
				incD = incNE;
				x = x + 1;
				y = y + 1;
			}

			d += incD;


			// Desfaz as transformações para exibir o pixel no octante real
			int xReal = x;
			int yReal = y;
			if (trocaDeltas) {
				int xAux = xReal;
				xReal = yReal;
				yReal = xAux;
			}
			if (inverteDeltaX) {
				xReal = -xReal;
			}
			if (inverteDeltaY) {
				yReal = -yReal;
			}

			xReal += x1;
			yReal += y1;

			List<Ponto> pixel = pixel(xReal, yReal);
			Ponto real = pixel.get(0);
            String descricao = String.format(
					"%d: d=%d; escolhe %s; Δd=%d; novo d=%d; local=(%d, %d)→(%d, %d); pixel=(%d, %d)",
					iteracao, dAnterior, decisao, incD, d, xAnterior, yAnterior, x, y,
					(int) real.getX(), (int) real.getY());

			passos.add(new PassoAlgoritmo(descricao, pixel));
			iteracao++;
		}
		return passos;
	}

	private static List <Ponto> pixel(int x, int y) {
		List <Ponto> pixels = new ArrayList < > ();
		pixels.add(new Ponto(x, y));
		return pixels;
	}
}
