package application.controller;

import application.classiGeneriche.*;

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

public class RilevazioneController {


    @FXML
    private DatePicker dataPicker;

    @FXML
    private TextField glicemiaField;
    private TextFormatter<Integer> glicemiaFormatter;

    @FXML
    private TextField orarioField;

    @FXML
    private ComboBox<MomentoRilevazione> momentoComboBox;

    @FXML
    private TextField pastoField;

    private Consumer<Rilevazione> salvataggio;

    private final DateTimeFormatter timeFormatter =
            DateTimeFormatter.ofPattern("HH:mm");


    public void inizializza(
            Consumer<Rilevazione> salvataggio) {

        inizializzaFormattatori();

        momentoComboBox.getItems().setAll(
                MomentoRilevazione.values()
        );

        this.salvataggio = salvataggio;
    }


    private void inizializzaFormattatori() {

        this.glicemiaFormatter =
                new TextFormatter<>(
                        new IntegerStringConverter(),
                        0,
                        change -> {

                            if (change.getControlNewText()
                                    .matches("\\d*")) {
                                return change;
                            }

                            return null;
                        }
                );


        TextFormatter<String> orarioFormatter = new TextFormatter<>(change -> {

            if (change.getControlNewText().length() > 5) {
                return null;
            }

            if (change.getControlNewText()
                    .matches("\\d{0,2}:?\\d{0,2}")) {
                return change;
            }

            return null;
        });


        TextFormatter<String> pastoFormatter = new TextFormatter<>(change -> {

            if (change.getControlNewText().length() > 5) {
                return null;
            }

            if (change.getControlNewText()
                    .matches("\\d{0,2}:?\\d{0,2}")) {
                return change;
            }

            return null;
        });


        glicemiaField.setTextFormatter(
                glicemiaFormatter
        );

        orarioField.setTextFormatter(
                orarioFormatter
        );

        pastoField.setTextFormatter(
                pastoFormatter
        );
    }


    @FXML
    private void salva() {

        if (dataPicker.getValue() == null) {
            return;
        }

        if (momentoComboBox.getValue() == null) {
            return;
        }

        LocalDate data =
                dataPicker.getValue();

        int glicemia =
                glicemiaFormatter.getValue();


        LocalTime orarioRilevazione;
        LocalTime ultimoPasto;

        try {

            orarioRilevazione =
                    LocalTime.parse(
                            orarioField.getText(),
                            timeFormatter
                    );

            ultimoPasto =
                    LocalTime.parse(
                            pastoField.getText(),
                            timeFormatter
                    );

        } catch (Exception e) {

            System.err.println(
                    "Orario non valido"
            );

            return;
        }


        MomentoRilevazione momento =
                momentoComboBox.getValue();


        Rilevazione rilevazione =
                new Rilevazione(
                        data,
                        glicemia,
                        ultimoPasto,
                        orarioRilevazione,
                        momento,
                        (Paziente) Session.getInstance().getCurrentUser()
                );


        GestoreAlert.verificaGlicemia(rilevazione);

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
            Rilevazione rilevazione, Runnable aggiornamento) {

        inizializzaFormattatori();

        momentoComboBox.getItems().setAll(
                MomentoRilevazione.values()
        );


        dataPicker.setValue(
                rilevazione.getData()
        );


        glicemiaField.setText(
                String.valueOf(
                        rilevazione.getLivelloGlicemia()
                )
        );


        pastoField.setText(
                rilevazione
                        .getOrarioPasto()
                        .toString()
        );


        orarioField.setText(
                rilevazione
                        .getOrarioRilevazione()
                        .toString()
        );


        momentoComboBox.setValue(
                rilevazione.getMomentoRilevazione()
        );


        this.salvataggio =
                nuovaRilevazione -> {
                    Database.getInstance().updateRilevazione(rilevazione, nuovaRilevazione);

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


                    rilevazione.setMomentoRilevazione(
                            nuovaRilevazione
                                    .getMomentoRilevazione()
                    );


                    aggiornamento.run();
                };
    }
    
    
}