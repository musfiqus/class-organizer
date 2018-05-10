package bd.edu.daffodilvarsity.classorganizer.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import bd.edu.daffodilvarsity.classorganizer.data.DayData;
import bd.edu.daffodilvarsity.classorganizer.utils.AlarmHelper;
import bd.edu.daffodilvarsity.classorganizer.utils.FileUtils;
import bd.edu.daffodilvarsity.classorganizer.utils.PrefManager;

public class MuteActionReceiver extends BroadcastReceiver {
    private static final String TAG = "MuteActionReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean error = false;
        Bundle bundle = intent.getBundleExtra(AlarmHelper.TAG_ALARM_BUNDLE_DATA);
        if (bundle != null) {
            DayData dayData = null;
            try {
                dayData = bundle.getParcelable(AlarmHelper.TAG_ALARM_DAYDATA_OBJECT);
            } catch (IllegalStateException e) {
                FileUtils.logAnError(context, TAG, "onReceive: "+e.toString());
                Toast.makeText(context, "Error! Couldn't mute notification", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                FileUtils.logAnError(context, TAG, "onReceive: "+e.toString());
                Toast.makeText(context, "Error! Couldn't mute notification", Toast.LENGTH_SHORT).show();

            }
            int index = bundle.getInt(AlarmHelper.TAG_ALARM_INDEX);
            if (dayData != null) {
                PrefManager prefManager = new PrefManager(context);
                int position = prefManager.getDayDataPosition(dayData);
                if (position != -1) {
                    ArrayList<DayData> newList = prefManager.getSavedDayData();
                    newList.get(position).setMuted(true);
                    prefManager.saveDayData(newList);
                    Log.d(TAG, "onReceive: Notifications muted for "+dayData.getCourseCode());
                    Toast.makeText(context, "Notifications muted for "+dayData.getCourseCode(), Toast.LENGTH_SHORT).show();
                } else {
                    error = true;
                }
            } else {
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
            Toast.makeText(context, "Unable to mute notifications", Toast.LENGTH_SHORT).show();
        }
    }
}
