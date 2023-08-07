package vivo.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import vivo.App;
import vivo.entities.Transaction;
import vivo.firebase.DatabaseManager;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;


public class MakeTransferController implements Initializable {
    @FXML
    private TextField inputDestination;

    @FXML
    private TextField inputAmount;

    @FXML
    private Button GoButton;

    @FXML
    private Button BackButton;

    @FXML
    private Label WarningMessage;

    @FXML
    private TextField inputRecUser;

    @FXML
    void pushBackButton(MouseEvent event) throws IOException {
        String previous_scene_name = "wallet_menu";
        App.setRoot(previous_scene_name);
    }

    @FXML
    void goButtonPressed(MouseEvent event) {
        String destinationCode = inputDestination.getText();
        String amount = inputAmount.getText();
        String RecieverUsername = inputRecUser.getText();
        if (destinationCode.equals("") || amount.equals("")) {
            WarningMessage.setText("Please complete all the fields correctly!");
        } else {
            String numberPattern = "([0-9]*)|([0-9]*)(\\.|,)([0-9]*)";
            boolean match = Pattern.matches(numberPattern, amount);

            String serialCodePattern = "^(\\d){4}-(\\d){4}-(\\d){4}-(\\d){4}$";
            boolean matchSC = Pattern.matches(serialCodePattern, destinationCode);

            if (!matchSC) {
                WarningMessage.setText("Please insert a valid serial code");
            } else if (!match) {
                WarningMessage.setText("Amount is not a number, please insert a number.");
            } else {
                Double Amount = Double.valueOf(amount);
                Transaction tr = new Transaction(destinationCode, WalletListMenuController.getClickedWallet().code, Amount);
                try {
                    int ret;
                    if (RecieverUsername.equals("")) {
                        ret = DatabaseManager.getDatabaseManager()
                                .addMoneyToOtherUserWalletOrPhyCard(App.curr_user, tr, null);
                    } else {
                        ret = DatabaseManager.getDatabaseManager()
                                .addMoneyToOtherUserWalletOrPhyCard(App.curr_user, tr, RecieverUsername);
                    }
                    if (ret == -1) {
                        WarningMessage.setText("Physical card/wallet with this serial code does not exist!");
                    } else if (ret == -3) {
                        WarningMessage.setText("Destination username is invalid!");
                    } else if (ret == -2) {
                        WarningMessage.setText("The chosen card/wallet does not have sufficient money!");
                    } else {
                        WarningMessage.setText("Your transaction has terminated succesfully!");
                        inputDestination.setText("");
                        inputAmount.setText("");
                        inputRecUser.setText("");
                        WalletListMenuController.getClickedWallet().transactions = (ArrayList<Transaction>) DatabaseManager.getDatabaseManager()
                                .getTransactionsOfWalletFromUser(App.curr_user, WalletListMenuController.getClickedWallet());
                    }
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        inputDestination.setFocusTraversable(false);
        inputAmount.setFocusTraversable(false);
        inputRecUser.setFocusTraversable(false);
    }
}
