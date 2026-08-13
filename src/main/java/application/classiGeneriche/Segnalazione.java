package application.classiGeneriche;

public class Segnalazione {

    private String data;
    private String testo;


    public Segnalazione(
            String data,
            String testo) {

        this.data = data;
        this.testo = testo;
    }


    public String getData() {
        return data;
    }


    public String getTesto() {
        return testo;
    }


    public void setData(String data) {
        this.data = data;
    }


    public void setTesto(String testo) {
        this.testo = testo;
    }
}