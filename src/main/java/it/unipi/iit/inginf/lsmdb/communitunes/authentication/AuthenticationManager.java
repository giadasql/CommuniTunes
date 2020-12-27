package it.unipi.iit.inginf.lsmdb.communitunes.authentication;

public interface AuthenticationManager {
    AuthResult Login(String username, String psw);
    AuthResult Register(String username, String email, String psw);
}
