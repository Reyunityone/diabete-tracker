package application.classiGeneriche;

import java.io.*;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.Objects;

public class Database {
    private static Database database;
    private final static String DEFAULT_FILE_NAME = "database.data";
    private final String fileName;
    private ArrayList<Diabetologo> diabetologi;
    private ArrayList<Paziente> pazienti;
    private ArrayList<Terapia> terapie;
    private ArrayList<AssunzioneFarmaco> assunzioni;
    private ArrayList<Rilevazione> rilevazioni;
    private ArrayList<Segnalazione> segnalazioni;
    private ArrayList<Responsabile> responsabili;
    private ArrayList<Messaggio> messaggi;

    Database(String fileName){
        this.fileName = fileName;
        load();
    }

    private Database(){
        this(DEFAULT_FILE_NAME);
    }

    public static synchronized Database getInstance(){
        if(database == null) database = new Database();
        return database;
    }

    @SuppressWarnings("unchecked")
    public void load(){
        try (FileInputStream file = new FileInputStream(fileName); ObjectInputStream ois = new ObjectInputStream(file)){
            this.diabetologi = (ArrayList<Diabetologo>) ois.readObject();
            this.pazienti = (ArrayList<Paziente>) ois.readObject();
            this.terapie = (ArrayList<Terapia>) ois.readObject();
            this.assunzioni = (ArrayList<AssunzioneFarmaco>) ois.readObject();
            this.rilevazioni = (ArrayList<Rilevazione>) ois.readObject();
            this.segnalazioni = (ArrayList<Segnalazione>) ois.readObject();
            this.responsabili = (ArrayList<Responsabile>) ois.readObject();
            this.messaggi = (ArrayList<Messaggio>) ois.readObject();
            System.out.println("LETTURA COMPLETATA");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("ERRORE NELLA LETTURA DB");
            this.diabetologi = new ArrayList<>();
            this.pazienti = new ArrayList<>();
            this.terapie = new ArrayList<>();
            this.assunzioni = new ArrayList<>();
            this.rilevazioni = new ArrayList<>();
            this.segnalazioni = new ArrayList<>();
            this.responsabili = new ArrayList<>();
            this.messaggi = new ArrayList<>();
        }

    }

    public void save(){
        try(FileOutputStream file = new FileOutputStream(fileName); ObjectOutputStream oos = new ObjectOutputStream(file)){
            oos.writeObject(this.diabetologi);
            oos.writeObject(this.pazienti);
            oos.writeObject(this.terapie);
            oos.writeObject(this.assunzioni);
            oos.writeObject(this.rilevazioni);
            oos.writeObject(this.segnalazioni);
            oos.writeObject(this.responsabili);
            oos.writeObject(this.messaggi);
            System.out.println("SCRITTURA COMPLETATA");
        } catch(IOException e){
            System.err.println("ERRORE NELLA SCRITTURA DEL DATABASE");
        }
    }


    public ArrayList<Diabetologo> getDiabetologi() {
        return new ArrayList<>(diabetologi);
    }

    public ArrayList<Paziente> getPazienti() {
        return new ArrayList<>(pazienti);
    }

    public ArrayList<Terapia> getTerapie() {
        return new ArrayList<>(terapie);
    }

    public ArrayList<AssunzioneFarmaco> getAssunzioni() {
        return new ArrayList<>(assunzioni);
    }

    public ArrayList<Rilevazione> getRilevazioni() {
        return new ArrayList<>(rilevazioni);
    }

    public ArrayList<Segnalazione> getSegnalazioni() {
        return new ArrayList<>(segnalazioni);
    }

    public ArrayList<Responsabile> getResponsabili(){ return new ArrayList<>(responsabili);}

    public ArrayList<Messaggio> getAllMessaggi(){
        return new ArrayList<>(messaggi);
    }

    public void addDiabetologo(Diabetologo d){
        if (!diabetologi.contains(d)) {
            this.diabetologi.add(d);
            save();
        }
    }

    public void addPaziente(Paziente p){
        if (!pazienti.contains(p)) {
            this.pazienti.add(p);
            save();
        }
    }
    public void addResponsabile(Responsabile responsabile) {
        if(!responsabili.contains(responsabile)){
            this.responsabili.add(responsabile);
            save();
        }
    }

    public void addTerapia(Terapia t){
        if (!terapie.contains(t)) {
            this.terapie.add(t);
            save();
        }
    }

    public void addAssunzione(AssunzioneFarmaco a){
        this.assunzioni.add(a);
        save();
    }

    public void addRilevazione(Rilevazione r){
        this.rilevazioni.add(r);
        save();
    }

    public void addSegnalazione(Segnalazione s){
        this.segnalazioni.add(s);
        save();
    }

