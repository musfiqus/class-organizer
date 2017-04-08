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
    private static final String SAVE_DAYDATA = "dayData";
    private static final String SAVE_LEVEL = "level";
    private static final String SAVE_TERM = "term";
    private static final String SAVE_SECTION = "section";
    private static final String SAVE_RECREATE = "recreate";
    private static final String SAVE_SNACK_TAG = "SnackTag";
    private static final String SAVE_SNACK = "Snack";
    private static final String SAVE_CONTACT = "Contact";

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.apply();
    }

    public void saveDayData(ArrayList<DayData> daydata) {
        editor.remove(SAVE_DAYDATA).apply();
        Gson gson = new Gson();
        String json = gson.toJson(daydata);
        editor.putString(SAVE_DAYDATA, json);
        editor.apply();
    }

    public ArrayList<DayData> getSavedDayData() {
        Gson gson = new Gson();
        String json = pref.getString(SAVE_DAYDATA, null);
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
        editor.remove(SAVE_LEVEL).apply();
        editor.putInt(SAVE_LEVEL, level);
        editor.apply();
    }

    public String getSection() {
        return pref.getString(SAVE_SECTION, null);
    }

    public int getTerm() {
        return pref.getInt(SAVE_TERM, -1);
    }

    public int getLevel() {
        return pref.getInt(SAVE_LEVEL, -1);
    }

    public void saveReCreate(boolean value) {
        editor.remove(SAVE_RECREATE).apply();
        editor.putBoolean(SAVE_RECREATE, value);
        editor.apply();
    }

    public boolean getReCreate() {
        return pref.getBoolean(SAVE_RECREATE, false);
    }

    public void saveSnackData(String snack) {
        editor.remove(SAVE_SNACK_TAG).apply();
        editor.putString(SAVE_SNACK_TAG, snack);
        editor.apply();
    }

    public String getSnackData() {
        return pref.getString(SAVE_SNACK_TAG, "done");
    }

    public void saveShowSnack(boolean snack) {
        editor.remove(SAVE_SNACK).apply();
        editor.putBoolean(SAVE_SNACK, snack);
        editor.apply();
    }

    public boolean showSnack() {
        return pref.getBoolean(SAVE_SNACK, false);
    }

    public void saveShowContact(boolean value) {
        editor.remove(SAVE_CONTACT).apply();
        editor.putBoolean(SAVE_CONTACT, value);
        editor.apply();
    }

    public boolean showContact() {
        return pref.getBoolean(SAVE_CONTACT, false);
    }
}

