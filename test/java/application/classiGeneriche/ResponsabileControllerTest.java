package application.controller;

import application.classiGeneriche.Database;
import application.classiGeneriche.Diabetologo;
import application.classiGeneriche.Paziente;
import application.classiGeneriche.Responsabile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResponsabileControllerTest {

    private ResponsabileController controller;

    @BeforeEach
    void setUp() {
        controller = new ResponsabileController();
    }

    // =========================================================
    // GET MEDICI
    // =========================================================

    @Test
    void getMediciRestituisceListaMedici() {

        Diabetologo medico = new Diabetologo(
                "medicoTest",
                "password",
                "CFMEDICO",
                "Mario",
                "Rossi",
                "mario@test.it"
        );

        Database.getInstance().addDiabetologo(medico);

        assertTrue(controller.getMedici().contains(medico));
    }

    // =========================================================
    // GET PAZIENTI
    // =========================================================

    @Test
    void getPazientiRestituisceListaPazienti() {

        Paziente paziente = new Paziente(
                "pazienteTest",
                "password",
                "CFPAZIENTE",
                "Luca",
                "Bianchi",
                "luca@test.it",
                null,
                new Diabetologo(),
                null,
                null,
                null
        );

        Database.getInstance().addPaziente(paziente);

        assertTrue(controller.getPazienti().contains(paziente));
    }


    // =========================================================
    // AGGIUNTA MEDICO
    // =========================================================

    @Test
    void aggiuntaMedicoRendeIlMedicoDisponibileNelController() {

        Diabetologo medico = new Diabetologo(
                "medicoNuovo",
                "password",
                "CFNUOVOMED",
                "Anna",
                "Neri",
                "anna@test.it"
        );

        Database.getInstance().addDiabetologo(medico);

        assertTrue(
                controller.getMedici().stream()
                        .anyMatch(d -> d.getUsername().equals("medicoNuovo"))
        );
    }

    // =========================================================
    // AGGIUNTA PAZIENTE
    // =========================================================

    @Test
    void aggiuntaPazienteRendeIlPazienteDisponibileNelController() {

        Paziente paziente = new Paziente(
                "pazienteNuovo",
                "password",
                "CFNUOVOPAZ",
                "Paolo",
                "Neri",
                "paolo@test.it",
                null,
                new Diabetologo(),
                null,
                null,
                null
        );

        Database.getInstance().addPaziente(paziente);

        assertTrue(
                controller.getPazienti().stream()
                        .anyMatch(p -> p.getUsername().equals("pazienteNuovo"))
        );
    }

    // =========================================================
    // ASSOCIAZIONE PAZIENTE - DIABETOLOGO
    // =========================================================

    @Test
    void pazienteVieneAssociatoAlDiabetologo() {

        Diabetologo medico = new Diabetologo(
                "medicoAssociazione",
                "password",
                "CFMEDASS",
                "Mario",
                "Rossi",
                "medico@test.it"
        );

        Paziente paziente = new Paziente(
                "pazienteAssociazione",
                "password",
                "CFPAZASS",
                "Luca",
                "Bianchi",
                "paziente@test.it",
                null,
                new Diabetologo(),
                null,
                null,
                null
        );

        Database.getInstance().addDiabetologo(medico);
        Database.getInstance().addPaziente(paziente);

        Database.getInstance()
                .updatePazienteDiabetologo(paziente, medico);

        Paziente risultato = Database.getInstance()
                .getPazienti()
                .stream()
                .filter(p -> p.getUsername().equals("pazienteAssociazione"))
                .findFirst()
                .orElseThrow();

        assertNotNull(risultato.getMedicoDiRiferimento());

        assertEquals(
                medico.getUsername(),
                risultato.getMedicoDiRiferimento().getUsername()
        );
    }

    // =========================================================
    // PAZIENTI SEGUITI DA UN MEDICO
    // =========================================================

    @Test
    void getPazientiByDiabetologoRestituiscePazientiCorretti() {

        Diabetologo medico = new Diabetologo(
                "medicoSeguiti",
                "password",
                "CFMEDSEG",
                "Mario",
                "Rossi",
                "medico@test.it"
        );

        Paziente paziente = new Paziente(
                "pazienteSeguito",
                "password",
                "CFPAZSEG",
                "Luca",
                "Bianchi",
                "paziente@test.it",
                null,
                medico,
                null,
                null,
                null
        );

        Database.getInstance().addDiabetologo(medico);
        Database.getInstance().addPaziente(paziente);

        var pazientiSeguiti =
                Database.getInstance().getPazientiByDiabetologo(medico);

        assertEquals(1, pazientiSeguiti.size());
        assertTrue(pazientiSeguiti.contains(paziente));
    }

    // =========================================================
    // ELIMINAZIONE PAZIENTE
    // =========================================================

    @Test
    void eliminazionePazienteRimuoveIlPazienteDalDatabase() {

        Paziente paziente = new Paziente(
                "pazienteDelete",
                "password",
                "CFDELETE",
                "Paolo",
                "Verdi",
                "paolo@test.it",
                null,
                new Diabetologo(),
                null,
                null,
                null
        );

        Database.getInstance().addPaziente(paziente);

        assertTrue(
                Database.getInstance()
                        .getPazienti()
                        .contains(paziente)
        );

        Database.getInstance().deletePaziente(paziente);

        assertFalse(
                Database.getInstance()
                        .getPazienti()
                        .contains(paziente)
        );
    }

    // =========================================================
    // ELIMINAZIONE DIABETOLOGO
    // =========================================================

    @Test
    void eliminazioneDiabetologoRimuoveIlMedicoDalDatabase() {

        Diabetologo medico = new Diabetologo(
                "medicoDelete",
                "password",
                "CFMEDDELETE",
                "Anna",
                "Neri",
                "anna@test.it"
        );

        Database.getInstance().addDiabetologo(medico);

        assertTrue(
                Database.getInstance()
                        .getDiabetologi()
                        .contains(medico)
        );

        Database.getInstance().deleteDiabetologo(medico);

        assertFalse(
                Database.getInstance()
                        .getDiabetologi()
                        .contains(medico)
        );
    }

    // =========================================================
    // CONTROLLO ELIMINAZIONE MEDICO CON PAZIENTI
    // =========================================================

    @Test
    void diabetologoConPazientiNonDovrebbeEssereEliminabile() {

        Diabetologo medico = new Diabetologo(
                "medicoConPaziente",
                "password",
                "CFMEDPAZ",
                "Mario",
                "Rossi",
                "medico@test.it"
        );

        Paziente paziente = new Paziente(
                "pazienteMedico",
                "password",
                "CFPAZMED",
                "Luca",
                "Bianchi",
                "paziente@test.it",
                null,
                medico,
                null,
                null,
                null
        );

        Database.getInstance().addDiabetologo(medico);
        Database.getInstance().addPaziente(paziente);

        assertFalse(
                Database.getInstance()
                        .getPazientiByDiabetologo(medico)
                        .isEmpty()
        );
    }

}