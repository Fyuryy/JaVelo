package ch.epfl.javelo;

    /**
    * Classe preconditions
    *
    * @author Pedro Gouveia (345768)
    * @author Idriss Mimet (324424)
    */
public final class Preconditions {
    /**
     * Constructeur privé (classe non-instantiable)
     */
    private Preconditions(){}

    /**
     * Vérifie les arguments d'autres méthodes dans le projet
     * @param shouldBeTrue: condition à vérifier
     * @throws IllegalArgumentException si la condition à vérifier est fausse
     *
     */
     public static void checkArgument(boolean shouldBeTrue){
        if(!shouldBeTrue){
            throw new IllegalArgumentException();
        }
   }
}
