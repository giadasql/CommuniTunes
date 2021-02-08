package it.unipi.iit.inginf.lsmdb.communitunes.authentication;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.User;

public class AuthResult {

    public String Authenticated = null;
    public boolean Success;
    public String ErrorMsg = null;
    public Role role;

    public AuthResult(String authenticated, boolean success, Role role, String errorMsg){
        Success = success;
        this.role = role;
        if(success){
            Authenticated = authenticated;
        }
        else{
            ErrorMsg = errorMsg;
        }
    }
}
