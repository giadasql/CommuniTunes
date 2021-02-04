package it.unipi.iit.inginf.lsmdb.communitunes.frontend;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.User;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers.LayoutController;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers.UIController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

import java.io.IOException;

public class LayoutManager {

    private Stage primary;
    private final FXMLLoader layoutLoader;
    private LayoutController layoutController;
    private boolean layoutVisible = false;

    public User authenticated;
    public final String HOMEPAGE_EXAMPLE = "./ui/pages/homepage_example.fxml";
    public final String LOGIN = "ui/pages/authentication/login.fxml";
    public final String GENERAL_LAYOUT = "./ui/pages/general_layout.fxml";
    public final String REGISTER = "./ui/pages/authentication/register.fxml";

    public LayoutManager() throws IOException {
        layoutLoader = getLoader(GENERAL_LAYOUT);
    }

    public void startApp(Stage primaryStage) throws IOException {
        primary = primaryStage;
        primaryStage.setTitle("Communitunes");

        showAuthenticationPage(LOGIN);
        primaryStage.show();
    }

    public void showAuthenticationPage(String resourcePath) throws IOException {
        layoutVisible = false;
        FXMLLoader authLoader = getLoader(resourcePath);

        primary.setScene(
                new Scene(authLoader.load(), 900, 600)
        );

        UIController controller = authLoader.getController();
        controller.init(this);
    }

    public void setContent(String resource) throws IOException {
        if(!layoutVisible){
            primary.setScene( new Scene(layoutLoader.load(), 900, 600));
            layoutController = layoutLoader.getController();
            layoutController.init(this);
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
