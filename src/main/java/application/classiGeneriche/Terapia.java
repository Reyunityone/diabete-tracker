package application.classiGeneriche;

public class Terapia {
    private String farmaco;
    private int dose;
    private int numeroAssunzioniGiornaliere;
    private Diabetologo medicoAssegnante;
    private Paziente paziente;
    private String indicazioni;

    public Terapia(String farmaco, int dose, int numeroAssunzioniGiornaliere, Diabetologo medicoAssegnante, Paziente paziente, String indicazioni){
        this.farmaco = farmaco;
        this.dose = dose;
        this.numeroAssunzioniGiornaliere = numeroAssunzioniGiornaliere;
        this.medicoAssegnante = medicoAssegnante;
        this.paziente = paziente;
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

    public Paziente getPaziente() {
        return paziente;
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

    public void setPaziente(Paziente paziente) {
        this.paziente = paziente;
    }

    @Override
    public String toString() {
        return "[" + farmaco.toUpperCase() + "] dose: " + dose + "mg " + numeroAssunzioniGiornaliere + " volte al giorno, " + indicazioni;
    }
}


