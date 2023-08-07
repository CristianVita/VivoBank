package vivo.entities;

import java.time.LocalDateTime;

public class Transaction {
    public String destCode;
    public String fromCode;
    public Double amount;
    public String date;
    public String usernameDest;

    public Transaction(String destCode, String fromCode, Double amount) {
        this.destCode = destCode;
        this.fromCode = fromCode;
        this.amount = amount;
        this.date = LocalDateTime.now().toString();
    }

    public Transaction(String destCode, String fromCode, Double amount, String date) {
        this.destCode = destCode;
        this.fromCode = fromCode;
        this.amount = amount;
        this.date = date;
    }

    @Override
    public String toString() {
        return "destination: " + destCode + '\n' +
                "source: " + fromCode + '\n' +
                "amount: " + amount + '\n' +
                "date: " + date;
    }
}
