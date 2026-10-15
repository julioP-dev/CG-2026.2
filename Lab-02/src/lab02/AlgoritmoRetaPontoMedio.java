package lab02;
import java.util.ArrayList;
import java.util.List;
/**
 * implementação do algoritmo do Ponto Médio (Bresenham's Line Algorithm)
 * para 1º octante: 0 <= m <= 1, x1 <= x2
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
		dx = x2 - x1;
		dy = y2 - y1;
		d = 2 * dy - dx; /* Valor inicial de d */
		incE = 2 * dy; /* Incremento de E */
		incNE = 2 * (dy - dx); /* Incremento de NE */
		x = x1;
		y = y1;
		validarEntrada(x1, y1, x2, y2);;
		passos.add(new PassoAlgoritmo(String.format("Inicial: x=%d; y=%d, dx=%d, dy=%d, dInicial=%d, incE=%d, incNE=%d,", x, y, dx, dy, d, incE, incNE), pixel(x, y)));
		int iteracao = 1;
		while(x < x2) {
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

            String descricao = String.format("%d: d=%d; escolhe %s; Δd=%d; novo d=%d; x=%d→%d; y=%d→%d", iteracao, dAnterior, decisao, incD, d, xAnterior, x, yAnterior, y);
			passos.add(new PassoAlgoritmo(descricao, pixel(x, y)));
			iteracao++;
		}
		return passos;
	}
	private static void validarEntrada(int x1, int y1, int x2, int y2) {
		boolean valido = (Math.abs(x2 - x1) >= Math.abs(y2 - y1)) && (x2 >= x1) && (y2 >= y1);
		if(!valido) throw new IllegalArgumentException("Os pontos devem estar no primeiro octante.");
	}
	private static List < Ponto > pixel(int x, int y) {
		List < Ponto > pixels = new ArrayList < > ();
		pixels.add(new Ponto(x, y));
		return pixels;
	}
}
