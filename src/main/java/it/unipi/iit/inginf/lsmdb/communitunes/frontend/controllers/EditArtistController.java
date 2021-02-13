package it.unipi.iit.inginf.lsmdb.communitunes.frontend.controllers;

import it.unipi.iit.inginf.lsmdb.communitunes.authentication.AuthenticationFactory;
import it.unipi.iit.inginf.lsmdb.communitunes.authentication.AuthenticationManager;
import it.unipi.iit.inginf.lsmdb.communitunes.entities.Artist;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EditArtistController implements UIController {
    public TextField email;
    public PasswordField password;
    public TextField firstName;
    public TextField lastName;
    public TextField country;
    public DatePicker birthday;
    public TextField image;
    public TextField biography;
    public TextField activeYears;
    public TextField stageName;
    public Text msg;
    private LayoutManager manager;
    private AuthenticationManager authManager;
    private Persistence dbManager;
    private Artist artist;

    @Override
    public void init() {
        this.manager = LayoutManagerFactory.getManager();
        this.authManager = AuthenticationFactory.CreateAuthenticationManager();
        this.dbManager = PersistenceFactory.CreatePersistence();
        this.artist = manager.context.getAuthenticatedArtist();
        setDefaultValues();
    }

    public void closeEditWindow(MouseEvent mouseEvent) throws IOException {
        manager.setContent(Path.ARTIST_PROFILE);
    }

    public void cancelEdit(ActionEvent actionEvent) {
        setDefaultValues();
    }

    public void saveInfo(ActionEvent actionEvent) {
        EmailValidator emailValidator = EmailValidator.getInstance();
        if(!emailValidator.isValid(email.getText())){
            msg.setFill(Color.RED);
            msg.setText("Invalid email address. Please use a valid address.");
            return;
        }
        if(!password.getText().equals("")){
            artist.Password = authManager.securePassword(password.getText());
        }
        msg.setText("");
        artist.Email = email.getText();
        if(country.getText() == null || "".equals(country.getText())){
            artist.Country = null;
        }
        else{
            artist.Country = country.getText();
        }
        if(image.getText() == null || "".equals(image.getText())){
            artist.Image = null;
        }
        else{
            artist.Image = image.getText();
        }
        if(firstName.getText() == null || "".equals(firstName.getText())){
            artist.FirstName = null;
        }
        else{
            artist.FirstName = firstName.getText();
        }
        if(lastName.getText() == null || "".equals(lastName.getText())){
            artist.LastName = null;
        }
        else{
            artist.LastName = lastName.getText();
        }
        if(birthday.getValue() == null){
            artist.Birthday = null;
        }
        else{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            artist.Birthday = formatter.format(birthday.getValue());
        }

        if(biography.getText() == null || "".equals(biography.getText())){
            artist.Biography = null;
        }
        else{
            artist.Biography = biography.getText();
        }

        if(activeYears.getText() == null || "".equals(activeYears.getText())){
            artist.ActiveYears = null;
        }
        else{
            artist.ActiveYears = activeYears.getText();
        }

        if(stageName.getText() == null || "".equals(stageName.getText())){
            artist.StageName = null;
        }
        else{
            artist.StageName = stageName.getText();
        }


        if(dbManager.updateArtist(artist)){
            msg.setFill(Color.GREEN);
            msg.setText("The information was successfully updated.");
        }
        else{
            msg.setFill(Color.RED);
            msg.setText("An error occurred while updating the information. Please try again later.");
        }
    }

    private void setDefaultValues(){
        email.setText(artist.Email);
        country.setText(artist.Country);
        firstName.setText(artist.FirstName);
        lastName.setText(artist.LastName);
        if(artist.Birthday != null){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            birthday.setValue(LocalDate.parse(artist.Birthday, formatter));
        }
        image.setText(artist.Image);
        biography.setText(artist.Biography);
        activeYears.setText(artist.ActiveYears);
        stageName.setText(artist.StageName);
        password.setText("");
    }
}
