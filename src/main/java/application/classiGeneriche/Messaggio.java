package application.classiGeneriche;

import java.io.Serializable;
import java.util.Objects;

public class Messaggio implements Serializable {

    private final Paziente paziente;
    private final Diabetologo diabetologo;
    private final String testo;
    private boolean letto;
    private final TipoAlert tipo;
    private final UrgenzaAlert urgenza;

    public Messaggio(Paziente paziente, Diabetologo diabetologo, String testo, TipoAlert tipo, UrgenzaAlert urgenza) {
        this.paziente = paziente;
        this.diabetologo = diabetologo;
        this.testo = testo;
        this.tipo = tipo;
        this.urgenza = urgenza;
        this.letto = false;
    }

    public Paziente getPaziente() {
        return paziente;
    }

    public Diabetologo getDiabetologo() {
        return diabetologo;
    }

    public String getTesto() {
        return testo;
    }

    public boolean isLetto() {
        return letto;
    }

    public TipoAlert getTipo() {
        return tipo;
    }

    public void setLetto(
            boolean letto) {

        this.letto = letto;
    }

    public User getMittente(){
        if(this.getTipo() == TipoAlert.MEDICO_PAZIENTE) return getDiabetologo();
        if(this.getTipo() == TipoAlert.PAZIENTE_MEDICO) return getPaziente();
        return null;
    }

    public String getMittenteString(){
        if(getMittente() == null) return "Sistema";
        return getMittente().getNome() + " " + getMittente().getCognome();
    }

    public UrgenzaAlert getUrgenza() {
        return urgenza;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof Messaggio m)) return false;
        return Objects.equals(this.getPaziente(), m.getPaziente()) && this.getTipo() == m.getTipo() && Objects.equals(this.getDiabetologo(), m.getDiabetologo()) && this.getTesto().equals(m.getTesto()) && this.getUrgenza() == m.getUrgenza();
    }
}