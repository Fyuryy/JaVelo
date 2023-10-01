package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;
/**
 * Class WebMercator
 *
 * @author Pedro Gouveia (345768)
 * @author Idriss Mimet (324424)
 */
public final class WebMercator {

    /**
     * Constructeur privé
     */
    private WebMercator(){}

    /**
     * Calcule qui retourne la coordonnée x de la projection d'un point se trouvant à la longitude lon, donnée en radians
     * @param lon longitude du point
     * @return la coordonnée x de la projection d'un point se trouvant à la longitude lon, donnée en radians
     */
    public static double x(double lon){
        return (1/(2*Math.PI))*(lon+Math.PI);
    }
    /**
     * Calcule qui retourne la coordonnée y de la projection d'un point se trouvant à la longitude lot, donnée en radians
     * @param lat longitude du point
     * @return la coordonnée y de la projection d'un point se trouvant à la longitude lat, donnée en radians
     */
    public static double y(double lat){
        return (1/(2*Math.PI))*(Math.PI - Math2.asinh((Math.tan(lat))));
    }

    /**
     * Calcule qui retourne la longitude, en radians, d'un point dont la projection se trouve à la coordonnée x donnée
     * @param x coordonnée x du point
     * @return longitude, en radians, d'un point dont la projection se trouve à la coordonnée x donnée
     */
    public static double lon(double x){
        return 2*Math.PI*x - Math.PI;
    }

    /**
     * Calcule  qui retourne la latitude, en radians, d'un point dont la projection se trouve à la coordonnée y donnée.
     * @param y coordonnée y du point
     * @return  retourne la latitude, en radians, d'un point dont la projection se trouve à la coordonnée y donnée.
     */
    public static double lat(double y){
        return Math.atan(Math.sinh(Math.PI-2*Math.PI*y));
    }

}
