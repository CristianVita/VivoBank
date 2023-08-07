package vivo.entities;

import java.util.ArrayList;
import java.util.Random;

public class Wallet {
    public String name;
    public String code;
    public Double amount;
    public ArrayList<Transaction> transactions;

    public Wallet(String name) {
        this.name = name;
        this.code = generateNewWalletCode();
        this.amount = 0.0;
        this.transactions = new ArrayList<>();
    }

    public Wallet(String name, String code, Double amount) {
        this.name = name;
        this.code = code;
        this.amount = amount;
        this.transactions = new ArrayList<>();
    }

    private static String rand4NumberString() {
        return new Random().ints(48, 57)
                .limit(4)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public static String generateNewWalletCode() {
        Random random = new Random();

        return rand4NumberString() +
                "-" +
                rand4NumberString() +
                "-" +
                rand4NumberString() +
                "-" +
                rand4NumberString();
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", amount=" + amount +
                '}';
    }
}
