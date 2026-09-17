package application.controller;

import application.classiGeneriche.*;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;


class PazienteControllerTest {

    @TempDir
    Path tempDir;

    private Database db;

    private Paziente paziente;
    private Diabetologo medico;
    private Terapia terapia;

    private Stage stage;
    private Scene scene;
    private Parent root;

    private RilevazioneController rilevazioneController;
    private DatePicker dataPicker;
    private TextField glicemiaField;
    private TextField orarioField;
    private TextField pastoField;
    private ComboBox<MomentoRilevazione> momentoComboBox;

    private FarmacoController farmacoController;
    private TextField quantitaField;
    private ComboBox<Terapia> terapiaBox;

    private SegnalazioneController segnalazioneController;
    private DatePicker dataInizioPicker;
    private DatePicker dataFinePicker;
    private TextArea testoArea;

    private ScriviEmailController scriviEmailController;
    private TextField destinatarioField;
    private TextArea testoEmailArea;
    private Button inviaButton;

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

        paziente = new Paziente("pazienteTest", "password", "CFPAZIENTE01", "Anna", "Verdi", "anna@test.it", null, medico, null, null, null);

        terapia = new Terapia("Insulina", 10, 2, medico, new ArrayList<>(), "Dopo i pasti");

        terapia.getPazienti().add(paziente);

        db.addDiabetologo(medico);
        db.addPaziente(paziente);
        db.addTerapia(terapia);

        Session.getInstance().setCurrentUser(paziente);
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
    // RILEVAZIONE
    // =========================================================

