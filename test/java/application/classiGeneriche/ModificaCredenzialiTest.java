package application.controller;

import application.classiGeneriche.Database;
import application.classiGeneriche.Diabetologo;
import application.classiGeneriche.Paziente;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ModificaCredenzialiTest {

    private Database db;
    private ModificaCredenzialiController controller;
    private ResponsabileController responsabileController;

    @BeforeEach
    void setUp() {
        controller = new ModificaCredenzialiController();
        responsabileController = new ResponsabileController();
        db = Database.getInstance();
    }

    // =========================================================
    // MODIFICA DIABETOLOGO
    // =========================================================

    @Test
    void modificaDiabetologoAggiornaIdatiPersonali() {

        Diabetologo vecchio = new Diabetologo(
                "medicoVecchio",
                "passwordVecchia",
                "CFVECCHIO",
                "Mario",
                "Rossi",
                "vecchia@test.it"
        );

        db.addDiabetologo(vecchio);

        Diabetologo nuovo = new Diabetologo(
                "medicoNuovo",
                "passwordNuova",
                "CFNUOVO",
                "Luca",
                "Bianchi",
                "nuova@test.it"
        );

        db.updateDiabetologo(vecchio, nuovo);

        assertFalse(
                db.getDiabetologi().contains(vecchio)
        );

        assertTrue(
                db.getDiabetologi().contains(nuovo)
        );
    }

    @Test
    void modificaDiabetologoAggiornaUsername() {

        Diabetologo vecchio = new Diabetologo(
                "usernameVecchio",
                "password",
                "CFMED09",
                "Mario",
                "Rossi",
                "mail@test.it"
        );

        db.addDiabetologo(vecchio);

        Diabetologo nuovo = new Diabetologo(
                "usernameNuovo",
                "password",
                "CFMED09",
                "Mario",
                "Rossi",
                "mail@test.it"
        );

        db.updateDiabetologo(vecchio, nuovo);

        assertTrue(
                db.usernameEsistente("usernameNuovo")
        );

        assertFalse(
                db.usernameEsistente("usernameVecchio")
        );
    }

    @Test
    void modificaDiabetologoAggiornaPassword() {

        Diabetologo vecchio = new Diabetologo(
                "medicoPassword",
                "vecchiaPassword",
                "CFMED10",
                "Mario",
                "Rossi",
                "mail@test.it"
        );

        db.addDiabetologo(vecchio);

        Diabetologo nuovo = new Diabetologo(
                "medicoPassword",
                "nuovaPassword",
                "CFMED10",
                "Mario",
                "Rossi",
                "mail@test.it"
        );

        db.updateDiabetologo(vecchio, nuovo);

        Diabetologo risultato = db.getDiabetologi()
                .stream()
                .filter(d -> d.getUsername().equals("medicoPassword"))
                .findFirst()
                .orElseThrow();

        assertEquals(
                "nuovaPassword",
                risultato.getPassword()
        );
    }

    // =========================================================
    // MODIFICA PAZIENTE
    // =========================================================

    @Test
    void modificaPazienteAggiornaIdatiPersonali() {

        Diabetologo medico = new Diabetologo(
                "medicoPazienteMod",
                "password",
                "CFMED11",
                "Mario",
                "Rossi",
                "medico@test.it"
        );

        db.addDiabetologo(medico);

        Paziente vecchio = new Paziente(
                "pazienteVecchio",
                "passwordVecchia",
                "CFPAZ07",
                "Anna",
                "Rossi",
                "annaVecchia@test.it",
                null,
                medico,
                null,
                null,
                null
        );

        db.addPaziente(vecchio);

        Paziente nuovo = new Paziente(
                "pazienteNuovo",
                "passwordNuova",
                "CFPAZ08",
                "Giulia",
                "Bianchi",
                "giuliaNuova@test.it",
                null,
                medico,
                null,
                null,
                null
        );

        db.updatePaziente(vecchio, nuovo);

        assertFalse(
                db.getPazienti().contains(vecchio)
        );

        assertTrue(
                db.getPazienti().contains(nuovo)
        );
    }

    @Test
    void modificaPazienteAggiornaUsername() {

        Paziente vecchio = new Paziente(
                "pazienteUsernameVecchio",
                "password",
                "CFPAZ09",
                "Mario",
                "Rossi",
                "mail@test.it",
                null,
                new Diabetologo(),
                null,
                null,
                null
        );

        db.addPaziente(vecchio);

        Paziente nuovo = new Paziente(
                "pazienteUsernameNuovo",
                "password",
                "CFPAZ09",
                "Mario",
                "Rossi",
                "mail@test.it",
                null,
                new Diabetologo(),
                null,
                null,
                null
        );

        db.updatePaziente(vecchio, nuovo);

        assertTrue(
                db.getPazienti()
                        .stream()
                        .anyMatch(p ->
                                p.getUsername()
                                        .equals("pazienteUsernameNuovo")
                        )
        );
    }

    // =========================================================
    // CAMBIO MEDICO DEL PAZIENTE
    // =========================================================

    @Test
    void modificaPazientePermetteDiCambiareMedico() {

        Diabetologo medicoVecchio = new Diabetologo(
                "medicoVecchioMod",
                "password",
                "CFMED12",
                "Mario",
                "Rossi",
                "mario@test.it"
        );

        Diabetologo medicoNuovo = new Diabetologo(
                "medicoNuovoMod",
                "password",
                "CFMED13",
                "Luca",
                "Bianchi",
                "luca@test.it"
        );

        db.addDiabetologo(medicoVecchio);
        db.addDiabetologo(medicoNuovo);

        Paziente paziente = new Paziente(
                "pazienteCambioMedico",
                "password",
                "CFPAZ10",
                "Anna",
                "Verdi",
                "anna@test.it",
                null,
                medicoVecchio,
                null,
                null,
                null
        );

        db.addPaziente(paziente);

        db.updatePazienteDiabetologo(
                paziente,
                medicoNuovo
        );

        Paziente risultato = db.getPazienti()
                .stream()
                .filter(p ->
                        p.getUsername()
                                .equals("pazienteCambioMedico")
                )
                .findFirst()
                .orElseThrow();

        assertEquals(
                medicoNuovo,
                risultato.getMedicoDiRiferimento()
        );

        assertNotEquals(
                medicoVecchio,
                risultato.getMedicoDiRiferimento()
        );
    }

    // =========================================================
    // MODIFICA DATI PAZIENTE + MEDICO
    // =========================================================

    @Test
    void modificaPazienteMantieneIlMedicoSeNonCambia() {

        Diabetologo medico = new Diabetologo(
                "medicoMantieni",
                "password",
                "CFMED14",
                "Mario",
                "Rossi",
                "medico@test.it"
        );

        db.addDiabetologo(medico);

        Paziente vecchio = new Paziente(
                "pazienteMantieni",
                "password",
                "CFPAZ11",
                "Anna",
                "Verdi",
                "anna@test.it",
                null,
                medico,
                null,
                null,
                null
        );

        db.addPaziente(vecchio);

        Paziente nuovo = new Paziente(
                "pazienteMantieni",
                "nuovaPassword",
                "CFPAZ11",
                "Anna",
                "Verdi",
                "annaNuova@test.it",
                null,
                medico,
                null,
                null,
                null
        );

        db.updatePaziente(
                vecchio,
                nuovo
        );

        db.updatePazienteDiabetologo(
                nuovo,
                medico
        );

        Paziente risultato = db.getPazienti()
                .stream()
                .filter(p ->
                        p.getUsername()
                                .equals("pazienteMantieni")
                )
                .findFirst()
                .orElseThrow();

        assertEquals(
                medico,
                risultato.getMedicoDiRiferimento()
        );

        assertEquals(
                "nuovaPassword",
                risultato.getPassword()
        );

        assertEquals(
                "annaNuova@test.it",
                risultato.getEmail()
        );
    }

    // =========================================================
    // ASSOCIAZIONE PAZIENTI A DIABETOLOGO
    // =========================================================

    @Test
    void modificaDiabetologoAggiornaLeAssociazioniConIPazienti() {

        Diabetologo medicoVecchio = new Diabetologo(
                "medicoAssVecchio",
                "password",
                "CFMED15",
                "Mario",
                "Rossi",
                "mario@test.it"
        );

        Diabetologo medicoNuovo = new Diabetologo(
                "medicoAssNuovo",
                "password",
                "CFMED16",
                "Luca",
                "Bianchi",
                "luca@test.it"
        );

        db.addDiabetologo(medicoVecchio);
        db.addDiabetologo(medicoNuovo);

        Paziente paziente = new Paziente(
                "pazienteAssociazioneMod",
                "password",
                "CFPAZ12",
                "Paolo",
                "Verdi",
                "paolo@test.it",
                null,
                medicoVecchio,
                null,
                null,
                null
        );

        db.addPaziente(paziente);

        ArrayList<Paziente> pazientiSelezionati =
                new ArrayList<>();

        pazientiSelezionati.add(paziente);

        db.updateDiabetologoPazienti(
                medicoNuovo,
                pazientiSelezionati
        );

        Paziente risultato = db.getPazienti()
                .stream()
                .filter(p ->
                        p.getUsername()
                                .equals("pazienteAssociazioneMod")
                )
                .findFirst()
                .orElseThrow();

        assertEquals(
                medicoNuovo,
                risultato.getMedicoDiRiferimento()
        );
    }

    // =========================================================
    // UPDATE DI ELEMENTO INESISTENTE
    // =========================================================

    @Test
    void updateDiabetologoNonModificaIlDatabaseSeIlMedicoNonEsiste() {

        Diabetologo vecchio = new Diabetologo(
                "inesistente",
                "password",
                "CFINESISTENTE",
                "Mario",
                "Rossi",
                "mail@test.it"
        );

        Diabetologo nuovo = new Diabetologo(
                "nuovo",
                "password",
                "CFNUOVO",
                "Luca",
                "Bianchi",
                "mail2@test.it"
        );

        int numeroDiabetologiPrima =
                db.getDiabetologi().size();

        assertDoesNotThrow(() ->
                db.updateDiabetologo(vecchio, nuovo)
        );

        int numeroDiabetologiDopo =
                db.getDiabetologi().size();

        assertEquals(
                numeroDiabetologiPrima,
                numeroDiabetologiDopo
        );
    }

    @Test
    void updatePazienteNonModificaIlDatabaseSeIlPazienteNonEsiste() {

        Paziente vecchio = new Paziente(
                "inesistentePaziente",
                "password",
                "CFINESP",
                "Mario",
                "Rossi",
                "mail@test.it",
                null,
                new Diabetologo(),
                null,
                null,
                null
        );

        Paziente nuovo = new Paziente(
                "nuovoPaziente",
                "password",
                "CFNUOVOP",
                "Luca",
                "Bianchi",
                "mail2@test.it",
                null,
                new Diabetologo(),
                null,
                null,
                null
        );

        assertDoesNotThrow(() ->
                db.updatePaziente(vecchio, nuovo)
        );

        assertFalse(
                db.getPazienti().contains(nuovo)
        );
    }
}