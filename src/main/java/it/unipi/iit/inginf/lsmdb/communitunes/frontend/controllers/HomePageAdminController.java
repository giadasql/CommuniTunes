package it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers;

import it.unipi.iit.inginf.lsmdb.communitunes.authentication.AuthenticationManager;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManager;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.Persistence;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.PersistenceFactory;
import javafx.collections.FXCollections;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class HomePageAdminController implements UIController {

    public ChoiceBox searchMenu;
    public TextField searchText;
    public VBox requestReportBox;
    public VBox searchBox;

    private LayoutManager manager;
    private AuthenticationManager authManager;
    private Persistence dbManager;

    public void init(){
        dbManager = PersistenceFactory.CreatePersistence();
        ArrayList<String> choices = new ArrayList<>();
        choices.add("User");
        choices.add("Song");
        choices.add("Comment");
        searchMenu.setItems(FXCollections.observableArrayList(choices));
    }
}
