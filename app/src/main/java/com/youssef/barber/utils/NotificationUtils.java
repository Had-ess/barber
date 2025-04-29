package com.youssef.barber.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.youssef.barber.R;
import com.youssef.barber.MainActivity;

public class NotificationUtils {
    private static final String CHANNEL_ID = "barber_app_channel";
    private static final String CHANNEL_NAME = "Barber App Notifications";
    private static final int NOTIFICATION_ID = 100;

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH);

            channel.setDescription("Barber appointment notifications");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300});

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    public static void showNotification(Context context, String title, String message) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_scissors)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent)
                        .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    public static void showAppointmentReminder(Context context, String customerName, String time) {
        String message = String.format("You have an appointment with %s at %s", customerName, time);
        showNotification(context, "Appointment Reminder", message);
    }

    public static void showBookingConfirmation(Context context, String barberName, String date) {
        String message = String.format("Your booking with %s on %s is confirmed", barberName, date);
        showNotification(context, "Booking Confirmed", message);
    }

    public static void showCancellationNotification(Context context, String barberName, String date) {
        String message = String.format("Your appointment with %s on %s was cancelled", barberName, date);
        showNotification(context, "Appointment Cancelled", message);
    }

    public static void clearAllNotifications(Context context) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }
}