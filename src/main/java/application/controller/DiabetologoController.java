package application.controller;

import application.classiGeneriche.*;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DiabetologoController {

    // PROFILO
    @FXML private ImageView profileImage;
    @FXML private Label nomeCognomeLabel;
    @FXML private Label ruoloLabel;
    @FXML private Button logoutButton;

    // MAIL
    @FXML private AnchorPane mailContainer;
    @FXML private Button mailButton;
    @FXML private HBox mailMenu;
    @FXML private Button telefonoButton;
    @FXML private Button messaggioButton;
    @FXML private ImageView messaggioNotification;
    @FXML private ImageView telefonoNotification;

    // PAZIENTI
    @FXML private TextField searchField;
    @FXML private ScrollPane pazientiScrollPane;
    @FXML private VBox pazientiContainer;

    // DATI
    private final List<Paziente> pazienti = new ArrayList<>();
    private Diabetologo medico;
    private final List<Messaggio> messaggi = new ArrayList<>();
    private final List<Chiamata> chiamate = new ArrayList<>();
    @FXML private ImageView mailNotification;

    // TIMER CHIUSURA MENU
    private PauseTransition chiusuraMenu;

    // INITIALIZE
    @FXML
    public void initialize() {
        ruoloLabel.setText("Medico");
        aggiornaListaPazienti();
        configuraRicerca();
        configuraMenuMail();
        inizializzaMessaggi();
        inizializzaChiamate();
        aggiornaPallinoNotifiche();
    }

    private void inizializzaMessaggi() {
        messaggi.add(new Messaggio("Mario", "Rossi", "Buongiorno dottore, volevo chiederle informazioni sulla terapia.", false));
        messaggi.add(new Messaggio("Luca", "Bianchi", "Ho effettuato gli esami richiesti.", true));
        messaggi.add(new Messaggio("Anna", "Verdi", "Quando posso fissare il prossimo controllo?", false));
    }

    private void inizializzaChiamate() {
        chiamate.add(new Chiamata("Giulia", "Romano", "Richiesta di chiarimento sulla terapia.", false));
        chiamate.add(new Chiamata("Marco", "Ferrari", "Problema con il monitoraggio glicemico.", true));
    }

    private void aggiornaPallinoNotifiche() {
        boolean messaggiNonLetti = messaggi.stream().anyMatch(messaggio -> !messaggio.isLetto());
        boolean chiamateNonLette = chiamate.stream().anyMatch(chiamata -> !chiamata.isLetta());

        mailNotification.setVisible(messaggiNonLetti || chiamateNonLette);
        messaggioNotification.setVisible(messaggiNonLetti);
        telefonoNotification.setVisible(chiamateNonLette);
    }

    // PROFILO
    public void inizializzaProfilo(Diabetologo medico) {
        this.medico = medico;
        Database db = Database.getInstance();
        nomeCognomeLabel.setText(medico.getNome() + " " + medico.getCognome());
        ruoloLabel.setText("Medico");
        pazienti.clear();
        pazienti.addAll(db.getPazientiFromMedico(medico));
        aggiornaListaPazienti();
    }

    // RICERCA
    private void configuraRicerca() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            aggiornaListaPazienti(newValue);
        });
    }

    // LISTA PAZIENTI
    private void aggiornaListaPazienti() {
        aggiornaListaPazienti("");
    }

    private void aggiornaListaPazienti(String ricerca) {
        pazientiContainer.getChildren().clear();
        ArrayList<Paziente> filteredPazienti = pazienti.stream().filter(p -> {
            String nomeCognome = p.getNome() + " " + p.getCognome();
            if(nomeCognome.toLowerCase().contains(ricerca.toLowerCase().trim())) return true;
            if(p.getCodiceFiscale().toLowerCase().contains(ricerca.toLowerCase().trim())) return true;
            return false;
        }).collect(Collectors.toCollection(ArrayList::new));

        for(Paziente p : filteredPazienti){
            pazientiContainer.getChildren().add(creaBoxPaziente(p));
        }
    }

    // BOX PAZIENTE
    private HBox creaBoxPaziente(Paziente paziente) {
        HBox box = new HBox();
        box.setSpacing(15);
        box.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        box.getStyleClass().add("patient-box");

        // AVATAR
        ImageView avatar = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/application/images/avatar.png"))));
        avatar.setFitWidth(50);
        avatar.setFitHeight(50);
        avatar.setPreserveRatio(true);

        // NOME
        Label nome = new Label(paziente.getNome() + " " + paziente.getCognome());
        nome.getStyleClass().add("patient-name");

        // SPAZIO
        javafx.scene.layout.Region spazio = new javafx.scene.layout.Region();
        HBox.setHgrow(spazio, javafx.scene.layout.Priority.ALWAYS);

        // BOTTONI
        Button andamento = creaBottone("andamento.png", "Andamento");
        Button terapia = creaBottone("terapia.png", "Terapia");
        Button info = creaBottone("infoPaziente.png", "Info Paziente");

        andamento.setOnAction(event -> apriAndamento(paziente));
        terapia.setOnAction(event -> apriTerapia(paziente));
        info.setOnAction(event -> apriInfoPaziente(paziente));

        box.getChildren().addAll(avatar, nome, spazio, andamento, terapia, info);
        return box;
    }

    // CREAZIONE BOTTONE
    private Button creaBottone(String immagine, String testo) {
        Button button = new Button(testo);
        button.getStyleClass().add("patient-action-button");

        ImageView image = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/application/images/" + immagine))));
        image.setFitWidth(25);
        image.setFitHeight(25);
        image.setPreserveRatio(true);

        button.setGraphic(image);
        return button;
    }

    // MENU MAIL
    private void configuraMenuMail() {
        mailContainer.setOnMouseEntered(event -> mostraMenuMail());
        mailContainer.setOnMouseExited(event -> avviaChiusuraMenu());
        mailMenu.setOnMouseEntered(event -> annullaChiusuraMenu());
        mailMenu.setOnMouseExited(event -> avviaChiusuraMenu());

        messaggioButton.setOnAction(event -> apriMessaggi());
        telefonoButton.setOnAction(event -> apriChiamate());
    }

    private void apriMessaggi() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/Messaggi.fxml"));
            Parent root = loader.load();
            MessaggiController controller = loader.getController();
            controller.inizializza(messaggi, this::aggiornaPallinoNotifiche);

            Stage stage = new Stage();
            stage.setTitle("Messaggi");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void apriChiamate() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/Chiamate.fxml"));
            Parent root = loader.load();
            ChiamateController controller = loader.getController();
            controller.inizializza(chiamate, this::aggiornaPallinoNotifiche);

            Stage stage = new Stage();
            stage.setTitle("Chiamate");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // MOSTRA MENU
    private void mostraMenuMail() {
        annullaChiusuraMenu();
        mailMenu.setManaged(true);
        mailMenu.setVisible(true);
        mailMenu.setOpacity(1);
    }

    // NASCONDI MENU
    private void nascondiMenuMail() {
        mailMenu.setVisible(false);
        mailMenu.setManaged(false);
    }

    // AVVIA CHIUSURA
    private void avviaChiusuraMenu() {
        annullaChiusuraMenu();
        chiusuraMenu = new PauseTransition(Duration.millis(500));
        chiusuraMenu.setOnFinished(event -> nascondiMenuMail());
        chiusuraMenu.play();
    }

    // ANNULLA CHIUSURA
    private void annullaChiusuraMenu() {
        if (chiusuraMenu != null) {
            chiusuraMenu.stop();
            chiusuraMenu = null;
        }
    }

    // LOGOUT
    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 750));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void apriFinestra(String fxml, String titolo, Paziente paziente) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/" + fxml));
            Parent root = loader.load();
            Object controller = loader.getController();

            if (controller instanceof AndamentoController) {
                ((AndamentoController) controller).inizializzaPaziente(paziente);
            }
            if (controller instanceof StoricoTerapieController) {
                ((StoricoTerapieController) controller).inizializza(paziente, medico);
            }
            if (controller instanceof InfoPazienteController) {
                ((InfoPazienteController) controller).inizializzaPaziente(paziente);
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

    private void apriAndamento(Paziente paziente) {
        apriFinestra("Andamento.fxml", "Andamento glicemico", paziente);
    }

    private void apriTerapia(Paziente paziente) {
        apriFinestra("StoricoTerapie.fxml", "Terapia", paziente);
    }

    private void apriInfoPaziente(Paziente paziente) {
        apriFinestra("InfoPaziente.fxml", "Informazioni paziente", paziente);
    }

    // CLASSE PERSONA
    public static class Persona {
        private final String nome;
        private final String cognome;

        public Persona(String nome, String cognome) {
            this.nome = nome;
            this.cognome = cognome;
        }

        public String getNome() { return nome; }
        public String getCognome() { return cognome; }
    }
}