package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;

/**
 * Enregistrement Waypoint
 *
 * @author Pedro Gouveia (345768)
 * @author Idriss Mimet (324424)
 */
public record Waypoint(PointCh point, int closestNodeId) {
}
