package bd.edu.daffodilvarsity.classorganizer.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import java.util.ArrayList;

import bd.edu.daffodilvarsity.classorganizer.data.DayData;

/**
 * Created by Mushfiqus Salehin on 4/1/2017.
 * musfiqus@gmail.com
 */

public class RoutineLoader {

    private PrefManager prefManager;
    private int level;
    private int term;
    private String section;
    private Context context;
    private String dept;
    private String campus;
    private String program;

    public RoutineLoader(int level, int term, String section, Context context, String dept, String campus, String program) {
        this.level = level;
        this.term = term;
        this.section = section;
        this.context = context;
        this.dept = dept;
        this.campus = campus;
        this.program = program;
        prefManager = new PrefManager(context);
    }

    private int getSemester() {
        return getSemester(this.level, this.term);
    }

    public static int getSemester(int level, int term) {
        if (level == 0) {
            return 1 + term;
        } else if (level == 1) {
            return 4 + term;
        } else if (level == 2) {
            return 7 + term;
        } else {
            return 10 + term;
        }
    }

    private ArrayList<String> courseCodeGenerator(int semester) {
        return CourseUtils.getInstance(context).getCourseCodes(semester, campus, dept, program);
    }

    public ArrayList<DayData> loadRoutine(boolean loadPersonal) {
        //Generating course codes from generated semester
        ArrayList<String> courseCodes = courseCodeGenerator(getSemester());
        ArrayList<DayData> vanillaRoutine;
        //Initializing DB Helper

        if (prefManager.isUpdatedOnline()) {
            UpdatedDatabaseHelper updatedDatabaseHelper = UpdatedDatabaseHelper.getInstance(context, prefManager.getDatabaseVersion());
            vanillaRoutine = updatedDatabaseHelper.getDayData(courseCodes, section, level, term, dept, campus, program);
        } else {
            DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
            vanillaRoutine = databaseHelper.getDayData(courseCodes, section, level, term, dept, campus, program);
        }
        if (!loadPersonal) {
            return vanillaRoutine;
        } else {
            return loadPersonalDayData(vanillaRoutine);
        }
    }

    public ArrayList<DayData> loadPersonalDayData(ArrayList<DayData> loadedDayData) {
        if (loadedDayData.size() > 0) {
            //Checking for modified daydata
            ArrayList<DayData> addDayData = prefManager.getModifiedData(PrefManager.ADD_DATA_TAG);
            ArrayList<DayData> editDayData = prefManager.getModifiedData(PrefManager.EDIT_DATA_TAG);
            ArrayList<DayData> saveDayData = prefManager.getModifiedData(PrefManager.SAVE_DATA_TAG);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            boolean isLimited = preferences.getBoolean("limit_preference", true);
            if (addDayData != null) {
                for (DayData eachEditedDayData : addDayData) {
                    if (eachEditedDayData != null) {
                        if (!(prefManager.isDuplicate(loadedDayData, eachEditedDayData))) {
                            if (isLimited) {
                                if (this.section.equalsIgnoreCase(eachEditedDayData.getSection()) && this.term == eachEditedDayData.getTerm() && this.level == eachEditedDayData.getLevel()) {
                                    loadedDayData.add(eachEditedDayData);
                                }
                            } else {
                                loadedDayData.add(eachEditedDayData);
                            }
                        }
                    }
                }
            }
            if (editDayData != null) {
                for (DayData eachEditedDayData : editDayData) {
                    if (eachEditedDayData != null) {
                        if (!(prefManager.isDuplicate(loadedDayData, eachEditedDayData))) {
                            if (isLimited) {
                                if (this.section.equalsIgnoreCase(eachEditedDayData.getSection()) && this.term == eachEditedDayData.getTerm() && this.level == eachEditedDayData.getLevel()) {
                                    loadedDayData.add(eachEditedDayData);
                                }
                            } else {
                                loadedDayData.add(eachEditedDayData);
                            }
                        }
                    }
                }
            }
            if (saveDayData != null) {
                for (DayData eachEditedDayData : saveDayData) {
                    if (eachEditedDayData != null) {
                        if (!(prefManager.isDuplicate(loadedDayData, eachEditedDayData))) {
                            if (isLimited) {
                                if (this.section.equalsIgnoreCase(eachEditedDayData.getSection()) && this.term == eachEditedDayData.getTerm() && this.level == eachEditedDayData.getLevel()) {
                                    loadedDayData.add(eachEditedDayData);
                                }
                            } else {
                                loadedDayData.add(eachEditedDayData);
                            }
                        }
                    }
                }
            }

            //Checking for deleted classes
            ArrayList<DayData> deleteDayData = prefManager.getModifiedData(PrefManager.DELETE_DATA_TAG);
            if (deleteDayData != null) {
                for (DayData eachDeletedDayData : deleteDayData) {
                    for (DayData eachLoadedDayData : loadedDayData) {
                        if (eachDeletedDayData.equals(eachLoadedDayData)) {
                            loadedDayData.remove(eachLoadedDayData);
                            break;
                        }
                    }
                }
            }
        }
        return loadedDayData;
    }

    public boolean verifyUpdatedDb(int dbVersion) {
        UpdatedDatabaseHelper databaseHelper = UpdatedDatabaseHelper.getInstance(context, dbVersion);
        ArrayList<String> courseCodes = courseCodeGenerator(getSemester());
        ArrayList<DayData> vanillaRoutine = databaseHelper.getDayData(courseCodes, section, level, term, dept, campus, program);
        if (vanillaRoutine == null) {
            return false;
        }
        return vanillaRoutine.size() > 0;
    }
}
