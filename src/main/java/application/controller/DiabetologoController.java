package application.controller;

import application.classiGeneriche.*;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
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

    // MESSAGGI
    @FXML private Button messaggiButton;
    @FXML private ImageView messaggiNotification;

    // PAZIENTI
    @FXML private TextField searchField;
    @FXML private ScrollPane pazientiScrollPane;
    @FXML private VBox pazientiContainer;

    // DATI
    private final List<Paziente> pazienti = new ArrayList<>();
    private final Diabetologo medico = (Diabetologo) Session.getInstance().getCurrentUser();
    private List<Messaggio> messaggi = new ArrayList<>();

    // =========================================================
    // INITIALIZE
    // =========================================================
    
    @FXML
    public void initialize() {
        ruoloLabel.setText("Medico");
        aggiornaListaPazienti();
        configuraRicerca();
        configuraMessaggi();
        GestoreAlert.verificaTuttiIPazienti(medico);
        this.messaggi = Database.getInstance().getMessaggiFromMedico(medico);
        aggiornaPallinoNotifiche();
    }
    
    private void aggiornaPallinoNotifiche() {
        boolean messaggiNonLetti =
                messaggi.stream()
                        .anyMatch(messaggio -> !messaggio.isLetto());

        messaggiNotification.setVisible(messaggiNonLetti);
    }

    // =========================================================
    // PROFILO
    // =========================================================
    
    public void inizializzaProfilo() {
        Database db = Database.getInstance();
        nomeCognomeLabel.setText(medico.getNome() + " " + medico.getCognome());
        ruoloLabel.setText("Medico");
        pazienti.clear();
        pazienti.addAll(db.getPazientiByMedico(medico));
        aggiornaListaPazienti();
    }

    // =========================================================
    // RICERCA
    // =========================================================
    
    private void configuraRicerca() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            aggiornaListaPazienti(newValue);
        });
    }

    // =========================================================
    // LISTA PAZIENTI
    // =========================================================
    
    private void aggiornaListaPazienti() {
        aggiornaListaPazienti("");
    }

    private void aggiornaListaPazienti(String ricerca) {
        pazientiContainer.getChildren().clear();
        ArrayList<Paziente> filteredPazienti = pazienti.stream().filter(p -> {
            String nomeCognome = p.getNome() + " " + p.getCognome();
            if(nomeCognome.toLowerCase().contains(ricerca.toLowerCase().trim())) return true;
            return p.getCodiceFiscale().toLowerCase().contains(ricerca.toLowerCase().trim());
        }).collect(Collectors.toCollection(ArrayList::new));

        for(Paziente p : filteredPazienti){
            pazientiContainer.getChildren().add(creaBoxPaziente(p));
        }
    }

    // =========================================================
    // BOX PAZIENTE
    // =========================================================
    
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
        Region spazio = new Region();
        HBox.setHgrow(spazio, Priority.ALWAYS);

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

    // =========================================================
    // CREAZIONE BOTTONE
    // =========================================================
    
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
    
    // =========================================================
    // APERTURA FINESTRE DI SERVIZIO
    // =========================================================
    
    private void configuraMessaggi() {
        messaggiButton.setOnAction(event -> apriFinestra("Messaggi.fxml", "Messaggi", null));
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
    
    private void apriFinestra(String fxml, String titolo, Paziente paziente) {
        try {
            FXMLLoader loader =new FXMLLoader(getClass().getResource("/application/view/" + fxml));
            Parent root = loader.load();
            Object controller = loader.getController();

            // ANDAMENTO
            if (controller instanceof AndamentoController) {
                ((AndamentoController) controller).inizializzaPaziente(paziente);
            }

            // TERAPIA
            if (controller instanceof StoricoTerapieController) {
                ((StoricoTerapieController) controller).inizializza(paziente, medico);
            }

            // INFORMAZIONI PAZIENTE
            if (controller instanceof InfoPazienteController) {
                ((InfoPazienteController) controller).inizializzaPaziente(paziente);
            }

            // MESSAGGI
            if (controller instanceof MessaggiController) {
                ((MessaggiController) controller).inizializza(messaggi,this::aggiornaPallinoNotifiche);
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
    // LOGOUT
    // =========================================================
    
    @FXML
    private void handleLogout() {
        Session.getInstance().logout();
        List<Window> windows = new ArrayList<>(Window.getWindows());
        for(Window w: windows){
            w.hide();
        }

        try {
            FXMLLoader loader =new FXMLLoader(getClass().getResource("/application/view/Login.fxml"));
            Parent root =loader.load();
            Stage stage =(Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root,1200,750));
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}