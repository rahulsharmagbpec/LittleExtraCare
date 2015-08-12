package littleextracare.bifortis.com.handler;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import littleextracare.bifortis.com.Constants.SharedPrefConstants;
import littleextracare.bifortis.com.activities.PushNotificationClickedActivity;
import littleextracare.bifortis.com.activities.R;
import littleextracare.bifortis.com.data.SharedPref;

@SuppressWarnings({"deprecation", "Convert2Lambda"})
public class GcmMessageHandler extends IntentService {


    private static final String TAG = GcmMessageHandler.class.getName();
    public GcmMessageHandler() {
        super("GcmMessageHandler");
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        Handler handler = new Handler();
    }
    @Override
    protected void onHandleIntent(Intent intent){
        Log.e(TAG, "Push Notification Received");
        String mySwitch = SharedPref.getData(getApplicationContext(), SharedPrefConstants.PREF_SWITCH);
        //intent.getExtras().getString("text");

        if(mySwitch != null) {
            if (mySwitch.equalsIgnoreCase("true")) {
                generateNotification(this, "message", 44);
            }
        }
    }

    /*
      Function to generate the Notification for the Questions.
    * */
    @SuppressLint("CommitPrefEdits")
    private void generateNotification(Context context, String message,int qid) {
        SharedPreferences settings = getSharedPreferences("notify", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("flag", false);
        editor.commit();
        int icon = getNotificationIcon();
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);

        String title = "Little Extra Care";

        //Intent notificationIntent = new Intent(context, LetsGoActivity.class);
        Intent notificationIntent = new Intent(context, PushNotificationClickedActivity.class);
        // set intent so it does not start a new activity
        notificationIntent.putExtra("qid", qid);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        // Play default notification sound
        notification.defaults |= Notification.DEFAULT_SOUND;

        // Vibrate if vibrate is enabled
        notification.defaults |= Notification.DEFAULT_VIBRATE;

        notificationManager.notify(1, notification);
    }

    private int getNotificationIcon() {
        boolean whiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return whiteIcon ? R.mipmap.ic_launcher : R.mipmap.ic_launcher;
    }
}