package bd.edu.daffodilvarsity.classorganizer.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

import bd.edu.daffodilvarsity.classorganizer.data.DayData;
import bd.edu.daffodilvarsity.classorganizer.R;

/**
 * Created by musfiqus on 4/1/2017.
 */

public class RoutineLoader {

    PrefManager prefManager;
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

    private int setSemester() {
        if (this.level == 0) {
            return 1 + this.term;
        } else if (this.level == 1) {
            return 4 + this.term;
        } else if (this.level == 2) {
            return 7 + this.term;
        } else {
            return 10 + this.term;
        }
    }

    private ArrayList<String> courseCodeGenerator(int semester) {
        Log.e("Dammit", "fix");
        if (campus.equalsIgnoreCase("main")) {
            if (dept.equalsIgnoreCase("cse")) {
                if (program.equalsIgnoreCase("day")) {
                    if (semester == 1) {
                        String[] semesterData = context.getResources().getStringArray(R.array.CSE_DAY_MAIN_L1T1);
                        return new ArrayList<>(Arrays.asList(semesterData));
                    } else if (semester == 2) {
                        String[] semesterData = context.getResources().getStringArray(R.array.CSE_DAY_MAIN_L1T2);
                        return new ArrayList<>(Arrays.asList(semesterData));
                    } else if (semester == 3) {
                        String[] semesterData = context.getResources().getStringArray(R.array.CSE_DAY_MAIN_L1T3);
                        return new ArrayList<>(Arrays.asList(semesterData));
                    } else if (semester == 4) {
                        String[] semesterData = context.getResources().getStringArray(R.array.CSE_DAY_MAIN_L2T1);
                        return new ArrayList<>(Arrays.asList(semesterData));
                    } else if (semester == 5) {
                        String[] semesterData = context.getResources().getStringArray(R.array.CSE_DAY_MAIN_L2T2);
                        return new ArrayList<>(Arrays.asList(semesterData));
                    } else if (semester == 6) {
                        String[] semesterData = context.getResources().getStringArray(R.array.CSE_DAY_MAIN_L2T3);
                        return new ArrayList<>(Arrays.asList(semesterData));
                    } else if (semester == 7) {
                        String[] semesterData = context.getResources().getStringArray(R.array.CSE_DAY_MAIN_L3T1);
                        return new ArrayList<>(Arrays.asList(semesterData));
                    } else if (semester == 8) {
                        String[] semesterData = context.getResources().getStringArray(R.array.CSE_DAY_MAIN_L3T2);
                        return new ArrayList<>(Arrays.asList(semesterData));
                    } else if (semester == 9) {
                        String[] semesterData = context.getResources().getStringArray(R.array.CSE_DAY_MAIN_L3T3);
                        return new ArrayList<>(Arrays.asList(semesterData));
                    } else if (semester == 10) {
                        String[] semesterData = context.getResources().getStringArray(R.array.CSE_DAY_MAIN_L4T1);
                        return new ArrayList<>(Arrays.asList(semesterData));
                    } else if (semester == 11) {
                        String[] semesterData = context.getResources().getStringArray(R.array.CSE_DAY_MAIN_L4T2);
                        return new ArrayList<>(Arrays.asList(semesterData));
                    } else if (semester == 12) {
                        String[] semesterData = context.getResources().getStringArray(R.array.CSE_DAY_MAIN_L4T3);
                        return new ArrayList<>(Arrays.asList(semesterData));
                    } else {
                        return null;
                    }
                } else if (program.equalsIgnoreCase("eve")) {
                    if (semester == 1) {
                        String[] semesterData = context.getResources().getStringArray(R.array.CSE_EVE_MAIN_L1T1);
                        return new ArrayList<>(Arrays.asList(semesterData));
                    } else if (semester == 2) {
                        String[] semesterData = context.getResources().getStringArray(R.array.CSE_EVE_MAIN_L1T2);
                        return new ArrayList<>(Arrays.asList(semesterData));
                    } else if (semester == 3) {
                        String[] semesterData = context.getResources().getStringArray(R.array.CSE_EVE_MAIN_L1T3);
                        return new ArrayList<>(Arrays.asList(semesterData));
                    } else if (semester == 4) {
                        String[] semesterData = context.getResources().getStringArray(R.array.CSE_EVE_MAIN_L2T1);
                        return new ArrayList<>(Arrays.asList(semesterData));
                    } else if (semester == 5) {
                        String[] semesterData = context.getResources().getStringArray(R.array.CSE_EVE_MAIN_L2T2);
                        return new ArrayList<>(Arrays.asList(semesterData));
                    } else if (semester == 6) {
                        String[] semesterData = context.getResources().getStringArray(R.array.CSE_EVE_MAIN_L2T3);
                        return new ArrayList<>(Arrays.asList(semesterData));
                    } else if (semester == 7) {
                        String[] semesterData = context.getResources().getStringArray(R.array.CSE_EVE_MAIN_L3T1);
                        return new ArrayList<>(Arrays.asList(semesterData));
                    } else if (semester == 8) {
                        String[] semesterData = context.getResources().getStringArray(R.array.CSE_EVE_MAIN_L3T2);
                        return new ArrayList<>(Arrays.asList(semesterData));
                    } else if (semester == 9) {
                        String[] semesterData = context.getResources().getStringArray(R.array.CSE_EVE_MAIN_L3T3);
                        return new ArrayList<>(Arrays.asList(semesterData));
                    } else {
                        return null;
                    }
                }
            }
        } else if (campus.equalsIgnoreCase("perm")) {
            Log.e("Dammit", "fix1");
            if (dept.equalsIgnoreCase("cse")) {
                if (program.equalsIgnoreCase("day")) {
                    Log.e("WUT", "WUT PLOX");
                    if (semester == 1) {
                        String[] semesterData = context.getResources().getStringArray(R.array.CSE_DAY_MAIN_L1T1);
                        return new ArrayList<>(Arrays.asList(semesterData));
                    } else if (semester == 2) {
                        String[] semesterData = context.getResources().getStringArray(R.array.CSE_DAY_MAIN_L1T2);
                        return new ArrayList<>(Arrays.asList(semesterData));
                    } else if (semester == 3) {
                        String[] semesterData = context.getResources().getStringArray(R.array.CSE_DAY_MAIN_L1T3);
                        return new ArrayList<>(Arrays.asList(semesterData));
                    } else if (semester == 4) {
                        String[] semesterData = context.getResources().getStringArray(R.array.CSE_DAY_MAIN_L2T1);
                        return new ArrayList<>(Arrays.asList(semesterData));
                    } else if (semester == 5) {
                        String[] semesterData = context.getResources().getStringArray(R.array.CSE_DAY_MAIN_L2T2);
                        return new ArrayList<>(Arrays.asList(semesterData));
                    } else if (semester == 6) {
                        String[] semesterData = context.getResources().getStringArray(R.array.CSE_DAY_MAIN_L2T3);
                        return new ArrayList<>(Arrays.asList(semesterData));
                    } else if (semester == 7) {
                        String[] semesterData = context.getResources().getStringArray(R.array.CSE_DAY_MAIN_L3T1);
                        return new ArrayList<>(Arrays.asList(semesterData));
                    } else if (semester == 8) {
                        String[] semesterData = context.getResources().getStringArray(R.array.CSE_DAY_MAIN_L3T2);
                        return new ArrayList<>(Arrays.asList(semesterData));
                    } else if (semester == 9) {
                        String[] semesterData = context.getResources().getStringArray(R.array.CSE_DAY_MAIN_L3T3);
                        return new ArrayList<>(Arrays.asList(semesterData));
                    } else if (semester == 10) {
                        String[] semesterData = context.getResources().getStringArray(R.array.CSE_DAY_MAIN_L4T1);
                        return new ArrayList<>(Arrays.asList(semesterData));
                    } else if (semester == 11) {
                        String[] semesterData = context.getResources().getStringArray(R.array.CSE_DAY_MAIN_L4T2);
                        return new ArrayList<>(Arrays.asList(semesterData));
                    } else if (semester == 12) {
                        String[] semesterData = context.getResources().getStringArray(R.array.CSE_DAY_MAIN_L4T3);
                        return new ArrayList<>(Arrays.asList(semesterData));
                    } else {
                        return null;
                    }
                }
            }
        }
        return null;
    }

    public ArrayList<DayData> loadRoutine(boolean loadPersonal) {
        //Initializing DB Helper
        DatabaseHelper db = DatabaseHelper.getInstance(context);
        //Generating course codes from generated semester
        ArrayList<String> courseCodes = courseCodeGenerator(setSemester());
        ArrayList<DayData> vanillaRoutine = db.getDayData(courseCodes, section, level, term, dept, campus, program);
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
            if (editDayData != null) {
                for (DayData eachEditedDayData : editDayData) {
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
            if (saveDayData != null) {
                for (DayData eachEditedDayData : saveDayData) {
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
}
