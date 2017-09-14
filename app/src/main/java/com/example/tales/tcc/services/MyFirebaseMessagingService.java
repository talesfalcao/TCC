package com.example.tales.tcc.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.tales.tcc.Constants;
import com.example.tales.tcc.R;
import com.example.tales.tcc.activities.DrawerActivity;
import com.example.tales.tcc.activities.MainActivity;
import com.example.tales.tcc.db.PatternsModel;
import com.example.tales.tcc.db.UserLocModel;
import com.example.tales.tcc.db.UserSetPatternModel;
import com.example.tales.tcc.receivers.AdminReceiver;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import static com.example.tales.tcc.services.LocationService.parseTimeStamp;

/**
 * Created by tales on 07/08/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseMessagingServce";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String notificationTitle = null, notificationBody = null;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            notificationTitle = remoteMessage.getNotification().getTitle();
            notificationBody = remoteMessage.getNotification().getBody();// Also if you intend on generating your own notifications as a result of a received FCM

            // message, here is where that should be initiated. See sendNotification method below.
            sendNotification(notificationTitle, notificationBody);
        } else {
            Log.d(TAG, "AAAAAAAAAA");
            Map<String, String> map = remoteMessage.getData();
            String id = "", lat = "", lon = "", pats = "", name = "", inside = "", password = "", patternstr = "", disable = "";
            for (Map.Entry<String,String> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                // do stuff

                switch (key) {
                    case "id":
                        id = value;
                        break;
                    case "currentLati":
                        lat = value;
                        break;
                    case "currentLong":
                        lon = value;
                        break;
                    case "pats":
                        pats = value;
                        break;
                    case "name":
                        name = value;
                        break;
                    case "inside":
                        inside = value;
                        break;
                    case "password":
                        password = value;
                        break;
                    case "setPattern":
                        patternstr = value;
                        break;
                    case "disable":
                        disable = value;
                        break;
                }
            }

            if(id.isEmpty()) {
                if(!password.isEmpty()) {
                    password = password.substring(3);
                    Log.d(TAG, "PASSWORD: " + password);
                    if (password.isEmpty()) {
                        removePassword();
                    } else {
                        lockPassword(password);
                    }
                }
                if(!patternstr.isEmpty()) {
                    String[] split1 = patternstr.split("LAT=");
                    String[] split2 = split1[1].split("LONG=");
                    String[] split3 = split2[1].split("DAY=");
                    String[] split4 = split3[1].split("START=");
                    String[] split5 = split4[1].split("END=");
                    String newLat = split2[0];
                    String newLon = split3[0];
                    String day = split4[0];
                    String start = split5[0];
                    String end = split5[1];

                    UserSetPatternModel model = new UserSetPatternModel(start, end, day, newLat, newLon);
                    model.insertLocation(this);

                    Log.d(TAG, "dgs");
                }
                if(!disable.isEmpty()) {
                    SharedPreferences sharedPreferences = getSharedPreferences(Constants.family, Context.MODE_PRIVATE);
                    sharedPreferences.edit().putBoolean("disable", disable.equals("true")).apply();
                    Log.d(TAG, "aaa" + disable);
                }
            } else {
                Log.d(TAG, "Message Data Body: " + name + " " + lat + "  :  " + lon + " Inside > " + inside);
                Log.d(TAG, "PATTERNS: " + pats);

                final String[] stamp = parseTimeStamp();
                final int bottom = (Integer.parseInt(stamp[2]) / 15) * 15;

                String[] split1 = pats.split("latitude");
                PatternsModel.deleteAllByWeekdayHour(this, stamp[3], bottom + "");
                for(String s : split1) {
                    if(!s.isEmpty()) {
                        String[] res = s.split("longitude");
                        UserSetPatternModel p = new UserSetPatternModel(stamp[3], bottom + "", (bottom + 15) + "", res[0], res[1]);
                        p.insertLocation(this);
                    }
                }
                UserLocModel.deleteName(this, id);
                UserLocModel model = new UserLocModel(id, name, lat, lon, inside);
                model.insertLocation(this);
            }
        }
    }


    private void sendNotification(String notificationTitle, String notificationBody) {
        Intent intent = new Intent(this, DrawerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("OUTSIDE", true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setAutoCancel(true)   //Automatically delete the notification
                .setSmallIcon(R.mipmap.ic_launcher) //Notification icon
                .setContentIntent(pendingIntent)
                .setContentTitle(notificationTitle)
                .setContentText(notificationBody)
                .setSound(defaultSoundUri);


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

    private void lockPassword(String pw) {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager)getSystemService(DEVICE_POLICY_SERVICE);
        ComponentName demoDeviceAdmin = new ComponentName(this, AdminReceiver.class);

        devicePolicyManager.setPasswordQuality(
                demoDeviceAdmin,DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED);
        devicePolicyManager.setPasswordMinimumLength(demoDeviceAdmin, 5);

        boolean result = devicePolicyManager.resetPassword(pw,
                DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);

        devicePolicyManager.lockNow();
    }

    private void removePassword() {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager)getSystemService(DEVICE_POLICY_SERVICE);
        ComponentName demoDeviceAdmin = new ComponentName(this, AdminReceiver.class);

        boolean active = devicePolicyManager.isAdminActive(demoDeviceAdmin);

        if (active) {
            devicePolicyManager.setPasswordMinimumLength(demoDeviceAdmin, 0);
            boolean result = devicePolicyManager.resetPassword("", DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);

            devicePolicyManager.lockNow();
        }
    }
}