package it.unipi.iit.inginf.lsmdb.communitunes.persistence;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.User;

public interface Persistence {
    boolean checkIfUserExists(User newUser);
    boolean addNewUser(User newUser);
    boolean deleteUser(User user);
    void close();
}
