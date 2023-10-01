package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Preconditions;

import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

/**
 * Class ElevationProfile
 *
 * @author Pedro Gouveia (345768)
 * @author Idriss Mimet (324424)
 */
public final class ElevationProfile {
    private final DoubleSummaryStatistics ss;
    private final double length;
    private final float[] elevationSamples;
    private final DoubleUnaryOperator profile;
    private final double totalAscent;
    private final double totalDescent;

    /**
     * Constructeur publique
     * @param length           longueur de l'itinéraire
     * @param elevationSamples tableau contenant les échantillons d'altitude
     */
    public ElevationProfile(double length, float[] elevationSamples) {
        Preconditions.checkArgument(elevationSamples.length >= 2 && length > 0);
        ss = new DoubleSummaryStatistics();
        this.length = length;
        this.elevationSamples = new float[elevationSamples.length];
        System.arraycopy(elevationSamples, 0, this.elevationSamples, 0, elevationSamples.length);
        for(float sample: elevationSamples){
            ss.accept(sample);
        }
        double maxHeight = maxElevation();
        double minHeight = minElevation();
        totalAscent = calcTotalAscent();
        totalDescent = calcTotalDescent();
        profile = Functions.sampled(elevationSamples, length);

    }

    /**
     * Retourne la longueur de l'itinéraire
     * @return la longueur de l'itinéraire
     */
    public double length() {
        return this.length;
    }


    /**
     * Obtient l'altitude minimale du profil
     * @return l'altitude minimale du profil
     */
    public double minElevation() {
        return ss.getMin();
    }

    /**
     * Obtient l'altitude maximale du profil
     * @return l'altitude maximale du profil
     */
    public double maxElevation() {
        return ss.getMax();
    }

    /**
     * Calcule le dénivelé positif total
     * @return le dénivelé positif total
     */
    public double totalAscent() {
        return totalAscent;
    }

    private double calcTotalAscent(){
        double countPlus = 0;
        for (int i = 0; i < elevationSamples.length - 1; i++) {
            if (elevationSamples[i + 1] - elevationSamples[i] > 0) {
                countPlus += elevationSamples[i + 1] - elevationSamples[i];
            }
        }
        return countPlus;
    }

    /**
     * Calcule le dénivelé négatif total
     * @return le dénivelé négatif total
     */
    public double totalDescent() {
        return totalDescent;
    }

    private double calcTotalDescent(){
        double countMoins = 0;
        for (int i = 0; i < elevationSamples.length - 1; i++) {
            if (elevationSamples[i + 1] - elevationSamples[i] < 0) {
                countMoins += elevationSamples[i + 1] - elevationSamples[i];
            }
        }
        return Math.abs(countMoins);
    }

    /**
     * Calcule l'altitude du profil à la position donnée
     * @param position position de laquelle on veut calculer l'altitude
     * @return l'altitude du profile à la position donnée
     */
    public double elevationAt(double position) {
        return profile.applyAsDouble(position);
    }
}
