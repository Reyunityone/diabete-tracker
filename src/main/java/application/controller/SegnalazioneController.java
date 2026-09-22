package application.controller;

import application.classiGeneriche.*;

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

    @FXML private DatePicker dataInizioPicker;
    @FXML private DatePicker dataFinePicker;
    @FXML private TextArea testoArea;


    // =========================================================
    // DATI
    // =========================================================

    private Consumer<Segnalazione> salvataggio;

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

        if (segnalazione.getDataInizio() != null) {

            dataInizioPicker.setValue(segnalazione.getDataInizio());
        }

        if(segnalazione.getDataFine() != null){
            dataFinePicker.setValue(segnalazione.getDataFine());
        }
        // -----------------------------------------------------
        // CARICA TESTO
        // -----------------------------------------------------

        if(segnalazione.getTesto()!=null|| testoArea.getText().trim().isEmpty()) {
        	testoArea.setText(segnalazione.getTesto());
        }
    }


    // =========================================================
    // SALVA
    // =========================================================

    @FXML
    private void salva() {

        // -----------------------------------------------------
        // CONTROLLO DATA
        // -----------------------------------------------------

    	if (dataInizioPicker.getValue() == null
    	        || testoArea.getText() == null
    	        || testoArea.getText().trim().isEmpty()) {

    	    return;
    	}


        // -----------------------------------------------------
        // DATA
        // -----------------------------------------------------

        LocalDate dataInizio =
                dataInizioPicker.getValue();

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
            Segnalazione nuova = new Segnalazione(dataInizio,dataFine , (Paziente) Session.getInstance().getCurrentUser(),testo);
            Paziente p = (Paziente) Session.getInstance().getCurrentUser();
            Messaggio m = new Messaggio(null, p.getMedicoDiRiferimento(), "[Rettifica segnalazione] " + segnalazioneDaModificare.toString() + "--->" + nuova.toString(), TipoAlert.SISTEMA_MEDICO, UrgenzaAlert.MEDIUM);
            Database.getInstance().updateSegnalazione(segnalazioneDaModificare, nuova);
            Database.getInstance().addMessaggio(m);
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
                    (Paziente) Session.getInstance().getCurrentUser(),
                    testo
            );


        if (salvataggio != null) {
            Paziente p = (Paziente) Session.getInstance().getCurrentUser();
            Messaggio m = new Messaggio(null, p.getMedicoDiRiferimento(), "[Segnalazione/Sintomo]:  " + segnalazione.toString(), TipoAlert.SISTEMA_MEDICO, UrgenzaAlert.MEDIUM);
            Database.getInstance().addMessaggio(m);
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