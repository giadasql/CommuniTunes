package it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.User;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.LayoutManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class HomepageExample implements UIController {
    @FXML
    public Text message;

    @Override
    public void init(LayoutManager manager) {
        if(manager.authenticated != null){
            message.setText("Welcome " + manager.authenticated.Username + "!");
            message.setFill(Color.BLACK);
        }
    }
}
