package ch.epfl.javelo.projection;
/**
 * Class Ch1903
 *
 * @author Pedro Gouveia (345768)
 * @author Idriss Mimet (324424)
 */
public final class Ch1903 {
    /**
     * Constructeur privé (classe non-instantiabole)
     */
    private Ch1903(){};

    /**
     * Calcule qui retourne la coordonnée E du point de longitude lon et latitude lat en radians dans le système WGS84
     * @param lon longitude en radians du point
     * @param lat latitude en radians du point
     * @return la coordonnée E du point de longitude lon et latitude lat dans le système WGS84
     */
    public static double e(double lon, double lat){
        double lambda1 = (3600*Math.toDegrees(lon) - 26782.5)*Math.pow(10,-4);
        double phi1 = (3600*Math.toDegrees(lat) - 169028.66)*Math.pow(10,-4);

        return 2600072.37 + 211455.93*lambda1 -
                10938.51*lambda1*phi1
                - 0.36*lambda1*Math.pow(phi1, 2)
                - 44.54*Math.pow(lambda1, 3);


    }
    /**
     * Calcule qui retourne la coordonnée N du point de longitude lon et latitude lat dans le système WGS84
     * @param lon longitude en radians du point
     * @param lat latitude en radians du point
     * @return la coordonnée N du point de longitude lon et latitude lat dans le système WGS84
     */

    public static double n(double lon, double lat){
        double lambda1 = (3600*Math.toDegrees(lon) -26782.5)*Math.pow(10,-4);
        double phi1 = (3600*Math.toDegrees(lat) - 169028.66)*Math.pow(10,-4);

        return 1200147.07 + 308807.95 * phi1 +
                3745.25*Math.pow(lambda1, 2)
                + 76.63 * Math.pow(phi1, 2)
                - 194.56 * Math.pow(lambda1, 2)*phi1 +
                119.79*Math.pow(phi1, 3);
    }

    /**
     * Calcule qui retourne la longitude en radians du point de coordonnées E et N dans le système suisse
     * @param e coordonnée E du point
     * @param n coordonnée N du point
     * @return la longitude en radians du point de coordonnée e et n dans le system suisse
     */
    public static double lon(double e, double n){
        double x = (e - 2600000)*Math.pow(10, -6);
        double y = (n - 1200000)*Math.pow(10,-6);

        return Math.toRadians(100/36.0 * (2.6779094
                + 4.728982*x + 0.791484*x*y +
                0.1306*x*Math.pow(y, 2) -
                0.0436*Math.pow(x, 3)));
    }

    /**
     * Calcule qui retourne la latitude en radians du point de coordonnées E et N dans le système suisse
     * @param e coordonnée E du point
     * @param n coordonnée N du point
     * @return la latitude en radians du point au point de coordonnée e et n dans le système suisse
     */
    public static double lat(double e, double n){
        double x = (e - 2600000)*Math.pow(10, -6);
        double y = (n - 1200000)*Math.pow(10,-6);

        return Math.toRadians(100/36.0 * (16.9023892 +
                3.238272*y - 0.270978*Math.pow(x,2) -
                0.002528 * Math.pow(y,2) - 0.0447 *
                Math.pow(x,2)*y - 0.0140*Math.pow(y,3)));

    }
}
