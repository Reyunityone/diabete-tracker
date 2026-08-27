package application.classiGeneriche;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class Diabetologo extends User {

    public Diabetologo(String username, String password,String codiceFiscale, String nome, String cognome, String email){
        super(username, password,codiceFiscale, nome, cognome, email);

    }

    public Diabetologo(){
        super("mariorossi1","diabetologo" ,"MMMMMM", "Mario", "Rossi", "rossimario@ulss9.it");
    }





}
