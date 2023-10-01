package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;



import java.io.IOException;


/**
 * Classe BaseMapManager
 *
 * @author Pedro Gouveia (345768)
 * @author Idriss Mimet (324424)
 */

public final class BaseMapManager {
    private final Pane pane;
    private final Canvas canvas;
    private final TileManager tiles;
    private final WaypointsManager waypoints;
    private final ObjectProperty<MapViewParameters> prop;
    private boolean redrawNeeded;
    private final SimpleObjectProperty<Point2D> oldPoint;

    /**
     * Niveau de zoom maximal
     */
    private final static int MAX_ZOOM_LEVEL = 19;
    /**
     * Niveau de zoom minimal
     */
    private final static int MIN_ZOOM_LEVEL = 8;
    /**
     * Nombre de pixels composant une tuile
     */
    private final static int tilePixels = 256;


    /**
     * Constructeur public
     * @param tiles gestionnaire de tuiles
     * @param waypoints gestionnaire des points de passage
     * @param prop propriété javaFX contenant les paramètres de la carte
     */
    public BaseMapManager(TileManager tiles,WaypointsManager waypoints,
                          ObjectProperty<MapViewParameters> prop){
        this.tiles = tiles;
        this.waypoints = waypoints;
        this.canvas = new Canvas();
        this.pane = new Pane();
        pane.getChildren().add(canvas);
        this.prop = prop;
        this.oldPoint = new SimpleObjectProperty<>();


        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this :: redrawIfNeeded);
        });


        pane.heightProperty().addListener((p, oldS, newS) ->{
            redrawOnNextPulse();
        });

        pane.widthProperty().addListener((p, oldS, newS) ->{
            redrawOnNextPulse();
        });

        prop.addListener((p, oldS, newS) ->{
            redrawOnNextPulse();
        });

        pointUpdate();
        clickedMouse();
        dragged();
        zoomLevel();


    }

    /**
     * Méthode permettant l'accès au pane
     * @return pane
     */
    public Pane pane(){
        return pane;
    }

    /**
     * Méthode auxiliaire qui se charge de dessiner le carte
     */
    private void drawCanvas(){
        GraphicsContext gc = canvas.getGraphicsContext2D();

        double  propX = prop.get().x();
        double propY = prop.get().y();
        double diffX = propX % tilePixels;
        double diffY = propY % tilePixels;


        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        for (int x = 0; x <= canvas.getWidth() + tilePixels ; x+=tilePixels) {
            for (int y = 0; y <= canvas.getHeight() + tilePixels; y += tilePixels) {


                TileManager.TileId id = new TileManager.TileId(prop.get().zoom(),
                        Math.floorDiv((int) (propX + x), tilePixels),
                        Math.floorDiv((int) (propY + y), tilePixels));

                if (!(TileManager.TileId.isValid(id.zoom(), id.X(), id.Y()))) {
                    continue;
                }

                try {
                    gc.drawImage(tiles.imageForTileAt(id), x - diffX, y - diffY);
                } catch (IOException ignored) {
                }

        }

        }
    }

    /**
     * Méthode auxiliaire qui est appelée lorsqu'un redessin est nécessaire
     */
    private void redrawIfNeeded(){
        if(!redrawNeeded) return;
        redrawNeeded = false;
        drawCanvas();
    }

    /**
     * Méthode auxiliaire qui est appelé lorsqu'un redessin est nécessaire au prochain pulse
     */
    private void redrawOnNextPulse(){
        redrawNeeded = true;
        Platform.requestNextPulse();
    }


    /**
     * Méthode auxiliaire qui gère le glissement de la carte
     */
    private void dragged(){
        pane.setOnMouseDragged(e -> {

            double newX = prop.get().x() + oldPoint.get().getX() - e.getX();
            double newY = prop.get().y() + oldPoint.get().getY() - e.getY();
            prop.setValue(prop.get().withMinXY(newX, newY));
            oldPoint.set(new Point2D(e.getX(), e.getY()));
            redrawOnNextPulse();
        });
    }

    /**
     * Méthode qui permet de mettre à jour les coordonnées du point de la souris lorsque celle ci est
     * appuyée
     */
    private void pointUpdate(){
        pane().setOnMousePressed(e -> {
            oldPoint.set(new Point2D(e.getX(), e.getY()));
        });
    }


    /**
     * Méthode auxiliaire qui permet de gérer les clicks de la souris et ses utilités
     */
    private void clickedMouse() {
        pane.setOnMouseClicked(e -> {
            if (e.isStillSincePress()) {
                waypoints.addWaypoint(e.getX(), e.getY());
            }
        });
    }

    /**
     * Méthode auxiliaire qui permet de gérer le zoom de la carte
     */
    private void zoomLevel(){
        SimpleLongProperty minScrollTime = new SimpleLongProperty();
        pane.setOnScroll(s -> {
            if (s.getDeltaY() == 0d) return;
            long currentTime = System.currentTimeMillis();
            if (currentTime < minScrollTime.get()) return;
            minScrollTime.set(currentTime + 200);
            int zoomDelta = (int) Math.signum(s.getDeltaY());
            int newZoomlevel = Math2.clamp(MIN_ZOOM_LEVEL, prop.get().zoom() + zoomDelta ,MAX_ZOOM_LEVEL);
            double pointX = s.getX();
            double pointY = s.getY();
            PointWebMercator sourisPoint = prop.get().pointAt(pointX,pointY);
            double newX = sourisPoint.xAtZoomLevel(newZoomlevel);
            double newY = sourisPoint.yAtZoomLevel(newZoomlevel);
            prop.setValue(new MapViewParameters(newZoomlevel, newX-pointX, newY-pointY));
            redrawOnNextPulse();

        });

    }
}


