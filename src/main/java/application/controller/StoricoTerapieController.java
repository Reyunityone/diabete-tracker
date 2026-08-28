package application.controller;

import application.classiGeneriche.Database;
import application.classiGeneriche.Diabetologo;
import application.classiGeneriche.Paziente;
import application.classiGeneriche.Terapia;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.TextField;

import java.io.IOException;

public class StoricoTerapieController {

    @FXML
    private Label titoloLabel;

    @FXML
    private Button aggiungiButton;

    @FXML
    private HBox aggiungiMenu;

    @FXML
    private Button nuovaTerapiaButton;

    @FXML
    private Button esistenteTerapiaButton;

    @FXML
    private ScrollPane terapieScrollPane;

    @FXML
    private VBox terapieContainer;

    @FXML
    private TextField searchField;

    private Paziente paziente;
    private Diabetologo medico;

    public void inizializza(Paziente paziente, Diabetologo medico) {

        this.paziente = paziente;
        this.medico = medico;

        titoloLabel.setText(
                "Terapie - "
                        + paziente.getNome() + " "
                        + paziente.getCognome()
        );

        aggiungiButton.setOnAction(
                event -> mostraMenuAggiungi()
        );

        nuovaTerapiaButton.setOnAction(
                event -> {

                    aggiungiMenu.setVisible(false);
                    aggiungiMenu.setManaged(false);

                    apriNuovaTerapia();
                }
        );

        esistenteTerapiaButton.setOnAction(
                event -> {

                    aggiungiMenu.setVisible(false);
                    aggiungiMenu.setManaged(false);

                    apriTerapieEsistenti();
                }
        );

        configuraRicerca();

        aggiornaLista();
    }

    private void aggiornaLista() {
            aggiornaLista("");
        }

    private void aggiornaLista(String ricerca) {

        terapieContainer.getChildren().clear();

        String testo =
                ricerca.toLowerCase().trim();

        for (Terapia terapia :
                Database.getInstance()
                        .getTerapieByPaziente(paziente)) {

            String nomeFarmaco =
                    terapia.getFarmaco().toLowerCase();

            if (!testo.isEmpty()
                    && !nomeFarmaco.contains(testo)) {
                continue;
            }

            terapieContainer.getChildren().add(
                    creaBoxTerapia(terapia)
            );
        }
    }

    private HBox creaBoxTerapia(Terapia terapia) {

        HBox box = new HBox(15);
        box.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        box.setPrefHeight(80);
        box.setMaxWidth(Double.MAX_VALUE);
        box.getStyleClass().add("history-item");

        VBox informazioni = new VBox(4);

        Label farmaco = new Label(
                terapia.getFarmaco()
        );
        farmaco.getStyleClass().add("history-date");

        Label dettagli = new Label(
                "Dose: " + terapia.getDose() + " mg"
                        + " | " + terapia.getNumeroAssunzioniGiornaliere()
                        + " assunzioni/giorno"
        );

        Label indicazioni = new Label(
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

        Region spazio = new Region();
        HBox.setHgrow(
                spazio,
                javafx.scene.layout.Priority.ALWAYS
        );

        Button modificaButton = new Button("Modifica");
        modificaButton.getStyleClass().add("open-button");
        modificaButton.setOnAction(
                event -> apriModificaTerapia(terapia)
        );

        Button eliminaButton = new Button("Elimina");
        eliminaButton.getStyleClass().add("open-button");
        eliminaButton.setOnAction(
                event -> eliminaTerapia(terapia)
        );

        box.getChildren().addAll(
                informazioni,
                spazio,
                modificaButton,
                eliminaButton
        );

        return box;
    }

    private void apriNuovaTerapia() {
        try {
            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/application/view/Terapia.fxml"
                            )
                    );

            Parent root = loader.load();

            TerapiaController controller =
                    loader.getController();

            controller.inizializzaNuova(
                    medico,
                    terapia -> {

                        Database.getInstance().assegnaTerapia(
                                terapia,
                                paziente
                        );

                        aggiornaLista();
                    }
            );

            Stage stage = new Stage();
            stage.setTitle("Nuova terapia");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void apriModificaTerapia(Terapia terapia) {
        try {
            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/application/view/Terapia.fxml"
                            )
                    );

            Parent root = loader.load();

            TerapiaController controller =
                    loader.getController();

            controller.inizializzaModifica(
                    terapia,
                    this::aggiornaLista
            );

            Stage stage = new Stage();
            stage.setTitle("Modifica terapia");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void configuraRicerca() {

        searchField.textProperty()
                .addListener(
                        (observable,
                         oldValue,
                         newValue) -> {

                            aggiornaLista(
                                    newValue
                            );
                        }
                );
    }

    private void eliminaTerapia(Terapia terapia) {

        Alert alert = new Alert(
                Alert.AlertType.CONFIRMATION
        );

        alert.setTitle("Elimina terapia");
        alert.setHeaderText("Eliminare la terapia?");
        alert.setContentText(
                terapia.getFarmaco()
        );

        alert.showAndWait().ifPresent(
                risposta -> {
                    if (risposta == javafx.scene.control.ButtonType.OK) {
                        terapia.getPazienti().remove(paziente);

                        Database.getInstance().save();

                        aggiornaLista();
                    }
                }
        );
    }

    private void mostraMenuAggiungi() {

        boolean visibile =
                !aggiungiMenu.isVisible();

        aggiungiMenu.setVisible(
                visibile
        );

        aggiungiMenu.setManaged(
                visibile
        );
    }

    private void apriTerapieEsistenti() {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/application/view/TerapieEsistenti.fxml"
                            )
                    );

            Parent root = loader.load();

            TerapieEsistentiController controller =
                    loader.getController();

            controller.inizializza(
                    paziente,
                    medico,
                    this::aggiornaLista
            );

            Stage stage = new Stage();

            stage.setTitle("Terapie esistenti");

            stage.setScene(
                    new Scene(root)
            );

            stage.setResizable(false);

            stage.show();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

}

