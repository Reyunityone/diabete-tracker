package application.classiGeneriche;

public class Paziente extends User{
    private RiskFactor[] fattoriDiRischio;
    private Diabetologo medicoDiRiferimento;
    private String patologiePregresse;
    private String comorbidita;
    private String dettagli;

    public Paziente(String username,String codiceFiscale, String nome, String cognome, String email, RiskFactor[] fattoriDiRischio, Diabetologo medicoDiRiferimento, String patologiePregresse, String comorbidità, String dettagli){
        super(username, codiceFiscale, nome, cognome, email);
        this.fattoriDiRischio = fattoriDiRischio;
        this.medicoDiRiferimento = medicoDiRiferimento;
        this.patologiePregresse = patologiePregresse;
        this.comorbidita = comorbidita;
        this.dettagli = dettagli;
    }

    public Paziente(){
        super("luigiverdi1", "LLLLLL", "Luigi", "Verdi", "luigiverdi@libero.it");
        this.fattoriDiRischio = new RiskFactor[]{RiskFactor.EX_DIPENDENZA_STUPEFACENTI, RiskFactor.FUMATORE};
        this.medicoDiRiferimento = new Diabetologo();
        this.patologiePregresse = "Appendicite";
        this.comorbidita = "Nessuna";
        this.dettagli = "Asportata l'appendice a 15 anni";
    }
}
