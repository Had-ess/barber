package com.youssef.barber.utils;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseUtils {
    private static final String TAG = "FirebaseUtils";

    // Authentication
    public static FirebaseAuth getAuth() {
        return FirebaseAuth.getInstance();
    }

    public static FirebaseUser getCurrentUser() {
        return getAuth().getCurrentUser();
    }

    public static String getCurrentUserId() {
        FirebaseUser user = getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    // Realtime Database
    public static DatabaseReference getDatabaseRef() {
        return FirebaseDatabase.getInstance().getReference();
    }

    public static DatabaseReference getUsersRef() {
        return getDatabaseRef().child("users");
    }

    public static DatabaseReference getCurrentUserRef() {
        String userId = getCurrentUserId();
        return userId != null ? getUsersRef().child(userId) : null;
    }

    public static DatabaseReference getBarbersRef() {
        return getDatabaseRef().child("barbers");
    }

    public static DatabaseReference getAppointmentsRef() {
        return getDatabaseRef().child("appointments");
    }

    public static DatabaseReference getUserAppointmentsRef(String userId) {
        return getDatabaseRef().child("user_appointments").child(userId);
    }

    // Storage
    public static StorageReference getStorageRef() {
        return FirebaseStorage.getInstance().getReference();
    }

    public static StorageReference getUserProfileImagesRef() {
        return getStorageRef().child("profile_images");
    }

    public static StorageReference getCurrentUserProfileImageRef() {
        String userId = getCurrentUserId();
        return userId != null ? getUserProfileImagesRef().child(userId + ".jpg") : null;
    }

    // Helper Methods
    public static boolean isUserLoggedIn() {
        return getCurrentUser() != null;
    }

    public static void signOut() {
        getAuth().signOut();
    }

    public static void logError(String operation, Exception e) {
        Log.e(TAG, operation + " failed: " + e.getMessage());
        e.printStackTrace();
    }

    public static String generatePushId() {
        return getDatabaseRef().push().getKey();
    }
}