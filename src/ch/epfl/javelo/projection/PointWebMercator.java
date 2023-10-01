package ch.epfl.javelo.projection;

import ch.epfl.javelo.Preconditions;

/**
 * Enregistrement PointWebMercator
 *
 * @author Pedro Gouveia (345768)
 * @author Idriss Mimet (324424)
 */

public record PointWebMercator(double x, double y) {

    /**
     * Constante d'exponentiation pour le zoom
     */
    private static final int ZOOM_EXPONENT = 8;


    /**
     * Constructeur compact
     * @param x coordonnée x du point
     * @param y coordonnée y du point
     * @throws IllegalArgumentException si les coordonnées x et y ne sont pas comprises dans l'intervalle [0,1]
     */
    public PointWebMercator{
        Preconditions.checkArgument(x>=0 && x<=1 && y>=0 && y<=1);
    }

    /**
     * Calcule le point dont les coordonnées sont x et y au niveau de zoom donné
     * @param zoomLevel niveau de zoom donné
     * @param x coordonnée x du nouveau point
     * @param y coordonnée y du nouveau point
     * @return le point dont les coordonnées sont x et y au niveau do zoom donné
     */
    public static PointWebMercator of(int zoomLevel, double x, double y){
        return new PointWebMercator(
                Math.scalb(x,-ZOOM_EXPONENT-zoomLevel),
                Math.scalb(y,-ZOOM_EXPONENT-zoomLevel));
    }

    /**
     * Convertis un point en coordonnées suisse en un point en coordonnées WebMercator
     * @param PointCh point en coordonnées suisses
     * @return point en coordonnées WebMercator correspondant au point en coordonnées suisses donné
     */
    public static PointWebMercator ofPointCh(PointCh PointCh){
        return new PointWebMercator(
                WebMercator.x(PointCh.lon()),
                WebMercator.y(PointCh.lat()));
    }

    /**
     * Calcule la coordonnée x d'un point au niveau de zoom donné
     * @param zoomLevel niveau de zoom donné
     * @return coordonné x d'un point au niveau de zoom donné
     */
    public double xAtZoomLevel(int zoomLevel){
        return Math.scalb(x, ZOOM_EXPONENT + zoomLevel);
    }

    /**
     * Calcule la coordonnée y d'un point au niveau de zoom donné
     * @param zoomLevel niveau de zoom donné
     * @return coordonné y d'un point au niveau de zoom donné
     */
    public double yAtZoomLevel(int zoomLevel){
        return Math.scalb(y, ZOOM_EXPONENT+zoomLevel);
    }

    /**
     * Calcule la longitude d'un point
     * @return la longitude d'un point
     */
    public double lon(){
        return WebMercator.lon(x);
    }
    /**
     * Calcule la latitude d'un point
     * @return la latitude d'un point
     */
    public double lat(){
        return WebMercator.lat(y);
    }

    /**
     * Calcule le point de coordonnées suisses se trouvant à la même position que le point (this)
     * @return un point en coordonnées suisses ou null si le point n'est pas dans les limites de la Suisse
     */
    public PointCh toPointCh(){
        double e = Ch1903.e(lon(), lat());
        double n = Ch1903.n(lon(), lat());
        if (SwissBounds.containsEN(e,n)){
            return new PointCh(e, n);
        }
        return null;
    }



}
