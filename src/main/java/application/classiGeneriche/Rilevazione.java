package application.classiGeneriche;

public class Rilevazione {

    private String data;
    private int livelloGlicemia;
    private String orarioPasto;
    private String orarioRilevazione;


    public Rilevazione(
            String data,
            int livelloGlicemia,
            String orarioPasto,
            String orarioRilevazione) {

        this.data = data;
        this.livelloGlicemia = livelloGlicemia;
        this.orarioPasto = orarioPasto;
        this.orarioRilevazione = orarioRilevazione;
    }


    // =========================================================
    // GETTER
    // =========================================================

    public String getData() {
        return data;
    }

    public String getOrarioPasto() {
        return orarioPasto;
    }

    public String getOrarioRilevazione() {
        return orarioRilevazione;
    }

    public int getLivelloGlicemia() {
        return livelloGlicemia;
    }



    // =========================================================
    // SETTER
    // =========================================================

    public void setData(String data) {
        this.data = data;
    }


    public void setLivelloGlicemia(
            int livelloGlicemia) {

        this.livelloGlicemia =
                livelloGlicemia;
    }

    public void setOrarioPasto(String orarioPasto) {
        this.orarioPasto = orarioPasto;
    }

    public void setOrarioRilevazione(String orarioRilevazione) {
        this.orarioRilevazione = orarioRilevazione;
    }
}