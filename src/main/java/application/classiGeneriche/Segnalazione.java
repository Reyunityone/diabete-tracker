package application.classiGeneriche;

import java.io.Serializable;
import java.time.LocalDate;

public class Segnalazione implements Serializable {

    private LocalDate dataInizio;
    private LocalDate dataFine;
    private String testo;
    private Paziente paziente;


    public Segnalazione(
            LocalDate dataInizio,
            LocalDate dataFine,
            Paziente paziente,
            String testo) {

        this.dataInizio = dataInizio;
        if(dataFine !=null) this.dataFine = dataFine;
        this.paziente = paziente;
        this.testo = testo;
    }

    public Paziente getPaziente() {
        return paziente;
    }

    public LocalDate getDataFine() {
        return dataFine;
    }

    public LocalDate getDataInizio() {
        return dataInizio;
    }

    public void setPaziente(Paziente paziente) {
        this.paziente = paziente;
    }

    public void setDataFine(LocalDate dataFine) {
        this.dataFine = dataFine;
    }

    public void setDataInizio(LocalDate dataInizio) {
        this.dataInizio = dataInizio;
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }
}