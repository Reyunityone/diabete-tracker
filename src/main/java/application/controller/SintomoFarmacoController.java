package application.controller;

import application.classiGeneriche.SintomoFarmaco;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class SintomoFarmacoController {

    // =========================================================
    // FXML
    // =========================================================

    @FXML
    private DatePicker dataPicker;

    @FXML
    private TextArea indicazioneArea;


    // =========================================================
    // DATI
    // =========================================================

    private Consumer<SintomoFarmaco> salvataggio;


    // =========================================================
    // FORMATO DATA
    // =========================================================

    private final DateTimeFormatter formatoData =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");


    // =========================================================
    // MODALITÀ MODIFICA
    // =========================================================

    private boolean modalitaModifica = false;

    private SintomoFarmaco elementoDaModificare;

    private Runnable aggiornamento;


    // =========================================================
    // INIZIALIZZAZIONE - NUOVO ELEMENTO
    // =========================================================

    public void inizializza(
            Consumer<SintomoFarmaco> salvataggio) {

        this.salvataggio = salvataggio;

        this.modalitaModifica = false;
    }


    // =========================================================
    // INIZIALIZZAZIONE - MODIFICA
    // =========================================================

    public void inizializzaModifica(
            SintomoFarmaco elemento,
            Runnable aggiornamento) {

        this.modalitaModifica = true;

        this.elementoDaModificare =
                elemento;

        this.aggiornamento =
                aggiornamento;


        // -----------------------------------------------------
        // CARICA DATA
        // -----------------------------------------------------

        if (elemento.getData() != null
                && !elemento.getData().isEmpty()) {

            LocalDate data =
                    LocalDate.parse(
                            elemento.getData(),
                            formatoData
                    );

            dataPicker.setValue(data);
        }


        // -----------------------------------------------------
        // CARICA INDICAZIONE
        // -----------------------------------------------------

        indicazioneArea.setText(
                elemento.getIndicazione()
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

        String data =
                dataPicker
                        .getValue()
                        .format(
                                formatoData
                        );


        // -----------------------------------------------------
        // INDICAZIONE
        // -----------------------------------------------------

        String indicazione =
                indicazioneArea.getText();


        // =====================================================
        // MODIFICA
        // =====================================================

        if (modalitaModifica) {

            elementoDaModificare.setData(
                    data
            );

            elementoDaModificare.setIndicazione(
                    indicazione
            );


            // Aggiorna lo storico

            if (aggiornamento != null) {

                aggiornamento.run();
            }


            chiudiFinestra();

            return;
        }


        // =====================================================
        // NUOVO ELEMENTO
        // =====================================================

        SintomoFarmaco elemento =
                new SintomoFarmaco(
                        data,
                        indicazione
                );


        if (salvataggio != null) {

            salvataggio.accept(
                    elemento
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