package bd.edu.daffodilvarsity.classorganizer.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import bd.edu.daffodilvarsity.classorganizer.ClassOrganizer;
import bd.edu.daffodilvarsity.classorganizer.data.Repository;
import bd.edu.daffodilvarsity.classorganizer.model.Routine;
import bd.edu.daffodilvarsity.classorganizer.model.RoutineSemesterModel;
import bd.edu.daffodilvarsity.classorganizer.model.Semester;
import bd.edu.daffodilvarsity.classorganizer.receiver.NotificationPublisher;
import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Created by Mushfiqus Salehin on 6/6/2017.
 * musfiqus@gmail.com
 */

public class AlarmHelper {
    private Repository repository;

    public static String TAG_ALARM_ROUTINE_OBJECT = "routine_object";
    public static String TAG_ALARM_SEMESTER_OBJECT = "semester_object";
    public static String TAG_ALARM_INDEX = "index";
    public static String TAG_ALARM_DAY = "day";
    public static String TAG_ALARM_HOUR = "hour";
    public static String TAG_ALARM_TIME_BEFORE = "time_before";
    public static String TAG_ALARM_BUNDLE_DATA = "bundled_data";

    private static final String TAG = "AlarmHelper";

    public AlarmHelper() {

        this.repository = Repository.getInstance();
    }

    private void startAll(RoutineSemesterModel routineSemesterModel) {
        List<Routine> routines = routineSemesterModel.routines;
        Semester semester = routineSemesterModel.semester;
        boolean isRamadanTime = PreferenceGetter.isRamadanEnabled();
        if (routines != null) {
            for (int i = 0; i < routines.size(); i++) {
                if (routines.get(i) != null && !routines.get(i).isMuted()) {
                    if (routines.get(i).getDay() != null && routines.get(i).getTime() != null) {
                        int dayOfWeek = calculateDay(routines.get(i).getDay());
                        int time[] = calculateTime(isRamadanTime ?  routines.get(i).getAltTimeWeight(): routines.get(i).getTimeWeight());
                        if (dayOfWeek != -1) {
                            scheduleAlarm(dayOfWeek, i, time[0], time[1], routines.get(i), semester);
                        }
                    }
                }
            }
        }
    }

    private void cancelAll(List<Routine> routines) {
        boolean isRamadanTime = PreferenceGetter.isRamadanEnabled();
        if (routines != null) {
            for (int i = 0; i < routines.size(); i++) {
                if (routines.get(i) != null) {
                    if (routines.get(i).getDay() != null && routines.get(i).getTime() != null) {
                        int dayOfWeek = calculateDay(routines.get(i).getDay());
                        int time[] = calculateTime(isRamadanTime ? routines.get(i).getAltTimeWeight(): routines.get(i).getTimeWeight());
                        if (dayOfWeek != -1) {
                            cancelAlarm(dayOfWeek, i, time[0], time[1], routines.get(i));
                        }
                    }
                }

            }
        }
    }

    public Completable startAllAlarms() {
        return getRoutineSemesterCombined()
                .flatMapCompletable(routineSemesterModel -> Completable.fromAction(() -> startAll(routineSemesterModel)));
    }

    public Completable cancelAllAlarms() {
        return repository
                .getSavedRoutine()
                .flatMapCompletable(routines -> Completable.fromAction(() -> cancelAll(routines)));
    }

    public Completable restartAlarms() {
        return Completable
                .merge(Arrays.asList(cancelAllAlarms(), startAllAlarms()));
    }

