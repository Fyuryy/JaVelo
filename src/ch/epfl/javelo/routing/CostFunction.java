package ch.epfl.javelo.routing;

/**
 * Interface CostFunction
 *
 * @author Pedro Gouveia (345768)
 * @author Idriss Mimet (324424)
 */

public interface CostFunction {

    /**
     * Retourne le facteur par lequel la longueur de l'arête d'id edgeId, partant du noeud
     * d'id nodeId doit être multipliée. Ce facteur doit impérativement être supérieur ou
     * égal à 1. S'il vaut infinity, alors l'arête ne peut pas être empruntée
     * @param nodeId id du noeud d'où part l'edge
     * @param edgeId id de l'edge qui va être multiplié par le facteur
     */
    double costFactor(int nodeId, int edgeId);



}
