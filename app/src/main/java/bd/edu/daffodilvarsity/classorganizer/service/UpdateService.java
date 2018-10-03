package bd.edu.daffodilvarsity.classorganizer.service;

import android.app.IntentService;
import android.content.Intent;

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

    @Override
    protected void onHandleIntent(Intent intent) {

    }


}