package application.classiGeneriche;

public final class Responsabile extends User{

	public Responsabile(String username, String password, String codiceFiscale, String nome, String cognome, String email){
		super(username, password, codiceFiscale, nome, cognome, email);

	}

	public Responsabile(){
		super("topolinopippo1", "topolino1", "TTTTTT", "Topolino", "Pluto", "plutotopolino@ulss9.it");
	}
}