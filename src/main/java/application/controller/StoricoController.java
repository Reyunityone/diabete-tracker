package application.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import application.classiGeneriche.*;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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

    @FXML private Label titoloLabel;
    @FXML private TextField ricercaDataField;
    @FXML private VBox contenitoreStorico;

	 // =========================================================
	 // DATI
	 // =========================================================
	
	 private List<?> elementi;
	 private String tipo;
	 private Database db;
	 private boolean modalitaTest = false;
	 private Object ultimoControllerModifica;

    // =========================================================
    // INIZIALIZZAZIONE
    // =========================================================

    public void inizializza(List<?> elementi, String tipo) {
        this.db = Database.getInstance();
        this.elementi = elementi;
        this.tipo = tipo;
        this.user = (Paziente) Session.getInstance().getCurrentUser();
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
                titoloLabel.setText("Rilevazioni precedenti");
                break;

            case "sintomi":
                titoloLabel.setText("Sintomi / Farmaci precedenti");
                break;

            case "segnalazioni":
                titoloLabel.setText("Segnalazioni precedenti");
                break;

            default:
                titoloLabel.setText("Storico");
        }
    }

    // =========================================================
    // RICERCA
    // =========================================================

    private void configuraRicerca() {
        ricercaDataField.textProperty().addListener((observable, oldValue, newValue) -> {
            aggiornaLista();
        });
    }

    // =========================================================
    // AGGIORNA LISTA
    // =========================================================

    public void aggiornaLista() {
        if (user != null) {
            if (tipo.equals("rilevazioni")) {
                elementi = db.getRilevazioniByPaziente(user);
            } else if (tipo.equals("sintomi")) {
                elementi = db.getAssunzioniByPaziente(user);
            } else if (tipo.equals("segnalazioni")) {
                elementi = db.getSegnalazioniByPaziente(user);
            }
        }

        contenitoreStorico.getChildren().clear();

        if (elementi == null) {
            return;
        }

        String ricerca = ricercaDataField.getText().trim().toLowerCase();

        for (Object elemento : elementi) {
            String data = recuperaData(elemento);

            if (!ricerca.isEmpty() && !data.toLowerCase().contains(ricerca)) {
                continue;
            }

            HBox preview = creaPreview(elemento);
            contenitoreStorico.getChildren().add(preview);
        }
    }

    // =========================================================
    // RECUPERA DATA
    // =========================================================

    private String recuperaData(Object elemento) {
        if (elemento instanceof Rilevazione) {
            return ((Rilevazione) elemento).getData().toString();
        }

        if (elemento instanceof AssunzioneFarmaco) {
            return ((AssunzioneFarmaco) elemento).getData().toString();
        }

        if (elemento instanceof Segnalazione) {
            return ((Segnalazione) elemento).getDataInizio().toString();
        }

        return "";
    }

    // =========================================================
    // CREA PREVIEW
    // =========================================================

    private HBox creaPreview(Object elemento) {
        HBox box = new HBox();
        box.setSpacing(15);
        box.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        box.getStyleClass().add("history-item");

        VBox informazioni = new VBox();
        informazioni.setSpacing(5);

        // =====================================================
        // RILEVAZIONE
        // =====================================================

        if (elemento instanceof Rilevazione r) {
            Label data = new Label("Data: " + r.getData());
            Label contenuto = new Label("Glicemia: " + r.getLivelloGlicemia() + "    |    " + "Orario rilevazione: " + r.getOrarioRilevazione());

            data.getStyleClass().add("history-date");
            contenuto.getStyleClass().add("history-description");

            informazioni.getChildren().addAll(data, contenuto);
        }

        // =====================================================
        // SINTOMO / FARMACO
        // =====================================================

        else if (elemento instanceof AssunzioneFarmaco s) {
            Label data = new Label("Data: " + s.getData());
            Label contenuto = new Label("" + s.getQuantita());

            data.getStyleClass().add("history-date");
            contenuto.getStyleClass().add("history-description");

            informazioni.getChildren().addAll(data, contenuto);
        }

        // =====================================================
        // SEGNALAZIONE
        // =====================================================

        else if (elemento instanceof Segnalazione s) {
            Label data = new Label("Data: " + s.getDataInizio());
            Label contenuto = new Label(s.getTesto());

            data.getStyleClass().add("history-date");
            contenuto.getStyleClass().add("history-description");

            informazioni.getChildren().addAll(data, contenuto);
        }

        // =====================================================
        // SPAZIO
        // =====================================================

        Region spazio = new Region();
        HBox.setHgrow(spazio, javafx.scene.layout.Priority.ALWAYS);

        // =====================================================
        // BOTTONE MODIFICA
        // =====================================================

        Button modifica = new Button("Modifica");
        modifica.getStyleClass().add("standard-button");
        modifica.setOnAction(event -> modificaElemento(elemento));

        // =====================================================
        // BOTTONE ELIMINA
        // =====================================================

        Button elimina = new Button("Elimina");
        elimina.getStyleClass().add("delete-button");
        elimina.setOnAction(event -> confermaEliminazione(elemento));

        box.getChildren().addAll(informazioni, spazio, modifica, elimina);

        return box;
    }
    
	 // =========================================================
	 // CONFERMA ELIMINAZIONE
	 // =========================================================
	
	 private void confermaEliminazione(Object elemento) {
	     if (modalitaTest) {
	         eliminaElemento(elemento);
	         return;
	     }
	
	     Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
	     alert.setTitle("Eliminazione");
	     alert.setHeaderText("Eliminare questo elemento?");
	
	     Label descrizione = new Label("Stai per eliminare definitivamente " + descrizioneElemento(elemento) + ".");
	     descrizione.getStyleClass().add("delete-description");
	
	     Label avviso = new Label("Questa operazione non può essere annullata.");
	     avviso.getStyleClass().add("delete-warning");
	
	     VBox contenuto = new VBox(8);
	     contenuto.getChildren().addAll(descrizione, avviso);
	
	     alert.getDialogPane().setContent(contenuto);
	
	     Optional<ButtonType> risultato = alert.showAndWait();
	
	     if (risultato.isPresent() && risultato.get() == ButtonType.OK) {
	         eliminaElemento(elemento);
	     }
	 }

    // =========================================================
    // DESCRIZIONE ELEMENTO
    // =========================================================

    private String descrizioneElemento(Object elemento) {
        if (elemento instanceof Rilevazione) {
            return "questa rilevazione";
        }

        if (elemento instanceof AssunzioneFarmaco) {
            return "questa assunzione";
        }

        if (elemento instanceof Segnalazione) {
            return "questa segnalazione";
        }

        return "questo elemento";
    }

    // =========================================================
    // ELIMINA ELEMENTO
    // =========================================================

    private void eliminaElemento(Object elemento) {
        if (elemento instanceof Rilevazione) {
            db.deleteRilevazione((Rilevazione) elemento);
        } else if (elemento instanceof AssunzioneFarmaco) {
            db.deleteAssunzione((AssunzioneFarmaco) elemento);
        } else if (elemento instanceof Segnalazione) {
            Paziente p = (Paziente) Session.getInstance().getCurrentUser();
            Messaggio m = new Messaggio(null,p.getMedicoDiRiferimento(), "[Segnalazione eliminata] " + elemento.toString(), TipoAlert.SISTEMA_MEDICO, UrgenzaAlert.LOW);
            db.deleteSegnalazione((Segnalazione) elemento);
            db.addMessaggio(m);
        }

        aggiornaLista();
    }

    // =========================================================
    // MODIFICA
    // =========================================================

    private void modificaElemento(Object elemento) {
        if (elemento instanceof Rilevazione) {
            apriFinestraModifica(elemento, "/application/view/Rilevazione.fxml", "Modifica rilevazione");
            return;
        }

        if (elemento instanceof AssunzioneFarmaco) {
            apriFinestraModifica(elemento, "/application/view/AssunzioneFarmaco.fxml", "Modifica sintomo / farmaco");
            return;
        }

        if (elemento instanceof Segnalazione) {
            apriFinestraModifica(elemento, "/application/view/Segnalazione.fxml", "Modifica segnalazione");
        }
    }

    // =========================================================
    // APERTURA FINESTRA MODIFICA
    // =========================================================

    private void apriFinestraModifica(Object elemento, String percorsoFXML, String titolo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(percorsoFXML));
            Parent root = loader.load();

            if (elemento instanceof Rilevazione) {
                RilevazioneController controller = loader.getController();
                controller.inizializzaModifica((Rilevazione) elemento, this::aggiornaLista);
                ultimoControllerModifica = controller;

            } else if (elemento instanceof AssunzioneFarmaco) {
                FarmacoController controller = loader.getController();
                controller.inizializzaModifica((AssunzioneFarmaco) elemento, this::aggiornaLista);
                ultimoControllerModifica = controller;

            } else if (elemento instanceof Segnalazione) {
                SegnalazioneController controller = loader.getController();
                controller.inizializzaModifica((Segnalazione) elemento, this::aggiornaLista);
                ultimoControllerModifica = controller;
            }

            Stage stage = new Stage();
            stage.setTitle(titolo);
            stage.setScene(new Scene(root));
            stage.setResizable(false);

            if (!modalitaTest) {
                stage.show();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public Object getUltimoControllerModifica() {
        return ultimoControllerModifica;
    }
    
    public void setModalitaTest(boolean modalitaTest) {
        this.modalitaTest = modalitaTest;
    }
}