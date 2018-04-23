package bd.edu.daffodilvarsity.classorganizer.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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

    private static final String TAG = "AlarmHelper";

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
        Date date = calendar.getTime();
        if (isDateValidForAlarm(date)) {
            Log.e(TAG, "scheduleAlarm: Date: "+ date);
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

    private boolean isDateValidForAlarm(Date currentDate) {
        if (!isWithinSemester(currentDate)) {
            Log.d(TAG, "isDateValidForAlarm: Date: "+currentDate+" is outside of semester");
            return false;
        }
        if (isWithinMid(currentDate)) {
            Log.d(TAG, "isDateValidForAlarm: Date: "+currentDate+" is during mid");
            return false;
        }
        if (isWithinVacation(currentDate)) {
            Log.d(TAG, "isDateValidForAlarm: Date: "+currentDate+" is during mid");
            return false;
        }
        Log.d(TAG, "isDateValidForAlarm: Date: "+currentDate+" is valid for alarm");
        return true;
    }

    private boolean isWithinSemester(Date currentDate) {
        boolean result = false;
        Date classStart = CourseUtils
                .getInstance(context)
                .getDateFromSchedule(
                        MasterDBOffline.COLUMN_SCHEDULES_CLASS_START, prefManager.getSemester(),
                        prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram());
        Date classEnd = CourseUtils
                .getInstance(context)
                .getDateFromSchedule(
                        MasterDBOffline.COLUMN_SCHEDULES_CLASS_END, prefManager.getSemester(),
                        prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram());
        if (classStart != null && classEnd != null) {
            if (!(currentDate.before(classStart) || currentDate.after(classEnd))) {
                //if the date is not before the current date and not after the end date then we're ok
                result = true;
            }
        }
        return result;
    }

    private boolean isWithinMid(Date currentDate) {
        boolean result = false;
        Date midStart = CourseUtils
                .getInstance(context)
                .getDateFromSchedule(
                        MasterDBOffline.COLUMN_SCHEDULES_MID_START, prefManager.getSemester(),
                        prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram());
        Date midEnd = CourseUtils
                .getInstance(context)
                .getDateFromSchedule(
                        MasterDBOffline.COLUMN_SCHEDULES_MID_END, prefManager.getSemester(),
                        prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram());
        if (midStart != null && midEnd != null) {
            if (!(currentDate.before(midStart) || currentDate.after(midEnd))) {
                //if the date is not before the current date and not after the end date then we're ok
                result = true;
            }
        }
        return result;
    }

    private boolean isWithinVacation(Date currentDate) {
        boolean result = false;
        Date oneStart = CourseUtils
                .getInstance(context)
                .getDateFromSchedule(
                        MasterDBOffline.COLUMN_SCHEDULES_VACATION_ONE_START, prefManager.getSemester(),
                        prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram());
        Date oneEnd = CourseUtils
                .getInstance(context)
                .getDateFromSchedule(
                        MasterDBOffline.COLUMN_SCHEDULES_VACATION_ONE_END, prefManager.getSemester(),
                        prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram());

        Date twoStart = CourseUtils
                .getInstance(context)
                .getDateFromSchedule(
                        MasterDBOffline.COLUMN_SCHEDULES_VACATION_TWO_START, prefManager.getSemester(),
                        prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram());
        Date twoEnd = CourseUtils
                .getInstance(context)
                .getDateFromSchedule(
                        MasterDBOffline.COLUMN_SCHEDULES_VACATION_TWO_END, prefManager.getSemester(),
                        prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram());
        if (oneStart != null && oneEnd != null) {
            if (!(currentDate.before(oneStart) || currentDate.after(oneEnd))) {
                //if the date is not before the current date and not after the end date then we're ok
                result = true;
            }
        }
        if (twoStart != null && twoEnd != null) {
            if (!(currentDate.before(twoStart) || currentDate.after(twoEnd))) {
                //if the date is not before the current date and not after the end date then we're ok
                result = true;
            }
        }
        return result;
    }


}
