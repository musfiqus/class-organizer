package bd.edu.daffodilvarsity.classorganizer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import bd.edu.daffodilvarsity.classorganizer.service.NotificationRestartService;

/**
 * Created by Mushfiqus Salehin on 6/6/2017.
 * musfiqus@gmail.com
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            boolean hasNotification = preferences.getBoolean("notification_preference", false);
            if (hasNotification) {
                Intent i = new Intent(context, NotificationRestartService.class);
                context.startService(i);
            }
        }
    }
}
