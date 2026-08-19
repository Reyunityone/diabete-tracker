package application.controller;

import application.classiGeneriche.Paziente;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.util.Random;

public class AndamentoController {

    @FXML
    private Label titoloLabel;

    @FXML
    private Label periodoLabel;

    @FXML
    private LineChart<String, Number> grafico;

    @FXML
    private CategoryAxis asseX;

    @FXML
    private NumberAxis asseY;

    @FXML
    private Button settimanaButton;

    @FXML
    private Button meseButton;

    @FXML
    private Button indietroButton;

    @FXML
    private Button avantiButton;


    private Paziente paziente;

    private boolean modalitaSettimana = true;

    private int periodo = 1;


    @FXML
    public void initialize() {

        settimanaButton.setOnAction(
                event -> {

                    modalitaSettimana = true;

                    periodo = 1;

                    aggiornaGrafico();
                }
        );


        meseButton.setOnAction(
                event -> {

                    modalitaSettimana = false;

                    periodo = 1;

                    aggiornaGrafico();
                }
        );


        indietroButton.setOnAction(
                event -> {

                    if (periodo > 1) {

                        periodo--;

                        aggiornaGrafico();
                    }
                }
        );


        avantiButton.setOnAction(
                event -> {

                    periodo++;

                    aggiornaGrafico();
                }
        );


        aggiornaGrafico();
    }


    public void inizializzaPaziente(
            Paziente paziente) {

        this.paziente = paziente;

        titoloLabel.setText(
                "Andamento glicemico - "
                        + paziente.getNome()
                        + " "
                        + paziente.getCognome()
        );

        aggiornaGrafico();
    }


    private void aggiornaGrafico() {

        grafico.getData().clear();

        XYChart.Series<String, Number> serie =
                new XYChart.Series<>();


        if (modalitaSettimana) {

            periodoLabel.setText(
                    "Settimana " + periodo
            );

            String[] giorni = {
                    "Lun",
                    "Mar",
                    "Mer",
                    "Gio",
                    "Ven",
                    "Sab",
                    "Dom"
            };


            Random random = new Random(
                    periodo
            );


            for (String giorno : giorni) {

                int valore =
                        70 + random.nextInt(100);

                serie.getData().add(
                        new XYChart.Data<>(
                                giorno,
                                valore
                        )
                );
            }

        } else {

            periodoLabel.setText(
                    "Mese " + periodo
            );

            Random random = new Random(
                    periodo
            );


            for (int giorno = 1;
                 giorno <= 30;
                 giorno++) {

                int valore =
                        70 + random.nextInt(100);

                serie.getData().add(
                        new XYChart.Data<>(
                                String.valueOf(giorno),
                                valore
                        )
                );
            }
        }


        serie.setName(
                "Glucosio"
        );

        grafico.getData().add(serie);
    }
}