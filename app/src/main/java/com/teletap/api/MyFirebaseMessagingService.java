package com.teletap.api;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.teletap.R;
import com.teletap.activity.HomeActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import static android.content.Context.NOTIFICATION_SERVICE;
import static android.os.Build.VERSION;
import static android.os.Build.VERSION_CODES;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    String channelId = "channel2";
    String channelName = "Channeln2";
    int importance = NotificationManager.IMPORTANCE_HIGH;
    NotificationManager notificationManager;
    AudioAttributes attributes;
    NotificationChannel mChannel;
    NotificationCompat.Builder builder;

    private static final String TAG = "MyFirebaseMsgService";
    public static final String NOTIF_CHANNEL_ID = "my_channel_01";
    JSONObject json;

   //private SignupModel.UserBean userInfo;

    /*Notification type
            1. Registration - 1
            2. Superlike - 2
            3. Matched - 3
            4. Chat - 4
            5. Match%/ recommended - 5*/
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        //try {
            //sendNotification(remoteMessage.getData());
            String messageData = remoteMessage.getData().toString();
        /*try {
            userInfo = AppPreference.getUserInfo(getApplicationContext());
        }
        catch ( NullPointerException e){
            e.printStackTrace();
        }*/
            makeNotification(messageData);
        /*} catch (JSONException e) {
            e.printStackTrace();
        }*/

    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d("token","token "+s);
    }

    private void makeNotification(String messageData){
        try {
            JSONObject jsonObject = new JSONObject(messageData);

            /*{"data":{"sound":"default","title":"newTest","body":"Just For Testing..!-ANDROID"}}*/

            Log.e("fcm_message", String.valueOf(jsonObject));

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

            JSONObject dataJsonObject = jsonObject.getJSONObject("data");

            simpleNotification();
            if (builder!=null) {
                builder.setContentTitle(dataJsonObject.getString("title"));
                builder.setContentText(dataJsonObject.getString("body"));
            }
            //if (userInfo.getUser_id()!=0) {
                Intent intent = new Intent(this, HomeActivity.class);
                intent.putExtra("type",dataJsonObject.getString("type"));
                intent.putExtra("cameFrom", "notification");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                /*if (isAppIsInBackground(this)){*/
                stackBuilder.addNextIntent(intent);
                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(resultPendingIntent);
            //}
            /*}else{
                startActivity(intent);
            }*/

            builder.setAutoCancel(true);
            notificationManager.notify(1, builder.build());

        }catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(Map<String, String> data) throws JSONException {
        Log.e("fcm_message", String.valueOf(data));
        Intent intent = new Intent();
        try {

        }catch (NullPointerException e){
            e.printStackTrace();
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder =  new NotificationCompat.Builder(getApplicationContext(), NOTIF_CHANNEL_ID)
                .setContentTitle(json.getString("title"))
                .setContentText(json.getString("body"))
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setContentIntent(pendingIntent);

        notificationBuilder.setSmallIcon(getNotificationIcon(notificationBuilder), 1);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationChannel channel;
        wakeUpLock();
        if (VERSION.SDK_INT >= VERSION_CODES.O)
        {
            String channelId = NOTIF_CHANNEL_ID;
             channel = new NotificationChannel(
                    channelId,
                    getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT);

            notificationManager.createNotificationChannel(channel);
            notificationBuilder.setChannelId(channelId);

            notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle());
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        }
        notificationManager.notify(0, notificationBuilder.build());

    }
    private void wakeUpLock()
    {
        PowerManager pm = (PowerManager)this.getSystemService(Context.POWER_SERVICE);

        boolean isScreenOn = pm.isScreenOn();

        Log.i(TAG, "screen on: "+ isScreenOn);

        if(!isScreenOn)
        {
            Log.i(TAG, "screen on if: "+ isScreenOn);
//            wl_cpu.acquire(10000);
        }
    }

    private void simpleNotification(){
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            mChannel.setSound(soundUri,attributes);
            mChannel.setVibrationPattern(new long[]{1000, 1000, 1000, 1000, 1000});
            mChannel.enableLights(true);
            mChannel.enableVibration(true);
            mChannel.setShowBadge(true);

            if (notificationManager != null){
                notificationManager.createNotificationChannel(mChannel);
            }
        }

        builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.app_icon)
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setSound(soundUri)
                .setAutoCancel(true)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL);
    }

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        assert am != null;
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                for (String activeProcess : processInfo.pkgList) {
                    if (activeProcess.equals(context.getPackageName())) {
                        isInBackground = false;
                    }
                }
            }
        }

        return isInBackground;
    }

    private int getNotificationIcon(NotificationCompat.Builder notificationBuilder) {
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP){
            notificationBuilder.setColor(ContextCompat.getColor(getApplicationContext(),R.color.colorPrimary));
            return R.mipmap.app_icon;
        }else {
            return R.mipmap.app_icon;
        }
    }


