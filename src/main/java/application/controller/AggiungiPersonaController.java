package application.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AggiungiPersonaController {

    @FXML
    private Label titoloLabel;

    @FXML
    private TextField nomeField;

    @FXML
    private TextField cognomeField;

    @FXML
    private PasswordField credenzialiField;


    private ResponsabileController responsabileController;

    private boolean medico;


    public void inizializza(
            ResponsabileController controller,
            boolean medico) {

        this.responsabileController = controller;
        this.medico = medico;

        if (medico) {
            titoloLabel.setText("Aggiungi medico");
        } else {
            titoloLabel.setText("Aggiungi paziente");
        }
    }


    @FXML
    private void handleConferma() {

        String nome = nomeField.getText().trim();
        String cognome = cognomeField.getText().trim();
        String credenziali = credenzialiField.getText();


        // Controllo minimo dei campi

        if (nome.isEmpty()
                || cognome.isEmpty()
                || credenziali.isEmpty()) {

            System.out.println(
                    "Compila tutti i campi."
            );

            return;
        }


        // Aggiunta alla lista del Responsabile

        responsabileController.aggiungiPersona(
                nome,
                cognome,
                credenziali,
                medico
        );


        chiudiFinestra();
    }


    @FXML
    private void handleAnnulla() {

        chiudiFinestra();
    }


    private void chiudiFinestra() {

        Stage stage =
                (Stage) nomeField
                        .getScene()
                        .getWindow();

        stage.close();
    }
}