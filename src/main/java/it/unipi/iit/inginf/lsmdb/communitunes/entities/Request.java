package it.unipi.iit.inginf.lsmdb.communitunes.entities;

public class Request extends Entity {

    public String username;
    public String requestedStageName;

    public Request(String id, String username, String requestedStageName){
        super(id);
        this.username = username;
        this.requestedStageName = requestedStageName;
    }
}
