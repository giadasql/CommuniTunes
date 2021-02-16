package it.unipi.iit.inginf.lsmdb.communitunes.persistence;

import it.unipi.iit.inginf.lsmdb.communitunes.utilities.configurations.ConfigReader;
import it.unipi.iit.inginf.lsmdb.communitunes.utilities.exceptions.ConfigurationFileNotFoundException;
import it.unipi.iit.inginf.lsmdb.communitunes.utilities.exceptions.InvalidConfigurationException;

import java.io.IOException;

public class PersistenceFactory {

    private PersistenceFactory(){ }
    private static Persistence persistenceInstance = null;

    public static Persistence CreatePersistence(ConfigReader reader) throws InvalidConfigurationException {
        if(reader == null){
            throw new InvalidConfigurationException();
        }
        if(persistenceInstance == null){
            persistenceInstance = new PersistenceImplementation(reader);
        }
        return persistenceInstance;
    }

}
