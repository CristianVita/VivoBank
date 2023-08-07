package vivo.firebase;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import vivo.entities.Transaction;
import vivo.entities.User;
import vivo.entities.Wallet;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class DatabaseManager {
    private Firestore database;
    private static DatabaseManager databaseManager = null;

    private DatabaseManager(){
        database = FirebaseConnection.getDatabase();
    }

    public static DatabaseManager getDatabaseManager() {
        if (databaseManager == null) {
            databaseManager = new DatabaseManager();
        }

        return databaseManager;
    }

    /* Adauga un user nou in baza de date */
    public boolean addUser(User user) {
        boolean status = false;

        Map<String, Object> docData = new HashMap<>();

        docData.put("username", user.name);
        docData.put("password", user.pass);
        ApiFuture<WriteResult> future = database.collection("users")
                .document(user.CNP)
                .set(docData);
        try {
            System.out.println("Update time : " + future.get().getUpdateTime());
            status = true;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return status;
    }

    /* Verifica unicitatea CNP-ului */
    public boolean checkCNPUniqueness(String CNP) throws ExecutionException, InterruptedException {
        DocumentReference docRef = database.collection("users").document(CNP);
        ApiFuture<DocumentSnapshot> future = docRef.get();

        DocumentSnapshot document = future.get();

        return !document.exists();
    }

    /* Verifica unicitatea username-ului */
    public boolean checkUsernameUniqueness(String username) throws ExecutionException, InterruptedException {
        CollectionReference users = database.collection("users");
        Query query = users.whereEqualTo("username", username);
        ApiFuture<QuerySnapshot> querySnapshot  = query.get();

        List<QueryDocumentSnapshot> documents = null;
        documents = querySnapshot.get().getDocuments();

        return documents.size() == 0;
    }

    /* Adauga un nou portofel la lista de portofele
    * a user-ului */
    public void addWalletToUser(User user, Wallet wallet) throws ExecutionException, InterruptedException {
        Map<String, Object> docData = new HashMap<>();
        ApiFuture<WriteResult> future;

        docData.put("name", wallet.name);
        docData.put("amount", wallet.amount);

        future = database.collection("users")
                .document(user.CNP)
                .collection("wallets")
                .document(wallet.code)
                .set(docData);
        System.out.println("Update time : " + future.get().getUpdateTime());
    }

    /* Adauga un nou portofel la lista de portofele
     * a user-ului */
    public void deleteWalletToUser(User user, Wallet wallet) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> future;

        future = database.collection("users")
                .document(user.CNP)
                .collection("wallets")
                .document(wallet.code)
                .delete();
        System.out.println("Update time : " + future.get().getUpdateTime());
    }

    /* Sterge un portofel din lista de portofele
     * a user-ului */
    public void deleteWalletOfUser(User user, Wallet wallet) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> future;

        future = database.collection("users")
                .document(user.CNP)
                .collection("wallets")
                .document(wallet.code)
                .delete();
        System.out.println("Update time : " + future.get().getUpdateTime());
    }

    /* Verifica credentialele de autentificare ale user-ului */
    public boolean checkUserCredentials(User user) throws ExecutionException, InterruptedException {
        CollectionReference users = database.collection("users");
        Query query = users.whereEqualTo("username", user.name)
                .whereEqualTo("password", user.pass);
        ApiFuture<QuerySnapshot> querySnapshot  = query.get();
        List<QueryDocumentSnapshot> documents = null;

        documents = querySnapshot.get().getDocuments();

        if (documents.size() == 1) {
            DocumentReference dstUserDocRef = documents.get(0).getReference();
            ApiFuture<DocumentSnapshot> userDocSnap = dstUserDocRef.get();
            DocumentSnapshot userDoc = userDocSnap.get();

            user.CNP = userDoc.getId();

            return true;
        }

        return false;
    }

    public int addMoneyFromPhysicalCardToWallet(User user, Wallet wallet, Double amount, String phyCardCode, String phyCardCvv) throws ExecutionException, InterruptedException {
        CollectionReference dummy_cards = database.collection("dummy cards");
        CollectionReference users = database.collection("users");

        DocumentReference cardDocRef = dummy_cards.document(phyCardCode);
        ApiFuture<DocumentSnapshot> cardDocSnap = cardDocRef.get();

        DocumentReference walletDocRef = users.document(user.CNP).collection("wallets").document(wallet.code);
        if (!users.document(user.CNP).get().get().exists()){
            System.out.println("oh no");
        } else {
            System.out.println(users.document(user.CNP).get().get().getData());
        }
        ApiFuture<DocumentSnapshot> walletDocSnap = walletDocRef.get();

        /* Verific existenta cardului fizic */
        DocumentSnapshot cardDoc = cardDocSnap.get();

        if (!cardDoc.exists()) {
            System.out.println("Physical card doesn't exist!");
            return -1;
        }

        /* Preiau wallet-ul */
        DocumentSnapshot walletDoc = walletDocSnap.get();

        /* Extrag referinta catre colectia de tranzactii
         * ale wallet-ului destinatie */
        CollectionReference dstTransactionsRef = walletDocRef.collection("transactions");

        /* Creare Map cu detaliile tranzactiei */
        Map<String, Object> transData = new HashMap<>();
        transData.put("amount", amount);
        transData.put("date", LocalDateTime.now().toString());
        transData.put("destination", wallet.code);
        transData.put("source", phyCardCode);

        /* Copiat docs ca sa fie effectively final pt a putea fi
        * folosite in lambda function */
        ApiFuture<Integer> futureTransaction = database.runTransaction(transaction -> {
            /* Preiau suma de pe cardul fizic */
            Double cardAmount = cardDoc.getDouble("amount");
            Double walletAmount = walletDoc.getDouble("amount");
            String cardCvv = cardDoc.getString("cvv");

            assert cardCvv != null;
            if (!cardCvv.equals(phyCardCvv)) {
                System.out.println("Incorrect CVV!");
                return -3;
            }

            /* Verific daca suma pe care vreau sa o scot
             * de pe acesta este mai mare decat suma existenta */
            assert cardAmount != null;
            if (cardAmount < amount) {
                System.out.println("Physical card doesn't have enough money!");
                return -2;
            }

            transaction.update(cardDocRef, "amount", cardAmount - amount);
            assert walletAmount != null;
            transaction.update(walletDocRef, "amount", walletAmount + amount);
            dstTransactionsRef.add(transData);
            return 0;
        });

        return futureTransaction.get();
    }

    public List<Transaction> getTransactionsOfWalletFromUser(User user, Wallet wallet) throws ExecutionException, InterruptedException {
        ArrayList<Transaction> transactions = new ArrayList<>();
        CollectionReference walletsColRef = database.collection("users")
                .document(user.CNP)
                .collection("wallets");
        CollectionReference transactionsColRef = walletsColRef
                .document(wallet.code)
                .collection("transactions");
        ApiFuture<QuerySnapshot> futureTransactionsQuery = transactionsColRef.get();
        List<QueryDocumentSnapshot> transactionsDocs = futureTransactionsQuery.get().getDocuments();

        for (QueryDocumentSnapshot transaction : transactionsDocs) {
            Double amount = transaction.getDouble("amount");
            String date = transaction.getString("date");
            String dest = transaction.getString("destination");
            String src = transaction.getString("source");

            transactions.add(new Transaction(dest, src, amount, date));
        }

        return transactions;
    }

    public List<Wallet> getWalletsFromUser(User user) throws ExecutionException, InterruptedException {
        CollectionReference walletsColRef = database.collection("users").document(user.CNP).collection("wallets");
        ArrayList<Wallet> wallets = new ArrayList<>();

        ApiFuture<QuerySnapshot> future = walletsColRef.get();

        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        for (QueryDocumentSnapshot document : documents) {
            String name = document.getString("name");
            String code = document.getId();
            Double amount = document.getDouble("amount");

            Wallet w = new Wallet(name, code, amount);
            List<Transaction> transactions = getTransactionsOfWalletFromUser(user, w);

            w.transactions.addAll(transactions);

            wallets.add(w);
        }

        return wallets;
    }

    private int makeTransfer(DocumentReference source, DocumentReference destination, Transaction trans, boolean toPhysicalCard) throws ExecutionException, InterruptedException {
        /* Detalii tranzactie */
        String srcCode = trans.fromCode;
        String dstCode = trans.destCode;
        Double amount = trans.amount;
        String date = trans.date;

        /* Extrag documentul wallet-ului sursa */
        ApiFuture<DocumentSnapshot> srcDocSnap = source.get();
        DocumentSnapshot srcDoc = srcDocSnap.get();

        /* Extrag documentul wallet-ului destinatie */
        ApiFuture<DocumentSnapshot> dstDocSnap = destination.get();
        DocumentSnapshot dstDoc = dstDocSnap.get();

        /* Extrag referinta catre colectia de tranzactii
        * ale wallet-ului sursa */
        CollectionReference srcTransactionsRef = source.collection("transactions");

        /* Extrag referinta catre colectia de tranzactii
         * ale wallet-ului/cardului destinatie */
        CollectionReference dstTransactionsRef = null;

        /* Nu dorim retinerea tranzactiilor in bd a cardurilor fizice */
        if (!toPhysicalCard)
            dstTransactionsRef = destination.collection("transactions");

        /* Creare Map cu detaliile tranzactiei */
        Map<String, Object> transData = new HashMap<>();
        transData.put("amount", amount);
        transData.put("date", date);
        transData.put("destination", dstCode);
        transData.put("source", srcCode);

        /* Effectively final pt lambda */
        CollectionReference finalDstTransactionsRef = dstTransactionsRef;
        ApiFuture<Integer> futureTransaction = database.runTransaction(transaction -> {
            /* Preiau suma de la sursa */
            Double srcAmount = srcDoc.getDouble("amount");
            Double dstAmount = dstDoc.getDouble("amount");


            /* Verific daca suma pe care vreau sa o scot
             * de la sursa este mai mare decat suma existenta */
            assert srcAmount != null;
            if (srcAmount < amount) {
                System.out.println("Card/Wallet doesn't have enough money!");
                return -2;
            }

            transaction.update(source, "amount", srcAmount - amount);
            assert dstAmount != null;
            transaction.update(destination, "amount", dstAmount + amount);
            /* Adaug detaliile transferului la ambele entitati participante */
            srcTransactionsRef.add(transData);
            if (finalDstTransactionsRef != null)
                finalDstTransactionsRef.add(transData);
            return 0;
        });

        return futureTransaction.get();
    }

    public int addMoneyToOtherUserWalletOrPhyCard(User user, Transaction transaction, String dstUsername) throws ExecutionException, InterruptedException {
        String srcCode = transaction.fromCode;
        String dstCode = transaction.destCode;
        Double amount = transaction.amount;
        String date = transaction.date;

        /* Referinta catre wallet-ul sursa */
        DocumentReference srcWalletDocRef = database.collection("users")
                .document(user.CNP)
                .collection("wallets")
                .document(srcCode);

        /* Daca nu se face transfer catre wallet-ul
        * altui user, se face ori catre un wallet personal
        * ori catre un card fizic */
        if (dstUsername == null) {
            /* Verific daca transferul se face catre un wallet personal
            * verificand seria wallet-ului */
            DocumentReference dstWalletDocRef = database.collection("users")
                    .document(user.CNP)
                    .collection("wallets")
                    .document(dstCode);
            ApiFuture<DocumentSnapshot> futureQuery = dstWalletDocRef.get();

            /* Verfic daca exista vreun wallet personal
            * cu respectivul cod */
            DocumentSnapshot dstWalletDoc = futureQuery.get();
            if (dstWalletDoc.exists()) {
                return makeTransfer(srcWalletDocRef, dstWalletDocRef, transaction, false);
            }

            /* Verific daca transferul se face catre un card fizic */
            DocumentReference dstCardDocRef = database.collection("dummy cards")
                    .document(dstCode);
            futureQuery = dstCardDocRef.get();

            /* Verfic daca exista vreun card fizic
             * cu respectivul cod */
            DocumentSnapshot dstCardDoc = futureQuery.get();
            if (dstCardDoc.exists()) {
                return makeTransfer(srcWalletDocRef, dstCardDocRef, transaction, true);
            }

            /* Daca nu a fost gasit niciun wallet/card
            * cu acel cod, intorc un cod corespunzator */
            System.out.println("Unexistent wallet/card code");
            return -1;
        } else {
            /* Verific existenta userului cu username-ul respectiv */
            Query dstUserQuery = database.collection("users")
                    .whereEqualTo("username", dstUsername);
            ApiFuture<QuerySnapshot> querySnap = dstUserQuery.get();
            List<QueryDocumentSnapshot> userQueryDocSnaps = querySnap.get().getDocuments();

            if (userQueryDocSnaps.size() == 0) {
                System.out.println("User doesn't exist!");
                return -3;
            }

            /* Extrag o referinta catre documentul user-ului respectiv */
            DocumentReference dstUserDocRef = userQueryDocSnaps.get(0).getReference();

            /* Extrag o referinta catre documentul wallet-ului user-ului respectiv */
            DocumentReference dstWalletDocRef = dstUserDocRef
                    .collection("wallets")
                    .document(dstCode);
            ApiFuture<DocumentSnapshot> futureQuery = dstWalletDocRef.get();

            /* Verfic daca exista vreun wallet al user-ului
             * cu respectivul cod */
            DocumentSnapshot dstWalletDoc = futureQuery.get();
            if (dstWalletDoc.exists()) {
                /* Daca exista fac transferul */
                return makeTransfer(srcWalletDocRef, dstWalletDocRef, transaction, false);
            } else {
                System.out.println("User's wallet code not valid!");
                return -1;
            }
        }
    }
}

