package ch.epfl.javelo.gui;


import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import java.util.ArrayList;
import java.util.List;

/**
 * Class RouteManager
 *
 * @author Pedro Gouveia (345768)
 * @author Idriss Mimet (324424)

 */

public final class RouteManager {
    private final Polyline polyline;
    private final RouteBean bean;
    private final Circle circle;
    private final Pane pane;
    private final ReadOnlyObjectProperty<MapViewParameters> prop;
    /**
     * Rayon du disque
     */
    private final static int DISKRADIUS = 5;

    /**
     * Position du disque dans l'itinéraire
     */
    private int zoom;


    /**
     * Constructeur public
     *
     * @param bean bean de l'itinéraire
     * @param prop propriété contenant les paramètres de la carte affichée
     */
    public RouteManager(RouteBean bean, ReadOnlyObjectProperty<MapViewParameters> prop) {

        this.prop = prop;
        this.bean = bean;

        if (prop.get() != null) {
            this.zoom = prop.get().zoom();
        }

        Canvas canvas = new Canvas();
        pane = new Pane(canvas);
        pane.setPickOnBounds(false);
        polyline = new Polyline();
        polyline.setId("route");
        pane.getChildren().add(polyline);
        circle = new Circle(DISKRADIUS);
        circle.setId("highlight");
        circle.setVisible(false);
        pane.getChildren().add(circle);

        listeners();
        createWaypointFromCircle();

    }


    /**
     * Méthode permettant d'accéder au pane
     * @return le pane
     */
    public Pane pane(){
        return pane;
    }


    /**
     * Méthode auxiliaire permettant de créer la polyline
     * @return liste de points permettant de créer la polyline
     */
    private List<Double> createPolylineList() {
        List<Double> coordsList = new ArrayList<>();
        zoom = prop.get().zoom();
        if (bean.route().get() != null) {
            for (PointCh pt : bean.route().get().points()) {
                PointWebMercator point = PointWebMercator.ofPointCh(pt);
                double x = point.xAtZoomLevel(zoom);
                double y = point.yAtZoomLevel(zoom);
                coordsList.add(x);
                coordsList.add(y);
            }
        }
        return coordsList;
    }


    /**
     * Méthode permettant de placer la polyline
     */
    private void placePolyline(){
        polyline.getPoints().clear();
        polyline.getPoints().setAll(createPolylineList());
        polyline.setLayoutX(-prop.get().x());
        polyline.setLayoutY(-prop.get().y());


    }

    /**
     * Méthode auxiliaire permettant de placer le disque
     */
    private void placeDisk(){
        if(bean.route().get() != null && !Double.isNaN((bean.getHighlightedPosition()) )) {

            PointWebMercator point = PointWebMercator.
                    ofPointCh(bean.route().get().
                            pointAt(bean.getHighlightedPosition()));

            circle.setCenterX(prop.get().viewX(point));
            circle.setCenterY(prop.get().viewY(point));
            circle.setVisible(true);

        }else{
            circle.setVisible(false);
        }
    }

    private void listeners(){
        prop.addListener((s, oldS, newS) -> {
            circle.setVisible(false);
            if (bean.route().get() != null) {
                polyline.setLayoutX(-prop.get().x());
                polyline.setLayoutY(-prop.get().y());
                if (prop.get().zoom() != zoom) {
                    placePolyline();
                    placeDisk();
                }
            }
        });

        bean.route().addListener((e, old, news) -> {
            if (news != null) {
                placePolyline();
                placeDisk();
                polyline.setVisible(true);
            }else{
                polyline.setVisible(false);
                circle.setVisible(false);
            }
        });

        bean.HighlightedPosition().addListener((p, o, n)->{
            placeDisk();
        });

    }

    private void createWaypointFromCircle(){
        circle.setOnMouseClicked(e -> {
            Point2D coords = circle.localToParent(e.getX(), e.getY());
            PointCh point = prop.get().pointAt(
                            coords.getX(),
                            coords.getY()).
                    toPointCh();

            int closestNodeId = bean.route().get().
                    nodeClosestTo(bean.getHighlightedPosition());


            Waypoint waypoint = new Waypoint(point, closestNodeId);
            if (!bean.getWaypoints().contains(waypoint)) {
                bean.getWaypoints().add(bean.indexOfNonEmptySegmentAt(
                        bean.getHighlightedPosition()) + 1, waypoint);
            }
        });
    }



}


