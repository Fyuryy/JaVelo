package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.awt.*;
import java.lang.annotation.Inherited;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class MultiRoute
 *
 * @author Pedro Gouveia (345768)
 * @author Idriss Mimet (324424)
 */

public final class MultiRoute implements Route{
    private final List<Route> segments;

    /**
     * Constructeur de Multi Route
     * Retourne une instance de SingleRoute
     * @param segments liste de segment appartenant à une multiroute
     */
    public MultiRoute(List<Route> segments){
        Preconditions.checkArgument(segments.size()>0);
        this.segments = List.copyOf(segments);
    }

    /**
     * Retourne l'index du segment de l'itinéraire contenant la position donnée
     * @param position position du segment à retourner
     * @return l'index du segement de l'itinéraire contenant la position donnée
     */
    @Override
    public int indexOfSegmentAt(double position) {
        position = Math2.clamp(0, position, length());
        int index = 0;
        for(Route s: segments) {
            if (s.length() < position) {
                position = position - s.length();
                index += s.indexOfSegmentAt(s.length())+1;
            } else {
                index += s.indexOfSegmentAt(position);
                break;
            }
        }
        return index;
    }

    /**
     * Retourne la longueur de l'itinéraire en mètres
     * @return la longueur de l'itinéraire en mètres
     */
    @Override
    public double length() {
        double length = 0.0;
        for (Route segment : segments) {
            length += segment.length();
        }
        return length;
    }

    /**
     * Returne l'attribut edges, qui est la liste d'edges faisant partie de l'itinéraire
     * @return liste d'edges appartenant à l'itinéraire
     */
    @Override
    public List<Edge> edges() {
        List<Edge> liste = new ArrayList<>();
        for (Route segment : segments) {
            liste.addAll(segment.edges());
        }
        return Collections.unmodifiableList(liste);
    }

    /**
     * Retourne la totalité des points situés aux extrémités des arêtes de l'itinéraire
     * @return liste des points aux extrémités des arêtes de l'itinéraire
     */
    @Override
    public List<PointCh> points() {
         List<PointCh> points = new ArrayList<>();
         points.add(segments.get(0).points().get(0));
         for(Route segment: segments){
            int i = 0;
            for (PointCh point: segment.points()) {
            if(i!=0){
                points.add(point);
            }
            i++;}

         }
         return Collections.unmodifiableList(points);
    }

    /**
     * Retourne le point se trouvant à la distance donnée sur l'itinéraire
     * @param position position du point que l'on veut retourner
     * @return PointCh se trouvant à la distance donnée sur l'itinéraire
     */
    @Override
    public PointCh pointAt(double position) {
        position = Math2.clamp(0, position, length());
        IndexPosition ip = indexNPos(position);
        return segments.get(ip.index).pointAt(ip.position);
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
        IndexPosition ip = indexNPos(position);
        return segments.get(ip.index()).elevationAt(ip.position());
    }

    /**
     * Retourne l'identité du noeud appartenant à l'itinéraire et se trouvant le plus proche de la position donnée
     * @param position position de laquelle on veut retourner le noeud le plus proche
     * @return l'id du noeud de l'itinéraire se trouvant le plus proche de la position donnée
     */
    @Override
    public int nodeClosestTo(double position) {
        position = Math2.clamp(0, position, length());
        IndexPosition ip = indexNPos(position);
        return segments.get(ip.index()).nodeClosestTo(ip.position());
    }

    /**
     * Retourne le point de l'itinéraire se trouvant le plus proche du point de référence donné
     * @param point point de référence donné
     * @return le point de l'itinéraire se trouvant le plus proche du point de référence donné
     */
    @Override
        public RoutePoint pointClosestTo(PointCh point) {
        double addedDistance= 0;
        RoutePoint actualPoint = RoutePoint.NONE;
        for(Route s: segments) {
            actualPoint =  actualPoint.min(s.pointClosestTo(point).
                    withPositionShiftedBy(addedDistance));
            addedDistance += s.length();
        }
        return actualPoint;
    }


    /**
     * Méthode permettant de retourner l'index voulu à la position donnée
     * @param position position sur laquelle on est sur l'itinéraire
     * @return index et position
     */
    private IndexPosition indexNPos(double position) {
        int index = 0;
        for(Route s : segments){
            if(s.length() < position){
                position -= s.length();
                index++;
            }else{
                break;
            }
        }
        return new IndexPosition(position, index);
    }

    private record IndexPosition(double position, int index){}

}
