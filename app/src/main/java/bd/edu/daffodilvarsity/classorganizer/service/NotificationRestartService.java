package bd.edu.daffodilvarsity.classorganizer.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import bd.edu.daffodilvarsity.classorganizer.activity.MainActivity;
import bd.edu.daffodilvarsity.classorganizer.utils.AlarmHelper;

/**
 * Created by musfiqus on 6/5/2017.
 */

public class NotificationRestartService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * param name Used to name the worker thread, important only for debugging.
     */
    public NotificationRestartService() {
        super("NotificationRestartService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        new AlarmHelper(getApplicationContext()).startAll();
    }
}
