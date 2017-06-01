package bd.edu.daffodilvarsity.classorganizer.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import bd.edu.daffodilvarsity.classorganizer.data.DayData;
import bd.edu.daffodilvarsity.classorganizer.data.RecoverDayData;

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
    private static final String PREF_ADDED_DAYDATA = "added_daydata";
    private static final String PREF_SAVED_DAYDATA = "saved_daydata";
    private static final String PREF_DELETED_DAYDATA = "deleted_daydata";
    private static final String PREF_EDITED_DAYDATA = "edited_daydata";
    private static final String PREF_SUPPRESSED_UPDATE_DB_VERSION = "SuppressedUpdateDbVersion";
    private static final String HAS_CAMPUS_SETTINGS_CHANGED = "HasCampusChanged";
    private static final String IS_CAMPUS_CHANGE_ALERT_DISABLED = "IsCampusChangeAlertDisabled";
    private static final String IS_ROUTINE_UPDATED_ONLINE = "IsRoutineUpdatedOnline";
    private static final String IS_RAMADAN_GREETINGS_ENABLED = "IsRamadanGreetingsEnabled";
    private static final String IS_RECOVERY_FINISHED = "IsRecoveryFinished";
    public static final String SAVE_DATA_TAG = "save";
    public static final String ADD_DATA_TAG = "add";
    public static final String EDIT_DATA_TAG = "edit";
    public static final String DELETE_DATA_TAG = "delete";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime).apply();
    }

    public void saveDayData(ArrayList<DayData> daydata) {
        editor.remove(PREF_DAYDATA).apply();
        Gson gson = new Gson();
        String json = gson.toJson(daydata);
        editor.putString(PREF_DAYDATA, json).apply();
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
        editor.putString(SAVE_SECTION, section).apply();
    }

    public void saveTerm(int term) {
        editor.remove(SAVE_TERM).apply();
        editor.putInt(SAVE_TERM, term).apply();
    }

    public void saveLevel(int level) {
        editor.remove(PREF_LEVEL).apply();
        editor.putInt(PREF_LEVEL, level).apply();
    }

    public void saveSemester(String semester) {
        editor.remove(SAVE_SEMESTER).apply();
        editor.putString(SAVE_SEMESTER, semester).apply();
    }

    public void saveShowSnack(boolean snack) {
        editor.remove(SAVE_SNACK).apply();
        editor.putBoolean(SAVE_SNACK, snack).apply();
    }

    public void saveReCreate(boolean value) {
        editor.remove(SAVE_RECREATE).apply();
        editor.putBoolean(SAVE_RECREATE, value).apply();
    }

    public void saveSnackData(String snack) {
        editor.remove(SAVE_SNACK_TAG).apply();
        editor.putString(SAVE_SNACK_TAG, snack).apply();
    }

    public void saveDatabaseVersion(int version) {
        editor.remove(SAVE_DATABASE_VERSION).apply();
        editor.putInt(SAVE_DATABASE_VERSION, version).apply();
    }

    public void saveCampus(String campus) {
        campus = campus.toLowerCase();
        editor.remove(SAVE_CAMPUS).apply();
        editor.putString(SAVE_CAMPUS, campus).apply();
    }

    public void saveDept(String dept) {
        dept = dept.toLowerCase();
        editor.remove(SAVE_DEPT).apply();
        editor.putString(SAVE_DEPT, dept).apply();
    }

    public void saveProgram(String program) {
        program = program.toLowerCase();
        editor.remove(SAVE_PROGRAM).apply();
        editor.putString(SAVE_PROGRAM, program).apply();
    }

    public void saveModifiedData(DayData dayData, String which, boolean reset) {
        if (!reset) {
            Gson gson = new Gson();
            ArrayList<DayData> previousData = getModifiedData(which);
            if (which.equalsIgnoreCase(ADD_DATA_TAG) && previousData != null) {
                editor.remove(PREF_ADDED_DAYDATA).apply();
                if (!isDuplicate(previousData, dayData)) {
                    previousData.add(dayData);
                }
                String json = gson.toJson(previousData);
                editor.putString(PREF_ADDED_DAYDATA, json);
                editor.apply();
            } else if (which.equalsIgnoreCase(ADD_DATA_TAG) && previousData == null) {
                editor.remove(PREF_ADDED_DAYDATA).apply();
                ArrayList<DayData> newArray = new ArrayList<>();
                newArray.add(dayData);
                String json = gson.toJson(newArray);
                editor.putString(PREF_ADDED_DAYDATA, json);
                editor.apply();
            } else if (which.equalsIgnoreCase(SAVE_DATA_TAG) && previousData != null) {
                editor.remove(PREF_SAVED_DAYDATA).apply();
                if (!isDuplicate(previousData, dayData)) {
                    previousData.add(dayData);
                }
                String json = gson.toJson(previousData);
                editor.putString(PREF_SAVED_DAYDATA, json);
                editor.apply();
            } else if (which.equalsIgnoreCase(SAVE_DATA_TAG) && previousData == null) {
                editor.remove(PREF_SAVED_DAYDATA).apply();
                ArrayList<DayData> newArray = new ArrayList<>();
                newArray.add(dayData);
                String json = gson.toJson(newArray);
                editor.putString(PREF_SAVED_DAYDATA, json);
                editor.apply();
            } else if (which.equalsIgnoreCase(EDIT_DATA_TAG) && previousData != null) {
                editor.remove(PREF_EDITED_DAYDATA).apply();
                if (!isDuplicate(previousData, dayData)) {
                    previousData.add(dayData);
                }
                String json = gson.toJson(previousData);
                editor.putString(PREF_EDITED_DAYDATA, json);
                editor.apply();
            } else if (which.equalsIgnoreCase(EDIT_DATA_TAG) && previousData == null) {
                editor.remove(PREF_EDITED_DAYDATA).apply();
                ArrayList<DayData> newArray = new ArrayList<>();
                newArray.add(dayData);
                String json = gson.toJson(newArray);
                editor.putString(PREF_EDITED_DAYDATA, json);
                editor.apply();
            } else if (which.equalsIgnoreCase(DELETE_DATA_TAG) && previousData != null) {
                editor.remove(PREF_DELETED_DAYDATA).apply();
                if (!isDuplicate(previousData, dayData)) {
                    previousData.add(dayData);
                }
                String json = gson.toJson(previousData);
                editor.putString(PREF_DELETED_DAYDATA, json);
                editor.apply();
            } else if (which.equalsIgnoreCase(DELETE_DATA_TAG) && previousData == null) {
                editor.remove(PREF_DELETED_DAYDATA).apply();
                ArrayList<DayData> newArray = new ArrayList<>();
                newArray.add(dayData);
                String json = gson.toJson(newArray);
                editor.putString(PREF_DELETED_DAYDATA, json);
                editor.apply();
            }
        } else {
            if (which.equalsIgnoreCase(ADD_DATA_TAG)) {
                editor.remove(PREF_ADDED_DAYDATA).apply();
            } else if (which.equalsIgnoreCase(EDIT_DATA_TAG)) {
                editor.remove(PREF_EDITED_DAYDATA).apply();
            } else if (which.equalsIgnoreCase(SAVE_DATA_TAG)) {
                editor.remove(PREF_SAVED_DAYDATA).apply();
            } else if (which.equalsIgnoreCase(DELETE_DATA_TAG)) {
                editor.remove(PREF_DELETED_DAYDATA).apply();
            }
        }
    }

    public void setHasCampusSettingsChanged(boolean value) {
        editor.remove(HAS_CAMPUS_SETTINGS_CHANGED).apply();
        editor.putBoolean(HAS_CAMPUS_SETTINGS_CHANGED, value).apply();
    }

    public void setIsCampusChangeAlertDisabled(boolean value) {
        editor.remove(IS_CAMPUS_CHANGE_ALERT_DISABLED).apply();
        editor.putBoolean(IS_CAMPUS_CHANGE_ALERT_DISABLED, value).apply();
    }

    public boolean isCampusChangeAlertDisabled() {
        return pref.getBoolean(IS_CAMPUS_CHANGE_ALERT_DISABLED, false);
    }

    public boolean hasCampusSettingsChanged() {
        return pref.getBoolean(HAS_CAMPUS_SETTINGS_CHANGED, false);
    }

    public ArrayList<DayData> getModifiedData(String which) {
        Gson gson = new Gson();
        if (which.equalsIgnoreCase(ADD_DATA_TAG)) {
            String json = pref.getString(PREF_ADDED_DAYDATA, null);
            if (json != null) {
                Type type = new TypeToken<ArrayList<DayData>>() {
                }.getType();
                return gson.fromJson(json, type);
            }
            return null;
        } else if (which.equalsIgnoreCase(SAVE_DATA_TAG)) {
            String json = pref.getString(PREF_SAVED_DAYDATA, null);
            if (json != null) {
                Type type = new TypeToken<ArrayList<DayData>>() {
                }.getType();
                return gson.fromJson(json, type);
            }
            return null;
        } else if (which.equalsIgnoreCase(EDIT_DATA_TAG)) {
            String json = pref.getString(PREF_EDITED_DAYDATA, null);
            if (json != null) {
                Type type = new TypeToken<ArrayList<DayData>>() {
                }.getType();
                return gson.fromJson(json, type);
            }
            return null;
        } else if (which.equalsIgnoreCase(DELETE_DATA_TAG)) {
            String json = pref.getString(PREF_DELETED_DAYDATA, null);
            if (json != null) {
                Type type = new TypeToken<ArrayList<DayData>>() {
                }.getType();
                return gson.fromJson(json, type);
            }
            return null;
        }
        return null;
    }

    public void resetModification(boolean add, boolean edit, boolean save, boolean delete) {
        if (add) {
            saveModifiedData(null, ADD_DATA_TAG, true);
        }
        if (edit) {
            saveModifiedData(null, EDIT_DATA_TAG, true);
        }
        if (save) {
            saveModifiedData(null, SAVE_DATA_TAG, true);
        }
        if (delete) {
            saveModifiedData(null, DELETE_DATA_TAG, true);
        }
        RoutineLoader routineLoader = new RoutineLoader(getLevel(), getTerm(), getSection(), _context, getDept(), getCampus(), getProgram());
        ArrayList<DayData> loadedData = routineLoader.loadRoutine(true);
        saveDayData(loadedData);
    }

    public boolean isDuplicate(ArrayList<DayData> list, DayData object) {
        if (list == null) {
            return false;
        }
        if (list.size() == 0) {
            return false;
        }
        for (DayData dayData : list) {
            if (dayData.equals(object)) {
                return true;
            }
        }
        return false;
    }

    public void setUpdatedOnline(boolean value) {
        editor.remove(IS_ROUTINE_UPDATED_ONLINE).apply();
        editor.putBoolean(IS_ROUTINE_UPDATED_ONLINE, value).apply();
    }

    public void setSuppressedUpdateDbVersion(int dbVersion) {
        editor.remove(PREF_SUPPRESSED_UPDATE_DB_VERSION).apply();
        editor.putInt(PREF_SUPPRESSED_UPDATE_DB_VERSION, dbVersion).apply();
    }

    public void showRamadanGreetings(boolean value) {
        editor.remove(IS_RAMADAN_GREETINGS_ENABLED).apply();
        editor.putBoolean(IS_RAMADAN_GREETINGS_ENABLED, value).apply();
    }

    public boolean isRamadanGreetingsEnabled() {
        return pref.getBoolean(IS_RAMADAN_GREETINGS_ENABLED, true);
    }

    public int getSuppressedUpdateDbVersion() {
        return pref.getInt(PREF_SUPPRESSED_UPDATE_DB_VERSION, 0);
    }

    public boolean isUpdatedOnline() {
        return pref.getBoolean(IS_ROUTINE_UPDATED_ONLINE, false);
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
        return pref.getInt(SAVE_DATABASE_VERSION, 1);
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

    private void setRecoveryFinished(boolean value) {
        editor.remove(IS_RECOVERY_FINISHED).apply();
        editor.putBoolean(IS_RECOVERY_FINISHED, value).apply();
    }

    private boolean isRecoveryFinished() {
        return pref.getBoolean(IS_RECOVERY_FINISHED, false);
    }

    public void recoverSavedData() {
        if (!isRecoveryFinished()) {
            Log.e("Okay", "I'm working");
            String edit = pref.getString(PREF_EDITED_DAYDATA, null);
            String delete = pref.getString(PREF_DELETED_DAYDATA, null);
            String add = pref.getString(PREF_ADDED_DAYDATA, null);
            String save = pref.getString(PREF_SAVED_DAYDATA, null);
            boolean rEdit = checkValidRecoveryObject(edit);
            boolean rDelete = checkValidRecoveryObject(delete);
            boolean rAdd = checkValidRecoveryObject(add);
            boolean rSave = checkValidRecoveryObject(save);
            resetModification(rAdd, rEdit, rSave, rDelete);
            recoverData(edit, EDIT_DATA_TAG);
            recoverData(delete, DELETE_DATA_TAG);
            recoverData(add, ADD_DATA_TAG);
            recoverData(save, SAVE_DATA_TAG);
            setRecoveryFinished(true);
        }
    }

    private boolean checkValidRecoveryObject(String json) {
        if (json != null) {
            if (json.substring(0, 6).equalsIgnoreCase("[{\"a\":")) {
                return true;
            }
        }
        return false;
    }

    private void recoverData(String json, final String DATA_TAG) {
        if (json != null) {
            if (json.substring(0, 6).equalsIgnoreCase("[{\"a\":")) {
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<RecoverDayData>>() {
                }.getType();
                ArrayList<RecoverDayData> recoverDayData = gson.fromJson(json, type);
                CourseUtils.CourseTitleGenerator titleGenerator = CourseUtils.CourseTitleGenerator.getInstance(_context);
                for (RecoverDayData eachData : recoverDayData) {
                    String title = titleGenerator.getCourseTitle(eachData.getA(), getDept(), getProgram());
                    DayData dayData = new DayData(eachData.getA(), eachData.getB(), eachData.getC(), eachData.getD(), eachData.getE(), eachData.getF(), eachData.getG(), eachData.getH(), eachData.getI(), title);
                    saveModifiedData(dayData, DATA_TAG, false);
                }
            }
        }
    }
}

