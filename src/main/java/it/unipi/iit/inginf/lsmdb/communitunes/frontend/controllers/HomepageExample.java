package it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.User;
import javafx.fxml.Initializable;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class HomepageExample {

    public Text message;

    private Stage primary;

    public void initialize(User authenticated, Stage primary) {
        message.setText("Welcome " + authenticated.Username + "!");
        message.setFill(Color.BLACK);
        this.primary = primary;
    }
}
