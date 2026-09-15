package application.controller;

import application.classiGeneriche.*;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class FarmacoController implements Initializable {

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
    // MODALITÀ MODIFICA
    // =========================================================

    private boolean modalitaModifica = false;

    private AssunzioneFarmaco elementoDaModificare;

    private Runnable aggiornamento;


    // =========================================================
    // INIZIALIZZAZIONE - NUOVO ELEMENTO
    // =========================================================


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Paziente currentUser = (Paziente) Session.getInstance().getCurrentUser();
        terapiaBox.getItems().addAll(Database.getInstance().getTerapieByPaziente(currentUser));

    }

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

        if (dataPicker.getValue() == null || orarioField.getText().isEmpty() || quantitaField.getText().isEmpty() || terapiaBox.getSelectionModel().isEmpty()) return;


        // -----------------------------------------------------
        // DATA
        // -----------------------------------------------------

        LocalDate data =
                dataPicker
                        .getValue();

        //---------
        // ORARIO
        //---------
        LocalTime orario;

        try {
            orario = LocalTime.parse(orarioField.getText(), DateTimeFormatter.ofPattern("HH:mm"));
        } catch (Exception e) {
            System.err.println("ORARIO INVALIDO");
            return;
        }
        // -----------------------------------------------------
        // QUANTITA
        // -----------------------------------------------------

        int quantita =
                quantitaFormatter.getValue();
        //-----------------------
        //TERAPIA
        //------------------------

        Terapia terapia = terapiaBox.getValue();
        if(quantita > terapia.getDose()){
            System.err.println("SOVRADOSAGGIO RILEVATO");
            return;
        }
        if(quantita < terapia.getDose()){
            System.err.println("SOTTODOSAGGIO RILEVATO");
            return;
        }
        // =====================================================
        // MODIFICA
        // =====================================================

        if (modalitaModifica) {
            Database.getInstance().updateAssunzione(elementoDaModificare, new AssunzioneFarmaco((Paziente) Session.getInstance().getCurrentUser(), data, orario, quantita, terapia));
            elementoDaModificare.setOrarioAssunzione(orario);
            elementoDaModificare.setData(data);
            elementoDaModificare.setQuantita(quantita);
            elementoDaModificare.setTerapia(terapia);
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
                        (Paziente) Session.getInstance().getCurrentUser(),
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