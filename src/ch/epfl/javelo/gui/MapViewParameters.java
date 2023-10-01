package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Point2D;

/**
 * Enregistrement MapViewParameters
 *
 * @author Pedro Gouveia (345768)
 * @author Idriss Mimet (324424)
 */
public record MapViewParameters(int zoom, double x, double y) {


    /**
     * Méthode retournant un Point2D representant le coin haug gauche de la carte
     * @return coin haut gauche de la carte
     */
    public Point2D topLeft(){
        PointWebMercator p = PointWebMercator.of(zoom, x, y);
        return new Point2D(Math.toDegrees(p.lon()), Math.toDegrees(p.lat()));
    }

    /**
     * Méthode retournant une instance de MapViewParameters avec des nouvelles coordonnées, mais
     * le même niveau de zoom
     * @param newX nouvelle coordonnée x
     * @param newY nouvelle coordonnée y
     * @return
     */
    public MapViewParameters withMinXY(double newX, double newY){
        return new MapViewParameters(this.zoom, newX, newY);
    }

    /**
     * Méthode retournant une instance de PointWebMercator en prenant les coordonnées x et y de
     * MapViewParameters et en leur ajoutant les coordonnées en paramètre
     * @param x coordonnée x à ajouter
     * @param y coordonnée y à ajouter
     * @return PointWebMercator avec les coordonnées (x + this.x) et (y + this.y) et le même niveau
     * de zoom
     */
    public PointWebMercator pointAt(double x, double y){
        return PointWebMercator.of(zoom, x+this.x, y+this.y);

    }

    /**
     * Méthode qui retourne la coordonnée x du point p, exprimée par rapport au coin haut-gauche de la portion
     * de carte affichée à l'écran.
     * @param p point duquel on retourne la coordonnée x
     * @return nouvelle coordonnée x
     */
    public double viewX(PointWebMercator p){
        return p.xAtZoomLevel(zoom) - this.x;

    }
    /**
     * Méthode qui retourne la coordonnée y du point p, exprimée par rapport au coin haut-gauche de la portion
     * de carte affichée à l'écran.
     * @param p point duquel on retourne la coordonnée y
     * @return nouvelle coordonnée y
     */
    public double viewY(PointWebMercator p){
        return p.yAtZoomLevel(zoom) - this.y;

    }

}
