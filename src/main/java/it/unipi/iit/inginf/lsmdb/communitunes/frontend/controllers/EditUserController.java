package it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers;

import it.unipi.iit.inginf.lsmdb.communitunes.authentication.AuthenticationFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.authentication.AuthenticationManager;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.User;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManager;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManagerFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.Path;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.Persistence;
import it.unipi.iit.inginf.lsmdb.communitunes.persistence.PersistenceFactory;
import javafx.event.ActionEvent;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.apache.commons.validator.routines.EmailValidator;

import java.io.IOException;

public class EditUserController implements UIController {
    public TextField email;
    public PasswordField password;
    public TextField firstName;
    public TextField lastName;
    public TextField country;
    public DatePicker birthday;
    public Text msg;
    public TextField image;

    private User user;
    private Persistence dbManager;
    private AuthenticationManager authManager;
    private LayoutManager manager;

    public void saveInfo(ActionEvent actionEvent) {
        EmailValidator emailValidator = EmailValidator.getInstance();
        if(!emailValidator.isValid(email.getText())){
            msg.setFill(Color.RED);
            msg.setText("Invalid email address. Please use a valid address.");
            return;
        }
        if(!password.getText().equals("")){
            user.Password = authManager.securePassword(password.getText());
        }
        msg.setText("");
        user.Email = email.getText();
        if(country.getText() == null || "".equals(country.getText())){
            user.Country = null;
        }
        else{
            user.Country = country.getText();
        }
        if(image.getText() == null || "".equals(image.getText())){
            user.Image = null;
        }
        else{
            user.Image = image.getText();
        }
        if(firstName.getText() == null || "".equals(firstName.getText())){
            user.FirstName = null;
        }
        else{
            user.FirstName = firstName.getText();
        }
        if(lastName.getText() == null || "".equals(lastName.getText())){
            user.LastName = null;
        }
        else{
            user.LastName = lastName.getText();
        }
        if(birthday.getValue() == null){
            user.Birthday = null;
        }
        else{
            user.Birthday = birthday.getValue();
        }
        if(dbManager.updateUser(user)){
            msg.setFill(Color.GREEN);
            msg.setText("The information was successfully updated.");
        }
        else{
            msg.setFill(Color.RED);
            msg.setText("An error occurred while updating the information. Please try again later.");
        }
    }

    public void cancelEdit(ActionEvent actionEvent) {
        setDefaultValues();
    }

    @Override
    public void init() {
        manager = LayoutManagerFactory.getManager();
        user = manager.context.getAuthenticatedUser();
        dbManager = PersistenceFactory.CreatePersistence();
        authManager = AuthenticationFactory.CreateAuthenticationManager();
        setDefaultValues();
    }

    public void closeEditWindow(MouseEvent mouseEvent) throws IOException {
        manager.setContent(Path.USER_PROFILE);
    }

    private void setDefaultValues(){
        email.setText(user.Email);
        country.setText(user.Country);
        firstName.setText(user.FirstName);
        lastName.setText(user.LastName);
        birthday.setValue(user.Birthday);
        image.setText(user.Image);
        password.setText("");
    }

    public void goToRequest() throws IOException {
        manager.setContent(Path.ARTIST_REQUEST_PAGE);
    }
}
