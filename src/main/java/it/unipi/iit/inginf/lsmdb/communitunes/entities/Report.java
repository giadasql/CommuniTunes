package it.unipi.iit.inginf.lsmdb.communitunes.entities;

import java.util.HashMap;

public class Report extends Entity {

    public String reportedUser;
    public Integer numReports;
    public HashMap<String, String> comments;

    public Report(String reportedUser, Integer numReports, HashMap<String, String> comments){
        super(null);
        this.reportedUser = reportedUser;
        this.numReports = numReports;
        this.comments = comments;
    }
}
