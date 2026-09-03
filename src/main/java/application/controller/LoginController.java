package application.controller;

import application.classiGeneriche.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;


    @FXML
    public void initialize() {
        Paziente luca = new Paziente();
        Diabetologo mario = new Diabetologo();
        Database.getInstance().addDiabetologo(mario);
        Database.getInstance().addPaziente(luca);
        Database.getInstance().addResponsabile(new Responsabile());
        ArrayList<Paziente> pazientiTerapia = new ArrayList<>();
        pazientiTerapia.add(luca);
        Terapia t1 = new Terapia("dolipran", 12, 3, mario, new ArrayList<>(pazientiTerapia), "prima dei pasti");
        Terapia t2 = new Terapia("tachi", 10, 2, mario, new ArrayList<>(pazientiTerapia), "dopo i pasti");
        Database.getInstance().addTerapia(t1);
        Database.getInstance().addTerapia(t2);
    }


    @FXML
    private void handleLogin() {
        Database db = Database.getInstance();
        String username = usernameField.getText();
        String password = passwordField.getText();

        User loggedUser = db.login(username, password);
        if(loggedUser != null){
            Session.getInstance().setCurrentUser(loggedUser);
            switch(loggedUser){
                case Paziente p -> cambiaSchermataPaziente();
                case Diabetologo d -> cambiaSchermataDiabetologo();
                case Responsabile r -> cambiaSchermataResponsabile(r);
            }
        }
        else{
            System.out.println("CREDENZIALI NON VALIDE");
        }


    }


    /**
     * Apre la schermata del Responsabile
     * passando nome e cognome inseriti nel Login.
     */
    private void cambiaSchermataResponsabile(Responsabile responsabile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/Responsabile.fxml"));

            Parent root = loader.load();

            ResponsabileController controller =loader.getController();

            controller.inizializzaProfilo(responsabile);

            Stage stage =(Stage)usernameField.getScene().getWindow();

            Scene scene = new Scene(root);

            stage.setScene(scene);

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Apre la schermata del Diabetologo
     * passando nome e cognome inseriti nel Login.
     */
    private void cambiaSchermataDiabetologo() {
    	try {
    	FXMLLoader loader =
                new FXMLLoader(
                        getClass().getResource(
                                "/application/view/Diabetologo.fxml"
                        )
                );

        Parent root;
			root = loader.load();


        DiabetologoController controller =
                loader.getController();


        controller.inizializzaProfilo();


        Stage stage =
                (Stage) usernameField
                        .getScene()
                        .getWindow();


        stage.setScene(
                new Scene(
                        root
                )
        );


        stage.show();
        
    	} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * Apre la schermata del Paziente
     * passando nome e cognome inseriti nel Login.
     */
    private void cambiaSchermataPaziente() {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/application/view/Paziente.fxml"
                            )
                    );

            Parent root =
                    loader.load();


            // Recuperiamo il controller
            PazienteController controller =
                    loader.getController();


            // Passiamo il profilo
            controller.inizializzaProfilo(
            );


            Stage stage =
                    (Stage) usernameField
                            .getScene()
                            .getWindow();


            stage.setScene(
                    new Scene(root)
            );


            stage.show();


        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}