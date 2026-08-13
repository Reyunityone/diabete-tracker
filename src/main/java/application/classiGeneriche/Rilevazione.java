package application.classiGeneriche;

public class Rilevazione {

    private String data;
    private String livelloGlicemia;
    private String momentoGiornata;


    public Rilevazione(
            String data,
            String livelloGlicemia,
            String momentoGiornata) {

        this.data = data;
        this.livelloGlicemia = livelloGlicemia;
        this.momentoGiornata = momentoGiornata;
    }


    // =========================================================
    // GETTER
    // =========================================================

    public String getData() {
        return data;
    }


    public String getLivelloGlicemia() {
        return livelloGlicemia;
    }


    public String getMomentoGiornata() {
        return momentoGiornata;
    }


    // =========================================================
    // SETTER
    // =========================================================

    public void setData(String data) {
        this.data = data;
    }


    public void setLivelloGlicemia(
            String livelloGlicemia) {

        this.livelloGlicemia =
                livelloGlicemia;
    }


    public void setMomentoGiornata(
            String momentoGiornata) {

        this.momentoGiornata =
                momentoGiornata;
    }
}