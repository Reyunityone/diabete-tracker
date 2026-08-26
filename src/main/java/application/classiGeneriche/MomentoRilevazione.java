package application.classiGeneriche;

public enum MomentoRilevazione {

    PRIMA_COLAZIONE("Prima di colazione"),
    DOPO_COLAZIONE("Dopo colazione"),
    PRIMA_PRANZO("Prima del pranzo"),
    DOPO_PRANZO("Dopo pranzo"),
    PRIMA_CENA("Prima della cena"),
    DOPO_CENA("Dopo della cena");

    private final String descrizione;

    MomentoRilevazione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getDescrizione() {
        return descrizione;
    }

    @Override
    public String toString() {
        return descrizione;
    }
}