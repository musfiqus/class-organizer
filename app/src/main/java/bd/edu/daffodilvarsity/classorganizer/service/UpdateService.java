package bd.edu.daffodilvarsity.classorganizer.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.activity.MainActivity;
import bd.edu.daffodilvarsity.classorganizer.data.DayData;
import bd.edu.daffodilvarsity.classorganizer.data.Download;
import bd.edu.daffodilvarsity.classorganizer.data.UpdateResponse;
import bd.edu.daffodilvarsity.classorganizer.utils.ClassOrganizerApi;
import bd.edu.daffodilvarsity.classorganizer.utils.CourseUtils;
import bd.edu.daffodilvarsity.classorganizer.utils.FileUtils;
import bd.edu.daffodilvarsity.classorganizer.utils.MasterDBOffline;
import bd.edu.daffodilvarsity.classorganizer.utils.PrefManager;
import bd.edu.daffodilvarsity.classorganizer.utils.RoutineLoader;
import bd.edu.daffodilvarsity.classorganizer.utils.UpdateNotificationHelper;
import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.Url;

public class UpdateService extends IntentService {

    public UpdateService() {
        super(UpdateService.class.getSimpleName());
    }

    public static final int UPDATE_NORMAL = 200;
    public static final int UPDATE_SEMESTER = 300;
    public static final int UPDATE_VERIFYING = 201;
    public static final int UPDATE_FAILED = -1;

    public static final int UPDATE_SERVICE_NOTIFICATION_CODE = 69096;

    private static final String TAG = "UpdateService";

    public static final String TAG_UPDATE_RESPONSE = "UpdateResponse";
    public static final String TAG_DOWNLOAD = "Download";

