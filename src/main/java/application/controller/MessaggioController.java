package application.controller;

import application.classiGeneriche.Database;
import application.classiGeneriche.Messaggio;
import application.classiGeneriche.UrgenzaAlert;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class MessaggioController {

	//OGGETTI FXML
    @FXML private Label nomeLabel;
    @FXML private Label testoLabel;
    @FXML private Label urgenzaLabel;

    // =========================================================
    // INIZIALIZZAZIONE
    // =========================================================

    public void inizializza(Messaggio messaggio,Runnable messaggioLetto) {
        String nomeCompleto = messaggio.getMittenteString();
        nomeLabel.setText(nomeCompleto);
        testoLabel.setText(messaggio.getTesto());
        
        if (messaggio.getUrgenza() != null) {
        	String testoUrgenza;
        	
        	if(messaggio.getUrgenza().equals(UrgenzaAlert.LOW)) {
        		testoUrgenza="BASSA";
        	}else if(messaggio.getUrgenza().equals(UrgenzaAlert.MEDIUM)) {
        		testoUrgenza="MEDIA";
        	}else {
        		testoUrgenza="ALTA";
        	}
            urgenzaLabel.setText("[EMERGENZA "+testoUrgenza+"]");
            
            
            urgenzaLabel.getStyleClass().add("message-urgency");
            urgenzaLabel.getStyleClass().add("urgency-" + messaggio.getUrgenza().name().toLowerCase());
        } else {
            urgenzaLabel.setVisible(false);
            urgenzaLabel.setManaged(false);
        }

        // IL MESSAGGIO VIENE CONSIDERATO LETTO
        if (!messaggio.isLetto()) {
            Database.getInstance().setMessaggioLetto(messaggio);
            messaggioLetto.run();
        }
    }


    // =========================================================
    // CHIUDI
    // =========================================================

    @FXML
    private void chiudi() {
        Stage stage =(Stage) nomeLabel.getScene().getWindow();
        stage.close();
    }
}