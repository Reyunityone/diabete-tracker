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

    public ArrayList<Paziente> getPazienti(){
        return db.getPazienti().stream().filter(p -> p.getMedicoDiRiferimento().equals(this)).collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Paziente> getFilteredPazienti(String searchText) {

        return this.getPazienti().stream().filter(p -> {
            String nomeCognome = p.getNome() + " " + p.getCognome();
            if(nomeCognome.toLowerCase().contains(searchText.toLowerCase().trim())) return true;
            if(p.getCodiceFiscale().toLowerCase().contains(searchText.toLowerCase().trim())) return true;
            return false;
        }).collect(Collectors.toCollection(ArrayList::new));
    }

    public void creaTerapia(Paziente p, String farmaco, int dose, int numeroAssunzioniGiornaliere, String indicazioni){

    }

}