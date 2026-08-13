package application.controller;

import application.classiGeneriche.Chiamata;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ChiamataController {

    @FXML
    private Label nomeLabel;

    @FXML
    private Label motivazioneLabel;


    public void inizializza(
            Chiamata chiamata,
            Runnable chiamataLetta) {

        nomeLabel.setText(
                chiamata.getNome()
                        + " "
                        + chiamata.getCognome()
        );


        motivazioneLabel.setText(
                chiamata.getMotivazione()
        );


        if (!chiamata.isLetta()) {

            chiamata.setLetta(true);

            chiamataLetta.run();
        }
    }


    @FXML
    private void chiudi() {

        Stage stage =
                (Stage) nomeLabel
                        .getScene()
                        .getWindow();

        stage.close();
    }
}