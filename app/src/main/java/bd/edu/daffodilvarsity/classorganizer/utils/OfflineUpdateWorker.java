package bd.edu.daffodilvarsity.classorganizer.utils;

import android.content.Context;
import androidx.annotation.NonNull;

import androidx.work.Worker;
import androidx.work.WorkerParameters;
import bd.edu.daffodilvarsity.classorganizer.data.Repository;
import bd.edu.daffodilvarsity.classorganizer.model.Database;

public class OfflineUpdateWorker extends Worker {
    public OfflineUpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            if (PreferenceGetter.SHIPPED_DATABASE_VERSION > PreferenceGetter.getDatabaseVersion()) {
                Database database = FileUtils.readOfflineDatabase();
                Repository repository = Repository.getInstance();
                repository.upgradeDatabaseFromResponse(database);
            }
            return Result.SUCCESS;
        } catch (RuntimeException e) {
            return Result.FAILURE;
        }
    }
}
