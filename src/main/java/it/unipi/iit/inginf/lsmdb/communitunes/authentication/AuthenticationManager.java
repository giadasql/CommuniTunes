package it.unipi.iit.inginf.lsmdb.communitunes.authentication;


import it.unipi.iit.inginf.lsmdb.communitunes.utilities.exceptions.PersistenceInconsistencyException;


public interface AuthenticationManager {

    /**
     *
     * Searches for a user account with the specified username and, if it exists, checks if the password matches, returning
     * the result of the match.
     *
     * @param username String representing the username of the account the user wants to log into.
     * @param psw String representing the password connected to the username passed by the user.
     * @return The result of the authentication process in form of an AuthResult object.
     */
    AuthResult login(String username, String psw);

    /**
     *
     * Searches for a user account with the specified username and, if it exists, checks if the password matches, along
     * with checking if the account has the isAdmin field set. Returns the result of the match.
     *
     * @param username String representing the username of the account the user wants to log into.
     * @param psw String representing the password connected to the username passed by the user.
     * @return The result of the authentication process in form of an AuthResult object.
     */
    AuthResult adminLogin(String username, String psw);

    /**
     *
     * Creates a new user account with the specified information passed. Return the result of the registration, that can
     * fail if the email has already been used or the username already exists.
     *
     * @param username String containing the username chose by the user to be identified with on the platform.
     * @param email String containing the email chose by the user to register with.
     * @param psw String containing the secret key chose by the user to login with.
     * @return The result of the registration process in form of an AuthResult object.
     * @throws PersistenceInconsistencyException In case of error when saving the information.
     */
    AuthResult register(String username, String email, String psw) throws PersistenceInconsistencyException;

    String securePassword(String password);
}
