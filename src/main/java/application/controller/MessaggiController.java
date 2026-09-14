package application.controller;

import application.classiGeneriche.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MessaggiController {

	//OGGETTI FXML
    @FXML private Label titoloLabel;
    @FXML private TextField searchField;
    @FXML private VBox messaggiContainer;

    //DATI
    private List<Messaggio> messaggi =new ArrayList<>();
    private Runnable aggiornamentoNotifiche;


    // =========================================================
    // INIZIALIZZAZIONE
    // =========================================================

    public void inizializza(List<Messaggio> messaggi,Runnable aggiornamentoNotifiche) {
        this.messaggi = messaggi;
        this.aggiornamentoNotifiche =aggiornamentoNotifiche;
        configuraRicerca();
        aggiornaLista();
    }

    // =========================================================
    // RICERCA
    // =========================================================

    private void configuraRicerca() {
        searchField.textProperty().addListener((observable,oldValue,newValue) -> {aggiornaLista(newValue);});
    }

    // =========================================================
    // LISTA
    // =========================================================

    private void aggiornaLista() {
        aggiornaLista("");
    }

    private void aggiornaLista(String ricerca) {
        messaggiContainer.getChildren().clear();

        String testo = ricerca.toLowerCase().trim();

        for (Messaggio messaggio : messaggi) {
            if (!testo.isEmpty()&& !messaggio.getMittenteString().toLowerCase().contains(testo)) {
                continue;
            }

            messaggiContainer.getChildren().add(creaBoxMessaggio(messaggio));
        }
    }


    // =========================================================
    // BOX MESSAGGIO
    // =========================================================

    private HBox creaBoxMessaggio(Messaggio messaggio) {
        HBox box =new HBox();
        box.setSpacing(15);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPrefHeight(70);
        box.setMaxWidth(Double.MAX_VALUE);
        box.getStyleClass().add("message-box");

        // AVATAR
        ImageView avatar =new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/application/images/avatar.png"))));
        avatar.setFitWidth(50);
        avatar.setFitHeight(50);
        avatar.setPreserveRatio(true);

        // NOME
        Label nome =new Label(messaggio.getMittenteString());
        nome.getStyleClass().add("message-name");

        // ANTEPRIMA
        String anteprima =messaggio.getTesto();
        if (anteprima.length() > 45) {
            anteprima =anteprima.substring(0,45)+ "...";
        }

        //TESTO
        Label testo =new Label(anteprima);
        testo.setWrapText(true);
        testo.getStyleClass().add("message-preview");

        //INFORMAZIONI
        VBox informazioni =new VBox(3,nome,testo);

        // SPAZIO
        Region spazio =new Region();
        HBox.setHgrow(spazio,Priority.ALWAYS);
        
        // APRI
        Button apri =new Button("Apri");
        apri.getStyleClass().add("open-button");
        apri.setOnAction(event ->apriMessaggio(messaggio));
        
        box.getChildren().addAll(avatar,informazioni,spazio,apri);

        // LETTO / NON LETTO
        aggiornaAspettoLetto(box,messaggio);

        return box;
    }


    // =========================================================
    // ASPETTO LETTO
    // =========================================================

    private void aggiornaAspettoLetto(HBox box,Messaggio messaggio) {
        if (messaggio.isLetto()) {
            box.setOpacity(0.55);

        } else {
            box.setOpacity(1.0);
        }
    }


    // =========================================================
    // APRI MESSAGGIO
    // =========================================================

    private void apriMessaggio(Messaggio messaggio) {
        try {
            FXMLLoader loader =new FXMLLoader(getClass().getResource("/application/view/Messaggio.fxml"));
            Parent root =loader.load();
            MessaggioController controller =loader.getController();
            controller.inizializza(messaggio,() -> {aggiornaLista();aggiornamentoNotifiche.run();});
            
            Stage stage =new Stage();
            stage.setTitle("Messaggio");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}