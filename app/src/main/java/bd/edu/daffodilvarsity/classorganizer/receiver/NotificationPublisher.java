package bd.edu.daffodilvarsity.classorganizer.receiver;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.model.DayData;
import bd.edu.daffodilvarsity.classorganizer.ui.detail.RoutineDetailActivity;
import bd.edu.daffodilvarsity.classorganizer.utils.AlarmHelper;
import bd.edu.daffodilvarsity.classorganizer.utils.CourseUtils;
import bd.edu.daffodilvarsity.classorganizer.utils.FileUtils;

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
            DayData dayData = null;
            try {
                dayData = bundle.getParcelable(AlarmHelper.TAG_ALARM_ROUTINE_OBJECT);
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
            if (dayData != null) {
                showNotification(index, context, dayData);
//                AlarmHelper alarmHelper = new AlarmHelper(context);
//                alarmHelper.scheduleAlarm(dayOfWeek, index, hour, timeBefore, dayData);
            }
        }
    }

    private void showNotification(int index, Context context, DayData dayData) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isRamadanTime = preferences.getBoolean("ramadan_preference", false);

        Intent notificationIntent = new Intent(context, RoutineDetailActivity.class);
        notificationIntent.setAction(context.getString(R.string.notification_publisher_filter));
        Bundle bundle = new Bundle();
        bundle.putByteArray(TAG_NOTIFICATION_DATA, CourseUtils.convertToByteArray(dayData));
        notificationIntent.putExtra(AlarmHelper.TAG_ALARM_BUNDLE_DATA, bundle);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, index, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CLASSES_CHANNEL_ID)
//                .setContentTitle("You have " + article(dayData.getCourseCode().substring(0, 1)) + " " + dayData.getCourseCode() + " class soon")
//                .setContentText("Today's class is at "
//                        + (isRamadanTime ? DayDataAdapter.DayDataHolder.convertToRamadanTime(dayData.getTime(),
//                        dayData.getTimeWeight()).substring(0, 8) : dayData.getTime()).substring(0, 8)
//                        + " in room " + dayData.getRoomNo())
//                .setTicker("You have a class soon!")
//                .setPriority(NotificationCompat.PRIORITY_MAX)
//                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
//                .setSmallIcon(R.drawable.icon_silhouette)
//                .setContentIntent(pendingIntent)
//                .setAutoCancel(true)
//                .addAction(getMuteAction(dayData, context, index));

//        Notification notification = new NotificationCompat.InboxStyle(builder)
//                .addLine("Time: " + (isRamadanTime ? DayDataAdapter.DayDataHolder.convertToRamadanTime(dayData.getTime(), dayData.getTimeWeight()).substring(0, 8) : dayData.getTime()).substring(0, 8))
//                .addLine("Room: " + dayData.getRoomNo())
//                .setSummaryText(dayData.getCourseTitle())
//                .setBigContentTitle("Details of Today's Class")
//                .build();

//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        if (notificationManager != null) {
//
//            //Create notification channel if OREO
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                NotificationChannel channel = new NotificationChannel(CLASSES_CHANNEL_ID, context.getResources().getString(R.string.notification_channel_name), NotificationManager.IMPORTANCE_HIGH);
//                notificationManager.createNotificationChannel(channel);
//            }
//
//            notificationManager.notify(index, notification);
//        }
    }

    @NonNull
    private NotificationCompat.Action getMuteAction(DayData dayData, Context context, int index) {
        Log.d(TAG, "getMuteAction: Mute created");
        Intent muteIntent = new Intent(context, MuteActionReceiver.class);
        muteIntent.setAction(context.getString(R.string.mute_action_filter));

        Bundle bundle = new Bundle();
        bundle.putParcelable(AlarmHelper.TAG_ALARM_ROUTINE_OBJECT, dayData);
        bundle.putInt(AlarmHelper.TAG_ALARM_INDEX, index);

        muteIntent.putExtra(AlarmHelper.TAG_ALARM_BUNDLE_DATA, bundle);

        PendingIntent mutePendingIntent = PendingIntent.getBroadcast(context, index, muteIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        return new NotificationCompat.Action.Builder(0, context.getString(R.string.mute_action), mutePendingIntent).build();
    }

    @NonNull
    private String article(String firstChar) {
        if (firstChar.equalsIgnoreCase("a") || firstChar.equalsIgnoreCase("e") || firstChar.equalsIgnoreCase("i") || firstChar.equalsIgnoreCase("o") || firstChar.equalsIgnoreCase("u")) {
            return "an";
        } else {
            return "a";
        }
    }
}
