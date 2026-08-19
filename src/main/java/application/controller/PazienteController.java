package application.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import application.classiGeneriche.Chiamata;
import application.classiGeneriche.Messaggio;
import application.classiGeneriche.Rilevazione;
import application.classiGeneriche.Segnalazione;
import application.classiGeneriche.AssunzioneFarmaco;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;


public class PazienteController {


    // =========================================================
    // PROFILO
    // =========================================================

    @FXML
    private ImageView profileImage;

    @FXML
    private Label nomeCognomeLabel;

    @FXML
    private Label ruoloLabel;

    @FXML
    private Button logoutButton;


    // =========================================================
    // MAIL
    // =========================================================

    @FXML
    private AnchorPane mailContainer;

    @FXML
    private Button mailButton;

    @FXML
    private HBox mailMenu;

    @FXML
    private Button telefonoButton;

    @FXML
    private Button messaggioButton;


    // =========================================================
    // DATI PROFILO
    // =========================================================

    private String nomePaziente;
    private String cognomePaziente;


    // =========================================================
    // STORICO RILEVAZIONI
    // =========================================================

    private final List<Rilevazione> rilevazioni =
            new ArrayList<>();


    // =========================================================
    // STORICO SINTOMI / FARMACI
    // =========================================================

    private final List<AssunzioneFarmaco> assunzioniFarmaci =
            new ArrayList<>();


    // =========================================================
    // STORICO SEGNALAZIONI
    // =========================================================

    private final List<Segnalazione> segnalazioni =
            new ArrayList<>();


    // =========================================================
    // MESSAGGI
    // =========================================================

    private final List<Messaggio> messaggi =
            new ArrayList<>();


    // =========================================================
    // CHIAMATE
    // =========================================================

    private final List<Chiamata> chiamate =
            new ArrayList<>();


    // =========================================================
    // NOTIFICHE
    // =========================================================

    @FXML
    private ImageView mailNotification;

    @FXML
    private ImageView messaggioNotification;

    @FXML
    private ImageView telefonoNotification;


    // =========================================================
    // PULSANTI INFO
    // =========================================================

    @FXML
    private Button infoRilevazioniButton;

    @FXML
    private Button infoSintomiButton;

    @FXML
    private Button infoSegnalazioniButton;


    // =========================================================
    // PULSANTI RILEVAZIONI
    // =========================================================

    @FXML
    private Button aggiungiRilevazioneButton;

    @FXML
    private Button precedentiRilevazioniButton;


    // =========================================================
    // PULSANTI SINTOMI / FARMACI
    // =========================================================

    @FXML
    private Button aggiungiSintomoButton;

    @FXML
    private Button precedentiSintomiButton;


    // =========================================================
    // PULSANTI SEGNALAZIONI
    // =========================================================

    @FXML
    private Button aggiungiSegnalazioneButton;

    @FXML
    private Button precedentiSegnalazioniButton;


    // =========================================================
    // TIMER MENU MAIL
    // =========================================================

    private PauseTransition chiusuraMenu;


    // =========================================================
    // INITIALIZE
    // =========================================================

    @FXML
    public void initialize() {

        ruoloLabel.setText("Paziente");


        // -----------------------------------------------------
        // DATI DI PROVA
        // -----------------------------------------------------

        inizializzaMessaggi();

        inizializzaChiamate();


        // -----------------------------------------------------
        // MENU MAIL
        // -----------------------------------------------------

        configuraMenuMail();


        // -----------------------------------------------------
        // PULSANTI
        // -----------------------------------------------------

        configuraPulsanti();


        // -----------------------------------------------------
        // NOTIFICHE
        // -----------------------------------------------------

        aggiornaPallinoNotifiche();
    }


    // =========================================================
    // MESSAGGI DI PROVA
    // =========================================================

    private void inizializzaMessaggi() {

        messaggi.add(
                new Messaggio(
                        "Diabetologo",
                        "Rossi",
                        "Buongiorno, come sta andando il monitoraggio della glicemia?",
                        false
                )
        );


        messaggi.add(
                new Messaggio(
                        "Diabetologo",
                        "Rossi",
                        "Ricordo di effettuare le rilevazioni giornaliere.",
                        true
                )
        );
    }


