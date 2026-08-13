package application.controller;

import application.classiGeneriche.Rilevazione;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class RilevazioneController {

    @FXML
    private DatePicker dataPicker;

    @FXML
    private TextField glicemiaField;

    @FXML
    private TextField momentoField;


    private Consumer<Rilevazione> salvataggio;


    public void inizializza(
            Consumer<Rilevazione> salvataggio) {

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


        String glicemia =
                glicemiaField.getText();


        String momento =
                momentoField.getText();


        Rilevazione rilevazione =
                new Rilevazione(
                        data,
                        glicemia,
                        momento
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

        dataPicker.setValue(
                java.time.LocalDate.parse(
                        rilevazione.getData(),
                        DateTimeFormatter.ofPattern(
                                "dd/MM/yyyy"
                        )
                )
        );


        glicemiaField.setText(
                rilevazione.getLivelloGlicemia()
        );


        momentoField.setText(
                rilevazione.getMomentoGiornata()
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

                    rilevazione.setMomentoGiornata(
                            nuovaRilevazione
                                    .getMomentoGiornata()
                    );


                    aggiornamento.run();
                };
    }
    
    
}