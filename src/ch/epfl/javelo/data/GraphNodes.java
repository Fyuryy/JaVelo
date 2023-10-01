package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Q28_4;
import java.nio.IntBuffer;

/**
 *  * Enregistrement GraphNodes
 *  *
 *  * @author Pedro Gouveia (345768)
 *  * @author Idriss Mimet (324424)
 *  */

public record GraphNodes(IntBuffer buffer) {

    /**
     * East coordinate
     */
    private static final int POSITION_E = 0;
    /**
     * North coordinate
     */
    private static final int POSITION_N = POSITION_E+1;
    /**
     * LEAVING EDGES FROM A NODE
     */
    private static final int EDGES_SORTANTS = POSITION_N+1;
    /**
     * CAPACITY TAKEN TO REPRESENT A NODE INSIDE THE BUFFER
     */
    private static final int NODE_INTS = EDGES_SORTANTS+1;
    /**
     * Début de l'edge
     */
    private static final int START_EDGE = 0;

    /**
     * index du bit de début des edges sortantes
     */
    private static final int START_OUT_DEGREE = 28;

    private static final int EDGE_LENGTH = 28;

    private static final int OUT_DEGREE_LENGTH = 4;


    /** Retourne la capacité du buffer
     * @return la capacité du buffer
     */
    public int count(){
        return buffer.capacity()/3;
    }
    /** Retourne la coordonnée E du node d'id donné
     * @param nodeId Id du node duquel on veut la coordonnée N
     * @return la coordonnée E du node d'id donné
     */
    public double nodeE(int nodeId){
        return Q28_4.asDouble(buffer.get(nodeId * NODE_INTS + POSITION_E));
    }
    /** Retourne la coordonnée N du node d'id donné
     * @param nodeId Id du node duquel on veut la coordonnée N
     * @return la coordonnée N du node d'id donné
     */
    public double nodeN(int nodeId){return Q28_4.asDouble(buffer.get(nodeId * NODE_INTS + POSITION_N));}

    /** Retourne le nombre d'arêtes sortantes du noeud d'id donné
     * @param nodeId Id du node duquel on veut le nombre d'arêtes sortantes
     * @return le nombre d'arêtes sortantes du noeud
     */
    public int outDegree(int nodeId){
        return Bits.extractUnsigned(buffer.get(nodeId * NODE_INTS + EDGES_SORTANTS), START_OUT_DEGREE, OUT_DEGREE_LENGTH);
    }

    /** Retourne l'identité de la edgeIndex-ième arête sortante du noeud d'id nodeId
     * @param nodeId Id du node duquel on veut la coordonnée N
     * @param edgeIndex index de l'arête voulue
     * @return l'identité de la edgeIndex-ième arête sortante du noeud d'id nodeId
     */
    public int edgeId(int nodeId, int edgeIndex){
        assert 0 <= edgeIndex && edgeIndex < outDegree(nodeId);
        return Bits.extractUnsigned(buffer.get(nodeId * NODE_INTS + EDGES_SORTANTS), START_EDGE, EDGE_LENGTH) + edgeIndex;

    }



}




