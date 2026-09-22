package application.classiGeneriche;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Random;

public class GeneratoreDatiTest {

    private static final Random random = new Random();
    public static void generaRilevazioniTest(Paziente paziente, int numeroGiorni) {

        Database db = Database.getInstance();
        LocalDate oggi = LocalDate.now();

        for (int i = 0; i < numeroGiorni; i++) {
            LocalDate giorno = oggi.minusDays(i);

            aggiungiRilevazione(db, paziente, giorno, MomentoRilevazione.PRIMA_COLAZIONE,
                    LocalTime.of(7, 30), LocalTime.of(7, 0));
            aggiungiRilevazione(db, paziente, giorno, MomentoRilevazione.DOPO_COLAZIONE,
                    LocalTime.of(7, 30), LocalTime.of(9, 30));

            aggiungiRilevazione(db, paziente, giorno, MomentoRilevazione.PRIMA_PRANZO,
                    LocalTime.of(12, 30), LocalTime.of(12, 0));
            aggiungiRilevazione(db, paziente, giorno, MomentoRilevazione.DOPO_PRANZO,
                    LocalTime.of(12, 30), LocalTime.of(14, 30));

            aggiungiRilevazione(db, paziente, giorno, MomentoRilevazione.PRIMA_CENA,
                    LocalTime.of(19, 30), LocalTime.of(19, 0));
            aggiungiRilevazione(db, paziente, giorno, MomentoRilevazione.DOPO_CENA,
                    LocalTime.of(19, 30), LocalTime.of(21, 30));
        }

        System.out.println("Generate " + (numeroGiorni * 6) + " rilevazioni di test ("
                + numeroGiorni + " giorni) per " + paziente.getNome() + " " + paziente.getCognome());
    }

    private static void aggiungiRilevazione(Database db, Paziente paziente, LocalDate giorno,
                                            MomentoRilevazione momento,
                                            LocalTime orarioPasto, LocalTime orarioRilevazione) {

        int valore = generaValoreRealistico(momento);

        Rilevazione r = new Rilevazione(giorno, valore, orarioPasto, orarioRilevazione, momento, paziente);

        db.addRilevazione(r);
    }

    private static int generaValoreRealistico(MomentoRilevazione momento) {

        boolean primaDelPasto = momento == MomentoRilevazione.PRIMA_COLAZIONE
                || momento == MomentoRilevazione.PRIMA_PRANZO
                || momento == MomentoRilevazione.PRIMA_CENA;

        // valore anomalo occasionale, utile per testare gli alert
        if (random.nextInt(10) == 0) {
            return primaDelPasto
                    ? 55 + random.nextInt(15)     // ipoglicemia (<70)
                    : 260 + random.nextInt(50);   // iperglicemia grave (>=250)
        }

        int base = primaDelPasto ? 100 : 150;      // valore centrale nella norma
        int variazione = random.nextInt(41) - 20;  // +/- 20

        return base + variazione;
    }

    public static void generaDatiTest(){
        Paziente luca = new Paziente();
        Diabetologo mario = new Diabetologo();
        Database.getInstance().addDiabetologo(mario);
        Database.getInstance().addPaziente(luca);
        Database.getInstance().addResponsabile(new Responsabile());
        ArrayList<Paziente> pazientiTerapia = new ArrayList<>();
        pazientiTerapia.add(luca);
        Terapia t1 = new Terapia("farmaco1", 12, 3, mario, new ArrayList<>(pazientiTerapia), "prima dei pasti");
        Terapia t2 = new Terapia("farmaco2", 10, 2, mario, new ArrayList<>(pazientiTerapia), "dopo i pasti");
        Database.getInstance().addTerapia(t1);
        Database.getInstance().addTerapia(t2);
        generaRilevazioniTest(luca, 40);
    }

    public static void main(String[] args){
        generaDatiTest();
    }
}