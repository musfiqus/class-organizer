package bd.edu.daffodilvarsity.classorganizer.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.preference.PreferenceManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.activity.DayDataDetailActivity;
import bd.edu.daffodilvarsity.classorganizer.adapter.DayDataAdapter;
import bd.edu.daffodilvarsity.classorganizer.data.DayData;
import bd.edu.daffodilvarsity.classorganizer.utils.AlarmHelper;

/**
 * Created by Mushfiqus Salehin on 6/5/2017.
 * musfiqus@gmail.com
 */

public class NotificationPublisher extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getBundleExtra("bundled_data");
        if (bundle != null) {
            DayData dayData = bundle.getParcelable("DayData_Object");
            int index = bundle.getInt("index");
            int dayOfWeek = bundle.getInt("day");
            int hour = bundle.getInt("hour");
            int timeBefore = bundle.getInt("timeBefore");
            if (dayData != null) {
                showNotification(index, context, dayData);
                AlarmHelper alarmHelper = new AlarmHelper(context);
                alarmHelper.scheduleAlarm(dayOfWeek, index, hour, timeBefore, dayData);
            }
        }
    }

    private void showNotification(int index, Context context, DayData dayData) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isRamadanTime = preferences.getBoolean("ramadan_preference", false);

        Intent notificationIntent = new Intent(context, DayDataDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putByteArray("NotificationData", convertToByteArray(dayData));
        notificationIntent.putExtra("bundled_data", bundle);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, index, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String article = dayData.getCourseCode().substring(0, 1);
        if (article.equalsIgnoreCase("a") || article.equalsIgnoreCase("e") || article.equalsIgnoreCase("i") || article.equalsIgnoreCase("o") || article.equalsIgnoreCase("u")) {
            article = "an";
        } else {
            article = "a";
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle("You have " + article + " " + dayData.getCourseCode() + " class soon")
                .setContentText("Today's class is at "
                        + (isRamadanTime ? DayDataAdapter.DayDataHolder.convertToRamadanTime(dayData.getTime(), dayData.getTimeWeight()).substring(0, 8) : dayData.getTime()).substring(0, 8)
                        + " in room " + dayData.getRoomNo())
                .setTicker("You have a class soon!")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                .setSmallIcon(getNotificationIcon())
                .setContentIntent(pendingIntent);
        Notification notification = new NotificationCompat.InboxStyle(builder)
                .addLine("Time: " + (isRamadanTime ? DayDataAdapter.DayDataHolder.convertToRamadanTime(dayData.getTime(), dayData.getTimeWeight()).substring(0, 8) : dayData.getTime()).substring(0, 8))
                .addLine("Room: " + dayData.getRoomNo())
                .setSummaryText(dayData.getCourseTitle())
                .setBigContentTitle("Details of Today's Class")
                .build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(index, notification);
        }
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.icon_silhouette : R.mipmap.ic_launcher;
    }

    private byte[] convertToByteArray(DayData dayData) {
        byte[] data = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(dayData);
            out.flush();
            data = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return data;
    }
}
