package com.zeeroapps.wssp.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.zeeroapps.wssp.R;
import com.zeeroapps.wssp.activities.DrawerActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private final String TAG = "MyApp_FirebaseMsg";
    private int notificationID = 1;
    private String complaintNo;
    private static final String ADMIN_CHANNEL_ID ="admin_channel";
    NotificationManager notificationManager;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        //You should use an actual ID instead
        super.onMessageReceived(remoteMessage);
        
        if (remoteMessage.getNotification() != null){
            sendNotification(remoteMessage.getNotification().getBody());
        }

        if (remoteMessage.getData().size() > 0){
            parseData(remoteMessage.getData().toString());
        }
    }

    private void parseData(String response){
        try {
            JSONObject jObj = new JSONObject(response);
            JSONObject jObjData = jObj.getJSONObject("data");
            complaintNo = jObjData.getString("title");
            String msg = jObjData.getString("message");
            sendNotification(msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendNotification(String msg){
        notificationID += 1;
        String notificationMessage = msg;
        Log.d("message", msg);
        if (msg.toLowerCase().contains("pendingreview")){
            notificationMessage = "Your complaint is waiting for the reviews.";
        } else if (msg.toLowerCase().contains("inprogress")){
            notificationMessage = "Your complaint is under processing.";
        }else if (msg.toLowerCase().contains("completed")){
            notificationMessage = "Your complaint is now completed!";
        }



        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            setupChannels();
        }
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this,ADMIN_CHANNEL_ID);
        nBuilder.setSmallIcon(R.drawable.ic_stat_notification)
                .setAutoCancel(true)
                .setContentTitle("Complaint status changed")
                .setContentText(notificationMessage)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationMessage));

        Intent intent = new Intent(this, DrawerActivity.class);
        intent.putExtra("CALLED_FROM_THANK_YOU_ACTIVITY", true);
        intent.putExtra("COMPLAINT_NUMBER", complaintNo);
//
               TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(DrawerActivity.class);
            stackBuilder.addNextIntent(intent);
//
            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        nBuilder.setContentIntent(pendingIntent);
        notificationManager.notify(notificationID, nBuilder.build());

        
        //////////////////////////////////////////////


//        NotificationCompat.Builder notificationBuilder =
//                new NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
//                        .setSmallIcon(R.mipmap.ic_launcher)
//                        .setAutoCancel(true)
//                        .setContentTitle("Complaint status changed")
//                        .setContentText(notificationMessage)
//                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
//                        .setContentIntent(pendingIntent);
//
//        notificationManager.notify(notificationID, notificationBuilder.build());

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels(){
        CharSequence adminChannelName = getString(R.string.app_name);
        String adminChannelDescription = getString(R.string.app_name);

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_LOW);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.WHITE);
        adminChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }

}
