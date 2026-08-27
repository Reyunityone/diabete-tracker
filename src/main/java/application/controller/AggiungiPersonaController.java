package application.controller;

import java.util.ArrayList;
import java.util.List;

import application.classiGeneriche.User;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CustomMenuItem;
import javafx.stage.Stage;

public class AggiungiPersonaController {

    // ============================================================
    // COMPONENTI GRAFICI
    // ============================================================

    @FXML
    private Label titoloLabel;

    @FXML
    private TextField nomeField;

    @FXML
    private TextField cognomeField;

    @FXML
    private TextField codiceFiscaleField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label assegnazioneLabel;

    @FXML
    private ComboBox<User> medicoComboBox;

    @FXML
    private MenuButton pazientiMenuButton;


    // ============================================================
    // DATI E CONTROLLER
    // ============================================================

    private ResponsabileController responsabileController;

    private boolean medico;


    // ============================================================
    // INIZIALIZZAZIONE DELLA FINESTRA
    // ============================================================

    public void inizializza(ResponsabileController controller,boolean medico) {
        this.responsabileController = controller;
        this.medico = medico;

        if (medico) {
            titoloLabel.setText("Aggiungi medico");
            assegnazioneLabel.setText("Assegna pazienti");
            medicoComboBox.setVisible(false);
            medicoComboBox.setManaged(false);

            pazientiMenuButton.setVisible(true);
            pazientiMenuButton.setManaged(true);

            caricaPazienti();

        } else {
            titoloLabel.setText("Aggiungi paziente");
            assegnazioneLabel.setText("Assegna medico");
            pazientiMenuButton.setVisible(false);
            pazientiMenuButton.setManaged(false);

            medicoComboBox.setVisible(true);
            medicoComboBox.setManaged(true);

            caricaMedici();
        }
    }


    // ============================================================
    // CARICAMENTO DEI MEDICI
    // ============================================================

    private void caricaMedici() {
        medicoComboBox.getItems().clear();
        medicoComboBox.getItems().addAll(responsabileController.getMedici());
    }


    // ============================================================
    // CARICAMENTO DEI PAZIENTI
    // ============================================================

    private void caricaPazienti() {
        pazientiMenuButton.getItems().clear();

        for (User paziente : responsabileController.getPazienti()) {
            CheckBox checkBox = new CheckBox(paziente.getNome() + " " + paziente.getCognome());

            checkBox.setUserData(paziente);

            CustomMenuItem item =new CustomMenuItem(checkBox);

            item.setHideOnClick(false);

            pazientiMenuButton.getItems().add(item);
        }
    }


    // ============================================================
    // CONFERMA INSERIMENTO
    // ============================================================

    @FXML
    private void handleConferma() {
        String nome =nomeField.getText().trim();
        String cognome =cognomeField.getText().trim();
        String codiceFiscale =codiceFiscaleField.getText().trim();
        String email =emailField.getText().trim();
        String username =usernameField.getText().trim();
        String password =passwordField.getText();


        // --------------------------------------------------------
        // CONTROLLO DEI CAMPI
        // --------------------------------------------------------

        if (nome.isEmpty()|| cognome.isEmpty()|| codiceFiscale.isEmpty()|| email.isEmpty()|| username.isEmpty()|| password.isEmpty()) {
            return;
        }


        // --------------------------------------------------------
        // CREAZIONE NUOVO MEDICO
        // --------------------------------------------------------

        if (medico) {
            List<User> pazientiSelezionati =new ArrayList<>();

            for (MenuItem menuItem :pazientiMenuButton.getItems()) {

            	CheckBox checkBox =(CheckBox) ((CustomMenuItem) menuItem).getContent();

            	if (checkBox.isSelected()) {

            		User paziente =(User) checkBox.getUserData();

            		pazientiSelezionati.add(paziente);
            	}
            }

            responsabileController.aggiungiMedico(nome,cognome,codiceFiscale,email,username,password,pazientiSelezionati);
        }


        // --------------------------------------------------------
        // CREAZIONE NUOVO PAZIENTE
        // --------------------------------------------------------

        else {
            User medicoSelezionato =medicoComboBox.getValue();

            if (medicoSelezionato == null) return;

            responsabileController.aggiungiPaziente(nome,cognome,codiceFiscale,email,username,password,medicoSelezionato);
        }

        chiudiFinestra();
    }


    // ============================================================
    // ANNULLAMENTO
    // ============================================================

    @FXML
    private void handleAnnulla() {
        chiudiFinestra();
    }


    // ============================================================
    // CHIUSURA FINESTRA
    // ============================================================

    private void chiudiFinestra() {
        Stage stage =(Stage) nomeField.getScene().getWindow();

        stage.close();
    }
}