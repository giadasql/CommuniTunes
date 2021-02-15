package it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers.user;

import it.unipi.iit.inginf.lsmdb.communitunes.entities.Request;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.User;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManager;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManagerFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.Path;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers.UIController;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.Persistence;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.PersistenceFactory;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import java.io.IOException;

public class RequestPage implements UIController {

    public Text startingMessage;
    public Text finalMessage;
    public TextField stageName;

    private User user;
    private LayoutManager manager;
    private Persistence dbManager;

    public void init(){
        this.manager = LayoutManagerFactory.getManager();
        user = manager.context.getFocusedUser();
        dbManager = PersistenceFactory.CreatePersistence();
    }

    public void closeWindow(MouseEvent mouseEvent) throws IOException {
        manager.setContent(Path.USER_EDIT);
    }

    public void sumbitRequest(MouseEvent mouseEvent) {
        startingMessage.setVisible(false);
        startingMessage.setManaged(false);
        if(stageName.getText().equals("")){
            finalMessage.setVisible(true);
            finalMessage.setManaged(true);
            finalMessage.setText("\nPlease choose a stage name.");
        }else if(dbManager.checkIfStageNameExists(stageName.getText())){
            finalMessage.setVisible(true);
            finalMessage.setManaged(true);
            finalMessage.setText("It appears that the stage name you choose is already in use.\nPlease choose a different stage name.");
        }else if (dbManager.checkIfRequestExists(user.username)){
            finalMessage.setVisible(true);
            finalMessage.setManaged(true);
            finalMessage.setText("You have already a pending request, we'll let you know our decision soon!");
        }
        else{
            finalMessage.setVisible(true);
            finalMessage.setManaged(true);
            if(dbManager.addRequest(new Request(null, user.username, stageName.getText()))){
                finalMessage.setText("Request correctly inserted! We'll give you news as soon as possible!");
            }
            else{
                finalMessage.setText("An unexpected error occurred, please try again in a few minutes.");
            }
        }
    }
}
