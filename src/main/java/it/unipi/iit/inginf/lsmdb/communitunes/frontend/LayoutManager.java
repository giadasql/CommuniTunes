package it.unipi.iit.inginf.lsmdb.communitunes.frontend;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.User;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers.AuthenticationController;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers.HomepageExample;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers.LayoutController;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers.UIController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

import java.io.IOException;

public class LayoutManager {

    private Stage primary;
    private final FXMLLoader layoutLoader;
    private LayoutController layoutController;
    private final FXMLLoader authLoader;
    private boolean layoutVisible = false;

    public User authenticated;
    public final String HOMEPAGE_EXAMPLE = "./ui/pages/homepage_example.fxml";

    public LayoutManager() throws IOException {
        layoutLoader = getLoader("./ui/pages/general_layout.fxml");

        authLoader = getLoader("./ui/pages/login.fxml");
    }

    public void startApp(Stage primaryStage) throws IOException {
        primary = primaryStage;
        primaryStage.setTitle("Communitunes");

        showAuthenticationPage();
        primaryStage.show();
    }

    public void showAuthenticationPage() throws IOException {
        layoutVisible = false;

        primary.setScene(
                new Scene(authLoader.load(), 900, 600)
        );

        UIController controller = authLoader.getController();
        controller.init(this);
    }

    public void setContent(String resource) throws IOException {
        if(!layoutVisible){
            primary.setScene(layoutLoader.load());
            layoutController = layoutLoader.getController();
            layoutVisible = true;
        }

        FXMLLoader loader = getLoader(resource);
        ScrollPane scrollPane = new ScrollPane(loader.load());
        scrollPane.fitToWidthProperty().setValue(true);
        scrollPane.fitToHeightProperty().setValue(true);

        layoutController.setContent(scrollPane);

        UIController controller = loader.getController();
        controller.init(this);
        primary.show();
    }

    private FXMLLoader getLoader(String resourcePath){
        return new FXMLLoader(
                getClass().getClassLoader().getResource(
                        resourcePath
                )
        );
    }
}
