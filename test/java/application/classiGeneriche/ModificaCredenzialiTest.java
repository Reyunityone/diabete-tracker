package application.controller;

import application.classiGeneriche.Database;
import application.classiGeneriche.Diabetologo;
import application.classiGeneriche.Paziente;
import application.classiGeneriche.User;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;


class ModificaCredenzialiTest {

    @TempDir
    Path tempDir;

    private Database db;
    private ModificaCredenzialiController controller;
    private ResponsabileController responsabileController;

    private Stage stage;
    private VBox root;
    private Scene scene;
    private Label titoloLabel;
    private Label personaLabel;
    private TextField nomeField;
    private TextField cognomeField;
    private TextField codiceFiscaleField;
    private TextField emailField;
    private TextField usernameField;
    private TextField passwordField;
    private Label assegnazioneLabel;
    private ComboBox<User> medicoComboBox;
    private MenuButton pazientiMenuButton;


    // =========================================================
    // INIZIZALIZZAZIONE JAVAFX
    // =========================================================

    @BeforeAll
    static void inizializzaJavaFX() throws InterruptedException {
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

        controller =new ModificaCredenzialiController();
        responsabileController =new ResponsabileController();

        creaComponentiJavaFX();
        collegaComponentiAlController();
        creaScena();
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
    // CREA E COLLEGA ELEMENTI JAVAFX
    // =========================================================

    private void creaComponentiJavaFX() {
        runAndWait(() -> {
            titoloLabel = new Label();
            personaLabel = new Label();
            nomeField = new TextField();
            cognomeField = new TextField();
            codiceFiscaleField = new TextField();
            emailField = new TextField();
            usernameField = new TextField();
            passwordField = new TextField();
            assegnazioneLabel = new Label();
            medicoComboBox =new ComboBox<>();
            pazientiMenuButton =new MenuButton();
        });
    }


    private void collegaComponentiAlController()throws Exception {
        setField("titoloLabel", titoloLabel);
        setField("personaLabel", personaLabel);
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


    private void setField(String nome,Object valore) throws Exception {
        Field field =ModificaCredenzialiController.class.getDeclaredField(nome);
        field.setAccessible(true);
        field.set(controller, valore);
    }
    
    private void creaScena() {
        runAndWait(() -> {
            root = new VBox();
            root.getChildren().addAll(titoloLabel,personaLabel, nomeField,cognomeField,codiceFiscaleField,emailField,usernameField,passwordField,assegnazioneLabel,medicoComboBox,pazientiMenuButton);
            
            scene = new Scene(root);
            stage = new Stage();
            stage.setScene(scene);
        });
    }


    // =========================================================
    // INIZIALIZZAZIONE DIABETOLOGO
    // =========================================================

    @Test
    void inizializzazioneDiabetologoPrecompilaICampi()throws Exception {
        Diabetologo medico =new Diabetologo("medicoTest","password123","CFMED01","Mario","Rossi","mario@test.it");
        db.addDiabetologo(medico);

        runAndWait(() -> {
            controller.inizializza(responsabileController,medico);
            assertEquals("Mario Rossi",personaLabel.getText());
            assertEquals("Mario",nomeField.getText());
            assertEquals("Rossi",cognomeField.getText());
            assertEquals("CFMED01",codiceFiscaleField.getText());
            assertEquals("mario@test.it",emailField.getText());
            assertEquals("medicoTest",usernameField.getText());
            assertEquals("password123",passwordField.getText());
        });
    }
    
    // =========================================================
    // INIZIALIZZAZIONE PAZIENTE
    // =========================================================

    @Test
    void inizializzazionePazientePrecompilaICampi()throws Exception {
    	Paziente paziente =new Paziente("pazienteTest","password","CFPAZ03","Anna","Verdi","anna@test.it",null,new Diabetologo(),null,null,null);
    	db.addPaziente(paziente);

        runAndWait(() -> {
            controller.inizializza(responsabileController,paziente);
            assertEquals("Anna Verdi",personaLabel.getText());
            assertEquals("Anna",nomeField.getText());
            assertEquals("Verdi",cognomeField.getText());
            assertEquals("CFPAZ03",codiceFiscaleField.getText());
            assertEquals("anna@test.it",emailField.getText());
            assertEquals("pazienteTest",usernameField.getText());
            assertEquals("password",passwordField.getText());
        });
    }


    // =========================================================
    // CONFIGURAZIONE DIABETOLOGO
    // =========================================================

    @Test
    void inizializzazioneDiabetologoConfiguraCorrettaInterfaccia()throws Exception {
        Diabetologo medico =new Diabetologo();
        db.addDiabetologo(medico);

        runAndWait(() -> {
            controller.inizializza(responsabileController,medico);
            assertEquals("Modifica medico",titoloLabel.getText());
            assertEquals("Assegna pazienti",assegnazioneLabel.getText());
            assertFalse(medicoComboBox.isVisible());
            assertFalse(medicoComboBox.isManaged());
            assertTrue(pazientiMenuButton.isVisible());
            assertTrue(pazientiMenuButton.isManaged());
        });
    }


    // =========================================================
    // CONFIGURAZIONE PAZIENTE
    // =========================================================

    @Test
    void inizializzazionePazienteConfiguraCorrettaInterfaccia()throws Exception {
        Paziente paziente =new Paziente();
        db.addPaziente(paziente);

        runAndWait(() -> {
            controller.inizializza(responsabileController,paziente);
            assertEquals("Modifica paziente",titoloLabel.getText());
            assertEquals("Assegna medico",assegnazioneLabel.getText());
            assertTrue(medicoComboBox.isVisible());
            assertTrue(medicoComboBox.isManaged());
            assertFalse(pazientiMenuButton.isVisible());
            assertFalse(pazientiMenuButton.isManaged());
        });
    }


    // =========================================================
    // PRESELEZIONE MEDICO
    // =========================================================

    @Test
    void inizializzazionePazientePreselezionaIlMedicoAttuale()throws Exception {
        Diabetologo medico =new Diabetologo("medico","password","CFMED02","Mario","Rossi","medico@test.it");
        Paziente paziente =new Paziente("paziente","password","CFPAZ02","Anna","Verdi","anna@test.it",null,medico,null,null,null);

        db.addDiabetologo(medico);
        db.addPaziente(paziente);

        runAndWait(() -> {
            controller.inizializza(responsabileController,paziente);
            assertEquals(medico,medicoComboBox.getValue());
        });
    }


    // =========================================================
    // CHECKBOX PAZIENTI
    // =========================================================

    @Test
    void inizializzazioneDiabetologoSelezionaIPazientiGiaAssociati()throws Exception {
        Diabetologo medico =new Diabetologo("medicoCheck","password","CFMED03","Mario","Rossi","medico@test.it");
        Paziente associato =new Paziente("associato","password","CFPAZ03","Anna","Verdi","anna@test.it",null,medico,null,null,null);
        Paziente nonAssociato =new Paziente("nonAssociato","password","CFPAZ04","Luca","Neri","luca@test.it",null,new Diabetologo(),null,null,null);

        db.addDiabetologo(medico);
        db.addPaziente(associato);
        db.addPaziente(nonAssociato);

        runAndWait(() -> {
            controller.inizializza(responsabileController,medico);
            assertEquals(2,pazientiMenuButton.getItems().size());
            
            for (MenuItemWrapper wrapper :getCheckBoxes()) {
                if (wrapper.paziente().equals(associato)) {
                    assertTrue(wrapper.checkBox().isSelected());
                }

                if (wrapper.paziente().equals(nonAssociato)) {
                    assertFalse(wrapper.checkBox().isSelected());
                }
            }
        });
    }


    // =========================================================
    // MODIFICA DIABETOLOGO
    // =========================================================

    @Test
    void modificaCredenzialiDiabetologoAggiornaTuttiIDati() {
        Diabetologo vecchio =new Diabetologo("vecchio","passwordVecchia","CFVECCHIO","Mario","Rossi","vecchio@test.it");
        db.addDiabetologo(vecchio);
        
        runAndWait(() -> {
        	assertDoesNotThrow(() ->controller.modificaCredenziali(vecchio,"nuovo","passwordNuova","CFNUOVO","Luca","Bianchi","nuovo@test.it",new ArrayList<>(),null));
        });

        assertFalse(db.getDiabetologi().contains(vecchio));

        Diabetologo nuovo =db.getDiabetologi().getFirst();
        assertEquals("nuovo", nuovo.getUsername());
        assertEquals("passwordNuova", nuovo.getPassword());
        assertEquals("CFNUOVO", nuovo.getCodiceFiscale());
        assertEquals("Luca", nuovo.getNome());
        assertEquals("Bianchi", nuovo.getCognome());
        assertEquals("nuovo@test.it", nuovo.getEmail());
    }


    // =========================================================
    // MODIFICA PAZIENTE
    // =========================================================

    @Test
    void modificaCredenzialiPazienteAggiornaTuttiIDati() {
        Diabetologo medico =new Diabetologo();
        Paziente vecchio =new Paziente("vecchioPaziente","passwordVecchia","CFPAZ05","Anna","Rossi","vecchia@test.it",null,medico,null,null,null);

        db.addDiabetologo(medico);
        db.addPaziente(vecchio);

        runAndWait(() -> {
            assertDoesNotThrow(() ->controller.modificaCredenziali(vecchio,"nuovoPaziente","passwordNuova","CFPAZ06","Giulia","Bianchi","nuova@test.it",null,medico));
        });

        Paziente nuovo =db.getPazienti().getFirst();
        assertEquals("nuovoPaziente",nuovo.getUsername());
        assertEquals("passwordNuova",nuovo.getPassword());
        assertEquals("CFPAZ06",nuovo.getCodiceFiscale());
        assertEquals("Giulia",nuovo.getNome());
        assertEquals("Bianchi",nuovo.getCognome());
        assertEquals("nuova@test.it",nuovo.getEmail());
    }


    // =========================================================
    // CAMBIO MEDICO
    // =========================================================

    @Test
    void modificaCredenzialiPazientePermetteDiCambiareMedico() {
        Diabetologo medicoVecchio =new Diabetologo();
        Diabetologo medicoNuovo =new Diabetologo("nuovoMedico","password","CFMEDNEW","Luca","Bianchi","luca@test.it");
        Paziente paziente =new Paziente("pazienteCambio","password","CFPAZ07","Anna","Verdi","anna@test.it",null,medicoVecchio,null,null,null);

        db.addDiabetologo(medicoVecchio);
        db.addDiabetologo(medicoNuovo);
        db.addPaziente(paziente);

        runAndWait(() -> {
            assertDoesNotThrow(() ->controller.modificaCredenziali(paziente,paziente.getUsername(),paziente.getPassword(),paziente.getCodiceFiscale(),paziente.getNome(),paziente.getCognome(),paziente.getEmail(),null,medicoNuovo));
        });

        Paziente risultato =db.getPazienti().getFirst();
        assertEquals(medicoNuovo,risultato.getMedicoDiRiferimento());
    }


    // =========================================================
    // PIU' PAZIENTI ASSOCIATI
    // =========================================================

    @Test
    void modificaDiabetologoAggiornaLeAssociazioni() {
        Diabetologo medico =new Diabetologo();
        Paziente p1 =new Paziente();
        Paziente p2 =new Paziente("p2","password","CFP2","Luca","Neri","p2@test.it",null,new Diabetologo(),null,null,null);

        db.addDiabetologo(medico);
        db.addPaziente(p1);
        db.addPaziente(p2);

        ArrayList<Paziente> selezionati =new ArrayList<>();

        selezionati.add(p1);
        selezionati.add(p2);

        runAndWait(() -> {
            assertDoesNotThrow(() ->controller.modificaCredenziali(medico,medico.getUsername(),medico.getPassword(),medico.getCodiceFiscale(),medico.getNome(),medico.getCognome(),medico.getEmail(),selezionati,null));
        });

        Diabetologo medicoAggiornato =db.getDiabetologi().getFirst();
        assertEquals(medicoAggiornato,db.getPazienti().getFirst().getMedicoDiRiferimento());
    }

    // =========================================================
    // SUPPORTO
    // =========================================================

    private ArrayList<MenuItemWrapper> getCheckBoxes() {
        ArrayList<MenuItemWrapper> result =new ArrayList<>();

        for (var item :pazientiMenuButton.getItems()) {
            CustomMenuItem customItem =(CustomMenuItem) item;
            CheckBox checkBox =(CheckBox) customItem.getContent();
            Paziente paziente =(Paziente) checkBox.getUserData();
            result.add(new MenuItemWrapper(paziente,checkBox));
        }
        
        return result;
    }


    private record MenuItemWrapper(Paziente paziente,CheckBox checkBox) {
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