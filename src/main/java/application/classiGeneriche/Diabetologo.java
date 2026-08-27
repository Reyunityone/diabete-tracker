package application.classiGeneriche;

import java.util.ArrayList;
import java.util.List;

public final class Diabetologo extends User{

    private final List<Terapia> terapieAssegnate = new ArrayList<>();

    public Diabetologo(String username, String password, String codiceFiscale, String nome, String cognome, String email){
        super(username, password, codiceFiscale, nome, cognome, email);

    }

    public Diabetologo(){
        super("mariorossi1","mariorossi1" ,"MMMMMM", "Mario", "Rossi", "rossimario@ulss9.it");
    }

    public List<Terapia> getTerapieAssegnate() {
        return terapieAssegnate;
    }


    public void aggiungiTerapiaAssegnata(
            Terapia terapia) {

        boolean giaPresente =
                terapieAssegnate.stream()
                        .anyMatch(t ->
                                t.getFarmaco()
                                        .equalsIgnoreCase(
                                                terapia.getFarmaco()
                                        )
                                        &&
                                        t.getDose() == terapia.getDose()
                                        &&
                                        t.getNumeroAssunzioniGiornaliere() == terapia.getNumeroAssunzioniGiornaliere()
                                        &&
                                        java.util.Objects.equals(
                                                t.getIndicazioni(),
                                                terapia.getIndicazioni()
                                        )
                        );

        if (!giaPresente) {
            terapieAssegnate.add(terapia);
        }
    }



}
