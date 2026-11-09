package cg;

import java.util.List;

/**
 * Ponto de integração entre a interface e os módulos de transformação.
 * A interface só conhece este contrato: para ligar os módulos reais (transformações
 * básicas, reflexão e cisalhamento, motor de composição), basta uma implementação
 * que traduza cada Operacao para o módulo correspondente.
 */
public interface MotorTransformacoes {

    /**
     * Aplica as operações ao polígono, na ordem da lista (a primeira é aplicada primeiro).
     * Coordenadas do mundo: origem no centro, x para a direita, y para cima.
     * Deve devolver um novo Poligono, sem alterar o original.
     */
    Poligono aplicar(Poligono poligono, List<Operacao> operacoes);

    /** Texto com a matriz composta, para o passo a passo; null se o motor não expõe a matriz. */
    default String matrizComposta(List<Operacao> operacoes) {
        return null;
    }
}
