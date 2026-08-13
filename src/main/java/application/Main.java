package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/application/view/Login.fxml")
        );

        Parent root = loader.load();

        Scene scene = new Scene(root);

        primaryStage.setTitle("Gestione Diabete");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        
        primaryStage.setWidth(1200);
        primaryStage.setHeight(750);
        
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}