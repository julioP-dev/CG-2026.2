package lab02;

import java.util.Collections;
import java.util.List;

/** Estado de uma iteração e os pixels que ela produz. */
public final class PassoAlgoritmo {
    private final String descricao;
    private final List<Ponto> pixels;

    public PassoAlgoritmo(String descricao, List<Ponto> pixels) {
        this.descricao = descricao;
        this.pixels = Collections.unmodifiableList(pixels);
    }

    public String getDescricao() { return descricao; }
    public List<Ponto> getPixels() { return pixels; }
}
