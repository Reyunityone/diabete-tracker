package application.controller;

import java.io.IOException;
import java.util.List;

import application.classiGeneriche.Rilevazione;
import application.classiGeneriche.Segnalazione;
import application.classiGeneriche.AssunzioneFarmaco;

import application.classiGeneriche.Paziente;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StoricoController {

    private Paziente user;
    // =========================================================
    // FXML
    // =========================================================

    @FXML
    private Label titoloLabel;

    @FXML
    private TextField ricercaDataField;

    @FXML
    private VBox contenitoreStorico;


    // =========================================================
    // DATI
    // =========================================================

    private List<?> elementi;

    private String tipo;


    // =========================================================
    // INIZIALIZZAZIONE
    // =========================================================

    public void inizializza(
            Paziente user,
            List<?> elementi,
            String tipo) {

        this.elementi = elementi;
        this.user = user;
        this.tipo = tipo;

        impostaTitolo();

        configuraRicerca();

        aggiornaLista();
    }


    // =========================================================
    // TITOLO
    // =========================================================

    private void impostaTitolo() {

        switch (tipo) {

            case "rilevazioni":

                titoloLabel.setText(
                        "Rilevazioni precedenti"
                );

                break;


            case "sintomi":

                titoloLabel.setText(
                        "Sintomi / Farmaci precedenti"
                );

                break;


            case "segnalazioni":

                titoloLabel.setText(
                        "Segnalazioni precedenti"
                );

                break;


            default:

                titoloLabel.setText(
                        "Storico"
                );
        }
    }


    // =========================================================
    // RICERCA
    // =========================================================

    private void configuraRicerca() {

        ricercaDataField
                .textProperty()
                .addListener(
                        (observable,
                         oldValue,
                         newValue) -> {

                            aggiornaLista();

                        }
                );
    }


    // =========================================================
    // AGGIORNA LISTA
    // =========================================================

    private void aggiornaLista() {

        contenitoreStorico
                .getChildren()
                .clear();


        if (elementi == null) {

            return;
        }


        String ricerca =
                ricercaDataField
                        .getText()
                        .trim()
                        .toLowerCase();


        for (Object elemento : elementi) {

            String data =
                    recuperaData(elemento);


            if (!ricerca.isEmpty()
                    && !data
                    .toLowerCase()
                    .contains(ricerca)) {

                continue;
            }


            HBox preview =
                    creaPreview(elemento);


            contenitoreStorico
                    .getChildren()
                    .add(preview);
        }
    }


    // =========================================================
    // RECUPERA DATA
    // =========================================================

    private String recuperaData(
            Object elemento) {

        if (elemento instanceof Rilevazione) {

            return ((Rilevazione) elemento)
                    .getData().toString();
        }


        if (elemento instanceof AssunzioneFarmaco) {

            return ((AssunzioneFarmaco) elemento)
                    .getData().toString();
        }


        if (elemento instanceof Segnalazione) {

            return ((Segnalazione) elemento)
                    .getDataInizio().toString();
        }


        return "";
    }


    // =========================================================
    // CREA PREVIEW
    // =========================================================

    private HBox creaPreview(
            Object elemento) {

        HBox box =
                new HBox();

        box.setSpacing(15);

        box.setAlignment(
                javafx.geometry.Pos.CENTER_LEFT
        );

        box.getStyleClass()
                .add("history-item");


        VBox informazioni =
                new VBox();

        informazioni.setSpacing(5);


        // =====================================================
        // RILEVAZIONE
        // =====================================================

        if (elemento instanceof Rilevazione r) {


            Label data =
                    new Label(
                            "Data: "
                                    + r.getData()
                    );


            Label contenuto =
                    new Label(
                            "Glicemia: "
                                    + r.getLivelloGlicemia()
                                    + "    |    "
                                    + "Orario rilevazione: "
                                    + r.getOrarioRilevazione()
                    );


            data.getStyleClass()
                    .add("history-date");

            contenuto.getStyleClass()
                    .add("history-description");


            informazioni
                    .getChildren()
                    .addAll(
                            data,
                            contenuto
                    );
        }


        // =====================================================
        // SINTOMO / FARMACO
        // =====================================================

        else if (elemento instanceof AssunzioneFarmaco s) {


            Label data =
                    new Label(
                            "Data: "
                                    + s.getData()
                    );


            Label contenuto =
                    new Label(
                            ""+ s.getQuantita()
                    );


            data.getStyleClass()
                    .add("history-date");

            contenuto.getStyleClass()
                    .add("history-description");


            informazioni
                    .getChildren()
                    .addAll(
                            data,
                            contenuto
                    );
        }


        // =====================================================
        // SEGNALAZIONE
        // =====================================================

        else if (elemento instanceof Segnalazione s) {


            Label data =
                    new Label(
                            "Data: "
                                    + s.getDataInizio()
                    );


            Label contenuto =
                    new Label(
                            s.getTesto()
                    );


            data.getStyleClass()
                    .add("history-date");

            contenuto.getStyleClass()
                    .add("history-description");


            informazioni
                    .getChildren()
                    .addAll(
                            data,
                            contenuto
                    );
        }


        // =====================================================
        // SPAZIO
        // =====================================================

        Region spazio =
                new Region();


        HBox.setHgrow(
                spazio,
                javafx.scene.layout.Priority.ALWAYS
        );


        // =====================================================
        // BOTTONE MODIFICA
        // =====================================================

        Button modifica =
                new Button(
                        "Modifica"
                );


        modifica.getStyleClass()
                .add("modify-button");


        modifica.setOnAction(
                event ->
                        modificaElemento(
                                elemento
                        )
        );


        box.getChildren().addAll(
                informazioni,
                spazio,
                modifica
        );


        return box;
    }


    // =========================================================
    // MODIFICA
    // =========================================================

    private void modificaElemento(
            Object elemento) {

        if (elemento instanceof Rilevazione) {

            modificaRilevazione(
                    (Rilevazione) elemento
            );

            return;
        }


        if (elemento instanceof AssunzioneFarmaco) {

            modificaSintomo(
                    (AssunzioneFarmaco) elemento
            );

            return;
        }


        if (elemento instanceof Segnalazione) {

            modificaSegnalazione(
                    (Segnalazione) elemento
            );
        }
    }


    // =========================================================
    // MODIFICA RILEVAZIONE
    // =========================================================

    private void modificaRilevazione(
            Rilevazione rilevazione) {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/application/view/Rilevazione.fxml"
                            )
                    );

            Parent root = loader.load();

            RilevazioneController controller =
                    loader.getController();

            controller.inizializzaModifica(
                    user,
                    rilevazione,
                    this::aggiornaLista
            );

            Stage stage = new Stage();

            stage.setTitle(
                    "Modifica rilevazione"
            );

            stage.setScene(
                    new Scene(root)
            );

            stage.setResizable(false);

            stage.show();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }


    // =========================================================
    // MODIFICA SINTOMO
    // =========================================================

    private void modificaSintomo(
            AssunzioneFarmaco sintomo) {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/application/view/AssunzioneFarmaco.fxml"
                            )
                    );

            Parent root = loader.load();

            FarmacoController controller =
                    loader.getController();

            controller.inizializzaModifica(
                    sintomo,
                    this::aggiornaLista
            );

            Stage stage = new Stage();

            stage.setTitle(
                    "Modifica sintomo / farmaco"
            );

            stage.setScene(
                    new Scene(root)
            );

            stage.setResizable(false);

            stage.show();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }
    
    // =========================================================
    // MODIFICA SEGNALAZIONE
    // =========================================================

    private void modificaSegnalazione(
            Segnalazione segnalazione) {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/application/view/Segnalazione.fxml"
                            )
                    );

            Parent root = loader.load();

            SegnalazioneController controller =
                    loader.getController();

            controller.inizializzaModifica(
                    user,
                    segnalazione,
                    this::aggiornaLista
            );

            Stage stage = new Stage();

            stage.setTitle(
                    "Modifica segnalazione"
            );

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