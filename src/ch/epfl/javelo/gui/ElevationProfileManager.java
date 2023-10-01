package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.*;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;

import java.util.ArrayList;
import java.util.List;


/**
 * Classe ElevationProfileManager
 *
 * @author Pedro Gouveia (345768)
 * @author Idriss Mimet (324424)
 */
public final class ElevationProfileManager {
    private final Pane pane;
    private final DoubleProperty mousePositionOnProfileProperty;
    private final ReadOnlyDoubleProperty position;
    private final ReadOnlyObjectProperty<ElevationProfile> profile;
    private final BorderPane borderPane;
    private final Path path;
    private final Group group;
    private final Polygon polygon;
    private final Line line;
    private final Text text;
    private final Insets insets  = new Insets(10, 10, 20, 40);
    private final ObjectProperty<Transform> screenToWorld;
    private final ObjectProperty<Transform> worldToScreen;
    private final ObjectProperty<Rectangle2D> rect;
    /**
     * Constante utilisée pour transformer des kilomètres en mètres
     */
    private final static float KM_IN_METERS = 1000;

    /**
     * Tableau contenant les espacements minimaux pour la position
     */
    private static final int[] POS_STEPS =
            {1000, 2000, 5000, 10_000, 25_000, 50_000, 100_000};

    /**
     * Tableau contenant les espacements minimaux pour l'élévation
     */
    private static final int[] ELE_STEPS =
            {5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000};


