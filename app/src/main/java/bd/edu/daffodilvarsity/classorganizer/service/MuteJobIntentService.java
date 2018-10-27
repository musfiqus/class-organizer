package bd.edu.daffodilvarsity.classorganizer.service;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import android.util.Log;
import android.widget.Toast;

import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.data.Repository;
import bd.edu.daffodilvarsity.classorganizer.model.Routine;
import bd.edu.daffodilvarsity.classorganizer.utils.AlarmHelper;
import bd.edu.daffodilvarsity.classorganizer.utils.FileUtils;
import es.dmoral.toasty.Toasty;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class MuteJobIntentService extends JobIntentService {
    private static final String TAG = "MuteJobIntentService";
    private static final int JOB_ID = 42080;
    private Disposable disposable;
    private int index;

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, MuteJobIntentService.class, JOB_ID, intent);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        boolean error = false;
        Bundle bundle = intent.getBundleExtra(AlarmHelper.TAG_ALARM_BUNDLE_DATA);
        if (bundle != null) {
            Routine routine = null;
            index = bundle.getInt(AlarmHelper.TAG_ALARM_INDEX);
            try {
                routine = bundle.getParcelable(AlarmHelper.TAG_ALARM_ROUTINE_OBJECT);
            } catch (IllegalStateException e) {
                error = true;
                Log.e(TAG, "onReceive: ", e);
                FileUtils.logAnError(getApplicationContext(), TAG, "onReceive: ", e);
            } catch (Exception e) {
                Log.e(TAG, "onReceive: ", e);
                FileUtils.logAnError(getApplicationContext(), TAG, "onReceive: ", e);
                error =true;
            }
            Repository repository = Repository.getInstance();
            try {
                repository.mutifyRoutine(routine).blockingAwait();
            } catch (RuntimeException e) {
                Log.e(TAG, "onReceive: ", e);
                e.printStackTrace();
                error = true;
            }

            NotificationManager notificationManager = (NotificationManager) getApplicationContext()
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.cancel(index);
            }
            if (routine != null) {
                Single.just(getString(R.string.mute_success, routine.getCourseCode()))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<String>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                disposable = d;
                            }

                            @Override
                            public void onSuccess(String s) {
                                Toasty.success(getApplicationContext(), s, Toast.LENGTH_SHORT, true).show();
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: ", e);
                            }
                        });

            }
        } else {
            Log.e(TAG, "onReceive: Null");
            error = true;
        }
        if (error) {
            Single.just(getString(R.string.mute_error))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<String>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            disposable = d;
                        }

                        @Override
                        public void onSuccess(String s) {
                            Toasty.error(getApplicationContext(), s, Toast.LENGTH_SHORT, true).show();
                            NotificationManager notificationManager = (NotificationManager) getApplicationContext()
                                    .getSystemService(Context.NOTIFICATION_SERVICE);
                            if (notificationManager != null) {
                                notificationManager.cancel(index);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toasty.error(getApplicationContext(), getString(R.string.mute_error), Toast.LENGTH_SHORT, true).show();
                            NotificationManager notificationManager = (NotificationManager) getApplicationContext()
                                    .getSystemService(Context.NOTIFICATION_SERVICE);
                            if (notificationManager != null) {
                                notificationManager.cancel(index);
                            }
                        }
                    });

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
