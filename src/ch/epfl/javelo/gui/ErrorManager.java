package ch.epfl.javelo.gui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Classe ErrorManager
 *
 * @author Pedro Gouveia (345768)
 * @author Idriss Mimet (324424)
 */

public final class ErrorManager {
    private final VBox vbox;
    private final Text text;
    private SequentialTransition sequentialTransition;


    /**
     * Constructeur public
     */
    public ErrorManager(){

        text = new Text();
        vbox = new VBox(text);
        vbox.setMouseTransparent(true);
        vbox.getStylesheets().add("error.css");
        transitions();
    }

    /**
     * Méthode permettant d'accéder à la vbox
     * @return vbox
     */
    public Pane pane(){
        return vbox;
    }

    /**
     * Méthode qui permet de créer et d'afficher les messages d'erreur ainsi que leurs animations
     * @param error message d'erreur à afficher
     */
    public void displayError(String error){
        sequentialTransition.playFromStart();
        text.setText(error);
        java.awt.Toolkit.getDefaultToolkit().beep();


    }

    /**
     * Méthode auxiliaire permettant d'instancier les animations du message d'erreur
     */
    private void transitions(){

        FadeTransition fadeTransition1 = new FadeTransition
                (new Duration(200),
                        vbox);
        FadeTransition fadeTransition2 = new FadeTransition(
                new Duration(500)
                , vbox) ;

        fadeTransition1.setFromValue(0.0);
        fadeTransition1.setToValue(0.8);

        PauseTransition pauseTransition = new PauseTransition(
                new Duration(2000));
        fadeTransition2.setFromValue(0.8);
        fadeTransition2.setToValue(0.0);

        sequentialTransition = new SequentialTransition
                (fadeTransition1,
                        pauseTransition,
                        fadeTransition2);

    }



}
