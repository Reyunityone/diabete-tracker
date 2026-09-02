package application.controller;

import application.classiGeneriche.Database;
import application.classiGeneriche.Diabetologo;
import application.classiGeneriche.Paziente;
import application.classiGeneriche.Responsabile;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;


class ResponsabileControllerTest {

    @TempDir
    Path tempDir;

    private Database db;
    private ResponsabileController controller;

    private ImageView profileImage;
    private Label nomeCognomeLabel;
    private Label ruoloLabel;
    private Button logoutButton;

    private TextField searchField;
    private ScrollPane mediciScrollPane;
    private ScrollPane pazientiScrollPane;
    private VBox mediciContainer;
    private VBox pazientiContainer;


    // =========================================================
    // INIZIALIZZAZIONE JAVAFX
    // =========================================================

    @BeforeAll
    static void inizializzaJavaFX()throws InterruptedException {
        CountDownLatch latch =new CountDownLatch(1);
        try {
            Platform.startup(latch::countDown);
            latch.await();
        } catch (IllegalStateException e) {
            // JavaFX già inizializzato
        }
    }


    // =========================================================
    // SETUP E TEARDOWN
    // =========================================================

    @BeforeEach
    void setUp() throws Exception {
    	db = new Database(tempDir.resolve("test-database.data").toString());
        sostituisciDatabaseSingleton(db);

        controller =new ResponsabileController();

        creaComponentiJavaFX();

        collegaComponentiAlController();
    }


    @AfterEach
    void tearDown() throws Exception {
        sostituisciDatabaseSingleton(null);
    }


    // =========================================================
    // DATABASE DI TEST
    // =========================================================


    private void sostituisciDatabaseSingleton(Database nuovoDatabase) throws Exception {
        Field field =Database.class.getDeclaredField("database");

        field.setAccessible(true);

        field.set(null, nuovoDatabase);
    }


    // =========================================================
    // CREA E COLLEGA COMPONENTI JAVAFX
    // =========================================================

    private void creaComponentiJavaFX() {
        runAndWait(() -> {
            profileImage =new ImageView();
            nomeCognomeLabel =new Label();
            ruoloLabel =new Label();
            logoutButton =new Button();
            searchField =new TextField();
            mediciScrollPane =new ScrollPane();
            pazientiScrollPane =new ScrollPane();
            mediciContainer =new VBox();
            pazientiContainer =new VBox();
        });
    }


    private void collegaComponentiAlController()throws Exception {
        setField("profileImage",profileImage);
        setField("nomeCognomeLabel",nomeCognomeLabel);
        setField("ruoloLabel",ruoloLabel);
        setField("logoutButton",logoutButton);
        setField("searchField",searchField);
        setField("mediciScrollPane",mediciScrollPane);
        setField("pazientiScrollPane",pazientiScrollPane);
        setField("mediciContainer",mediciContainer);
        setField("pazientiContainer",pazientiContainer);
    }


    private void setField(String nome,Object valore) throws Exception {
        Field field =ResponsabileController.class.getDeclaredField(nome);
        field.setAccessible(true);
        field.set(controller,valore);
    }


    // =========================================================
    // INITIALIZE
    // =========================================================

    @Test
    void initializeImpostaIlRuoloResponsabile() {
        runAndWait(() -> {
            controller.initialize();
            assertEquals("Responsabile",ruoloLabel.getText());
        });
    }


    // =========================================================
    // PROFILO
    // =========================================================

    @Test
    void inizializzaProfiloVisualizzaNomeECognome() {
        Responsabile responsabile =new Responsabile("responsabile","password","CFRESP","Mario","Rossi","mario@test.it");
        
        runAndWait(() -> {
            controller.inizializzaProfilo(responsabile);
            assertEquals("Mario Rossi",nomeCognomeLabel.getText());
            assertEquals("Responsabile",ruoloLabel.getText());
        });
    }


    // =========================================================
    // LISTE SENZA RICERCA
    // =========================================================
    @Test
    void aggiornaListeVisualizzaTuttiGliUtentiQuandoLaRicercaEVuota() {
        aggiungiMedico("medico1","Mario","Rossi");
        aggiungiMedico("medico2","Luca","Bianchi");
        aggiungiPaziente("paziente1","Anna","Verdi");

        runAndWait(() -> {
            searchField.setText("");
            controller.initialize();
            assertEquals(2,mediciContainer.getChildren().size());
            assertEquals(1,pazientiContainer.getChildren().size());
        });
    }


    // =========================================================
    // RICERCA PER NOME
    // =========================================================

    @Test
    void ricercaPerNomeMostraSoloLaPersonaCorrispondente() {
        aggiungiMedico("medico1","Mario","Rossi");
        aggiungiMedico("medico2","Luca","Bianchi");

        runAndWait(() -> {
            searchField.setText("Mario");
            controller.initialize();
            assertEquals(1,mediciContainer.getChildren().size());
            assertEquals("Mario Rossi",nomeDelBox(mediciContainer.getChildren().getFirst()));
        });
    }


    // =========================================================
    // RICERCA PER COGNOME
    // =========================================================

