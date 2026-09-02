package application.classiGeneriche;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Ogni test usa una Database creata con il costruttore package-private
 * Database(String fileName), puntato a un file temporaneo che non esiste
 * ancora. Il metodo load() fallisce silenziosamente (comportamento gia' previsto
 * dalla classe) e parte con liste vuote: nessuna dipendenza dal file di
 * produzione, nessuno stato condiviso tra i test.
 */
class DatabaseTest {

    @TempDir
    Path tempDir;

    private Database db;

    @BeforeEach
    void setUp() {
        db = new Database(tempDir.resolve("test-database.data").toString());
    }

    // =========================================================
    // DEDUPLICAZIONE (dipende da Paziente/Diabetologo.equals)
    // =========================================================

    @Test
    void addPazienteNonAggiungeDuplicatiConStessoCodiceFiscale() {
        Paziente p1 = new Paziente("user1", "pw1", "AAAAAA", "Nome1", "Cognome1", "e1@mail.it",
                null, new Diabetologo(), null, null, null);
        Paziente p2 = new Paziente("user2", "pw2", "AAAAAA", "Nome2", "Cognome2", "e2@mail.it",
                null, new Diabetologo(), null, null, null);

        db.addPaziente(p1);
        db.addPaziente(p2);

        assertEquals(1, db.getPazienti().size());
    }

    @Test
    void addDiabetologoNonAggiungeDuplicatiConStessoCodiceFiscale() {
        Diabetologo d1 = new Diabetologo("user1", "pw1", "BBBBBB", "Nome1", "Cognome1", "e1@mail.it");
        Diabetologo d2 = new Diabetologo("user2", "pw2", "BBBBBB", "Nome2", "Cognome2", "e2@mail.it");

        db.addDiabetologo(d1);
        db.addDiabetologo(d2);

        assertEquals(1, db.getDiabetologi().size());
    }

    @Test
    void addResponsabileNonAggiungeDuplicatiCodiceFiscaleUguale(){
        Responsabile r1 = new Responsabile("user1", "pw1", "AAA", "Nome1", "Cognome1", "email1");
        Responsabile r2 = new Responsabile("user2", "pw2", "AAA", "Nome2", "Cognome2", "email2");
        db.addResponsabile(r1);
        db.addResponsabile(r2);
        assertEquals(1, db.getResponsabili().size());
    }

    @Test
    void addResponsabileNonAggiungeDuplicatiUsernameUguale(){
        Responsabile r1 = new Responsabile("user", "pw1", "AAA", "Nome1", "Cognome1", "email1");
        Responsabile r2 = new Responsabile("user", "pw2", "BBB", "Nome2", "Cognome2", "email2");
        db.addResponsabile(r1);
        db.addResponsabile(r2);
        assertEquals(1, db.getResponsabili().size());
    }

    // =========================================================
    // FILTRI PER PAZIENTE
    // =========================================================



    @Test
    void getTerapieByPazienteRestituisceSoloLeTerapieDelPaziente() {
        Paziente p1 = new Paziente();
        Paziente p2 = new Paziente("altro1", "pw", "ZZZZZZ", "Altro", "Paziente", "altro@mail.it",
                null, new Diabetologo(), null, null, null);

        ArrayList<Paziente> pazientiT1 = new ArrayList<>();
        pazientiT1.add(p1);

        ArrayList<Paziente> pazientiT2 = new ArrayList<>();
        pazientiT2.add(p2);

        Terapia t1 = new Terapia("Metformina", 500, 2, new Diabetologo(), pazientiT1, "dopo i pasti");
        Terapia t2 = new Terapia("Insulina", 10, 1, new Diabetologo(), pazientiT2, "prima dei pasti");

        db.addTerapia(t1);
        db.addTerapia(t2);

        ArrayList<Terapia> risultato = db.getTerapieByPaziente(p1);

        assertEquals(1, risultato.size());
        assertTrue(risultato.contains(t1));
    }

    @Test
    void getAssunzioniByPazienteRestituisceSoloQuelleDelPaziente() {
        Paziente p1 = new Paziente();
        Paziente p2 = new Paziente("altro1", "pw", "ZZZZZZ", "Altro", "Paziente", "altro@mail.it",
                null, new Diabetologo(), null, null, null);

        Terapia t = new Terapia("Metformina", 500, 2, new Diabetologo(), new ArrayList<>(), "dopo i pasti");

        AssunzioneFarmaco a1 = new AssunzioneFarmaco(p1, LocalDate.now(), LocalTime.now(), 500, t);
        AssunzioneFarmaco a2 = new AssunzioneFarmaco(p2, LocalDate.now(), LocalTime.now(), 500, t);

        db.addAssunzione(a1);
        db.addAssunzione(a2);

        ArrayList<AssunzioneFarmaco> risultato = db.getAssunzioniByPaziente(p1);

        assertEquals(1, risultato.size());
        assertTrue(risultato.contains(a1));
    }

