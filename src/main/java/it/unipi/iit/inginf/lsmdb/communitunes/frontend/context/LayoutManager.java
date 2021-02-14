package it.unipi.iit.inginf.lsmdb.communitunes.frontend.context;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.Artist;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.Song;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.User;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers.HomePageAdminController;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers.LayoutController;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers.UIController;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.Path;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.Persistence;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.PersistenceFactory;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class LayoutManager {

    private Stage primary;
    private final LayoutController layoutController;
    private boolean layoutVisible = false;
    private Scene layoutScene = null;
    public final ApplicationContext context = new ApplicationContext();
    public final Persistence dbManager;
    private HomePageAdminController adminController;

    LayoutManager() {
        FXMLLoader layoutLoader = getLoader(Path.GENERAL_LAYOUT);
        try {
            layoutScene = new Scene(layoutLoader.load(), 900, 600);
        } catch (IOException e) {
            // TODO: log
            e.printStackTrace();
        }
        layoutController = layoutLoader.getController();
        dbManager = PersistenceFactory.CreatePersistence();
    }

    public void startApp(Stage primaryStage, HostServices hostServices) throws IOException {
        primary = primaryStage;
        primaryStage.setTitle("Communitunes");
        context.hostServices = hostServices;
        showAuthenticationPage(Path.LOGIN);
        primaryStage.show();
    }

    public void showAuthenticationPage(String resourcePath) throws IOException {
        layoutVisible = false;
        FXMLLoader authLoader = getLoader(resourcePath);

        primary.setScene(
                new Scene(authLoader.load(), 900, 600)
        );

        UIController controller = authLoader.getController();
        controller.init();
    }

    public void showAdminPage(String adminPagePath) throws IOException {
        context.inAdminPanel = true;
        layoutVisible = false;
        FXMLLoader adminLoader = getLoader(adminPagePath);

        primary.setScene(
                new Scene(adminLoader.load(), 900, 600)
        );

        adminController = adminLoader.getController();
        adminController.init();
    }

    public void setContent(String resource) throws IOException {
        if(context.inAdminPanel){
            adminController.setContent(resource);
            return;
        }

        if(!layoutVisible){
            primary.setScene( layoutScene);
            layoutController.init();
            layoutVisible = true;
        }

        FXMLLoader loader = getLoader(resource);
        layoutController.setContent(loader.load());

        UIController controller = loader.getController();
        controller.init();
        primary.show();
    }

    private FXMLLoader getLoader(String resourcePath){
        return new FXMLLoader(
                getClass().getClassLoader().getResource(
                        resourcePath
                )
        );
    }

    public void goToUserOrArtistPage(String username){
        Artist artist = dbManager.getArtist(username);
        if(artist == null){
            goToUserPage(username);
        }
        else{
            goToArtistPage(artist);
        }
    }

    public void goToUserPage(String username){
        User focused = dbManager.getUser(username);
        context.setFocusedUser(focused);
        try {
            setContent(Path.USER_PROFILE);
        } catch (IOException e) {
            // TODO: log
            e.printStackTrace();
        }
    }

    public void goToArtistPage(String username){
        Artist focused = dbManager.getArtist(username);
        context.setFocusedArtist(focused);
        try {
            setContent(Path.ARTIST_PROFILE);
        } catch (IOException e) {
            // TODO: log
            e.printStackTrace();
        }
    }

    public void goToArtistPage(Artist artist){
        context.setFocusedArtist(artist);
        try {
            setContent(Path.ARTIST_PROFILE);
        } catch (IOException e) {
            // TODO: log
            e.printStackTrace();
        }
    }

    public void goToSongPage(String id){
        Song focused = dbManager.getSong(id);
        context.setFocusedSong(focused);
        try {
            setContent(Path.SONG_PAGE);
        } catch (IOException e) {
            // TODO: log
            e.printStackTrace();
        }
    }
}
