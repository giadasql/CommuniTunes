package it.unipi.iit.inginf.lsmdb.communitunes.frontend;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.User;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManager;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManagerFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.Persistence;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.PersistenceFactory;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.stage.Stage;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        LayoutManager manager = LayoutManagerFactory.getManager();
        HostServices hostServices = this.getHostServices();
        manager.startApp(primaryStage, hostServices);
    }

    public static void main(String args[]) {launch(args);}

}
