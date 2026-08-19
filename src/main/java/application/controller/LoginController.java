package application.controller;

import application.classiGeneriche.Diabetologo;
import application.classiGeneriche.Paziente;
import application.classiGeneriche.User;
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

    }


    @FXML
    private void handleLogin() {

        String username = usernameField.getText();
        String password = passwordField.getText();

        // TODO Validate user
        User loggedUser = new Paziente();

        if(loggedUser instanceof Paziente)
            cambiaSchermataPaziente(loggedUser);
        if(loggedUser instanceof Diabetologo)
            cambiaSchermataDiabetologo(loggedUser.getNome(), loggedUser.getCognome());
        //if(loggedUser instanceof Responsabile)
        //    cambiaSchermataResponsabile(loggedUser.getNome(), loggedUser.getCognome());

    }


    /**
     * Apre la schermata del Responsabile
     * passando nome e cognome inseriti nel Login.
     */
//    private void cambiaSchermataResponsabile(
//            String nome,
//            String cognome) {
//
//        try {
//
//            FXMLLoader loader = new FXMLLoader(
//                    getClass().getResource(
//                            "/application/view/Responsabile.fxml"
//                    )
//            );
//
//            Parent root = loader.load();
//
//
//            // Recuperiamo il controller
//            ResponsabileController controller =
//                    loader.getController();
//
//
//            // Passiamo nome e cognome
//            controller.impostaProfilo(
//                    nome,
//                    cognome
//            );
//
//
//            Stage stage =
//                    (Stage) ruoloComboBox
//                            .getScene()
//                            .getWindow();
//
//
//            Scene scene = new Scene(root);
//
//            stage.setScene(scene);
//
//
//            stage.show();
//
//        } catch (IOException e) {
//
//            e.printStackTrace();
//        }
//    }
    
    /**
     * Apre la schermata del Diabetologo
     * passando nome e cognome inseriti nel Login.
     */
    private void cambiaSchermataDiabetologo(String nome, String cognome) {
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
                nome,
                cognome
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
    private void cambiaSchermataPaziente(User loggedUser) {

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
                    loggedUser
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