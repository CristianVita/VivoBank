package vivo.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;
import java.io.IOException;

public class FirebaseConnection {
    private static Firestore firestore = null;
    private static String FIRESTORE_PRIVATE_KEY = "src/main/resources/vivo/firebase_key/firebase_key.json";

    private static Firestore initConnection() {
        FirebaseOptions options = null;

        try {
            FileInputStream serviceAccount =
                    new FileInputStream(FIRESTORE_PRIVATE_KEY);

            options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FirebaseApp.initializeApp(options);

        return FirestoreClient.getFirestore();
    }

    public static Firestore getDatabase() {
        if (firestore == null) {
            firestore = initConnection();
        }

        return firestore;
    }
}

