package vivo.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import vivo.entities.Wallet;

public class WalletController {

    @FXML
    private Label walletName;

    @FXML
    private Label walletAmount;

    @FXML
    private Label walletCode;

    private Wallet wallet;

    private WalletListener listener;

    @FXML
    private void clickOnWallet(MouseEvent event) {
        listener.onClickListener(wallet);
    }

    public void setData(Wallet wallet, WalletListener listener) {
        this.wallet = wallet;
        this.walletName.setText(wallet.name);
        this.walletAmount.setText(String.valueOf(wallet.amount));
        this.walletCode.setText(wallet.code);
        this.listener = listener;
    }
}
