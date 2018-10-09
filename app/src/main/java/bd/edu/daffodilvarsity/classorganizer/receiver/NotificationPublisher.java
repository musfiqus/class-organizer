package bd.edu.daffodilvarsity.classorganizer.receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.model.Routine;
import bd.edu.daffodilvarsity.classorganizer.model.Semester;
import bd.edu.daffodilvarsity.classorganizer.ui.main.MainActivity;
import bd.edu.daffodilvarsity.classorganizer.utils.AlarmHelper;
import bd.edu.daffodilvarsity.classorganizer.utils.FileUtils;
import bd.edu.daffodilvarsity.classorganizer.utils.PreferenceGetter;

/**
 * Created by Mushfiqus Salehin on 6/5/2017.
 * musfiqus@gmail.com
 */

public class NotificationPublisher extends BroadcastReceiver {
    private static final String TAG = "NotificationPublisher";
    private static String CLASSES_CHANNEL_ID = "notification_channel_01";
    public static String TAG_NOTIFICATION_DATA = "NotificationData";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getBundleExtra(AlarmHelper.TAG_ALARM_BUNDLE_DATA);
        if (bundle != null) {
            Routine routine = null;
            Semester semester = null;
            try {
                routine = bundle.getParcelable(AlarmHelper.TAG_ALARM_ROUTINE_OBJECT);
                semester = bundle.getParcelable(AlarmHelper.TAG_ALARM_SEMESTER_OBJECT);
            } catch (IllegalStateException e) {
                FileUtils.logAnError(context, TAG, "onReceive: ", e);
                Toast.makeText(context, "Error! Couldn't show notification", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                FileUtils.logAnError(context, TAG, "onReceive: WTF? ", e);
                Toast.makeText(context, "Error! Couldn't show notification", Toast.LENGTH_SHORT).show();
            }
            int index = bundle.getInt(AlarmHelper.TAG_ALARM_INDEX);
            int dayOfWeek = bundle.getInt(AlarmHelper.TAG_ALARM_DAY);
            int hour = bundle.getInt(AlarmHelper.TAG_ALARM_HOUR);
            int timeBefore = bundle.getInt(AlarmHelper.TAG_ALARM_TIME_BEFORE);
            if (routine != null && semester != null) {
                showNotification(index, context, routine, bundle);
                AlarmHelper alarmHelper = new AlarmHelper();
                alarmHelper.scheduleAlarm(dayOfWeek, index, hour, timeBefore, routine, semester);
            }
        }
    }

    private void showNotification(int index, Context context, Routine routine, Bundle bundle) {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setAction(context.getString(R.string.notification_publisher_filter));
        bundle.putByteArray(TAG_NOTIFICATION_DATA, FileUtils.convertToByteArray(routine));
        notificationIntent.putExtra(AlarmHelper.TAG_ALARM_BUNDLE_DATA, bundle);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, index, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CLASSES_CHANNEL_ID)
                .setContentTitle(context.getString(R.string.routine_notfication_title, routine.getCourseTitle()))
                .setContentText("Today's class is at "
                        + (PreferenceGetter.isRamadanEnabled() ? routine.getAltTime(): routine.getTime())
                        + " in room " + routine.getRoomNo())
                .setTicker("You have a class soon!")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                .setSmallIcon(R.drawable.icon_silhouette)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .addAction(getMuteAction(context, index, bundle));

        Notification notification = new NotificationCompat.InboxStyle(builder)
                .addLine("Time: " + (PreferenceGetter.isRamadanEnabled() ? routine.getAltTime(): routine.getTime()))
                .addLine("Room: " + routine.getRoomNo())
                .setSummaryText(routine.getCourseTitle())
                .setBigContentTitle("Details of Today's Class")
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {

            //Create notification channel if OREO
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CLASSES_CHANNEL_ID, context.getResources().getString(R.string.notification_channel_name), NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }

            notificationManager.notify(index, notification);
        }
    }

    @NonNull
    private NotificationCompat.Action getMuteAction(Context context, int index, Bundle bundle) {
        Intent muteIntent = new Intent(context, MuteActionReceiver.class);
        muteIntent.setAction(context.getString(R.string.mute_action_filter));

        muteIntent.putExtra(AlarmHelper.TAG_ALARM_BUNDLE_DATA, bundle);

        PendingIntent mutePendingIntent = PendingIntent.getBroadcast(context, index, muteIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        return new NotificationCompat.Action.Builder(0, context.getString(R.string.mute_action), mutePendingIntent).build();
    }
}