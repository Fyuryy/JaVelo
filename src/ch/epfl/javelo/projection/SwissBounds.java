package ch.epfl.javelo.projection;
/**
 * Class SwissBounds
 *
 * @author Pedro Gouveia (345768)
 * @author Idriss Mimet (324424)
 */
public final class SwissBounds {

   /**
    * Attributs de la classe SwissBounds
    */

   /**
    * Coordonnée minimale E
    */
   public final static double MIN_E = 2485000;
   /**
    * Coordonnée maximale E
    */
   public final static double MAX_E = 2834000;
   /**
    * Coordonnée minimale N
    */
   public final static double MIN_N = 1075000;
   /**
    * Coordonnée maximale N
    */
   public final static double MAX_N = 1296000;
   /**
    * Largeur
    */
   public final static double WIDTH = MAX_E-MIN_E;
   /**
    * Hauteur
    */
   public final static double HEIGHT = MAX_N - MIN_N;

   /**
    * Constructeur privé
    */
   private SwissBounds(){}

   /**
    * Vérifie si la valeur e est comprise entre MIN_E et MAX_E et si la valeur n est comprise entre MIN_N et MAX_n
    * @param e coordonnée E
    * @param n coordonnée N
    * @return vrai si et seulement si les coordonnées E et N sont dans les limites de la Suisse
    */


   public static boolean containsEN(double e, double n){
      return (MIN_E <= e && e <= MAX_E) && (MIN_N <= n && n <= MAX_N);
   }
}
