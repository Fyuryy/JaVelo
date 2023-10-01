package ch.epfl.javelo.routing;


import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.function.DoubleUnaryOperator;

/**
 * Enregistrement Edge
 *
 * @author Pedro Gouveia (345768)
 * @author Idriss Mimet (324424)
 */

public record Edge(int fromNodeId, int toNodeId, PointCh fromPoint, PointCh toPoint,
                   double length, DoubleUnaryOperator profile) {

    /**
     * Méthode facilitant la construction d'une instance d'Edge
     * @param graph graph contenant le reste des attributs nécessaires à la construction d'une Edge
     * @param edgeId id de l'egde dans le graph, on l'utilise pour accéder aux attributs restants
     * @param fromNodeId id du node de départ de l'arête
     * @param toNodeId id du node d'arrivée de l'arête
     * @return une instance de Edge dont les attributs fromNodeId et toNodeId sont ceux donnés,
     * les autres étant ceux de l'arête d'identité edgeId dans le graphe Graph.
     */
    public static Edge of(Graph graph, int edgeId, int fromNodeId, int toNodeId){

            return new Edge(fromNodeId,
                    toNodeId,
                    graph.nodePoint(fromNodeId),
                    graph.nodePoint(toNodeId),
                    graph.edgeLength(edgeId),
                    graph.edgeProfile(edgeId));
    }

    /**
     * Calcule la distance de la position sur l'arête se trouvant le plus proche possible du point passé en paramètre
     * @param point point duquel on va calculer la projection
     * @return la distance de la projection du point passé en paramètre, qui est la position du point le plus proche
     * sur l'arête du point passé en argument
     */
    public double positionClosestTo(PointCh point){
        return Math2.projectionLength(fromPoint.e(),
                fromPoint.n(),
                toPoint.e(),
                toPoint().n(),
                point.e(),
                point.n());
    }


    /**
     * Retourne un point dont la position est passée en paramètre
     * @param position position de laquelle on veut trouver le point
     * @return PointCh se trouvant à la distance voulue, sur l'arête
     */
    public PointCh pointAt(double position){
        if(length!=0){
            double factor = position / length;
            return new PointCh(Math2.interpolate(fromPoint.e(), toPoint.e(),factor),
                    Math2.interpolate(fromPoint.n(),toPoint.n(),factor));
        }else{
            return fromPoint;
        }

    }

    /**
     * Retourne l'altitude, en mètres, à la position donnée sur l'arête
     * @param position distance de laquelle on veut connaître l'altitude
     * @return l'altitude à la position donnée
     */
    public double elevationAt(double position){
        return  profile.applyAsDouble(position);

    }






























}
