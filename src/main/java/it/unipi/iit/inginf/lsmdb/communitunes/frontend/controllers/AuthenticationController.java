package it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers;


import it.unipi.iit.inginf.lsmdb.communitunes.authentication.AuthResult;
import it.unipi.iit.inginf.lsmdb.communitunes.authentication.AuthenticationFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.authentication.AuthenticationManager;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManager;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.Path;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.Persistence;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.PersistenceFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.utilities.exceptions.PersistenceInconsistencyException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class AuthenticationController implements UIController {
    @FXML
    public TextField errorMsg;
    @FXML
    public TextField email;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;

    private Stage primary;
    private LayoutManager manager;
    private AuthenticationManager authManager;
    private Persistence dbManager;

    @FXML
    public void loginEventHandler(ActionEvent event) throws IOException {
        if (authManager != null && manager != null){
            AuthResult result = authManager.Login(username.getText(), password.getText());
            if(result.Success){
                errorMsg.setText("");
                manager.context.authenticatedUser = dbManager.getUser(result.AuthenticatedUser);
                manager.setContent(Path.HOMEPAGE_EXAMPLE);
            }
            else{
                errorMsg.setText(result.ErrorMsg);
            }
        }
    }


    @Override
    public void init(LayoutManager manager) {
        authManager = AuthenticationFactory.CreateAuthenticationManager();
        dbManager = PersistenceFactory.CreatePersistence();
        this.manager = manager;
        errorMsg.setText("");
    }

    @FXML
    public void registerEventHandler(ActionEvent actionEvent) throws PersistenceInconsistencyException, IOException {
        if (authManager != null && manager != null){
            AuthResult result = authManager.Register(username.getText(), email.getText(), password.getText());
            if (result.Success){
                errorMsg.setText("");
                manager.context.authenticatedUser = dbManager.getUser(result.AuthenticatedUser);
                manager.setContent(Path.HOMEPAGE_EXAMPLE);
            }
            else{
                errorMsg.setText(result.ErrorMsg);
            }
        }
    }

    @FXML
    public void showLogin(ActionEvent actionEvent) throws IOException {
        manager.showAuthenticationPage(Path.LOGIN);
    }

    @FXML
    public void showAdmin(ActionEvent actionEvent) {

    }

    @FXML
    public void showRegister(ActionEvent actionEvent) throws IOException {
        manager.showAuthenticationPage(Path.REGISTER);
    }
}
