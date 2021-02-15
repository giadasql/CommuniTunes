package it.unipi.iit.inginf.lsmdb.communitunes.authentication;

public class AuthResult {

    public String authenticated = null;
    public boolean success;
    public String errorMsg = null;
    public Role role;

    public AuthResult(String authenticated, boolean success, Role role, String errorMsg){
        this.success = success;
        this.role = role;
        if(success){
            this.authenticated = authenticated;
        }
        else{
            this.errorMsg = errorMsg;
        }
    }
}
