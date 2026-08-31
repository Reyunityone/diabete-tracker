package application.controller;

import application.classiGeneriche.Database;
import application.classiGeneriche.Diabetologo;
import application.classiGeneriche.Paziente;
import application.classiGeneriche.Terapia;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;

public class TerapieEsistentiController {

    @FXML
    private Label titoloLabel;

    @FXML
    private ScrollPane terapieScrollPane;

    @FXML
    private VBox terapieContainer;


    private Paziente paziente;
    private Diabetologo medico;
    private Runnable aggiornamento;


    public void inizializza(
            Paziente paziente,
            Diabetologo medico,
            Runnable aggiornamento) {

        this.paziente = paziente;
        this.medico = medico;
        this.aggiornamento = aggiornamento;

        titoloLabel.setText(
                "Terapie esistenti"
        );

        aggiornaLista();
    }


    private void aggiornaLista() {

        terapieContainer.getChildren().clear();

        for (Terapia terapia :
                Database.getInstance().getTerapie()) {

            if (terapia.getMedicoAssegnante().equals(medico)) {

                terapieContainer.getChildren().add(
                        creaBoxTerapia(terapia)
                );
            }
        }
    }


    private HBox creaBoxTerapia(
            Terapia terapia) {

        HBox box = new HBox(15);

        box.setAlignment(
                javafx.geometry.Pos.CENTER_LEFT
        );

        box.setMaxWidth(
                Double.MAX_VALUE
        );

        box.getStyleClass().add(
                "history-item"
        );


        VBox informazioni =
                new VBox(4);


        Label farmaco =
                new Label(
                        terapia.getFarmaco()
                );

        farmaco.getStyleClass().add(
                "history-date"
        );


        Label dettagli =
                new Label(
                        "Dose: "
                                + terapia.getDose()
                                + " mg | "
                                + terapia.getNumeroAssunzioniGiornaliere()
                                + " assunzioni/giorno"
                );


        Label indicazioni =
                new Label(
                        terapia.getIndicazioni() == null
                                ? ""
                                : terapia.getIndicazioni()
                );

        indicazioni.setWrapText(true);


        informazioni.getChildren().addAll(
                farmaco,
                dettagli,
                indicazioni
        );


        Region spazio =
                new Region();

        HBox.setHgrow(
                spazio,
                Priority.ALWAYS
        );


        Button assegnaButton = new Button("Assegna");
        assegnaButton.getStyleClass().add("open-button");
        assegnaButton.setOnAction(
                event -> assegnaTerapia(terapia)
        );

        Button rimuoviButton = new Button("Rimuovi");
        rimuoviButton.getStyleClass().add("open-button");
        rimuoviButton.setOnAction(
                event -> rimuoviTerapia(terapia)
        );


        box.getChildren().addAll(
                informazioni,
                spazio,
                assegnaButton,
                rimuoviButton
        );

        return box;
    }


    private void assegnaTerapia(
            Terapia terapiaEsistente) {

        if (!terapiaEsistente
                .getPazienti()
                .contains(paziente)) {

            terapiaEsistente
                    .getPazienti()
                    .add(paziente);

            Database.getInstance().save();
        }

        if (aggiornamento != null) {
            aggiornamento.run();
        }

        chiudiFinestra();
    }

    private void rimuoviTerapia(Terapia terapia) {

        Database.getInstance().removeTerapia(terapia);

        aggiornaLista();
    }

    private void chiudiFinestra() {

        Stage stage =
                (Stage) titoloLabel
                        .getScene()
                        .getWindow();

        stage.close();
    }
}