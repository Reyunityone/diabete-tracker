package application.controller;

import application.classiGeneriche.Database;
import application.classiGeneriche.Messaggio;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class MessaggioController {

	//OGGETTI FXML
    @FXML private Label nomeLabel;
    @FXML private Label testoLabel;
    @FXML private Label urgenzaLabel;

    // =========================================================
    // INIZIALIZZAZIONE
    // =========================================================

    public void inizializza(Messaggio messaggio,Runnable messaggioLetto) {
        String nomeCompleto = messaggio.getMittente() != null ? messaggio.getMittente().getNome() + " " + messaggio.getMittente().getCognome() : "Sistema";
        nomeLabel.setText(nomeCompleto);
        testoLabel.setText(messaggio.getTesto());
        
        if (messaggio.getUrgenza() != null) {

            String tipoUrgenza = messaggio.getUrgenza().name();
            tipoUrgenza = Character.toUpperCase(tipoUrgenza.charAt(0))
                    + tipoUrgenza.substring(1);

            urgenzaLabel.setText("[Emergency Type: " + tipoUrgenza + "]");
            urgenzaLabel.getStyleClass().add("message-urgency");
            urgenzaLabel.getStyleClass().add(
                    "urgency-" + messaggio.getUrgenza().name().toLowerCase()
            );

        } else {
            urgenzaLabel.setVisible(false);
            urgenzaLabel.setManaged(false);
        }

        // IL MESSAGGIO VIENE CONSIDERATO LETTO
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
        Stage stage =(Stage) nomeLabel.getScene().getWindow();
        stage.close();
    }
}