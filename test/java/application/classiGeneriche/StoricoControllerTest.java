package application.controller;

import application.classiGeneriche.*;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.stage.Window;

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
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class StoricoControllerTest {

    @TempDir
    Path tempDir;

    private Database db;
    private Paziente paziente;
    private Diabetologo medico;
    private Terapia terapia;

    private Stage stage;
    private Scene scene;
    private Parent root;

    private StoricoController storicoController;
    private TextField ricercaDataField;
    private VBox contenitoreStorico;

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
            // JavaFX già inizializzato
        }
    }

    // =========================================================
    // SETUP
    // =========================================================

    @BeforeEach
    void setUp() throws Exception {
        db = creaDatabaseTemporaneo();
        sostituisciDatabaseSingleton(db);

        medico = new Diabetologo("medicoTest", "password", "RSSMRA80A01H501Z", "Mario", "Rossi", "mario.rossi@test.it");

        paziente = new Paziente(
                "pazienteTest",
                "password",
                "BNCLGU00A01H501Z",
                "Luca",
                "Bianchi",
                "luca.bianchi@test.it",
                new ArrayList<>(),
                medico,
                "",
                "",
                ""
        );

        terapia = new Terapia(
                "Insulina",
                10,
                2,
                medico,
                new ArrayList<>(List.of(paziente)),
                "Terapia di test"
        );

        db.addDiabetologo(medico);
        db.addPaziente(paziente);
        db.addTerapia(terapia);

        Session.getInstance().setCurrentUser(paziente);

        caricaStorico();
        
        storicoController.setModalitaTest(true);
    }

    @AfterEach
    void tearDown() {
        Session.getInstance().logout();

        chiudiFinestre();

        try {
            sostituisciDatabaseSingleton(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================================================
    // APERTURA STORICO
    // =========================================================

    @Test
    void aperturaStoricoCaricaCorrettaLaSchermata() {
        assertNotNull(storicoController);
        assertNotNull(ricercaDataField);
        assertNotNull(contenitoreStorico);
    }

    // =========================================================
    // VISUALIZZAZIONE
    // =========================================================

    @Test
    void storicoVisualizzaCorrettaUnaRilevazione() {
        Rilevazione rilevazione = new Rilevazione(
                LocalDate.of(2026, 9, 15),
                100,
                LocalTime.of(12, 30),
                LocalTime.of(12, 0),
                MomentoRilevazione.PRIMA_COLAZIONE,
                paziente
        );

        db.addRilevazione(rilevazione);

        storicoController.inizializza(
                db.getRilevazioniByPaziente(paziente),
                "rilevazioni"
        );

        assertEquals(1, contenitoreStorico.getChildren().size());

        HBox box = (HBox) contenitoreStorico.getChildren().get(0);
        VBox informazioni = (VBox) box.getChildren().get(0);

        Label data = (Label) informazioni.getChildren().get(0);
        Label contenuto = (Label) informazioni.getChildren().get(1);

        assertTrue(data.getText().contains("2026-09-15"));
        assertTrue(contenuto.getText().contains("100"));
        assertTrue(contenuto.getText().contains("12:00"));
    }

    @Test
    void storicoVisualizzaCorrettaUnaAssunzione() {
        AssunzioneFarmaco assunzione = new AssunzioneFarmaco(
                paziente,
                LocalDate.of(2026, 9, 15),
                LocalTime.of(13, 0),
                10,
                terapia
        );

        db.addAssunzione(assunzione);

        storicoController.inizializza(
                db.getAssunzioniByPaziente(paziente),
                "sintomi"
        );

        assertEquals(1, contenitoreStorico.getChildren().size());

        HBox box = (HBox) contenitoreStorico.getChildren().get(0);
        VBox informazioni = (VBox) box.getChildren().get(0);

        Label data = (Label) informazioni.getChildren().get(0);
        Label contenuto = (Label) informazioni.getChildren().get(1);

        assertTrue(data.getText().contains("2026-09-15"));
        assertTrue(contenuto.getText().contains("10"));
    }

    @Test
    void storicoVisualizzaCorrettaUnaSegnalazione() {
        Segnalazione segnalazione = new Segnalazione(
                LocalDate.of(2026, 9, 15),
                LocalDate.of(2026, 9, 20),
                paziente,
                "Malessere generale"
        );

        db.addSegnalazione(segnalazione);

        storicoController.inizializza(
                db.getSegnalazioniByPaziente(paziente),
                "segnalazioni"
        );

        assertEquals(1, contenitoreStorico.getChildren().size());

        HBox box = (HBox) contenitoreStorico.getChildren().get(0);
        VBox informazioni = (VBox) box.getChildren().get(0);

        Label data = (Label) informazioni.getChildren().get(0);
        Label contenuto = (Label) informazioni.getChildren().get(1);

        assertTrue(data.getText().contains("2026-09-15"));
        assertEquals("Malessere generale", contenuto.getText());
    }

    // =========================================================
    // RICERCA
    // =========================================================

    @Test
    void ricercaDataMostraSoloGliElementiCorrispondenti() {
        Rilevazione rilevazione1 = new Rilevazione(
                LocalDate.of(2026, 9, 15),
                100,
                LocalTime.of(12, 30),
                LocalTime.of(12, 0),
                MomentoRilevazione.PRIMA_COLAZIONE,
                paziente
        );

        Rilevazione rilevazione2 = new Rilevazione(
                LocalDate.of(2026, 9, 16),
                120,
                LocalTime.of(13, 30),
                LocalTime.of(13, 0),
                MomentoRilevazione.DOPO_PRANZO,
                paziente
        );

        db.addRilevazione(rilevazione1);
        db.addRilevazione(rilevazione2);

        storicoController.inizializza(
                db.getRilevazioniByPaziente(paziente),
                "rilevazioni"
        );

        assertEquals(2, contenitoreStorico.getChildren().size());

        ricercaDataField.setText("2026-09-15");

        assertEquals(1, contenitoreStorico.getChildren().size());

        HBox box = (HBox) contenitoreStorico.getChildren().get(0);
        VBox informazioni = (VBox) box.getChildren().get(0);

        Label data = (Label) informazioni.getChildren().get(0);

        assertEquals("Data: 2026-09-15", data.getText());
    }

    @Test
    void ricercaDataSenzaCorrispondenzeNonMostraElementi() {
        Rilevazione rilevazione = new Rilevazione(
                LocalDate.of(2026, 9, 15),
                100,
                LocalTime.of(12, 30),
                LocalTime.of(12, 0),
                MomentoRilevazione.PRIMA_COLAZIONE,
                paziente
        );

        db.addRilevazione(rilevazione);

        storicoController.inizializza(
                db.getRilevazioniByPaziente(paziente),
                "rilevazioni"
        );

        ricercaDataField.setText("2026-09-20");

        assertEquals(0, contenitoreStorico.getChildren().size());
    }

    // =========================================================
    // TASTO MODIFICA
    // =========================================================

    @Test
    void pulsanteModificaRilevazioneSalvaModifica() throws Exception {
        Rilevazione rilevazione = new Rilevazione(
                LocalDate.of(2026, 9, 15),
                100,
                LocalTime.of(12, 30),
                LocalTime.of(12, 0),
                MomentoRilevazione.PRIMA_COLAZIONE,
                paziente
        );

        db.addRilevazione(rilevazione);

        RilevazioneController controller = apriModifica(
                rilevazione,
                "rilevazioni",
                RilevazioneController.class
        );

        TextField glicemiaField = getCampo(controller, "glicemiaField");

        runAndWait(() -> glicemiaField.setText("120"));

        invocaSalva(controller);

        Rilevazione modificata = db.getRilevazioniByPaziente(paziente).get(0);

        assertEquals(120, modificata.getLivelloGlicemia());
    }

    @Test
    void pulsanteModificaRilevazioneSenzaSalvareMantieneValoreOriginale() throws Exception {
        Rilevazione rilevazione = new Rilevazione(
                LocalDate.of(2026, 9, 15),
                100,
                LocalTime.of(12, 30),
                LocalTime.of(12, 0),
                MomentoRilevazione.PRIMA_COLAZIONE,
                paziente
        );

        db.addRilevazione(rilevazione);

        RilevazioneController controller = apriModifica(
                rilevazione,
                "rilevazioni",
                RilevazioneController.class
        );

        TextField glicemiaField = getCampo(controller, "glicemiaField");

        runAndWait(() -> glicemiaField.setText("120"));

        Rilevazione presente = db.getRilevazioniByPaziente(paziente).get(0);

        assertEquals(100, presente.getLivelloGlicemia());
    }

    @Test
    void pulsanteModificaAssunzioneSalvaModifica() throws Exception {
	    Terapia terapiaTest = new Terapia(
	            "Insulina",
	            20,
	            2,
	            medico,
	            new ArrayList<>(List.of(paziente)),
	            "Terapia di test"
	    );
	
	    db.addTerapia(terapiaTest);
	
	    AssunzioneFarmaco assunzione = new AssunzioneFarmaco(
	            paziente,
	            LocalDate.of(2026, 9, 15),
	            LocalTime.of(13, 0),
	            10,
	            terapiaTest
	    );
	
	    db.addAssunzione(assunzione);
	
	    FarmacoController controller = apriModifica(
	            assunzione,
	            "sintomi",
	            FarmacoController.class
	    );
	
	    TextField quantitaField = getCampo(controller, "quantitaField");
	
	    runAndWait(() -> quantitaField.setText("20"));
	
	    invocaSalva(controller);
	
	    AssunzioneFarmaco modificata =
	            db.getAssunzioniByPaziente(paziente).get(0);
	
	    assertEquals(20, modificata.getQuantita());
    }


    @Test
    void pulsanteModificaAssunzioneSenzaSalvareMantieneValoreOriginale() throws Exception {
        AssunzioneFarmaco assunzione = new AssunzioneFarmaco(
                paziente,
                LocalDate.of(2026, 9, 15),
                LocalTime.of(13, 0),
                10,
                terapia
        );

        db.addAssunzione(assunzione);

        FarmacoController controller = apriModifica(
                assunzione,
                "sintomi",
                FarmacoController.class
        );

        TextField quantitaField = getCampo(controller, "quantitaField");

        runAndWait(() -> quantitaField.setText("20"));
        
        AssunzioneFarmaco presente = db.getAssunzioniByPaziente(paziente).get(0);

        assertEquals(10, presente.getQuantita());
    }

    @Test
    void pulsanteModificaSegnalazioneSalvaModifica() throws Exception {
        Segnalazione segnalazione = new Segnalazione(
                LocalDate.of(2026, 9, 15),
                LocalDate.of(2026, 9, 20),
                paziente,
                "Malessere generale"
        );

        db.addSegnalazione(segnalazione);

        SegnalazioneController controller = apriModifica(
                segnalazione,
                "segnalazioni",
                SegnalazioneController.class
        );

        TextArea testoArea = getCampo(controller, "testoArea");

        runAndWait(() -> testoArea.setText("Nuovo testo della segnalazione"));

        invocaSalva(controller);

        Segnalazione modificata = db.getSegnalazioniByPaziente(paziente).get(0);

        assertEquals("Nuovo testo della segnalazione", modificata.getTesto());
    }

    @Test
    void pulsanteModificaSegnalazioneSenzaSalvareMantieneValoreOriginale() throws Exception {
        Segnalazione segnalazione = new Segnalazione(
                LocalDate.of(2026, 9, 15),
                LocalDate.of(2026, 9, 20),
                paziente,
                "Malessere generale"
        );

        db.addSegnalazione(segnalazione);

        SegnalazioneController controller = apriModifica(
                segnalazione,
                "segnalazioni",
                SegnalazioneController.class
        );

        TextArea testoArea = getCampo(controller, "testoArea");

        runAndWait(() -> testoArea.setText("Nuovo testo della segnalazione"));
        
        Segnalazione presente = db.getSegnalazioniByPaziente(paziente).get(0);

        assertEquals("Malessere generale", presente.getTesto());
    }

    // =========================================================
    // TASTO ELIMINA
    // =========================================================

    @Test
    void pulsanteEliminaRilevazioneRimuoveElementoDalDatabase() {
        Rilevazione rilevazione = new Rilevazione(
                LocalDate.of(2026, 9, 15),
                100,
                LocalTime.of(12, 30),
                LocalTime.of(12, 0),
                MomentoRilevazione.PRIMA_COLAZIONE,
                paziente
        );

        db.addRilevazione(rilevazione);

        storicoController.inizializza(
                db.getRilevazioniByPaziente(paziente),
                "rilevazioni"
        );

        assertEquals(1, contenitoreStorico.getChildren().size());
        assertEquals(1, db.getRilevazioniByPaziente(paziente).size());

        HBox box = (HBox) contenitoreStorico.getChildren().get(0);
        Button elimina = (Button) box.getChildren().get(3);

        runAndWait(elimina::fire);

        assertEquals(0, db.getRilevazioniByPaziente(paziente).size());
        assertEquals(0, contenitoreStorico.getChildren().size());
    }

    @Test
    void pulsanteEliminaAssunzioneRimuoveElementoDalDatabase() {
        AssunzioneFarmaco assunzione = new AssunzioneFarmaco(
                paziente,
                LocalDate.of(2026, 9, 15),
                LocalTime.of(13, 0),
                10,
                terapia
        );

        db.addAssunzione(assunzione);

        storicoController.inizializza(
                db.getAssunzioniByPaziente(paziente),
                "sintomi"
        );

        assertEquals(1, contenitoreStorico.getChildren().size());
        assertEquals(1, db.getAssunzioniByPaziente(paziente).size());

        HBox box = (HBox) contenitoreStorico.getChildren().get(0);
        Button elimina = (Button) box.getChildren().get(3);

        runAndWait(elimina::fire);

        assertEquals(0, db.getAssunzioniByPaziente(paziente).size());
        assertEquals(0, contenitoreStorico.getChildren().size());
    }

    @Test
    void pulsanteEliminaSegnalazioneRimuoveElementoDalDatabase() {
        Segnalazione segnalazione = new Segnalazione(
                LocalDate.of(2026, 9, 15),
                LocalDate.of(2026, 9, 20),
                paziente,
                "Malessere generale"
        );

        db.addSegnalazione(segnalazione);

        storicoController.inizializza(
                db.getSegnalazioniByPaziente(paziente),
                "segnalazioni"
        );

        assertEquals(1, contenitoreStorico.getChildren().size());
        assertEquals(1, db.getSegnalazioniByPaziente(paziente).size());

        HBox box = (HBox) contenitoreStorico.getChildren().get(0);
        Button elimina = (Button) box.getChildren().get(3);

        runAndWait(elimina::fire);

        assertEquals(0, db.getSegnalazioniByPaziente(paziente).size());
        assertEquals(0, contenitoreStorico.getChildren().size());
    }

    // =========================================================
    // SUPPORTO
    // =========================================================

    private void caricaStorico() throws Exception {
        runAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/Storico.fxml"));

                root = loader.load();

                storicoController = loader.getController();

                storicoController.inizializza(
                        new ArrayList<>(),
                        "rilevazioni"
                );

                ricercaDataField = getCampo(
                        storicoController,
                        "ricercaDataField"
                );

                contenitoreStorico = getCampo(
                        storicoController,
                        "contenitoreStorico"
                );

                creaStage(root);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private Database creaDatabaseTemporaneo() throws Exception {
        Path file = tempDir.resolve("database.data");

        java.lang.reflect.Constructor<Database> constructor =
                Database.class.getDeclaredConstructor(String.class);

        constructor.setAccessible(true);

        return constructor.newInstance(file.toString());
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

    private void creaStage(Parent root) {
        stage = new Stage();
        scene = new Scene(root);
        stage.setScene(scene);
    }

    private void chiudiFinestre() {
        runAndWait(() -> {
            for (Stage stage : new ArrayList<>(Stage.getWindows().stream()
                    .filter(window -> window instanceof Stage)
                    .map(window -> (Stage) window)
                    .toList())) {

                stage.close();
            }
        });
    }
    
	 // =========================================================
	 // APERTURA FINESTRA DI MODIFICA SENZA MOSTRARLA
	 // =========================================================
	
	 private <T> T apriModifica(Object elemento, String tipo, Class<T> tipoController) throws Exception {
	
	     storicoController.inizializza(
	             getElementiPerTipo(tipo),
	             tipo
	     );
	
	     HBox box = (HBox) contenitoreStorico.getChildren().get(0);
	     Button modifica = (Button) box.getChildren().get(2);
	
	     assertNotNull(modifica);
	
	     runAndWait(modifica::fire);
	
	     Object controller = storicoController.getUltimoControllerModifica();
	
	     assertNotNull(controller);
	     assertTrue(tipoController.isInstance(controller));
	
	     return tipoController.cast(controller);
	 }
	 
    private List<?> getElementiPerTipo(String tipo) {
        if (tipo.equals("rilevazioni")) {
            return db.getRilevazioniByPaziente(paziente);
        }

        if (tipo.equals("sintomi")) {
            return db.getAssunzioniByPaziente(paziente);
        }

        if (tipo.equals("segnalazioni")) {
            return db.getSegnalazioniByPaziente(paziente);
        }

        throw new IllegalArgumentException("Tipo storico non valido: " + tipo);
    }

    private void invocaSalva(Object controller) throws Exception {
        Method metodo = controller.getClass().getDeclaredMethod("salva");

        metodo.setAccessible(true);

        runAndWait(() -> {
            try {
                metodo.invoke(controller);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    // =========================================================
    // ESECUZIONE JAVAFX
    // =========================================================

    private void runAndWait(Runnable runnable) {
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

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        if (errore.get() != null) {
            throw new RuntimeException(errore.get());
        }
    }
}