package application.classiGeneriche;

public class User {
    private String username, codiceFiscale, nome, cognome, email;

    public User(String username, String codiceFiscale, String nome, String cognome, String email){
        if(!username.isEmpty()) this.username = username;
        if(!codiceFiscale.isEmpty()) this.codiceFiscale = codiceFiscale;
        if(!nome.isEmpty()) this.nome = nome;
        if(!cognome.isEmpty()) this.cognome = cognome;
        if(!email.isEmpty()) this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }
}
