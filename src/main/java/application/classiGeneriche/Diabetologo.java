package application.classiGeneriche;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class Diabetologo extends User {

    private Database db = Database.getInstance();

    public Diabetologo(String username, String password,String codiceFiscale, String nome, String cognome, String email){
        super(username, password,codiceFiscale, nome, cognome, email);
    }

    public Diabetologo(){
        super("doc","doc" ,"MMMMMM", "Mario", "Rossi", "rossimario@ulss9.it");
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof Diabetologo d)) return false;
        return this.getCodiceFiscale().equals(d.getCodiceFiscale()); // or whatever your unique key is
    }

    @Override
    public int hashCode(){
        return getCodiceFiscale().hashCode();
    }

    public boolean isMioPaziente(Paziente p){
        return p.getMedicoDiRiferimento().equals(this);
    }

    public void creaTerapia(Paziente p, String farmaco, int dose, int numeroAssunzioniGiornaliere, String indicazioni){
        ArrayList<Paziente> pazienti = new ArrayList<>();
        pazienti.add(p);

        Terapia terapia = new Terapia(farmaco, dose, numeroAssunzioniGiornaliere, this, pazienti, indicazioni);

        db.assegnaTerapia(terapia, p);
    }


}