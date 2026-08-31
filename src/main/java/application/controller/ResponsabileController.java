package application.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import application.classiGeneriche.Database;
import application.classiGeneriche.Diabetologo;
import application.classiGeneriche.Paziente;
import application.classiGeneriche.Responsabile;
import application.classiGeneriche.User;

public class ResponsabileController {

    // =========================================================
    // ELEMENTI FXML: PROFILO
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
    // ELEMENTI FXML: COMPONENTI
    // =========================================================

    @FXML
    private TextField searchField;

    @FXML
    private ScrollPane mediciScrollPane;

    @FXML
    private ScrollPane pazientiScrollPane;

    @FXML
    private VBox mediciContainer;

    @FXML
    private VBox pazientiContainer;


    // =========================================================
    // DATI
    // =========================================================

    private Responsabile responsabile;


    // =========================================================
    // INITIALIZE
    // =========================================================

    @FXML
    public void initialize() {
        ruoloLabel.setText("Responsabile");

        aggiornaListe();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> aggiornaListe());
    }


    // =========================================================
    // INIZIALIZZA PROFILO
    // =========================================================

    public void inizializzaProfilo(Responsabile responsabile) {

        this.responsabile = responsabile;

        nomeCognomeLabel.setText(responsabile.getNome() + " " + responsabile.getCognome());

        ruoloLabel.setText("Responsabile");
    }


    // =========================================================
    // AGGIORNA LISTE
    // =========================================================

    public void aggiornaListe() {

        String ricerca = searchField.getText();

        if (ricerca == null)ricerca = "";

        ricerca = ricerca.toLowerCase().trim();

        mediciContainer.getChildren().clear();
        pazientiContainer.getChildren().clear();

        List<Diabetologo> medici =Database.getInstance().getDiabetologi();

        List<Paziente> pazienti =Database.getInstance().getPazienti();

        // =====================================================
        // RICERCA TRA I MEDICI
        // =====================================================

        for (Diabetologo medico : medici) {
            if (corrisponde(medico, ricerca)) {
                mediciContainer.getChildren().add(creaBoxPersona(medico, true));
            }
        }

        // =====================================================
        // RICERCA TRA I PAZIENTI
        // =====================================================

        for (Paziente paziente : pazienti) {
            if (corrisponde(paziente, ricerca)) {
                pazientiContainer.getChildren().add(creaBoxPersona(paziente, false));
            }
        }
    }


    // =========================================================
    // CORRISPONDENZA CON LA RICERCA
    // =========================================================

    private boolean corrisponde(User persona, String ricerca) {
        if (ricerca.isEmpty()) return true;

        String nome = persona.getNome().toLowerCase();
        String cognome = persona.getCognome().toLowerCase();

        return nome.contains(ricerca)|| cognome.contains(ricerca)|| (nome + " " + cognome).contains(ricerca);
    }


    // =========================================================
    // CREAZIONE BOX PERSONA
    // =========================================================
    private HBox creaBoxPersona(User persona, boolean medico) {
        HBox box = new HBox();
        box.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        box.setSpacing(15);
        box.getStyleClass().add("person-box");

        // AVATAR
        ImageView avatar = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/application/images/avatar.png")).toExternalForm()));
        avatar.setFitWidth(45);
        avatar.setFitHeight(45);
        avatar.setPreserveRatio(true);

        // NOME E COGNOME
        Label nome = new Label(persona.getNome() + " " + persona.getCognome());
        nome.getStyleClass().add("person-name");

        // SPAZIO
        Region spazio = new Region();

        HBox.setHgrow(spazio, Priority.ALWAYS);

        // BOTTONE CAMBIA CREDENZIALI
        Button cambiaCredenziali =new Button("Cambia credenziali");
        cambiaCredenziali.getStyleClass().add("credentials-button");
        cambiaCredenziali.setOnAction(event -> apriFinestra("modificaCredenziali",medico,persona,"/application/view/ModificaCredenziali.fxml"));

        // BOTTONE ELIMINA ACCOUNT
        Button eliminaAccount =new Button("Elimina account");
        eliminaAccount.getStyleClass().add("delete-button");
        eliminaAccount.setOnAction(event -> confermaEliminazione(persona, medico));

        // AGGIUNTA ELEMENTI AL BOX
        box.getChildren().addAll(avatar,nome,spazio,cambiaCredenziali,eliminaAccount);

        return box;
    }


    // =========================================================
    // CONFERMA ELIMINAZIONE ACCOUNT
    // =========================================================
    private void confermaEliminazione(User persona, boolean medico) {
    	
    	//CONTROLLO SE É POSSIBILE ELIMINARE UN DIABETOLOGO
    	if (medico) {
    	    Diabetologo diabetologo =(Diabetologo) persona;
    	    List<Paziente> pazientiSeguiti =Database.getInstance().getPazientiByMedico(diabetologo);

    	    if (!pazientiSeguiti.isEmpty()) {
    	    	System.out.println("Impossibile eliminare un diabetologo che segue dei pazienti");
    	        return;
    	    }
    	}
    	
    	//CREAZIONE ALR+ERT PER ELIMINAZIONE
        Optional<ButtonType> risultato = getRisultato(persona);

        //CHIAMO IL DB PER AGGIORNAMENTO LISTE
        if (risultato.isPresent()&& risultato.get() == ButtonType.OK) {
            if (medico) {
                Database.getInstance().deleteDiabetologo((Diabetologo) persona);

            } else {
                Database.getInstance().deletePaziente((Paziente) persona);
            }

            aggiornaListe();
        }
    }

    private static Optional<ButtonType> getRisultato(User persona) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        alert.setTitle("Eliminazione account");
        alert.setHeaderText("Eliminare questo account?");
        alert.setContentText(
                "Stai per eliminare l'account di "
                        + persona.getNome()
                        + " "
                        + persona.getCognome()
                        + ".\n\n"
                        + "Questa operazione non può essere annullata."
        );


        return alert.showAndWait();
    }


    // =========================================================
    // HANDLE AGGIUNTA MEDICO
    // =========================================================
    @FXML
    private void handleAggiungiMedico() {
        apriFinestra("aggiungiPersona",true,null,"/application/view/AggiungiPersona.fxml");
    }

    // =========================================================
    // HANDLE AGGIUNTA PAZIENTE
    // =========================================================
    @FXML
    private void handleAggiungiPaziente() {
        apriFinestra("aggiungiPersona",false,null,"/application/view/AggiungiPersona.fxml");
    }

    // =========================================================
    // LOGOUT
    // =========================================================
    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader =new FXMLLoader(getClass().getResource("/application/view/Login.fxml"));

            Parent root = loader.load();

            Stage stage =(Stage) logoutButton.getScene().getWindow();

            Scene scene = new Scene(root);

            stage.setScene(scene);

            stage.setWidth(1200);
            stage.setHeight(750);

            stage.show();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    // =========================================================
    // APERTURA FINESTRA GENERICA
    // =========================================================
    private void apriFinestra(String controller,boolean medico,User persona,String percorsoFXML) {
        try {
            FXMLLoader loader =new FXMLLoader(getClass().getResource(percorsoFXML));

            Parent root = loader.load();

            Stage stage = new Stage();


            // AGGIUNTA PERSONA
            if (controller.equals("aggiungiPersona")) {
                AggiungiPersonaController aggiungiPersonaController =loader.getController();
                aggiungiPersonaController.inizializza(this,medico);

                stage.setTitle(medico? "Aggiungi medico": "Aggiungi paziente");
            }

            // MODIFICA CREDENZIALI
            else if (controller.equals("modificaCredenziali")) {
                ModificaCredenzialiController modificaCredenzialiController =loader.getController();
                modificaCredenzialiController.inizializza(this,persona);
                stage.setTitle("Modifica credenziali");
            }

            stage.setScene(new Scene(root));

            stage.initModality(Modality.APPLICATION_MODAL);

            stage.setResizable(false);

            stage.showAndWait();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }


    // =========================================================
    // GET MEDICI
    // =========================================================
    public List<Diabetologo> getMedici() {
        return Database.getInstance().getDiabetologi();
    }


    // =========================================================
    // GET PAZIENTI
    // =========================================================
    public List<Paziente> getPazienti() {
        return Database.getInstance().getPazienti();
    }
}