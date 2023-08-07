package vivo.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import vivo.App;
import vivo.entities.Transaction;
import vivo.entities.Wallet;
import vivo.firebase.DatabaseManager;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

interface WalletListener {
    public void onClickListener(Wallet wallet);
}

public class WalletListMenuController implements Initializable {
    @FXML
    private ScrollPane scrollPane;

    @FXML
    private GridPane gridPane;

    @FXML
    private Button backButton;

    private List<Wallet> wallets;

    private WalletListener walletListener;

    private static Wallet selectedWallet;

    public static Wallet getClickedWallet() {
        return selectedWallet;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            App.wallets = DatabaseManager.getDatabaseManager().getWalletsFromUser(App.curr_user);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        int row = 0;
        int column = 1;

        walletListener = new WalletListener() {
            @Override
            public void onClickListener(Wallet wallet) {
                try {
                    System.out.println("Wallet clicked details: " + wallet.name + " " + wallet.amount + " " + wallet.code);
                    selectedWallet = wallet;
                    App.setRoot("wallet_menu");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        App.wallets.sort(Comparator.comparing(wallet -> wallet.name));

        for (Wallet wallet : App.wallets) {
            try {
                FXMLLoader fxmlLoader = App.getFXMLLoader("wallet_list_resources/Wallet");
                AnchorPane anchorPane = fxmlLoader.load();

                WalletController walletController = fxmlLoader.getController();
                walletController.setData(wallet, walletListener);

                if (column == 4) {
                    column = 1;
                    row++;
                }

                gridPane.add(anchorPane, column++, row);
                /* Setez latimea unui grid */
                gridPane.setMinWidth(Region.USE_COMPUTED_SIZE);
                gridPane.setPrefWidth(Region.USE_COMPUTED_SIZE);
                gridPane.setMaxWidth(Region.USE_PREF_SIZE);

                /* Setez inaltimea unui grid */
                gridPane.setMinHeight(Region.USE_COMPUTED_SIZE);
                gridPane.setPrefHeight(Region.USE_COMPUTED_SIZE);
                gridPane.setMaxHeight(Region.USE_PREF_SIZE);

                GridPane.setMargin(anchorPane, new Insets(10));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void backButtonPressed(MouseEvent event) {
        try {
            App.setRoot("main_menu");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
