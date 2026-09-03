package application.classiGeneriche;

import java.util.Objects;

public final class Responsabile extends User{

	public Responsabile(String username, String password, String codiceFiscale, String nome, String cognome, String email){
		super(username, password, codiceFiscale, nome, cognome, email);

	}

	public Responsabile(){
		super("topolinopippo1", "topolino1", "TTTTTT", "Topolino", "Pluto", "plutotopolino@ulss9.it");
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(!(obj instanceof Responsabile r)) return false;
		return r.getUsername().equals(this.getUsername()) || r.getCodiceFiscale().equals(this.getCodiceFiscale());
 	}

	@Override
	public int hashCode() {
		return Objects.hash(getCodiceFiscale(), getUsername());
	}
}