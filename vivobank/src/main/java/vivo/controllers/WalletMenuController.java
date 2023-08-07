package vivo.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import vivo.App;
import vivo.entities.Transaction;
import vivo.entities.Wallet;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class WalletMenuController implements Initializable {
    @FXML
    private ListView<String> transactionsListView;

    @FXML
    private Label walletNameLabel;

    @FXML
    private Button addMoneyButton;

    @FXML
    private Button makeTransferButton;

    @FXML
    private Button backButton;

    @FXML
    void pressAddMoneyButton(MouseEvent event) {
        try {
            App.setRoot("add_money_window");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void pressBackButton(MouseEvent event) {
        try {
            App.setRoot("wallet_list_resources/wallet_list_menu");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void pressMakeTransferButton(MouseEvent event) {
        try {
            App.setRoot("make_transfer_window");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Wallet wallet = WalletListMenuController.getClickedWallet();
        ObservableList<String> data = FXCollections.observableArrayList();

        wallet.transactions.sort((o1, o2) -> o2.date.compareTo(o1.date));

        for (Transaction t : wallet.transactions)
            data.add(t.toString());

        walletNameLabel.setText(wallet.name);
        transactionsListView.setItems(data);
        System.out.println(transactionsListView);
    }
}
