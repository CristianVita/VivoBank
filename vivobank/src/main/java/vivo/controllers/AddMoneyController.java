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


public class AddMoneyController implements Initializable {
    @FXML
    private TextField inputCVV;

    @FXML
    private TextField inputAmount;

    @FXML
    private TextField inputCode;

    @FXML
    private Button goButton;

    @FXML
    private Button backButton;

    @FXML
    private Label msgLabel;

    @FXML
    void pushBackButton(MouseEvent event) throws IOException {
        String previous_scene_name = "wallet_menu";
        App.setRoot(previous_scene_name);
    }

    @FXML
    void goButtonPressed(MouseEvent event) {
        String serial_code = inputCode.getText();
        String amount = inputAmount.getText();
        String cvv = inputCVV.getText();
        if (serial_code.equals("") || amount.equals("") || cvv.equals("")){
            msgLabel.setText("Please complete all the fields correctly!");
        }else {
            String numberPattern = "([0-9]*)|([0-9]*)(\\.)([0-9]*)";
            boolean match = Pattern.matches(numberPattern, amount);

            String serialCodePattern = "^(\\d){4}-(\\d){4}-(\\d){4}-(\\d){4}$";
            boolean matchSC = Pattern.matches(serialCodePattern, serial_code);

            String cvvPattern = "^(\\d){3}$";
            boolean matchCVV = Pattern.matches(cvvPattern, cvv);

            if (!matchCVV) {
                msgLabel.setText("Please insert a valid cvv");
            } else if (!matchSC) {
                msgLabel.setText("Please insert a valid serial code");
            } else if (!match){
                msgLabel.setText("Amount is not a number, please insert a number.");
            } else {
                Double Amount = Double.valueOf(amount);
                try {
                    int ret = DatabaseManager.getDatabaseManager()
                            .addMoneyFromPhysicalCardToWallet(App.curr_user, WalletListMenuController.getClickedWallet(), Amount, serial_code, cvv);
                    if (ret == -1){
                        msgLabel.setText("Physical card with this serial code does not exist!");
                    }else if (ret == -3){
                        msgLabel.setText("The inserted CVV is not correct!");
                    }else if (ret == -2){
                        msgLabel.setText("The chosen card does not have sufficient money!");
                    }else {
                        msgLabel.setText("Your transaction has terminated succesfully!");
                        inputCode.setText("");
                        inputAmount.setText("");
                        inputCVV.setText("");
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

         inputCVV.setFocusTraversable(false);
         inputAmount.setFocusTraversable(false);
         inputCode.setFocusTraversable(false);
    }
}
