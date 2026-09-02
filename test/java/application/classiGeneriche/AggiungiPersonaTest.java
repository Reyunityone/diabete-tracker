package application.controller;

import application.classiGeneriche.Database;
import application.classiGeneriche.Diabetologo;
import application.classiGeneriche.Paziente;
import application.classiGeneriche.User;

import javafx.application.Platform;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.MenuButton;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;


class AggiungiPersonaTest {

    @TempDir
    Path tempDir;

    private Database db;
    private AggiungiPersonaController controller;
    private ResponsabileController responsabileController;

    private Stage stage;
    private Scene scene;
    private VBox root;
    private Label titoloLabel;
    private TextField nomeField;
    private TextField cognomeField;
    private TextField codiceFiscaleField;
    private TextField emailField;
    private TextField usernameField;
    private PasswordField passwordField;
    private Label assegnazioneLabel;
    private ComboBox<User> medicoComboBox;
    private MenuButton pazientiMenuButton;


    // =========================================================
    // INIZIALIZZAZIONE JAVAFX
    // =========================================================

    @BeforeAll
    static void inizializzaJavaFX() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        try {
            Platform.startup(latch::countDown);
            latch.await();
        } catch (IllegalStateException e) {
            // JavaFX è già stato inizializzato
        }
    }


    // =========================================================
    // SETUP E TEARDOWN
    // =========================================================

    @BeforeEach
    void setUp() throws Exception {
    	db = new Database(tempDir.resolve("test-database.data").toString());
        sostituisciDatabaseSingleton(db);

        controller = new AggiungiPersonaController();
        responsabileController = new ResponsabileController();

        creaComponentiJavaFX();
        collegaComponentiAlController();
        creaScena();
    }


    @AfterEach
    void tearDown() throws Exception {
    	runAndWait(() -> {
            if (stage != null) stage.close();
        });
    	
        sostituisciDatabaseSingleton(null);
    }


    // =========================================================
    // DATABASE DI TEST
    // =========================================================
    
    private void sostituisciDatabaseSingleton(Database nuovoDatabase)throws Exception {
        Field field = Database.class.getDeclaredField("database");
        field.setAccessible(true);
        field.set(null, nuovoDatabase);
    }


    // =========================================================
    // CREA E COLLEGA ELEMENTI JAVAFX
    // =========================================================

    private void creaComponentiJavaFX() {
        runAndWait(() -> {
            titoloLabel = new Label();
            nomeField = new TextField();
            cognomeField = new TextField();
            codiceFiscaleField = new TextField();
            emailField = new TextField();
            usernameField = new TextField();
            passwordField = new PasswordField();
            assegnazioneLabel = new Label();
            medicoComboBox = new ComboBox<>();
            pazientiMenuButton = new MenuButton();
        });
    }


    private void collegaComponentiAlController() throws Exception {
        setField("titoloLabel", titoloLabel);
        setField("nomeField", nomeField);
        setField("cognomeField", cognomeField);
        setField("codiceFiscaleField", codiceFiscaleField);
        setField("emailField", emailField);
        setField("usernameField", usernameField);
        setField("passwordField", passwordField);
        setField("assegnazioneLabel", assegnazioneLabel);
        setField("medicoComboBox", medicoComboBox);
        setField("pazientiMenuButton", pazientiMenuButton);
    }


    private void setField(String nome, Object valore) throws Exception {
        Field field =AggiungiPersonaController.class.getDeclaredField(nome);
        field.setAccessible(true);
        field.set(controller, valore);
    }
    
    private void creaScena() {
        runAndWait(() -> {
            root = new VBox();
            root.getChildren().addAll(titoloLabel,nomeField,cognomeField,codiceFiscaleField,emailField,usernameField,passwordField,assegnazioneLabel,medicoComboBox,pazientiMenuButton);
            
            scene = new Scene(root);
            stage = new Stage();
            stage.setScene(scene);
        });
    }


    // =========================================================
    // INIZIALIZZAZIONE COME MEDICO
    // =========================================================

    @Test
    void inizializzazioneComeMedicoConfiguraCorrettaInterfaccia() {
        runAndWait(() -> {
            controller.inizializza(responsabileController, true);
            assertEquals("Aggiungi medico",titoloLabel.getText());
            assertEquals("Assegna pazienti",assegnazioneLabel.getText());
            assertFalse(medicoComboBox.isVisible());
            assertFalse(medicoComboBox.isManaged());
            assertTrue(pazientiMenuButton.isVisible());
            assertTrue(pazientiMenuButton.isManaged());
        });
    }


    // =========================================================
    // INIZIALIZZAZIONE COME PAZIENTE
    // =========================================================

    @Test
    void inizializzazioneComePazienteConfiguraCorrettaInterfaccia() {
        runAndWait(() -> {
            controller.inizializza(responsabileController, false);
            assertEquals("Aggiungi paziente",titoloLabel.getText());
            assertEquals("Assegna medico",assegnazioneLabel.getText());
            assertTrue(medicoComboBox.isVisible());
            assertTrue(medicoComboBox.isManaged());
            assertFalse(pazientiMenuButton.isVisible());
            assertFalse(pazientiMenuButton.isManaged());
        });
    }


    // =========================================================
    // CARICAMENTO MEDICI
    // =========================================================
    @Test
    void inizializzazionePazienteCaricaIMediciNelComboBox() {
        Diabetologo medico1 =new Diabetologo("medico1","password","CFMED01","Mario","Rossi","mario@test.it");
        Diabetologo medico2 =new Diabetologo("medico2","password","CFMED02","Luca","Bianchi","luca@test.it");

        db.addDiabetologo(medico1);
        db.addDiabetologo(medico2);

        runAndWait(() -> {
            controller.inizializza(responsabileController,false);
            assertEquals(2,medicoComboBox.getItems().size());
            assertTrue(medicoComboBox.getItems().contains(medico1));
            assertTrue(medicoComboBox.getItems().contains(medico2));
        });
    }


    // =========================================================
    // CARICAMENTO PAZIENTI
    // =========================================================

    @Test
    void inizializzazioneMedicoCaricaIPazientiNelMenuButton() {
        Paziente p1 =new Paziente("paziente1","password","CFPAZ01","Anna","Verdi","anna@test.it",null,new Diabetologo(),null,null,null);
        Paziente p2 =new Paziente("paziente2","password","CFPAZ02","Paolo","Neri","paolo@test.it",null,new Diabetologo(),null,null,null);

        db.addPaziente(p1);
        db.addPaziente(p2);

        runAndWait(() -> {
            controller.inizializza(responsabileController,true);
            assertEquals(2,pazientiMenuButton.getItems().size());
            
            for (MenuItem item :pazientiMenuButton.getItems()) {
                assertTrue(item instanceof CustomMenuItem);
                CustomMenuItem customItem =(CustomMenuItem) item;
                assertTrue(customItem.getContent()instanceof CheckBox);
            }
        });
    }


    // =========================================================
    // CHECKBOX PAZIENTI
    // =========================================================

    @Test
    void checkboxPazienteContieneIlPazienteComeUserData() {
        Paziente paziente =new Paziente("pazienteCheck","password","CFCHECK","Mario","Rossi","mario@test.it",null,new Diabetologo(),null,null,null);
        db.addPaziente(paziente);

        runAndWait(() -> {
            controller.inizializza(responsabileController,true);

            CustomMenuItem item =(CustomMenuItem)pazientiMenuButton.getItems().getFirst();

            CheckBox checkBox =(CheckBox) item.getContent();

            assertEquals("Mario Rossi",checkBox.getText());
            assertEquals(paziente,checkBox.getUserData());
            assertFalse(checkBox.isSelected());
        });
    }


    // =========================================================
    // INSERIMENTO MEDICO
    // =========================================================

    @Test
    void handleConfermaInserisceUnNuovoMedico() throws Exception {
        preparaCampi("Mario","Rossi","CFMED100","mario@test.it","mario100","password");

        runAndWait(() -> {
            controller.inizializza(responsabileController,true);
            assertDoesNotThrow(() -> controller.handleConferma());
        });
        
        Diabetologo medico =db.getDiabetologi().getFirst();
        assertEquals("Mario", medico.getNome());
        assertEquals("Rossi", medico.getCognome());
        assertEquals("CFMED100", medico.getCodiceFiscale());
        assertEquals("mario@test.it", medico.getEmail());
        assertEquals("mario100", medico.getUsername());
        assertEquals("password", medico.getPassword());
    }


    // =========================================================
    // INSERIMENTO MEDICO + PAZIENTE ASSOCIATO
    // =========================================================

    @Test
    void handleConfermaMedicoAssociaIPazientiSelezionati() throws Exception {
        Paziente paziente =new Paziente("pazienteAssociato","password","CFPAZ100","Anna","Verdi","anna@test.it",null,new Diabetologo(),null,null,null);
        db.addPaziente(paziente);

        preparaCampi("Mario","Rossi","CFMED101","mario101@test.it","medico101","password");

        runAndWait(() -> {
            controller.inizializza(responsabileController,true);

            CustomMenuItem item =(CustomMenuItem)pazientiMenuButton.getItems().getFirst();

            CheckBox checkBox =(CheckBox) item.getContent();

            checkBox.setSelected(true);
            assertDoesNotThrow(() -> controller.handleConferma());
        });

        Diabetologo medico =db.getDiabetologi().getFirst();

        Paziente risultato =db.getPazienti().stream().filter(p ->p.getUsername().equals("pazienteAssociato")).findFirst().orElseThrow();
        assertEquals(medico,risultato.getMedicoDiRiferimento());
    }


    // =========================================================
    // INSERIMENTO PAZIENTE
    // =========================================================

    @Test
    void handleConfermaInserisceUnNuovoPaziente() throws Exception {
        Diabetologo medico =new Diabetologo("medicoPaz","password","CFMED102","Mario","Rossi","medico@test.it");
        db.addDiabetologo(medico);

        preparaCampi("Anna","Verdi","CFPAZ102","anna@test.it","anna102","password");

        runAndWait(() -> {
            controller.inizializza(responsabileController,false);

            medicoComboBox.setValue(medico);

            assertDoesNotThrow(() -> controller.handleConferma());
        });

        Paziente paziente =db.getPazienti().stream().filter(p ->p.getUsername().equals("anna102")).findFirst().orElseThrow();
        assertEquals("Anna",paziente.getNome());
        assertEquals("Verdi",paziente.getCognome());
        assertEquals(medico,paziente.getMedicoDiRiferimento());
    }


    // =========================================================
    // CAMPO OBBLIGATORIO
    // =========================================================

    @Test
    void handleConfermaNonInserisceSeMancaUnCampo() throws Exception {
        preparaCampi("","Rossi","CFMED103","mail@test.it","utente103","password");

        runAndWait(() -> {
            controller.inizializza(responsabileController,true);
            assertDoesNotThrow(() -> controller.handleConferma());
        });

        assertTrue(db.getDiabetologi().isEmpty());
    }


    // =========================================================
    // USERNAME DUPLICATO
    // =========================================================

    @Test
    void handleConfermaNonInserisceUsernameGiaEsistente()throws Exception {
        Diabetologo esistente =new Diabetologo("utenteDuplicato","password","CFOLD","Mario","Rossi","old@test.it");

        db.addDiabetologo(esistente);

        preparaCampi("Luca","Bianchi","CFNEW","new@test.it","utenteDuplicato","nuovaPassword");

        runAndWait(() -> {
            controller.inizializza(responsabileController,true);
            assertDoesNotThrow(() -> controller.handleConferma());
        });

        assertEquals(1,db.getDiabetologi().size());
        assertEquals("Mario",db.getDiabetologi().getFirst().getNome());
    }


    // =========================================================
    // PAZIENTE SENZA MEDICO
    // =========================================================

    @Test
    void handleConfermaNonInseriscePazienteSenzaMedico()throws Exception {
        preparaCampi("Anna","Verdi","CFPAZ104","anna104@test.it","anna104","password");

        runAndWait(() -> {
            controller.inizializza(responsabileController,false);
            medicoComboBox.setValue(null);
            assertDoesNotThrow(() -> controller.handleConferma());
        });

        assertTrue(db.getPazienti().isEmpty());
    }


    // =========================================================
    // SUPPORTO
    // =========================================================

    private void preparaCampi(String nome,String cognome,String codiceFiscale,String email,String username,String password) {
        runAndWait(() -> {
            nomeField.setText(nome);
            cognomeField.setText(cognome);
            codiceFiscaleField.setText(codiceFiscale);
            emailField.setText(email);
            usernameField.setText(username);
            passwordField.setText(password);
        });
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