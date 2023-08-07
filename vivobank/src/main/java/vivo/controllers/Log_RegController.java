package vivo.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import vivo.App;
import vivo.entities.User;
import vivo.firebase.DatabaseManager;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class Log_RegController implements Initializable {

    @FXML
    TextField username_field;
    @FXML
    Label attentionLabel;
    @FXML
    PasswordField pass_field;
    @FXML
    TextField cnp_field;


    public void pushLoginButton(ActionEvent event) throws IOException, ExecutionException, InterruptedException {


        App.curr_user =new User( username_field.getText(), pass_field.getText(),null);
        if ( App.curr_user.name.length()==0 ||  App.curr_user.pass.length()==0) {
            attentionLabel.setText("Complete all the fields to login!");
            return;
        }

        if (DatabaseManager.getDatabaseManager().checkUserCredentials(App.curr_user) == true) {
            System.out.println(App.curr_user.name);
            System.out.println(App.curr_user.pass);

            App.wallets = DatabaseManager.getDatabaseManager().getWalletsFromUser(App.curr_user);
            App.setRoot("main_menu");
            attentionLabel.setText("");
        } else {
            attentionLabel.setText("The username or password you passed is not correct!");
        }
    }


    public void pushBackButton(ActionEvent event)  throws IOException {
        String previous_scene_name=""; // set the name without .fxml
        App.setRoot(previous_scene_name);
    }


    public void pushRegisterButton(ActionEvent event) throws IOException, ExecutionException, InterruptedException {
        System.out.println("Activated");

        App.curr_user =new User( username_field.getText(), pass_field.getText(),cnp_field.getText());
        if ( App.curr_user.name.length()==0 ||  App.curr_user.pass.length()==0 ||  App.curr_user.CNP.length()==0) {
            attentionLabel.setText("Complete all the fields to register!");
            return;
        }
        if ( App.curr_user.CNP.length()!=13) {
            attentionLabel.setText("Complete CNP field!!");
            return;
        }

        String regexCNP= "^[1-9]\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])(0[1-9]|[1-4]\\d|5[0-2]|99)(00[1-9]|0[1-9]\\d|[1-9]\\d\\d)\\d$";
        boolean match = Pattern.matches(regexCNP, App.curr_user.CNP);
        // correctnes of CNP
        if (!match) {
            attentionLabel.setText("Not a valid CNP!");
            return;
        }


            if (DatabaseManager.getDatabaseManager().checkCNPUniqueness( App.curr_user.CNP)
            && DatabaseManager.getDatabaseManager().checkUsernameUniqueness( App.curr_user.name) ) {
                DatabaseManager.getDatabaseManager().addUser(App.curr_user);
                attentionLabel.setText("The User was succesfuly registered!");
            } else {
                attentionLabel.setText("The CNP or USER is used!");
            }
            username_field.setText("");
            pass_field.setText("");
            cnp_field.setText("");


    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        username_field.setFocusTraversable(false);
        pass_field.setFocusTraversable(false);
        cnp_field.setFocusTraversable(false);

    }
}
