package application.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import application.classiGeneriche.*;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;


public class PazienteController {

    // PROFILO
    @FXML private ImageView profileImage;
    @FXML private Label nomeCognomeLabel;
    @FXML private Label ruoloLabel;
    @FXML private Button logoutButton;

    // MESSAGGI
    @FXML private Button messaggiButton;
    @FXML private ImageView messaggiNotification;
    private List<Messaggio> messaggi;

    // PULSANTI INFO
    @FXML private Button infoRilevazioniButton;
    @FXML private Button infoSintomiButton;
    @FXML private Button infoSegnalazioniButton; 

    // PULSANTI RILEVAZIONI
    @FXML private Button aggiungiRilevazioneButton;
    @FXML private Button precedentiRilevazioniButton;

    // PULSANTI SINTOMI / FARMACI
    @FXML private Button aggiungiSintomoButton;
    @FXML private Button precedentiSintomiButton;

    // PULSANTI SEGNALAZIONI
    @FXML private Button aggiungiSegnalazioneButton;
    @FXML private Button precedentiSegnalazioniButton; 

    // =========================================================
    // INITIALIZE
    // =========================================================

    @FXML
    public void initialize() {
        ruoloLabel.setText("Paziente");
        configuraMessaggi();
        configuraPulsanti();
        
        GestoreAlert.verificaAssunzioniGiornaliere((Paziente) Session.getInstance().getCurrentUser());
        this.messaggi = Database.getInstance().getMessaggiFromPaziente((Paziente) Session.getInstance().getCurrentUser());
        
        aggiornaPallinoNotifiche();
    }


    // =========================================================
    // PROFILO
    // =========================================================

    public void inizializzaProfilo() {
        User user = Session.getInstance().getCurrentUser();
        nomeCognomeLabel.setText(user.getNome() + " " + user.getCognome());
        ruoloLabel.setText("Paziente");
    }
    
    private void aggiornaPallinoNotifiche() {
        boolean messaggiNonLetti =messaggi.stream().anyMatch(messaggio ->!messaggio.isLetto());
        messaggiNotification.setVisible(messaggiNonLetti);
    }


    // =========================================================
    // CONFIGURAZIONE PULSANTI
    // =========================================================
    private void configuraPulsanti() {
    	//PULSANTI
    	aggiungiRilevazioneButton.setOnAction(event -> apriAggiungiRilevazione());
        precedentiRilevazioniButton.setOnAction(event -> apriStoricoRilevazioni());
        aggiungiSintomoButton.setOnAction(event -> apriAggiungiSintomo());
        precedentiSintomiButton.setOnAction(event -> apriStoricoSintomi());
        aggiungiSegnalazioneButton.setOnAction(event -> apriAggiungiSegnalazione());
        precedentiSegnalazioniButton.setOnAction(event -> apriStoricoSegnalazioni());

        //TOOLTIP
        configuraTooltip(
                infoRilevazioniButton,
                "In questa sezione puoi registrare le tue " +
                "rilevazioni giornaliere. Inserisci la data, " +
                "il livello della glicemia e il momento della " +
                "giornata. Con Vedi Precedenti puoi consultare " +
                "tutto lo storico delle rilevazioni e modificare " +
                "quelle già registrate."
        );
        configuraTooltip(
                infoSintomiButton,
                "In questa sezione puoi registrare sintomi " +
                "avvertiti oppure informazioni relative ai " +
                "farmaci. Puoi inserire la data e una descrizione " +
                "libera. Vedi Precedenti permette di consultare " +
                "e modificare lo storico."
        );
        configuraTooltip(
                infoSegnalazioniButton,
                "In questa sezione puoi segnalare al personale " +
                "medico problemi, anomalie o situazioni che " +
                "ritieni importanti. Inserisci la data e descrivi " +
                "liberamente ciò che vuoi comunicare. Puoi anche " +
                "consultare le segnalazioni precedenti."
        );
    }


