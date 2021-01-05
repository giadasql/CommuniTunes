package it.unipi.iit.inginf.lsmdb.communitunes.authentication;

import it.unipi.iit.inginf.lsmdb.communitunes.utilities.exceptions.PersistenceInconsistencyException;

public interface AuthenticationManager {
    AuthResult Login(String username, String psw);
    AuthResult Register(String username, String email, String psw) throws PersistenceInconsistencyException;
}
