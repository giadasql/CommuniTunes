package it.unipi.iit.inginf.lsmdb.communitunes.frontend.context;

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
    private final Scene layoutScene;
    public final ApplicationContext context = new ApplicationContext();

    public LayoutManager() throws IOException {
        FXMLLoader layoutLoader = getLoader(Path.GENERAL_LAYOUT);
        layoutScene = new Scene(layoutLoader.load(), 900, 600);
        layoutController = layoutLoader.getController();
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
        controller.init(this);
    }

    public void setContent(String resource) throws IOException {
        if(!layoutVisible){
            primary.setScene( layoutScene);
            layoutController.init(this);
            layoutVisible = true;
        }

        FXMLLoader loader = getLoader(resource);
        layoutController.setContent(loader.load());

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
