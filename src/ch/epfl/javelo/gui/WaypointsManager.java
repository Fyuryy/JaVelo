package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;
import java.util.function.Consumer;

/**
 * Classe WaypointsManager
 *
 * @author Pedro Gouveia (345768)
 * @author Idriss Mimet (324424)
 */
public final class WaypointsManager {

    private final Graph graph;
    private final ObjectProperty<MapViewParameters> prop;
    private final ObservableList<Waypoint> waypointsList;
    private final Consumer<String> consumer;
    private final Pane pane;
    private final SimpleObjectProperty<Point2D> oldPoint;

    /**
     * Constructeur public
     * @param graph graph contenu dans l'instance de WaypointsManager à créer
     * @param prop propriété de MapViewParameters representant la carte qui s'affiche à l'écran
     * @param waypointsList liste de waypoints contenue dans l'instance de WaypointsManager à créer
     * @param consumer Object de type consumer qui sert à gérer les erreurs
     */
    public WaypointsManager(Graph graph, ObjectProperty<MapViewParameters> prop,
                            ObservableList<Waypoint> waypointsList, Consumer<String> consumer){

        this.graph = graph;
        this.prop = prop;
        this.waypointsList = waypointsList;
        this.consumer = consumer;
        Canvas canvas = new Canvas();
        this.pane = new Pane(canvas);
        this.oldPoint = new SimpleObjectProperty<>();
        pane.setPickOnBounds(false);
        group();

        prop.addListener((Observable o)-> {
            group();
        });


        waypointsList.addListener((Observable o) ->{
            group();
        });

    }

    /**
     * Méthode auxiliaire permettant d'appeler la méthode createGroups en boucle
     */
    private void group(){
        pane.getChildren().clear();
        for (int i = 0; i < waypointsList.size(); i++) {
            createGroups(i);
        }
    }

    /**
     * Méthode auxiliaire qui permet de créer les waypoints
     * @param i index auxiliair qui permet de gérer les positions des waypoints dans la liste de
     * ces derniers
     */
    private void createGroups(int i){

        SVGPath outside = new SVGPath();
        outside.setContent("M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20");
        outside.getStyleClass().add("pin_outside");

        SVGPath inside = new SVGPath();
        inside.setContent("M0-23A1 1 0 000-29 1 1 0 000-23");
        inside.getStyleClass().add("pin_inside");

        Group group = new Group(outside, inside);
        group.getStyleClass().add("pin");

        if(i==0){
            group.getStyleClass().add("first" );
        }else if(i==waypointsList.size()-1){
            group.getStyleClass().add("last");
        }else{
            group.getStyleClass().add("middle" );
        }
        PointWebMercator point = PointWebMercator.ofPointCh(waypointsList.get(i).point());
        group.setLayoutX(prop.get().viewX(point));
        group.setLayoutY(prop.get().viewY(point));
        pane.getChildren().add(group);

        pointUpdate(group);

        waypointDrag(group);

        endDragHandler(group,i);



    }

    /**
     * Méthode qui retourne le Pane
     * @return le pane
     */
    public Pane pane(){
        return pane;
    }


    /**
     * Méthode permettant d'ajouter un waypoint sur l'itinéraire
     * @param x coordonnée x du waypoint
     * @param y coordonnée y du waypoint
     */
    public void addWaypoint(double x, double y) {
        NodeNPoint pointNnode = findClosestNode(x,y);
        if (pointNnode.error()) {
            consumer.accept("Aucune route à proximité");
        } else {
            Waypoint waypoint = new Waypoint(pointNnode.point(),
                    pointNnode.closestNode());
            waypointsList.add(waypoint);
            //a revoir

        }
    }


    /**
     * Méthode qui gère les événements javaFX pour l'ajout de nouveaux Waypoints
     * @param group waypoints à ajouter
     * @param i index auxiliaire
     */
    private void endDragHandler(Group group, int i){
        group.setOnMouseReleased(e -> {
            if(e.isStillSincePress()){
                waypointsList.remove(i);
                pane.getChildren().remove(group);
            }else{
                double newX = group.getLayoutX() - oldPoint.get().getX() + e.getX();
                double newY = group.getLayoutY() - oldPoint.get().getY() + e.getY();
                NodeNPoint nodeNPoint = findClosestNode(newX,newY);

                if (nodeNPoint.error()){
                    consumer.accept("Aucune route à proximité");
                    PointWebMercator point = PointWebMercator.ofPointCh(waypointsList.get(i).point());
                    group.setLayoutX(prop.get().viewX(point));
                    group.setLayoutY(prop.get().viewY(point));

                } else{
                    waypointsList.set(i, new Waypoint(nodeNPoint.point() , nodeNPoint.closestNode()));
                }
            }
        });
    }

    /**
     * Méthode privée qui gère le glissement des waypoints dans la carte
     * @param group waypoint à glisser
     */
    private void waypointDrag(Group group){
        group.setOnMouseDragged(e ->{
            double newX = group.getLayoutX() - oldPoint.get().getX() + e.getX();
            double newY = group.getLayoutY() - oldPoint.get().getY() + e.getY();
            group.setLayoutX(newX);
            group.setLayoutY(newY);


        });
    }


    /**
     * Méthode auxiliaire permettant d'enregistrer l'ancienne position de la souris
     * @param group paramètre auxiliaire à partir du quel on peut gérer les événements javaFX
     */
    private void pointUpdate(Group group){
        group.setOnMousePressed(e ->{
            oldPoint.set(new Point2D(e.getX(),e.getY()));
        });
    }

    /**
     * Méthode auxiliaire permettant de trouver le node le plus proche d'un point
     * @param x coordonnée x du point
     * @param y coordonnée y du point
     * @return instande ce NodeNpoint
     */
    private NodeNPoint findClosestNode(double x, double y){
        PointCh point = prop.get().pointAt(x, y).toPointCh();
        if(point==null){
            return new NodeNPoint(null,0,true);
        }
        int closestNode = graph.nodeClosestTo(point, 500);
        return new NodeNPoint(point, closestNode, (closestNode==-1));
    }

    /**
     * Enregistrement auxiliaire permettant le retour de ses instances dans certaines méthodes
     */
    private record NodeNPoint(PointCh point, int closestNode, Boolean error){ }




}
