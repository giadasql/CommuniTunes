package it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers;

import it.unipi.iit.inginf.lsmdb.communitunes.authentication.Role;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.Artist;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.User;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManager;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManagerFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.Path;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.Persistence;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.PersistenceFactory;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class LayoutController implements UIController {
    @FXML
    private TextField searchBarText;

    @FXML
    private Pane content;

    private LayoutManager manager;
    private Persistence dbManager;

    @FXML
    public void searchEventHandler(MouseEvent event) throws IOException {
        System.out.println(searchBarText.getText());
    }

    @Override
    public void init() {
        dbManager = PersistenceFactory.CreatePersistence();
        this.manager = LayoutManagerFactory.getManager();
    }

    public void setContent(Node node){
        content.getChildren().setAll(node);
    }

    public void logoutHandler( ) throws IOException {
        manager.context.setAuthenticatedUser(null);
        manager.context.setAuthenticatedArtist(null);
        manager.context.inAdminPanel = false;
        manager.showAuthenticationPage(Path.LOGIN);
    }

    public void showUserProfile(MouseEvent mouseEvent) throws IOException {
        Role authRole = manager.context.getAuthenticatedRole();
        refreshAuthenticated(authRole);
        if(authRole == Role.User){
            manager.context.setFocusedUser(manager.context.getAuthenticatedUser());
            manager.setContent(Path.USER_PROFILE);
        }
        else if(authRole == Role.Artist){
            manager.context.setFocusedArtist(manager.context.getAuthenticatedArtist());
            manager.setContent(Path.ARTIST_PROFILE);
        }
    }

    private void refreshAuthenticated(Role role){
        if(role == Role.Artist){
            Artist authArtist = dbManager.getArtist(manager.context.getAuthenticatedArtist().Username);
            manager.context.setAuthenticatedArtist(authArtist);
        }
        else if(role == Role.User){
            User authUser = dbManager.getUser(manager.context.getAuthenticatedUser().Username);
            manager.context.setAuthenticatedUser(authUser);
        }
    }

    public void goToHomepage(MouseEvent mouseEvent) throws IOException {
        manager.setContent(Path.HOMEPAGE);
    }
}
