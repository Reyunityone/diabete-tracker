package application.controller;

import application.classiGeneriche.Database;
import application.classiGeneriche.Diabetologo;
import application.classiGeneriche.Messaggio;
import application.classiGeneriche.Paziente;
import application.classiGeneriche.TipoAlert;
import application.classiGeneriche.UrgenzaAlert;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
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
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class MessaggiTest {

	@TempDir
	Path tempDir;

	private Database db;

	private MessaggiController controller;
	private MessaggioController messaggioController;

	private TextField searchField;
	private VBox messaggiContainer;

	private Label nomeLabel;
	private Label testoLabel;
	private Label urgenzaLabel;


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
	    db = creaDatabaseTemporaneo();
	    sostituisciDatabaseSingleton(db);
	
	    controller = new MessaggiController();
	    messaggioController = new MessaggioController();
	
	    creaComponentiJavaFX();
	    collegaComponentiAlController();
	    collegaComponentiAlMessaggioController();
	}
	
	
	@AfterEach
	void tearDown() throws Exception {
	    sostituisciDatabaseSingleton(null);
	}
	
	
	// =========================================================
	// DATABASE DI TEST
	// =========================================================
	
	private Database creaDatabaseTemporaneo() throws Exception {
	    Constructor<Database> constructor =Database.class.getDeclaredConstructor(String.class);
	    constructor.setAccessible(true);
	
	    return constructor.newInstance(tempDir.resolve("test-database.data").toString());
	}
	
	
	private void sostituisciDatabaseSingleton(Database nuovoDatabase)throws Exception {
	    Field field = Database.class.getDeclaredField("database");
	    field.setAccessible(true);
	    field.set(null, nuovoDatabase);
	}
	
	
	// =========================================================
	// CREA COMPONENTI JAVAFX
	// =========================================================
	
	private void creaComponentiJavaFX() {
	    runAndWait(() -> {
	        searchField = new TextField();
	        messaggiContainer = new VBox();
	        nomeLabel = new Label();
	        testoLabel = new Label();
	        urgenzaLabel = new Label();
	    });
	}
	
	
	// =========================================================
	// COLLEGA COMPONENTI AL CONTROLLER
	// =========================================================
	
	private void collegaComponentiAlController()throws Exception {
	    setField(MessaggiController.class,controller,"searchField",searchField);
	    setField(MessaggiController.class,controller,"messaggiContainer",messaggiContainer);
	}
	
	private void collegaComponentiAlMessaggioController()throws Exception {
	    setField(MessaggioController.class,messaggioController,"nomeLabel",nomeLabel);
	    setField(MessaggioController.class,messaggioController,"testoLabel",testoLabel);
	    setField(MessaggioController.class,messaggioController,"urgenzaLabel",urgenzaLabel);
	}
	
	private void setField(Class<?> classe,Object oggetto,String nome,Object valore)throws Exception {
	    Field field = classe.getDeclaredField(nome);
	    field.setAccessible(true);
	    field.set(oggetto, valore);
	}
	
	
	// =========================================================
	// SUPPORTO CREAZIONE UTENTI
	// =========================================================
	
	private Paziente creaPaziente(String nome,String cognome) {
	    return new Paziente("paziente" + nome,"password","CF" + nome + cognome,nome,cognome,nome + "@test.it",null,new Diabetologo(),null,null,null);
	}
	
	private Diabetologo creaDiabetologo(String nome,String cognome) {
	    return new Diabetologo("medico" + nome,"password","CF" + nome + cognome,nome,cognome,nome + "@test.it");
	}
	
	// =========================================================
	// MESSAGGIO PAZIENTE VERSO IL DIABETOLOGO
	// =========================================================
	
	@Test
	void messaggiDelDiabetologoVengonoVisualizzati() {
	    Paziente paziente = creaPaziente("Mario", "Rossi");
	    Diabetologo medico = creaDiabetologo("Luca", "Bianchi");
	
	    Messaggio messaggio = new Messaggio(paziente,medico,"Messaggio inviato dal paziente al medico.",TipoAlert.PAZIENTE_MEDICO,UrgenzaAlert.MEDIUM);
	
	    runAndWait(() -> {
	        controller.inizializza(List.of(messaggio),() -> {});
	        
	        assertEquals(1,messaggiContainer.getChildren().size());
	    });
	}
	
	
	// =========================================================
	// RICERCA PER MITTENTE
	// =========================================================
	
	@Test
	void ricercaPerMittenteMostraSoloIlMessaggioCorrispondente() {
	    Paziente paziente = creaPaziente("Mario", "Rossi");
	    Diabetologo medico = creaDiabetologo("Luca", "Bianchi");
	
	    Messaggio messaggio1 = new Messaggio(paziente,medico,"Messaggio di Mario.",TipoAlert.PAZIENTE_MEDICO,UrgenzaAlert.LOW);
	    Messaggio messaggio2 = new Messaggio(paziente,medico,"Secondo messaggio.",TipoAlert.PAZIENTE_MEDICO,UrgenzaAlert.MEDIUM);
	
	    runAndWait(() -> {
	        controller.inizializza(List.of(messaggio1, messaggio2),() -> {});
	        assertEquals(2,messaggiContainer.getChildren().size());
	
	        searchField.setText("Mario");
	        assertEquals(2,messaggiContainer.getChildren().size());
	    });
	}
	
	
	// =========================================================
	// RICERCA CASE INSENSITIVE
	// =========================================================
	
	@Test
	void ricercaPerMittenteNonFaDifferenzaTraMaiuscoleEMinuscole() {
	    Paziente paziente = creaPaziente("Mario", "Rossi");
	    Diabetologo medico = creaDiabetologo("Luca", "Bianchi");
	
	    Messaggio messaggio = new Messaggio(paziente,medico,"Test ricerca.",TipoAlert.PAZIENTE_MEDICO,UrgenzaAlert.LOW);
	
	    runAndWait(() -> {
	        controller.inizializza(List.of(messaggio),() -> {});
	        searchField.setText("mArIo");
	
	        assertEquals(1,messaggiContainer.getChildren().size());
	    });
	}
	
	
	// =========================================================
	// RICERCA SENZA RISULTATI
	// =========================================================
	
	@Test
	void ricercaSenzaRisultatiLasciaVuotoIlContainer() {
	    Paziente paziente = creaPaziente("Mario", "Rossi");
	    Diabetologo medico = creaDiabetologo("Luca", "Bianchi");
	
	    Messaggio messaggio = new Messaggio(paziente,medico,"Test ricerca.",TipoAlert.PAZIENTE_MEDICO,UrgenzaAlert.LOW);
	
	    runAndWait(() -> {
	        controller.inizializza(List.of(messaggio),() -> {});
	        searchField.setText("PersonaInesistente");
	
	        assertTrue(messaggiContainer.getChildren().isEmpty());
	    });
	}
	
	
	// =========================================================
	// STRUTTURA BOX MESSAGGIO
	// =========================================================
	
	@Test
	void boxMessaggioContieneAvatarMittenteAnteprimaEApri() {
	    Paziente paziente = creaPaziente("Mario", "Rossi");
	    Diabetologo medico = creaDiabetologo("Luca", "Bianchi");
	
	    Messaggio messaggio = new Messaggio(paziente,medico,"Messaggio di prova.",TipoAlert.PAZIENTE_MEDICO,UrgenzaAlert.LOW);
	
	    runAndWait(() -> {
	        controller.inizializza(List.of(messaggio),() -> {});
	
	        HBox box =(HBox) messaggiContainer.getChildren().getFirst();
	        
	        assertTrue(box.getChildren().stream().anyMatch(node -> node.getClass().getSimpleName().equals("ImageView")));
	        
	        VBox informazioni = (VBox) box.getChildren().get(1);
	        HBox nomeEUrgenza = (HBox) informazioni.getChildren().get(0);
	        assertTrue(nomeEUrgenza.getChildren().stream().anyMatch(node -> node instanceof Label &&((Label) node).getText().equals("Mario Rossi")));
	        
	        assertTrue(box.getChildren().stream().anyMatch(node ->node instanceof Button&& ((Button) node).getText().equals("Apri")));
	    });
	}
	
	// =========================================================
	// LETTO / NON LETTO
	// =========================================================
	
	@Test
	void messaggioNonLettoHaOpacitaCompleta() {
	    Paziente paziente = creaPaziente("Mario", "Rossi");
	    Diabetologo medico = creaDiabetologo("Luca", "Bianchi");
	
	    Messaggio messaggio = new Messaggio(paziente,medico,"Messaggio non letto.",TipoAlert.PAZIENTE_MEDICO,UrgenzaAlert.LOW);
	    
	    runAndWait(() -> {
	        controller.inizializza(List.of(messaggio),() -> {});
	
	        HBox box =(HBox) messaggiContainer.getChildren().getFirst();
	
	        assertFalse(messaggio.isLetto());
	        assertEquals(1.0, box.getOpacity());
	    });
	}
	
	
	@Test
	void messaggioLettoHaOpacitaRidotta() {
	    Paziente paziente = creaPaziente("Mario", "Rossi");
	    Diabetologo medico = creaDiabetologo("Luca", "Bianchi");
	
	    Messaggio messaggio = new Messaggio(paziente,medico,"Messaggio letto.",TipoAlert.PAZIENTE_MEDICO,UrgenzaAlert.LOW);
	    messaggio.setLetto(true);
	
	    runAndWait(() -> {
	        controller.inizializza(List.of(messaggio),() -> {});
	        
	        HBox box =(HBox) messaggiContainer.getChildren().getFirst();
	
	        assertTrue(messaggio.isLetto());
	        assertEquals(0.55, box.getOpacity());
	    });
	}
	
	
	// =========================================================
	// APERTURA MESSAGGIO
	// =========================================================
	
	@Test
	void aperturaMessaggioLoImpostaComeLetto() {
	    Paziente paziente = creaPaziente("Mario", "Rossi");
	    Diabetologo medico = creaDiabetologo("Luca", "Bianchi");
	
	    Messaggio messaggio = new Messaggio(paziente,medico,"Messaggio da leggere.",TipoAlert.PAZIENTE_MEDICO,UrgenzaAlert.LOW);

	    db.addMessaggio(messaggio);
	
	    assertFalse(messaggio.isLetto());
	
	    runAndWait(() -> {	
	        messaggioController.inizializza(messaggio,() -> {});
	
	        assertTrue(messaggio.isLetto());
	        assertTrue(db.getAllMessaggi().getFirst().isLetto()
	        );
	    });
	}
	
	
	@Test
	void aperturaDiUnMessaggioGiaLettoNonEsegueNuovamenteIlCallback() {
	    Paziente paziente = creaPaziente("Mario", "Rossi");
	    Diabetologo medico = creaDiabetologo("Luca", "Bianchi");
	
	    Messaggio messaggio = new Messaggio(paziente,medico,"Messaggio già letto.",TipoAlert.PAZIENTE_MEDICO,UrgenzaAlert.LOW);
	    messaggio.setLetto(true);
	    
	    db.addMessaggio(messaggio);
	
	    AtomicReference<Integer> numeroCallback =new AtomicReference<>(0);
	
	    runAndWait(() -> {
	        messaggioController.inizializza(messaggio,() -> numeroCallback.set(numeroCallback.get() + 1));
	    });
	
	    assertEquals(0, numeroCallback.get());
	}
	
	
	// =========================================================
	// CALLBACK NOTIFICHE
	// =========================================================
	
	@Test
	void aperturaDiUnMessaggioNonLettoEsegueIlCallbackNotifiche() {
	    Paziente paziente = creaPaziente("Mario", "Rossi");
	    Diabetologo medico = creaDiabetologo("Luca", "Bianchi");
	
	    Messaggio messaggio = new Messaggio(paziente,medico,"Messaggio con notifica.",TipoAlert.PAZIENTE_MEDICO,UrgenzaAlert.LOW);
	
	    db.addMessaggio(messaggio);
	
	    AtomicReference<Boolean> callbackEseguito =new AtomicReference<>(false);
	
	    runAndWait(() -> {
	        messaggioController.inizializza(messaggio,() -> callbackEseguito.set(true));
	    });
	
	    assertTrue(callbackEseguito.get());
	}
	
	
	// =========================================================
	// URGENZA
	// =========================================================
	
	@Test
	void messaggioMostraIlLivelloDiUrgenza() {
	    Paziente paziente = creaPaziente("Mario", "Rossi");
	    Diabetologo medico = creaDiabetologo("Luca", "Bianchi");
	
	    Messaggio messaggio = new Messaggio(paziente,medico,"Messaggio urgente.",TipoAlert.PAZIENTE_MEDICO,UrgenzaAlert.HIGH);
	
	    runAndWait(() -> {
	        controller.inizializza(List.of(messaggio),() -> {});
	
	        HBox box =(HBox) messaggiContainer.getChildren().getFirst();
	
	        assertTrue(contieneTesto(box,"[EMERGENZA ALTA]"));
	    });
	}
	
	// =========================================================
	// VISUALIZZAZIONE MESSAGGIO COMPLETO
	// =========================================================
	
	@Test
	void aperturaMessaggioVisualizzaMittenteETestoCompleto() {
	    Paziente paziente = creaPaziente("Mario", "Rossi");
	    Diabetologo medico = creaDiabetologo("Luca", "Bianchi");
	    
	    String testo ="Questo è il testo completo del messaggio.";
	    Messaggio messaggio = new Messaggio(paziente,medico,testo,TipoAlert.PAZIENTE_MEDICO,UrgenzaAlert.MEDIUM);
	
	    db.addMessaggio(messaggio);
	
	    runAndWait(() -> {
	        messaggioController.inizializza(messaggio,() -> {});
	
	        assertEquals("Mario Rossi",nomeLabel.getText());
	        assertEquals(testo,testoLabel.getText());
	        assertEquals("[EMERGENZA MEDIA]",urgenzaLabel.getText());
	    });
	}
	
	
	// =========================================================
	// SUPPORTO
	// =========================================================
	
