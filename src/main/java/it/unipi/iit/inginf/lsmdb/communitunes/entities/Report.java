package it.unipi.iit.inginf.lsmdb.communitunes.entities;

import java.util.HashMap;
import java.util.List;

public class Report extends Entity {

    public String reportedUser;
    public Integer numReports;
    public List<HashMap<String, String>> comments;

    public Report(String reportedUser, Integer numReports, List<HashMap<String, String>> comments){
        super(null);
        this.reportedUser = reportedUser;
        this.numReports = numReports;
        this.comments = comments;
    }
}
