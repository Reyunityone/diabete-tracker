package application.classiGeneriche;

import java.io.*;
import java.util.ArrayList;

public class Database{
    private static Database database;
    private final String fileName = "database.data";
    private ArrayList<Diabetologo> diabetologi;
    private ArrayList<Paziente> pazienti;
    private ArrayList<Terapia> terapie;


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
            System.out.println("LETTURA COMPLETATA" + pazienti.toString() +" " + terapie.toString() + " " +  diabetologi.toString());
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("ERRORE NELLA LETTURA DB");
            this.diabetologi = new ArrayList<>();
            this.pazienti = new ArrayList<>();
            this.terapie = new ArrayList<>();
        }

    }

    public void save(){
        try(FileOutputStream file = new FileOutputStream(fileName); ObjectOutputStream oos = new ObjectOutputStream(file)){
            oos.writeObject(this.diabetologi);
            oos.writeObject(this.pazienti);
            oos.writeObject(this.terapie);
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

    public void addDiabetologo(Diabetologo d){
        this.diabetologi.add(d);
        save();
    }

    public void addPaziente(Paziente p){
        this.pazienti.add(p);
        save();
    }

    public void addTerapia(Terapia t){
        this.terapie.add(t);
        save();
    }

    public User login(String username, String password){
        for(Diabetologo d : diabetologi){
            if(d.getUsername().equals(username) && d.getPassword().equals(password)) return d;
        }
        for(Paziente p : pazienti){
            if(p.getUsername().equals(username) && p.getPassword().equals(password)) return p;
        }
        return null;
    }
}
