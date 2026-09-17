package application.classiGeneriche;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class GestoreAlertTest {

    @TempDir
    Path tempDir;

    private Database db;

    private Diabetologo medico;
    private Paziente paziente;
    private Terapia terapia;


    // =========================================================
    // SETUP DATABASE
    // =========================================================

    @BeforeEach
    void setUp() throws Exception {
    	db = creaDatabaseTemporaneo();
        sostituisciDatabaseSingleton(db);

        medico = new Diabetologo("medicoTest","password","CFMEDICO01","Mario","Rossi","medico@test.it");
        db.addDiabetologo(medico);

        paziente = new Paziente("pazienteTest","password","CFPAZIENTE01","Anna","Verdi","anna@test.it",null,medico,null,null,null);
        db.addPaziente(paziente);

        terapia = creaTerapia("Metformina",500,2,medico,paziente,"Dopo i pasti");
        db.addTerapia(terapia);
    }


    @AfterEach
    void tearDown() throws Exception {
        sostituisciDatabaseSingleton(null);
    }
    

    // =========================================================
    // METODI PER IL DB
    // =========================================================

    private void sostituisciDatabaseSingleton(Database nuovoDatabase) throws Exception {
        Field field =Database.class.getDeclaredField("database");
        field.setAccessible(true);
        field.set(null,nuovoDatabase);
    }
    
    private Database creaDatabaseTemporaneo() throws Exception {
	    Constructor<Database> constructor =Database.class.getDeclaredConstructor(String.class);
	    constructor.setAccessible(true);
	
	    return constructor.newInstance(tempDir.resolve("test-database.data").toString());
	}


    // =========================================================
    // VERIFICA ASSUNZIONI GIORNALIERE
    // =========================================================

    @Test
    void verificaAssunzioniGiornaliereNonInviaPromemoriaSeTutteLeAssunzioniSonoStateFatte() {
        aggiungiAssunzione(LocalDate.now(),LocalTime.of(8, 0));
        aggiungiAssunzione(LocalDate.now(),LocalTime.of(20, 0));
        
        GestoreAlert.verificaAssunzioniGiornaliere(paziente);

        assertTrue(db.getMessaggiFromPaziente(paziente).isEmpty());
    }


    @Test
    void verificaAssunzioniGiornaliereInviaPromemoriaSeMancaUnaAssunzione() {
        aggiungiAssunzione(LocalDate.now(),LocalTime.of(8, 0));

        GestoreAlert.verificaAssunzioniGiornaliere(paziente);

        List<Messaggio> messaggi =db.getMessaggiFromPaziente(paziente);
        assertEquals(1, messaggi.size());

        Messaggio messaggio = messaggi.get(0);

        assertEquals(TipoAlert.SISTEMA_PAZIENTE,messaggio.getTipo());
        assertEquals(UrgenzaAlert.LOW,messaggio.getUrgenza());
        assertEquals("Ricorda di completare le assunzioni di METFORMINA: registrate 1 su 2 previste per oggi.",messaggio.getTesto());
    }


    @Test
    void verificaAssunzioniGiornaliereInviaPromemoriaSeNonCiSonoAssunzioni() {
        GestoreAlert.verificaAssunzioniGiornaliere(paziente);

        List<Messaggio> messaggi =db.getMessaggiFromPaziente(paziente);
        
        assertEquals(1, messaggi.size());
        assertEquals(UrgenzaAlert.LOW,messaggi.get(0).getUrgenza());
        assertEquals(TipoAlert.SISTEMA_PAZIENTE,messaggi.get(0).getTipo());
    }


    @Test
    void verificaAssunzioniGiornaliereNonInviaDueVolteLoStessoPromemoria() {
        aggiungiAssunzione(LocalDate.now(),LocalTime.of(8, 0));

        GestoreAlert.verificaAssunzioniGiornaliere(paziente);
        GestoreAlert.verificaAssunzioniGiornaliere(paziente);

        assertEquals(1,db.getMessaggiFromPaziente(paziente).size());
    }


    @Test
    void verificaAssunzioniGiornaliereIgnoraLeAssunzioniDiAltriGiorni() {
        aggiungiAssunzione(LocalDate.now().minusDays(1),LocalTime.of(8, 0));
        aggiungiAssunzione(LocalDate.now().plusDays(1),LocalTime.of(8, 0));

        GestoreAlert.verificaAssunzioniGiornaliere(paziente);

        List<Messaggio> messaggi =db.getMessaggiFromPaziente(paziente);

        assertEquals(1, messaggi.size());
        assertTrue(messaggi.get(0).getTesto().contains("registrate 0 su 2"));
    }


    @Test
    void verificaAssunzioniGiornaliereGestiscePiuTerapieSeparatamente() {
        Terapia secondaTerapia = creaTerapia("Insulina",100,1,medico,paziente,"Prima di cena");
        db.addTerapia(secondaTerapia);

        // Prima terapia: 1 su 2 → manca una assunzione
        aggiungiAssunzione(LocalDate.now(),LocalTime.of(8, 0));

        // Seconda terapia: nessuna assunzione → manca quella prevista
        GestoreAlert.verificaAssunzioniGiornaliere(paziente);

        List<Messaggio> messaggi =db.getMessaggiFromPaziente(paziente);

        assertEquals(2, messaggi.size());
        assertTrue(messaggi.stream().anyMatch(m ->m.getTesto().contains("METFORMINA")));
        assertTrue(messaggi.stream().anyMatch(m ->m.getTesto().contains("INSULINA")));
    }


    // =========================================================
    // VERIFICA ADERENZA TERAPIA
    // =========================================================

    @Test
    void verificaAderenzaTerapiaInviaAlertDopoTreGiorniMancati() {
        LocalDate ieri = LocalDate.now().minusDays(1);
        LocalDate dueGiorniFa = LocalDate.now().minusDays(2);
        LocalDate treGiorniFa = LocalDate.now().minusDays(3);
        
        GestoreAlert.verificaAderenzaTerapia(paziente,terapia);

        List<Messaggio> messaggi =db.getMessaggiFromMedico(medico);
        assertEquals(1, messaggi.size());

        Messaggio messaggio = messaggi.get(0);
        
        assertEquals(TipoAlert.SISTEMA_MEDICO,messaggio.getTipo());
        assertEquals(UrgenzaAlert.MEDIUM,messaggio.getUrgenza());
        assertEquals(paziente,messaggio.getPaziente());
        assertTrue(messaggio.getTesto().contains("Il paziente Anna Verdi"));
        assertTrue(messaggio.getTesto().contains("da almeno 3 giorni consecutivi."));
    }


    @Test
    void verificaAderenzaTerapiaNonInviaAlertConMenoDiTreGiorniMancati() {
        LocalDate ieri = LocalDate.now().minusDays(1);
        LocalDate dueGiorniFa = LocalDate.now().minusDays(2);

        // Ieri: 1 su 2 → giorno mancato
        aggiungiAssunzione(ieri, LocalTime.of(8, 0));

        // Due giorni fa: 2 su 2 → giornata completata
        aggiungiAssunzione(dueGiorniFa, LocalTime.of(8, 0));
        aggiungiAssunzione(dueGiorniFa, LocalTime.of(20, 0));

        GestoreAlert.verificaAderenzaTerapia(paziente, terapia);

        assertTrue(db.getMessaggiFromMedico(medico).isEmpty());
    }


    @Test
    void verificaAderenzaTerapiaNonInviaAlertSeLaTerapiaECompleta() {
        LocalDate ieri = LocalDate.now().minusDays(1);
        LocalDate dueGiorniFa = LocalDate.now().minusDays(2);
        LocalDate treGiorniFa = LocalDate.now().minusDays(3);

        aggiungiAssunzione(ieri,LocalTime.of(8, 0));
        aggiungiAssunzione(ieri,LocalTime.of(20, 0));
        aggiungiAssunzione(dueGiorniFa,LocalTime.of(8, 0));
        aggiungiAssunzione(dueGiorniFa,LocalTime.of(20, 0));
        aggiungiAssunzione(treGiorniFa,LocalTime.of(8, 0));
        aggiungiAssunzione(treGiorniFa,LocalTime.of(20, 0));

        GestoreAlert.verificaAderenzaTerapia(paziente,terapia);

        assertTrue(db.getMessaggiFromMedico(medico).isEmpty());
    }


    @Test
    void verificaAderenzaTerapiaNonInviaDueVolteLoStessoAlert() {
        GestoreAlert.verificaAderenzaTerapia(paziente,terapia);
        GestoreAlert.verificaAderenzaTerapia(paziente,terapia);

        assertEquals(1,db.getMessaggiFromMedico(medico).size());
    }

    @Test
    void verificaAderenzaTerapiaControllaTreGiorniDiversi() {

        LocalDate ieri = LocalDate.now().minusDays(1);
        LocalDate dueGiorniFa = LocalDate.now().minusDays(2);
        LocalDate treGiorniFa = LocalDate.now().minusDays(3);

        /*
         * Ieri: terapia completata → 0 giorni mancati
         * Due giorni fa: terapia mancante → 1 giorno mancato
         * Tre giorni fa: terapia completata → 1 giorno mancato
         *
         * Totale = 1, quindi NON deve partire l'alert.
         *
         * Questo test serve anche a verificare che il codice
         * passi realmente da un giorno al precedente.
         */

        aggiungiAssunzione(ieri,LocalTime.of(8, 0));
        aggiungiAssunzione(ieri,LocalTime.of(20, 0));
        aggiungiAssunzione(treGiorniFa,LocalTime.of(8, 0));
        aggiungiAssunzione(treGiorniFa,LocalTime.of(20, 0));
        
        GestoreAlert.verificaAderenzaTerapia(paziente,terapia);
        
        assertTrue(db.getMessaggiFromMedico(medico).isEmpty());
    }


    // =========================================================
    // VERIFICA GLICEMIA - PRE PASTO
    // =========================================================

    @Test
    void verificaGlicemiaPrePastoSotto70InviaAlertHigh() {
        Rilevazione rilevazione =creaRilevazione(69,MomentoRilevazione.PRIMA_COLAZIONE);

        GestoreAlert.verificaGlicemia(rilevazione);

        verificaAlertGlicemia(UrgenzaAlert.HIGH,69,MomentoRilevazione.PRIMA_COLAZIONE);
    }


    @Test
    void verificaGlicemiaPrePastoA70InviaAlertLow() {
        Rilevazione rilevazione =creaRilevazione(70,MomentoRilevazione.PRIMA_COLAZIONE);

        GestoreAlert.verificaGlicemia(rilevazione);

        verificaAlertGlicemia(UrgenzaAlert.LOW,70,MomentoRilevazione.PRIMA_COLAZIONE);
    }


    @Test
    void verificaGlicemiaPrePastoA79InviaAlertLow() {
        Rilevazione rilevazione =creaRilevazione(79,MomentoRilevazione.PRIMA_COLAZIONE);

        GestoreAlert.verificaGlicemia(rilevazione);

        verificaAlertGlicemia(UrgenzaAlert.LOW,79,MomentoRilevazione.PRIMA_COLAZIONE);
    }


    @Test
    void verificaGlicemiaPrePastoA80NonInviaAlert() {
        Rilevazione rilevazione =creaRilevazione(80,MomentoRilevazione.PRIMA_COLAZIONE);

        GestoreAlert.verificaGlicemia(rilevazione);

        assertTrue(db.getMessaggiFromMedico(medico).isEmpty());
    }


    @Test
    void verificaGlicemiaPrePastoA130NonInviaAlert() {
        Rilevazione rilevazione =creaRilevazione(130,MomentoRilevazione.PRIMA_PRANZO);

        GestoreAlert.verificaGlicemia(rilevazione);

        assertTrue(db.getMessaggiFromMedico(medico).isEmpty());
    }


    @Test
    void verificaGlicemiaPrePastoA131InviaAlertMedium() {
        Rilevazione rilevazione =creaRilevazione(131,MomentoRilevazione.PRIMA_PRANZO);

        GestoreAlert.verificaGlicemia(rilevazione);

        verificaAlertGlicemia(UrgenzaAlert.MEDIUM,131,MomentoRilevazione.PRIMA_PRANZO);
    }


    @Test
    void verificaGlicemiaPrePastoA249InviaAlertMedium() {
        Rilevazione rilevazione =creaRilevazione(249,MomentoRilevazione.PRIMA_CENA);

        GestoreAlert.verificaGlicemia(rilevazione);

        verificaAlertGlicemia(UrgenzaAlert.MEDIUM,249,MomentoRilevazione.PRIMA_CENA);
    }


    @Test
    void verificaGlicemiaPrePastoA250InviaAlertHigh() {
        Rilevazione rilevazione =creaRilevazione(250,MomentoRilevazione.PRIMA_CENA);

        GestoreAlert.verificaGlicemia(rilevazione);

        verificaAlertGlicemia(UrgenzaAlert.HIGH,250,MomentoRilevazione.PRIMA_CENA);
    }


    // =========================================================
    // VERIFICA GLICEMIA - POST PASTO
    // =========================================================

    @Test
    void verificaGlicemiaPostPastoSotto70InviaAlertHigh() {
        Rilevazione rilevazione =creaRilevazione(69,MomentoRilevazione.DOPO_COLAZIONE);

        GestoreAlert.verificaGlicemia(rilevazione);

        verificaAlertGlicemia(UrgenzaAlert.HIGH,69,MomentoRilevazione.DOPO_COLAZIONE);
    }


    @Test
    void verificaGlicemiaPostPastoA180NonInviaAlert() {
        Rilevazione rilevazione =creaRilevazione(180,MomentoRilevazione.DOPO_PRANZO);

        GestoreAlert.verificaGlicemia(rilevazione);

        assertTrue(db.getMessaggiFromMedico(medico).isEmpty());
    }


    @Test
    void verificaGlicemiaPostPastoA181InviaAlertMedium() {
        Rilevazione rilevazione =creaRilevazione(181,MomentoRilevazione.DOPO_PRANZO);

        GestoreAlert.verificaGlicemia(rilevazione);

        verificaAlertGlicemia(UrgenzaAlert.MEDIUM,181,MomentoRilevazione.DOPO_PRANZO);
    }


    @Test
    void verificaGlicemiaPostPastoA249InviaAlertMedium() {
        Rilevazione rilevazione =creaRilevazione(249,MomentoRilevazione.DOPO_CENA);

        GestoreAlert.verificaGlicemia(rilevazione);

        verificaAlertGlicemia(UrgenzaAlert.MEDIUM,249,MomentoRilevazione.DOPO_CENA);
    }


    @Test
    void verificaGlicemiaPostPastoA250InviaAlertHigh() {
        Rilevazione rilevazione =creaRilevazione(250,MomentoRilevazione.DOPO_CENA);

        GestoreAlert.verificaGlicemia(rilevazione);

        verificaAlertGlicemia(UrgenzaAlert.HIGH,250,MomentoRilevazione.DOPO_CENA);
    }



    // =========================================================
    // VERIFICA TUTTI I PAZIENTI
    // =========================================================

    @Test
    void verificaTuttiIPazientiControllaIPazientiDelMedico() {
        GestoreAlert.verificaTuttiIPazienti(medico);

        /*
         * La terapia prevede due assunzioni al giorno.
         * Non essendocene nessuna, deve essere creato
         * almeno il promemoria per il paziente.
         */

        List<Messaggio> messaggi =db.getMessaggiFromPaziente(paziente);

        assertEquals(1, messaggi.size());
        assertEquals(TipoAlert.SISTEMA_PAZIENTE,messaggi.get(0).getTipo());
    }


    @Test
    void verificaTuttiIPazientiNonControllaPazientiDiUnAltroMedico() {
        Diabetologo altroMedico = new Diabetologo("altroMedico","password","CFMEDICO02","Luca","Bianchi","altro@test.it");
        db.addDiabetologo(altroMedico);

        Paziente altroPaziente = new Paziente("altroPaziente","password","CFPAZIENTE04","Marco","Neri","marco@test.it",null,altroMedico,null,null,null);
        db.addPaziente(altroPaziente);

        Terapia altraTerapia = creaTerapia("Insulina",100,1,altroMedico,altroPaziente,"Prima di cena");
        db.addTerapia(altraTerapia);

        GestoreAlert.verificaTuttiIPazienti(medico);

        assertTrue(db.getMessaggiFromPaziente(altroPaziente).isEmpty());
    }


    @Test
    void verificaTuttiIPazientiGestiscePazienteSenzaTerapie() {
        Paziente pazienteSenzaTerapie = new Paziente("senzaTerapie","password","CFPAZIENTE05","Giulia","Neri","giulia@test.it",null,medico,null,null,null);
        db.addPaziente(pazienteSenzaTerapie);

        GestoreAlert.verificaTuttiIPazienti(medico);

        List<Messaggio> messaggi = db.getMessaggiFromMedico(medico);

        assertTrue(messaggi.stream().noneMatch(m -> m.getPaziente().equals(pazienteSenzaTerapie)));
    }


    // =========================================================
    // METODI DI SUPPORTO
    // =========================================================

    private Terapia creaTerapia(String farmaco,int dose,int numeroAssunzioni,Diabetologo medico,Paziente paziente,String indicazioni) {
        ArrayList<Paziente> pazienti =new ArrayList<>();

        pazienti.add(paziente);

        return new Terapia(farmaco,dose,numeroAssunzioni,medico,pazienti,indicazioni);
    }


    private void aggiungiAssunzione(LocalDate data,LocalTime orario) {
        AssunzioneFarmaco assunzione =new AssunzioneFarmaco(paziente,data,orario,terapia.getDose(),terapia);
        db.addAssunzione(assunzione);
    }


    private Rilevazione creaRilevazione(int glicemia,MomentoRilevazione momento) {
        return new Rilevazione(LocalDate.now(),glicemia,LocalTime.of(12, 0),LocalTime.of(12, 30),momento,paziente);
    }


    private void verificaAlertGlicemia(UrgenzaAlert urgenzaAttesa,int glicemia,MomentoRilevazione momento) {
        List<Messaggio> messaggi =db.getMessaggiFromMedico(medico);

        assertEquals(1,messaggi.size());

        Messaggio messaggio = messaggi.get(0);
        assertEquals(TipoAlert.SISTEMA_MEDICO,messaggio.getTipo());
        assertEquals(UrgenzaAlert.class,messaggio.getUrgenza().getClass());
        assertEquals(urgenzaAttesa,messaggio.getUrgenza());
        assertEquals(paziente,messaggio.getPaziente());
        assertTrue(messaggio.getTesto().contains("Glicemia fuori soglia"));
        assertTrue(messaggio.getTesto().contains(String.valueOf(glicemia)));
        assertTrue(messaggio.getTesto().contains(momento.toString()));
        assertTrue(messaggio.getTesto().contains(paziente.getNome()));
        assertTrue(messaggio.getTesto().contains(paziente.getCognome()));
    }
}