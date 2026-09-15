package application.controller;

import application.classiGeneriche.Database;
import application.classiGeneriche.Diabetologo;
import application.classiGeneriche.Messaggio;
import application.classiGeneriche.Paziente;
import application.classiGeneriche.TipoAlert;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ScriviEmailController {

    @FXML private Label titoloLabel;
    @FXML private TextField destinatarioField;
    @FXML private TextArea testoEmailArea;
    @FXML private Button inviaButton;

    private Paziente paziente;
    private Diabetologo diabetologo;

    // =========================================================
    // INIZIALIZZAZIONE
    // =========================================================

    public void inizializza(Paziente paziente, Diabetologo diabetologo) {
        this.paziente = paziente;
        this.diabetologo = diabetologo;

        titoloLabel.setText("Scrivi una e-mail al tuo diabetologo");
        destinatarioField.setText(diabetologo.getEmail());
        
        inviaButton.setOnAction(event -> inviaEmail());
    }

    // =========================================================
    // INVIA E-MAIL
    // =========================================================

    private void inviaEmail() {
        String testo = testoEmailArea.getText().trim();
        if (testo.isEmpty()) {
            return;
        }

        Messaggio messaggio = new Messaggio(paziente,diabetologo,testo,TipoAlert.PAZIENTE_MEDICO,null);

        Database.getInstance().addMessaggio(messaggio);

        Stage stage =(Stage) inviaButton.getScene().getWindow();
        stage.close();
    }
}