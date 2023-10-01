package ch.epfl.javelo;

    /**
     * Class Math2
     *
     * @author Pedro Gouveia (345768)
     * @author Idriss Mimet (324424)
     */
public final class Math2 {
    /**
     * Constructeur privé (classe non-instantiable)
     */
    private Math2(){}
         /**
         * Retourne la partie entière par excès de la division de deux entiers
         * @param x entier
         * @param y entier
         * @throws IllegalArgumentException quand x est négatif ou quand y est négatif ou nul
         * @return la partie entière par excès de la division de x par y
         */
    public static int ceilDiv(int x, int y){
        Preconditions.checkArgument((x>=0 && y>0));
        return (x+y-1)/y;
    }
        /**
         * Calcule la coordonnée y du point se trouvant sur la droite passant par (0, y0) et (1, y1) et de coordonnée
         * x donnee
         * @param y0 coordonnée y correspondant au point d'abscisse 0
         * @param y1 coordonnée y correspondant au point d'abscisse 1
         * @param x point d'abscisse dont on recherche la coordonée y
         * @return coordonnée y correspondant à la coordonnée d'abscisse x
         */
    public static double interpolate(double y0, double y1, double x){
        return Math.fma(y1-y0,x,y0);
    }
        /**
         * Limite la valeur v entre la valeur min et la valeur max
         * @param min borne inférieure
         * @param max borne supérieure
         * @param v valeur à borner
         * @throws IllegalArgumentException quand le paramètre min est plus grand ou égal au paramètre max
         * @return la partie entière par excès de la division de x par y
         */
    public static int clamp(int min, int v, int max){
        Preconditions.checkArgument(max>=min);
        return Math.min(Math.max(v,min),max);
    }
        /**
         * Limite la valeur v entre la valeur min et la valeur max (surcharge de la méthode clamp antérieure)
         * @param min borne inférieure
         * @param max borne supérieure
         * @param v valeur à borner
         * @throws IllegalArgumentException quand le paramètre min est plus grand ou égal au paramètre max
         * @return la partie entière par excès de la division de x par y
         */
    public static double clamp(double min, double v, double max){
        Preconditions.checkArgument(max>=min);
        return Math.min(Math.max(v,min),max);
    }
        /**
         * Retourne le sinus hyperbolique inverse du paramètre donné
         * @param x paramètre donné
         * @return le sinus hyperbolique inverse de x
         */
    public static double asinh(double x){
        return Math.log(x+(Math.sqrt(1+ (x * x) )));
    }
        /**
         * Effectue le produit scalaire entre le vecteur u (de composante uX, uY) et le vecteur v (de composante vX, vY)
         * @param uX composante x du vecteur u
         * @param uY composante y du vecteur u
         * @param vX composante x du vecteur v
         * @param vY composante y du vecteur v
         * @return le produit scalaire entre le vecteur u et le vecteur v
         */
    public static double dotProduct(double uX, double uY, double vX, double vY){
        return (uX*vX + uY*vY);
    }
        /**
         * Calcule le carré de la norme du vecteur u passé en paramètre
         * @param uX composante x du vecteur u
         * @param uY composante y du vecteur u
         * @return le carré de la norme du vecteur u
         */
    public static double squaredNorm(double uX, double uY){
        return dotProduct(uX,uY,uX,uY);
    }
        /**
         * Calcule la norme du vecteur u passé en paramètre
         * @param uX composante x du vecteur u
         * @param uY composante y du vecteur u
         * @return la norme du vecteur u
         */
   public static double norm(double uX, double uY){
        return Math.sqrt(squaredNorm(uX, uY));
    }

        /**
         * Calcule la projection orthogonale du vecteur AP sur le vecteur AB
         * @param aX coordonnée X du point A
         * @param aY coordonnée Y du point A
         * @param bX coordonnée X du point B
         * @param bY coordonnée Y du point B
         * @param pX coordonnée X du point P
         * @param pY coordonnée Y du point P
         * @return la longueur de la projection orthogonale du vecteur AP sur le vecteur AB
         */
    public static double projectionLength(double aX, double aY, double bX, double bY, double pX, double pY){
        return (dotProduct(pX-aX,pY-aY,bX-aX,bY-aY)/norm(bX-aX,bY-aY));

    }






}

