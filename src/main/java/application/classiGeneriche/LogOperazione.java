package application.classiGeneriche;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class LogOperazione {

    private final LocalDateTime timestamp;
    private final User author;
    private final Paziente pazienteModificato;
    private final String descrizione;
    private final SnapshotPaziente beforeState;
    private final SnapshotPaziente afterState;
    private boolean ripristinato;

    public LogOperazione(User author, Paziente pazienteModificato, String descrizione, SnapshotPaziente beforeState, SnapshotPaziente afterState){
        this.timestamp = LocalDateTime.now();
        this.author = author;
        this.pazienteModificato = pazienteModificato;
        this.descrizione = descrizione;
        this.beforeState = beforeState;
        this.afterState = afterState;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public User getAuthor() {
        return author;
    }

    public Paziente getPazienteModificato() {
        return pazienteModificato;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public SnapshotPaziente getBeforeState() {
        return beforeState;
    }

    public SnapshotPaziente getAfterState() {
        return afterState;
    }

    public boolean isReversibile() {
        return !ripristinato && beforeState != null;
    }

    public void setRipristinato(boolean ripristinato) {
        this.ripristinato = ripristinato;
    }

    public String getAutoreString(){
        return author !=null ? author.getUsername() : "Sistema";
    }

    public record SnapshotPaziente(List<RiskFactor> riskFactorList, String comorbidita, String dettagli,
                                   String patologiePregresse) {
            public SnapshotPaziente(List<RiskFactor> riskFactorList, String comorbidita, String dettagli, String patologiePregresse) {
                this.riskFactorList = riskFactorList != null ? new ArrayList<>(riskFactorList) : new ArrayList<>();
                this.comorbidita = comorbidita;
                this.dettagli = dettagli;
                this.patologiePregresse = patologiePregresse;
            }

            public boolean matchesSnapshot(SnapshotPaziente toMatch) {
                if (toMatch == null) return false;
                if (this.equals(toMatch)) return true;
                boolean sameRiskFactors = riskFactorList().size() == toMatch.riskFactorList().size() && new HashSet<>(riskFactorList()).containsAll(toMatch.riskFactorList());
                return sameRiskFactors
                        && Objects.equals(comorbidita(), toMatch.comorbidita())
                        && Objects.equals(dettagli(), toMatch.dettagli())
                        && Objects.equals(patologiePregresse(), toMatch.patologiePregresse());
            }
        }

}
