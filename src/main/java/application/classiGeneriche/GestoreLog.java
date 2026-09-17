package application.classiGeneriche;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public final class GestoreLog {

    private GestoreLog(){};

    public static void registraModificaPaziente(Paziente pazienteModificato, LogOperazione.SnapshotPaziente bs, LogOperazione.SnapshotPaziente as, boolean isUndo, boolean isPreviousLogUndo){
        if(pazienteModificato == null || bs == null || as == null) return;
        if(bs.matchesSnapshot(as)) return;
        User author = Session.getInstance().getCurrentUser();
        String descrizione = generaDescrizione(pazienteModificato, bs, as, isUndo, isPreviousLogUndo);
        boolean isRedo = isPreviousLogUndo && isUndo;
        LogOperazione log = new LogOperazione(author, pazienteModificato, descrizione, bs, as, !isRedo && isUndo);

        Database.getInstance().addLog(log);

    }

    private static String generaDescrizione(Paziente pazienteModificato, LogOperazione.SnapshotPaziente bs, LogOperazione.SnapshotPaziente as, boolean isUndo, boolean isPreviousLogUndo){
        boolean isRedo = isPreviousLogUndo && isUndo;
        List<String> campiModificati = new ArrayList<>();
        if(!bs.comorbidita().equals(as.comorbidita())) campiModificati.add("comorbidita");
        if(!bs.dettagli().equals(as.dettagli())) campiModificati.add("dettagli");
        if(!bs.patologiePregresse().equals(as.patologiePregresse())) campiModificati.add("patologie pregresse");
        if(!(bs.riskFactorList().size() == as.riskFactorList().size() && new HashSet<>(bs.riskFactorList()).containsAll(as.riskFactorList()))) campiModificati.add("fattori di rischio");
        return (isRedo ? "REDO -> " : isUndo ? "UNDO -> " : "") +  "AGGIORNAMENTO " + pazienteModificato.getNome() + " " + pazienteModificato.getCognome() + " CAMPI : [" + String.join(",", campiModificati) + "]";
    }

    public static boolean undoOperation(LogOperazione log){
        if(log == null || !log.isReversibile()) return false;

        Paziente p = log.getPazienteModificato();
        p.setComorbidita(log.getBeforeState().comorbidita());
        p.setDettagli(log.getBeforeState().dettagli());
        p.setFattoriDiRischio(log.getBeforeState().riskFactorList());
        p.setPatologiePregresse(log.getBeforeState().patologiePregresse());

        log.setRipristinato(true);
        registraModificaPaziente(p, log.getAfterState(), log.getBeforeState(), true, log.isUndoLog());
        Database.getInstance().updatePaziente(p, p);

        return true;
    }
}
