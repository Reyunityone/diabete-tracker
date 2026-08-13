package application.classiGeneriche;

public class Chiamata {

    private final String nome;
    private final String cognome;
    private final String motivazione;

    private boolean letta;


    public Chiamata(
            String nome,
            String cognome,
            String motivazione,
            boolean letta) {

        this.nome = nome;
        this.cognome = cognome;
        this.motivazione = motivazione;
        this.letta = letta;
    }


    public String getNome() {
        return nome;
    }


    public String getCognome() {
        return cognome;
    }


    public String getMotivazione() {
        return motivazione;
    }


    public boolean isLetta() {
        return letta;
    }


    public void setLetta(
            boolean letta) {

        this.letta = letta;
    }
}