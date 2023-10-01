package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * CLasse ElevationProfileComputer
 * 
 * @author Pedro Gouveia (345768)
 * @author Idriss Mimet (324424)
 */

public final class ElevationProfileComputer {

    /**
     * Constructeur privé (classe non instantiable)
     */
    private ElevationProfileComputer(){}


    /**
     * Retourne le profil en long de l'itinéraire route, en garantissant que l'écart entre les échantillons du profil
     * soit d'au maximum maxStepLength mètres et comblant les profiles des arêtes n'ayant pas de profile
     * @param route itinéraire duquel on cherche le profile
     * @param maxStepLength distance maximale pour l'écart entre les échantillons
     * @return tableau ElevationProfile contenant le profile de l'itinéraire simples
     */
    public static ElevationProfile elevationProfile(Route route, double maxStepLength) {
        Preconditions.checkArgument(maxStepLength > 0);
        if (route != null) {
            int tabLength = (int) Math.ceil(route.length() / maxStepLength) + 1;
            double position = route.length() / (tabLength - 1);
            int index = 0;
            int inverseIndex = tabLength - 1;
            List<Integer> listeIndexNan = new ArrayList<Integer>();
            float[] elevationProfile = new float[tabLength];


            for (int i = 0; i < tabLength; i++) {
                elevationProfile[i] = (float) route.elevationAt(i * position);
            }

            while (index <= (tabLength - 1) && Float.isNaN(elevationProfile[index])) {
                index += 1;
            }

            if (index == tabLength) {
                return new ElevationProfile(route.length(), new float[tabLength]);

            }
            Arrays.fill(elevationProfile,
                    0, index,
                    elevationProfile[index]);

            if (Float.isNaN(elevationProfile[inverseIndex])) {
                while (Float.isNaN(elevationProfile[inverseIndex])) {
                    inverseIndex -= 1;
                }
                Arrays.fill(elevationProfile,
                        inverseIndex + 1,
                        tabLength,
                        elevationProfile[inverseIndex]);
            }
            for (int i = index; i < inverseIndex; i++) {
                if (Float.isNaN(elevationProfile[i])) {
                    listeIndexNan.add(i);
                }
            }

            int indexTemp = 0;

            while (indexTemp < listeIndexNan.size()) {
                List<Integer> temp = new ArrayList<>();
                if ((indexTemp < listeIndexNan.size() - 1) &&
                        (listeIndexNan.get(indexTemp + 1) - listeIndexNan.get(indexTemp) == 1)) {
                    temp.add(listeIndexNan.get(indexTemp));
                    temp.add(listeIndexNan.get(indexTemp + 1));
                    indexTemp += 2;
                    while ((indexTemp <= listeIndexNan.size() - 1) &&
                            (listeIndexNan.get(indexTemp) - listeIndexNan.get(indexTemp - 1) == 1)) {
                        temp.add(listeIndexNan.get(indexTemp));
                        indexTemp += 1;
                    }
                } else {
                    temp.add(listeIndexNan.get(indexTemp));
                    indexTemp += 1;
                }
                int start = temp.get(0) - 1;
                int end = temp.get(temp.size() - 1) + 1;

                for (int tabIndex : temp) {
                    elevationProfile[tabIndex] = (float) Math2.interpolate(elevationProfile[start],
                            elevationProfile[end],
                            ((double) tabIndex - start) / (end - start));
                }
            }
            return new ElevationProfile(route.length(), elevationProfile);
        }
        return null;
    }
}
