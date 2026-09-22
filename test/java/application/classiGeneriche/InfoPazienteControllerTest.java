package application.classiGeneriche;

import application.controller.InfoPazienteController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class InfoPazienteControllerTest {

    @TempDir
    Path tempDir;

    private Database db;
    private Paziente paziente;
    private Diabetologo medico;
    private Stage stage;
    private Scene scene;
    private Parent root;
    private InfoPazienteController infoPazienteController;
    private Button fattoriRischioButton;
    private Button salvaButton;
    private TextArea fattoriRischioArea;
    private TextArea patologieArea;
    private TextArea comorbiditaArea;
    private TextArea dettagliArea;

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
    // SETUP
    // =========================================================

    @BeforeEach
    void setUp() throws Exception {
        db = creaDatabaseTemporaneo();
        sostituisciDatabaseSingleton(db);

        medico = new Diabetologo("medicoTest", "password", "CFMEDICO01", "Mario", "Rossi", "medico@test.it");

        paziente = new Paziente("pazienteTest", "password", "CFPAZIENTE01", "Anna", "Verdi", "anna@test.it",
                new ArrayList<>(), medico, "Appendicite", "Nessuna", "Nessun dettaglio");

        db.addDiabetologo(medico);
        db.addPaziente(paziente);
        Session.getInstance().setCurrentUser(medico);
    }

    // =========================================================
    // TEARDOWN
    // =========================================================

    @AfterEach
    void tearDown() throws Exception {
        Session.getInstance().logout();
        chiudiFinestre();
        sostituisciDatabaseSingleton(null);
    }

    // =========================================================
    // FATTORE DI RISCHIO
    // =========================================================

    @Test
    void pulsanteFattoriRischioApreMenuESelezionaFattore() throws Exception {
        runAndWait(() -> {
            try {
                caricaInfoPaziente();
                fattoriRischioButton.fire();

                ContextMenu menu = getCampo(infoPazienteController, "menuFattoriRischio");

                CheckBox checkBox = trovaCheckBox(menu, RiskFactor.FUMATORE);
                assertNotNull(checkBox);

                checkBox.fire();
                assertTrue(checkBox.isSelected());
                assertEquals("Fumatore", fattoriRischioArea.getText());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void salvataggioFattoreDiRischioAggiornaPazienteEDatabase() throws Exception {
        runAndWait(() -> {
            try {
                caricaInfoPaziente();
                fattoriRischioButton.fire();

                ContextMenu menu = getCampo(infoPazienteController, "menuFattoriRischio");
                CheckBox checkBox = trovaCheckBox(menu, RiskFactor.FUMATORE);

                checkBox.fire();
                salvaButton.fire();

                List<RiskFactor> fattori = db.getFattoriDiRischioByPaziente(paziente);
                assertEquals(1, fattori.size());
                assertTrue(fattori.contains(RiskFactor.FUMATORE));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    // =========================================================
    // MODIFICA CAMPI PAZIENTE
    // =========================================================

    @Test
    void salvataggioModificaCampiTestualiAggiornaPaziente() throws Exception {
        runAndWait(() -> {
            try {
                caricaInfoPaziente();

                patologieArea.setText("Appendicite e ipertensione");
                comorbiditaArea.setText("Ipertensione");
                dettagliArea.setText("Trattamento in corso");

                salvaButton.fire();

                assertEquals("Appendicite e ipertensione", db.getPatologiePregresseByPaziente(paziente));
                assertEquals("Ipertensione", db.getComorbiditaByPaziente(paziente));
                assertEquals("Trattamento in corso", db.getDettagliByPaziente(paziente));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void salvataggioModificaFattoriECampiTestualiCreaUnSoloLog() throws Exception {
        runAndWait(() -> {
            try {
                caricaInfoPaziente();
                fattoriRischioButton.fire();

                ContextMenu menu = getCampo(infoPazienteController, "menuFattoriRischio");
                CheckBox fumatore = trovaCheckBox(menu, RiskFactor.FUMATORE);
                CheckBox obesita = trovaCheckBox(menu, RiskFactor.OBESITA);

                fumatore.fire();
                obesita.fire();

                patologieArea.setText("Patologia aggiornata");
                comorbiditaArea.setText("Comorbidità aggiornata");
                dettagliArea.setText("Dettagli aggiornati");

                salvaButton.fire();

                List<RiskFactor> fattori = db.getFattoriDiRischioByPaziente(paziente);
                assertEquals(2, fattori.size());
                assertTrue(fattori.contains(RiskFactor.FUMATORE));
                assertTrue(fattori.contains(RiskFactor.OBESITA));

                assertEquals("Patologia aggiornata", paziente.getPatologiePregresse());
                assertEquals("Comorbidità aggiornata", paziente.getComorbidita());
                assertEquals("Dettagli aggiornati", paziente.getDettagli());

                assertEquals(1, db.getLogs().size());

                LogOperazione log = db.getLogs().get(0);
                assertEquals(medico, log.getAuthor());
                assertEquals(paziente, log.getPazienteModificato());
                assertFalse(log.isUndoLog());
                assertTrue(log.getDescrizione().contains("AGGIORNAMENTO"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void salvataggioSenzaModificheNonCreaNuovoLog() throws Exception {
        runAndWait(() -> {
            try {
                caricaInfoPaziente();
                salvaButton.fire();

                assertEquals(0, db.getLogs().size());
                assertEquals("Appendicite", paziente.getPatologiePregresse());
                assertEquals("Nessuna", paziente.getComorbidita());
                assertEquals("Nessun dettaglio", paziente.getDettagli());
                assertTrue(paziente.getFattoriDiRischio().isEmpty());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    // =========================================================
    // STORICO
    // =========================================================

    @Test
    void pulsanteStoricoApreStoricoDelleModifiche() throws Exception {
        runAndWait(() -> {
            try {
                caricaInfoPaziente();

                patologieArea.setText("Nuova patologia");
                salvaButton.fire();

                caricaInfoPaziente();

                Stage storicoStage = infoPazienteController.creaStoricoStage();
                assertNotNull(storicoStage);
                assertNotNull(storicoStage.getScene());

                VBox contenitore = trovaContenitoreStorico(storicoStage);
                assertNotNull(contenitore);
                assertFalse(contenitore.getChildren().isEmpty());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void undoDalloStoricoRipristinaStatoPrecedente() throws Exception {
        runAndWait(() -> {
            try {
                caricaInfoPaziente();

                patologieArea.setText("Nuova patologia");
                comorbiditaArea.setText("Nuova comorbidità");
                dettagliArea.setText("Nuovi dettagli");

                salvaButton.fire();

                assertEquals("Nuova patologia", paziente.getPatologiePregresse());
                assertEquals("Nuova comorbidità", paziente.getComorbidita());
                assertEquals("Nuovi dettagli", paziente.getDettagli());
                assertEquals(1, db.getLogs().size());

                caricaInfoPaziente();

                Stage storicoStage = infoPazienteController.creaStoricoStage();
                assertNotNull(storicoStage);

                VBox contenitore = trovaContenitoreStorico(storicoStage);
                Button annullaButton = trovaPulsanteUndo(contenitore);

                assertNotNull(annullaButton);

                annullaButton.fire();

                assertEquals("Appendicite", paziente.getPatologiePregresse());
                assertEquals("Nessuna", paziente.getComorbidita());
                assertEquals("Nessun dettaglio", paziente.getDettagli());

                assertEquals("Appendicite", db.getPatologiePregresseByPaziente(paziente));
                assertEquals("Nessuna", db.getComorbiditaByPaziente(paziente));
                assertEquals("Nessun dettaglio", db.getDettagliByPaziente(paziente));

                assertEquals(2, db.getLogs().size());

                LogOperazione logOriginale = db.getLogs().get(0);
                LogOperazione logUndo = db.getLogs().get(1);

                assertTrue(logOriginale.isRipristinato());
                assertFalse(logOriginale.isUndoLog());
                assertTrue(logUndo.isUndoLog());
                assertTrue(logUndo.getDescrizione().startsWith("UNDO -> "));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void aggiornamentoDopoUndoCreaNuovaModifica() throws Exception {
        runAndWait(() -> {
            try {
                caricaInfoPaziente();

                patologieArea.setText("Prima modifica");
                salvaButton.fire();

                caricaInfoPaziente();

                Stage storicoStage = infoPazienteController.creaStoricoStage();
                VBox contenitore = trovaContenitoreStorico(storicoStage);
                Button annullaButton = trovaPulsanteUndo(contenitore);

                annullaButton.fire();

                assertEquals("Appendicite", paziente.getPatologiePregresse());

                caricaInfoPaziente();

                patologieArea.setText("Seconda modifica");
                salvaButton.fire();

                assertEquals("Seconda modifica", paziente.getPatologiePregresse());
                assertEquals("Seconda modifica", db.getPatologiePregresseByPaziente(paziente));
                assertEquals(3, db.getLogs().size());

                LogOperazione ultimoLog = db.getLogs().get(db.getLogs().size() - 1);
                assertFalse(ultimoLog.isUndoLog());
                assertFalse(ultimoLog.isRipristinato());
                assertTrue(ultimoLog.getDescrizione().contains("patologie pregresse"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    // =========================================================
    // CARICAMENTO FXML
    // =========================================================

    private void caricaInfoPaziente() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/InfoPaziente.fxml"));
        root = loader.load();

        infoPazienteController = loader.getController();
        infoPazienteController.inizializzaPaziente(paziente);

        fattoriRischioButton = getCampo(infoPazienteController, "fattoriRischioButton");
        salvaButton = getCampo(infoPazienteController, "salvaButton");
        fattoriRischioArea = getCampo(infoPazienteController, "fattoriRischioArea");
        patologieArea = getCampo(infoPazienteController, "patologieArea");
        comorbiditaArea = getCampo(infoPazienteController, "comorbiditaArea");
        dettagliArea = getCampo(infoPazienteController, "dettagliArea");

        creaStage(root);
    }

    // =========================================================
    // RICERCA CHECKBOX FATTORE DI RISCHIO
    // =========================================================

    private CheckBox trovaCheckBox(ContextMenu menu, RiskFactor fattore) {
        for (javafx.scene.control.MenuItem item : menu.getItems()) {
            if (item instanceof CustomMenuItem customItem) {
                if (customItem.getContent() instanceof CheckBox checkBox) {
                    if (fattore.equals(checkBox.getUserData())) {
                        return checkBox;
                    }
                }
            }
        }

        return null;
    }

    // =========================================================
    // RICERCA CONTENITORE STORICO
    // =========================================================

    private VBox trovaContenitoreStorico(Stage storicoStage) {
        if (storicoStage == null || storicoStage.getScene() == null) {
            return null;
        }

        return trovaVBox(storicoStage.getScene().getRoot());
    }

    private VBox trovaVBox(javafx.scene.Node node) {
        if (node instanceof VBox vbox) {
            for (javafx.scene.Node child : vbox.getChildren()) {
                if (child instanceof javafx.scene.control.ScrollPane scrollPane) {
                    if (scrollPane.getContent() instanceof VBox contenitore) {
                        return contenitore;
                    }
                }
            }

            for (javafx.scene.Node child : vbox.getChildren()) {
                VBox risultato = trovaVBox(child);

                if (risultato != null) {
                    return risultato;
                }
            }
        }

        return null;
    }

    // =========================================================
    // RICERCA PULSANTE UNDO
    // =========================================================

    private Button trovaPulsanteUndo(VBox contenitore) {
        if (contenitore == null) {
            return null;
        }

        for (javafx.scene.Node node : contenitore.getChildren()) {
            if (node instanceof javafx.scene.layout.HBox box) {
                for (javafx.scene.Node child : box.getChildren()) {
                    if (child instanceof Button button) {
                        return button;
                    }
                }
            }
        }

        return null;
    }

    // =========================================================
    // METODI DI SUPPORTO DATABASE
    // =========================================================

    private Database creaDatabaseTemporaneo() throws Exception {
        var constructor = Database.class.getDeclaredConstructor(String.class);
        constructor.setAccessible(true);

        return constructor.newInstance(tempDir.resolve("test-database.data").toString());
    }

    private void sostituisciDatabaseSingleton(Database nuovoDatabase) throws Exception {
        Field field = Database.class.getDeclaredField("database");
        field.setAccessible(true);
        field.set(null, nuovoDatabase);
    }

    // =========================================================
    // RIFLESSIONE
    // =========================================================

    @SuppressWarnings("unchecked")
    private <T> T getCampo(Object oggetto, String nomeCampo) throws Exception {
        Field field = oggetto.getClass().getDeclaredField(nomeCampo);
        field.setAccessible(true);

        return (T) field.get(oggetto);
    }

    // =========================================================
    // STAGE
    // =========================================================

    private void creaStage(Parent root) {
        stage = new Stage();
        scene = new Scene(root);
        stage.setScene(scene);
    }

    private void chiudiFinestre() throws InterruptedException {
        runAndWait(() -> {
            for (javafx.stage.Window window : javafx.stage.Window.getWindows()) {
                if (window instanceof Stage stage) {
                    stage.close();
                }
            }
        });
    }

    // =========================================================
    // JAVAFX THREAD
    // =========================================================

    /*
     * Esegue il codice sul JavaFX Application Thread e
     * attende che venga completato.
     *
     * È necessario perché i componenti JavaFX possono essere
     * letti o modificati in sicurezza solo dal thread JavaFX.
     */
    private static void runAndWait(Runnable runnable) throws InterruptedException {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
            return;
        }

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Throwable> errore = new AtomicReference<>();

        Platform.runLater(() -> {
            try {
                runnable.run();
            } catch (Throwable e) {
                errore.set(e);
            } finally {
                latch.countDown();
            }
        });

        latch.await();

        if (errore.get() != null) {
            throw new RuntimeException(errore.get());
        }
    }
}