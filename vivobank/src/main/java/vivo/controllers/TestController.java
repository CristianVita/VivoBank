package vivo.controllers;

import java.io.IOException;

import vivo.firebase.DatabaseManager;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;

public class TestController {

    @FXML
    TextArea usernameBar;
    @FXML
    PasswordField passwordBar;

    @FXML
    private void addDataDB() throws IOException {
        String username = usernameBar.getText();
        String password = passwordBar.getText();

        if (!username.equals("") && !password.equals("")) {
            //DatabaseManager.getDatabaseManager().addData(username, password);
            usernameBar.setText("");
            passwordBar.setText("");
            System.out.println("Hai ca mearsă! E ce trebuie baieti!");
        } else {
            System.out.println("Please complete both fields!");
        }
        System.out.println("Clicked!");
    }
}
