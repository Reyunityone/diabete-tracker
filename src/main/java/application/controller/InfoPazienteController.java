package application.controller;

import application.classiGeneriche.*;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class InfoPazienteController {

    @FXML private Label titoloLabel;
    @FXML private TextArea fattoriRischioArea;
    @FXML private Button fattoriRischioButton;
    @FXML private TextArea patologieArea;
    @FXML private TextArea comorbiditaArea;
    @FXML private TextArea dettagliArea;
    @FXML private Button salvaButton;
    @FXML private Button storicoButton;


    private Paziente paziente;
    private final Database db = Database.getInstance();
    private final ContextMenu menuFattoriRischio = new ContextMenu();

    @FXML
    public void initialize() {
        inizializzaMenuFattoriRischio();
        fattoriRischioButton.setOnAction(event -> apriMenuFattoriRischio());
        salvaButton.setOnAction(event -> salvaInformazioni());
        storicoButton.setOnAction(event -> apriStoricoModifiche());
    }

    public void inizializzaPaziente(Paziente paziente) {
        this.paziente = paziente;

        titoloLabel.setText("Info paziente - " + paziente.getNome() + " " + paziente.getCognome());

        List<RiskFactor> fattoriRischio = db.getFattoriDiRischioByPaziente(paziente);

        for (var item : menuFattoriRischio.getItems()) {
            if (item instanceof CustomMenuItem customItem && customItem.getContent() instanceof CheckBox checkBox) {
                RiskFactor fattore = (RiskFactor) checkBox.getUserData();
                checkBox.setSelected(fattoriRischio != null && fattoriRischio.contains(fattore));
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

        List<RiskFactor> fattoriSelezionati = menuFattoriRischio.getItems().stream()
                .filter(item -> item instanceof CustomMenuItem)
                .map(item -> (CustomMenuItem) item)
                .map(CustomMenuItem::getContent)
                .filter(content -> content instanceof CheckBox)
                .map(content -> (CheckBox) content)
                .filter(CheckBox::isSelected)
                .map(CheckBox::getUserData)
                .map(RiskFactor.class::cast)
                .collect(Collectors.toList());
        LogOperazione.SnapshotPaziente beforeState = new LogOperazione.SnapshotPaziente(paziente.getFattoriDiRischio(), paziente.getComorbidita(), paziente.getDettagli(), paziente.getPatologiePregresse());
        paziente.setFattoriDiRischio(fattoriSelezionati);
        paziente.setPatologiePregresse(patologieArea.getText().trim());
        paziente.setComorbidita(comorbiditaArea.getText().trim());
        paziente.setDettagli(dettagliArea.getText().trim());
        LogOperazione.SnapshotPaziente afterState = new LogOperazione.SnapshotPaziente(paziente.getFattoriDiRischio(), paziente.getComorbidita(), paziente.getDettagli(), paziente.getPatologiePregresse());
        db.updatePaziente(paziente, paziente);
        GestoreLog.registraModificaPaziente(paziente, beforeState, afterState, false, false);
        Stage stage = (Stage) salvaButton.getScene().getWindow();
        stage.close();
    }

    private List<LogOperazione> logStoricoCorrente = new ArrayList<>();
    private VBox contenitoreStorico;
    
    private void apriStoricoModifiche() {
        if (paziente == null) return;

        Stage stage = creaStoricoStage();
        stage.show();
    }
    
    public Stage creaStoricoStage() {
        Label titolo = new Label("Storico modifiche - " + paziente.getNome() + " " + paziente.getCognome());
        titolo.getStyleClass().add("history-title");

        TextField ricercaField = new TextField();
        ricercaField.setPromptText("Cerca nello storico...");
        ricercaField.getStyleClass().add("search-field");

        contenitoreStorico = new VBox(12);

        ScrollPane scrollPane = new ScrollPane(contenitoreStorico);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().addAll("history-scroll", "list-scroll");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        ricercaField.textProperty().addListener(
                (observable, oldValue, newValue) -> aggiornaListaStorico(newValue)
        );

        VBox root = new VBox(15, titolo, ricercaField, scrollPane);
        root.setPadding(new Insets(25, 30, 25, 30));
        root.setPrefSize(500, 550);

        logStoricoCorrente = Database.getInstance().getLogsByAutore(Session.getInstance().getCurrentUser());
        aggiornaListaStorico("");

        Scene scene = new Scene(root);
        scene.getStylesheets().addAll(
                Objects.requireNonNull(getClass().getResource("/application/css/graficaComune.css")).toExternalForm(),
                Objects.requireNonNull(getClass().getResource("/application/css/diabetologo.css")).toExternalForm(),
                Objects.requireNonNull(getClass().getResource("/application/css/storico.css")).toExternalForm()
        );

        Stage stage = new Stage();
        stage.setTitle("Storico modifiche");
        stage.setScene(scene);
        stage.setResizable(false);

        return stage;
    }

    private void aggiornaListaStorico(String ricerca) {
        contenitoreStorico.getChildren().clear();

        String testo = ricerca == null ? "" : ricerca.toLowerCase().trim();

        List<LogOperazione> filtrati = logStoricoCorrente.stream()
                .filter(log -> testo.isEmpty()
                        || log.getDescrizione().toLowerCase().contains(testo)
                        || log.getAutoreString().toLowerCase().contains(testo)
                        || log.getTimestamp().toLocalDate().toString().contains(testo))
                .toList();

        if (filtrati.isEmpty()) {
            Label vuoto = new Label("Nessuna modifica registrata.");
            vuoto.getStyleClass().add("history-description");
            contenitoreStorico.getChildren().add(vuoto);
            return;
        }

        for (LogOperazione log : filtrati) {
            contenitoreStorico.getChildren().add(creaBoxLog(log));
        }
    }

    private Node creaBoxLog(LogOperazione log) {
        HBox box = new HBox(15);
        box.setAlignment(Pos.CENTER_LEFT);
        box.getStyleClass().add("history-item");

        // -----------------------------------------------------
        // INFORMAZIONI
        // -----------------------------------------------------
        VBox informazioni = new VBox(5);

        Label data = new Label(
                log.getTimestamp().toLocalDate() + "  " + log.getTimestamp().toLocalTime().withNano(0)
                        + "  ·  " + log.getAutoreString()
        );
        data.getStyleClass().add("history-date");

        Label descrizione = new Label(log.getDescrizione());
        descrizione.setWrapText(true);
        descrizione.getStyleClass().add("history-description");

        informazioni.getChildren().addAll(data, descrizione);

        if (log.isRipristinato()) {
            Label ripristinato = new Label("Modifica già ripristinata");
            ripristinato.getStyleClass().add("profile-role");
            informazioni.getChildren().add(ripristinato);
        }

        // -----------------------------------------------------
        // SPAZIO
        // -----------------------------------------------------
        Region spazio = new Region();
        HBox.setHgrow(spazio, Priority.ALWAYS);

        box.getChildren().addAll(informazioni, spazio);

        // -----------------------------------------------------
        // AZIONE DI RIPRISTINO
        // -----------------------------------------------------
        if (log.isReversibile()) {
            Button annullaButton = new Button();
            annullaButton.getStyleClass().addAll("secondary-button", "modify-button");
            ImageView icona = new ImageView(new Image("/application/images/vediPrecedenti.png"));
            icona.setFitHeight(20);
            icona.setFitWidth(20);
            icona.setPreserveRatio(true);
            annullaButton.setGraphic(icona);
            annullaButton.setOnAction(event -> {
                boolean esito = GestoreLog.undoOperation(log);
                if (esito) {
                    // Ricarico il paziente con lo stato ripristinato
                    inizializzaPaziente(paziente);
                    logStoricoCorrente = Database.getInstance().getLogsByAutore(Session.getInstance().getCurrentUser());
                    aggiornaListaStorico("");
                }
            });
            box.getChildren().add(annullaButton);
        }

        return box;
    }
}
