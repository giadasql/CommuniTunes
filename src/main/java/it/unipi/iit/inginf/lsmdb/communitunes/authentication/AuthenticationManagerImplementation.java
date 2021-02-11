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
        int checkResult = persistenceManager.checkCredentials(username, securePassword(psw));
        if(checkResult == 0){
            return new AuthResult(username, true, Role.User, null);
        }
        else if (checkResult == 1){
            return new AuthResult(username, true, Role.Artist, null);
        }
        else {
            return new AuthResult(null, false, null, WrongCredentials);
        }
    }

    @Override
    public AuthResult adminLogin(String username, String psw) {
        if(persistenceManager.checkAdminCredentials(username, securePassword(psw))) {
            return new AuthResult(username, true, Role.Admin, null);
        }
        else {
            return new AuthResult(null, false, null, WrongCredentials);
        }
    }

    @Override
    public AuthResult Register(String username, String email, String psw) throws PersistenceInconsistencyException {
        String usernameRegex = "^[a-zA-Z0-9._-]{3,}$";
        if(!username.matches(usernameRegex)){
            return new AuthResult(null, false, null, UsernameInvalidChars);
        }
        EmailValidator emailValidator = EmailValidator.getInstance();
        if(!emailValidator.isValid(email)){
            return new AuthResult(null, false, null, InvalidEmail);
        }
        if(persistenceManager.checkIfUsernameExists(username)){
            return new AuthResult(null, false, null, UsernameTaken);
        }
        else{
            User newUser = new User(username, email, securePassword(psw));
            persistenceManager.addNewUser(newUser);
            return new AuthResult(username, true, Role.User, null);
        }
    }

    @Override
    public String securePassword(String password) {
        return DigestUtils.sha256Hex(password);
    }

}
