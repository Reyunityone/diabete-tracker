package application.controller;

import javafx.fxml.FXML;
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

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    private final List<User> medici = new ArrayList<>();

    private final List<User> pazienti = new ArrayList<>();

    
    // =========================================================
    // INITIALIZE
    // =========================================================

    @FXML
    public void initialize() {
    	
    	aggiungiMediciPazienti();
    	
        ruoloLabel.setText("Responsabile");

        aggiornaListe();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {aggiornaListe();});
    }
    
    public void aggiungiMediciPazienti() {
    	Diabetologo medicoMarco = new Diabetologo("mariorossi", "mariorossi", "MMMMMM", "Mario","Rossi","mail@gmail.com");
    	Paziente pazienteMatteo=new Paziente("matteobianchi", "matteobianchi", "MMMMMM", "Matteo","Bianchi","mail@gmail.com", null, medicoMarco, null, null, null);
    	
    	medici.add(medicoMarco);
    	pazienti.add(pazienteMatteo);
    }
    
    // =========================================================
    // INIZIALIZZA PROFILO
    // =========================================================

    public void inizializzaProfilo(Responsabile responsabile) {
        this.responsabile = responsabile;

        nomeCognomeLabel.setText(responsabile.getNome() + " " + responsabile.getCognome());

        ruoloLabel.setText("Responsabile");
    }


    private void aggiornaListe() {
        String ricerca = searchField.getText();

        if (ricerca == null) ricerca = "";

        ricerca = ricerca.toLowerCase().trim();

        mediciContainer.getChildren().clear();
        pazientiContainer.getChildren().clear();


        // RICERCA TRA I MEDICI
        for (User medico : medici) {
            if (corrisponde(medico, ricerca)) {
                mediciContainer.getChildren().add(creaBoxPersona(medico, true));
            }
        }


        // RICERCA TRA I PAZIENTI
        for (User paziente : pazienti) {
            if (corrisponde(paziente, ricerca)) {
                pazientiContainer.getChildren().add(creaBoxPersona(paziente, false));
            }
        }
    }


    // =========================================================
    // CORRISPONDENZA PERSONA INSERITA CON LA RICERCA EFFETTUATA
    // =========================================================

    private boolean corrisponde(User persona, String ricerca) {
        if (ricerca.isEmpty()) return true;

        String nome = persona.getNome().toLowerCase();
        String cognome = persona.getCognome().toLowerCase();

        return nome.contains(ricerca)|| cognome.contains(ricerca)|| (nome + " " + cognome).contains(ricerca);
    }


    // =========================================================
    // CREAZIONE BOX PER LA PERSONA
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
        Region spazio =new Region();
        HBox.setHgrow(spazio,Priority.ALWAYS);

        // BOTTONE
        Button cambiaCredenziali =new Button("Cambia credenziali");
        cambiaCredenziali.getStyleClass().add("credentials-button");
        cambiaCredenziali.setOnAction(event ->apriFinestra("modificaCredenziali", true, persona, "/application/view/ModificaCredenziali.fxml"));

        box.getChildren().addAll(avatar,nome,spazio,cambiaCredenziali);

        return box;
    }

    
    // =========================================================
    // HANDLE DI SUPPORTO PER L'FXML
    // =========================================================

    @FXML
    private void handleAggiungiMedico() {
        apriFinestra("aggiungiPersona", true, null, "/application/view/AggiungiPersona.fxml");
    }

    @FXML
    private void handleAggiungiPaziente() {
        apriFinestra("aggiungiPersona", false, null, "/application/view/AggiungiPersona.fxml");
    }


    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/Login.fxml"));
            
            Parent root = loader.load();
            
            Stage stage =(Stage) logoutButton.getScene().getWindow();

            Scene scene =new Scene(root);

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

    private void apriFinestra(String controller, boolean medico, User persona, String percorsoFXML) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(percorsoFXML));

            Parent root = loader.load();
            
            Stage stage = new Stage();

            if(controller.equals("aggiungiPersona")) {
            	AggiungiPersonaController aggiungiPersonaController =loader.getController();
            	aggiungiPersonaController.inizializza(this,medico);
            	stage.setTitle(medico? "Aggiungi medico": "Aggiungi paziente");
            	
            }else if(controller.equals("modificaCredenziali")){
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
    // METODO DI SUPPORTO PER AGGIUNGERE ALLA LISTA IN HOME UNA PERSONA
    // =========================================================

//    public void aggiungiPersona(String nome,String cognome,String credenziali,boolean medico) {
//        Persona persona =new Persona(nome,cognome,credenziali);
//        
//        if (medico) {
//            medici.add(persona);
//
//        } else {
//            pazienti.add(persona);
//        }
//
//        aggiornaListe();
//    }
    
    public List<User> getMedici(){
    	return medici;
    }
    
    public List<User> getPazienti(){
    	return pazienti;
    }
    
    public void aggiungiMedico(String nome, String password, String cognome, String codiceFiscale, String email, String username, List<User> pazientiSelezionati) {
    	User medico = new Diabetologo(username, password, codiceFiscale, nome, cognome, email);
    	
    	//Implementare come il diabetologo accoglie i nuovi pazienti aggiunti
//    	medico.addPazienti(pazientiSelezionati);
    	
    	medici.add(medico);
    	
    	aggiornaListe();
    }
    
    public void aggiungiPaziente(String nome, String password, String cognome, String codiceFiscale, String email, String username, User medicoSelezionato) {
    	pazienti.add(new Paziente(username, password, codiceFiscale, nome, cognome, email, null, (Diabetologo)medicoSelezionato, null, null, null));
    
    	aggiornaListe();
    }


    // =========================================================
    // CLASSE PER RAPPRESENTARE UNA PERSONA
    // =========================================================

//    public static class Persona {
//        private final String nome;
//        private final String cognome;
//        private String credenziali;
//
//        public Persona(String nome,String cognome,String credenziali) {
//            this.nome = nome;
//            this.cognome = cognome;
//            this.credenziali = credenziali;
//        }
//
//        public String getNome() {
//            return nome;
//        }
//
//        public String getCognome() {
//            return cognome;
//        }
//
//        public String getCredenziali() {
//            return credenziali;
//        }
//
//        public void setCredenziali(String credenziali) {
//            this.credenziali = credenziali;
//        }
//    }
    
    // =========================================================
    // MODIFICA DELLE CREDENZIALI DI UNA PERSONA
    // =========================================================

    public void modificaCredenziali(User persona, String password, List<User> pazientiSelezionati, User medicoSelezionato) {
    	persona.setPassword(password);
    	
    	if(persona instanceof Diabetologo) {
    		//metodo da implementare
//    		persona.aggiornaListaPazienti(pazientiSelezionati);
    		
    	}else if(persona instanceof Paziente) {
    		((Paziente) persona).setMedicoDiRiferimento((Diabetologo)medicoSelezionato);
    	}
    }
}