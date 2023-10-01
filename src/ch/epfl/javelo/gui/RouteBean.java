package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.*;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;
import java.util.*;


/**
 * CLasse RouteBean
 *
 * @author Pedro Gouveia (345768)
 * @author Idriss Mimet (324424)
 */
public final class RouteBean {
    private final ObservableList<Waypoint> waypoints;
    private final ObjectProperty<Route> route;
    private final DoubleProperty highlightedPosition;
    private final ObjectProperty<ElevationProfile> elevationProfile;
    private final LinkedHashMap<Pair<Integer, Integer>, Route > memory;
    private final RouteComputer routeComputer;
    /**
     * Longueur maximale d'un step
     */
    private final static int MAX_STEP_LENGTH = 5;
    /**
     * Capacité maximale du cacheDisque
     */
    private static final int MAX_CAPACITY = 100;


    /**
     * Constructeur public
     * @param routeComputer calculateur d'itinéraire
     */
    public RouteBean(RouteComputer routeComputer){
        this.routeComputer = routeComputer;
        memory = new LinkedHashMap<>(MAX_CAPACITY,0.75f, true);
        highlightedPosition = new SimpleDoubleProperty(Double.NaN);
        route = new SimpleObjectProperty<>();
        elevationProfile = new SimpleObjectProperty<>();
        waypoints = FXCollections.observableArrayList();
        waypoints.addListener((Observable o) ->{
            findRoute();
            computeElevation();
        } );

    }

    /**
     * Méthode retournant les waypoints
     * @return liste observable de waypoints
     */
    public ObservableList<Waypoint> getWaypoints() {
        return waypoints;
    }

    /**
     * Méthode permettant de modifier l'itinéraire
     * @param route que l'on veut ajouter à l'itinéraire
     */
    public void setRoute(Route route){
        this.route.setValue(route);
    }

    /**
     * Méthode retournant la position mise en évidence
     * @return la position mise en évidence
     */
    public double getHighlightedPosition(){

        return highlightedPosition.get();
    }

    public DoubleProperty HighlightedPosition(){
        return highlightedPosition;
    }

    /**
     * Méthode permettant la modification de la position mise en évidence
     * @param val nouvelle position à mettre en évidence
     */
    public void setHighlightedPosition(double val){
        highlightedPosition.set(val);
    }

    /**
     * Méthode permettant d'accéder à la propriété contenant l'élévation
     * @return l'élevation
     */
    public ReadOnlyObjectProperty<ElevationProfile> elevationProfile() {
        return elevationProfile;
    }

    /**
     * Méthode permettant d'accéder à la propriété contenant la route
     * @return l'itinéraire
     */
    public ReadOnlyObjectProperty<Route> route(){
        return route;
    }

    /**
     * Méthode auxiliaire permettant de trouver le meilleur itinéraire entre deux waypoints
     */
    private void findRoute(){
        List<Route> segments = new ArrayList<>();
        boolean nul = waypoints.size() < 2;
        if(!nul) {
            for (int i = 0; i < waypoints.size() - 1; i++) {
                int startNodeId = waypoints.get(i).closestNodeId();
                int endNodeId = waypoints.get(i + 1).closestNodeId();
                Pair<Integer, Integer> key = new Pair<>(startNodeId, endNodeId);
                if (!memory.containsKey(key)) {
                    createRoute(startNodeId, endNodeId);
                }
                Route route = memory.get(key);
                if (route == null) {
                    nul = true;
                    break;
                }
                segments.add(route);
            }
        }
        if(nul){
            setRoute(null);
        }else {
            setRoute(new MultiRoute(segments));
        }

    }

    /**
     * Méthode permettant de créer le meilleur itinéraire entre deux noeuds
     * @param startNodeId id du premier noeud
     * @param endNodeId id du dernier noeud
     */
    private void createRoute(int startNodeId, int endNodeId){
        Route newRoute =  routeComputer.bestRouteBetween(startNodeId,endNodeId);
        if(memory.size()==MAX_CAPACITY){
            Iterator<Pair<Integer, Integer>> it = memory.keySet().iterator();
            memory.remove(it.next());
        }
        memory.put(new Pair<>(startNodeId,endNodeId), newRoute);

    }

    /**
     * Méthode auxiliaire permettant de calculer l'élévation d'un itinéraire
     */
    private void computeElevation(){
        if(route.getValue()!= null){
            elevationProfile.setValue(ElevationProfileComputer.elevationProfile(
                    route().getValue(), MAX_STEP_LENGTH));
        }else{
            elevationProfile.setValue(null);
        }
    }

    /**
     * Méthode permettant de calculer l'index de la route on est, à la position donnée
     * @param position position à laquelle on veut calculer l'index
     * @return index
     */
    public int indexOfNonEmptySegmentAt(double position) {
        int index = route().get().indexOfSegmentAt(position);
        for (int i = 0; i <= index; i += 1) {
            int n1 = waypoints.get(i).closestNodeId();
            int n2 = waypoints.get(i + 1).closestNodeId();
            if (n1 == n2) index += 1;
        }
        return index;
    }

}
