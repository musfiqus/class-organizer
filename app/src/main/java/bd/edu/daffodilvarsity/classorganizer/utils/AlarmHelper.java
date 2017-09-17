package bd.edu.daffodilvarsity.classorganizer.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Calendar;

import bd.edu.daffodilvarsity.classorganizer.adapter.DayDataAdapter;
import bd.edu.daffodilvarsity.classorganizer.data.DayData;
import bd.edu.daffodilvarsity.classorganizer.receiver.NotificationPublisher;

/**
 * Created by Mushfiqus Salehin on 6/6/2017.
 * musfiqus@gmail.com
 */

public class AlarmHelper {
    private Context context;
    private PrefManager prefManager;

    public AlarmHelper(Context context) {
        this.context = context;
        this.prefManager = new PrefManager(context);
    }

    public void startAll() {
        ArrayList<DayData> data = prefManager.getSavedDayData();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isRamadanTime = preferences.getBoolean("ramadan_preference", false);
        if (data != null) {
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i) != null) {
                    if (data.get(i).getDay() != null && data.get(i).getTime() != null) {
                        int dayOfWeek = calculateDay(data.get(i).getDay());
                        int time[] = calculateTime(isRamadanTime ? DayDataAdapter.DayDataHolder.convertToRamadanTime(data.get(i).getTime(), data.get(i).getTimeWeight()): data.get(i).getTime());
                        if (dayOfWeek != -1) {
                            scheduleAlarm(dayOfWeek, i, time[0], time[1], data.get(i));
                        }
                    }
                }
            }
        }
    }

    public void cancelAll() {
        ArrayList<DayData> data = prefManager.getSavedDayData();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isRamadanTime = preferences.getBoolean("ramadan_preference", false);
        if (data != null) {
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i) != null) {
                    if (data.get(i).getDay() != null && data.get(i).getTime() != null) {
                        int dayOfWeek = calculateDay(data.get(i).getDay());
                        int time[] = calculateTime(isRamadanTime ? DayDataAdapter.DayDataHolder.convertToRamadanTime(data.get(i).getTime(), data.get(i).getTimeWeight()): data.get(i).getTime());
                        if (dayOfWeek != -1) {
                            cancelAlarm(dayOfWeek, i, time[0], time[1], data.get(i));
                        }
                    }
                }

            }
        }
    }

    public void forceRestart(boolean isRamadanTime) {
        ArrayList<DayData> data = prefManager.getSavedDayData();
        if (data != null) {
            for (int i = 0; i < data.size(); i++) {
                int dayOfWeek = calculateDay(data.get(i).getDay());
                int time[] = calculateTime(isRamadanTime ? DayDataAdapter.DayDataHolder.convertToRamadanTime(data.get(i).getTime(), data.get(i).getTimeWeight()): data.get(i).getTime());
                if (dayOfWeek != -1) {
                    cancelAlarm(dayOfWeek, i, time[0], time[1], data.get(i));
                    scheduleAlarm(dayOfWeek, i, time[0], time[1], data.get(i));
                }
            }
        }
    }

    public void cancelAlarm(int dayOfWeek, int index, int hour, int timeBefore, DayData dayData) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, timeBefore);

        // Check we aren't setting it in the past or present which would trigger it to fire instantly
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 7);
        }
        Intent dayDataIntent = new Intent(context, NotificationPublisher.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("DayData_Object", dayData);
        bundle.putInt("index", index);
        bundle.putInt("day", dayOfWeek);
        bundle.putInt("hour", hour);
        bundle.putInt("timeBefore", timeBefore);
        dayDataIntent.putExtra("bundled_data", bundle);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, index, dayDataIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    public void scheduleAlarm(int dayOfWeek, int index, int hour, int timeBefore, DayData dayData) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, timeBefore);

        // Check we aren't setting it in the past or present which would trigger it to fire instantly
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 7);
        }
        Intent dayDataIntent = new Intent(context, NotificationPublisher.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("DayData_Object", dayData);
        bundle.putInt("index", index);
        bundle.putInt("day", dayOfWeek);
        bundle.putInt("hour", hour);
        bundle.putInt("timeBefore", timeBefore);
        dayDataIntent.putExtra("bundled_data", bundle);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, index, dayDataIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (alarmManager != null) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        } else {
            if (alarmManager != null) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        }
    }


    private int calculateDay(String day) {
        if (day != null) {
            int dayOfWeek = -1;
            if (day.equalsIgnoreCase("Saturday")) {
                dayOfWeek = Calendar.SATURDAY;
            } else if (day.equalsIgnoreCase("Sunday")) {
                dayOfWeek = Calendar.SUNDAY;
            } else if (day.equalsIgnoreCase("Monday")) {
                dayOfWeek = Calendar.MONDAY;
            } else if (day.equalsIgnoreCase("Tuesday")) {
                dayOfWeek = Calendar.TUESDAY;
            } else if (day.equalsIgnoreCase("Wednesday")) {
                dayOfWeek = Calendar.WEDNESDAY;
            } else if (day.equalsIgnoreCase("Thursday")) {
                dayOfWeek = Calendar.THURSDAY;
            } else if (day.equalsIgnoreCase("Friday")) {
                dayOfWeek = Calendar.FRIDAY;
            }
            return dayOfWeek;
        }
        return -1;
    }

    private int[] calculateTime(String time) {
        int[] calculatedTime = new int[2];
        int hour, minute;
        minute = calculateMinute(time);
        hour = calculateHour(time);


        int delay = prefManager.getReminderDelay();
        calculatedTime[0] = hour;
        calculatedTime[1] = minute + (60 - delay);
        return calculatedTime;

    }

    private int calculateMinute(String time) {
        int minute = 0;
        try {
            minute = Integer.parseInt(time.substring(3, 5));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return minute;
    }


    private int calculateHour(String time) {
        int hour = 0;
        try {
            hour = Integer.parseInt(time.substring(0, 2));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (time.substring(6, 8).equalsIgnoreCase("pm")) {
            if (hour != 12) {
                hour += 12;
            }
        } else {
            if (hour == 12) {
                hour = 0;
            }
        }
        if (hour == 0) {
            return 23;
        } else {
            return hour - 1;
        }
    }

}