    @Test
    void ricercaPerCognomeMostraLaPersonaCorrispondente() {
        aggiungiMedico("medico1","Mario","Rossi");
        aggiungiMedico("medico2","Luca","Bianchi");

        runAndWait(() -> {
            searchField.setText("Bianchi");
            controller.initialize();
            assertEquals(1,mediciContainer.getChildren().size());
            assertEquals("Luca Bianchi",nomeDelBox(mediciContainer.getChildren().getFirst()));
        });
    }


    // =========================================================
    // RICERCA CASE INSENSITIVE
    // =========================================================

    @Test
    void ricercaNonFaDifferenzaTraMaiuscoleEMinuscole() {
        aggiungiMedico("medico1","Mario","Rossi");

        runAndWait(() -> {
            searchField.setText("mArIo");
            controller.initialize();
            assertEquals(1,mediciContainer.getChildren().size());
        });
    }


    // =========================================================
    // RICERCA NOME + COGNOME
    // =========================================================

    @Test
    void ricercaPerNomeECognomeFunziona() {
        aggiungiPaziente("paziente","Anna","Verdi");

        runAndWait(() -> {
            searchField.setText("Anna Verdi");
            controller.initialize();
            assertEquals(1,pazientiContainer.getChildren().size());
        });
    }


    // =========================================================
    // RICERCA SENZA RISULTATI
    // =========================================================

    @Test
    void ricercaSenzaRisultatiLasciaVuotiIContainer() {
        aggiungiMedico("medico","Mario","Rossi");
        aggiungiPaziente("paziente","Anna","Verdi");
        
        runAndWait(() -> {
            searchField.setText("PersonaInesistente");
            controller.initialize();
            assertTrue(mediciContainer.getChildren().isEmpty());
            assertTrue(pazientiContainer.getChildren().isEmpty());
        });
    }


    // =========================================================
    // LISTENER DELLA RICERCA
    // =========================================================

    @Test
    void modificaDelTestoDiRicercaAggiornaAutomaticamenteLeListe() {
        aggiungiMedico("medico","Mario","Rossi");

        runAndWait(() -> {
            searchField.setText("");
            controller.initialize();
            assertEquals(1,mediciContainer.getChildren().size());
            
            searchField.setText("inesistente");
            assertTrue(mediciContainer.getChildren().isEmpty());
            
            searchField.setText("Mario");
            assertEquals(1,mediciContainer.getChildren().size());
        });
    }


    // =========================================================
    // STRUTTURA BOX PERSONA
    // =========================================================

    @Test
    void boxPersonaContieneAvatarNomeEModificaElimina() {
        aggiungiMedico("medico","Mario","Rossi");

        runAndWait(() -> {
            controller.initialize();
            assertEquals(1,mediciContainer.getChildren().size());

            HBox box =(HBox)mediciContainer.getChildren().getFirst();
            assertTrue(box.getChildren().stream().anyMatch(node ->node instanceof ImageView));
            assertTrue(box.getChildren().stream().anyMatch(node ->node instanceof Label));
            assertTrue(box.getChildren().stream().anyMatch(node ->node instanceof Button&& ((Button) node).getText().equals("Cambia credenziali")));
            assertTrue(box.getChildren().stream().anyMatch(node ->node instanceof Button&& ((Button) node).getText().equals("Elimina account")));
        });
    }


    // =========================================================
    // SUPPORTO
    // =========================================================

    private void aggiungiMedico(String username,String nome,String cognome) {
        db.addDiabetologo(new Diabetologo(username,"password","CF" + username,nome,cognome,username + "@test.it"));
    }


    private void aggiungiPaziente(String username,String nome,String cognome) {
        db.addPaziente(new Paziente(username,"password","CF" + username,nome,cognome,username + "@test.it",null,new Diabetologo(),null,null,null));
    }


    private String nomeDelBox(Node node) {
        HBox box = (HBox) node;
        return box.getChildren().stream().filter(elemento ->elemento instanceof Label).map(elemento ->((Label) elemento).getText()).findFirst().orElse(null);
    }

    /*
     * Esegue il codice sul JavaFX Application Thread e attende che venga completato.
     *
     * È necessario perché i componenti grafici JavaFX (TextField, ListView,
     * ComboBox, ecc.) possono essere letti o modificati in sicurezza solo
     * dal thread principale di JavaFX.
     *
     * Nei test, invece, il codice può essere eseguito da un thread diverso
     * dal JavaFX Application Thread. Per questo motivo utilizziamo
     * Platform.runLater() per spostare l'esecuzione sul thread corretto
     * e CountDownLatch per attendere che l'operazione sia terminata prima
     * di proseguire.
     *
     * In questo modo il metodo chiamante non continua l'esecuzione mentre
     * la GUI sta ancora aggiornando i suoi componenti.
     */
    private void runAndWait(Runnable runnable) {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
            return;
        }

        CountDownLatch latch =new CountDownLatch(1);

        AtomicReference<Throwable> errore =new AtomicReference<>();

        Platform.runLater(() -> {
            try {
                runnable.run();
            } catch (Throwable e) {
                errore.set(e);
            } finally {
                latch.countDown();
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        if (errore.get() != null) {
            throw new RuntimeException(
                    errore.get()
            );
        }
    }
}