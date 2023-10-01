package ch.epfl.javelo;
import java.util.function.DoubleUnaryOperator;

/**
 * Enregistrement PoitnWebMercator
 *
 * @author Pedro Gouveia (345768)
 * @author Idriss Mimet (324424)
 */
public final class Functions {
    /**
     * Constructeur privé (classe non-instantiable)
     */
    private Functions() {
    }

    /**
     * Retourne une fonction constante dont la valeur est toujours y
     *
     * @param y valeur donnée pour la fonction constante
     * @return la valeur y
     */
    public static DoubleUnaryOperator constant(double y) {
        return new Constant(y);
    }

    /**
     * Retourne une fonction obtenue par interpolation linéaire entre les échantillons samples
     *
     * @param samples tableau contenant les samples
     * @param xMax    valeur maximale que peut retourner la fonction
     * @return fonction obtenue par interpolation linéaire entre les échantillons samples
     * @throws IllegalArgumentException si le tableau samples contient moins de deux éléments ou si
     * xMax est inférieur ou égal à zéro
     */
    public static DoubleUnaryOperator sampled(float[] samples, double xMax) {
        Preconditions.checkArgument(samples.length >= 2 && xMax > 0);
        return new Sampled(samples, xMax);
    }

    /**
     * Classe imbriquée Constant
     */
    private  final record Constant (double y) implements DoubleUnaryOperator {
        public Constant(double y) {
            this.y = y;
        }

        /**
         * Méthode qui retourne une fonction constante dont la valeur vaut y
         * @param y valeur constante à retourner par la fonction
         * return la fonction constante qui retourne y
         */
        @Override
        public double applyAsDouble(double y) {
            return this.y;
        }
    }

    /**
     * Classe imbriquée Sampled
     */
    private static final class Sampled implements DoubleUnaryOperator {
        private final float[] samples;
        private final double xMax;

        public Sampled(float[] samples, double xMax) {
            int length = samples.length;
            this.samples = new float[length];
            System.arraycopy(samples, 0, this.samples, 0, length);
            this.xMax = xMax;
        }

        /**
         * Méthode qui retourne une fonction obtenue par interpolation linéaire entre
         * les échantillons samples, espacés régulièrement et couvrant la plage allant de 0 à xMax
         * @param x coordonnée x dont on veut calculer le y
         * return fonction qui calcule par interpolation le y d'abscisse x
         */
        @Override
        public double applyAsDouble(double x) {

            double interval = xMax / (samples.length - 1);
            if (x < 0) {
                return samples[0];
            }
            if (x >= xMax) {
                return samples[samples.length-1];
            }
            int i = (int) Math.floor(x / interval);
            return Math2.interpolate(samples[i], samples[i + 1], (x/interval)-i);
        }
    }
}

