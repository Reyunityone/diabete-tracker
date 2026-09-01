package application.classiGeneriche;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class GestoreAlert {

    // =========================================================
    // COSTANTI DI CONFIGURAZIONE
    // =========================================================
    private static final int GIORNI_CONSECUTIVI_ALERT_MEDICO = 3;

    private static final int SOGLIA_IPOGLICEMIA_CRITICA = 70;

    private static final int PRE_PASTO_MIN = 80;
    private static final int PRE_PASTO_MAX = 130;

    private static final int POST_PASTO_MAX = 180;

    private static final int SOGLIA_IPERGLICEMIA_GRAVE = 250;

    private GestoreAlert() {
    }


    // =========================================================
    // PROMEMORIA ASSUNZIONI GIORNALIERE (VERSO IL PAZIENTE)
    // =========================================================
    public static void verificaAssunzioniGiornaliere(Paziente paziente) {

        Database db = Database.getInstance();

        List<Terapia> terapie = db.getTerapieByPaziente(paziente);
        List<AssunzioneFarmaco> assunzioniOggi =
                filtraPerData(db.getAssunzioniByPaziente(paziente), LocalDate.now());

        for (Terapia terapia : terapie) {

            long assunte = assunzioniOggi.stream()
                    .filter(a -> a.getTerapia().equals(terapia))
                    .count();

            if (assunte < terapia.getNumeroAssunzioniGiornaliere()) {
                inviaPromemoriaPaziente(paziente, terapia, assunte);
            }
        }
    }

    private static void inviaPromemoriaPaziente(Paziente paziente, Terapia terapia, long assunte) {

        String testo = "Ricorda di completare le assunzioni di " + terapia.getFarmaco().toUpperCase()
                + ": registrate " + assunte + " su " + terapia.getNumeroAssunzioniGiornaliere()
                + " previste per oggi.";

        boolean giaInviatoOggi = Database.getInstance().getMessaggiFromPaziente(paziente).stream()
                .anyMatch(m -> m.getTipo() == TipoAlert.SISTEMA_PAZIENTE
                        && m.getTesto().equals(testo));

        if (giaInviatoOggi) return;

        Messaggio promemoria = new Messaggio(
                paziente, null, testo, TipoAlert.SISTEMA_PAZIENTE, UrgenzaAlert.LOW
        );

        Database.getInstance().addMessaggio(promemoria);
    }


    // =========================================================
    // MANCATA ADERENZA PROLUNGATA (VERSO IL MEDICO)
    // =========================================================

    public static void verificaAderenzaTerapia(Paziente paziente, Terapia terapia) {
        int giorniMancati = 0;
        LocalDate giorno = LocalDate.now().minusDays(1);
        ArrayList<AssunzioneFarmaco> assunzioni = Database.getInstance().getAssunzioniByPaziente(paziente);
        for(int i = 0; i < GIORNI_CONSECUTIVI_ALERT_MEDICO; i++){
            long assunzioniRelativeATerapia = assunzioni.stream().filter( assunzione ->  assunzione.getTerapia().equals(terapia) && assunzione.getData().isEqual(giorno)).count();
            if(assunzioniRelativeATerapia != terapia.getNumeroAssunzioniGiornaliere()) giorniMancati++;
            giorno.minusDays(1);
        }

        if(giorniMancati >= GIORNI_CONSECUTIVI_ALERT_MEDICO){
            inviaAlertMedicoAderenza(paziente, terapia);
        }
    }

    private static void inviaAlertMedicoAderenza(Paziente paziente, Terapia terapia) {

        Diabetologo medico = paziente.getMedicoDiRiferimento();
        if (medico == null) return;

        String testo = "Il paziente " + paziente.getNome() + " " + paziente.getCognome()
                + " non segue correttamente la terapia:"+ terapia.toString() +" da almeno "
                + GIORNI_CONSECUTIVI_ALERT_MEDICO + " giorni consecutivi.";

        boolean giaSegnalato = Database.getInstance().getMessaggiFromMedico(medico).stream()
                .anyMatch(m -> m.getTipo() == TipoAlert.SISTEMA_MEDICO
                        && m.getPaziente() != null
                        && m.getPaziente().equals(paziente)
                        && m.getTesto().equals(testo));

        if (giaSegnalato) return;

        Messaggio alert = new Messaggio(
                paziente, medico, testo, TipoAlert.SISTEMA_MEDICO, UrgenzaAlert.MEDIUM
        );

        Database.getInstance().addMessaggio(alert);
    }


    // =========================================================
    // GLICEMIA FUORI SOGLIA (VERSO IL MEDICO)
    // =========================================================

    public static void verificaGlicemia(Rilevazione rilevazione) {

        Paziente paziente = rilevazione.getPaziente();
        Diabetologo medico = paziente.getMedicoDiRiferimento();
        if (medico == null) return;

        UrgenzaAlert urgenza = calcolaUrgenza(
                rilevazione.getLivelloGlicemia(),
                rilevazione.getMomentoRilevazione()
        );
        if (urgenza == null) return; // valore nella norma: nessun alert

        String testo = "Glicemia fuori soglia per il paziente " + paziente.getNome() + " "
                + paziente.getCognome() + ": " + rilevazione.getLivelloGlicemia() + " mg/dL ("
                + rilevazione.getMomentoRilevazione() + ") registrata il "
                + rilevazione.getData() + ".";

        Messaggio alert = new Messaggio(
                paziente, medico, testo, TipoAlert.SISTEMA_MEDICO, urgenza
        );

        Database.getInstance().addMessaggio(alert);
    }

    private static UrgenzaAlert calcolaUrgenza(int livelloGlicemia, MomentoRilevazione momento) {

        // Ipoglicemia critica: vale sempre, a prescindere dal momento
        if (livelloGlicemia < SOGLIA_IPOGLICEMIA_CRITICA) {
            return UrgenzaAlert.HIGH;
        }

        if (isPrimaDelPasto(momento)) {

            // Leggermente sotto il range minimo, ma non ancora critico
            if (livelloGlicemia < PRE_PASTO_MIN) {
                return UrgenzaAlert.LOW;
            }

            if (livelloGlicemia <= PRE_PASTO_MAX) {
                return null; // valore nella norma
            }

            return livelloGlicemia >= SOGLIA_IPERGLICEMIA_GRAVE
                    ? UrgenzaAlert.HIGH
                    : UrgenzaAlert.MEDIUM;

        } else {

            if (livelloGlicemia <= POST_PASTO_MAX) {
                return null; // valore nella norma
            }

            return livelloGlicemia >= SOGLIA_IPERGLICEMIA_GRAVE
                    ? UrgenzaAlert.HIGH
                    : UrgenzaAlert.MEDIUM;
        }
    }

    private static boolean isPrimaDelPasto(MomentoRilevazione momento) {
        return momento == MomentoRilevazione.PRIMA_COLAZIONE
                || momento == MomentoRilevazione.PRIMA_PRANZO
                || momento == MomentoRilevazione.PRIMA_CENA;
    }


    // =========================================================
    // CONTROLLO MASSIVO (es. da eseguire al login del medico/responsabile)
    // =========================================================

    public static void verificaTuttiIPazienti(Diabetologo d) {
        for (Paziente paziente : Database.getInstance().getPazientiByMedico(d)) {
            verificaAssunzioniGiornaliere(paziente);
            ArrayList<Terapia> terapie = Database.getInstance().getTerapieByPaziente(paziente);
            if(terapie.isEmpty()) continue;
            for(Terapia t: terapie){
                verificaAderenzaTerapia(paziente, t);
            }
        }
    }


    // =========================================================
    // METODI DI SUPPORTO
    // =========================================================

    private static ArrayList<AssunzioneFarmaco> filtraPerData(
            List<AssunzioneFarmaco> lista, LocalDate data) {

        ArrayList<AssunzioneFarmaco> risultato = new ArrayList<>();

        for (AssunzioneFarmaco a : lista) {
            if (a.getData().equals(data)) {
                risultato.add(a);
            }
        }

        return risultato;
    }
}