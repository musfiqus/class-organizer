package bd.edu.daffodilvarsity.classorganizer;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by musfiqus on 3/25/2017.
 */

class PrefManager {
    // Shared preferences file name
    private static final String PREF_NAME = "diu-class-organizer";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String PREF_DAYDATA = "dayData";
    private static final String PREF_LEVEL = "level";
    private static final String SAVE_TERM = "term";
    private static final String SAVE_SECTION = "section";
    private static final String SAVE_RECREATE = "recreate";
    private static final String SAVE_SNACK_TAG = "SnackTag";
    private static final String SAVE_SNACK = "Snack";
    private static final String SAVE_SEMESTER = "Semester";
    private static final String SAVE_DATABASE_VERSION = "dbversion";
    private static final String SAVE_CAMPUS = "campus";
    private static final String SAVE_DEPT = "department";
    private static final String SAVE_PROGRAM = "program";
    private static final String PREF_DELETED_DAYDATA = "deleted_daydata";
    private static final String PREF_EDITED_DAYDATA = "edited_daydata";
    private static final String PREF_SNAPSHOT_DAYDATA = "snapshot_daydata";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;

    PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.apply();
    }

    void saveDayData(ArrayList<DayData> daydata) {
        editor.remove(PREF_DAYDATA).apply();
        Gson gson = new Gson();
        String json = gson.toJson(daydata);
        editor.putString(PREF_DAYDATA, json);
        editor.apply();
    }

    ArrayList<DayData> getSavedDayData() {
        Gson gson = new Gson();
        String json = pref.getString(PREF_DAYDATA, null);
        Type type = new TypeToken<ArrayList<DayData>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    void saveSection(String section) {
        editor.remove(SAVE_SECTION).apply();
        editor.putString(SAVE_SECTION, section);
        editor.apply();
    }

    void saveTerm(int term) {
        editor.remove(SAVE_TERM).apply();
        editor.putInt(SAVE_TERM, term);
        editor.apply();
    }

    void saveLevel(int level) {
        editor.remove(PREF_LEVEL).apply();
        editor.putInt(PREF_LEVEL, level);
        editor.apply();
    }

    void saveSemester(String semester) {
        editor.remove(SAVE_SEMESTER).apply();
        editor.putString(SAVE_SEMESTER, semester);
        editor.apply();
    }

    void saveShowSnack(boolean snack) {
        editor.remove(SAVE_SNACK).apply();
        editor.putBoolean(SAVE_SNACK, snack);
        editor.apply();
    }

    void saveReCreate(boolean value) {
        editor.remove(SAVE_RECREATE).apply();
        editor.putBoolean(SAVE_RECREATE, value);
        editor.apply();
    }

    void saveSnackData(String snack) {
        editor.remove(SAVE_SNACK_TAG).apply();
        editor.putString(SAVE_SNACK_TAG, snack);
        editor.apply();
    }

    void saveDatabaseVersion(int version) {
        editor.remove(SAVE_DATABASE_VERSION).apply();
        editor.putInt(SAVE_DATABASE_VERSION, version);
        editor.apply();
    }

    void saveCampus(String campus) {
        editor.remove(SAVE_CAMPUS).apply();
        editor.putString(SAVE_CAMPUS, campus);
        editor.apply();
    }

    void saveDept(String dept) {
        editor.remove(SAVE_DEPT).apply();
        editor.putString(SAVE_DEPT, dept);
        editor.apply();
    }

    void saveProgram(String program) {
        editor.remove(SAVE_PROGRAM).apply();
        editor.putString(SAVE_PROGRAM, program);
        editor.apply();
    }

    void saveDeletedDayData(DayData dayData, boolean reset) {
        ArrayList<DayData> deletedArray;
        editor.remove(PREF_DELETED_DAYDATA).apply();
        Gson gson = new Gson();
        if (!reset) {
            if (getDeletedDayData() != null) {
                deletedArray = getDeletedDayData();
            } else {
                deletedArray = new ArrayList<>();
            }
            deletedArray.add(dayData);
            String json = gson.toJson(deletedArray);
            editor.putString(PREF_DELETED_DAYDATA, json);
            editor.apply();
        } else {
            editor.putString(PREF_DELETED_DAYDATA, null);
            editor.apply();
        }
    }

    void resetModification() {
        saveEditedDayData(null, true);
        saveDeletedDayData(null, true);
        RoutineLoader routineLoader = new RoutineLoader(getLevel(), getTerm(), getSection(), _context, getDept(), getCampus(), getProgram());
        ArrayList<DayData> loadedData = routineLoader.loadRoutine(false);
        saveDayData(loadedData);
    }

    void saveEditedDayData(DayData dayData, boolean reset) {
        ArrayList<DayData> editedArray;
        editor.remove(PREF_EDITED_DAYDATA).apply();
        Gson gson = new Gson();
        if (!reset) {
            if (getEditedDayData() != null) {
                editedArray = getEditedDayData();
            } else {
                editedArray = new ArrayList<>();
            }
            editedArray.add(dayData);
            String json = gson.toJson(editedArray);
            editor.putString(PREF_EDITED_DAYDATA, json);
            editor.apply();
        } else {
            editor.putString(PREF_EDITED_DAYDATA, null);
            editor.apply();
        }
    }

    //This will be removed in future
    void deleteSnapshotDayData() {
        editor.remove(PREF_SNAPSHOT_DAYDATA).apply();
    }


    ArrayList<DayData> getEditedDayData() {
        Gson gson = new Gson();
        String json = pref.getString(PREF_EDITED_DAYDATA, null);
        if (json != null) {
            Type type = new TypeToken<ArrayList<DayData>>() {
            }.getType();
            return gson.fromJson(json, type);
        }
        return null;
    }

    ArrayList<DayData> getDeletedDayData() {
        Gson gson = new Gson();
        String json = pref.getString(PREF_DELETED_DAYDATA, null);
        if (json != null) {
            Type type = new TypeToken<ArrayList<DayData>>() {
            }.getType();
            return gson.fromJson(json, type);
        }
        return null;
    }

    boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    String getSection() {
        return pref.getString(SAVE_SECTION, null);
    }

    int getTerm() {
        return pref.getInt(SAVE_TERM, -1);
    }

    int getLevel() {
        return pref.getInt(PREF_LEVEL, -1);
    }

    boolean getReCreate() {
        return pref.getBoolean(SAVE_RECREATE, false);
    }

    String getSnackData() {
        return pref.getString(SAVE_SNACK_TAG, "done");
    }

    boolean showSnack() {
        return pref.getBoolean(SAVE_SNACK, false);
    }

    String getSemester() {
        return pref.getString(SAVE_SEMESTER, null);
    }

    int getDatabaseVersion() {
        return pref.getInt(SAVE_DATABASE_VERSION, 0);
    }

    String getCampus() {
        return pref.getString(SAVE_CAMPUS, null);
    }

    String getDept() {
        return pref.getString(SAVE_DEPT, null);
    }

    String getProgram() {
        return pref.getString(SAVE_PROGRAM, null);
    }
}

