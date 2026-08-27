package application.controller;

import application.classiGeneriche.MomentoRilevazione;
import application.classiGeneriche.Paziente;
import application.classiGeneriche.Rilevazione;

import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class AndamentoController {

	// ============================================================
	
    // ELEMENTI DELLA VIEW
	
    // ============================================================	
	
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
    private Button giornoButton;

    @FXML
    private Button settimanaButton;

    @FXML
    private Button meseButton;

    @FXML
    private DatePicker dataPicker;

    @FXML
    private Button indietroButton;

    @FXML
    private Button avantiButton;
    
    
    
    // ============================================================
    
    // CONTROLLER, FORMATTAZIONE E VISUALIZZAZIONE
    
    // ============================================================

    private Paziente paziente;

    private LocalDate dataSelezionata = LocalDate.now();
    
    private final DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final DateTimeFormatter formatoOrario = DateTimeFormatter.ofPattern("HH:mm");

    private final Locale localeItaliano = Locale.ITALIAN;

    private ModalitaVisualizzazione modalita = ModalitaVisualizzazione.GIORNO;
    
    private enum ModalitaVisualizzazione {GIORNO, SETTIMANA, MESE}

    
    
    // ============================================================
    
    // INIZIALIZZAZIONE DELLA SCHERMATA E DEI CONTROLLI
    
    // ============================================================
    
    @FXML
    public void initialize() {
        dataPicker.setValue(dataSelezionata);

        giornoButton.setOnAction(
        		event -> {
                    modalita =ModalitaVisualizzazione.GIORNO;
                    aggiornaGrafico();
                }
        );

        settimanaButton.setOnAction(
                event -> {
                    modalita =ModalitaVisualizzazione.SETTIMANA;
                    aggiornaGrafico();
                }
        );


        meseButton.setOnAction(
                event -> {
                    modalita = ModalitaVisualizzazione.MESE;
                    aggiornaGrafico();
                }
        );

        dataPicker.setOnAction(
                event -> {
                    if (dataPicker.getValue() != null) {
                        dataSelezionata =dataPicker.getValue();
                        aggiornaGrafico();
                    }
                }
        );

        indietroButton.setOnAction(event -> {cambiaPeriodo(-1);});
        
        avantiButton.setOnAction(event -> {cambiaPeriodo(1);});

        aggiornaGrafico();
    }
    
    
    
    // ============================================================
    
    // INIZIALIZZAZIONE DEL PAZIENTE
    
    // ============================================================

    public void inizializzaPaziente(Paziente paziente) {
        this.paziente = paziente;

        titoloLabel.setText("Andamento glicemico - "+ paziente.getNome()+ " "+ paziente.getCognome());

        aggiornaGrafico();
    }

    
    
    // ============================================================
    
    // CAMBIO DEL PERIODO VISUALIZZATO: GIORNO, SETTIMANA, MESE
    
    // ============================================================

    private void cambiaPeriodo(int direzione) {
        switch (modalita) {

            case GIORNO:
                dataSelezionata = dataSelezionata.plusDays(direzione);
                break;

            case SETTIMANA:
                dataSelezionata = dataSelezionata.plusWeeks(direzione);
                break;

            case MESE:
                dataSelezionata = dataSelezionata.plusMonths(direzione);
                break;
        }

        dataPicker.setValue(dataSelezionata);
        
        aggiornaGrafico();
    }
    
    
    
    // ============================================================
    
    // AGGIORNAMENTO DEL GRAFICO IN BASE ALLA MODALITÁ SELEZIONATA
    
    // ============================================================

    private void aggiornaGrafico() {
        if (grafico == null) {
            return;
        }

        grafico.getData().clear();
        
        switch (modalita) {
            case GIORNO:
                aggiornaGraficoGiorno();
                break;

            case SETTIMANA:
                aggiornaGraficoSettimana();
                break;

            case MESE:
                aggiornaGraficoMese();
                break;
        }
    }

    
    
    // ============================================================
    
    // VISUALIZZAZIONE GIORNALIERA PRIMA E DOPO I PASTI
    
    // ============================================================
    
    private void aggiornaGraficoGiorno() {
        periodoLabel.setText(dataSelezionata.format(DateTimeFormatter.ofPattern("dd MMMM yyyy",localeItaliano)));

        asseX.setLabel("Orario");
        asseY.setLabel("Glicemia (mg/dL)");

        List<Rilevazione> rilevazioni =rilevazioniDelGiorno(dataSelezionata);

        XYChart.Series<String, Number> prima =new XYChart.Series<>();
        prima.setName("Prima del pasto");

        XYChart.Series<String, Number> dopo =new XYChart.Series<>();
        dopo.setName("Dopo il pasto");


        for (Rilevazione rilevazione :rilevazioni) {
            String orario =rilevazione.getOrarioRilevazione().format(formatoOrario);

            Number valore =rilevazione.getLivelloGlicemia();

            if (isPrimaDelPasto(rilevazione.getMomentoRilevazione())) {
                prima.getData().add(new XYChart.Data<>(orario,valore));

            } else {
                dopo.getData().add(new XYChart.Data<>(orario,valore));
            }
        }

        grafico.getData().add(prima);
        grafico.getData().add(dopo);
    }

    
    
    // ============================================================
    
    // VISUALIZZAZIONE SETTIMANALE - MEDIA GLICEMIA PRIMA E DOPO I PASTI
    
    // ============================================================
    
    private void aggiornaGraficoSettimana() {
        LocalDate inizioSettimana =dataSelezionata.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate fineSettimana =inizioSettimana.plusDays(6);

        periodoLabel.setText("Settimana dal "+ inizioSettimana.format(formatoData)+ " al "+ fineSettimana.format(formatoData));

        asseX.setLabel("Giorno");
        asseY.setLabel("Glicemia media (mg/dL)");

        XYChart.Series<String, Number> prima =new XYChart.Series<>();
        prima.setName("Prima del pasto");

        XYChart.Series<String, Number> dopo =new XYChart.Series<>();
        dopo.setName("Dopo il pasto");


        for (int i = 0; i < 7; i++) {
            LocalDate giorno =inizioSettimana.plusDays(i);

            List<Rilevazione> rilevazioni =rilevazioniDelGiorno(giorno);

            List<Rilevazione> rilevazioniPrima =
                    rilevazioni.stream()
                            .filter(r ->
                                    isPrimaDelPasto(
                                            r.getMomentoRilevazione()
                                    )
                            )
                            .collect(
                                    Collectors.toList()
                            );


            List<Rilevazione> rilevazioniDopo =
                    rilevazioni.stream()
                            .filter(r ->
                                    !isPrimaDelPasto(
                                            r.getMomentoRilevazione()
                                    )
                            )
                            .collect(
                                    Collectors.toList()
                            );


            String nomeGiorno =giorno.getDayOfWeek().getDisplayName(TextStyle.FULL,localeItaliano);

            String etichetta =nomeGiorno+ " "+ giorno.format(formatoData);

            Double mediaPrima =calcolaMedia(rilevazioniPrima);
            Double mediaDopo =calcolaMedia(rilevazioniDopo);

            if (mediaPrima != null) {
                prima.getData().add(new XYChart.Data<>(etichetta,mediaPrima));
            }

            if (mediaDopo != null) {
                dopo.getData().add(new XYChart.Data<>(etichetta,mediaDopo));
            }
        }

        grafico.getData().add(prima);
        grafico.getData().add(dopo);
    }
    
    
    
    // ============================================================
    
    // VISUALIZZAZIONE MENSILE - MEDIA GLICEMIA PRIMA E DOPO I PASTI
    
    // ============================================================

    private void aggiornaGraficoMese() {
        YearMonth mese =YearMonth.from(dataSelezionata);

        periodoLabel.setText(mese.getMonth().getDisplayName(TextStyle.FULL,localeItaliano)+ " "+ mese.getYear());

        asseX.setLabel("Giorno");
        asseY.setLabel("Glicemia media (mg/dL)");

        XYChart.Series<String, Number> prima =new XYChart.Series<>();
        prima.setName("Prima del pasto");

        XYChart.Series<String, Number> dopo =new XYChart.Series<>();
        dopo.setName("Dopo il pasto");

        int numeroGiorni =mese.lengthOfMonth();

        for (int giorno = 1;giorno <= numeroGiorni;giorno++) {
            LocalDate data =mese.atDay(giorno);

            List<Rilevazione> rilevazioni =rilevazioniDelGiorno(data);
            
            List<Rilevazione> rilevazioniPrima =
                    rilevazioni.stream()
                            .filter(r ->
                                    isPrimaDelPasto(
                                            r.getMomentoRilevazione()
                                    )
                            )
                            .collect(
                                    Collectors.toList()
                            );


            List<Rilevazione> rilevazioniDopo =
                    rilevazioni.stream()
                            .filter(r ->
                                    !isPrimaDelPasto(
                                            r.getMomentoRilevazione()
                                    )
                            )
                            .collect(
                                    Collectors.toList()
                            );


            Double mediaPrima =calcolaMedia(rilevazioniPrima);
            Double mediaDopo =calcolaMedia(rilevazioniDopo);

            String etichetta =String.valueOf(giorno);

            if (mediaPrima != null) {
                prima.getData().add(new XYChart.Data<>(etichetta,mediaPrima));
            }

            if (mediaDopo != null) {
                dopo.getData().add(new XYChart.Data<>(etichetta,mediaDopo));
            }
        }

        grafico.getData().add(prima);
        grafico.getData().add(dopo);
    }
    
    
    
    // ============================================================
    
    // RILEVAZIONI DEL GIORNO (per ora dati fittizi)
    
    // ============================================================

    private List<Rilevazione> rilevazioniDelGiorno(LocalDate giorno) {
        List<Rilevazione> rilevazioni =new ArrayList<>();

        rilevazioni.add(new Rilevazione(giorno,90,LocalTime.of(7, 0),LocalTime.of(7, 30),MomentoRilevazione.PRIMA_COLAZIONE,paziente));
        rilevazioni.add(new Rilevazione(giorno,125,LocalTime.of(9, 30),LocalTime.of(9, 30),MomentoRilevazione.DOPO_COLAZIONE,paziente));

        rilevazioni.add(new Rilevazione(giorno,95,LocalTime.of(12, 0),LocalTime.of(12, 30),MomentoRilevazione.PRIMA_PRANZO,paziente));
        rilevazioni.add(new Rilevazione(giorno,140,LocalTime.of(14, 30),LocalTime.of(14, 30),MomentoRilevazione.DOPO_PRANZO,paziente));

        rilevazioni.add(new Rilevazione(giorno,100,LocalTime.of(19, 0),LocalTime.of(19, 30),MomentoRilevazione.PRIMA_CENA,paziente));
        rilevazioni.add(new Rilevazione(giorno,135,LocalTime.of(21, 30),LocalTime.of(21, 30),MomentoRilevazione.DOPO_CENA,paziente));

        rilevazioni.sort((r1, r2) ->r1.getOrarioRilevazione().compareTo(r2.getOrarioRilevazione()));
        
        return rilevazioni;
    }

    
    
    // ============================================================
    
    // CALCOLO DELLA MEDIA DELLE RILEVAZIONI
    
    // ============================================================

    private Double calcolaMedia(List<Rilevazione> rilevazioni) {
        if (rilevazioni == null|| rilevazioni.isEmpty()) return null;

        return rilevazioni.stream().mapToInt(Rilevazione::getLivelloGlicemia).average().orElse(0);
    }


    
    // ============================================================
    
    // CONTROLLO DEL MOMENTO DELLA RILEVAZIONE
    // ============================================================
    
    
    private boolean isPrimaDelPasto(MomentoRilevazione momento) {
        return momento ==MomentoRilevazione.PRIMA_COLAZIONE|| momento ==MomentoRilevazione.PRIMA_PRANZO|| momento ==MomentoRilevazione.PRIMA_CENA;
    }
}