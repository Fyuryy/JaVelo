package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Classe SingleRoute
 *
 * @author Pedro Gouveia (345768)
 * @author Idriss Mimet (324424)
 */

import static java.util.Arrays.binarySearch;
/**
 * Classe SingleRoute
 *
 * @author Pedro Gouveia (345768)
 * @author Idriss Mimet (324424)
 */
public final class SingleRoute implements Route{
    private final List<Edge> edges;
    private final double[] positionList;
    private final List<PointCh> pointList;

    /**
     * Constructeur de Single Route
     * Retourne une instance de SingleRoute
     * @param edges liste d'edges appartenant à l'itinéraire SingleRoute
     */
    public SingleRoute(List<Edge> edges){
        Preconditions.checkArgument(edges.size()>0);
        this.edges = List.copyOf(edges);
        positionList = new double[edges.size()+1];
        for (int i = 1; i < positionList.length; i++) {
            positionList[i] = positionList[i-1]+edges.get(i-1).length();
        }
        pointList = Collections.unmodifiableList(pointListComputer());

    }

    /**
     * Retourne l'index du segment de l'itinéraire contenant la position donnée
     * @param position position du segment à retourner
     * @return l'index du segement de l'itinéraire contenant la position donnée
     */
    @Override
    public int indexOfSegmentAt(double position) {
        return 0;
    }


    /**
     * Retourne la longueur de l'itinéraire en mètres
     * @return la longueur de l'itinéraire en mètres
     */
    @Override
    public double length() {
        return positionList[positionList.length-1];
    }

    /**
     * Returne l'attribut edges, qui est la liste d'edges faisant partie de l'itinéraire
     * @return liste d'edges appartenant à l'itinéraire
     */
    @Override
    public List<Edge> edges() {
        return edges;
    }

    /**
     * Retourne la totalité des points situés aux extrémités des arêtes de l'itinéraire
     * @return liste des points aux extrémités des arêtes de l'itinéraire
     */
    @Override
    public List<PointCh> points() {
       return pointList;
    }

    /**
     * Méthode auxiliaire qui calcule la liste des points de l'itinéraire
     * @return la liste de points de l'itinéraire
     */
    private List<PointCh> pointListComputer(){
        List<PointCh> points = new ArrayList<>();
        for (Edge e: edges){
            points.add(e.fromPoint());
        }
        points.add(edges.get(edges().size()-1).toPoint());
        return points;
    }

    /**
     * Retourne le point se trouvant à la distance donnée sur l'itinéraire
     * @param position position du point que l'on veut retourner
     * @return PointCh se trouvant à la distance donnée sur l'itinéraire
     */
    @Override
    public PointCh pointAt(double position) {
        position = Math2.clamp(0, position, length());
        int binarySearch = binarySearch(positionList, position);

        if (binarySearch>=0) {
            if(binarySearch == edges.size()){
                return edges.get(binarySearch-1).toPoint();
            }
            return edges.get(binarySearch).fromPoint();
        }
        int index = negIndexToPos(binarySearch);
        return edges.get(index).pointAt(position-positionList[index]);
    }

    /**
     * Retourne l'altitude à la position donnée le long du profile, qui peut valoir NaN si l'arête contenant
     * cette position n'a pas de profile
     * @param position position à laquelle on va calculer l'altitude
     * @return l'altitude à la position donnée
     */
    @Override
    public double elevationAt(double position) {
        position = Math2.clamp(0, position, length());
        int binarySearch = binarySearch(positionList, position);
        if(binarySearch<0) {
            binarySearch = negIndexToPos(binarySearch);
        }
        if(binarySearch == edges.size()){
            return edges.get(binarySearch-1).elevationAt(edges.get(binarySearch-1).length());
        }
        return edges.get(binarySearch).elevationAt(position-positionList[binarySearch]);
    }

    /**
     * Retourne l'identité du noeud appartenant à l'itinéraire et se trouvant le plus proche de la position donnée
     * @param position position de laquelle on veut retourner le noeud le plus proche
     * @return l'id du noeud de l'itinéraire se trouvant le plus proche de la position donnée
     */
    @Override
    public int nodeClosestTo(double position) {
        position = Math2.clamp(0, position, length());
        int binarySearch = binarySearch(positionList,position);
        if(binarySearch==edges.size()){
            return edges.get(binarySearch-1).toNodeId();
        }
        if(binarySearch>=0){
            return edges.get(binarySearch).fromNodeId();
        }
        int index = negIndexToPos(binarySearch);
        return (position-positionList[index]<positionList[index+1]-position ?
                edges.get(index).fromNodeId():edges.get(index).toNodeId());
    }
    
    /**
     * Retourne le point de l'itinéraire se trouvant le plus proche du point de référence donné
     * @param point point de référence donné
     * @return le point de l'itinéraire se trouvant le plus proche du point de référence donné
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        int index = 0;
        RoutePoint  actualRoutePoint = RoutePoint.NONE;
        PointCh closestPointToRefPoint;
        for(Edge e : edges){
            double distanceClosest = Math2.clamp(0, e.positionClosestTo(point), e.length());
            closestPointToRefPoint = e.pointAt(distanceClosest);
            double distancePointToRef = closestPointToRefPoint.distanceTo(point);
            actualRoutePoint = actualRoutePoint.min(closestPointToRefPoint,
                    distanceClosest+positionList[index], distancePointToRef);
            index++;
        }
        return actualRoutePoint;
    }

    /** Methode permettant de calculer l'index positif correspondant à
     * la valeur negative de la recherche dichotomique
     * @param binarySearch valeur de la recherche dichotomique
     * @return index positif correspondant à la valeur de la recherche dichotomique
     */
    private int negIndexToPos(int binarySearch){
        return Math.abs(binarySearch)-2;
    }

}