package it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers;


import it.unipi.iit.inginf.lsmdb.communitunes.authentication.AuthResult;
import it.unipi.iit.inginf.lsmdb.communitunes.authentication.AuthenticationFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.authentication.AuthenticationManager;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.User;
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

public class AuthenticationController {
    @FXML
    private TextField username;

    @FXML
    private TextField password;

    private Stage primary;

    @FXML
    public void loginEventHandler(ActionEvent event) throws IOException {
        AuthenticationManager authManager = AuthenticationFactory.CreateAuthenticationManager();
        Persistence dbManager = PersistenceFactory.CreatePersistence();
        AuthResult result = authManager.Login(username.getText(), password.getText());
        if(result.Success){
            User authenticated = dbManager.getUser(result.AuthenticatedUser);

            FXMLLoader loader = new FXMLLoader(
                    getClass().getClassLoader().getResource(
                            "homepage_example.fxml"
                    )
            );


            primary.setScene(
                    new Scene(loader.load(), 900, 600)
            );

            HomepageExample controller = loader.getController();
            controller.initialize(authenticated, primary);

            primary.show();
        }
    }

    public void init(Stage primaryStage){
        primary = primaryStage;
    }
}
