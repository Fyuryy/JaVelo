package ch.epfl.javelo.routing;
import ch.epfl.javelo.projection.PointCh;
import java.util.List;


/**
 * Interface Route
 *
 * @author Pedro Gouveia (345768)
 * @author Idriss Mimet (324424)
 */

public interface Route {

 /**
  * Retourne l'index du segment à la position donnée
  * @param position position du segment à retourner
  * @return index du segment à la position donnée
  */
   int indexOfSegmentAt(double position);


 /**
  * Retourne la longueur de l'itinéraire
  * @return la longueur de l'itinéraire
  */
     double length();


 /**
  * Retourne une liste avec la totalité des arêtes de l'itinéraire
  * @return une liste contenant toutes les arêtes de l'itinéraire
  */
     List<Edge> edges();

 /**
  * Retourne la totalité des points situés aux extrémités des arêtes de l'itinéraire
  * @return une liste contenant les points situés aux extrémités des arêtes de l'itinéraire
  */
     List<PointCh> points();

 /**
  * Retourne le point se trouvant à la position donnée le long de l'itinéraire
  * @param position position du point que l'on veut retourner
  * @return PointCh se trouvant à la position donnée, sur l'itinéraire
  */
    PointCh pointAt(double position);

 /**
  * Retourne l'altitude à la position donnée le long de l'itinéraire
  * @param position position à laquelle on va calculer l'altitude
  * @return l'altitude à la position donnée
  */
     double elevationAt(double position);

 /**
  * Retourne l'identité du nœud appartenant à l'itinéraire et se trouvant le plus proche de la position donnée
  * @param position position de laquelle on veut retourner le noeud le plus proche
  * @return l'identité du noeud le plus proche de la position donnée, et appartenant à l'itinéraire
  */
     int nodeClosestTo(double position);

 /**
  * Retourne le point de l'itinéraire se trouvant le plus proche du point de référence donné
  * @param point point de référence donné
  * @return PointCh se trouvant sur l'itinéraire, le plus proche du point de référence
  */
   RoutePoint pointClosestTo(PointCh point);

}



