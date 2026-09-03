package application.controller;

import application.classiGeneriche.Database;
import application.classiGeneriche.Messaggio;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class MessaggioController {

    @FXML
    private Label nomeLabel;

    @FXML
    private Label testoLabel;


    // =========================================================
    // INIZIALIZZAZIONE
    // =========================================================

    public void inizializza(
            Messaggio messaggio,
            Runnable messaggioLetto) {

        String nomeCompleto = messaggio.getMittente() != null ? messaggio.getMittente().getNome() + " " + messaggio.getMittente().getCognome() : "Sistema";

        nomeLabel.setText(
                nomeCompleto
        );


        testoLabel.setText(
                messaggio.getTesto()
        );


        // =====================================================
        // IL MESSAGGIO VIENE CONSIDERATO LETTO
        // =====================================================

        if (!messaggio.isLetto()) {
            Database.getInstance().setMessaggioLetto(messaggio);
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