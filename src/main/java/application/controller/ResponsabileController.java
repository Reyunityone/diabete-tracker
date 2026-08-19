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

public class ResponsabileController {

    @FXML
    private ImageView profileImage;

    @FXML
    private Label nomeCognomeLabel;

    @FXML
    private Label ruoloLabel;

    @FXML
    private Button logoutButton;

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


    // Liste temporanee.
    // In futuro arriveranno dal database.

    private final List<Persona> medici = new ArrayList<>();

    private final List<Persona> pazienti = new ArrayList<>();


    @FXML
    public void initialize() {

        // Dati temporanei di prova

    	medici.add(
    	        new Persona(
    	                "Mario",
    	                "Rossi",
    	                "password"
    	        )
    	);

    	medici.add(
    	        new Persona(
    	                "Luca",
    	                "Bianchi",
    	                "password"
    	        )
    	);

    	medici.add(
    	        new Persona(
    	                "Anna",
    	                "Verdi",
    	                "password"
    	        )
    	);

    	pazienti.add(
    	        new Persona(
    	                "Marco",
    	                "Rossi",
    	                "password"
    	        )
    	);

    	pazienti.add(
    	        new Persona(
    	                "Giulia",
    	                "Bianchi",
    	                "password"
    	        )
    	);

    	pazienti.add(
    	        new Persona(
    	                "Andrea",
    	                "Verdi",
    	                "password"
    	        )
    	);


        // Informazioni del responsabile
        ruoloLabel.setText("Responsabile");


        // Costruzione iniziale delle liste

        aggiornaListe();


        // Ricerca dinamica

        searchField.textProperty().addListener(
                (observable, oldValue, newValue) -> {

                    aggiornaListe();

                }
        );
    }


    /**
     * Aggiorna entrambe le liste in base alla ricerca.
     */
    private void aggiornaListe() {

        String ricerca = searchField.getText();

        if (ricerca == null) {
            ricerca = "";
        }

        ricerca = ricerca.toLowerCase().trim();


        mediciContainer.getChildren().clear();
        pazientiContainer.getChildren().clear();


        // MEDICI

        for (Persona medico : medici) {

            if (corrisponde(medico, ricerca)) {

                mediciContainer.getChildren().add(
                        creaBoxPersona(medico, true)
                );

            }

        }


        // PAZIENTI

        for (Persona paziente : pazienti) {

            if (corrisponde(paziente, ricerca)) {

                pazientiContainer.getChildren().add(
                        creaBoxPersona(paziente, false)
                );

            }

        }
    }


    /**
     * Controlla se nome o cognome corrispondono
     * al testo inserito nella ricerca.
     */
    private boolean corrisponde(Persona persona, String ricerca) {

        if (ricerca.isEmpty()) {
            return true;
        }

        String nome = persona.getNome().toLowerCase();
        String cognome = persona.getCognome().toLowerCase();

        return nome.contains(ricerca)
                || cognome.contains(ricerca)
                || (nome + " " + cognome).contains(ricerca);
    }


    /**
     * Crea graficamente il box di un medico o paziente.
     */
    private HBox creaBoxPersona(Persona persona, boolean medico) {

        HBox box = new HBox();

        box.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        box.setSpacing(15);

        box.getStyleClass().add("person-box");


        // AVATAR

        ImageView avatar = new ImageView(
                new Image(
                        Objects.requireNonNull(getClass()
                                        .getResource("/application/images/avatar.png"))
                                .toExternalForm()
                )
        );

        avatar.setFitWidth(45);
        avatar.setFitHeight(45);
        avatar.setPreserveRatio(true);


        // NOME E COGNOME

        Label nome = new Label(
                persona.getNome() + " " + persona.getCognome()
        );

        nome.getStyleClass().add("person-name");


        // SPAZIO

        javafx.scene.layout.Region spazio =
                new javafx.scene.layout.Region();

        HBox.setHgrow(
                spazio,
                Priority.ALWAYS
        );


        // BOTTONE

        Button cambiaCredenziali =
                new Button("Cambia credenziali");

        cambiaCredenziali.getStyleClass().add(
                "credentials-button"
        );
        
        cambiaCredenziali.setOnAction(event ->
        apriFinestraCredenziali(persona)
        		);


        box.getChildren().addAll(
                avatar,
                nome,
                spazio,
                cambiaCredenziali
        );


        return box;
    }


