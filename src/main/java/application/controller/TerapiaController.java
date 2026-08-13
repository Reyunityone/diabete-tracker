package application.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class TerapiaController {

    @FXML
    private Label titoloLabel;

    @FXML
    private TextField farmacoField;

    @FXML
    private TextField assunzioniField;

    @FXML
    private TextField quantitaField;

    @FXML
    private TextArea indicazioniArea;

    @FXML
    private Button salvaButton;


    @FXML
    public void initialize() {

        salvaButton.setOnAction(
                event -> salvaTerapia()
        );
    }


    public void inizializzaPaziente(
            DiabetologoController.Persona paziente) {

        titoloLabel.setText(
                "Terapia - "
                        + paziente.getNome()
                        + " "
                        + paziente.getCognome()
        );


        // DATI DI PROVA

        farmacoField.setText(
                "Metformina"
        );

        assunzioniField.setText(
                "2"
        );

        quantitaField.setText(
                "500 mg"
        );

        indicazioniArea.setText(
                "Assumere dopo i pasti."
        );
    }


    private void salvaTerapia() {

        String farmaco =
                farmacoField.getText();

        String assunzioni =
                assunzioniField.getText();

        String quantita =
                quantitaField.getText();

        String indicazioni =
                indicazioniArea.getText();


        System.out.println(
                "TERAPIA SALVATA"
        );

        System.out.println(
                "Farmaco: " + farmaco
        );

        System.out.println(
                "Assunzioni: " + assunzioni
        );

        System.out.println(
                "Quantità: " + quantita
        );

        System.out.println(
                "Indicazioni: " + indicazioni
        );
        
        Stage stage =
                (Stage) salvaButton
                        .getScene()
                        .getWindow();

        stage.close();
    }
}