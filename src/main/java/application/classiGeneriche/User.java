package application.classiGeneriche;

public abstract sealed class User permits Paziente, Diabetologo, Responsabile{
    private String username, password, codiceFiscale, nome, cognome, email;

    public User(String username, String password, String codiceFiscale, String nome, String cognome, String email){
        if(!username.isEmpty()) this.username = username;
        if(!password.isEmpty()) this.setPassword(password);
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

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

	public String getPassword() {
		return password;
	}

    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Override
	public String toString() {
	    return nome + " " + cognome;
	}
}
