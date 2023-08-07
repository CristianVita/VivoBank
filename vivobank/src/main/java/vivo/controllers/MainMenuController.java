package vivo.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import vivo.App;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {
    public boolean deleteAction;

    @FXML
    private Button SelectWalletBtn;

    @FXML
    private Button LogOutBtn;

    @FXML
    private Button NewWalletBtn;

    @FXML
    private Button DeleteWalletBtn;

    @FXML
    public Label successfullyNewWallet;

    @FXML
    void logout(MouseEvent event) throws IOException {
        String previous_scene_name = "login_auth";
        App.setRoot(previous_scene_name);
    }

    @FXML
    void makenewWallet(MouseEvent event) throws IOException {
        String previous_scene_name = "new_wallet_window";
        App.setRoot(previous_scene_name);
    }

    @FXML
    void makeDeleteWallet(MouseEvent event){
        String next_scene_name = "DeleteWallet";
        try {
            App.setRoot(next_scene_name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void makeSelectWallet(MouseEvent event) throws IOException {
        String previous_scene_name = "wallet_list_resources/wallet_list_menu";
        App.setRoot(previous_scene_name);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if(NewWalletController.newWalletSuccesfully){
            successfullyNewWallet.setText("New wallet was succesfully added");
            NewWalletController.newWalletSuccesfully = false;
        }
    }
}
