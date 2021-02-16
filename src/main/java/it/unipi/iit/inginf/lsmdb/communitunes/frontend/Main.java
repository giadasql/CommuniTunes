package it.unipi.iit.inginf.lsmdb.communitunes.frontend;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.User;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManager;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManagerFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.Persistence;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.PersistenceFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.utilities.configurations.ConfigReader;
import it.unipi.iit.inginf.lsmdb.communitunes.utilities.configurations.ConfigReaderFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.utilities.configurations.ConfigReaderType;
import it.unipi.iit.inginf.lsmdb.communitunes.utilities.exceptions.InvalidConfigurationException;
import it.unipi.iit.inginf.lsmdb.communitunes.utilities.exceptions.PersistenceLayerUnreachableException;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

public class Main extends Application {

    private Persistence dbManager;

    @Override
    public void start(Stage primaryStage) throws Exception{
        InputStream settingsFileStream = this.getClass().getClassLoader().getResourceAsStream("Settings.xml");
        ConfigReader reader;
        try{
            reader = ConfigReaderFactory.CreateConfigReader(ConfigReaderType.Xml, settingsFileStream);
            dbManager = PersistenceFactory.CreatePersistence(reader);
            LayoutManager manager = LayoutManagerFactory.getManager();
            HostServices hostServices = this.getHostServices();
            manager.startApp(primaryStage, hostServices, dbManager);
        }
        catch (ParserConfigurationException | IOException | SAXException | InvalidConfigurationException | PersistenceLayerUnreachableException e) {
            Logger logger = LoggerFactory.getLogger(Main.class);
            logger.error("An exception occurred: ", e);
        }
        finally {
            if(settingsFileStream != null){
                try {
                    settingsFileStream.close();
                } catch (IOException e) {
                    Logger logger = LoggerFactory.getLogger(Main.class);
                    logger.error("An exception occurred: ", e);
                }
            }
        }
    }

    public static void main(String args[]) {launch(args);}

    @Override
    public void stop() throws Exception {
        super.stop();
        if(dbManager != null){
            dbManager.close();
        }
    }
}
