package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

/**
 * Enregistrement RoutePoint
 *
 * @author Pedro Gouveia (345768)
 * @author Idriss Mimet (324424)
 */

public record RoutePoint(PointCh point, double position, double distanceToReference) {

    /**
     * Constante NONE: répresente un point RoutePoint null;
     */
    public static final RoutePoint NONE = new RoutePoint(null, Double.NaN, Double.POSITIVE_INFINITY);

    /**
     * Retourne un point identique au récepteur mais dont la position est décalé d'une distance donnée
     * @param positionDifference distance à decaler le point
     * @return une nouvelle instance de RoutePoint identique au point.this, sauf pour la position qui a été décalée
     * d'une distance donnée
     */
    public RoutePoint withPositionShiftedBy(double positionDifference){

            return (positionDifference==0) ?
                    this : new RoutePoint(point,
                    position+positionDifference,
                    distanceToReference);
    }

    /**
     * Compare la distance à la référence des deux points et retourne celui dont la distance est la plus courte
     * @param that point à comparer avec le point récépteur (this)
     * @return this si la distance à la réference du point this est plus courte que la distance du point that au point
     * de référence et that autrement
     */
    public RoutePoint min(RoutePoint that){
        return distanceToReference <= that.distanceToReference ? this : that;
    }

    /**
     * Compare la distance à la référence d'un point passé en argument avec une distance donnée
     * @param thatPoint point duquel on compare la distance à la référence
     * @param thatPosition position du point passé en argument
     * @param thatDistanceToReference distance utilisée pour comparer avec la distance à la référence du point
     * passé en argument
     * @return this (point récepteur si sa distance à la référence est plus petite ou égale à thatDistanceToReference
     * ou retourne une nouvelle instance de RoutePoint avec les arguments passés en paramètre
     */
    public RoutePoint min(PointCh thatPoint, double thatPosition, double thatDistanceToReference){
       return min(new RoutePoint(thatPoint, thatPosition, thatDistanceToReference));

    }

}
