package application.classiGeneriche;

public class SintomoFarmaco {

    private String data;
    private String indicazione;


    public SintomoFarmaco(
            String data,
            String indicazione) {

        this.data = data;
        this.indicazione = indicazione;
    }


    public String getData() {
        return data;
    }


    public String getIndicazione() {
        return indicazione;
    }


    public void setData(String data) {
        this.data = data;
    }


    public void setIndicazione(
            String indicazione) {

        this.indicazione =
                indicazione;
    }
}