package bd.edu.daffodilvarsity.classorganizer.utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import androidx.work.Worker;
import androidx.work.WorkerParameters;
import bd.edu.daffodilvarsity.classorganizer.data.Repository;
import bd.edu.daffodilvarsity.classorganizer.model.Database;
import io.reactivex.disposables.Disposable;

public class OfflineUpdateWorker extends Worker {
    private Disposable mDisposable;
    private static final String TAG = "OfflineUpdateWorker";
    public OfflineUpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "doWork: Called");
        try {
            if (PreferenceGetter.SHIPPED_DATABASE_VERSION > PreferenceGetter.getDatabaseVersion()) {
                Log.e(TAG, "doWork: Previous version: "+PreferenceGetter.getDatabaseVersion());
                Database database = FileUtils.readOfflineDatabase();
                Log.e(TAG, "doWork: New version: "+database.getDatabaseVersion());
                Repository repository = Repository.getInstance();
                mDisposable = repository.upgradeDatabaseFromResponse(database).subscribe();
                Log.e(TAG, "doWork: Updated version: "+PreferenceGetter.getDatabaseVersion());
            }
            Log.d(TAG, "doWork: Success");
            return Result.success();
        } catch (RuntimeException e) {
            Log.e(TAG, "doWork: Failed", e);
            return Result.failure();
        }
    }

    @Override
    public void onStopped() {
        super.onStopped();
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }
}
