package it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers.user;

import it.unipi.iit.inginf.lsmdb.communitunes.authentication.AuthenticationFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.authentication.AuthenticationManager;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.User;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManager;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.LayoutManagerFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.context.Path;
import it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers.UIController;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
            user.password = authManager.securePassword(password.getText());
        }
        msg.setText("");
        user.string = email.getText();
        if(country.getText() == null || "".equals(country.getText())){
            user.country = null;
        }
        else{
            user.country = country.getText();
        }
        if(image.getText() == null || "".equals(image.getText())){
            user.image = null;
        }
        else{
            user.image = image.getText();
        }
        if(firstName.getText() == null || "".equals(firstName.getText())){
            user.firstName = null;
        }
        else{
            user.firstName = firstName.getText();
        }
        if(lastName.getText() == null || "".equals(lastName.getText())){
            user.lastName = null;
        }
        else{
            user.lastName = lastName.getText();
        }
        if(birthday.getValue() == null){
            user.birthday = null;
        }
        else{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            user.birthday = formatter.format(birthday.getValue());
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
        dbManager = manager.dbManager;
        authManager = AuthenticationFactory.CreateAuthenticationManager();
        setDefaultValues();
    }

    public void closeEditWindow(MouseEvent mouseEvent) throws IOException {
        manager.setContent(Path.USER_PROFILE);
    }

    private void setDefaultValues() {
        email.setText(user.string);
        country.setText(user.country);
        firstName.setText(user.firstName);
        lastName.setText(user.lastName);
        if(user.birthday != null){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            birthday.setValue(LocalDate.parse(user.birthday, formatter));
        }
        image.setText(user.image);
        password.setText("");
    }

    public void goToRequest() throws IOException {
        manager.setContent(Path.ARTIST_REQUEST_PAGE);
    }
}