    public ArrayList<Paziente> getPazientiFromMedico(Diabetologo medico){
        return pazienti.stream().filter(p -> p.getMedicoDiRiferimento().equals(medico)).collect(Collectors.toCollection(ArrayList::new));
    public void addMessaggio(Messaggio m){
        this.messaggi.add(m);
        save();
    }

    public ArrayList<Terapia> getTerapieByPaziente(Paziente p){
        ArrayList<Terapia> result = new ArrayList<>();
        for(Terapia t: terapie){
            if(t.getPazienti().contains(p)) result.add(t);
        }
        return result;
    }

    public ArrayList<AssunzioneFarmaco> getAssunzioniByPaziente(Paziente p){
        ArrayList<AssunzioneFarmaco> result = new ArrayList<>();
        for (AssunzioneFarmaco a : assunzioni) {
            if (a.getPaziente().equals(p)) result.add(a);
        }
        return result;
    }

    public ArrayList<Rilevazione> getRilevazioniByPaziente(Paziente p){
        ArrayList<Rilevazione> result = new ArrayList<>();
        for (Rilevazione r : rilevazioni) {
            if (r.getPaziente().equals(p)) result.add(r);
        }
        return result;
    }

    public ArrayList<Segnalazione> getSegnalazioniByPaziente(Paziente p){
        ArrayList<Segnalazione> result = new ArrayList<>();
        for (Segnalazione s : segnalazioni) {
            if (s.getPaziente().equals(p)) result.add(s);
        }
        return result;
    }

    public ArrayList<Paziente> getPazientiByMedico(Diabetologo d){
        ArrayList<Paziente> result = new ArrayList<>();
        for(Paziente p : pazienti){
            if(p.getMedicoDiRiferimento().equals(d)) result.add(p);
        }
        return result;
    }

    public ArrayList<Messaggio> getMessaggiFromMedico(Diabetologo d){
        ArrayList<Messaggio> result = new ArrayList<>();
        for(Messaggio m: messaggi){
            if(m.getDiabetologo() != null){
                if(m.getDiabetologo().equals(d) && (m.getTipo() == TipoAlert.PAZIENTE_MEDICO || m.getTipo() == TipoAlert.SISTEMA_MEDICO)) result.add(m);
            }
        }
        return result;
    }

    public ArrayList<Messaggio> getMessaggiFromPaziente(Paziente p){
        ArrayList<Messaggio> result = new ArrayList<>();
        for(Messaggio m: messaggi){
            if(m.getPaziente() != null){
                if(m.getPaziente().equals(p) && (m.getTipo() == TipoAlert.MEDICO_PAZIENTE || m.getTipo() == TipoAlert.SISTEMA_PAZIENTE)) result.add(m);
            }

        }
        return result;
    }

    public void setMessaggioLetto(Messaggio m){
        if(!this.messaggi.contains(m)) return;
        int i = this.messaggi.indexOf(m);
        m.setLetto(true);
        this.messaggi.set(i, m);
        save();
    }

    public User login(String username, String password){
        for(Diabetologo d : diabetologi){
            if(d.getUsername().equals(username) && d.getPassword().equals(password)) return d;
        }
        for(Paziente p : pazienti){
            if(p.getUsername().equals(username) && p.getPassword().equals(password)) return p;
        }
        for(Responsabile r: responsabili){
            if(r.getUsername().equals(username) && r.getPassword().equals(password)) return r;
        }
        return null;
    }

    public void updateAssunzione(AssunzioneFarmaco vecchio, AssunzioneFarmaco nuovo){
        int i = assunzioni.indexOf(vecchio);
        if(i != -1){
            assunzioni.set(i, nuovo);
            save();
        }
    }

    public void updateSegnalazione(Segnalazione vecchio, Segnalazione nuovo){
        int i = segnalazioni.indexOf(vecchio);
        if(i!=-1){
            segnalazioni.set(i, nuovo);
            save();
        }
    }

    public void updateRilevazione(Rilevazione vecchio, Rilevazione nuovo){
        int i = rilevazioni.indexOf(vecchio);
        if(i!=-1){
            rilevazioni.set(i, nuovo);
            save();
        }
    }
    
    public void updateDiabetologo(Diabetologo vecchio, Diabetologo nuovo) {
        int i = diabetologi.indexOf(vecchio);

        if (i != -1) {
            diabetologi.set(i, nuovo);
            save();
        }
    }
    
    public void updatePaziente(Paziente vecchio, Paziente nuovo) {
        int i = pazienti.indexOf(vecchio);

        if (i != -1) {
            pazienti.set(i, nuovo);
            save();
        }
    }
    
    public void updateDiabetologoPazienti(Diabetologo nuovoDiabetologo,  ArrayList<Paziente> pazientiSelezionati) {
    	for(Paziente p: pazientiSelezionati) {
    		if(pazienti.contains(p)) {
    			p.setMedicoDiRiferimento(nuovoDiabetologo);
    		}
    	}
    	
    	save();
    }
    
    public void updatePazienteDiabetologo(Paziente p, Diabetologo d) {
    	if(pazienti.contains(p) && diabetologi.contains(d)) p.setMedicoDiRiferimento(d);
    	save();
    }
    
    public boolean usernameEsistente(String username) {
        for (Paziente p : pazienti) {
            if (Objects.equals(p.getUsername(), username)) {
                return true;
            }
        }

        for (Diabetologo d : diabetologi) {
            if (Objects.equals(d.getUsername(), username)) {
                return true;
            }
        }

        return false;
    }
    public void assegnaTerapia(
            Terapia terapia,
            Paziente paziente) {

        int indice =
                terapie.indexOf(terapia);

        if (indice != -1) {

            Terapia terapiaEsistente =
                    terapie.get(indice);

            if (!terapiaEsistente
                    .getPazienti()
                    .contains(paziente)) {

                terapiaEsistente
                        .getPazienti()
                        .add(paziente);

                save();
            }

        } else {

            terapia.getPazienti()
                    .add(paziente);

            terapie.add(terapia);

            save();
        }
    }

    public void removeTerapia(Terapia t) {
        if (terapie.remove(t)) {
            save();
        }
    }


	public void deleteDiabetologo(Diabetologo diabetologoDeleted) {
		for(Diabetologo d: diabetologi) {
			if(d.equals(diabetologoDeleted)) {
				diabetologi.remove(d);
				break;
			}
		}
		
		save();
	}

	public void deletePaziente(Paziente pazienteDeleted) {
		
		for(Paziente p:pazienti) {
			if(p.equals(pazienteDeleted)) {
				pazienti.remove(p);
				break;
			}
		}
		
		save();
	}
}