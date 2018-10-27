package bd.edu.daffodilvarsity.classorganizer.service;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import java.util.List;

import bd.edu.daffodilvarsity.classorganizer.ClassOrganizer;
import bd.edu.daffodilvarsity.classorganizer.data.ClassOrganizerDatabase;
import bd.edu.daffodilvarsity.classorganizer.data.Repository;
import bd.edu.daffodilvarsity.classorganizer.model.Database;
import bd.edu.daffodilvarsity.classorganizer.model.Routine;
import bd.edu.daffodilvarsity.classorganizer.model.RoutineSemesterModel;
import bd.edu.daffodilvarsity.classorganizer.model.Semester;
import bd.edu.daffodilvarsity.classorganizer.utils.AlarmHelper;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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
        AlarmHelper alarmHelper = new AlarmHelper();
        try {
            alarmHelper.startAllAlarms().blockingAwait();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }
}
