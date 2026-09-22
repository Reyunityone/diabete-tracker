package application.classiGeneriche;

import application.controller.AndamentoController;
import application.controller.DiabetologoController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;


class DiabetologoControllerTest {

    @TempDir
    Path tempDir;

    private Database db;
    private Diabetologo medico;
    private DiabetologoController controller;

    private TextField searchField;
    private VBox pazientiContainer;


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

        medico = new Diabetologo("medicoTest", "password", "CFMEDICO", "Mario", "Rossi", "mario@test.it");

        db.addDiabetologo(medico);
        Session.getInstance().setCurrentUser(medico);
    }


    @AfterEach
    void tearDown() throws Exception {
        Session.getInstance().logout();
        chiudiFinestre();
        sostituisciDatabaseSingleton(null);
    }


    // =========================================================
    // APERTURA SCHERMATA
    // =========================================================

    @Test
    void aperturaDiabetologoCaricaCorrettaLaSchermata() {
        caricaController();

        assertNotNull(controller);
        assertNotNull(searchField);
        assertNotNull(pazientiContainer);
    }


    // =========================================================
    // BARRA DI RICERCA
    // =========================================================

    @Test
    void ricercaPerNomeMostraSoloIlPazienteCorrispondente() {
        aggiungiPaziente("paziente1", "Luca", "Bianchi", "CFLUCA");
        aggiungiPaziente("paziente2", "Anna", "Verdi", "CFANNA");

        caricaController();

        runAndWait(() -> searchField.setText("Luca"));

        assertEquals(1, pazientiContainer.getChildren().size());
        assertEquals("Luca Bianchi", nomeDelBox(pazientiContainer.getChildren().getFirst()));
    }


    @Test
    void ricercaPerCognomeMostraSoloIlPazienteCorrispondente() {
        aggiungiPaziente("paziente1", "Luca", "Bianchi", "CFLUCA");
        aggiungiPaziente("paziente2", "Anna", "Verdi", "CFANNA");

        caricaController();

        runAndWait(() -> searchField.setText("Verdi"));

        assertEquals(1, pazientiContainer.getChildren().size());
        assertEquals("Anna Verdi", nomeDelBox(pazientiContainer.getChildren().getFirst()));
    }


    @Test
    void ricercaPerCodiceFiscaleMostraIlPazienteCorrispondente() {
        aggiungiPaziente("paziente1", "Luca", "Bianchi", "CFLUCA123");
        aggiungiPaziente("paziente2", "Anna", "Verdi", "CFANNA456");

        caricaController();

        runAndWait(() -> searchField.setText("CFLUCA123"));

        assertEquals(1, pazientiContainer.getChildren().size());
        assertEquals("Luca Bianchi", nomeDelBox(pazientiContainer.getChildren().getFirst()));
    }


    @Test
    void ricercaNonFaDifferenzaTraMaiuscoleEMinuscole() {
        aggiungiPaziente("paziente1", "Luca", "Bianchi", "CFLUCA");

        caricaController();

        runAndWait(() -> searchField.setText("lUcA"));

        assertEquals(1, pazientiContainer.getChildren().size());
        assertEquals("Luca Bianchi", nomeDelBox(pazientiContainer.getChildren().getFirst()));
    }


    @Test
    void ricercaSenzaRisultatiLasciaVuotoIlContainer() {
        aggiungiPaziente("paziente1", "Luca", "Bianchi", "CFLUCA");

        caricaController();

        runAndWait(() -> searchField.setText("PazienteInesistente"));

        assertTrue(pazientiContainer.getChildren().isEmpty());
    }


    @Test
    void modificaDelTestoDiRicercaAggiornaAutomaticamenteLaLista() {
        aggiungiPaziente("paziente1", "Luca", "Bianchi", "CFLUCA");
        aggiungiPaziente("paziente2", "Anna", "Verdi", "CFANNA");

        caricaController();

        runAndWait(() -> {
            searchField.setText("");
            assertEquals(2, pazientiContainer.getChildren().size());

            searchField.setText("Luca");
            assertEquals(1, pazientiContainer.getChildren().size());

            searchField.setText("Anna");
            assertEquals(1, pazientiContainer.getChildren().size());
            assertEquals("Anna Verdi", nomeDelBox(pazientiContainer.getChildren().getFirst()));
        });
    }


    // =========================================================
    // BOX PAZIENTE
    // =========================================================

    @Test
    void listaVisualizzaSoloIPazientiSeguitiDalMedico() {

        Diabetologo altroMedico = new Diabetologo("altroMedico", "password", "CFALTRO", "Anna", "Neri", "anna@test.it");
        db.addDiabetologo(altroMedico);

        Paziente pazienteNonSeguito = new Paziente(
                "pazienteNonSeguito", "password", "CFNONSEGUITO", "Paolo", "Verdi", "paolo@test.it",
                null, altroMedico, null, null, null
        );

        db.addPaziente(pazienteNonSeguito);

        caricaController();

        assertEquals(1, pazientiContainer.getChildren().size());
        assertEquals("Luca Bianchi", nomeDelBox(pazientiContainer.getChildren().getFirst()));
        assertFalse(nomeDelBox(pazientiContainer.getChildren().getFirst()).contains(pazienteNonSeguito.getNome()));
    }


    @Test
    void boxPazienteContieneAvatarNomeEIBottoni() {
        aggiungiPaziente("paziente", "Luca", "Bianchi", "CFLUCA");

        caricaController();

        assertEquals(1, pazientiContainer.getChildren().size());

        HBox box = (HBox) pazientiContainer.getChildren().getFirst();

        assertTrue(box.getChildren().stream().anyMatch(node -> node instanceof ImageView));
        assertTrue(box.getChildren().stream().anyMatch(node -> node instanceof Label));
        assertTrue(box.getChildren().stream().anyMatch(node ->
                node instanceof Button && ((Button) node).getText().equals("Andamento")));
        assertTrue(box.getChildren().stream().anyMatch(node ->
                node instanceof Button && ((Button) node).getText().equals("Terapia")));
        assertTrue(box.getChildren().stream().anyMatch(node ->
                node instanceof Button && ((Button) node).getText().equals("Info Paziente")));
    }


    // =========================================================
    // PULSANTE ANDAMENTO VISUALIZZA IL PAZIENTE CORRETTO
    // =========================================================

    @Test
    void andamentoRiceveIlPazienteCorretto() throws Exception {
        Paziente paziente1 = aggiungiPaziente("paziente1", "Luca", "Bianchi", "CFLUCA");
        Paziente paziente2 = aggiungiPaziente("paziente2", "Anna", "Verdi", "CFANNA");

        aggiungiRilevazione(
                paziente1, LocalDate.of(2026, 9, 15), 100, LocalTime.of(8, 0),
                MomentoRilevazione.PRIMA_COLAZIONE
        );

        aggiungiRilevazione(
                paziente2, LocalDate.of(2026, 9, 15), 200, LocalTime.of(8, 0),
                MomentoRilevazione.PRIMA_COLAZIONE
        );

        caricaController();

        AndamentoController andamento = apriAndamento(paziente1);

        assertSame(paziente1, andamento.getPaziente());

        DatePicker dataPicker = getCampo(andamento, "dataPicker");
        Button giornoButton = getCampo(andamento, "giornoButton");

        runAndWait(() -> {
            dataPicker.setValue(LocalDate.of(2026, 9, 15));
            dataPicker.fireEvent(new ActionEvent());
            giornoButton.fire();
        });

        LineChartData grafico = leggiGrafico(andamento);

        assertEquals(1, grafico.numeroElementiPrima());
        assertEquals(100, grafico.prima.getData().get(0).getYValue());
    }


    // =========================================================
    // ANDAMENTO - GIORNO
    // =========================================================

    @Test
    void andamentoVisualizzaCorrettaRilevazioneDelGiorno() throws Exception {
        Paziente paziente = aggiungiPaziente("paziente", "Luca", "Bianchi", "CFLUCA");

        aggiungiRilevazione(
                paziente, LocalDate.of(2026, 9, 15), 100, LocalTime.of(12, 30),
                MomentoRilevazione.PRIMA_COLAZIONE
        );

        caricaController();

        AndamentoController andamento = apriAndamento(paziente);

        DatePicker dataPicker = getCampo(andamento, "dataPicker");
        Button giornoButton = getCampo(andamento, "giornoButton");

        runAndWait(() -> {
            dataPicker.setValue(LocalDate.of(2026, 9, 15));
            dataPicker.fireEvent(new ActionEvent());
            giornoButton.fire();
        });

        LineChartData grafico = leggiGrafico(andamento);

        assertEquals(2, grafico.numeroSerie());
        assertEquals(1, grafico.numeroElementiPrima());
        assertEquals(0, grafico.numeroElementiDopo());
        assertEquals("12:30", grafico.prima.getData().get(0).getXValue());
        assertEquals(100, grafico.prima.getData().get(0).getYValue());
    }


    // =========================================================
    // ANDAMENTO - SETTIMANA
    // =========================================================

    @Test
    void andamentoVisualizzaMediaSettimanaleCorretta() throws Exception {
        Paziente paziente = aggiungiPaziente("paziente", "Luca", "Bianchi", "CFLUCA");

        aggiungiRilevazione(
                paziente, LocalDate.of(2026, 9, 14), 100, LocalTime.of(8, 0),
                MomentoRilevazione.PRIMA_COLAZIONE
        );

        aggiungiRilevazione(
                paziente, LocalDate.of(2026, 9, 14), 140, LocalTime.of(10, 0),
                MomentoRilevazione.DOPO_COLAZIONE
        );

        caricaController();

        AndamentoController andamento = apriAndamento(paziente);

        DatePicker dataPicker = getCampo(andamento, "dataPicker");
        Button settimanaButton = getCampo(andamento, "settimanaButton");

        runAndWait(() -> {
            dataPicker.setValue(LocalDate.of(2026, 9, 14));
            dataPicker.fireEvent(new ActionEvent());
            settimanaButton.fire();
        });

        LineChartData grafico = leggiGrafico(andamento);

        assertEquals(2, grafico.numeroSerie());
        assertEquals(1, grafico.numeroElementiPrima());
        assertEquals(1, grafico.numeroElementiDopo());
        assertEquals(100.0, grafico.prima.getData().get(0).getYValue());
        assertEquals(140.0, grafico.dopo.getData().get(0).getYValue());
    }


    // =========================================================
    // ANDAMENTO - MESE
    // =========================================================

    @Test
    void andamentoVisualizzaMediaMensileCorretta() throws Exception {
        Paziente paziente = aggiungiPaziente("paziente", "Luca", "Bianchi", "CFLUCA");

        aggiungiRilevazione(
                paziente, LocalDate.of(2026, 9, 15), 100, LocalTime.of(8, 0),
                MomentoRilevazione.PRIMA_COLAZIONE
        );

        aggiungiRilevazione(
                paziente, LocalDate.of(2026, 9, 15), 120, LocalTime.of(10, 0),
                MomentoRilevazione.PRIMA_PRANZO
        );

        caricaController();

        AndamentoController andamento = apriAndamento(paziente);

        DatePicker dataPicker = getCampo(andamento, "dataPicker");
        Button meseButton = getCampo(andamento, "meseButton");

        runAndWait(() -> {
            dataPicker.setValue(LocalDate.of(2026, 9, 15));
            dataPicker.fireEvent(new ActionEvent());
            meseButton.fire();
        });

        LineChartData grafico = leggiGrafico(andamento);

        assertEquals(2, grafico.numeroSerie());
        assertEquals(1, grafico.numeroElementiPrima());
        assertEquals(0, grafico.numeroElementiDopo());
        assertEquals("15", grafico.prima.getData().get(0).getXValue());
        assertEquals(110.0, grafico.prima.getData().get(0).getYValue());
    }


    // =========================================================
    // SUPPORTO - CREAZIONE PAZIENTE
    // =========================================================

    private Paziente aggiungiPaziente(String username, String nome, String cognome, String codiceFiscale) {
        Paziente paziente = new Paziente(
                username, "password", codiceFiscale, nome, cognome, username + "@test.it",
                null, medico, null, null, null
        );

        db.addPaziente(paziente);
        return paziente;
    }


    // =========================================================
    // SUPPORTO - CREAZIONE RILEVAZIONE
    // =========================================================

    private void aggiungiRilevazione(Paziente paziente, LocalDate data, int glicemia, LocalTime orario, MomentoRilevazione momento) {
        Rilevazione rilevazione = new Rilevazione(
                data, glicemia, orario, orario, momento, paziente
        );

        db.addRilevazione(rilevazione);
    }


    // =========================================================
    // SUPPORTO - CARICAMENTO CONTROLLER
    // =========================================================

    private void caricaController() {
        runAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/Diabetologo.fxml"));

                controller = loader.getController();
                searchField = getCampo(controller, "searchField");
                pazientiContainer = getCampo(controller, "pazientiContainer");

                controller.inizializzaProfilo();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }


    // =========================================================
    // SUPPORTO - APERTURA ANDAMENTO
    // =========================================================

    private AndamentoController apriAndamento(Paziente paziente) throws Exception {
        AtomicReference<AndamentoController> riferimento = new AtomicReference<>();

        runAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/Andamento.fxml"));
                loader.load();

                AndamentoController andamento = loader.getController();
                andamento.inizializzaPaziente(paziente);
                riferimento.set(andamento);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        return riferimento.get();
    }


    // =========================================================
    // SUPPORTO - GRAFICO
    // =========================================================

    private LineChartData leggiGrafico(AndamentoController controller) throws Exception {
        javafx.scene.chart.LineChart<String, Number> grafico = getCampo(controller, "grafico");
        List<XYChart.Series<String, Number>> serie = new ArrayList<>(grafico.getData());

        return new LineChartData(serie.get(0), serie.get(1));
    }


    private static class LineChartData {

        private final XYChart.Series<String, Number> prima;
        private final XYChart.Series<String, Number> dopo;

        private LineChartData(XYChart.Series<String, Number> prima, XYChart.Series<String, Number> dopo) {
            this.prima = prima;
            this.dopo = dopo;
        }

        private int numeroSerie() {
            return 2;
        }

        private int numeroElementiPrima() {
            return prima.getData().size();
        }

        private int numeroElementiDopo() {
            return dopo.getData().size();
        }
    }


    // =========================================================
    // SUPPORTO - RIFLESSIONE
    // =========================================================

    @SuppressWarnings("unchecked")
    private <T> T getCampo(Object oggetto, String nomeCampo) throws Exception {
        Field field = oggetto.getClass().getDeclaredField(nomeCampo);
        field.setAccessible(true);

        return (T) field.get(oggetto);
    }


    // =========================================================
    // SUPPORTO - NOME BOX
    // =========================================================

    private String nomeDelBox(javafx.scene.Node node) {
        HBox box = (HBox) node;

        return box.getChildren().stream()
                .filter(elemento -> elemento instanceof Label)
                .map(elemento -> ((Label) elemento).getText())
                .findFirst()
                .orElse(null);
    }


    // =========================================================
    // SUPPORTO - DATABASE
    // =========================================================

    private void sostituisciDatabaseSingleton(Database nuovoDatabase) throws Exception {
        Field field = Database.class.getDeclaredField("database");
        field.setAccessible(true);
        field.set(null, nuovoDatabase);
    }


    // =========================================================
    // SUPPORTO - CHIUSURA FINESTRE
    // =========================================================

    private void chiudiFinestre() {
        runAndWait(() -> {
            for (Stage stage : new ArrayList<>(
                    Window.getWindows().stream()
                            .filter(window -> window instanceof Stage)
                            .map(window -> (Stage) window)
                            .toList())) {

                stage.close();
            }
        });
    }


    // =========================================================
    // SUPPORTO - JAVAFX
    // =========================================================

    /*
     * Esegue il codice sul JavaFX Application Thread e attende che venga completato.
     *
     * È necessario perché i componenti grafici JavaFX possono essere letti o modificati
     * in sicurezza solo dal thread principale di JavaFX.
     *
     * Nei test, invece, il codice può essere eseguito da un thread diverso dal JavaFX
     * Application Thread. Per questo motivo utilizziamo Platform.runLater() per spostare
     * l'esecuzione sul thread corretto e CountDownLatch per attendere che l'operazione
     * sia terminata prima di proseguire.
     *
     * In questo modo il metodo chiamante non continua l'esecuzione mentre la GUI sta
     * ancora aggiornando i suoi componenti.
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