package it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers;

import it.unipi.iit.inginf.lsmdb.communitunes.authentication.Role;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManager;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.Path;
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

    @FXML
    public void searchEventHandler(MouseEvent event) throws IOException {
        System.out.println(searchBarText.getText());
    }

    @Override
    public void init(LayoutManager manager) {
        this.manager = manager;
    }

    public void setContent(Node node){
        content.getChildren().setAll(node);
    }

    public void logoutHandler( ) throws IOException {
        manager.context.setAuthenticatedUser(null);
        manager.showAuthenticationPage(Path.LOGIN);
    }

    public void showUserProfile(MouseEvent mouseEvent) throws IOException {
        if(manager.context.getAuthenticatedRole() == Role.User){
            manager.context.setFocusedUser(manager.context.getAuthenticatedUser());
            manager.setContent(Path.USER_PROFILE);
        }
        else if(manager.context.getAuthenticatedRole() == Role.Artist){
            manager.context.setFocusedArtist(manager.context.getAuthenticatedArtist());
            manager.setContent(Path.ARTIST_PROFILE);
        }
    }

    public void goToHomepage(MouseEvent mouseEvent) throws IOException {
        manager.setContent(Path.HOMEPAGE);
    }
}