    @FXML
    private void handleAggiungiMedico() {

        apriFinestraAggiunta(true);
    }


    @FXML
    private void handleAggiungiPaziente() {

        apriFinestraAggiunta(false);
    }


    @FXML
    private void handleLogout() {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/application/view/Login.fxml"
                    )
            );

            Parent root = loader.load();


            Stage stage =
                    (Stage) logoutButton
                            .getScene()
                            .getWindow();


            Scene scene =
                    new Scene(root);


            stage.setScene(scene);

            stage.setWidth(1200);

            stage.setHeight(750);

            stage.show();


        } catch (IOException e) {

            e.printStackTrace();
        }
    }
    
    private void apriFinestraAggiunta(boolean medico) {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/application/view/AggiungiPersona.fxml"
                    )
            );

            Parent root = loader.load();


            AggiungiPersonaController controller =
                    loader.getController();


            controller.inizializza(
                    this,
                    medico
            );


            Stage stage = new Stage();

            stage.setTitle(
                    medico
                            ? "Aggiungi medico"
                            : "Aggiungi paziente"
            );

            stage.setScene(
                    new Scene(root)
            );

            stage.initModality(
                    Modality.APPLICATION_MODAL
            );

            stage.setResizable(false);

            stage.showAndWait();


        } catch (IOException e) {

            e.printStackTrace();
        }
    }
    
    public void aggiungiPersona(
            String nome,
            String cognome,
            String credenziali,
            boolean medico) {

        Persona persona =
                new Persona(
                        nome,
                        cognome,
                        credenziali
                );


        if (medico) {

            medici.add(persona);

        } else {

            pazienti.add(persona);
        }


        aggiornaListe();
    }


    /**
     * Classe temporanea per rappresentare
     * un medico o un paziente.
     *
     * In futuro verrà sostituita dal modello
     * collegato al database.
     */
    public static class Persona {

        private final String nome;

        private final String cognome;

        private String credenziali;


        public Persona(
                String nome,
                String cognome,
                String credenziali) {

            this.nome = nome;
            this.cognome = cognome;
            this.credenziali = credenziali;
        }


        public String getNome() {
            return nome;
        }


        public String getCognome() {
            return cognome;
        }


        public String getCredenziali() {
            return credenziali;
        }


        public void setCredenziali(
                String credenziali) {

            this.credenziali = credenziali;
        }
    }
    
    public void impostaProfilo(String nome, String cognome) {
        nomeCognomeLabel.setText(nome + " " + cognome);
    }
    
    private void apriFinestraCredenziali(
            Persona persona) {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/application/view/ModificaCredenziali.fxml"
                    )
            );

            Parent root = loader.load();


            ModificaCredenzialiController controller =
                    loader.getController();


            controller.inizializza(
                    this,
                    persona
            );


            Stage stage = new Stage();

            stage.setTitle("Modifica credenziali");

            stage.setScene(
                    new Scene(root)
            );

            stage.initModality(
                    Modality.APPLICATION_MODAL
            );

            stage.setResizable(false);

            stage.showAndWait();


        } catch (IOException e) {

            e.printStackTrace();
        }
    }
    
    public void modificaCredenziali(
            Persona persona,
            String vecchieCredenziali,
            String nuoveCredenziali) {


        if (!persona.getCredenziali()
                .equals(vecchieCredenziali)) {

            System.out.println(
                    "Le vecchie credenziali non sono corrette."
            );

            return;
        }


        persona.setCredenziali(
                nuoveCredenziali
        );


        System.out.println(
                "Credenziali modificate."
        );
    }
}