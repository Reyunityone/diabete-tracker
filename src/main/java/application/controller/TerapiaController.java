package application.controller;

import application.classiGeneriche.Diabetologo;
import application.classiGeneriche.Paziente;
import application.classiGeneriche.Terapia;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.util.function.Consumer;

public class TerapiaController {

    @FXML
    private Label titoloLabel;

    @FXML
    private TextField farmacoField;

    @FXML
    private TextField assunzioniField;

    @FXML
    private TextField quantitaField;

    @FXML
    private TextArea indicazioniArea;

    @FXML
    private Button salvaButton;

    private Paziente paziente;
    private Diabetologo medico;
    private Terapia terapiaDaModificare;

    private Consumer<Terapia> salvataggio;
    private Runnable aggiornamento;
    private boolean modalitaModifica = false;

    @FXML
    public void initialize() {
        TextFormatter<Integer> interoFormatter =
                new TextFormatter<>(new IntegerStringConverter(), 0, change -> {
                    if (change.getControlNewText().matches("\\d*")) {
                        return change;
                    }
                    return null;
                });

        assunzioniField.setTextFormatter(interoFormatter);

        TextFormatter<Integer> doseFormatter =
                new TextFormatter<>(new IntegerStringConverter(), 0, change -> {
                    if (change.getControlNewText().matches("\\d*")) {
                        return change;
                    }
                    return null;
                });

        quantitaField.setTextFormatter(doseFormatter);
        salvaButton.setOnAction(event -> salvaTerapia());
    }

    public void inizializzaNuova(
            Paziente paziente,
            Diabetologo medico,
            Consumer<Terapia> salvataggio) {

        this.paziente = paziente;
        this.medico = medico;
        this.salvataggio = salvataggio;
        this.aggiornamento = null;
        this.terapiaDaModificare = null;
        this.modalitaModifica = false;

        titoloLabel.setText(
                "Nuova terapia - "
                        + paziente.getNome() + " "
                        + paziente.getCognome()
        );

        farmacoField.clear();
        assunzioniField.clear();
        quantitaField.clear();
        indicazioniArea.clear();
    }

    public void inizializzaModifica(
            Terapia terapia,
            Runnable aggiornamento) {

        this.paziente = terapia.getPaziente();
        this.medico = terapia.getMedicoAssegnante();
        this.terapiaDaModificare = terapia;
        this.aggiornamento = aggiornamento;
        this.salvataggio = null;
        this.modalitaModifica = true;

        titoloLabel.setText(
                "Modifica terapia - "
                        + terapia.getPaziente().getNome() + " "
                        + terapia.getPaziente().getCognome()
        );

        farmacoField.setText(terapia.getFarmaco());
        assunzioniField.setText(
                String.valueOf(terapia.getNumeroAssunzioniGiornaliere())
        );
        quantitaField.setText(
                String.valueOf(terapia.getDose())
        );
        indicazioniArea.setText(terapia.getIndicazioni());
    }

    private void salvaTerapia() {

        String farmaco = farmacoField.getText().trim();
        String assunzioniTesto = assunzioniField.getText().trim();
        String doseTesto = quantitaField.getText().trim();
        String indicazioni = indicazioniArea.getText().trim();

        if (farmaco.isEmpty() || assunzioniTesto.isEmpty() || doseTesto.isEmpty()) {
            return;
        }

        int assunzioni;
        int dose;

        try {
            assunzioni = Integer.parseInt(assunzioniTesto);
            dose = Integer.parseInt(doseTesto);
        } catch (NumberFormatException e) {
            return;
        }

        if (assunzioni <= 0 || dose <= 0) {
            return;
        }

        if (modalitaModifica) {

            terapiaDaModificare.setFarmaco(farmaco);
            terapiaDaModificare.setNumeroAssunzioniGiornaliere(assunzioni);
            terapiaDaModificare.setDose(dose);
            terapiaDaModificare.setIndicazioni(indicazioni);

            if (aggiornamento != null) {
                aggiornamento.run();
            }

        } else {

            Terapia nuovaTerapia = new Terapia(
                    farmaco,
                    dose,
                    assunzioni,
                    medico,
                    paziente,
                    indicazioni
            );

            if (salvataggio != null) {
                salvataggio.accept(nuovaTerapia);
            }
        }

        chiudiFinestra();
    }

    private void chiudiFinestra() {
        Stage stage =
                (Stage) salvaButton.getScene().getWindow();
        stage.close();
    }
}
