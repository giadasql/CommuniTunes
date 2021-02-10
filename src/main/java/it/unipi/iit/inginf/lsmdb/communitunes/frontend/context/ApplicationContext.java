package it.unipi.iit.inginf.lsmdb.communitunes.frontend.context;

import it.unipi.iit.inginf.lsmdb.communitunes.authentication.Role;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.Artist;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.User;
import javafx.application.HostServices;

public class ApplicationContext {
    private User authenticatedUser;
    private User focusedUser;
    private Artist focusedArtist;
    private Artist authenticatedArtist;
    private Role authenticatedRole;
    private Role focusedRole;
    public HostServices hostServices;

    ApplicationContext(){};

    public User getAuthenticatedUser() {
        return authenticatedUser == null ? authenticatedArtist : authenticatedUser;
    }

    public User getFocusedUser() {
        return focusedUser == null ? focusedArtist : focusedUser;
    }

    public void setAuthenticatedUser(User authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
        this.authenticatedRole =  Role.User;
        this.authenticatedArtist = null;
    }

    public void setFocusedUser(User focusedUser) {
        this.focusedUser = focusedUser;
        this.focusedRole = Role.User;
        this.focusedArtist = null;
    }

    public Artist getFocusedArtist() {
        return focusedArtist;
    }

    public void setFocusedArtist(Artist focusedArtist) {
        this.focusedArtist = focusedArtist;
        this.focusedRole = Role.Artist;
        this.focusedUser = null;
    }

    public Artist getAuthenticatedArtist() {
        return authenticatedArtist;
    }

    public void setAuthenticatedArtist(Artist authenticatedArtist) {
        this.authenticatedArtist = authenticatedArtist;
        authenticatedRole =  Role.Artist;
        this.authenticatedUser = null;
    }

    public Role getAuthenticatedRole() {
        return authenticatedRole;
    }


    public Role getFocusedRole() {
        return focusedRole;
    }
}
