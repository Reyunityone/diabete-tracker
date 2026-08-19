package application.classiGeneriche;

import java.time.LocalDate;

public class Segnalazione {

    private LocalDate data;
    private String testo;


    public Segnalazione(
            LocalDate data,
            String testo) {

        this.data = data;
        this.testo = testo;
    }


    public LocalDate getData() {
        return data;
    }

    public String getTesto() {
        return testo;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }
}