    @Test
    void inserimentoRilevazioneValidaInserisceTuttiICampi() throws Exception {
        runAndWait(() -> {
            try {
                caricaRilevazione(false);

                MomentoRilevazione momento = MomentoRilevazione.values()[0];

                dataPicker.setValue(LocalDate.of(2026, 9, 15));
                glicemiaField.setText("100");
                orarioField.setText("12:30");
                pastoField.setText("12:00");
                momentoComboBox.setValue(momento);

                invocaSalva(rilevazioneController);

                Rilevazione risultato = db.getRilevazioniByPaziente(paziente).get(0);

                assertEquals(LocalDate.of(2026, 9, 15), risultato.getData());
                assertEquals(100, risultato.getLivelloGlicemia());
                assertEquals(LocalTime.of(12, 30), risultato.getOrarioRilevazione());
                assertEquals(LocalTime.of(12, 0), risultato.getOrarioPasto());
                assertEquals(momento, risultato.getMomentoRilevazione());
                assertEquals(paziente, risultato.getPaziente());

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void salvataggioRilevazioneAggiungeCorrettamenteAlDatabase() throws Exception {
        runAndWait(() -> {
            try {
                caricaRilevazione(false);

                dataPicker.setValue(LocalDate.of(2026, 9, 15));
                glicemiaField.setText("110");
                orarioField.setText("08:30");
                pastoField.setText("08:00");
                momentoComboBox.setValue(MomentoRilevazione.values()[0]);

                invocaSalva(rilevazioneController);

                assertEquals(1, db.getRilevazioniByPaziente(paziente).size());

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void salvataggioRilevazioneSenzaCampiNonInserisceNelDatabase() throws Exception {
        runAndWait(() -> {
            try {
                caricaRilevazione(false);
                invocaSalva(rilevazioneController);

                assertEquals(0, db.getRilevazioniByPaziente(paziente).size());

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void salvataggioRilevazioneConUnCampoMancanteNonInserisceNelDatabase() throws Exception {
        runAndWait(() -> {
            try {
                caricaRilevazione(false);

                dataPicker.setValue(LocalDate.of(2026, 9, 15));
                glicemiaField.setText("100");
                orarioField.setText("12:30");
                pastoField.setText("12:00");

                // Momento della rilevazione non inserito
                momentoComboBox.setValue(null);

                invocaSalva(rilevazioneController);

                assertEquals(0, db.getRilevazioniByPaziente(paziente).size());

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    // =========================================================
    // ASSUNZIONE FARMACO
    // =========================================================

    @Test
    void inserimentoAssunzioneValidaInserisceTuttiICampi() throws Exception {
        runAndWait(() -> {
            try {
                caricaFarmaco(false);

                dataPicker.setValue(LocalDate.of(2026, 9, 15));
                orarioField.setText("13:00");
                quantitaField.setText("10");
                terapiaBox.setValue(terapia);

                invocaSalva(farmacoController);

                AssunzioneFarmaco risultato = db.getAssunzioniByPaziente(paziente).get(0);

                assertEquals(paziente, risultato.getPaziente());
                assertEquals(LocalDate.of(2026, 9, 15), risultato.getData());
                assertEquals(LocalTime.of(13, 0), risultato.getOrarioAssunzione());
                assertEquals(10, risultato.getQuantita());
                assertEquals(terapia, risultato.getTerapia());

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void salvataggioAssunzioneAggiungeCorrettamenteAlDatabase() throws Exception {
        runAndWait(() -> {
            try {
                caricaFarmaco(false);

                dataPicker.setValue(LocalDate.of(2026, 9, 15));
                orarioField.setText("13:00");
                quantitaField.setText("10");
                terapiaBox.setValue(terapia);

                invocaSalva(farmacoController);

                assertEquals(1, db.getAssunzioniByPaziente(paziente).size());

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void salvataggioAssunzioneSenzaCampiNonInserisceNelDatabase() throws Exception {
        runAndWait(() -> {
            try {
                caricaFarmaco(false);
                invocaSalva(farmacoController);

                assertEquals(0, db.getAssunzioniByPaziente(paziente).size());

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void salvataggioAssunzioneConUnCampoMancanteNonInserisceNelDatabase() throws Exception {
        runAndWait(() -> {
            try {
                caricaFarmaco(false);

                dataPicker.setValue(LocalDate.of(2026, 9, 15));
                orarioField.setText("13:00");
                quantitaField.setText("10");

                // Terapia non selezionata
                terapiaBox.setValue(null);

                invocaSalva(farmacoController);

                assertEquals(0, db.getAssunzioniByPaziente(paziente).size());

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    // =========================================================
    // SEGNALAZIONE
    // =========================================================

    @Test
    void inserimentoSegnalazioneValidaInserisceTuttiICampi() throws Exception {
        runAndWait(() -> {
            try {
                caricaSegnalazione(false);

                dataInizioPicker.setValue(LocalDate.of(2026, 9, 15));
                dataFinePicker.setValue(LocalDate.of(2026, 9, 20));
                testoArea.setText("Segnalazione di prova");

                invocaSalva(segnalazioneController);

                Segnalazione risultato = db.getSegnalazioniByPaziente(paziente).get(0);

                assertEquals(LocalDate.of(2026, 9, 15), risultato.getDataInizio());
                assertEquals(LocalDate.of(2026, 9, 20), risultato.getDataFine());
                assertEquals("Segnalazione di prova", risultato.getTesto());
                assertEquals(paziente, risultato.getPaziente());

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void salvataggioSegnalazioneAggiungeCorrettamenteAlDatabase() throws Exception {
        runAndWait(() -> {
            try {
                caricaSegnalazione(false);

                dataInizioPicker.setValue(LocalDate.of(2026, 9, 15));
                dataFinePicker.setValue(LocalDate.of(2026, 9, 15));
                testoArea.setText("Segnalazione salvata");

                invocaSalva(segnalazioneController);

                assertEquals(1, db.getSegnalazioniByPaziente(paziente).size());

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void salvataggioSegnalazioneSenzaCampiNonInserisceNelDatabase() throws Exception {
        runAndWait(() -> {
            try {
                caricaSegnalazione(false);
                invocaSalva(segnalazioneController);

                assertEquals(0, db.getSegnalazioniByPaziente(paziente).size());

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void salvataggioSegnalazioneConUnCampoMancanteNonInserisceNelDatabase() throws Exception {
        runAndWait(() -> {
            try {
                caricaSegnalazione(false);

                dataInizioPicker.setValue(LocalDate.of(2026, 9, 15));
                dataFinePicker.setValue(LocalDate.of(2026, 9, 20));

                // Testo della segnalazione non inserito
                testoArea.setText("");

                invocaSalva(segnalazioneController);

                assertEquals(0, db.getSegnalazioniByPaziente(paziente).size());

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    // =========================================================
    // MAIL
    // =========================================================

    @Test
    void emailDelDiabetologoECorretta() throws Exception {
        runAndWait(() -> {
            try {
                caricaScriviEmail();

                assertEquals(medico.getEmail(), destinatarioField.getText());
                assertEquals("medico@test.it", destinatarioField.getText());

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void invioEmailInviaCorrettaAlDiabetologoDiRiferimento() throws Exception {
        runAndWait(() -> {
            try {
                caricaScriviEmail();

                testoEmailArea.setText("Messaggio di prova");
                inviaButton.fire();

                assertEquals(1, db.getAllMessaggi().size());

                Messaggio messaggio = db.getAllMessaggi().get(0);

                assertEquals(paziente, messaggio.getPaziente());
                assertEquals(medico, messaggio.getDiabetologo());
                assertEquals("Messaggio di prova", messaggio.getTesto());
                assertEquals(TipoAlert.PAZIENTE_MEDICO, messaggio.getTipo());

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    // =========================================================
    // CARICAMENTO FXML
    // =========================================================

    private void caricaRilevazione(boolean modifica) throws Exception {
        caricaRilevazione(modifica, null);
    }

    private void caricaRilevazione(boolean modifica, Rilevazione rilevazione) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/Rilevazione.fxml"));
        root = loader.load();
        rilevazioneController = loader.getController();

        if (modifica) {
            rilevazioneController.inizializzaModifica(rilevazione, () -> {});
        } else {
            rilevazioneController.inizializza(db::addRilevazione);
        }

        dataPicker = getCampo(rilevazioneController, "dataPicker");
        glicemiaField = getCampo(rilevazioneController, "glicemiaField");
        orarioField = getCampo(rilevazioneController, "orarioField");
        pastoField = getCampo(rilevazioneController, "pastoField");
        momentoComboBox = getCampo(rilevazioneController, "momentoComboBox");

        creaStage(root);
    }

    private void caricaFarmaco(boolean modifica) throws Exception {
        caricaFarmaco(modifica, null);
    }

    private void caricaFarmaco(boolean modifica, AssunzioneFarmaco assunzione) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/AssunzioneFarmaco.fxml"));
        root = loader.load();
        farmacoController = loader.getController();

        if (modifica) {
            farmacoController.inizializzaModifica(assunzione, () -> {});
        } else {
            farmacoController.inizializza(db::addAssunzione);
        }

        dataPicker = getCampo(farmacoController, "dataPicker");
        orarioField = getCampo(farmacoController, "orarioField");
        quantitaField = getCampo(farmacoController, "quantitaField");
        terapiaBox = getCampo(farmacoController, "terapiaBox");

        creaStage(root);
    }

    private void caricaSegnalazione(boolean modifica) throws Exception {
        caricaSegnalazione(modifica, null);
    }

    private void caricaSegnalazione(boolean modifica, Segnalazione segnalazione) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/Segnalazione.fxml"));
        root = loader.load();
        segnalazioneController = loader.getController();

        if (modifica) {
            segnalazioneController.inizializzaModifica(segnalazione, () -> {});
        } else {
            segnalazioneController.inizializza(db::addSegnalazione);
        }

        dataInizioPicker = getCampo(segnalazioneController, "dataInizioPicker");
        dataFinePicker = getCampo(segnalazioneController, "dataFinePicker");
        testoArea = getCampo(segnalazioneController, "testoArea");

        creaStage(root);
    }

    private void caricaScriviEmail() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/ScriviEmail.fxml"));
        root = loader.load();
        scriviEmailController = loader.getController();

        scriviEmailController.inizializza(paziente, medico);

        destinatarioField = getCampo(scriviEmailController, "destinatarioField");
        testoEmailArea = getCampo(scriviEmailController, "testoEmailArea");
        inviaButton = getCampo(scriviEmailController, "inviaButton");

        creaStage(root);
    }

    // =========================================================
    // METODI DI SUPPORTO
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

    @SuppressWarnings("unchecked")
    private <T> T getCampo(Object oggetto, String nomeCampo) throws Exception {
        Field field = oggetto.getClass().getDeclaredField(nomeCampo);
        field.setAccessible(true);

        return (T) field.get(oggetto);
    }

    private void invocaSalva(Object controller) throws Exception {
        Method metodo = controller.getClass().getDeclaredMethod("salva");
        metodo.setAccessible(true);
        metodo.invoke(controller);
    }

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