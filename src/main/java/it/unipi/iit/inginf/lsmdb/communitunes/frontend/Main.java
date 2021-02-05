package it.unipi.iit.inginf.lsmdb.communitunes.frontend;

import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        LayoutManager manager = new LayoutManager();
        manager.startApp(primaryStage);
    }

    public static void main(String args[]) {launch(args);}

}
