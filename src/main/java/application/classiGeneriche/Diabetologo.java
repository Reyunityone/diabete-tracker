package application.classiGeneriche;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class Diabetologo extends User {

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
        return this.getCodiceFiscale().equals(d.getCodiceFiscale());
    }

    @Override
    public int hashCode(){
        return getCodiceFiscale().hashCode();
    }

}