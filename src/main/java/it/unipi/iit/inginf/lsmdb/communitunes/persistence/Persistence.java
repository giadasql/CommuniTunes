package it.unipi.iit.inginf.lsmdb.communitunes.persistence;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.User;
import it.unipi.iit.inginf.lsmdb.communitunes.utilities.exceptions.PersistenceInconsistencyException;

public interface Persistence {
    boolean checkIfUserExists(User user);
    boolean addNewUser(User newUser) throws PersistenceInconsistencyException;
    boolean deleteUser(User user);
    boolean checkPassword(User user);
    void close();
}
