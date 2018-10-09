package bd.edu.daffodilvarsity.classorganizer.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import bd.edu.daffodilvarsity.classorganizer.data.Repository;
import bd.edu.daffodilvarsity.classorganizer.model.Routine;
import bd.edu.daffodilvarsity.classorganizer.utils.AlarmHelper;
import bd.edu.daffodilvarsity.classorganizer.utils.FileUtils;
import es.dmoral.toasty.Toasty;

public class MuteActionReceiver extends BroadcastReceiver {
    private static final String TAG = "MuteActionReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean error = false;
        Bundle bundle = intent.getBundleExtra(AlarmHelper.TAG_ALARM_BUNDLE_DATA);
        if (bundle != null) {
            Routine routine = null;
            int index = bundle.getInt(AlarmHelper.TAG_ALARM_INDEX);
            try {
                routine = bundle.getParcelable(AlarmHelper.TAG_ALARM_ROUTINE_OBJECT);
            } catch (IllegalStateException e) {
                error = true;
                FileUtils.logAnError(context, TAG, "onReceive: ", e);
            } catch (Exception e) {
                FileUtils.logAnError(context, TAG, "onReceive: ", e);
                error =true;
            }
            Repository repository = Repository.getInstance();
            try {
                repository.mutifyRoutine(routine).blockingAwait();
            } catch (RuntimeException e) {
                e.printStackTrace();
                error = true;
            }

            NotificationManager notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.cancel(index);
            }
        } else {
            error = true;
        }
        if (error) {
            Toasty.error(context, "Unable to mute notifications", Toast.LENGTH_SHORT, true).show();
        }
    }
}