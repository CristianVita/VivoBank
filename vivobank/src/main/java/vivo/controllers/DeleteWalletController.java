package vivo.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import vivo.App;
import vivo.entities.Wallet;
import vivo.firebase.DatabaseManager;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

public class DeleteWalletController implements Initializable {
    private boolean WalletNameFound;
    @FXML
    private TextField WalletName;
    @FXML
    private Button DeleteButton;

    @FXML
    private Button backButton;

    @FXML
    private Label AttentionLabel;

    @FXML
    void onBackButtonPressed(MouseEvent event) {
        String next_scene_name = "main_menu";
        try {
            App.setRoot(next_scene_name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void ondeleteButtonPressed(MouseEvent event) {
        String walletname = WalletName.getText();
        for(Wallet w : App.wallets){
            if(w.name.equals(walletname)){
                WalletNameFound = true;
                if(w.amount > 0){
                    AttentionLabel.setText("Your wallet is not empty! Transfer your money to another wallet");
                } else {
                    try {
                        DatabaseManager.getDatabaseManager().deleteWalletOfUser(App.curr_user, w);
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    AttentionLabel.setText("Successfully deleted");
                    WalletName.setText("");
                    break;
                }
            }
        }
        if(!WalletNameFound) {
            AttentionLabel.setText("Wallet Name is not found!");
            WalletName.setText("");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        WalletName.setFocusTraversable(false);
        try {
            App.wallets = DatabaseManager.getDatabaseManager().getWalletsFromUser(App.curr_user);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
