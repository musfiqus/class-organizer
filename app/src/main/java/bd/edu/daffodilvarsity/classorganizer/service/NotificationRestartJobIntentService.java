package bd.edu.daffodilvarsity.classorganizer.service;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;

import bd.edu.daffodilvarsity.classorganizer.utils.AlarmHelper;

/**
 * Created by musfiqus on 11/21/2017.
 */

public class NotificationRestartJobIntentService extends JobIntentService {
    private static int JOB_ID = 42069;

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, NotificationRestartJobIntentService.class, JOB_ID, intent);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        AlarmHelper alarmHelper = new AlarmHelper(getApplicationContext());
        alarmHelper.cancelAll();
        alarmHelper.startAll();
    }
}
