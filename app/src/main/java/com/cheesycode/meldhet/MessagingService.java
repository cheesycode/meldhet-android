package com.cheesycode.meldhet;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessagingService  extends FirebaseMessagingService {
    public static final String NOTIFICATION_REPLY = "NotificationReply";
    public static final String CHANNNEL_ID = "MeldingenVanDeGemeente";
    public static final String CHANNEL_NAME = "MeldingenVanDeGemeente";
    public static final String CHANNEL_DESC = "Hiermee ontvangt u meldingen van de gemeente omtrend de status van uw meldingen";
    public static final int REQUEST_CODE_REPLY = 101;
    public static final int NOTIFICATION_ID = 200;



    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        prepareNotificationShizzle();
        String issue = remoteMessage.getData().get(getString(R.string.missue));
        String sender = remoteMessage.getData().get(getString(R.string.msender));
        String body = remoteMessage.getData().get(getString(R.string.mbody));
        displayNotification(issue, sender, body);
    }
    private void prepareNotificationShizzle(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNNEL_ID, CHANNEL_NAME, importance);
            mChannel.setDescription(CHANNEL_DESC);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }


    public void displayNotification(String issue, String sender, String body) {
        RemoteInput remoteInput = new RemoteInput.Builder(NOTIFICATION_REPLY)
                .setLabel("Uw bericht...")
                .build();

        PendingIntent sendMessagge = PendingIntent.getBroadcast(
                this,
                REQUEST_CODE_REPLY,
                new Intent(this, MyReceiver.class)
                        .putExtra(getString(R.string.missue), issue)
                        .putExtra(getString(R.string.msender), sender)
                        .putExtra(getString(R.string.mid), getToken(this)),
                PendingIntent.FLAG_UPDATE_CURRENT
        );


        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(getString(R.string.issueid),issue);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1234567, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(android.R.drawable.ic_menu_send,
                        getString(R.string.reply), sendMessagge)
                        .addRemoteInput(remoteInput)
                        .build();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNNEL_ID)
                .setSmallIcon(R.mipmap.launcher_icon_test)
                .setContentTitle(getString(R.string.newmessagefrom) + sender)
                .setContentText(((body.length()>=40)?body.substring(0,40): body.substring(0,body.length()-1)) + "...")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(body))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .addAction(action);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d("NEW_TOKEN",s);
        getSharedPreferences("_", MODE_PRIVATE).edit().putString(getString(R.string.firebase), s).apply();
    }

    public static String getToken(Context context) {
        return context.getSharedPreferences("_", MODE_PRIVATE).getString(context.getString(R.string.firebase), "empty");
    }
}
