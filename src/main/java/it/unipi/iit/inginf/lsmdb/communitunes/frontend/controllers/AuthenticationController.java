package it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers;


import it.unipi.iit.inginf.lsmdb.communitunes.authentication.AuthResult;
import it.unipi.iit.inginf.lsmdb.communitunes.authentication.AuthenticationFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.authentication.AuthenticationManager;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.User;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.LayoutManager;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.Persistence;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.PersistenceFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class AuthenticationController implements UIController {
    @FXML
    private TextField username;

    @FXML
    private TextField password;

    private Stage primary;

    private LayoutManager manager;

    @FXML
    public void loginEventHandler(ActionEvent event) throws IOException {
        AuthenticationManager authManager = AuthenticationFactory.CreateAuthenticationManager();
        Persistence dbManager = PersistenceFactory.CreatePersistence();
        AuthResult result = authManager.Login(username.getText(), password.getText());
        if(result.Success){
            manager.authenticated = dbManager.getUser(result.AuthenticatedUser);
            manager.setContent(manager.HOMEPAGE_EXAMPLE);
        }
    }



    @Override
    public void init(LayoutManager manager) {
        this.manager = manager;
    }
}
