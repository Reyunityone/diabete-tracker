package application.controller;

import java.util.ArrayList;
import java.util.List;

import application.classiGeneriche.User;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ModificaCredenzialiController {

    // ============================================================
    // COMPONENTI GRAFICI
    // ============================================================

    @FXML
    private Label titoloLabel;

    @FXML
    private Label personaLabel;

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
    // DATI
    // ============================================================

    private ResponsabileController responsabileController;

    private User persona;

    private boolean medico;


    // ============================================================
    // INIZIALIZZAZIONE
    // ============================================================

    public void inizializza(ResponsabileController controller,User persona) {
        this.responsabileController = controller;
        this.persona = persona;

        medico = persona.getClass().getSimpleName().equals("Diabetologo");

        personaLabel.setText(persona.getNome()+ " "+ persona.getCognome());
        nomeField.setText(persona.getNome());
        cognomeField.setText(persona.getCognome());
        codiceFiscaleField.setText(persona.getCodiceFiscale());
        emailField.setText(persona.getEmail());
        usernameField.setText(persona.getUsername());


        // --------------------------------------------------------
        // CONFIGURAZIONE ASSEGNAZIONE
        // --------------------------------------------------------

        if (medico) {
            titoloLabel.setText("Modifica medico");
            assegnazioneLabel.setText("Assegna pazienti");
            medicoComboBox.setVisible(false);
            medicoComboBox.setManaged(false);
            pazientiMenuButton.setVisible(true);
            pazientiMenuButton.setManaged(true);

            caricaPazienti();

        } else {
            titoloLabel.setText("Modifica paziente");
            assegnazioneLabel.setText("Assegna medico");
            pazientiMenuButton.setVisible(false);
            pazientiMenuButton.setManaged(false);
            medicoComboBox.setVisible(true);
            medicoComboBox.setManaged(true);

            caricaMedici();
        }
    }


    // ============================================================
    // CARICAMENTO MEDICI
    // ============================================================

    private void caricaMedici() {
        medicoComboBox.getItems().clear();
        medicoComboBox.getItems().addAll(responsabileController.getMedici());
    }


    // ============================================================
    // CARICAMENTO PAZIENTI
    // ============================================================

    private void caricaPazienti() {
        pazientiMenuButton.getItems().clear();

        for (User paziente :responsabileController.getPazienti()) {

            CheckBox checkBox =new CheckBox(paziente.getNome()+ " "+ paziente.getCognome());

            checkBox.setUserData(paziente);

            CustomMenuItem item =new CustomMenuItem(checkBox);

            item.setHideOnClick(false);

            pazientiMenuButton.getItems().add(item);
        }
    }


    // ============================================================
    // CONFERMA MODIFICHE
    // ============================================================

    @FXML
    private void handleConferma() {
        String nome =nomeField.getText().trim();
        String cognome =cognomeField.getText().trim();
        String codiceFiscale =codiceFiscaleField.getText().trim();
        String email =emailField.getText().trim();
        String username =usernameField.getText().trim();
        String password =passwordField.getText();

        if (nome.isEmpty()|| cognome.isEmpty()|| codiceFiscale.isEmpty()|| email.isEmpty()|| username.isEmpty()) {
            return;
        }


        // --------------------------------------------------------
        // MODIFICA MEDICO
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

            
            //MODIFICA DATI USER
            persona.setNome(nome);
            persona.setCognome(cognome);
            persona.setCodiceFiscale(codiceFiscale);
            persona.setEmail(email);
            persona.setUsername(username);

            //AGGIORNAMENTO CREDENZIALI LASCIATO AL CONTROLLER
            responsabileController.modificaCredenziali(persona,password,pazientiSelezionati,null);
        }


        // --------------------------------------------------------
        // MODIFICA PAZIENTE
        // --------------------------------------------------------
        else {
            User medicoSelezionato =medicoComboBox.getValue();

            if (medicoSelezionato == null) return;

            //MODIFICA DATI USER
            persona.setNome(nome);
            persona.setCognome(cognome);
            persona.setCodiceFiscale(codiceFiscale);
            persona.setEmail(email);
            persona.setUsername(username);

            //AGIORNAMENTO CREDENZIALI LASCIATO AL CONTROLLER
            responsabileController.modificaCredenziali(persona,password,null,medicoSelezionato);
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
