package application.controller;

import application.classiGeneriche.Segnalazione;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class SegnalazioneController {

    // =========================================================
    // FXML
    // =========================================================

    @FXML
    private DatePicker dataPicker;

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

    public void inizializza(
            Consumer<Segnalazione> salvataggio) {

        this.salvataggio = salvataggio;

        this.modalitaModifica = false;
    }


    // =========================================================
    // INIZIALIZZAZIONE - MODIFICA
    // =========================================================

    public void inizializzaModifica(
            Segnalazione segnalazione,
            Runnable aggiornamento) {

        this.modalitaModifica = true;

        this.segnalazioneDaModificare =
                segnalazione;

        this.aggiornamento =
                aggiornamento;


        // -----------------------------------------------------
        // CARICA DATA
        // -----------------------------------------------------

        if (segnalazione.getData() != null) {

            dataPicker.setValue(segnalazione.getData());
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

        if (dataPicker.getValue() == null) {

            return;
        }


        // -----------------------------------------------------
        // DATA
        // -----------------------------------------------------

        LocalDate data =
                dataPicker
                        .getValue();


        // -----------------------------------------------------
        // TESTO
        // -----------------------------------------------------

        String testo =
                testoArea.getText();


        // =====================================================
        // MODIFICA
        // =====================================================

        if (modalitaModifica) {

            segnalazioneDaModificare.setData(
                    data
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

        Segnalazione segnalazione =
                new Segnalazione(
                        data,
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
                (Stage) dataPicker
                        .getScene()
                        .getWindow();


        stage.close();
    }
}