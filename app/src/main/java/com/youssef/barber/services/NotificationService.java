package com.youssef.barber.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.youssef.barber.R;
import com.youssef.barber.MainActivity;

public class NotificationService {
    private static final String CHANNEL_ID = "barber_app_channel";
    private static final String CHANNEL_NAME = "Barber App Notifications";
    private static final int NOTIFICATION_ID = 100;

    private final Context context;
    private final NotificationManager notificationManager;

    public NotificationService(Context context) {
        this.context = context;
        this.notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Barber appointment notifications");
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void showAppointmentNotification(String title, String message) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_scissors)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    public void showReminderNotification(String customerName, String appointmentTime) {
        String message = String.format("You have an appointment with %s at %s",
                customerName, appointmentTime);

        showAppointmentNotification("Appointment Reminder", message);
    }

    public void showCancellationNotification(String customerName) {
        String message = String.format("%s has cancelled their appointment", customerName);
        showAppointmentNotification("Appointment Cancelled", message);
    }

    public void clearAllNotifications() {
        notificationManager.cancelAll();
    }
}