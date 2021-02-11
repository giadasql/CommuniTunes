package it.unipi.iit.inginf.lsmdb.communitunes.authentication;


import it.unipi.iit.inginf.lsmdb.communitunes.utilities.exceptions.PersistenceInconsistencyException;

/**
 *
 */
public interface AuthenticationManager {

    /**
     *
     * @param username
     * @param psw
     * @return
     */
    AuthResult Login(String username, String psw);

    /**
     *
     * @param username
     * @param psw
     * @return
     */
    AuthResult adminLogin(String username, String psw);

    /**
     *
     * @param username
     * @param email
     * @param psw
     * @return
     * @throws PersistenceInconsistencyException
     */
    AuthResult Register(String username, String email, String psw) throws PersistenceInconsistencyException;

    String securePassword(String password);
}
