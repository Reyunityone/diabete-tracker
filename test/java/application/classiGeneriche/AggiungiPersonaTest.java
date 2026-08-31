package application.controller;

import application.classiGeneriche.Database;
import application.classiGeneriche.Diabetologo;
import application.classiGeneriche.Paziente;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AggiungiPersonaControllerTest {

    private Database db;
    private AggiungiPersonaController controller;
    private ResponsabileController responsabileController;

    @BeforeEach
    void setUp() {
        controller = new AggiungiPersonaController();
        responsabileController = new ResponsabileController();
        db = Database.getInstance();
    }

    // =========================================================
    // AGGIUNTA DIABETOLOGO
    // =========================================================

    @Test
    void aggiuntaDiabetologoInserisceIlMedicoNelDatabase() {

        Diabetologo medico = new Diabetologo(
                "medicoTest",
                "password123",
                "CFMEDICO01",
                "Mario",
                "Rossi",
                "mario@test.it"
        );

        db.addDiabetologo(medico);

        assertTrue(
                db.getDiabetologi().contains(medico)
        );
    }

    @Test
    void aggiuntaDiabetologoSalvaCorrettamenteTuttiIDati() {

        Diabetologo medico = new Diabetologo(
                "medicoTest2",
                "password123",
                "CFMEDICO02",
                "Luca",
                "Bianchi",
                "luca@test.it"
        );

        db.addDiabetologo(medico);

        Diabetologo risultato = db.getDiabetologi()
                .stream()
                .filter(d -> d.getUsername().equals("medicoTest2"))
                .findFirst()
                .orElse(null);

        assertNotNull(risultato);

        assertEquals("password123", risultato.getPassword());
        assertEquals("CFMEDICO02", risultato.getCodiceFiscale());
        assertEquals("Luca", risultato.getNome());
        assertEquals("Bianchi", risultato.getCognome());
        assertEquals("luca@test.it", risultato.getEmail());
    }

    // =========================================================
    // AGGIUNTA PAZIENTE
    // =========================================================

    @Test
    void aggiuntaPazienteInserisceIlPazienteNelDatabase() {

        Diabetologo medico = new Diabetologo(
                "medicoPaziente",
                "password",
                "CFMEDICO03",
                "Mario",
                "Rossi",
                "medico@test.it"
        );

        db.addDiabetologo(medico);

        Paziente paziente = new Paziente(
                "pazienteTest",
                "password123",
                "CFPAZIENTE01",
                "Anna",
                "Verdi",
                "anna@test.it",
                null,
                medico,
                null,
                null,
                null
        );

        db.addPaziente(paziente);

        assertTrue(
                db.getPazienti().contains(paziente)
        );
    }

    @Test
    void aggiuntaPazienteSalvaCorrettamenteTuttiIDati() {

        Diabetologo medico = new Diabetologo(
                "medicoPaziente2",
                "password",
                "CFMEDICO04",
                "Mario",
                "Rossi",
                "medico2@test.it"
        );

        db.addDiabetologo(medico);

        Paziente paziente = new Paziente(
                "pazienteTest2",
                "password123",
                "CFPAZIENTE02",
                "Giulia",
                "Neri",
                "giulia@test.it",
                null,
                medico,
                null,
                null,
                null
        );

        db.addPaziente(paziente);

        Paziente risultato = db.getPazienti()
                .stream()
                .filter(p -> p.getUsername().equals("pazienteTest2"))
                .findFirst()
                .orElse(null);

        assertNotNull(risultato);

        assertEquals("password123", risultato.getPassword());
        assertEquals("CFPAZIENTE02", risultato.getCodiceFiscale());
        assertEquals("Giulia", risultato.getNome());
        assertEquals("Neri", risultato.getCognome());
        assertEquals("giulia@test.it", risultato.getEmail());
    }

    // =========================================================
    // ASSOCIAZIONE PAZIENTE - DIABETOLOGO
    // =========================================================

    @Test
    void aggiuntaPazienteAssociaCorrettamenteIlMedico() {

        Diabetologo medico = new Diabetologo(
                "medicoAssociazione",
                "password",
                "CFMEDICO05",
                "Mario",
                "Rossi",
                "medico3@test.it"
        );

        db.addDiabetologo(medico);

        Paziente paziente = new Paziente(
                "pazienteAssociazione",
                "password",
                "CFPAZIENTE03",
                "Paolo",
                "Blu",
                "paolo@test.it",
                null,
                medico,
                null,
                null,
                null
        );

        db.addPaziente(paziente);

        db.updatePazienteDiabetologo(paziente, medico);

        Paziente risultato = db.getPazienti()
                .stream()
                .filter(p -> p.getUsername().equals("pazienteAssociazione"))
                .findFirst()
                .orElse(null);

        assertNotNull(risultato);

        assertEquals(
                medico,
                risultato.getMedicoDiRiferimento()
        );
    }

    // =========================================================
    // ASSOCIAZIONE MEDICO - PAZIENTI
    // =========================================================

    @Test
    void aggiuntaDiabetologoPuoAssociarePiuPazienti() {

        Diabetologo medico = new Diabetologo(
                "medicoMulti",
                "password",
                "CFMEDICO06",
                "Andrea",
                "Rossi",
                "andrea@test.it"
        );

        Paziente p1 = new Paziente(
                "paziente1",
                "password",
                "CFPAZ04",
                "Luca",
                "Bianchi",
                "luca@test.it",
                null,
                new Diabetologo(),
                null,
                null,
                null
        );

        Paziente p2 = new Paziente(
                "paziente2",
                "password",
                "CFPAZ05",
                "Marco",
                "Verdi",
                "marco@test.it",
                null,
                new Diabetologo(),
                null,
                null,
                null
        );

        db.addDiabetologo(medico);
        db.addPaziente(p1);
        db.addPaziente(p2);

        var pazientiSelezionati =
                new java.util.ArrayList<Paziente>();

        pazientiSelezionati.add(p1);
        pazientiSelezionati.add(p2);

        db.updateDiabetologoPazienti(
                medico,
                pazientiSelezionati
        );

        assertEquals(
                medico,
                db.getPazienti()
                        .stream()
                        .filter(p -> p.getUsername().equals("paziente1"))
                        .findFirst()
                        .orElseThrow()
                        .getMedicoDiRiferimento()
        );

        assertEquals(
                medico,
                db.getPazienti()
                        .stream()
                        .filter(p -> p.getUsername().equals("paziente2"))
                        .findFirst()
                        .orElseThrow()
                        .getMedicoDiRiferimento()
        );
    }

    // =========================================================
    // USERNAME
    // =========================================================

    @Test
    void usernameEsistenteRiconosceUnUsernameGiaPresente() {

        Diabetologo medico = new Diabetologo(
                "usernameEsistente",
                "password",
                "CFMED07",
                "Mario",
                "Rossi",
                "mario@test.it"
        );

        db.addDiabetologo(medico);

        assertTrue(
                db.usernameEsistente("usernameEsistente")
        );
    }

    @Test
    void usernameNonEsistenteRestituisceFalse() {

        assertFalse(
                db.usernameEsistente("usernameCheNonEsiste")
        );
    }

    // =========================================================
    // DUPLICATI
    // =========================================================

    @Test
    void nonVieneInseritoDueVolteLoStessoDiabetologo() {

        Diabetologo medico = new Diabetologo(
                "medicoDuplicato",
                "password",
                "CFMED08",
                "Mario",
                "Rossi",
                "mario@test.it"
        );

        db.addDiabetologo(medico);
        db.addDiabetologo(medico);

        long numero = db.getDiabetologi()
                .stream()
                .filter(d -> d.getUsername().equals("medicoDuplicato"))
                .count();

        assertEquals(1, numero);
    }

    @Test
    void nonVieneInseritoDueVolteLoStessoPaziente() {

        Paziente paziente = new Paziente(
                "pazienteDuplicato",
                "password",
                "CFPAZ06",
                "Mario",
                "Rossi",
                "mario@test.it",
                null,
                new Diabetologo(),
                null,
                null,
                null
        );

        db.addPaziente(paziente);
        db.addPaziente(paziente);

        long numero = db.getPazienti()
                .stream()
                .filter(p -> p.getUsername().equals("pazienteDuplicato"))
                .count();

        assertEquals(1, numero);
    }
}