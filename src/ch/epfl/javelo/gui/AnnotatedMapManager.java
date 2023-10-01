package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.RoutePoint;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Class AnnotatedMapManager
 *
 * @author Pedro Gouveia (345768)
 * @author Idriss Mimet (324424)
 */

public final class AnnotatedMapManager {

    private final ObjectProperty<MapViewParameters> prop;
    private final StackPane stackPane;
    private final ObjectProperty<Point2D> mousePosition;
    private final DoubleProperty position;
    /**
     * Distance minimale pour afficher le disque representant la position mise en évidence
     */
    private final static int CLOSEST_DISTANCE_TO_MOUSE = 15;

    /**
     * Niveau de zoom initial
     */
    private final static int START_ZOOM_LEVEL = 12;

    /**
     * Coordonnée X initiale
     */
    private final static int START_X = 543200;

    /**
     * Coordonnée Y initiale
     */
    private final static int START_Y = 370650;


    /**
     * Constructeur publique
     *
     * @param graph    réseau routier
     * @param handler  Handler permettant de télécharger les tuiles
     * @param bean     bean de la route
     * @param consumer consumer permettant de gérer les erreurs
     */
    public AnnotatedMapManager(Graph graph,
                               TileManager handler,
                               RouteBean bean,
                               Consumer<String> consumer) {

        prop = new SimpleObjectProperty<>();
        prop.set(new MapViewParameters(START_ZOOM_LEVEL, START_X, START_Y));
        ObservableList<Waypoint> waypointsList = bean.getWaypoints();

        WaypointsManager waypointsManager =
                new WaypointsManager(graph, prop, waypointsList, consumer);

        BaseMapManager baseMapManager =
                new BaseMapManager(handler, waypointsManager, prop);

        RouteManager routeManager = new RouteManager(bean, prop);

        stackPane = new StackPane(baseMapManager.pane(),
                routeManager.pane(),
                waypointsManager.pane());

        stackPane.getStylesheets().add("map.css");
        position = new SimpleDoubleProperty(Double.NaN);


        stackPane.setOnMouseExited(e -> {
            position.set(Double.NaN);

        });
        mousePosition = new SimpleObjectProperty<>();


        stackPane.setOnMouseMoved(e -> {

            mousePosition.set(new Point2D(e.getX(), e.getY()));
            try {
                PointCh distanceWeb = prop.get().
                        pointAt(e.getX() + CLOSEST_DISTANCE_TO_MOUSE,
                                e.getY()).toPointCh();

                double distance = Objects.requireNonNull(
                        prop.get().pointAt(e.getX(),
                                e.getY()).toPointCh()).distanceTo(distanceWeb);

                PointWebMercator ptWeb = prop.get().
                        pointAt(e.getX(), e.getY());

                PointCh ptCh = ptWeb.toPointCh();

                if (bean.route().get() != null) {
                    RoutePoint rtPoint = bean.route().get().pointClosestTo(ptCh);

                    if (rtPoint.distanceToReference() <= distance) {
                        position.set(rtPoint.position());
                    } else {
                        position.set(Double.NaN);
                    }
                }
            } catch (NullPointerException ignored) {

            }
        });
    }

    /**
     * Méthode permettant l'accès au fond
     *
     * @return stackPane
     */
    public Pane pane() {
        return stackPane;
    }

    /**
     * Méthode permettant l'accès à la propriété qui contient la position de la souris sur l'itinéraire
     *
     * @return position
     */
    public DoubleProperty mousePositionOnRouteProperty() {
        return position;
    }
}


