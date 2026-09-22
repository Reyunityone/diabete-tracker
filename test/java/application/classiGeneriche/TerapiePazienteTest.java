package application.classiGeneriche;

import application.controller.StoricoTerapieController;
import application.controller.TerapiaController;
import application.controller.TerapieEsistentiController;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class TerapiePazienteTest {

    @TempDir
    Path tempDir;

    private Database db;
    private Paziente paziente;
    private Diabetologo medico;
    private StoricoTerapieController storicoController;
    private TerapieEsistentiController esistentiController;
    private TerapiaController terapiaController;

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
    // SETUP E TEARDOWN
    // =========================================================

    @BeforeEach
    void setUp() throws Exception {
        db = new Database(tempDir.resolve("test-database.data").toString());
        sostituisciDatabaseSingleton(db);

        medico = new Diabetologo(
                "medico",
                "password",
                "RSSMRA80A01H501Z",
                "Mario",
                "Rossi",
                "mario.rossi@email.it"
        );

        paziente = new Paziente(
                "paziente",
                "password",
                "VRDLGI05A01H501A",
                "Giovanni",
                "Verdi",
                "giovanni.verdi@email.it",
                null,
                medico,
                null,
                null,
                null
        );

        db.addDiabetologo(medico);
        db.addPaziente(paziente);
    }

    @AfterEach
    void tearDown() throws Exception {
        sostituisciDatabaseSingleton(null);
    }

    // =========================================================
    // DATABASE DI TEST
    // =========================================================

    private void sostituisciDatabaseSingleton(Database nuovoDatabase) throws Exception {
        Field field = Database.class.getDeclaredField("database");
        field.setAccessible(true);
        field.set(null, nuovoDatabase);
    }

    // =========================================================
    // STORICO TERAPIE
    // =========================================================

    private void creaStoricoController() throws Exception {
        runAndWait(() -> {
            storicoController = new StoricoTerapieController();
            Label titoloLabel = new Label();
            Button aggiungiButton = new Button();
            Button nuovaTerapiaButton = new Button();
            Button esistenteTerapiaButton = new Button();
            TextField searchField = new TextField();
            ScrollPane terapieScrollPane = new ScrollPane();
            VBox terapieContainer = new VBox();
            HBox aggiungiMenu = new HBox();

            setField(storicoController, "titoloLabel", titoloLabel);
            setField(storicoController, "aggiungiButton", aggiungiButton);
            setField(storicoController, "nuovaTerapiaButton", nuovaTerapiaButton);
            setField(storicoController, "esistenteTerapiaButton", esistenteTerapiaButton);
            setField(storicoController, "searchField", searchField);
            setField(storicoController, "terapieScrollPane", terapieScrollPane);
            setField(storicoController, "terapieContainer", terapieContainer);
            setField(storicoController, "aggiungiMenu", aggiungiMenu);

            storicoController.inizializza(paziente, medico);
            storicoController.setModalitaTest(true);
        });
    }

    // =========================================================
    // TERAPIE ESISTENTI
    // =========================================================

    private void creaEsistentiController() throws Exception {
        runAndWait(() -> {
            esistentiController = new TerapieEsistentiController();
            Label titoloLabel = new Label();
            ScrollPane terapieScrollPane = new ScrollPane();
            VBox terapieContainer = new VBox();

            setField(esistentiController, "titoloLabel", titoloLabel);
            setField(esistentiController, "terapieScrollPane", terapieScrollPane);
            setField(esistentiController, "terapieContainer", terapieContainer);

            terapieScrollPane.setContent(terapieContainer);

            VBox root = new VBox(titoloLabel, terapieScrollPane);
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setScene(new javafx.scene.Scene(root));

            esistentiController.inizializza(paziente, medico, null);
        });
    }

    // =========================================================
    // TERAPIA
    // =========================================================

    private void creaTerapiaController() throws Exception {
        runAndWait(() -> {
            terapiaController = new TerapiaController();
            Label titoloLabel = new Label();
            TextField farmacoField = new TextField();
            TextField assunzioniField = new TextField();
            TextField quantitaField = new TextField();
            TextArea indicazioniArea = new TextArea();
            Button salvaButton = new Button();

            setField(terapiaController, "titoloLabel", titoloLabel);
            setField(terapiaController, "farmacoField", farmacoField);
            setField(terapiaController, "assunzioniField", assunzioniField);
            setField(terapiaController, "quantitaField", quantitaField);
            setField(terapiaController, "indicazioniArea", indicazioniArea);
            setField(terapiaController, "salvaButton", salvaButton);

            VBox root = new VBox(
                    titoloLabel,
                    farmacoField,
                    assunzioniField,
                    quantitaField,
                    indicazioniArea,
                    salvaButton
            );

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setScene(new javafx.scene.Scene(root));

            terapiaController.initialize();
        });
    }

    // =========================================================
    // VISUALIZZAZIONE TERAPIE DEL PAZIENTE
    // =========================================================

    @Test
    void visualizzaCorrettamenteTerapiePaziente() throws Exception {
        Terapia insulina = creaTerapia("Insulina");
        Terapia metformina = creaTerapia("Metformina");

        db.addTerapia(insulina);
        db.addTerapia(metformina);

        insulina.getPazienti().add(paziente);
        metformina.getPazienti().add(paziente);

        db.save();
        creaStoricoController();

        VBox terapieContainer = getField(storicoController, "terapieContainer");

        assertEquals(2, terapieContainer.getChildren().size());
    }

    // =========================================================
    // RICERCA TERAPIA
    // =========================================================

    @Test
    void ricercaTerapiaPerNomeFarmaco() throws Exception {
        Terapia insulina = creaTerapia("Insulina");
        Terapia metformina = creaTerapia("Metformina");

        db.addTerapia(insulina);
        db.addTerapia(metformina);

        insulina.getPazienti().add(paziente);
        metformina.getPazienti().add(paziente);

        db.save();
        creaStoricoController();

        TextField searchField = getField(storicoController, "searchField");
        VBox terapieContainer = getField(storicoController, "terapieContainer");

        runAndWait(() -> searchField.setText("Insulina"));

        assertEquals(1, terapieContainer.getChildren().size());
    }

    // =========================================================
    // PULSANTE AGGIUNGI
    // =========================================================

    @Test
    void pulsanteAggiungiMostraSceltaTerapia() throws Exception {
        creaStoricoController();

        Button aggiungiButton = getField(storicoController, "aggiungiButton");
        Button nuovaTerapiaButton = getField(storicoController, "nuovaTerapiaButton");
        Button esistenteTerapiaButton = getField(storicoController, "esistenteTerapiaButton");

        runAndWait(aggiungiButton::fire);

        assertTrue(nuovaTerapiaButton.isVisible());
        assertTrue(esistenteTerapiaButton.isVisible());
    }

	 // =========================================================
	 // PULSANTE NUOVA TERAPIA
	 // =========================================================
	
	 @Test
	 void pulsanteNuovaTerapiaApreFinestraCorretta() throws Exception {
	     creaStoricoController();
	
	     Button aggiungiButton = getField(storicoController, "aggiungiButton");
	     Button nuovaTerapiaButton = getField(storicoController, "nuovaTerapiaButton");
	     HBox aggiungiMenu = getField(storicoController, "aggiungiMenu");
	
	     runAndWait(aggiungiButton::fire);
	
	     assertTrue(nuovaTerapiaButton.isVisible());
	
	     runAndWait(nuovaTerapiaButton::fire);
	
	     assertFalse(aggiungiMenu.isVisible());
	
	     TerapiaController controller = getField(
	             storicoController,
	             "terapiaControllerTest"
	     );
	
	     assertNotNull(controller);
	 }
	
	 // =========================================================
	 // PULSANTE TERAPIA ESISTENTE
	 // =========================================================
	
	 @Test
	 void pulsanteTerapiaEsistenteApreFinestraCorretta() throws Exception {
	     creaStoricoController();
	
	     Button aggiungiButton = getField(storicoController, "aggiungiButton");
	     Button esistenteTerapiaButton = getField(storicoController, "esistenteTerapiaButton");
	     HBox aggiungiMenu = getField(storicoController, "aggiungiMenu");
	
	     runAndWait(aggiungiButton::fire);
	
	     assertTrue(esistenteTerapiaButton.isVisible());
	
	     runAndWait(esistenteTerapiaButton::fire);
	
	     assertFalse(aggiungiMenu.isVisible());
	
	     TerapieEsistentiController controller = getField(
	             storicoController,
	             "terapieEsistentiControllerTest"
	     );
	
	     assertNotNull(controller);
	 }

    // =========================================================
    // SALVATAGGIO NUOVA TERAPIA
    // =========================================================

    @Test
    void pulsanteSalvaInserisceTerapiaNelDatabase() throws Exception {
        creaTerapiaController();

        terapiaController.inizializzaNuova(
                medico,
                terapia -> {
                    db.addTerapia(terapia);
                }
        );

        TextField farmacoField = getField(terapiaController, "farmacoField");
        TextField assunzioniField = getField(terapiaController, "assunzioniField");
        TextField quantitaField = getField(terapiaController, "quantitaField");
        TextArea indicazioniArea = getField(terapiaController, "indicazioniArea");
        Button salvaButton = getField(terapiaController, "salvaButton");

        runAndWait(() -> {
            farmacoField.setText("Insulina");
            assunzioniField.setText("2");
            quantitaField.setText("10");
            indicazioniArea.setText("Dopo i pasti");
        });

        runAndWait(salvaButton::fire);

        assertEquals(1, db.getTerapie().size());

        Terapia terapia = db.getTerapie().getFirst();

        assertEquals("Insulina", terapia.getFarmaco());
        assertEquals(2, terapia.getNumeroAssunzioniGiornaliere());
        assertEquals(10, terapia.getDose());
        assertEquals("Dopo i pasti", terapia.getIndicazioni());
    }

    // =========================================================
    // PULSANTE ASSEGNA
    // =========================================================

    @Test
    void pulsanteAssegnaAggiungeTerapiaAlPaziente() throws Exception {
        Terapia terapia = creaTerapia("Insulina");
        db.addTerapia(terapia);

        creaEsistentiController();

        VBox terapieContainer = getField(esistentiController, "terapieContainer");

        assertEquals(1, terapieContainer.getChildren().size());

        HBox box = (HBox) terapieContainer.getChildren().getFirst();
        Button assegnaButton = trovaPulsante(box, "Assegna");

        assertNotNull(assegnaButton);

        runAndWait(assegnaButton::fire);

        assertTrue(terapia.getPazienti().contains(paziente));
    }

    // =========================================================
    // PULSANTE RIMUOVI
    // =========================================================

    @Test
    void pulsanteRimuoviEliminaTerapiaDalDatabase() throws Exception {
        Terapia terapia = creaTerapia("Insulina");
        db.addTerapia(terapia);

        creaEsistentiController();

        VBox terapieContainer = getField(esistentiController, "terapieContainer");

        assertEquals(1, terapieContainer.getChildren().size());

        HBox box = (HBox) terapieContainer.getChildren().getFirst();
        Button rimuoviButton = trovaPulsante(box, "Rimuovi");

        assertNotNull(rimuoviButton);
        assertTrue(db.getTerapie().contains(terapia));

        runAndWait(rimuoviButton::fire);

        assertFalse(db.getTerapie().contains(terapia));
        assertEquals(0, terapieContainer.getChildren().size());
    }

    // =========================================================
    // PULSANTE ELIMINA DAL PAZIENTE
    // =========================================================

    @Test
    void pulsanteEliminaRimuoveTerapiaDalPaziente() throws Exception {
        Terapia terapia = creaTerapia("Insulina");
        terapia.getPazienti().add(paziente);
        db.addTerapia(terapia);

        creaStoricoController();

        VBox terapieContainer = getField(storicoController, "terapieContainer");

        assertEquals(1, terapieContainer.getChildren().size());

        HBox box = (HBox) terapieContainer.getChildren().getFirst();
        Button eliminaButton = trovaPulsante(box, "Elimina");

        assertNotNull(eliminaButton);
        assertTrue(terapia.getPazienti().contains(paziente));

        runAndWait(eliminaButton::fire);

        assertFalse(terapia.getPazienti().contains(paziente));
        assertEquals(0, terapieContainer.getChildren().size());
        assertTrue(db.getTerapie().contains(terapia));
    }

    // =========================================================
    // SUPPORTO
    // =========================================================

    private Terapia creaTerapia(String farmaco) {
        return new Terapia(farmaco, 10, 2, medico, new ArrayList<>(), "Dopo i pasti");
    }

    private void setField(Object controller, String nome, Object valore) {
        try {
            Field field = controller.getClass().getDeclaredField(nome);
            field.setAccessible(true);
            field.set(controller, valore);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T getField(Object controller, String nome) {
        try {
            Field field = controller.getClass().getDeclaredField(nome);
            field.setAccessible(true);
            return (T) field.get(controller);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Button trovaPulsante(HBox box, String testo) {
        for (javafx.scene.Node node : box.getChildren()) {
            if (node instanceof Button button && button.getText().equals(testo)) {
                return button;
            }
        }
        return null;
    }

    /*
     * Esegue il codice sul JavaFX Application Thread
     * e attende che venga completato.
     *
     * Nei test utilizziamo questo metodo per simulare
     * la pressione dei pulsanti tramite fire(), senza
     * mostrare le schermate all'utente.
     */
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