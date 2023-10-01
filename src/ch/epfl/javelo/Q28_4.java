package ch.epfl.javelo;



/**
        * Classe Q28_4
        *
        * @author Pedro Gouveia (345768)
        * @author Idriss Mimet (324424)
        */
public final class  Q28_4 {

    /**
     * constante utilisée pour convertir des valeurs dans le système Q28_4
     */
    private static final int CONVERTER = 4;

    /** Constructeur privé (classe non-instantiable)
     */
    private Q28_4() {}

    /** Convertis un entier donné dans le système Q28_4
     * @param i valeur à convertir
     * @return la valeur Q28_4 correspondant à l'entier donné
     */
    public static int ofInt(int i){
        return i<<CONVERTER;
    }
    /**
     * Convertis un entier donné dans le système Q28_4 de type double
     * @param q28_4 valeur à convertir
     * @return la valeur Q28_4 de type double correspondant à l'entier donné
            */
    public static double asDouble(int q28_4){
        return Math.scalb((double)q28_4,-CONVERTER);
    }
    /**
     * Convertis un entier donné dans le système Q28_4 de type float
     * @param q28_4 valeur à convertir
     * @return la valeur Q28_4 de type float correspondant à l'entier donné
     */
    public static float asFloat(int q28_4){
        return Math.scalb((float)q28_4,-CONVERTER);
    }


}
