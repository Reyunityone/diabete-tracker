package application.controller;

import application.classiGeneriche.Rilevazione;

import com.sun.javafx.scene.control.IntegerField;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class RilevazioneController {

    @FXML
    private DatePicker dataPicker;

    @FXML
    private TextField glicemiaField;
    private TextFormatter<Integer> glicemiaFormatter;

    @FXML
    private TextField orarioField;
    @FXML
    private TextField pastoField;


    private Consumer<Rilevazione> salvataggio;


    public void inizializza(
            Consumer<Rilevazione> salvataggio) {
        this.glicemiaFormatter = new TextFormatter<>(new IntegerStringConverter(), 0, change -> {
            if(change.getControlNewText().matches("\\d*")) return change;
            return null;
        });
        this.glicemiaField.setTextFormatter(this.glicemiaFormatter);
        this.salvataggio = salvataggio;
    }


    @FXML
    private void salva() {

        if (dataPicker.getValue() == null) {
            return;
        }


        String data =
                dataPicker
                        .getValue()
                        .format(
                                DateTimeFormatter.ofPattern(
                                        "dd/MM/yyyy"
                                )
                        );


        int glicemia =
                glicemiaFormatter.getValue();

        String orarioRilevazione =
                orarioField.getText();

        String ultimoPasto =
                pastoField.getText();


        Rilevazione rilevazione =
                new Rilevazione(
                        data,
                        glicemia,
                        orarioRilevazione,
                        ultimoPasto
                );


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
            Rilevazione rilevazione,
            Runnable aggiornamento) {

        this.glicemiaFormatter = new TextFormatter<>(new IntegerStringConverter(), 0, change -> {
            if(change.getControlNewText().matches("\\d*")) return change;
            return null;
        });
        this.glicemiaField.setTextFormatter(this.glicemiaFormatter);

        dataPicker.setValue(
                java.time.LocalDate.parse(
                        rilevazione.getData(),
                        DateTimeFormatter.ofPattern(
                                "dd/MM/yyyy"
                        )
                )
        );


        glicemiaField.setText(
                String.valueOf(rilevazione.getLivelloGlicemia())
        );


        pastoField.setText(
                rilevazione.getOrarioPasto()
        );

        orarioField.setText(
                rilevazione.getOrarioRilevazione()
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