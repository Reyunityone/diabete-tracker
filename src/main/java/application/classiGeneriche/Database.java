package application.classiGeneriche;

import java.io.*;
import java.util.ArrayList;

public class Database {
    private static Database database;
    private final String fileName = "database.data";
    private ArrayList<Diabetologo> diabetologi;
    private ArrayList<Paziente> pazienti;
    private ArrayList<Terapia> terapie;
    private ArrayList<AssunzioneFarmaco> assunzioni;
    private ArrayList<Rilevazione> rilevazioni;
    private ArrayList<Segnalazione> segnalazioni;
    private ArrayList<Responsabile> responsabili;

    private Database(){
        load();
    }

    public static synchronized Database getInstance(){
        if(database == null) database = new Database();
        return database;
    }

    @SuppressWarnings("unchecked")
    public void load(){
        try (FileInputStream file = new FileInputStream(fileName); ObjectInputStream ois = new ObjectInputStream(file);){
            this.diabetologi = (ArrayList<Diabetologo>) ois.readObject();
            this.pazienti = (ArrayList<Paziente>) ois.readObject();
            this.terapie = (ArrayList<Terapia>) ois.readObject();
            this.assunzioni = (ArrayList<AssunzioneFarmaco>) ois.readObject();
            this.rilevazioni = (ArrayList<Rilevazione>) ois.readObject();
            this.segnalazioni = (ArrayList<Segnalazione>) ois.readObject();
            this.responsabili = (ArrayList<Responsabile>) ois.readObject();
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
        if(!pazienti.contains(responsabile)){
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


}