package it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers;

import it.unipi.iit.inginf.lsmdb.communitunes.authentication.Role;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.Artist;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.User;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.ArtistPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.SongPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.previews.UserPreview;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.components.ArtistPreviewVBox;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.components.SearchBar;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.components.SongPreviewVBox;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.components.UserPreviewVBox;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManager;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManagerFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.Path;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.events.FoundArtistsEvent;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.events.FoundSongsEvent;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.events.FoundUsersEvent;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.Persistence;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.PersistenceFactory;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.io.IOException;

public class LayoutController implements UIController {
    public HBox searchBarHbox;
    @FXML
    private TextField searchBarText;

    @FXML
    private Pane content;

    private HBox searchResultsPane;
    private TilePane resultsVbox;

    private LayoutManager manager;
    private Persistence dbManager;

    @FXML
    public void searchEventHandler(MouseEvent event) throws IOException {
        System.out.println(searchBarText.getText());
    }

    @Override
    public void init() {
        dbManager = PersistenceFactory.CreatePersistence();
        searchBarHbox.setAlignment(Pos.CENTER);
        SearchBar bar = new SearchBar(50, 400, false, false);
        bar.addEventHandler(SearchBar.FOUND_USERS_EVENT, this::showUsersPreviews);
        bar.addEventHandler(SearchBar.FOUND_SONGS_EVENT, this::showSongsPreviews);
        bar.addEventHandler(SearchBar.FOUND_ARTISTS_EVENT, this::showArtistsPreviews);
        searchBarHbox.getChildren().add(bar);
        resultsVbox = new TilePane();
        resultsVbox.setPrefRows(6);
        resultsVbox.setHgap(10);
        resultsVbox.setVgap(10);
        resultsVbox.borderProperty().set(null);
        resultsVbox.setStyle("-fx-background-color:  #001a4d;");
        searchResultsPane = new HBox();
        searchResultsPane.setAlignment(Pos.CENTER);
        searchResultsPane.getChildren().add(resultsVbox);
        this.manager = LayoutManagerFactory.getManager();
    }

    public void setContent(Node node){
        content.getChildren().setAll(node);
    }

    public void logoutHandler( ) throws IOException {
        manager.context.setAuthenticatedUser(null);
        manager.context.setAuthenticatedArtist(null);
        manager.context.inAdminPanel = false;
        searchBarHbox.getChildren().clear();
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

    public void showArtistsPreviews(FoundArtistsEvent event){
        resultsVbox.getChildren().clear();
        for (ArtistPreview preview:
                event.foundArtists) {
            resultsVbox.getChildren().add(new ArtistPreviewVBox(preview, null));
        }
        setContent(searchResultsPane);
    }

    public void showSongsPreviews(FoundSongsEvent event){
        resultsVbox.getChildren().clear();
        for (SongPreview preview:
                event.foundSongs) {
            resultsVbox.getChildren().add(new SongPreviewVBox(preview, null));
        }
        setContent(searchResultsPane);
    }

    public void showUsersPreviews(FoundUsersEvent event){
        resultsVbox.getChildren().clear();
        for (UserPreview preview:
                event.foundUsers) {
            resultsVbox.getChildren().add(new UserPreviewVBox(preview, null));
        }
        setContent(searchResultsPane);
    }
}
