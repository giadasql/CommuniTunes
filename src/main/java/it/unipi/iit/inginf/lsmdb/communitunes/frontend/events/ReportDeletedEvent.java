package it.unipi.iit.inginf.lsmdb.communitunes.frontend.events;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.Report;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.components.ReportView;
import javafx.event.Event;
import javafx.event.EventType;

public class ReportDeletedEvent extends Event {
    public Report report;
    public ReportView reportView;

    public ReportDeletedEvent(EventType<? extends Event> eventType, Report report, ReportView reportView) {
        super(eventType);
        this.report = report;
        this.reportView = reportView;
    }
}
