package application.controller;

import application.classiGeneriche.Messaggio;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class MessaggioController {

    @FXML
    private Label nomeLabel;

    @FXML
    private Label testoLabel;


    private Runnable messaggioLetto;


    // =========================================================
    // INIZIALIZZAZIONE
    // =========================================================

    public void inizializza(
            Messaggio messaggio,
            Runnable messaggioLetto) {

        this.messaggioLetto =
                messaggioLetto;


        nomeLabel.setText(
                messaggio.getNome()
                        + " "
                        + messaggio.getCognome()
        );


        testoLabel.setText(
                messaggio.getTesto()
        );


        // =====================================================
        // IL MESSAGGIO VIENE CONSIDERATO LETTO
        // =====================================================

        if (!messaggio.isLetto()) {

            messaggio.setLetto(true);

            messaggioLetto.run();
        }
    }


    // =========================================================
    // CHIUDI
    // =========================================================

    @FXML
    private void chiudi() {

        Stage stage =
                (Stage) nomeLabel
                        .getScene()
                        .getWindow();

        stage.close();
    }
}