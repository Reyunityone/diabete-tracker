package application.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField nomeField;

    @FXML
    private TextField cognomeField;

    @FXML
    private PasswordField credenzialiField;

    @FXML
    private ComboBox<String> ruoloComboBox;


    @FXML
    public void initialize() {

        ruoloComboBox.getItems().addAll(
                "Responsabile",
                "Diabetologo",
                "Paziente"
        );
    }


    @FXML
    private void handleLogin() {

        String nome = nomeField.getText();
        String cognome = cognomeField.getText();

        String ruolo = ruoloComboBox.getValue();


        if (ruolo == null) {

            System.out.println("Seleziona un ruolo.");

            return;
        }


        switch (ruolo) {

            case "Responsabile":

                cambiaSchermataResponsabile(
                        nome,
                        cognome
                );

                break;


            case "Diabetologo":

                cambiaSchermataDiabetologo(
                        nome,
                        cognome
                );

                break;


            case "Paziente":

            	cambiaSchermataPaziente(
                        nome,
                        cognome
                );

                break;
        }
    }


    /**
     * Apre la schermata del Responsabile
     * passando nome e cognome inseriti nel Login.
     */
    private void cambiaSchermataResponsabile(
            String nome,
            String cognome) {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/application/view/Responsabile.fxml"
                    )
            );

            Parent root = loader.load();


            // Recuperiamo il controller
            ResponsabileController controller =
                    loader.getController();


            // Passiamo nome e cognome
            controller.impostaProfilo(
                    nome,
                    cognome
            );


            Stage stage =
                    (Stage) ruoloComboBox
                            .getScene()
                            .getWindow();


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
                (Stage) ruoloComboBox
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
    private void cambiaSchermataPaziente(
            String nome,
            String cognome) {

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


            // Passiamo nome e cognome
            controller.inizializzaProfilo(
                    nome,
                    cognome
            );


            Stage stage =
                    (Stage) ruoloComboBox
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