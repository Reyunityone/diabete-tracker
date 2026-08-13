package application.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class InfoPazienteController {

    @FXML
    private Label titoloLabel;

    @FXML
    private TextArea fattoriRischioArea;

    @FXML
    private TextArea patologieArea;

    @FXML
    private TextArea comorbiditaArea;

    @FXML
    private Button salvaButton;


    @FXML
    public void initialize() {

        salvaButton.setOnAction(
                event -> salvaInformazioni()
        );
    }


    public void inizializzaPaziente(
            DiabetologoController.Persona paziente) {

        titoloLabel.setText(
                "Info paziente - "
                        + paziente.getNome()
                        + " "
                        + paziente.getCognome()
        );


        fattoriRischioArea.setText(
                "Fumo, sedentarietà"
        );

        patologieArea.setText(
                "Nessuna"
        );

        comorbiditaArea.setText(
                "Ipertensione"
        );
    }


    private void salvaInformazioni() {

        System.out.println(
                "Informazioni paziente salvate."
        );
        
        Stage stage =
                (Stage) salvaButton
                        .getScene()
                        .getWindow();

        stage.close();
    }
}