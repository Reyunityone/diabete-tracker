package application.classiGeneriche;

import java.time.LocalDate;
import java.time.LocalTime;

public class Rilevazione {

    private LocalDate data;
    private int livelloGlicemia;
    private LocalTime orarioPasto;
    private LocalTime orarioRilevazione;
    private Paziente paziente;


    public Rilevazione(
            LocalDate data,
            int livelloGlicemia,
            LocalTime orarioPasto,
            LocalTime orarioRilevazione,
            Paziente paziente) {

        this.data = data;
        this.livelloGlicemia = livelloGlicemia;
        this.orarioRilevazione = orarioRilevazione;
        this.orarioPasto = orarioPasto;
        this.paziente = paziente;
    }


    // =========================================================
    // GETTER
    // =========================================================


    public LocalDate getData() {
        return data;
    }

    public LocalTime getOrarioPasto() {
        return orarioPasto;
    }

    public LocalTime getOrarioRilevazione() {
        return orarioRilevazione;
    }

    public int getLivelloGlicemia() {
        return livelloGlicemia;
    }



    // =========================================================
    // SETTER
    // =========================================================


    public void setOrarioRilevazione(LocalTime orarioRilevazione) {
        this.orarioRilevazione = orarioRilevazione;
    }

    public void setOrarioPasto(LocalTime orarioPasto) {
        this.orarioPasto = orarioPasto;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public void setLivelloGlicemia(
            int livelloGlicemia) {

        this.livelloGlicemia =
                livelloGlicemia;
    }

}