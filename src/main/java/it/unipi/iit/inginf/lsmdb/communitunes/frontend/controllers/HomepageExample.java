package it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers;

import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManager;
import javafx.fxml.FXML;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class HomepageExample implements UIController {
    @FXML
    public Text message;

    @Override
    public void init(LayoutManager manager) {
        if(manager.context.getAuthenticatedUser() != null){
            message.setText("Welcome " + manager.context.getAuthenticatedUser().Username + "!");
            message.setFill(Color.BLACK);
        }
    }
}
