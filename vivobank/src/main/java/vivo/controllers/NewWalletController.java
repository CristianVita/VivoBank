package vivo.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import vivo.App;
import vivo.entities.Wallet;
import vivo.firebase.DatabaseManager;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class NewWalletController {

    public static boolean newWalletSuccesfully;

        @FXML
        private TextField InputWalletName;

        @FXML
        private Button DoneButton;

        @FXML
        private Button BackButton;

        @FXML
        void pushBackButton(MouseEvent event) throws IOException {
            String previous_scene_name = "main_menu";
            App.setRoot(previous_scene_name);
        }

        @FXML
        private Label NameConflictLabel;

        @FXML
        void pushDoneButton(MouseEvent event) throws IOException, ExecutionException, InterruptedException {

            Wallet w = new Wallet(InputWalletName.getText());

            //Tratam si evitam cazaul in care avem 2 portofele cu acelasi nume

            boolean walletsamename = false;

                for(Wallet wallet : App.wallets){
                    if(wallet.name.equals(w.name)){
                        walletsamename = true;
                }
            }
            if(walletsamename) {
                NameConflictLabel.setText("Your wallet name already exists. Please insert another name for this wallet");
            } else if(!w.name.equals("")){
                newWalletSuccesfully = true;
                App.wallets.add(w);
                DatabaseManager.getDatabaseManager().addWalletToUser(App.curr_user, w);
                String next_scene_name = "main_menu";
                App.setRoot(next_scene_name);
            } else {
                NameConflictLabel.setText("Please complete the name field");
            }
        }
}