    @Test
    void getRilevazioniByPazienteRestituisceSoloQuelleDelPaziente() {
        Paziente p1 = new Paziente();
        Paziente p2 = new Paziente("altro1", "pw", "ZZZZZZ", "Altro", "Paziente", "altro@mail.it",
                null, new Diabetologo(), null, null, null);

        Rilevazione r1 = new Rilevazione(LocalDate.now(), 100, LocalTime.now(), LocalTime.now(),
                MomentoRilevazione.PRIMA_COLAZIONE, p1);
        Rilevazione r2 = new Rilevazione(LocalDate.now(), 110, LocalTime.now(), LocalTime.now(),
                MomentoRilevazione.PRIMA_PRANZO, p2);

        db.addRilevazione(r1);
        db.addRilevazione(r2);

        ArrayList<Rilevazione> risultato = db.getRilevazioniByPaziente(p1);

        assertEquals(1, risultato.size());
        assertTrue(risultato.contains(r1));
    }

    @Test
    void getSegnalazioniByPazienteRestituisceSoloQuelleDelPaziente() {
        Paziente p1 = new Paziente();
        Paziente p2 = new Paziente("altro1", "pw", "ZZZZZZ", "Altro", "Paziente", "altro@mail.it",
                null, new Diabetologo(), null, null, null);

        Segnalazione s1 = new Segnalazione(LocalDate.now(), null, p1, "problema 1");
        Segnalazione s2 = new Segnalazione(LocalDate.now(), null, p2, "problema 2");

        db.addSegnalazione(s1);
        db.addSegnalazione(s2);

        ArrayList<Segnalazione> risultato = db.getSegnalazioniByPaziente(p1);

        assertEquals(1, risultato.size());
        assertTrue(risultato.contains(s1));
    }

    // =========================================================
    // LOGIN
    // =========================================================

    @Test
    void loginRestituisceLUtenteConCredenzialiCorrette() {
        Paziente p = new Paziente("mariorossi", "password123", "AAAAAA", "Mario", "Rossi", "mario@mail.it",
                null, new Diabetologo(), null, null, null);

        db.addPaziente(p);

        assertEquals(p, db.login("mariorossi", "password123"));
    }

    @Test
    void loginRestituisceNullConPasswordErrata() {
        Paziente p = new Paziente("mariorossi", "password123", "AAAAAA", "Mario", "Rossi", "mario@mail.it",
                null, new Diabetologo(), null, null, null);

        db.addPaziente(p);

        assertNull(db.login("mariorossi", "passwordSbagliata"));
    }

    @Test
    void loginRestituisceNullConUtenteInesistente() {
        assertNull(db.login("nonEsisto", "qualsiasi"));
    }

    @Test
    void loginFunzionaAncheConUnResponsabile() {
        Responsabile r = new Responsabile("resp1", "pwresp", "RRRRRR", "Nome", "Cognome", "r@mail.it");

        db.addResponsabile(r);

        assertEquals(r, db.login("resp1", "pwresp"));
    }

    // =========================================================
    // UPDATE
    // =========================================================

    @Test
    void updateRilevazioneSostituisceLElementoNellaLista() {
        Paziente p = new Paziente();
        Rilevazione originale = new Rilevazione(LocalDate.now(), 100, LocalTime.now(), LocalTime.now(),
                MomentoRilevazione.PRIMA_COLAZIONE, p);

        db.addRilevazione(originale);

        Rilevazione aggiornata = new Rilevazione(LocalDate.now(), 130, LocalTime.now(), LocalTime.now(),
                MomentoRilevazione.DOPO_COLAZIONE, p);

        db.updateRilevazione(originale, aggiornata);

        ArrayList<Rilevazione> tutte = db.getRilevazioni();

        assertEquals(1, tutte.size());
        assertEquals(130, tutte.getFirst().getLivelloGlicemia());
    }

    @Test
    void updateRilevazioneNonFaNienteSeLElementoNonEPresente() {
        Paziente p = new Paziente();
        Rilevazione nonPresente = new Rilevazione(LocalDate.now(), 100, LocalTime.now(), LocalTime.now(),
                MomentoRilevazione.PRIMA_COLAZIONE, p);
        Rilevazione altra = new Rilevazione(LocalDate.now(), 200, LocalTime.now(), LocalTime.now(),
                MomentoRilevazione.DOPO_CENA, p);

        assertDoesNotThrow(() -> db.updateRilevazione(nonPresente, altra));
        assertTrue(db.getRilevazioni().isEmpty());
    }

