package it.unipi.iit.inginf.lsmdb.communitunes.frontend;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.User;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers.AuthenticationController;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers.HomepageExample;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private final User currentUser = null;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(
                getClass().getClassLoader().getResource(
                        "login.fxml"
                )
        );

        primaryStage.setTitle("Communitunes");
        primaryStage.setScene(
                new Scene(loader.load(), 900, 600)
        );

        AuthenticationController controller = loader.getController();
        controller.init(primaryStage);

        primaryStage.show();
    }

    public static void main(String args[]) {launch(args);}

}