//	private Label trovaLabel(HBox box,String testo) {
//	    for (Node node : box.getChildren()) {
//	        if (node instanceof VBox vbox) {
//	            for (Node elemento : vbox.getChildren()) {
//	                if (elemento instanceof Label label&& label.getText().equals(testo)) {
//	                    return label;
//	                }
//	            }
//	        }
//	    }
//	
//	    return null;
//	}
//	
//	
//	private Label trovaLabelConTestoCheIniziaCon(HBox box,String testo) {
//	    for (Node node : box.getChildren()) {
//	        if (node instanceof VBox vbox) {
//	            for (Node elemento : vbox.getChildren()) {
//	                if (elemento instanceof Label label&& label.getText().startsWith(testo)) {
//	                    return label;
//	                }
//	            }
//	        }
//	    }
//	
//	    return null;
//	}
	
	
	private boolean contieneTesto(HBox box,String testo) {
	    for (Node node : box.getChildren()) {
	        if (node instanceof VBox vbox) {
	            for (Node elemento : vbox.getChildren()) {
	                if (elemento instanceof HBox hbox) {
	                    for (Node figlio : hbox.getChildren()) {
	                        if (figlio instanceof Label label&& label.getText().equals(testo)) {
	                            return true;
	                        }
	                    }
	                }
	            }
	        }
	    }
	
	    return false;
	}
	
	
	/*
	 * Esegue il codice sul JavaFX Application Thread e attende
	 * che venga completato.
	 *
	 * I componenti JavaFX devono essere modificati sul thread
	 * principale di JavaFX. Nei test, invece, il codice può
	 * essere eseguito da un thread differente.
	 *
	 * Platform.runLater() esegue quindi l'operazione sul thread
	 * JavaFX, mentre CountDownLatch permette al test di attendere
	 * il completamento dell'operazione.
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
	        throw new RuntimeException(errore.get());
	    }
	}
}
