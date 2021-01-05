package it.unipi.iit.inginf.lsmdb.communitunes.authentication;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.User;

public class AuthResult {

    public String AuthenticatedUser = null;
    public boolean Success;
    public String ErrorMsg = null;

    public AuthResult(String authenticatedUser, boolean success, String errorMsg){
        Success = success;
        if(success){
            AuthenticatedUser = authenticatedUser;
        }
        else{
            ErrorMsg = errorMsg;
        }
    }
}
