package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

/**
 * Enregistrement PointCh
 *
 * @author Pedro Gouveia (345768)
 * @author Idriss Mimet (324424)
 */
public record PointCh(double e, double n) {
    /**
     * Constructeur compact
     * @param e coordonnée e du point
     * @param n coordonnée n du point
     * @throws IllegalArgumentException si les coordonnées e ou n ne sont pas dans les limites de la Suisse
     */
    public PointCh{
        Preconditions.checkArgument(SwissBounds.containsEN(e, n));

    }

    /**
     * Calcule le carré de la distance en mètres entre deux points
     * @param that
     * @return le carré de la distance en mètres séparant le point (this) du point (that)
     */
    public double squaredDistanceTo(PointCh that){
        return Math2.squaredNorm(e-that.e, n-that.n);
    }

    /**
     * Calcule la distance en mètres entre deux points
     * @param that
     * @return la distance en mètres séparant le point (this) du point (that)
     */
    public double distanceTo(PointCh that){
        return Math.sqrt(squaredDistanceTo(that));
    }

    /**
     * Calcule la longitude du point this, dans le systeme WGS84, en radians
     * @return la longitude du point this, dans le system WGS85, en radians
     */
    public double lon(){
        return Ch1903.lon(e,n);
    }
    /**
     * Calcule la latitude du point this dans le système WGS84 en radians
     * @return la latitude du point this dans le système WGS84
     */
    public double lat(){
        return Ch1903.lat(e, n);
    }



}


