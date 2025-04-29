package com.youssef.barber;

import android.app.Application;
import com.youssef.barber.utils.NotificationUtils;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        // Example Firebase setup (if necessary)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        // Create notification channel
        NotificationUtils.createNotificationChannel(this);

        // Other global initializations can go here
    }
}
