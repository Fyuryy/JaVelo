package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Q28_4;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static ch.epfl.javelo.Bits.extractSigned;
import static java.lang.Short.toUnsignedInt;
/**
 * Enregistrement GraphEdges
 *
 * @author Pedro Gouveia (345768)
 * @author Idriss Mimet (324424)
 */
public record GraphEdges(ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations) {

    private static final int NUMBER_OF_BYTES = 10;
    /**
     * Offset requis pour atteindre la longueur d'une arête
     */
    private static final int OFFSET_LENGTH = 4;

    /**
     * Retourne vrai ssi l'arête d'identité donnée va dans le sens inverse de la voie OSM dont elle provient
     * @param edgeId identité de l'arête
     * @return vrai si l'arête idendité va dans le sens inverse de la voie OSM
     */
    public boolean isInverted(int edgeId) {
        return edgesBuffer.getInt(NUMBER_OF_BYTES * edgeId) < 0;
    }

    /**
     * Retourne l'identité du nœud destination de l'arête d'identité donnée
     * @param edgeId identité de l'arête
     * @return l'identité du noeud de destination de l'arête d'identité donnée
     */
    public int targetNodeId(int edgeId) {
        if (isInverted(edgeId)) {
            return ~edgesBuffer.getInt(NUMBER_OF_BYTES * edgeId);
        }
        return Bits.extractUnsigned(edgesBuffer.getInt(NUMBER_OF_BYTES * edgeId), 0, 31);

    }

    /**
     * Retourne la longueur, en mètres, de l'arête d'identité donnée
     * @param edgeId identité de l'arête
     * @return longueur en mètre de l'arête d'identité donnée
     */
    public double length(int edgeId) {
        return Q28_4.asDouble(toUnsignedInt(edgesBuffer.getShort(NUMBER_OF_BYTES * edgeId + Integer.BYTES)));
    }

    /**
     * Retourne le dénivelé positif, en mètres, de l'arête d'identité donnée
     * @param edgeId identité de l'arête
     * @return dénivelé positif, en mètres, de l'arête d'identité donnée
     */
    public double elevationGain(int edgeId) {
        return Q28_4.asDouble(toUnsignedInt(edgesBuffer.getShort(NUMBER_OF_BYTES * edgeId + Integer.BYTES + Short.BYTES)));
    }

    /**
     * Retourne vrai ssi l'arête d'identité donnée possède un profil
     * @param edgeId identité de l'arête
     * @return vrai ssi l'arête d'identité donnée possède un profil
     */
    public boolean hasProfile(int edgeId) {
        return Bits.extractUnsigned(profileIds.get(edgeId), 30, 2) != 0;

    }

    /**
     * Retourne le tableau des échantillons du profil de l'arête d'identité donnée
     * @param edgeId identité de l'arête
     * @return tableau des échantillons du profil de l'arête d'identité donnée (vide si l'arête ne possède pas de profil)
     */
    public float[] profileSamples(int edgeId) {
        if (hasProfile(edgeId)) {

            int profile = Bits.extractUnsigned(profileIds.get(edgeId), 30, 2);
            int lengthTab = 1 + Math2.ceilDiv(toUnsignedInt(edgesBuffer.getShort
                    (NUMBER_OF_BYTES * edgeId + Integer.BYTES)), Q28_4.ofInt(2));

            float[] samples = new float[lengthTab];
            int firstId = Bits.extractUnsigned(profileIds.get(edgeId), 0, 30);
            int nbDiv , upInt , bitLength = 0;


            if (profile == 1) {
               for (int i = 0; i < lengthTab; i++) {
                    samples[i] = Q28_4.asFloat(toUnsignedInt(elevations.get(i + firstId)));
                }
            } else {
                if (profile == 2) {
                    nbDiv = 2;
                    upInt = 1;
                    bitLength = 8;
                }else{
                    nbDiv = 4;
                    upInt = 3;
                    bitLength = 4;
                }
                samples[0] = Q28_4.asFloat(toUnsignedInt(elevations.get(firstId)));

                for(int i=1; i<lengthTab; i +=nbDiv) {
                    float fstExtract = Q28_4.asFloat(Bits.extractSigned(elevations.get(
                            (i + upInt) / nbDiv + firstId),
                            upInt * bitLength, bitLength));

                          samples[i] = samples[i - 1] + fstExtract;

                    for (int j = 1; j < nbDiv; j++) {
                        if (i + j < lengthTab) {
                            float extShort = Q28_4.asFloat(Bits.extractSigned(elevations.get(
                                    (i + upInt) / nbDiv + firstId),
                                    (upInt - j) * bitLength, bitLength));

                            samples[i + j] = samples[i + j - 1] + extShort;
                        }
                    }
                }
            }
            if (isInverted(edgeId)) {
                return invert(samples);
            }
            return samples;
            }
            return new float[0];
    }




    /**
     * Retourne un tableau de float contenant l'inverse du tableau donné
     * @param samples tableau que l'on souhaite inverser
     * @return tableau inverse du tableau donné
     */
    private float[] invert(float[] samples) {
        int length = samples.length;
        float[] inverted = new float[length];
        for (int i = 0; i < (length/2)+1; i++) {
            inverted[i] = samples[length - 1 - i];
            inverted[length-1-i] = samples[i];
        }
        return inverted;

        }
    /**
     * Retourne l'identité de l'ensemble d'attributs attaché à l'arête d'identité donnée.
     * @param edgeId identité de l'arête
     * @return l'identité de l'ensemble d'attributs attaché à l'arête d'identité donnée.
     */
    public int attributesIndex(int edgeId){
        return  toUnsignedInt(edgesBuffer.getShort(NUMBER_OF_BYTES * edgeId + 8));
    }

}








