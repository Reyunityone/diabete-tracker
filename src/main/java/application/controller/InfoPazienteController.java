package application.controller;

import application.classiGeneriche.Database;
import application.classiGeneriche.Paziente;
import application.classiGeneriche.RiskFactor;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.stream.Collectors;

public class InfoPazienteController {

    @FXML private Label titoloLabel;
    @FXML private TextArea fattoriRischioArea;
    @FXML private Button fattoriRischioButton;
    @FXML private TextArea patologieArea;
    @FXML private TextArea comorbiditaArea;
    @FXML private TextArea dettagliArea;
    @FXML private Button salvaButton;

    private Paziente paziente;
    private final Database db = Database.getInstance();
    private final ContextMenu menuFattoriRischio = new ContextMenu();

    @FXML
    public void initialize() {
        inizializzaMenuFattoriRischio();
        fattoriRischioButton.setOnAction(event -> apriMenuFattoriRischio());
        salvaButton.setOnAction(event -> salvaInformazioni());
    }

    public void inizializzaPaziente(Paziente paziente) {
        this.paziente = paziente;

        titoloLabel.setText("Info paziente - " + paziente.getNome() + " " + paziente.getCognome());

        RiskFactor[] fattoriRischio = db.getFattoriDiRischioByPaziente(paziente);

        for (var item : menuFattoriRischio.getItems()) {
            if (item instanceof CustomMenuItem customItem && customItem.getContent() instanceof CheckBox checkBox) {
                RiskFactor fattore = (RiskFactor) checkBox.getUserData();
                checkBox.setSelected(fattoriRischio != null && Arrays.asList(fattoriRischio).contains(fattore));
            }
        }

        aggiornaTestoFattoriRischio();

        String patologie = db.getPatologiePregresseByPaziente(paziente);
        String comorbidita = db.getComorbiditaByPaziente(paziente);
        String dettagli = db.getDettagliByPaziente(paziente);

        patologieArea.setText(patologie != null ? patologie : "");
        comorbiditaArea.setText(comorbidita != null ? comorbidita : "");
        dettagliArea.setText(dettagli != null ? dettagli : "");
    }

    private void inizializzaMenuFattoriRischio() {
        for (RiskFactor fattore : RiskFactor.values()) {
            CheckBox checkBox = new CheckBox(nomeFattore(fattore));
            checkBox.setUserData(fattore);
            checkBox.setOnAction(event -> aggiornaTestoFattoriRischio());

            CustomMenuItem item = new CustomMenuItem(checkBox);
            item.setHideOnClick(false);
            menuFattoriRischio.getItems().add(item);
        }
    }

    @FXML
    private void apriMenuFattoriRischio() {
        menuFattoriRischio.show(fattoriRischioButton, Side.BOTTOM, 0, 0);
    }

    private void aggiornaTestoFattoriRischio() {
        String testo = menuFattoriRischio.getItems().stream()
                .filter(item -> item instanceof CustomMenuItem)
                .map(item -> (CustomMenuItem) item)
                .map(CustomMenuItem::getContent)
                .filter(content -> content instanceof CheckBox)
                .map(content -> (CheckBox) content)
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .collect(Collectors.joining(", "));

        fattoriRischioArea.setText(testo);
    }

    private String nomeFattore(RiskFactor fattore) {
        return switch (fattore) {
            case FUMATORE -> "Fumatore";
            case EX_FUMATORE -> "Ex fumatore";
            case OBESITA -> "Obesità";
            case DIPENDENZA_ALCOOL -> "Dipendenza da alcool";
            case EX_DIPENDENZA_ALCOOL -> "Ex dipendenza da alcool";
            case DIPENDENZA_STUPEFACENTI -> "Dipendenza da stupefacenti";
            case EX_DIPENDENZA_STUPEFACENTI -> "Ex dipendenza da stupefacenti";
            case ALTRA_DIPENDENZA -> "Altra dipendenza";
        };
    }

    private void salvaInformazioni() {
        if (paziente == null) return;

        RiskFactor[] fattoriSelezionati = menuFattoriRischio.getItems().stream()
                .filter(item -> item instanceof CustomMenuItem)
                .map(item -> (CustomMenuItem) item)
                .map(CustomMenuItem::getContent)
                .filter(content -> content instanceof CheckBox)
                .map(content -> (CheckBox) content)
                .filter(CheckBox::isSelected)
                .map(CheckBox::getUserData)
                .map(RiskFactor.class::cast)
                .toArray(RiskFactor[]::new);

        paziente.setFattoriDiRischio(fattoriSelezionati);
        paziente.setPatologiePregresse(patologieArea.getText().trim());
        paziente.setComorbidita(comorbiditaArea.getText().trim());
        paziente.setDettagli(dettagliArea.getText().trim());

        db.updatePaziente(paziente, paziente);

        Stage stage = (Stage) salvaButton.getScene().getWindow();
        stage.close();
    }
}