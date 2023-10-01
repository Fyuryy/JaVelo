package ch.epfl.javelo;
/**
 * Class Bits
 *
 * @author Pedro Gouveia (345768)
 * @author Idriss Mimet (324424)
 */
public final class Bits {
    
    /**
     * Constructeur privé (classe non-instantiable)
     */
    private Bits(){}

    /**
     * Extrait du vecteur de 32 bits value, la plage de length bits commencant au bit d'index start (interpreté
     * comme une valeur signée en complément à deux)
     * @param value vecteur de 32 bits
     * @param start position du premier bit qu'on va extraire
     * @param length taille de la plage de bits à extraire
     * @throws IllegalArgumentException si la plage est invalide
     * @return la plage de bits extraite
     */
    public static int extractSigned(int value, int start, int length){
        Preconditions.checkArgument((0<=start && start < Integer.SIZE ) &&
                (0<=length && length<=Integer.SIZE) && (start+length<=Integer.SIZE));
        value <<= Integer.SIZE - (start+length);
        value >>= Integer.SIZE - length;
        return value;
    }
    /**
     * Extrait du vecteur de 32 bits value, la plage de length bits commencant au bit d'index start (interpreté
     * de manière non signée)
     * @param value vecteur de 32 bits
     * @param start position du premier bit qu'on va extraire
     * @param length taille de la plage de bits à extraire
     * @throws IllegalArgumentException si la plage est invalide
     * @return la plage de bits extraite
     */
    public static int extractUnsigned(int value, int start, int length){
        Preconditions.checkArgument((0<=start && start<Integer.SIZE) &&
                (0<length && length<Integer.SIZE) && (start+length<=Integer.SIZE));
        value <<= Integer.SIZE - (start+length);
        value >>>= Integer.SIZE - length;
        return value;
    }
}