    @Test
    void updateSegnalazioneSostituisceLElementoNellaLista() {
        Paziente p = new Paziente();
        Segnalazione originale = new Segnalazione(LocalDate.now(), null, p, "testo originale");

        db.addSegnalazione(originale);

        Segnalazione aggiornata = new Segnalazione(LocalDate.now(), null, p, "testo aggiornato");

        db.updateSegnalazione(originale, aggiornata);

        assertEquals("testo aggiornato", db.getSegnalazioni().getFirst().getTesto());
    }

    @Test
    void getMessaggioFromMedicoRitornaMessaggiDelMedico(){
        Diabetologo d1 = new Diabetologo("a", "a", "a", "a", "a", "a");
        Diabetologo d2 = new Diabetologo("b","b","b","b","b","b");
        Messaggio m1 = new Messaggio(new Paziente(), d1, "messaggio per a", TipoAlert.PAZIENTE_MEDICO, UrgenzaAlert.LOW);
        Messaggio m2 = new Messaggio(null, d2, "messaggio per b", TipoAlert.SISTEMA_MEDICO, UrgenzaAlert.LOW);
        Messaggio m3 = new Messaggio(new Paziente(), d2, "messaggio per paziente", TipoAlert.MEDICO_PAZIENTE, UrgenzaAlert.LOW);
        db.addMessaggio(m1);
        db.addMessaggio(m2);
        db.addMessaggio(m3);
        assertEquals(1, db.getMessaggiFromMedico(d2).size());
        assertTrue(db.getMessaggiFromMedico(d2).contains(m2));
    }