    // =========================================================
    // TOOLTIP
    // =========================================================
    private void configuraTooltip(Button pulsante,String testo) {
        Tooltip tooltip =new Tooltip(testo);
        tooltip.setWrapText(true);
        tooltip.setMaxWidth(350);
        tooltip.setShowDelay(Duration.millis(100));
        
        Tooltip.install(pulsante,tooltip);
    }
  
    // =========================================================
    // APRI MESSAGGI
    // =========================================================

    private void configuraMessaggi() {
        messaggiButton.setOnAction(event -> apriFinestra("Messaggi.fxml", "Messaggi"));
    }

    // =========================================================
    // AGGIUNGI RILEVAZIONE
    // =========================================================

    @FXML
    private void apriAggiungiRilevazione() {
        apriFinestra("Rilevazione.fxml", "Aggiungi rilevazione");
    }

    // =========================================================
    // AGGIUNGI SINTOMO / FARMACO
    // =========================================================
    
    @FXML
    private void apriAggiungiSintomo() {
        apriFinestra("AssunzioneFarmaco.fxml", "Sintomo / Farmaco");
    }

    // =========================================================
    // AGGIUNGI SEGNALAZIONE
    // =========================================================
    
    @FXML
    private void apriAggiungiSegnalazione() {
        apriFinestra("Segnalazione.fxml", "Nuova segnalazione");
    }
    
    // =========================================================
    // APERTURA FINESTRA
    // =========================================================

    private void apriFinestra(String fxml, String titolo) {
        try {
            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/application/view/" + fxml
                            )
                    );

            Parent root = loader.load();
            Object controller = loader.getController();

            // MESSAGGI
            if (controller instanceof MessaggiController) {
                ((MessaggiController) controller).inizializza(
                        messaggi,
                        this::aggiornaPallinoNotifiche
                );
            }

            // RILEVAZIONE
            if (controller instanceof RilevazioneController) {
                ((RilevazioneController) controller).inizializza(
                        Database.getInstance()::addRilevazione
                );
            }

            // FARMACO / SINTOMO
            if (controller instanceof FarmacoController) {
                ((FarmacoController) controller).inizializza(
                        Database.getInstance()::addAssunzione
                );
            }

            // SEGNALAZIONE
            if (controller instanceof SegnalazioneController) {
                ((SegnalazioneController) controller).inizializza(
                        Database.getInstance()::addSegnalazione
                );
            }

            Stage stage = new Stage();
            stage.setTitle(titolo);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // =========================================================
    // APERTURA STORICI
    // =========================================================
    
    private void apriStorico(List<?> elementi,String tipo,String titolo) {
        try {
            FXMLLoader loader =new FXMLLoader(getClass().getResource("/application/view/Storico.fxml"));
            Parent root =loader.load();
            StoricoController controller =loader.getController();
            controller.inizializza(elementi,tipo);
            
            Stage stage =new Stage();
            stage.setTitle(titolo);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void apriStoricoRilevazioni() {
        apriStorico(
                Database.getInstance().getRilevazioniByPaziente((Paziente) Session.getInstance().getCurrentUser()),
                "rilevazioni",
                "Rilevazioni precedenti"
        );
    }

    @FXML
    private void apriStoricoSintomi() {
        apriStorico(
                Database.getInstance().getAssunzioniByPaziente((Paziente) Session.getInstance().getCurrentUser()),
                "sintomi",
                "Sintomi / Farmaci precedenti"
        );
    }


    @FXML
    private void apriStoricoSegnalazioni() {
        apriStorico(
                Database.getInstance().getSegnalazioniByPaziente( (Paziente) Session.getInstance().getCurrentUser()),
                "segnalazioni",
                "Segnalazioni precedenti"
        );
    }
    
    // =========================================================
    // LOGOUT
    // =========================================================

    @FXML
    private void handleLogout() {
        Session.getInstance().logout();
        List<Window> windows = new ArrayList<>(Window.getWindows());
        for(Window w : windows){
            w.hide();
        }

        try {
            FXMLLoader loader =new FXMLLoader(getClass().getResource("/application/view/Login.fxml"));
            Parent root =loader.load();
            Stage stage =(Stage)logoutButton.getScene().getWindow();
            stage.setScene(new Scene(  root,1200,750));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}