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

public class PrefManager {
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

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.apply();
    }

    public void saveDayData(ArrayList<DayData> daydata) {
        editor.remove(PREF_DAYDATA).apply();
        Gson gson = new Gson();
        String json = gson.toJson(daydata);
        editor.putString(PREF_DAYDATA, json);
        editor.apply();
    }

    public ArrayList<DayData> getSavedDayData() {
        Gson gson = new Gson();
        String json = pref.getString(PREF_DAYDATA, null);
        Type type = new TypeToken<ArrayList<DayData>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public void saveSection(String section) {
        editor.remove(SAVE_SECTION).apply();
        editor.putString(SAVE_SECTION, section);
        editor.apply();
    }

    public void saveTerm(int term) {
        editor.remove(SAVE_TERM).apply();
        editor.putInt(SAVE_TERM, term);
        editor.apply();
    }

    public void saveLevel(int level) {
        editor.remove(PREF_LEVEL).apply();
        editor.putInt(PREF_LEVEL, level);
        editor.apply();
    }

    public void saveSemester(String semester) {
        editor.remove(SAVE_SEMESTER).apply();
        editor.putString(SAVE_SEMESTER, semester);
        editor.apply();
    }

    public void saveShowSnack(boolean snack) {
        editor.remove(SAVE_SNACK).apply();
        editor.putBoolean(SAVE_SNACK, snack);
        editor.apply();
    }

    public void saveReCreate(boolean value) {
        editor.remove(SAVE_RECREATE).apply();
        editor.putBoolean(SAVE_RECREATE, value);
        editor.apply();
    }

    public void saveSnackData(String snack) {
        editor.remove(SAVE_SNACK_TAG).apply();
        editor.putString(SAVE_SNACK_TAG, snack);
        editor.apply();
    }

    public void saveDatabaseVersion(int version) {
        editor.remove(SAVE_DATABASE_VERSION).apply();
        editor.putInt(SAVE_DATABASE_VERSION, version);
        editor.apply();
    }

    public void saveCampus(String campus) {
        editor.remove(SAVE_CAMPUS).apply();
        editor.putString(SAVE_CAMPUS, campus);
        editor.apply();
    }

    public void saveDept(String dept) {
        editor.remove(SAVE_DEPT).apply();
        editor.putString(SAVE_DEPT, dept);
        editor.apply();
    }

    public void saveProgram(String program) {
        editor.remove(SAVE_PROGRAM).apply();
        editor.putString(SAVE_PROGRAM, program);
        editor.apply();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public String getSection() {
        return pref.getString(SAVE_SECTION, null);
    }

    public int getTerm() {
        return pref.getInt(SAVE_TERM, -1);
    }

    public int getLevel() {
        return pref.getInt(PREF_LEVEL, -1);
    }

    public boolean getReCreate() {
        return pref.getBoolean(SAVE_RECREATE, false);
    }

    public String getSnackData() {
        return pref.getString(SAVE_SNACK_TAG, "done");
    }

    public boolean showSnack() {
        return pref.getBoolean(SAVE_SNACK, false);
    }

    public String getSemester() {
        return pref.getString(SAVE_SEMESTER, null);
    }

    public int getDatabaseVersion() {
        return pref.getInt(SAVE_DATABASE_VERSION, 0);
    }

    public String getCampus() {
        return pref.getString(SAVE_CAMPUS, null);
    }

    public String getDept() {
        return pref.getString(SAVE_DEPT, null);
    }

    public String getProgram() {
        return pref.getString(SAVE_PROGRAM, null);
    }
}