//FOR FUTURE REFERENCE

//    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
//
//    private NotificationUtils notificationUtils;
//
//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//        utils.print(TAG, "From: " + remoteMessage.getFrom());
//
//        if (remoteMessage == null)
//            return;
//
//        // Check if message contains a notification payload.
//        if (remoteMessage.getNotification() != null) {
//            utils.print(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
//            handleNotification(remoteMessage.getNotification().getBody());
//        }
//
//        // Check if message contains a data payload.
//        if (remoteMessage.getData().size() > 0) {
//            utils.print(TAG, "Data Payload: " + remoteMessage.getData().toString());
//
//            try {
//                JSONObject json = new JSONObject(remoteMessage.getData().toString());
//                handleDataMessage(json);
//            } catch (Exception e) {
//                utils.print(TAG, "Exception: " + e.getMessage());
//            }
//        }
//    }
//
//    private void handleNotification(String message) {
//        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
//            // app is in foreground, broadcast the push message
//            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
//            pushNotification.putExtra("message", message);
//            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
//
//            // play notification sound
//            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
//            notificationUtils.playNotificationSound();
//        }else{
//            // If the app is in background, firebase itself handles the notification
//        }
//    }
//
//    private void handleDataMessage(JSONObject json) {
//        utils.print(TAG, "push json: " + json.toString());
//
//        try {
//            JSONObject data = json.getJSONObject("data");
//
//            String title = data.getString("title");
//            String message = data.getString("message");
//            boolean isBackground = data.getBoolean("is_background");
//            String imageUrl = data.getString("image");
//            String timestamp = data.getString("timestamp");
//            JSONObject payload = data.getJSONObject("payload");
//
//            utils.print(TAG, "title: " + title);
//            utils.print(TAG, "message: " + message);
//            utils.print(TAG, "isBackground: " + isBackground);
//            utils.print(TAG, "payload: " + payload.toString());
//            utils.print(TAG, "imageUrl: " + imageUrl);
//            utils.print(TAG, "timestamp: " + timestamp);
//
//
//            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
//                // app is in foreground, broadcast the push message
//                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
//                pushNotification.putExtra("message", message);
//                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
//
//                // play notification sound
//                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
//                notificationUtils.playNotificationSound();
//            } else {
//                // app is in background, show the notification in notification tray
//                Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
//                resultIntent.putExtra("message", message);
//
//                // check for image attachment
//                if (TextUtils.isEmpty(imageUrl)) {
//                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
//                } else {
//                    // image is present, show notification with image
//                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
//                }
//            }
//        } catch (JSONException e) {
//            utils.print(TAG, "Json Exception: " + e.getMessage());
//        } catch (Exception e) {
//            utils.print(TAG, "Exception: " + e.getMessage());
//        }
//    }
//
//    /**
//     * Showing notification with text only
//     */
//    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
//        notificationUtils = new NotificationUtils(context);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
//    }
//
//    /**
//     * Showing notification with text and image
//     */
//    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
//        notificationUtils = new NotificationUtils(context);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
//    }
}