    public static final String PROGRESS_UPDATE = "ProgressUpdate";

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    private UpdateResponse mUpdateResponse;
    private int totalFileSize;


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent.getParcelableExtra(TAG_UPDATE_RESPONSE) != null) {
            Log.e(TAG, "onHandleIntent: IMMA HIRE");
            mUpdateResponse = intent.getParcelableExtra(TAG_UPDATE_RESPONSE);
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            //Cancel update notification if there is one
            if (notificationManager != null) {
                notificationManager.cancel(UpdateNotificationHelper.UPDATE_NOTIFICATION_REQUEST_CODE);
            }
            PrefManager prefManager = new PrefManager(getApplicationContext());
            if (mUpdateResponse.getVersion() <= prefManager.getDatabaseVersion()) {
                //abort while there is still time
                alreadyUpdatedNotification();
                //send intent to activity
                //200 = normal update
                //300 = semester update
                Download download = new Download();
                download.setProgress(UPDATE_NORMAL);
                sendIntent(download);
                Toasty.info(getApplicationContext(), "Already on the latest version!", Toast.LENGTH_SHORT, true).show();
            } else {
                notificationBuilder = new NotificationCompat.Builder(this, UpdateNotificationHelper.UPDATE_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_download_24dp)
                        .setContentTitle("Routine Update")
                        .setContentText("Downloading update")
                        .setPriority(Notification.PRIORITY_MAX)
                        .setAutoCancel(true);
                notificationHandler(notificationBuilder.build());
                Gson gson = new Gson();
                Log.d(TAG, "onHandleIntent: "+gson.toJson(mUpdateResponse));
                initDownload(mUpdateResponse.getUrl(), mUpdateResponse.getFilename(), mUpdateResponse.getSize());
            }


        }
    }

    private void alreadyUpdatedNotification() {
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationManager.cancel(UPDATE_SERVICE_NOTIFICATION_CODE);
        notificationBuilder.setProgress(0, 0, false);
        notificationBuilder.setContentText("Already on the latest version!");
        notificationBuilder.setSmallIcon(R.drawable.ic_download_done_24dp);
        notificationBuilder.setContentIntent(pendingIntent);
        notificationBuilder.setAutoCancel(true);
        notificationManager.notify(UPDATE_SERVICE_NOTIFICATION_CODE, notificationBuilder.build());
    }

    private void initDownload(@Url String url, String fileName, long fileSize) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://cdn.rawgit.com/musfiqus/musfiqus.github.io/")
                .build();

        ClassOrganizerApi classOrganizerApi = retrofit.create(ClassOrganizerApi.class);

        Call<ResponseBody> request = classOrganizerApi.downloadFile(url);
        try {
            downloadFile(request.execute().body(), fileName, fileSize);
        } catch (IOException e) {

            e.printStackTrace();
            onDownloadFailure();
            Toasty.error(getApplicationContext(), "Unable to download update,", Toast.LENGTH_SHORT).show();

        }
    }

    private void downloadFile(ResponseBody body, String fileName, long fileSize) throws IOException {
        int count;
        byte data[] = new byte[1024 * 4];
        InputStream bis = new BufferedInputStream(body.byteStream(), 1024 * 8);
        File outputFile = new File(getDatabasePath(fileName).getAbsolutePath());
        if (outputFile.exists()) {
            boolean result = outputFile.delete();
            if (result) Log.d(TAG, "downloadFile: Old file deleted");
        }
        OutputStream output = new FileOutputStream(outputFile);
        long total = 0;
        int broadcastStart = 0;
        while ((count = bis.read(data)) != -1) {

            total += count;
            totalFileSize = (int) (fileSize / 1024);
            double current = Math.round(total / 1024);

            int progress = (int) ((total * 100) / fileSize);
            Download download = new Download();
            download.setTotalFileSize(totalFileSize);
            Log.e(TAG, "downloadFile: YO?");
            if (progress - broadcastStart >= 5) {
                broadcastStart = progress;
                download.setCurrentFileSize((int) current);
                download.setProgress(progress);
                sendNotification(download);
            }
            output.write(data, 0, count);
        }
        verify(outputFile);
        output.flush();
        output.close();
        bis.close();

    }

    private void sendNotification(Download download) {
        Log.e(TAG, "sendNotification: Progress: " + download.getProgress());

        sendIntent(download);
        notificationBuilder.setProgress(100, download.getProgress(), false);
        notificationBuilder.setContentText(String.format("Downloaded (%d/%d) KB", download.getCurrentFileSize(), download.getTotalFileSize()));
        notificationHandler(notificationBuilder.build());
    }

    private void notificationHandler(Notification notification) {
        if (notificationManager != null) {

            //Create notification channel if OREO
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(UpdateNotificationHelper.UPDATE_CHANNEL_ID, "Update", NotificationManager.IMPORTANCE_LOW);
                notificationManager.createNotificationChannel(channel);
            }

            notificationManager.notify(UPDATE_SERVICE_NOTIFICATION_CODE, notification);
        }
    }


    private void sendIntent(Download download) {

        Intent intent = new Intent(PROGRESS_UPDATE);
        intent.putExtra(TAG_DOWNLOAD, download);
        LocalBroadcastManager.getInstance(UpdateService.this).sendBroadcast(intent);
    }

    private void onDownloadComplete() {
        Download download = new Download();
        download.setProgress(100);
        sendIntent(download);

        loadNewRoutine();
    }

    private void loadNewRoutine() {
        if (isEligibleToLoad()) {
            boolean isNewSemester = isNewSemesterAvailable(getApplicationContext());
            boolean hasLoaded = loadRoutineFromDB(getApplicationContext(), isNewSemester);
            Log.e(TAG, "loadNewRoutine: "+hasLoaded);
            if (hasLoaded) {
                CourseUtils courseUtils = CourseUtils.getInstance(getApplicationContext());
                PrefManager prefManager = new PrefManager(getApplicationContext());
                prefManager.saveSemester(courseUtils.getCurrentSemester(prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram()));
                prefManager.setSemesterCount(courseUtils.getSemesterCount(prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram()));
                if (isNewSemester) prefManager.resetModification(true, true, true, true);
                updateSuccessful(isNewSemester);
            } else {
                Log.w(TAG, "loadNewRoutine: Failed to load routine");
                updateFailed();
            }
        } else {
            updateSuccessful(false);
            Log.d(TAG, "loadNewRoutine: Not eligible to load");
        }

    }

    public static boolean isNewSemesterAvailable(Context context) {
        RoutineLoader routineLoader = RoutineLoader.newInstance(context);
        return routineLoader.isNewSemesterAvailable();
    }

    public static boolean loadRoutineFromDB(Context context, boolean isNewSemester) {
        boolean hasLoaded = true;
        PrefManager prefManager = new PrefManager(context);
        int currentLevel = 0;
        int currentTerm = 0;
        if (prefManager.isUserStudent()) {
            currentLevel = prefManager.getLevel();
            currentTerm = prefManager.getTerm();
            int[] levelTerm = setLevelTermOnUpgrade(currentLevel, currentTerm);
            int level = isNewSemester ? levelTerm[0] : currentLevel;
            int term = isNewSemester ? levelTerm[1] : currentTerm;
            prefManager.saveLevel(level);
            prefManager.saveTerm(term);
        }

        RoutineLoader routineLoader = RoutineLoader.newInstance(context);
        ArrayList<DayData> routine = routineLoader.loadRoutine(!isNewSemester);
        if (routine == null || routine.size() == 0) {
            //Fallback level term
            if (prefManager.isUserStudent()) {
                prefManager.saveLevel(currentLevel);
                prefManager.saveTerm(currentTerm);
            }
            routineLoader = RoutineLoader.newInstance(context);
            ArrayList<DayData> fallback = routineLoader.loadRoutine(!isNewSemester);
            if ((fallback == null || fallback.size() == 0)) {
                //Fallback failed
                hasLoaded = false;
                Toasty.error(context, "Unable to load new routine. Falling back to old routine.", Toast.LENGTH_SHORT, true).show();
            } else {
                //save fallback
                prefManager.saveDayData(fallback);
            }
        } else {
            //Load new routine
            prefManager.saveDayData(routine);
        }
        return hasLoaded;
    }

    private void verify(File downloadedDB) {
        notificationManager.cancel(UPDATE_SERVICE_NOTIFICATION_CODE);
        notificationBuilder.setProgress(0, 0, true);
        notificationBuilder.setContentText("Verifying file");
        notificationManager.notify(UPDATE_SERVICE_NOTIFICATION_CODE, notificationBuilder.build());
        Download download = new Download();
        download.setProgress(UPDATE_VERIFYING);
        sendIntent(download);

        if (FileUtils.checkMD5(mUpdateResponse.getMd5(), downloadedDB)) {
            boolean result = replaceDBandVerify(downloadedDB);
            if (result) {
                //set new db version
                PrefManager prefManager = new PrefManager(getApplicationContext());
                prefManager.setDatabaseVersion(mUpdateResponse.getVersion());
                //download is complete, load routine
                onDownloadComplete();
            } else {
                Log.e(TAG, "verify: Replace failed");
                updateFailed();
            }
        } else {
            Log.e(TAG, "verify: MD5 mismatch");
            updateFailed();
        }
    }

    private boolean replaceDBandVerify(File downloadedDB) {
        //Backup old db
        boolean isBKP = FileUtils.backupDatabase(getApplicationContext(), MasterDBOffline.DATABASE_NAME);
        if (!isBKP) {
            FileUtils.deleteDatabase(getApplicationContext(), Uri.fromFile(downloadedDB).getLastPathSegment());
            Toasty.error(getApplicationContext(), "Unable to backup old database.", Toast.LENGTH_SHORT, true).show();
            return false;
        }
        deleteDatabase(MasterDBOffline.DATABASE_NAME);
        File oldDb = getDatabasePath(MasterDBOffline.DATABASE_NAME);
        try {
            FileUtils.copyFile(downloadedDB, oldDb);
        } catch (IOException e) {
            Log.e(TAG, "replaceDBandVerify: \n" + e.toString());
            FileUtils.logAnError(getApplicationContext(), TAG, "replaceDBandVerify: ", e);
            boolean fallback = FileUtils.restoreDatabase(getApplicationContext(), "routine.db");
            if (!fallback)
                Toasty.error(getApplicationContext(), "Unable to fallback to old database.", Toast.LENGTH_SHORT, true).show();
            return false;
        }
//        File copiedDb = getDatabasePath(mUpdateResponse.getFilename());
        if (!FileUtils.checkMD5(mUpdateResponse.getMd5(), oldDb)) {
            boolean fallback = FileUtils.restoreDatabase(getApplicationContext(), mUpdateResponse.getFilename());
            if (!fallback)
                Toasty.error(getApplicationContext(), "Unable to fallback to old database.", Toast.LENGTH_SHORT, true).show();
            return false;
        }
        //finally delete the newly downloaded db
        boolean deletenewdb = FileUtils.deleteDatabase(getApplicationContext(), mUpdateResponse.getFilename());
        if (!deletenewdb) Log.w(TAG, "replaceDBandVerify: New db not deleted");
        return true;
    }

    private void updateFailed() {
        failureNotification();
        Toasty.error(getApplicationContext(), "Routine update failed!", Toast.LENGTH_SHORT, true).show();
        FileUtils.logAnError(getApplicationContext(), TAG, "updateFailed(): ", new Exception("Unable to verify database. MD5 mismatch"));

    }

    private void updateSuccessful(boolean isSemesterUpdated) {
        successNotification();
        //send intent to activity
        //200 = normal update
        //300 = semester update
        Download download = new Download();
        download.setProgress(isSemesterUpdated ? UPDATE_SEMESTER : UPDATE_NORMAL);
        sendIntent(download);
        Toasty.success(getApplicationContext(), "Routine updated", Toast.LENGTH_SHORT, true).show();
    }

    private void successNotification() {
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationManager.cancel(UPDATE_SERVICE_NOTIFICATION_CODE);
        notificationBuilder.setProgress(0, 0, false);
        notificationBuilder.setContentText("Update successful");
        notificationBuilder.setSmallIcon(R.drawable.ic_download_done_24dp);
        notificationBuilder.setContentIntent(pendingIntent);
        notificationBuilder.setAutoCancel(true);
        notificationManager.notify(UPDATE_SERVICE_NOTIFICATION_CODE, notificationBuilder.build());
    }

    private void failureNotification() {
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationManager.cancel(UPDATE_SERVICE_NOTIFICATION_CODE);
        notificationBuilder.setProgress(0, 0, false);
        notificationBuilder.setContentText("Download failed");
        notificationBuilder.setSmallIcon(R.drawable.ic_download_failed_24dp);
        notificationBuilder.setContentIntent(pendingIntent);
        notificationBuilder.setAutoCancel(true);
        notificationManager.notify(UPDATE_SERVICE_NOTIFICATION_CODE, notificationBuilder.build());
    }

    private void onDownloadFailure() {

        Download download = new Download();
        download.setProgress(UPDATE_FAILED);
        sendIntent(download);
        failureNotification();
    }

    //Calculates the new level and term upon a new semester routine
    private static int[] setLevelTermOnUpgrade(int currentLevel, int currentTerm) {
        if (currentTerm == 2) {
            if (currentLevel < 3) {
                currentLevel++;
                currentTerm = 0;
            }
        } else {
            currentTerm++;
        }
        return new int[]{currentLevel, currentTerm};
    }


    private boolean isEligibleToLoad() {
        PrefManager prefManager = new PrefManager(getApplicationContext());
        if (prefManager.isUserStudent() && prefManager.getSection() == null) {
            return false;
        }
        if (prefManager.getCampus() == null) {
            return false;
        }
        if (prefManager.getDept() == null) {
            return false;
        }
        if (prefManager.getProgram() == null) {
            return false;
        }
        if (!prefManager.isUserStudent() && prefManager.getTeacherInitial() == null) {
            return false;
        }
        return true;
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        notificationManager.cancel(UPDATE_SERVICE_NOTIFICATION_CODE);
    }

}