    /**
     * Constructeur public
     * @param profile profile de l'itinéraire
     * @param position position mise en évidence
     */
    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> profile,
                                   ReadOnlyDoubleProperty position) {


        mousePositionOnProfileProperty = new SimpleDoubleProperty(Double.NaN);
        screenToWorld = new SimpleObjectProperty<>(new Affine());
        worldToScreen = new SimpleObjectProperty<>(new Affine());
        this.position = position;
        this.profile = profile;


        path = new Path();
        group = new Group();
        polygon = new Polygon();
        line = new Line();
        path.setId("grid");
        polygon.setId("profile");

        text = new Text();
        pane = new Pane(path, group, polygon, line);
        VBox vbox = new VBox(text);
        vbox.setId("profile_data");
        borderPane = new BorderPane(pane, null, null, vbox, null);

        borderPane.setCenter(pane);
        borderPane.setBottom(vbox);

        borderPane.getStylesheets().add("elevation_profile.css");

        rect = new SimpleObjectProperty<>(Rectangle2D.EMPTY);
        bindRect();
        listeners();
        mouseActions();


    }


    /**
     * Méthode auxiliaire permettant de créer les transformations nécessaires pour la conversion
     * entre coordonnées de la carte et coordonnées de l'écran
     */
    private void transformation() {
        double minY = rect.get().getMinY();
        double minX = rect.get().getMinX();

        double rectHeight = rect.get().getHeight();
        double rectWidth = rect.get().getWidth();

        double profileLength = profile.get().length();
        double profileMaxElev = profile.get().maxElevation();
        double profileMinElev = profile.get().minElevation();

        Affine transform = new Affine();
        transform.prependTranslation(-minX, -minY);

        transform.prependScale(profileLength / rectWidth,
                -(profileMaxElev - profileMinElev) / rectHeight);

        transform.prependTranslation(0, profileMaxElev);
        screenToWorld.set(transform);

        try {
            worldToScreen.set(transform.createInverse());
        } catch (NonInvertibleTransformException e) {
            throw new Error(e);
        }
    }


    /**
     * Méthode auxiliaire permettant de lier le rectangle au pane
     */
    private void bindRect() {
        rect.bind(Bindings.createObjectBinding(() ->
                        new Rectangle2D(
                                insets.getLeft(),
                                insets.getTop(),
                                Math.max(0, (pane.getWidth() - (insets.getLeft() + insets.getRight()))),
                                Math.max(0, (pane.getHeight() - (insets.getBottom() + insets.getTop()))))
                , pane.widthProperty(),
                pane.heightProperty()));

    }

    /**
     * Méthode auxiliaire permettant de créer la grille ou s'affichent l'itinéraire et son profile
     */
    private void grid() {

        List<PathElement> liste = new ArrayList<>();
        List<Text> listeText = new ArrayList<>();
        setHorizontalLines(liste,listeText);
        setVerticalLines(liste,listeText);
        path.getElements().setAll(liste);
        group.getChildren().setAll(listeText);

    }

    private void setHorizontalLines(List<PathElement> liste, List<Text> listeText){
        int nbOfHorizontalLines = 0;
        int heightPerBlock = computePerBlock(ELE_STEPS, 25, "y");
        int firstElevation = Math2.ceilDiv(
                (int) Math.round(profile.get().minElevation()), heightPerBlock) * heightPerBlock;
        while
        (nbOfHorizontalLines * heightPerBlock + firstElevation <= profile.get().maxElevation()){


            Point2D start = worldToScreen.get().transform(0,
                    nbOfHorizontalLines *
                            heightPerBlock +
                            firstElevation);

            Point2D end = worldToScreen.get().transform(
                    Math.round(profile.get().length()),
                    nbOfHorizontalLines *
                            heightPerBlock +
                            firstElevation);

            liste.add(new MoveTo(start.getX(), start.getY()));
            liste.add(new LineTo(end.getX(), end.getY()));


            Text text = new Text();
            text.setFont(Font.font("Avenir", 10));
            text.getStyleClass().add("grid_label");
            text.getStyleClass().add("horizontal");
            text.textOriginProperty().setValue(VPos.CENTER);
            text.setText(""+(firstElevation+ nbOfHorizontalLines * heightPerBlock));
            text.setLayoutX(rect.get().getMinX()-(text.prefWidth(0)+2));
            text.setLayoutY(start.getY()-insets.getTop());
            listeText.add(text);

            nbOfHorizontalLines++;

        }
    }

    private void setVerticalLines(List<PathElement> liste, List<Text> listeText) {

        int nbOfVerticalLines = 0;
        int lengthPerBlock = computePerBlock(POS_STEPS, 50, "x");


        while (nbOfVerticalLines * lengthPerBlock <= profile.get().length()) {

            Point2D start = worldToScreen.get().transform(
                    nbOfVerticalLines *
                            lengthPerBlock,
                    profile.get().minElevation());

            Point2D end = worldToScreen.get().transform(
                    nbOfVerticalLines *
                            lengthPerBlock,
                    profile.get().maxElevation());

            liste.add(new MoveTo(start.getX(), start.getY()));
            liste.add(new LineTo(end.getX(), end.getY()));

            Text text = new Text();
            text.setFont(Font.font("Avenir", 10));
            text.getStyleClass().add("grid_label");
            text.getStyleClass().add("vertical");
            text.textOriginProperty().setValue(VPos.TOP);
            text.setText("" + "" + (int) Math.ceil((nbOfVerticalLines * lengthPerBlock) / KM_IN_METERS));
            text.setLayoutX(start.getX() - insets.getLeft() - (text.prefWidth(0)) / 2 + rect.get().getMinX());
            text.setLayoutY(rect.get().getMaxY());
            listeText.add(text);
            nbOfVerticalLines++;

        }

    }


    /**
     * Méthode auxiliaire permettant d'afficher les statistiques de l'itinéraire
     */
    private void statistics() {
        String stats = "";
        if(profile.get()!=null){
            stats = String.format("Longueur : %.1f km" +
                            "     Montée : %.0f m" +
                            "     Descente : %.0f m" +
                            "     Altitude : de %.0f m à %.0f m",
                    profile.get().length() / KM_IN_METERS,
                    profile.get().totalAscent(),
                    profile.get().totalDescent(),
                    profile.get().minElevation(),
                    profile.get().maxElevation());
            text.setId("elevation_profile.css");
            text.setId("profile_data");

            text.setText(stats);
        }

    }

    /**
     * Méthode contenant tous les listeners qui vont nous permettre de gérer tous les événements
     * javaFX
     */
    private void listeners() {


        pane.heightProperty().addListener((a, b, c) -> {
            transformation();
            grid();
            polygonBuilder();
            statistics();
            createHighlightedPosition();
        });

        pane.widthProperty().addListener((a, b, c) -> {
            transformation();
            grid();
            polygonBuilder();
            statistics();
            createHighlightedPosition();

        });

        profile.addListener((e, oldS, newS) -> {

            if(newS!=null) {
                transformation();
                grid();
                polygonBuilder();
                statistics();
                createHighlightedPosition();
            }
        });

        worldToScreen.addListener((a, b, c) -> {
            grid();
            createHighlightedPosition();
        });

    }

    private void mouseActions(){
        borderPane.setOnMouseMoved(e -> {
            if ((e.getX() <= rect.get().getMaxX() && e.getX() >= rect.get().getMinX() &&
                    e.getY() >= rect.get().getMinY() && e.getY() <= rect.get().getMaxY())) {
                mousePositionOnProfileProperty.set(screenToWorld.get().deltaTransform
                        (e.getX()-insets.getLeft(), 0).getX());
            }else{
                mousePositionOnProfileProperty.set(Double.NaN);
            }
        });

        pane.setOnMouseExited(e -> {
            mousePositionOnProfileProperty.set(Double.NaN);
        });


    }



    /**
     * Méthode auxiliaire permettant de calculer l'espacement correct de chaque case de la grille,
     * en fonction de l'itinéraire et de son élévation
     * @param distances tableau des espacements de réference
     * @param minSpace espace minimal en pixels
     * @param s paramètre auxiliaire permettant de différencier la position de l'élévation
     * @return espacement ideale pour chaque case de la grille à afficher
     */
    private int computePerBlock(int[] distances, int minSpace, String s){
        double pix = 0;
        int value = 0;
        for (Integer i : distances) {
            if(s.equals("y")) {
                pix = worldToScreen.get().deltaTransform(0, -i).getY();
            }else{
                pix = worldToScreen.get().deltaTransform(i,0).getX();
            }
            if (pix >= minSpace) {
                value = i;
                break;
            }

        }
        return value !=0 ? value : distances[distances.length-1];
    }


    /**
     * Méthode auxiliaire qui crée le polygone representant l'élévation de l'itinéraire
     */
    private void polygonBuilder(){
        polygon.getPoints().clear();
        Point2D pointBasGauche = new Point2D(rect.get().getMinX(), rect.get().getMaxY());
        Point2D pointBasDroite = new Point2D(rect.get().getMaxX(), rect.get().getMaxY());


        for (double i = rect.get().getMinX(); i < rect.get().getMaxX() ; i++) {
            double newI = screenToWorld.get().transform(i, 0).getX();
            polygon.getPoints().add(i);
            polygon.getPoints().add(worldToScreen.get().transform(0, profile.get().elevationAt(newI)).getY());


        }
        addPoint(pointBasDroite);
        addPoint(pointBasGauche);

    }


    /**
     * Méthode permettant d'accéder au pane
     * @return pane
     */
    public Pane pane(){
        return borderPane;
    }

    /**
     * Méthode permettant d'accéder a la propriété contenant la position de la souris
     * @return la propriété contenant la position de la souris
     */
    public ReadOnlyDoubleProperty mousePositionOnProfileProperty() {
        return mousePositionOnProfileProperty;
    }

    /**
     * Méthode auxiliaire permettant d'ajouter des points lors de la construction du polygone
     * @param point point à ajouter
     */
    private void addPoint(Point2D point){
        polygon.getPoints().add(point.getX());
        polygon.getPoints().add(point.getY());

    }

    /**
     * Méthode auxiliaire permettant de créer la ligner qui represente la position mise en évidence
     */
    private void createHighlightedPosition(){
        line.startYProperty().bind(Bindings.select(rect, "minY"));
        line.endYProperty().bind(Bindings.select(rect, "maxY"));
        line.visibleProperty().bind(position.greaterThanOrEqualTo(0));

        line.layoutXProperty().bind(Bindings.createDoubleBinding(()->
                worldToScreen.get().transform(position.get(), 0).getX(), position, worldToScreen));

    }
}