    // =========================================================
    // CHIAMATE DI PROVA
    // =========================================================

    private void inizializzaChiamate() {

        chiamate.add(
                new Chiamata(
                        "Diabetologo",
                        "Rossi",
                        "Richiesta di contatto per chiarimenti sulla terapia.",
                        false
                )
        );
    }


    // =========================================================
    // PROFILO
    // =========================================================

    public void inizializzaProfilo(
            String nome,
            String cognome) {

        this.nomePaziente = nome;

        this.cognomePaziente = cognome;


        nomeCognomeLabel.setText(
                nome + " " + cognome
        );


        ruoloLabel.setText(
                "Paziente"
        );
    }


    // =========================================================
    // NOTIFICHE
    // =========================================================

    private void aggiornaPallinoNotifiche() {

        boolean messaggiNonLetti =
                messaggi.stream()
                        .anyMatch(
                                messaggio ->
                                        !messaggio.isLetto()
                        );


        boolean chiamateNonLette =
                chiamate.stream()
                        .anyMatch(
                                chiamata ->
                                        !chiamata.isLetta()
                        );


        // -----------------------------------------------------
        // PALLINO MAIL PRINCIPALE
        // -----------------------------------------------------

        mailNotification.setVisible(
                messaggiNonLetti ||
                chiamateNonLette
        );


        // -----------------------------------------------------
        // PALLINO MESSAGGI
        // -----------------------------------------------------

        messaggioNotification.setVisible(
                messaggiNonLetti
        );


        // -----------------------------------------------------
        // PALLINO CHIAMATE
        // -----------------------------------------------------

        telefonoNotification.setVisible(
                chiamateNonLette
        );
    }


    // =========================================================
    // CONFIGURAZIONE PULSANTI
    // =========================================================

    private void configuraPulsanti() {


        // =====================================================
        // RILEVAZIONI - AGGIUNGI
        // =====================================================

        aggiungiRilevazioneButton.setOnAction(
                event -> apriAggiungiRilevazione()
        );


        // =====================================================
        // RILEVAZIONI - PRECEDENTI
        // =====================================================

        precedentiRilevazioniButton.setOnAction(
                event -> apriStoricoRilevazioni()
        );


        // =====================================================
        // SINTOMI / FARMACI - AGGIUNGI
        // =====================================================

        aggiungiSintomoButton.setOnAction(
                event -> apriAggiungiSintomo()
        );


        // =====================================================
        // SINTOMI / FARMACI - PRECEDENTI
        // =====================================================

        precedentiSintomiButton.setOnAction(
                event -> apriStoricoSintomi()
        );


        // =====================================================
        // SEGNALAZIONI - AGGIUNGI
        // =====================================================

        aggiungiSegnalazioneButton.setOnAction(
                event -> apriAggiungiSegnalazione()
        );


        // =====================================================
        // SEGNALAZIONI - PRECEDENTI
        // =====================================================

        precedentiSegnalazioniButton.setOnAction(
                event -> apriStoricoSegnalazioni()
        );


        // =====================================================
        // INFO RILEVAZIONI
        // =====================================================

        configuraTooltip(
                infoRilevazioniButton,
                "In questa sezione puoi registrare le tue " +
                "rilevazioni giornaliere. Inserisci la data, " +
                "il livello della glicemia e il momento della " +
                "giornata. Con Vedi Precedenti puoi consultare " +
                "tutto lo storico delle rilevazioni e modificare " +
                "quelle già registrate."
        );


        // =====================================================
        // INFO SINTOMI / FARMACI
        // =====================================================

        configuraTooltip(
                infoSintomiButton,
                "In questa sezione puoi registrare sintomi " +
                "avvertiti oppure informazioni relative ai " +
                "farmaci. Puoi inserire la data e una descrizione " +
                "libera. Vedi Precedenti permette di consultare " +
                "e modificare lo storico."
        );


        // =====================================================
        // INFO SEGNALAZIONI
        // =====================================================

        configuraTooltip(
                infoSegnalazioniButton,
                "In questa sezione puoi segnalare al personale " +
                "medico problemi, anomalie o situazioni che " +
                "ritieni importanti. Inserisci la data e descrivi " +
                "liberamente ciò che vuoi comunicare. Puoi poi " +
                "consultare le segnalazioni precedenti."
        );
    }


