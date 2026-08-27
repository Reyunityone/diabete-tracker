package application.classiGeneriche;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class AssunzioneFarmaco implements Serializable {

    private LocalDate data;
    private LocalTime orarioAssunzione;
    private int quantita;
    private Terapia terapia;
    private Paziente paziente;


    public AssunzioneFarmaco (Paziente p, LocalDate data, LocalTime orarioAssunzione , int quantita, Terapia terapia) {
        this.paziente = p;
        this.data = data;
        this.orarioAssunzione = orarioAssunzione;
        this.quantita = quantita;
        this.terapia = terapia;
    }

    public LocalDate getData() {
        return data;
    }

    public int getQuantita() {
        return quantita;
    }

    public LocalTime getOrarioAssunzione() {
        return orarioAssunzione;
    }

    public Terapia getTerapia() {
        return terapia;
    }

    public void setPaziente(Paziente paziente) {
        this.paziente = paziente;
    }

    public Paziente getPaziente() {
        return paziente;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public void setQuantita(int quantita) {
        this.quantita = quantita;
    }

    public void setTerapia(Terapia terapia) {
        this.terapia = terapia;
    }

    public void setOrarioAssunzione(LocalTime orarioAssunzione) {
        this.orarioAssunzione = orarioAssunzione;
    }
}