package application.classiGeneriche;

public final class Diabetologo extends User{
    public Diabetologo(String username, String codiceFiscale, String nome, String cognome, String email){
        super(username, codiceFiscale, nome, cognome, email);
    }
    public Diabetologo(){
        super("mariorossi1", "MMMMMM", "Mario", "Rossi", "rossimario@ulss9.it");
    }

}
