package mazad.mazad.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.renderscript.RenderScript;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Map;

import mazad.mazad.HomeActivity;
import mazad.mazad.LoginActivity;
import mazad.mazad.NewsActivity;
import mazad.mazad.R;
import mazad.mazad.SplashActivity;
import mazad.mazad.models.UserModel;
import mazad.mazad.utils.Helper;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (Helper.getNotificationSharedPreferences(this)) {

            Map<String,String> notificationMessage = remoteMessage.getData();

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "0");
            mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
            //mBuilder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
            //   R.drawable.im_logo));
            mBuilder.setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(notificationMessage.get("body")));
            mBuilder.setContentTitle(notificationMessage.get("title"));
            mBuilder.setContentText(notificationMessage.get("body"));
            mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
            int color = ContextCompat.getColor(this, android.R.color.white);
            mBuilder.setColor(color);

            Intent resultIntent = new Intent(this, SplashActivity.class);

            UserModel userModel = Helper.getUserSharedPreferences(this);

            if (notificationMessage.containsKey("targetScreen")){
                if (notificationMessage.get("targetScreen").equals("news")){
                    resultIntent = new Intent(this, NewsActivity.class).putExtra("user",userModel);
                } else {
                    resultIntent = new Intent(this, HomeActivity.class).putExtra("goToChat",true);
                }
            }

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(LoginActivity.class);

            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            int random = (int)System.currentTimeMillis();
            if (mNotificationManager != null) {
                mNotificationManager.notify(random, mBuilder.build());
            }

        }
    }
}
