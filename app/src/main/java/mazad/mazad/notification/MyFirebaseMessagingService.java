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
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import mazad.mazad.LoginActivity;
import mazad.mazad.R;
import mazad.mazad.SplashActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String notificationMessage = remoteMessage.getNotification().getBody();

        JSONObject jsonObject = new JSONObject(remoteMessage.getData());

        Log.i("message", jsonObject.toString());

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,"0");
        mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        mBuilder.setSmallIcon(R.drawable.im_logo);
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                R.drawable.im_logo));
        mBuilder.setStyle(new NotificationCompat.BigTextStyle()
                .bigText(notificationMessage));
        mBuilder.setContentTitle(remoteMessage.getNotification().getTitle());
        mBuilder.setContentText(notificationMessage);
        mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);

        Intent resultIntent = new Intent(this, SplashActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(LoginActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(0,mBuilder.build());

    }
}
