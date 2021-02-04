package it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers;

import it.unipi.iit.inginf.lsmdb.communitunes.frontend.LayoutManager;
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
        manager.setContent("ui/pages/homepage_example.fxml");
    }

    @Override
    public void init(LayoutManager manager) {
        this.manager = manager;
    }

    public void setContent(Node node){
        content.getChildren().setAll(node);
    }

    public void logoutHandler( ) throws IOException {
        manager.authenticated = null;
        manager.showAuthenticationPage(manager.LOGIN);
    }
}
