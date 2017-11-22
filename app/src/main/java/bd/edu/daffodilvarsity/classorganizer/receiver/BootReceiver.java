package bd.edu.daffodilvarsity.classorganizer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import bd.edu.daffodilvarsity.classorganizer.service.NotificationRestartJobIntentService;

/**
 * Created by Mushfiqus Salehin on 6/6/2017.
 * musfiqus@gmail.com
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null) {
            if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                boolean hasNotification = preferences.getBoolean("notification_preference", true);
                if (hasNotification) {
                    Intent i = new Intent(context, NotificationRestartJobIntentService.class);
                    NotificationRestartJobIntentService.enqueueWork(context, i);
                }
            }
        }
    }
}
