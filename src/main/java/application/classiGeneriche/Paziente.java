package application.classiGeneriche;

import java.util.ArrayList;
import java.util.List;

public final class Paziente extends User{
    private RiskFactor[] fattoriDiRischio;
    private Diabetologo medicoDiRiferimento;
    private String patologiePregresse;
    private String comorbidita;
    private String dettagli;
    private final List<Terapia> terapie = new ArrayList<>();

    public Paziente(String username, String password, String codiceFiscale, String nome, String cognome, String email, RiskFactor[] fattoriDiRischio, Diabetologo medicoDiRiferimento, String patologiePregresse, String comorbidita, String dettagli){
        super(username, password, codiceFiscale, nome, cognome, email);
        this.fattoriDiRischio = fattoriDiRischio;
        this.medicoDiRiferimento = medicoDiRiferimento;
        this.patologiePregresse = patologiePregresse;
        this.comorbidita = comorbidita;
        this.dettagli = dettagli;
    }

    public Paziente(){
        super("luigiverdi1", "luigiverdi1", "LLLLLL", "Luigi", "Verdi", "luigiverdi@libero.it");
        this.fattoriDiRischio = new RiskFactor[]{RiskFactor.EX_DIPENDENZA_STUPEFACENTI, RiskFactor.FUMATORE};
        this.medicoDiRiferimento = new Diabetologo();
        this.patologiePregresse = "Appendicite";
        this.comorbidita = "Nessuna";
        this.dettagli = "Asportata l'appendice a 15 anni";
    }

    public RiskFactor[] getFattoriDiRischio() {
        return fattoriDiRischio;
    }

    public void setFattoriDiRischio(RiskFactor[] fattoriDiRischio) {
        this.fattoriDiRischio = fattoriDiRischio;
    }

    public Diabetologo getMedicoDiRiferimento() {
        return medicoDiRiferimento;
    }

    public void setMedicoDiRiferimento(Diabetologo medicoDiRiferimento) {
        this.medicoDiRiferimento = medicoDiRiferimento;
    }

    public String getPatologiePregresse() {
        return patologiePregresse;
    }

    public void setPatologiePregresse(String patologiePregresse) {
        this.patologiePregresse = patologiePregresse;
    }

    public String getComorbidita() {
        return comorbidita;
    }

    public void setComorbidita(String comorbidita) {
        this.comorbidita = comorbidita;
    }

    public String getDettagli() {
        return dettagli;
    }

    public void setDettagli(String dettagli) {
        this.dettagli = dettagli;
    }

    public List<Terapia> getTerapie() {
        return terapie;
    }

    public void aggiungiTerapia(Terapia terapia) {
        terapie.add(terapia);
    }

    public void rimuoviTerapia(
            Terapia terapia) {

        terapie.remove(terapia);
    }
}
