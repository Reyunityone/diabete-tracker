package application.controller;

import application.classiGeneriche.AssunzioneFarmaco;

import application.classiGeneriche.Terapia;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class FarmacoController {

    // =========================================================
    // FXML
    // =========================================================

    @FXML
    private DatePicker dataPicker;

    @FXML
    private TextField orarioField;
    private TextFormatter<String> orarioFormatter;

    @FXML
    private TextField quantitaField;
    private TextFormatter<Integer> quantitaFormatter;

    @FXML
    private ComboBox<Terapia> terapiaBox;

    // =========================================================
    // DATI
    // =========================================================

    private Consumer<AssunzioneFarmaco> salvataggio;


    // =========================================================
    // FORMATO DATA
    // =========================================================

    private final DateTimeFormatter formatoData =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");


    // =========================================================
    // MODALITÀ MODIFICA
    // =========================================================

    private boolean modalitaModifica = false;

    private AssunzioneFarmaco elementoDaModificare;

    private Runnable aggiornamento;


    // =========================================================
    // INIZIALIZZAZIONE - NUOVO ELEMENTO
    // =========================================================

    public void inizializza(
            Consumer<AssunzioneFarmaco> salvataggio) {

        this.orarioFormatter = new TextFormatter<String>( change -> {
           String text = change.getControlNewText();
           if(text.length() > 5) return null;
           if(!text.matches("\\d{0,2}:?\\d{0,2}")) return null;
           return change;
        });
        this.orarioField.setTextFormatter(orarioFormatter);

        this.quantitaFormatter = new TextFormatter<Integer>(new IntegerStringConverter(), 0, change -> {
            if(!change.getControlNewText().matches("\\d*")) return null;
            return change;
        });

        this.quantitaField.setTextFormatter(quantitaFormatter);

        this.salvataggio = salvataggio;

        this.modalitaModifica = false;
    }


    // =========================================================
    // INIZIALIZZAZIONE - MODIFICA
    // =========================================================

    public void inizializzaModifica(
            AssunzioneFarmaco elemento,
            Runnable aggiornamento) {
        this.orarioFormatter = new TextFormatter<String>( change -> {
            String text = change.getControlText();
            if(text.length() > 5) return null;
            if(!text.matches("\\d{0,2}:?\\d{0,2}")) return null;
            return change;
        });
        this.orarioField.setTextFormatter(orarioFormatter);

        this.quantitaFormatter = new TextFormatter<Integer>(new IntegerStringConverter(), 0, change -> {
            if(!change.getControlNewText().matches("\\d*")) return null;
            return change;
        });

        this.quantitaField.setTextFormatter(quantitaFormatter);


        this.modalitaModifica = true;

        this.elementoDaModificare =
                elemento;

        this.aggiornamento =
                aggiornamento;


        // -----------------------------------------------------
        // CARICA DATA
        // -----------------------------------------------------

        if (elemento.getData() != null) {

            LocalDate data = elemento.getData();

            dataPicker.setValue(data);
        }


        // -----------------------------------------------------
        // CARICA QUANTITA
        // -----------------------------------------------------

        quantitaField.setText(
                "" + elemento.getQuantita()
        );

        orarioField.setText(elemento.getOrarioAssunzione().toString());

        terapiaBox.setValue(elemento.getTerapia());
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

        //---------
        // ORARIO
        //---------
        LocalTime orario=
                LocalTime.parse(orarioField.getText(), DateTimeFormatter.ofPattern("HH:mm"));
        // -----------------------------------------------------
        // QUANTITA
        // -----------------------------------------------------

        int quantita =
                quantitaFormatter.getValue();

        //-----------------------
        //TERAPIA
        //------------------------

        Terapia terapia = terapiaBox.getValue();

        // =====================================================
        // MODIFICA
        // =====================================================

        if (modalitaModifica) {

            elementoDaModificare.setData(
                    data
            );

            elementoDaModificare.setQuantita(
                    quantita
            );
            elementoDaModificare.setTerapia(
                    terapia
            );
            elementoDaModificare.setOrarioAssunzione(
                    orario
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

        AssunzioneFarmaco elemento =
                new AssunzioneFarmaco(
                        data,
                        orario,
                        quantita,
                        terapia
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