    // =========================================================
    // TOOLTIP
    // =========================================================

    private void configuraTooltip(
            Button pulsante,
            String testo) {

        Tooltip tooltip =
                new Tooltip(testo);


        tooltip.setWrapText(true);

        tooltip.setMaxWidth(350);


        tooltip.setShowDelay(
                Duration.millis(100)
        );


        Tooltip.install(
                pulsante,
                tooltip
        );
    }


    // =========================================================
    // MENU MAIL
    // =========================================================

    private void configuraMenuMail() {

        // -----------------------------------------------------
        // ENTRATA NELLA ZONA MAIL
        // -----------------------------------------------------

        mailContainer.setOnMouseEntered(
                event -> mostraMenuMail()
        );


        // -----------------------------------------------------
        // USCITA DALLA ZONA MAIL
        // -----------------------------------------------------

        mailContainer.setOnMouseExited(
                event -> avviaChiusuraMenu()
        );


        // -----------------------------------------------------
        // ENTRATA NEL MENU
        // -----------------------------------------------------

        mailMenu.setOnMouseEntered(
                event -> annullaChiusuraMenu()
        );


        // -----------------------------------------------------
        // USCITA DAL MENU
        // -----------------------------------------------------

        mailMenu.setOnMouseExited(
                event -> avviaChiusuraMenu()
        );


        // -----------------------------------------------------
        // MESSAGGI
        // -----------------------------------------------------

        messaggioButton.setOnAction(
                event -> apriMessaggi()
        );


        // -----------------------------------------------------
        // CHIAMATE
        // -----------------------------------------------------

        telefonoButton.setOnAction(
                event -> apriChiamate()
        );
    }


    // =========================================================
    // APRI MESSAGGI
    // =========================================================

