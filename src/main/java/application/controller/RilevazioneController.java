package application.controller;

import application.classiGeneriche.Paziente;
import application.classiGeneriche.Rilevazione;

import application.classiGeneriche.User;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class RilevazioneController {

    private User user;

    @FXML
    private DatePicker dataPicker;

    @FXML
    private TextField glicemiaField;
    private TextFormatter<Integer> glicemiaFormatter;

    @FXML
    private TextField orarioField;
    private TextFormatter<String> orarioFormatter;
    @FXML
    private TextField pastoField;
    private TextFormatter<String> pastoFormatter;

    private Consumer<Rilevazione> salvataggio;


    public void inizializza(
            User user,
            Consumer<Rilevazione> salvataggio) {
        this.glicemiaFormatter = new TextFormatter<>(new IntegerStringConverter(), 0, change -> {
            if(change.getControlNewText().matches("\\d*")) return change;
            return null;
        });
        this.orarioFormatter = new TextFormatter<String>(change ->  {
            if(change.getControlNewText().length() > 5) return null;
            if(change.getControlNewText().matches("\\d{0,2}:?\\d{0,2}")) return change;
            return null;
        });
        this.pastoFormatter = new TextFormatter<String>(change ->  {
            if(change.getControlNewText().length() > 5) return null;
            if(change.getControlNewText().matches("\\d{0,2}:?\\d{0,2}")) return change;
            return null;
        });
        this.glicemiaField.setTextFormatter(this.glicemiaFormatter);
        this.orarioField.setTextFormatter(orarioFormatter);
        this.pastoField.setTextFormatter(pastoFormatter);
        this.salvataggio = salvataggio;
    }


    @FXML
    private void salva() {

        if (dataPicker.getValue() == null) {
            return;
        }


        LocalDate data = dataPicker.getValue();
        int glicemia =
                glicemiaFormatter.getValue();

        LocalTime orarioRilevazione =
                null;
        LocalTime ultimoPasto =
                null;
        try {
            orarioRilevazione = LocalTime.parse(orarioField.getText(), DateTimeFormatter.ofPattern("HH:mm"));

            ultimoPasto = LocalTime.parse(pastoField.getText(), DateTimeFormatter.ofPattern("HH:mm"));
        } catch (Exception e) {
            System.err.println("Orario non valido");
            return;
        }


        Rilevazione rilevazione =
                null;
        if (user instanceof Paziente) {
            rilevazione = new Rilevazione(
                    data,
                    glicemia,
                    orarioRilevazione,
                    ultimoPasto,
                    (Paziente) user
            );
        }


        salvataggio.accept(
                rilevazione
        );


        Stage stage =
                (Stage) dataPicker
                        .getScene()
                        .getWindow();

        stage.close();
    }
    
    public void inizializzaModifica(
            User user,
            Rilevazione rilevazione,
            Runnable aggiornamento) {

        this.glicemiaFormatter = new TextFormatter<>(new IntegerStringConverter(), 0, change -> {
            if(change.getControlNewText().matches("\\d*")) return change;
            return null;
        });
        this.orarioFormatter = new TextFormatter<String>(change ->  {
            if(change.getControlNewText().length() > 5) return null;
            if(change.getControlNewText().matches("\\d{0,2}:?\\d{0,2}")) return change;
            return null;
        });
        this.pastoFormatter = new TextFormatter<String>(change ->  {
            if(change.getControlNewText().length() > 5) return null;
            if(change.getControlNewText().matches("\\d{0,2}:?\\d{0,2}")) return change;
            return null;
        });
        this.glicemiaField.setTextFormatter(this.glicemiaFormatter);
        this.orarioField.setTextFormatter(orarioFormatter);
        this.pastoField.setTextFormatter(pastoFormatter);

        dataPicker.setValue(
                rilevazione.getData()
        );


        glicemiaField.setText(
                String.valueOf(rilevazione.getLivelloGlicemia())
        );


        pastoField.setText(
                rilevazione.getOrarioPasto().toString()
        );

        orarioField.setText(
                rilevazione.getOrarioRilevazione().toString()
        );

        this.salvataggio =
                nuovaRilevazione -> {

                    rilevazione.setData(
                            nuovaRilevazione.getData()
                    );

                    rilevazione.setLivelloGlicemia(
                            nuovaRilevazione
                                    .getLivelloGlicemia()
                    );

                    rilevazione.setOrarioRilevazione(
                            nuovaRilevazione
                                    .getOrarioRilevazione()
                    );

                    rilevazione.setOrarioPasto(
                            nuovaRilevazione
                                    .getOrarioPasto()
                    );
                    aggiornamento.run();
                };
    }
    
    
}