package application.controller;

import application.classiGeneriche.Paziente;
import application.classiGeneriche.Segnalazione;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class SegnalazioneController {
    private Paziente user;

    // =========================================================
    // FXML
    // =========================================================

    @FXML
    private DatePicker dataInizioPicker;

    //TODO Implementare data fine
    @FXML
    private DatePicker dataFinePicker;

    @FXML
    private TextArea testoArea;


    // =========================================================
    // DATI
    // =========================================================

    private Consumer<Segnalazione> salvataggio;


    // =========================================================
    // FORMATO DATA
    // =========================================================

    private final DateTimeFormatter formatoData =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");


    // =========================================================
    // MODALITÀ MODIFICA
    // =========================================================

    private boolean modalitaModifica = false;

    private Segnalazione segnalazioneDaModificare;

    private Runnable aggiornamento;


    // =========================================================
    // INIZIALIZZAZIONE - NUOVA SEGNALAZIONE
    // =========================================================

    public void inizializza(Paziente user,
                            Consumer<Segnalazione> salvataggio) {
        this.user = user;

        this.salvataggio = salvataggio;

        this.modalitaModifica = false;
    }


    // =========================================================
    // INIZIALIZZAZIONE - MODIFICA
    // =========================================================

    public void inizializzaModifica(
            Paziente user,
            Segnalazione segnalazione,
            Runnable aggiornamento) {
        this.user = user;
        this.modalitaModifica = true;

        this.segnalazioneDaModificare =
                segnalazione;

        this.aggiornamento =
                aggiornamento;


        // -----------------------------------------------------
        // CARICA DATA
        // -----------------------------------------------------

        if (segnalazione.getDataInizio() != null) {

            dataInizioPicker.setValue(segnalazione.getDataInizio());
        }


        // -----------------------------------------------------
        // CARICA TESTO
        // -----------------------------------------------------

        testoArea.setText(
                segnalazione.getTesto()
        );
    }


    // =========================================================
    // SALVA
    // =========================================================

    @FXML
    private void salva() {

        // -----------------------------------------------------
        // CONTROLLO DATA
        // -----------------------------------------------------

        if (dataInizioPicker.getValue() == null) {

            return;
        }


        // -----------------------------------------------------
        // DATA
        // -----------------------------------------------------

        LocalDate dataInizio =
                dataInizioPicker
                        .getValue();

        LocalDate dataFine =
                dataFinePicker.getValue();
        // -----------------------------------------------------
        // TESTO
        // -----------------------------------------------------

        String testo =
                testoArea.getText();


        // =====================================================
        // MODIFICA
        // =====================================================

        if (modalitaModifica) {

            segnalazioneDaModificare.setDataInizio(
                    dataInizio
            );

            segnalazioneDaModificare.setTesto(
                    testo
            );


            // Aggiorna lo storico

            if (aggiornamento != null) {

                aggiornamento.run();
            }


            chiudiFinestra();

            return;
        }


        // =====================================================
        // NUOVA SEGNALAZIONE
        // =====================================================


            Segnalazione segnalazione = new Segnalazione(
                    dataInizio,
                    dataFine,
                    user,
                    testo
            );


        if (salvataggio != null) {

            salvataggio.accept(
                    segnalazione
            );
        }


        chiudiFinestra();
    }


    // =========================================================
    // CHIUDI FINESTRA
    // =========================================================

    private void chiudiFinestra() {

        Stage stage =
                (Stage) dataInizioPicker
                        .getScene()
                        .getWindow();


        stage.close();
    }
}