    private void apriMessaggi() {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/application/view/Messaggi.fxml"
                            )
                    );


            Parent root =
                    loader.load();


            MessaggiController controller =
                    loader.getController();


            controller.inizializza(
                    messaggi,
                    this::aggiornaPallinoNotifiche
            );


            Stage stage =
                    new Stage();


            stage.setTitle(
                    "Messaggi"
            );


            stage.setScene(
                    new Scene(root)
            );


            stage.setResizable(false);


            stage.show();


        } catch (IOException e) {

            e.printStackTrace();
        }
    }


    // =========================================================
    // APRI CHIAMATE
    // =========================================================

    private void apriChiamate() {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/application/view/Chiamate.fxml"
                            )
                    );


            Parent root =
                    loader.load();


            ChiamateController controller =
                    loader.getController();


            controller.inizializza(
                    chiamate,
                    this::aggiornaPallinoNotifiche
            );


            Stage stage =
                    new Stage();


            stage.setTitle(
                    "Chiamate"
            );


            stage.setScene(
                    new Scene(root)
            );


            stage.setResizable(false);


            stage.show();


        } catch (IOException e) {

            e.printStackTrace();
        }
    }


    // =========================================================
    // MOSTRA MENU MAIL
    // =========================================================

    private void mostraMenuMail() {

        annullaChiusuraMenu();


        mailMenu.setManaged(true);

        mailMenu.setVisible(true);

        mailMenu.setOpacity(1);
    }


    // =========================================================
    // NASCONDI MENU MAIL
    // =========================================================

    private void nascondiMenuMail() {

        mailMenu.setVisible(false);

        mailMenu.setManaged(false);
    }


    // =========================================================
    // AVVIA CHIUSURA MENU
    // =========================================================

    private void avviaChiusuraMenu() {

        annullaChiusuraMenu();


        chiusuraMenu =
                new PauseTransition(
                        Duration.millis(500)
                );


        chiusuraMenu.setOnFinished(
                event -> nascondiMenuMail()
        );


        chiusuraMenu.play();
    }


    // =========================================================
    // ANNULLA CHIUSURA MENU
    // =========================================================

    private void annullaChiusuraMenu() {

        if (chiusuraMenu != null) {

            chiusuraMenu.stop();

            chiusuraMenu = null;
        }
    }


    // =========================================================
    // AGGIUNGI RILEVAZIONE
    // =========================================================

    @FXML
    private void apriAggiungiRilevazione() {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/application/view/Rilevazione.fxml"
                            )
                    );


            Parent root =
                    loader.load();


            RilevazioneController controller =
                    loader.getController();


            controller.inizializza(
                    rilevazioni::add
            );


            Stage stage =
                    new Stage();


            stage.setTitle(
                    "Aggiungi rilevazione"
            );


            stage.setScene(
                    new Scene(root)
            );


            stage.setResizable(false);


            stage.show();


        } catch (IOException e) {

            e.printStackTrace();
        }
    }



    // =========================================================
    // AGGIUNGI SINTOMO / FARMACO
    // =========================================================

    @FXML
    private void apriAggiungiSintomo() {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/application/view/AssunzioneFarmaco.fxml"
                            )
                    );


            Parent root =
                    loader.load();


            FarmacoController controller =
                    loader.getController();


            controller.inizializza(
                    assunzioniFarmaci::add
            );


            Stage stage =
                    new Stage();


            stage.setTitle(
                    "Sintomo / Farmaco"
            );


            stage.setScene(
                    new Scene(root)
            );


            stage.setResizable(false);


            stage.show();


        } catch (IOException e) {

            e.printStackTrace();
        }
    }




    // =========================================================
    // AGGIUNGI SEGNALAZIONE
    // =========================================================

    @FXML
    private void apriAggiungiSegnalazione() {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/application/view/Segnalazione.fxml"
                            )
                    );


            Parent root =
                    loader.load();


            SegnalazioneController controller =
                    loader.getController();


            controller.inizializza(
                    segnalazioni::add
            );


            Stage stage =
                    new Stage();


            stage.setTitle(
                    "Nuova segnalazione"
            );


            stage.setScene(
                    new Scene(root)
            );


            stage.setResizable(false);


            stage.show();


        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    // =========================================================
    // LOGOUT
    // =========================================================

    @FXML
    private void handleLogout() {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/application/view/Login.fxml"
                            )
                    );


            Parent root =
                    loader.load();


            Stage stage =
                    (Stage)
                    logoutButton
                            .getScene()
                            .getWindow();


            stage.setScene(
                    new Scene(
                            root,
                            1200,
                            750
                    )
            );


            stage.show();


        } catch (IOException e) {

            e.printStackTrace();
        }
    }
    
    private void apriStorico(
            List<?> elementi,
            String tipo,
            String titolo) {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/application/view/Storico.fxml"
                            )
                    );


            Parent root =
                    loader.load();


            StoricoController controller =
                    loader.getController();


            controller.inizializza(
                    elementi,
                    tipo
            );


            Stage stage =
                    new Stage();


            stage.setTitle(
                    titolo
            );


            stage.setScene(
                    new Scene(root)
            );


            stage.setResizable(false);

            stage.show();


        } catch (IOException e) {

            e.printStackTrace();
        }
    }
    
    @FXML
    private void apriStoricoRilevazioni() {

        apriStorico(
                rilevazioni,
                "rilevazioni",
                "Rilevazioni precedenti"
        );
    }


    @FXML
    private void apriStoricoSintomi() {

        apriStorico(
                assunzioniFarmaci,
                "sintomi",
                "Sintomi / Farmaci precedenti"
        );
    }


    @FXML
    private void apriStoricoSegnalazioni() {

        apriStorico(
                segnalazioni,
                "segnalazioni",
                "Segnalazioni precedenti"
        );
    }
}