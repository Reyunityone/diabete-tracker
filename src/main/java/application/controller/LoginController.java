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

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;


    @FXML
    public void initialize() {
        Database.getInstance().addDiabetologo(new Diabetologo());
        Database.getInstance().addPaziente(new Paziente());
    }


    @FXML
    private void handleLogin() {
        Database db = Database.getInstance();
        String username = usernameField.getText();
        String password = passwordField.getText();


        User loggedUser = db.login(username, password);
        if(loggedUser != null){
            Session.getInstance().getLoggedUser();
            switch(loggedUser){
                case Paziente p -> cambiaSchermataPaziente(p);
                case Diabetologo d -> cambiaSchermataDiabetologo(d);
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
    private void cambiaSchermataDiabetologo(Diabetologo medico) {
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


        controller.inizializzaProfilo(
                medico
        );


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
    private void cambiaSchermataPaziente(Paziente loggedUser) {

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