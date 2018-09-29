package bd.edu.daffodilvarsity.classorganizer.utils;


import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import bd.edu.daffodilvarsity.classorganizer.data.ClassOrganizerDatabase;
import bd.edu.daffodilvarsity.classorganizer.model.Routine;

public class PreferenceGetter {
    private static final String TAG = "PreferenceGetter";

    public static final String USER_STUDENT = "Student";
    public static final String USER_TEACHER = "Teacher";

    private static final String PREF_FIRST_TIME_LAUNCH = "first_time_launch";
    private static final String PREF_DATABASE_VERSION = "room_database_version";
    private static final String PREF_USER_TYPE = "user_type";
    private static final String PREF_CAMPUS = "user_campus";
    private static final String PREF_DEPARTMENT = "user_department";
    private static final String PREF_PROGRAM = "user_program";
    private static final String PREF_INITIAL = "user_initial";
    private static final String PREF_LEVEL = "user_level";
    private static final String PREF_TERM = "user_term";
    private static final String PREF_SECTION = "user_section";
    private static final String PREF_SAVED_ROUTINE = "user_saved_routine";
    private static final String PREF_MODIFIED_ROUTINE_ORIGINAL = "user_modified_routine_original";
    private static final String PREF_DELETED_ROUTINE = "user_deleted_routine";
    private static final String PREF_NOTIFICATION_DELAY = "user_notification_delay";
    private static final String PREF_SEMESTER_ID = "current_semester_id";
    private static final String PREF_RAMADAN = "ramadan_preference";


    public static boolean isFirstTimeLaunch() {
        //returning reversed result cause default is false
        return !PreferenceHelper.getBoolean(PREF_FIRST_TIME_LAUNCH);
    }

    public static void setFirstTimeLaunch(boolean isFirstTime) {
        PreferenceHelper.set(PREF_FIRST_TIME_LAUNCH, !isFirstTime);
    }

    public static int getDatabaseVersion() {
        if (PreferenceHelper.getInt(PREF_DATABASE_VERSION) <= 0) {
            return ClassOrganizerDatabase.DATABASE_VERSION;
        }
        return PreferenceHelper.getInt(PREF_DATABASE_VERSION);
    }

    public static void setDatabaseVersion(int version) {
        PreferenceHelper.set(PREF_DATABASE_VERSION, version);
    }

    public static String getUserType() {
        return PreferenceHelper.getString(PREF_USER_TYPE);
    }

    public static void setUserType(String userType) {
        PreferenceHelper.set(PREF_USER_TYPE, userType);
    }

    public static String getCampus() {
        return PreferenceHelper.getString(PREF_CAMPUS);
    }

    public static void setCampus(String campus) {
        PreferenceHelper.set(PREF_CAMPUS, campus);
    }

    public static String getDepartment() {
        return PreferenceHelper.getString(PREF_DEPARTMENT);
    }

    public static void setDepartment(String department) {
        PreferenceHelper.set(PREF_DEPARTMENT, department);
    }

    public static String getProgram() {
        return PreferenceHelper.getString(PREF_PROGRAM);
    }

    public static void setProgram(String program) {
        PreferenceHelper.set(PREF_PROGRAM, program);
    }

    public static String getInitial() {
        return PreferenceHelper.getString(PREF_INITIAL);
    }

    public static void setInitial(String initial) {
        PreferenceHelper.set(PREF_INITIAL, initial);
    }

    public static int getLevel() {
        return PreferenceHelper.getInt(PREF_LEVEL);
    }

    public static void setLevel(int level) {
        PreferenceHelper.set(PREF_LEVEL, level);
    }

    public static int getTerm() {
        return PreferenceHelper.getInt(PREF_TERM);
    }

    public static void setTerm(int term) {
        PreferenceHelper.set(PREF_TERM, term);
    }

    public static String getSection() {
        return PreferenceHelper.getString(PREF_SECTION);
    }

    public static void setSection(String section) {
        PreferenceHelper.set(PREF_SECTION, section);
    }

    @NonNull
    public static List<Routine> getSavedRoutine() {
        String jsonString = PreferenceHelper.getString(PREF_SAVED_ROUTINE);
        Gson gson = new Gson();
        return new ArrayList<>(Objects.requireNonNull(gson.fromJson(jsonString, new TypeToken<List<Routine>>() {
        }.getType())));
    }

    public static void setSavedRoutine(List<Routine> routines) {
        Gson gson = new Gson();
        PreferenceHelper.set(PREF_SAVED_ROUTINE, gson.toJson(routines));
    }

    @NonNull
    public static List<Routine> getModifiedRoutineOriginal() {
        String jsonString = PreferenceHelper.getString(PREF_MODIFIED_ROUTINE_ORIGINAL);
        Gson gson = new Gson();
        return new ArrayList<>(Objects.requireNonNull(gson.fromJson(jsonString, new TypeToken<List<Routine>>() {
        }.getType())));
    }

    public static void setModifiedRoutineOriginal(List<Routine> routines) {
        Gson gson = new Gson();
        PreferenceHelper.set(PREF_MODIFIED_ROUTINE_ORIGINAL, gson.toJson(routines));
    }

    @NonNull
    public static List<Routine> getDeletedRoutine() {
        String jsonString = PreferenceHelper.getString(PREF_DELETED_ROUTINE);
        Gson gson = new Gson();
        return new ArrayList<>(Objects.requireNonNull(gson.fromJson(jsonString, new TypeToken<List<Routine>>() {
        }.getType())));
    }

    public static void setDeletedRoutine(List<Routine> routines) {
        Gson gson = new Gson();
        PreferenceHelper.set(PREF_DELETED_ROUTINE, gson.toJson(routines));
    }

    public static boolean isStudent() {
        return USER_STUDENT.equals(getUserType());
    }

    public static int getSemesterId() {
        return PreferenceHelper.getInt(PREF_SEMESTER_ID);
    }

    public static void setSemesterId(int semesterId) {
        PreferenceHelper.set(PREF_SEMESTER_ID, semesterId);
    }

    public static int getNotificationDelay() {
        int delay = PreferenceHelper.getInt(PREF_NOTIFICATION_DELAY);
        if (delay < 15) {
            delay = 15;
        }
        return delay;
    }

    public static void setNotificationDelay(int minutes) {
        PreferenceHelper.set(PREF_NOTIFICATION_DELAY, minutes);
    }

    public static boolean isRamadanEnabled() {
        return PreferenceHelper.getBoolean(PREF_RAMADAN);
    }

    public static void printPref() {
        Log.e(TAG, "printPref: User: "+getUserType()+
                " Campus: "+getCampus()+" Department: "+getDepartment()+" Program: "+getProgram()
        +" Initial: "+getInitial()+ " Level: "+getLevel()+" Term: "+getTerm()+" Section: "+getSection());
    }

}
