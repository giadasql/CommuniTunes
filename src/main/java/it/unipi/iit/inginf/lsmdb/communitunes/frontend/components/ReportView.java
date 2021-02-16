package it.unipi.iit.inginf.lsmdb.communitunes.frontend.components;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.Report;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManagerFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.events.*;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.Persistence;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.PersistenceFactory;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class ReportView extends VBox {
    private final Report report;

    public ReportView(Report report){
        this.report = report;
        Line startLine = new Line(-100, 0, 300, 0);
        HBox reportedUserHBox = new HBox();
        Text reportedUser = new Text("Reported User: ");
        Text username = new Text(report.reportedUser);
        username.setFont(Font.font("System", FontWeight.BOLD, 12));
        reportedUserHBox.getChildren().addAll(reportedUser, username);
        HBox totReportsHBox = new HBox();
        Text reported = new Text("Reported ");
        Text tot = new Text(report.numReports.toString());
        tot.setFont(Font.font("System", FontWeight.BOLD, 12));
        Text times = new Text(" times.");
        totReportsHBox.getChildren().addAll(reported, tot, times);
        HBox buttonsHBox = new HBox();
        Button deleteReportBtn = new Button("Delete Report");
        deleteReportBtn.setOnMouseClicked(this::reportDeleted);
        deleteReportBtn.setCursor(Cursor.HAND);
        Button seeReviewsBtn = new Button("See Reviews");
        seeReviewsBtn.setOnMouseClicked(this::showReviews);
        seeReviewsBtn.setCursor(Cursor.HAND);
        Button seeProfileBtn = new Button("See Profile");
        seeProfileBtn.setCursor(Cursor.HAND);
        seeProfileBtn.setOnMouseClicked(this::seeUserProfileClicked);
        Button deleteUserBtn = new Button("DeleteUser");
        deleteUserBtn.setOnMouseClicked(this::deleteUser);
        deleteUserBtn.setCursor(Cursor.HAND);
        buttonsHBox.setSpacing(10);
        buttonsHBox.setPadding(new Insets(0, 0, 0, 25));
        buttonsHBox.getChildren().addAll(deleteReportBtn, seeProfileBtn, seeReviewsBtn, deleteUserBtn);
        this.getChildren().addAll(startLine, reportedUserHBox, totReportsHBox, buttonsHBox);
        this.setPadding(new Insets(10, 0, 0, 0));
        this.setSpacing(3);
    }

    private void seeUserProfileClicked(MouseEvent mouseEvent) {
        SeeUserProfileClicked  seeUserProfileClicked = new SeeUserProfileClicked(SEE_USER_PROFILE_CLICKED, report.reportedUser);
        fireEvent(seeUserProfileClicked);
    }

    private void reportDeleted(MouseEvent mouseEvent) {
        if(LayoutManagerFactory.getManager().dbManager.deleteReport(report.reportedUser)){
            ReportDeletedEvent reportDeletedEvent = new ReportDeletedEvent(REPORT_DELETED_EVENT, report, this);
            fireEvent(reportDeletedEvent);
        }
    }

    public static final EventType<Event> REPORT_EVENT = new EventType<>(Event.ANY, "REPORT_EVENT");
    public static final EventType<UserDeletedEvent> USER_DELETED_EVENT = new EventType<>(REPORT_EVENT, "USER_DELETED_EVENT");
    public static final EventType<ShowUserReviewsEvent> SHOW_USER_REVIEWS_EVENT = new EventType<>(REPORT_EVENT, "SHOW_USER_REVIEWS_EVENT");
    public static final EventType<ReportDeletedEvent> REPORT_DELETED_EVENT = new EventType<>(REPORT_EVENT, "REPORT_DELETED_EVENT");
    public static final EventType<SeeUserProfileClicked> SEE_USER_PROFILE_CLICKED = new EventType<>(REPORT_EVENT, "SEE_USER_PROFILE_CLICKED");

    private void deleteUser(MouseEvent event){
        Persistence dbManager = LayoutManagerFactory.getManager().dbManager;
        if(dbManager.deleteUser(report.reportedUser)){
            dbManager.deleteReport(report.reportedUser);
            dbManager.deleteRequest(report.reportedUser);
            UserDeletedEvent userDeletedEvent = new UserDeletedEvent(USER_DELETED_EVENT, this.report.reportedUser, this);
            this.fireEvent(userDeletedEvent);
        }
    }

    private void showReviews(MouseEvent event){
        ShowUserReviewsEvent showUserReviewsEvent = new ShowUserReviewsEvent(SHOW_USER_REVIEWS_EVENT, report);
        this.fireEvent(showUserReviewsEvent);
    }


}
