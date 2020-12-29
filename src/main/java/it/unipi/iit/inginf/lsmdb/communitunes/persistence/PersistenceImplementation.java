package it.unipi.iit.inginf.lsmdb.communitunes.persistence;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.User;
import it.unipi.iit.inginf.lsmdb.communitunes.utilities.configurations.ConfigReader;
import it.unipi.iit.inginf.lsmdb.communitunes.utilities.configurations.ConfigReaderFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.utilities.configurations.ConfigReaderType;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

class PersistenceImplementation implements Persistence {

    private ConfigReader reader;

    private MongoDriver mongo;
    private Neo4jDriver neo4j;

    public PersistenceImplementation() {
        String settingsFileName = "Settings.xml";
        InputStream settingsFileStream = this.getClass().getClassLoader().getResourceAsStream(settingsFileName);
        try{
            reader = ConfigReaderFactory.CreateConfigReader(ConfigReaderType.Xml, settingsFileStream);
        }
        catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
            // TODO: log
            return;
        }
        if(reader == null){
            // TODO: log
            return;
        }

        neo4jInit();
        mongoInit();
    }

    private void neo4jInit(){
        String neo4jUri = reader.getStringConfigValue("Neo4j", "uri");
        String neo4jUser = reader.getStringConfigValue("Neo4j", "username");
        String neo4jPsw = reader.getStringConfigValue("Neo4j", "password");
        neo4j = new Neo4jDriver(neo4jUri, neo4jUser, neo4jPsw);
    }

    private void mongoInit(){
        String connectionString = reader.getStringConfigValue("MongoDB", "connectionString");
        mongo = new MongoDriver(connectionString);
    }

    public boolean checkIfUserExists(User newUser){
        return mongo.checkIfEmailExists(newUser.Email) || mongo.checkIfUsernameExists(newUser.Username) || neo4j.checkIfUsernameExists(newUser.Username);
    }

    public boolean addNewUser(User newUser){
        return false;
    }
}