    @Test
    void getMessaggioFromPazienteRitornaMessaggiDelPaziente(){
        Paziente p1 = new Paziente("a", "a", "a", "a", "a", "a", null, new Diabetologo(), null, null, null);
        Paziente p2 = new Paziente("b","b","b","b","b","b", null, new Diabetologo(), null, null, null);
        Messaggio m1 = new Messaggio(p1, new Diabetologo(), "messaggio per a", TipoAlert.MEDICO_PAZIENTE, UrgenzaAlert.LOW);
        Messaggio m2 = new Messaggio(p2, null, "messaggio per b", TipoAlert.SISTEMA_PAZIENTE, UrgenzaAlert.LOW);
        Messaggio m3 = new Messaggio(p2, new Diabetologo(), "messaggio per paziente", TipoAlert.PAZIENTE_MEDICO, UrgenzaAlert.LOW);
        db.addMessaggio(m1);
        db.addMessaggio(m2);
        db.addMessaggio(m3);
        assertEquals(1, db.getMessaggiFromPaziente(p2).size());
        assertTrue(db.getMessaggiFromPaziente(p2).contains(m2));
    
    
    
    
    // =========================================================
    
    // TESTING DEI METODI CHE UTILIZZA IL RESPONSABILE
    
    // =========================================================
    
    
    
    // =========================================================
    // ASSOCIAZIONE PAZIENTE - DIABETOLOGO PER IL SINGOLO PAZIENTE (updatePazienteDiabetologo)
    // =========================================================

    @Test
    void pazienteVieneAssociatoAlDiabetologo() {
        Diabetologo medico = new Diabetologo("medicoAssociazione","password","CFMEDASS","Mario","Rossi","medico@test.it");
        Paziente paziente = new Paziente("pazienteAssociazione","password","CFPAZASS","Luca","Bianchi","paziente@test.it",null,new Diabetologo(),null,null,null);

        db.addDiabetologo(medico);
        db.addPaziente(paziente);

        db.updatePazienteDiabetologo(paziente, medico);

        Paziente risultato = db.getPazienti().stream().filter(p -> p.getUsername().equals("pazienteAssociazione")).findFirst().orElseThrow();
        assertNotNull(risultato.getMedicoDiRiferimento());
        assertEquals(medico.getUsername(),risultato.getMedicoDiRiferimento().getUsername());
    }
    
    // =========================================================
    // ASSOCIAZIONE PAZIENTE - DIABETOLOGO PER PIÚ PAZIENTI (updateDiabetologoPazienti)
    // =========================================================

    @Test
    void pazientiVengonoAssociatiAlloStessoDiabetologo() {
        Diabetologo medico = new Diabetologo("medicoAssociazione","password","CFMEDASS","Mario","Rossi","medico@test.it");
        Paziente paziente1 = new Paziente("paziente1","password","PPPPPPPP","Luca","Bianchi","paziente@test.it",null,new Diabetologo(),null,null,null);
        Paziente paziente2 = new Paziente("paziente2","password","AAAAAAAA","Luca","Bianchi","paziente@test.it",null,new Diabetologo(),null,null,null);
        Paziente paziente3 = new Paziente("paziente3","password","ZZZZZZZZ","Luca","Bianchi","paziente@test.it",null,new Diabetologo(),null,null,null);

        db.addDiabetologo(medico);
        db.addPaziente(paziente1);
        db.addPaziente(paziente2);
        db.addPaziente(paziente3);

        ArrayList<Paziente> selezionati=new ArrayList<>();
        selezionati.add(paziente1);
        selezionati.add(paziente2);
        selezionati.add(paziente3);
        
        db.updateDiabetologoPazienti(medico, selezionati);

        Paziente risultato1 = db.getPazienti().stream().filter(p -> p.getUsername().equals("paziente1")).findFirst().orElseThrow();
        Paziente risultato2 = db.getPazienti().stream().filter(p -> p.getUsername().equals("paziente2")).findFirst().orElseThrow();
        Paziente risultato3 = db.getPazienti().stream().filter(p -> p.getUsername().equals("paziente3")).findFirst().orElseThrow();
        
        assertNotNull(risultato1.getMedicoDiRiferimento());
        assertNotNull(risultato2.getMedicoDiRiferimento());
        assertNotNull(risultato3.getMedicoDiRiferimento());
        
        assertEquals(medico.getUsername(),risultato1.getMedicoDiRiferimento().getUsername());
        assertEquals(medico.getUsername(),risultato2.getMedicoDiRiferimento().getUsername());
        assertEquals(medico.getUsername(),risultato3.getMedicoDiRiferimento().getUsername());
    }

    // =========================================================
    // PAZIENTI SEGUITI DA UN MEDICO
    // =========================================================

    @Test
    void getPazientiByDiabetologoRestituiscePazientiCorretti() {
        Diabetologo medico = new Diabetologo("medicoSeguiti","password","CFMEDSEG","Mario","Rossi","medico@test.it");
        Paziente paziente = new Paziente("pazienteSeguito","password","CFPAZSEG","Luca","Bianchi","paziente@test.it",null,medico,null,null,null);

        db.addDiabetologo(medico);
        db.addPaziente(paziente);

        var pazientiSeguiti =db.getPazientiByDiabetologo(medico);

        assertEquals(1, pazientiSeguiti.size());
        assertTrue(pazientiSeguiti.contains(paziente));
    }

    // =========================================================
    // ELIMINAZIONE PAZIENTE
    // =========================================================

    @Test
    void eliminazionePazienteRimuoveIlPazienteDalDatabase() {
        Paziente paziente = new Paziente("pazienteDelete","password","CFDELETE","Paolo","Verdi","paolo@test.it",null,new Diabetologo(),null,null,null);

        db.addPaziente(paziente);
        assertTrue(db.getPazienti().contains(paziente));

        db.deletePaziente(paziente);
        assertFalse(db.getPazienti().contains(paziente));
    }

    // =========================================================
    // ELIMINAZIONE DIABETOLOGO
    // =========================================================

    @Test
    void eliminazioneDiabetologoRimuoveIlMedicoDalDatabase() {
        Diabetologo medico = new Diabetologo("medicoDelete","password","CFMEDDELETE","Anna","Neri","anna@test.it");
        
        db.addDiabetologo(medico);
        assertTrue(db.getDiabetologi().contains(medico));

        db.deleteDiabetologo(medico);
        assertFalse(db.getDiabetologi().contains(medico));
    }

    // =========================================================
    // CONTROLLO ELIMINAZIONE MEDICO CON PAZIENTI
    // =========================================================

    @Test
    void diabetologoConPazientiNonDovrebbeEssereEliminabile() {
        Diabetologo medico = new Diabetologo("medicoConPaziente","password","CFMEDPAZ","Mario","Rossi","medico@test.it");
        Paziente paziente = new Paziente("pazienteMedico","password","CFPAZMED","Luca","Bianchi","paziente@test.it",null,medico,null,null,null);
        db.addDiabetologo(medico);
        db.addPaziente(paziente);

        assertFalse(db.getPazientiByDiabetologo(medico).isEmpty());
    }
    
    // =========================================================
    // USERNAME GIÁ PRESENTE
    // =========================================================

    @Test
    void usernameEsistenteRiconosceUnUsernameGiaPresente() {
        Diabetologo medico = new Diabetologo("usernameEsistente","password","CFMED07","Mario","Rossi","mario@test.it");
        db.addDiabetologo(medico);
        assertTrue(db.usernameEsistente("usernameEsistente"));
    }

    @Test
    void usernameNonEsistenteRestituisceFalse() {
        assertFalse(db.usernameEsistente("usernameCheNonEsiste"));
    }
}