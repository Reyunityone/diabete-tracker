package application.classiGeneriche;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public final class GestoreLog {

    private GestoreLog(){};

    public void registraModificaPaziente(Paziente pazienteModificato, LogOperazione.SnapshotPaziente bs, LogOperazione.SnapshotPaziente as){
        if(bs.matchesSnapshot(as)) return;
        if(pazienteModificato == null || bs == null || as == null) return;

        User author = Session.getInstance().getCurrentUser();
        String descrizione = generaDescrizione(pazienteModificato, bs, as);
        LogOperazione log = new LogOperazione(author, pazienteModificato, descrizione, bs, as);

        Database.getInstance().addLog(log);

    }

    public String generaDescrizione(Paziente pazienteModificato, LogOperazione.SnapshotPaziente bs, LogOperazione.SnapshotPaziente as){
        List<String> campiModificati = new ArrayList<>();
        if(!bs.comorbidita().equals(as.comorbidita())) campiModificati.add("comorbidita");
        if(!bs.dettagli().equals(as.dettagli())) campiModificati.add("dettagli");
        if(!(bs.riskFactorList().size() == as.riskFactorList().size() && new HashSet<>(bs.riskFactorList()).containsAll(as.riskFactorList()))) campiModificati.add("fattori di rischio");
        return "AGGIORNAMENTO" + pazienteModificato.getNome() + " " + pazienteModificato.getCognome() + " CAMPI : [" + String.join(",", campiModificati) + "]";
    }

    public boolean undoOperation(LogOperazione log){
        if(log == null || log.isReversibile()) return false;

        Paziente p = log.getPazienteModificato();
        p.setComorbidita(log.getBeforeState().comorbidita());
        p.setDettagli(log.getBeforeState().dettagli());
        p.setFattoriDiRischio(log.getBeforeState().riskFactorList());
        p.setPatologiePregresse(log.getBeforeState().patologiePregresse());

        log.setRipristinato(true);
        this.registraModificaPaziente(p, log.getAfterState(), log.getBeforeState());
        Database.getInstance().updatePaziente(p, p);

        return true;
    }
}
