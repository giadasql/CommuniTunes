package it.unipi.iit.inginf.lsmdb.communitunes.authentication;
import it.unipi.iit.inginf.lsmdb.communitunes.utilities.exceptions.PersistenceInconsistencyException;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.User;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.Persistence;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.PersistenceFactory;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.validator.routines.EmailValidator;

class AuthenticationManagerImplementation implements AuthenticationManager {

    private final String WrongCredentials = "Wrong username or password";
    private final String UsernameTaken = "This username is already taken";
    private final String UsernameInvalidChars = "The username must contain only letters, numbers, or \"-\", \"_\" and \".\"";
    private final String InvalidEmail = "Please specify an email in the correct format.";
    private final Persistence persistenceManager;

    AuthenticationManagerImplementation(){
        persistenceManager = PersistenceFactory.CreatePersistence();
    }

    @Override
    public AuthResult Login(String username, String psw) {
        if(persistenceManager.checkPassword(username, DigestUtils.sha256Hex(psw))){
            return new AuthResult(username, true, null);
        }
        else{
            return new AuthResult(null, false, WrongCredentials);
        }
    }

    @Override
    public AuthResult Register(String username, String email, String psw) throws PersistenceInconsistencyException {
        String usernameRegex = "^[a-zA-Z0-9._-]{3,}$";
        if(!username.matches(usernameRegex)){
            return new AuthResult(null, false, UsernameInvalidChars);
        }
        EmailValidator emailValidator = EmailValidator.getInstance();
        if(!emailValidator.isValid(email)){
            return new AuthResult(null, false, InvalidEmail);
        }
        if(persistenceManager.checkIfUsernameExists(username)){
            return new AuthResult(null, false, UsernameTaken);
        }
        else{
            User newUser = new User(username, email, DigestUtils.sha256Hex(psw));
            persistenceManager.addNewUser(newUser);
            return new AuthResult(username, true, null);
        }
    }
}
