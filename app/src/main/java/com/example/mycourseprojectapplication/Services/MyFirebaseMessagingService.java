package com.example.mycourseprojectapplication.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.navigation.NavDeepLinkBuilder;

import com.example.mycourseprojectapplication.Activities.MainActivity;
import com.example.mycourseprojectapplication.R;
import com.example.mycourseprojectapplication.Utilities.UserSession;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private int numMessages = 0;
    private UserSession userSession;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        userSession = new UserSession(this);
        userSession.setNotificationKey(s);
        Log.d("Service", "Refreshed token: " + s);


    }


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        userSession = new UserSession(this);
        boolean isLoggedIn = userSession.isLoggedIn();
        if(isLoggedIn) {
            if (remoteMessage != null) {
                Map<String, String> data = remoteMessage.getData();
                Log.d("FROM", remoteMessage.getFrom());
                sendNotification(data);
            }
        }
        {

            return;
        }
    }

    private void sendNotification(Map<String,String> map) {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("FromNotification", true);
        intent.putExtras(bundle);
        PendingIntent pendingIntent = new NavDeepLinkBuilder(this).setComponentName(MainActivity.class).setGraph(R.navigation.mobile_navigation).setDestination(R.id.nav_orders).setArguments(bundle).createPendingIntent();
        PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
       // pendingIntent = new NavDeepLinkBuilder(this).setComponentName(MainActivity.class).setGraph(R.navigation.nav_graph).setDestination(R.id.nav_orders).setArguments(bundle).createPendingIntent();
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "ArTech")
                .setContentTitle(map.get("title"))
                .setContentText(map.get("message"))
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setLights(Color.RED, 1000, 300)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setNumber(++numMessages)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_PROMO)
                .setFullScreenIntent(pendingIntent, true)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_outline_shop_24_black);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "ArTech", "ArTech", NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("ArTech");
            channel.setShowBadge(true);
            channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
            channel.canShowBadge();
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});

            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }

        assert notificationManager != null;
        notificationManager.notify(0, notificationBuilder.build());
    }

}
