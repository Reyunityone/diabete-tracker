package application.classiGeneriche;

public class Messaggio {

    private final String nome;
    private final String cognome;
    private final String testo;

    private boolean letto;


    public Messaggio(
            String nome,
            String cognome,
            String testo,
            boolean letto) {

        this.nome = nome;
        this.cognome = cognome;
        this.testo = testo;
        this.letto = letto;
    }


    public String getNome() {
        return nome;
    }


    public String getCognome() {
        return cognome;
    }


    public String getTesto() {
        return testo;
    }


    public boolean isLetto() {
        return letto;
    }


    public void setLetto(
            boolean letto) {

        this.letto = letto;
    }
}