package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.*;
import javafx.application.Application;
import javafx.beans.binding.Bindings;

import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.UncheckedIOException;
import java.nio.file.Path;


public final class JaVelo extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {

        Graph graph = Graph.loadFrom(Path.of("javelo-data"));
        Path cacheBasePath = Path.of("osm-cache");
        String tileServer = "tile.openstreetmap.org";
        TileManager tileManager = new TileManager(cacheBasePath, tileServer);
        CostFunction costFunction = new CityBikeCF(graph);
        RouteComputer routeComputer = new RouteComputer(graph, costFunction);
        RouteBean routeBean = new RouteBean(routeComputer);
        ErrorManager errorManager = new ErrorManager();

        ElevationProfileManager elevationProfileManager =
                new ElevationProfileManager
                        (routeBean.elevationProfile(),
                                routeBean.HighlightedPosition());

        AnnotatedMapManager annotatedMapManager =
                new AnnotatedMapManager(graph,
                        tileManager,
                        routeBean,
                        errorManager::displayError);

        routeBean.HighlightedPosition().bind(Bindings.
                when(annotatedMapManager.
                        mousePositionOnRouteProperty().
                        greaterThanOrEqualTo(0)).then
                        (annotatedMapManager.mousePositionOnRouteProperty()).
                otherwise(elevationProfileManager.mousePositionOnProfileProperty()));


        MenuItem menuItem = new MenuItem("Exporter Gpx");
        Menu menu = new Menu("Fichier");
        menu.getItems().add(menuItem);
        MenuBar menuBar = new MenuBar(menu);

        menuItem.disableProperty().bind(Bindings.createBooleanBinding(() ->
                routeBean.route().get() == null, routeBean.route()
        ));

        menuItem.setOnAction(e -> {
            try {
                GpxGenerator.writeGpx("javelo.gpx",
                        routeBean.route().get(),
                        routeBean.elevationProfile().get());
            } catch (UncheckedIOException ignored) {
            }
        });

        SplitPane splitPane = new SplitPane(annotatedMapManager.pane());
        splitPane.orientationProperty().set(Orientation.VERTICAL);
        SplitPane.setResizableWithParent(elevationProfileManager.pane(), true);

        routeBean.elevationProfile().addListener((e, olds, news) -> {

            if (news != null) {
                if(splitPane.getItems().size() == 1) {
                    splitPane.getItems().add(elevationProfileManager.pane());
                } else {
                    splitPane.getItems().set(1, elevationProfileManager.pane());
                }
            }else{
                splitPane.getItems().remove(1);
            }
        });

        StackPane stackPane = new StackPane(splitPane, errorManager.pane());
        BorderPane pane = new BorderPane(stackPane, menuBar, null, null, null);

        primaryStage.setTitle("JaVelo");
        primaryStage.setScene(new Scene(pane));
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();

    }




}

