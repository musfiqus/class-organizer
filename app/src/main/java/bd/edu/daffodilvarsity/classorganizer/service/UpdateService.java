package bd.edu.daffodilvarsity.classorganizer.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.data.Repository;
import bd.edu.daffodilvarsity.classorganizer.model.Database;
import bd.edu.daffodilvarsity.classorganizer.ui.main.MainActivity;
import bd.edu.daffodilvarsity.classorganizer.utils.PreferenceGetter;
import bd.edu.daffodilvarsity.classorganizer.utils.UpdatePollWorker;

public class UpdateService extends IntentService {

    private static final String TAG = "UpdateService";

    public UpdateService() {
        super(UpdateService.class.getSimpleName());
    }

    public static final int UPDATE_SERVICE_NOTIFICATION_CODE = 69096;

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG, "onHandleIntent: Callled");
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(UpdatePollWorker.UPDATE_NOTIFICATION_REQUEST_CODE);
        try {
            Repository repository = Repository.getInstance();
            progressNotification();
            Database database = repository.getRoutineFromServer().blockingGet();
            if (database.getDatabaseVersion() <= PreferenceGetter.getDatabaseVersion()) {
                alreadyUpdatedNotification();
                Log.e(TAG, "onHandleIntent: already up 1");
            } else {
                boolean upgrade = repository.upgradeDatabaseFromResponse(database).blockingGet();
                if (upgrade) {
                    Log.e(TAG, "onHandleIntent: success");
                    successNotification();
                } else {
                    Log.e(TAG, "onHandleIntent: already up 2");
                    alreadyUpdatedNotification();
                }
            }
        } catch (RuntimeException e) {
            Log.e(TAG, "onHandleIntent: fail", e);
            failureNotification();
        }
    }

    private void successNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, UpdatePollWorker.UPDATE_CHANNEL_ID);
        if (notificationManager != null) {
            Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            notificationManager.cancel(UPDATE_SERVICE_NOTIFICATION_CODE);
            notificationBuilder.setProgress(0, 0, false);
            notificationBuilder.setContentText("Update successful");
            notificationBuilder.setContentIntent(pendingIntent);
            notificationBuilder.setAutoCancel(true);
            notificationBuilder.setSmallIcon(R.drawable.ic_download_done_24dp);
            notificationManager.notify(UPDATE_SERVICE_NOTIFICATION_CODE, notificationBuilder.build());
        }
    }

    private void progressNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, UpdatePollWorker.UPDATE_CHANNEL_ID);
        if (notificationManager != null) {
            notificationManager.cancel(UPDATE_SERVICE_NOTIFICATION_CODE);
            notificationBuilder.setSmallIcon(R.drawable.ic_download_24dp);
            notificationBuilder.setProgress(0, 0, true);
            notificationBuilder.setContentText("Downloading update");
            notificationManager.notify(UPDATE_SERVICE_NOTIFICATION_CODE, notificationBuilder.build());
        }

    }

    private void failureNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, UpdatePollWorker.UPDATE_CHANNEL_ID);
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (notificationManager != null) {
            notificationManager.cancel(UPDATE_SERVICE_NOTIFICATION_CODE);
            notificationBuilder.setProgress(0, 0, false);
            notificationBuilder.setContentText("Download failed");
            notificationBuilder.setContentIntent(pendingIntent);
            notificationBuilder.setAutoCancel(true);
            notificationBuilder.setSmallIcon(R.drawable.ic_download_failed_24dp);
            notificationManager.notify(UPDATE_SERVICE_NOTIFICATION_CODE, notificationBuilder.build());
        }
    }

    private void alreadyUpdatedNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, UpdatePollWorker.UPDATE_CHANNEL_ID);
        if (notificationManager != null) {
            Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            notificationManager.cancel(UPDATE_SERVICE_NOTIFICATION_CODE);
            notificationBuilder.setProgress(0, 0, false);
            notificationBuilder.setContentText("You are already on the latest version!");
            notificationBuilder.setSmallIcon(R.drawable.ic_download_done_24dp);
            notificationBuilder.setContentIntent(pendingIntent);
            notificationBuilder.setAutoCancel(true);
            notificationManager.notify(UPDATE_SERVICE_NOTIFICATION_CODE, notificationBuilder.build());
        }
    }



}