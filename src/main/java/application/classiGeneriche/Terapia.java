package application.classiGeneriche;

import java.io.Serializable;
import java.util.ArrayList;

public class Terapia implements Serializable {
    private String farmaco;
    private int dose;
    private int numeroAssunzioniGiornaliere;
    private Diabetologo medicoAssegnante;
    private ArrayList<Paziente> pazienti;
    private String indicazioni;

    public Terapia(String farmaco, int dose, int numeroAssunzioniGiornaliere, Diabetologo medicoAssegnante, ArrayList<Paziente> pazienti, String indicazioni){
        this.farmaco = farmaco;
        this.dose = dose;
        this.numeroAssunzioniGiornaliere = numeroAssunzioniGiornaliere;
        this.medicoAssegnante = medicoAssegnante;
        this.pazienti = pazienti;
        this.indicazioni = indicazioni;
    }

    public Diabetologo getMedicoAssegnante() {
        return medicoAssegnante;
    }

    public int getDose() {
        return dose;
    }

    public int getNumeroAssunzioniGiornaliere() {
        return numeroAssunzioniGiornaliere;
    }

    public ArrayList<Paziente> getPazienti() {
        return pazienti;
    }

    public String getFarmaco() {
        return farmaco;
    }

    public String getIndicazioni() {
        return indicazioni;
    }

    public void setDose(int dose) {
        this.dose = dose;
    }

    public void setFarmaco(String farmaco) {
        this.farmaco = farmaco;
    }

    public void setIndicazioni(String indicazioni) {
        this.indicazioni = indicazioni;
    }

    public void setMedicoAssegnante(Diabetologo medicoAssegnante) {
        this.medicoAssegnante = medicoAssegnante;
    }

    public void setNumeroAssunzioniGiornaliere(int numeroAssunzioniGiornaliere) {
        this.numeroAssunzioniGiornaliere = numeroAssunzioniGiornaliere;
    }

    public void setPazienti(ArrayList<Paziente> pazienti) {
        this.pazienti = pazienti;
    }

    @Override
    public String toString() {
        return "[" + farmaco.toUpperCase() + "] dose: " + dose + "mg " + numeroAssunzioniGiornaliere + " volte al giorno, " + indicazioni;
    }
}


