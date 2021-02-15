package it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers.authentication;


import it.unipi.iit.inginf.lsmdb.communitunes.authentication.AuthResult;
import it.unipi.iit.inginf.lsmdb.communitunes.authentication.AuthenticationFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.authentication.AuthenticationManager;
import it.unipi.iit.inginf.lsmdb.communitunes.authentication.Role;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManager;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManagerFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.Path;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers.UIController;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.Persistence;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.PersistenceFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.utilities.exceptions.PersistenceInconsistencyException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

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

    private LayoutManager manager;
    private AuthenticationManager authManager;
    private Persistence dbManager;

    @FXML
    public void loginEventHandler(ActionEvent event) throws IOException {
        if (authManager != null && manager != null){
            AuthResult result = authManager.login(username.getText(), password.getText());
            if(result.success){
                errorMsg.setText("");
                if(result.role == Role.Artist){
                    manager.context.setAuthenticatedArtist(dbManager.getArtist(result.authenticated));
                }
                else if(result.role == Role.User){
                    manager.context.setAuthenticatedUser(dbManager.getUser(result.authenticated));
                }
                manager.setContent(Path.HOMEPAGE);
            }
            else{
                errorMsg.setText(result.errorMsg);
            }
        }
    }

    @FXML
    public void adminLoginEventHandler(ActionEvent event) throws IOException {
        if (authManager != null && manager != null){
            AuthResult result = authManager.adminLogin(username.getText(), password.getText());
            if(result.success){
                errorMsg.setText("");
                manager.showAdminPage(Path.HOMEPAGE_ADMIN);
            }
            else{
                errorMsg.setText(result.errorMsg);
            }
        }
    }

    @Override
    public void init() {
        authManager = AuthenticationFactory.CreateAuthenticationManager();
        dbManager = PersistenceFactory.CreatePersistence();
        this.manager = LayoutManagerFactory.getManager();
        errorMsg.setText("");
    }

    @FXML
    public void registerEventHandler(ActionEvent actionEvent) throws PersistenceInconsistencyException, IOException {
        if (authManager != null && manager != null){
            AuthResult result = authManager.register(username.getText(), email.getText(), password.getText());
            if (result.success){
                errorMsg.setText("");
                manager.context.setAuthenticatedUser(dbManager.getUser(result.authenticated));
                manager.setContent(Path.HOMEPAGE);
            }
            else{
                errorMsg.setText(result.errorMsg);
            }
        }
    }

    @FXML
    public void showLogin(ActionEvent actionEvent) throws IOException {
        manager.showAuthenticationPage(Path.LOGIN);
    }

    @FXML
    public void showAdmin(ActionEvent actionEvent) throws IOException {
        manager.showAuthenticationPage(Path.ADMIN_LOGIN);
    }

    @FXML
    public void showRegister(ActionEvent actionEvent) throws IOException {
        manager.showAuthenticationPage(Path.REGISTER);
    }
}
