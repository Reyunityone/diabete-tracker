package application.classiGeneriche;

import java.time.LocalDate;
import java.time.LocalTime;

public class AssunzioneFarmaco {

    private LocalDate data;
    private LocalTime orarioAssunzione;
    private int quantita;
    private Terapia terapia;


    public AssunzioneFarmaco (LocalDate data, LocalTime orarioAssunzione ,int quantita, Terapia terapia) {

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