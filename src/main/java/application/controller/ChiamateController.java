package application.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import application.classiGeneriche.Chiamata;

public class ChiamateController {

    @FXML
    private Label titoloLabel;

    @FXML
    private TextField searchField;

    @FXML
    private VBox chiamateContainer;


    private List<Chiamata> chiamate =
            new ArrayList<>();


    private Runnable aggiornamentoNotifiche;


    public void inizializza(
            List<Chiamata> chiamate,
            Runnable aggiornamentoNotifiche) {

        this.chiamate = chiamate;

        this.aggiornamentoNotifiche =
                aggiornamentoNotifiche;

        configuraRicerca();

        aggiornaLista();
    }


    // =========================================================
    // RICERCA
    // =========================================================

    private void configuraRicerca() {

        searchField.textProperty()
                .addListener(
                        (observable,
                         oldValue,
                         newValue) -> {

                            aggiornaLista(newValue);

                        }
                );
    }


    // =========================================================
    // LISTA
    // =========================================================

    private void aggiornaLista() {

        aggiornaLista("");
    }


    private void aggiornaLista(
            String ricerca) {

        chiamateContainer
                .getChildren()
                .clear();


        String testo =
                ricerca
                        .toLowerCase()
                        .trim();


        for (Chiamata chiamata : chiamate) {

            String nomeCompleto =
                    chiamata.getNome()
                            + " "
                            + chiamata.getCognome();


            if (!testo.isEmpty()
                    && !nomeCompleto
                            .toLowerCase()
                            .contains(testo)) {

                continue;
            }


            chiamateContainer
                    .getChildren()
                    .add(
                            creaBoxChiamata(
                                    chiamata
                            )
                    );
        }
    }


    // =========================================================
    // BOX CHIAMATA
    // =========================================================

    private HBox creaBoxChiamata(
            Chiamata chiamata) {

        HBox box =
                new HBox();

        box.setSpacing(15);

        box.setAlignment(
                javafx.geometry.Pos.CENTER_LEFT
        );

        box.setPrefHeight(70);

        box.getStyleClass().add(
                "call-box"
        );


        // =====================================================
        // AVATAR
        // =====================================================

        ImageView avatar =
                new ImageView(
                        new Image(
                                getClass()
                                        .getResourceAsStream(
                                                "/application/images/avatar.png"
                                        )
                        )
                );

        avatar.setFitWidth(50);

        avatar.setFitHeight(50);

        avatar.setPreserveRatio(true);


        // =====================================================
        // INFORMAZIONI
        // =====================================================

        Label nome =
                new Label(
                        chiamata.getNome()
                                + " "
                                + chiamata.getCognome()
                );

        nome.getStyleClass().add(
                "message-name"
        );


        Label motivazione =
                new Label(
                        chiamata.getMotivazione()
                );

        motivazione.getStyleClass().add(
                "message-preview"
        );


        VBox informazioni =
                new VBox(
                        3,
                        nome,
                        motivazione
                );


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
        // APRI
        // =====================================================

        Button apri =
                new Button("Apri");

        apri.getStyleClass().add(
                "open-button"
        );


        apri.setOnAction(
                event ->
                        apriChiamata(
                                chiamata
                        )
        );


        box.getChildren().addAll(
                avatar,
                informazioni,
                spazio,
                apri
        );


        if (chiamata.isLetta()) {

            box.setOpacity(0.55);

        } else {

            box.setOpacity(1.0);
        }


        return box;
    }


    // =========================================================
    // APRI CHIAMATA
    // =========================================================

    private void apriChiamata(
            Chiamata chiamata) {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/application/view/Chiamata.fxml"
                            )
                    );


            Parent root =
                    loader.load();


            ChiamataController controller =
                    loader.getController();


            controller.inizializza(
                    chiamata,
                    () -> {

                        aggiornaLista();

                        aggiornamentoNotifiche.run();

                    }
            );


            Stage stage =
                    new Stage();


            stage.setTitle(
                    "Chiamata"
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