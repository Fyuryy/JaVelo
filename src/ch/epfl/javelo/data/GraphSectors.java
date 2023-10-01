package ch.epfl.javelo.data;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static ch.epfl.javelo.projection.SwissBounds.HEIGHT;
import static ch.epfl.javelo.projection.SwissBounds.WIDTH;



 /**
 * Enregistrement GraphSectors
 *
 * @author Pedro Gouveia (345768)
 * @author Idriss Mimet (324424)
 */

public record GraphSectors(ByteBuffer buffer) {
    
    private static final int NB_SECTORS_IN_LENGTH = 128;


    /**
     * Constructeur compacte
     */
    public record Sector(int startNodeId, int endNodeId) {
    }

    /**
     * Retourne la liste de secteurs existants dans le carré de centre center et de coté distance*2
     * @param center   PointCh représentant le centre du carré
     * @param distance distance allant du centre du carré aux bords du carré
     * @return les secteurs existants dans le carré de centre et longueur donnés
     */
    public List<Sector> sectorsInArea(PointCh center, double distance) {

        List<Sector> sectors = new ArrayList<Sector>();

        double hauteur = HEIGHT / NB_SECTORS_IN_LENGTH;
        double largeur = WIDTH / NB_SECTORS_IN_LENGTH;

        double distanceDroite = (center.e() + distance) - SwissBounds.MIN_E;
        double distanceBas = (center.n() - distance) - SwissBounds.MIN_N;
        double distanceHaut = center.n() + distance - SwissBounds.MIN_N;
        double distanceGauche = (center.e() - distance) - SwissBounds.MIN_E;

        int xMin = squareCoords(distanceGauche, largeur);
        int xMax = squareCoords(distanceDroite, largeur);
        int yMin = squareCoords(distanceBas, hauteur);
        int yMax = squareCoords(distanceHaut, hauteur);

        for (int y = yMin; y <= yMax; y++) {
            for (int x = xMin; x <= xMax; x++) {

                int index = (Integer.BYTES + Short.BYTES) * (y * NB_SECTORS_IN_LENGTH + x);
                int bufferInt = buffer.getInt(index);
                sectors.add(new Sector(bufferInt, bufferInt + Short.toUnsignedInt
                        (buffer.getShort(index + Integer.BYTES))));
            }
        }

        return sectors;
    }

    /**
     * Methode permettant de calculer les coordonnées du carré de distance donnée
     * @param distance distance du carré
     * @param divide longueur pour diviser la distance
     * @return coordonnée maximale du carré de distance donnée
     */
    private int squareCoords(double distance, double divide) {
        int calc = (int) (distance / divide);
        return Math2.clamp(0, calc, 127);
    }

}