    private void cancelAlarm(int dayOfWeek, int index, int hour, int timeBefore, Routine routine) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, timeBefore);

        // Check we aren't setting it in the past or present which would trigger it to fire instantly
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 7);
        }
        Intent dayDataIntent = new Intent(ClassOrganizer.getInstance(), NotificationPublisher.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(TAG_ALARM_ROUTINE_OBJECT, routine);
        bundle.putInt(TAG_ALARM_INDEX, index);
        bundle.putInt(TAG_ALARM_DAY, dayOfWeek);
        bundle.putInt(TAG_ALARM_HOUR, hour);
        bundle.putInt(TAG_ALARM_TIME_BEFORE, timeBefore);
        dayDataIntent.putExtra(TAG_ALARM_BUNDLE_DATA, bundle);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(ClassOrganizer.getInstance(), index, dayDataIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) ClassOrganizer.getInstance().getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    public void scheduleAlarm(int dayOfWeek, int index, int hour, int timeBefore, Routine routine, Semester semester) {
        if (!routine.isMuted()) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, timeBefore);

            // Check we aren't setting it in the past or present which would trigger it to fire instantly
            if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 7);
            }
            Date date = calendar.getTime();

            if (isDateValidForAlarm(date, semester)) {
                Log.d(TAG, "scheduleAlarm: Date: "+ date);
                Intent routineIntent = new Intent(ClassOrganizer.getInstance(), NotificationPublisher.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable(AlarmHelper.TAG_ALARM_ROUTINE_OBJECT, routine);
                bundle.putParcelable(TAG_ALARM_SEMESTER_OBJECT, semester);
                bundle.putInt(AlarmHelper.TAG_ALARM_INDEX, index);
                bundle.putInt(AlarmHelper.TAG_ALARM_DAY, dayOfWeek);
                bundle.putInt(AlarmHelper.TAG_ALARM_HOUR, hour);
                bundle.putInt(AlarmHelper.TAG_ALARM_TIME_BEFORE, timeBefore);
                routineIntent.putExtra(AlarmHelper.TAG_ALARM_BUNDLE_DATA, bundle);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(ClassOrganizer.getInstance(), index, routineIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) ClassOrganizer.getInstance().getSystemService(Context.ALARM_SERVICE);
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

    private int[] calculateTime(String timeWeight) {
        int[] calculatedTime = new int[2];
        try {
            String[] times = timeWeight.split("\\.");
            calculatedTime[0] = Integer.valueOf(times[0]);
            calculatedTime[1] = Integer.valueOf(times[1]) - PreferenceGetter.getNotificationDelay();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return calculatedTime;

    }





    private boolean isDateValidForAlarm(Date currentDate, Semester semester) {
        if (!isWithinSemester(currentDate, semester)) {
            Log.d(TAG, "isDateValidForAlarm: Date: "+currentDate+" is outside of semester");
            return false;
        }
        if (isWithinMid(currentDate, semester)) {
            Log.d(TAG, "isDateValidForAlarm: Date: "+currentDate+" is during mid");
            return false;
        }
        if (isWithinVacation(currentDate, semester)) {
            Log.d(TAG, "isDateValidForAlarm: Date: "+currentDate+" is during mid");
            return false;
        }
        Log.d(TAG, "isDateValidForAlarm: Date: "+currentDate+" is valid for alarm");
        return true;
    }

    private boolean isWithinSemester(Date currentDate, Semester semester) {
        boolean result = false;
        Date classStart = new Date(semester.getClassStart());
        Date classEnd = new Date(semester.getClassEnd());
        if (!(currentDate.before(classStart) || currentDate.after(classEnd))) {
            //if the date is not before the current date and not after the end date then we're ok
            result = true;
        }
        return result;
    }

    private boolean isWithinMid(Date currentDate, Semester semester) {
        boolean result = false;
        Date midStart = new Date(semester.getMidStart());
        Date midEnd = new Date(semester.getMidEnd());
        if (!(currentDate.before(midStart) || currentDate.after(midEnd))) {
            //if the date is not before the current date and not after the end date then we're ok
            result = true;
        }
        return result;
    }

    private boolean isWithinVacation(Date currentDate, Semester semester) {
        boolean result = false;
        Date oneStart = new Date(semester.getVacationOneStart());
        Date oneEnd = new Date(semester.getVacationOneEnd());

        Date twoStart = new Date(semester.getVacationTwoStart());
        Date twoEnd = new Date(semester.getVacationTwoEnd());
        if (!(currentDate.before(oneStart) || currentDate.after(oneEnd))) {
            //if the date is not before the current date and not after the end date then we're ok
            result = true;
        }
        if (!(currentDate.before(twoStart) || currentDate.after(twoEnd))) {
            //if the date is not before the current date and not after the end date then we're ok
            result = true;
        }
        return result;
    }

    private Single<RoutineSemesterModel> getRoutineSemesterCombined() {
        return Single.zip(
                repository.getSavedRoutine(),
                repository.getSemesterFromDb(),
                RoutineSemesterModel::new
        );
    }

}
