package application.classiGeneriche;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class Rilevazione implements Serializable {

    private LocalDate data;
    private int livelloGlicemia;
    private LocalTime orarioPasto;
    private LocalTime orarioRilevazione;
    private MomentoRilevazione momentoAssunzione;
    private Paziente paziente;


    public Rilevazione(LocalDate data, int livelloGlicemia, LocalTime orarioPasto, LocalTime orarioRilevazione, MomentoRilevazione momentoAssunzione, Paziente paziente) {
        this.data = data;
        this.livelloGlicemia = livelloGlicemia;
        this.orarioRilevazione = orarioRilevazione;
        this.orarioPasto = orarioPasto;
        this.momentoAssunzione=momentoAssunzione;
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

    public Paziente getPaziente() {
        return paziente;
    }
    

	public MomentoRilevazione getMomentoRilevazione() {
		return momentoAssunzione;
	}

    // =========================================================
    // SETTER
    // =========================================================


    public void setPaziente(Paziente paziente) {
        this.paziente = paziente;
    }

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


	public void setMomentoRilevazione(MomentoRilevazione momentoAssunzione) {
		this.momentoAssunzione = momentoAssunzione;
	}

}