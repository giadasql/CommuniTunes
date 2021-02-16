package it.unipi.iit.inginf.lsmdb.communitunes.persistence;

import it.unipi.iit.inginf.lsmdb.communitunes.utilities.exceptions.ConfigurationFileNotFoundException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class PersistenceFactoryTest {
    @Test
    void WHEN_createPesistence_InvokedWithNull_ThrowsInvalidConfigurationExeption() {
        assertThrows(ConfigurationFileNotFoundException.class, () -> PersistenceFactory.CreatePersistence(null));
    }
}