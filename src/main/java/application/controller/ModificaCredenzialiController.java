package application.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

public class ModificaCredenzialiController {

    @FXML
    private Label personaLabel;

    @FXML
    private PasswordField vecchieCredenzialiField;

    @FXML
    private PasswordField nuoveCredenzialiField;


    private ResponsabileController responsabileController;

    private ResponsabileController.Persona persona;


    public void inizializza(
            ResponsabileController controller,
            ResponsabileController.Persona persona) {

        this.responsabileController = controller;
        this.persona = persona;

        personaLabel.setText(
                persona.getNome()
                        + " "
                        + persona.getCognome()
        );
    }


    @FXML
    private void handleConferma() {

        String vecchie =
                vecchieCredenzialiField.getText();

        String nuove =
                nuoveCredenzialiField.getText();


        if (vecchie.isEmpty()
                || nuove.isEmpty()) {

            System.out.println(
                    "Compila tutti i campi."
            );

            return;
        }


        responsabileController.modificaCredenziali(
                persona,
                vecchie,
                nuove
        );


        chiudiFinestra();
    }


    @FXML
    private void handleAnnulla() {

        chiudiFinestra();
    }


    private void chiudiFinestra() {

        Stage stage =
                (Stage) vecchieCredenzialiField
                        .getScene()
                        .getWindow();

        stage.close();
    }
}