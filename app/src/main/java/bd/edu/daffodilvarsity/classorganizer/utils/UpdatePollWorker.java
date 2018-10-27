package bd.edu.daffodilvarsity.classorganizer.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import androidx.work.Worker;
import androidx.work.WorkerParameters;
import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.data.Repository;
import bd.edu.daffodilvarsity.classorganizer.model.UpdateResponse;
import bd.edu.daffodilvarsity.classorganizer.service.UpdateService;
import bd.edu.daffodilvarsity.classorganizer.ui.main.MainActivity;

public class UpdatePollWorker extends Worker {

    private static final String TAG = "UpdatePollWorker";

    public static final int UPDATE_NOTIFICATION_REQUEST_CODE = 42096;
    public static final String UPDATE_CHANNEL_ID = "notification_channel_02";

    public UpdatePollWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Repository repository = Repository.getInstance();
        try {
            UpdateResponse updateResponse = repository.getUpdateResponse().blockingGet();
            Log.e(TAG, "doWork: Response: "+ updateResponse.toString());
            if (updateResponse.getVersion() > PreferenceGetter.getDatabaseVersion()) {
                showNotification();
            }
            return Result.SUCCESS;
        } catch (RuntimeException e) {
            e.printStackTrace();
            return Result.RETRY;
        }
    }

    private void showNotification() {
        Log.e(TAG, "showNotification: WEEEEEEEEEEEEW");
        String title = "Routine Update";
        String message = "A new routine is available for download!";

        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), UPDATE_NOTIFICATION_REQUEST_CODE, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), UPDATE_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setTicker(getApplicationContext().getString(R.string.routine_update_ticker))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                .setSmallIcon(R.drawable.icon_silhouette)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .addAction(getUpdateAction());


        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {

            //Create notification channel if OREO
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(UPDATE_CHANNEL_ID, getApplicationContext().getResources().getString(R.string.notification_channel_name), NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }

            notificationManager.notify(UPDATE_NOTIFICATION_REQUEST_CODE, builder.build());
        }
    }

    private NotificationCompat.Action getUpdateAction() {
        Log.d(TAG, "getUpdateAction: update action created");
        Intent updateIntent = new Intent(getApplicationContext(), UpdateService.class);
        PendingIntent mutePendingIntent = PendingIntent.getService(getApplicationContext(), UPDATE_NOTIFICATION_REQUEST_CODE, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        return new NotificationCompat.Action.Builder(0, getApplicationContext().getString(R.string.update_action), mutePendingIntent).build();
    }
}
