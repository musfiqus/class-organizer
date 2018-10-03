package bd.edu.daffodilvarsity.classorganizer.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.service.UpdateService;
import bd.edu.daffodilvarsity.classorganizer.ui.main.MainActivity;
import bd.edu.daffodilvarsity.classorganizer.model.UpdateResponse;

public class UpdateNotificationHelper {
    private static final String TAG = "UpdateNotificationHelpe";

    public static final int UPDATE_NOTIFICATION_REQUEST_CODE = 42096;
    public static final String UPDATE_CHANNEL_ID = "notification_channel_02";

    private Context mContext;
    private UpdateResponse mUpdateResponse;

    public UpdateNotificationHelper(Context mContext, @NonNull UpdateResponse mUpdateResponse) {
        this.mContext = mContext;
        this.mUpdateResponse = mUpdateResponse;
    }

    public void showUpdateNotification(String title, String message) {
        //if no message or title is provided, use default ones
        if (title == null) {
            title = "Routine Update";
        }
        if (message == null) {
            message = "A new routine is available for download!";
        }
        Intent notificationIntent = new Intent(mContext, MainActivity.class);
        notificationIntent.putExtra(UpdateService.TAG_UPDATE_RESPONSE, mUpdateResponse);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, UPDATE_NOTIFICATION_REQUEST_CODE, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, UPDATE_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setTicker(mContext.getString(R.string.routine_update_ticker))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                .setSmallIcon(R.drawable.icon_silhouette)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .addAction(getUpdateAction());


        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {

            //Create notification channel if OREO
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(UPDATE_CHANNEL_ID, mContext.getResources().getString(R.string.notification_channel_name), NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }

            notificationManager.notify(UPDATE_NOTIFICATION_REQUEST_CODE, builder.build());
        }
    }

    private NotificationCompat.Action getUpdateAction() {
        Log.d(TAG, "getUpdateAction: update action created");
        Intent updateIntent = new Intent(mContext, UpdateService.class);
        updateIntent.putExtra(UpdateService.TAG_UPDATE_RESPONSE, mUpdateResponse);

        PendingIntent mutePendingIntent = PendingIntent.getService(mContext, UPDATE_NOTIFICATION_REQUEST_CODE, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        return new NotificationCompat.Action.Builder(0, mContext.getString(R.string.update_action), mutePendingIntent).build();
    }
}
