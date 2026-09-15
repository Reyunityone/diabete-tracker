package application.controller;

import java.util.ArrayList;

import application.classiGeneriche.Database;
import application.classiGeneriche.Diabetologo;
import application.classiGeneriche.Paziente;
import application.classiGeneriche.User;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
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

    // Password visibile e modificabile
    @FXML
    private TextField passwordField;

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

        medico = persona instanceof Diabetologo;

        //DATI PERSONALI
        personaLabel.setText(persona.getNome()+ " "+ persona.getCognome());
        nomeField.setText(persona.getNome());
        cognomeField.setText(persona.getCognome());
        codiceFiscaleField.setText(persona.getCodiceFiscale());
        emailField.setText(persona.getEmail());
        usernameField.setText(persona.getUsername());
        passwordField.setText(persona.getPassword());

        // CONFIGURAZIONE MEDICO
        if (medico) {
            titoloLabel.setText("Modifica medico");
            assegnazioneLabel.setText("Assegna pazienti");

            medicoComboBox.setVisible(false);
            medicoComboBox.setManaged(false);

            pazientiMenuButton.setVisible(true);
            pazientiMenuButton.setManaged(true);

            caricaPazienti();
        }

        // CONFIGURAZIONE PAZIENTE
        else {
            titoloLabel.setText("Modifica paziente");
            assegnazioneLabel.setText("Assegna medico");

            pazientiMenuButton.setVisible(false);
            pazientiMenuButton.setManaged(false);

            medicoComboBox.setVisible(true);
            medicoComboBox.setManaged(true);

            caricaMedici();


            // PRESELEZIONE MEDICO ATTUALE
            assert persona instanceof Paziente;
            Paziente paziente =(Paziente) persona;

            Diabetologo medicoAttuale =paziente.getMedicoDiRiferimento();

            if (medicoAttuale != null) medicoComboBox.setValue(medicoAttuale);
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
        
        Diabetologo diabetologo =(Diabetologo) persona;

        for (Paziente paziente :responsabileController.getPazienti()) {

            CheckBox checkBox =new CheckBox(paziente.getNome()+ " " + paziente.getCognome());
            checkBox.setUserData(paziente);

            Diabetologo medicoAttuale =paziente.getMedicoDiRiferimento();
            
            //CONTROLLO ASSOCIAZIONE ATTUALE
            if (medicoAttuale != null&& medicoAttuale.getUsername().equals(diabetologo.getUsername())) {
                checkBox.setSelected(true);
            }

           
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

        // CONTROLLO COMPLETEZZA
        if (nome.isEmpty()|| cognome.isEmpty()|| codiceFiscale.isEmpty()|| email.isEmpty()|| username.isEmpty()|| password.isEmpty()) {
            System.out.println("Completa tutti i campi");
            return;
        }

        // CONTROLLO USERNAME
        boolean usernameModificato =!username.equals(persona.getUsername());
        if (usernameModificato&& Database.getInstance().usernameEsistente(username)) {
            System.out.println("Username già esistente");
            return;
        }

        // MODIFICA MEDICO
        if (medico) {
            ArrayList<Paziente>pazientiSelezionati =new ArrayList<>();
            
            for (MenuItem menuItem :pazientiMenuButton.getItems()) {
                CheckBox checkBox =(CheckBox)((CustomMenuItem)menuItem).getContent();
                
                if (checkBox.isSelected()) {
                    Paziente paziente =(Paziente)checkBox.getUserData();
                    pazientiSelezionati.add(paziente);
                }
            }

            modificaCredenziali(persona,username,password,codiceFiscale,nome,cognome,email,pazientiSelezionati,null);
        }

        // MODIFICA PAZIENTE
        else {
            User medicoSelezionato =medicoComboBox.getValue();

            if (medicoSelezionato == null) {
                System.out.println("Seleziona un medico");
                return;
            }

            modificaCredenziali(persona,username,password,codiceFiscale,nome,cognome,email,null,medicoSelezionato);
        }

        chiudiFinestra();
    }


    // ============================================================
    // ANNULLA
    // ============================================================
    @FXML
    private void handleAnnulla() {
        chiudiFinestra();
    }


    // ============================================================
    // CHIUSURA FINESTRA
    // ============================================================

    private void chiudiFinestra() {
        Stage stage =(Stage)nomeField.getScene().getWindow();
        stage.close();
    }


    // ============================================================
    // MODIFICA CREDENZIALI
    // ============================================================
    public void modificaCredenziali(User persona,String username,String password,String codiceFiscale,String nome,String cognome,String email,
            ArrayList<Paziente> pazientiSelezionati,
            User medicoSelezionato) {

        // MODIFICA DIABETOLOGO
        if (persona instanceof Diabetologo vecchio) {

            Diabetologo nuovo =new Diabetologo(username,password,codiceFiscale,nome,cognome,email);

            // AGGIORNO IL DIABETOLOGO
            Database.getInstance().updateDiabetologo(vecchio,nuovo);

            // AGGIORNO I PAZIENTI ASSOCIATI
            Database.getInstance().updateDiabetologoPazienti(nuovo,pazientiSelezionati);
        }

        // MODIFICA PAZIENTE
        else if (persona instanceof Paziente vecchio) {

            Paziente nuovo =new Paziente(username,password,codiceFiscale,nome,cognome,email,vecchio.getFattoriDiRischio(),(Diabetologo)medicoSelezionato,
                            vecchio.getPatologiePregresse(),
                            vecchio.getComorbidita(),
                            vecchio.getDettagli()
                    );

            // AGGIORNO IL PAZIENTE
            Database.getInstance().updatePaziente(vecchio,nuovo);

            // AGGIORNO IL MEDICO REFERENTE
            Database.getInstance().updatePazienteDiabetologo(nuovo,(Diabetologo)medicoSelezionato);
        }

        responsabileController.aggiornaListe();
    }
}