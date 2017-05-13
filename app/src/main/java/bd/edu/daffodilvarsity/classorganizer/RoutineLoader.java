package bd.edu.daffodilvarsity.classorganizer;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by musfiqus on 4/1/2017.
 */

public class RoutineLoader {

    private int level;
    private int term;
    private String section;
    private Context context;
    private String dept;
    private String campus;
    private String program;
    PrefManager prefManager;

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
            Log.e("RoutineLoader", "Error in courseCodeGenerator");
            return null;
        }
    }

    public ArrayList<DayData> loadRoutine() {
        //Initializing DB Helper
        DatabaseHelper db = DatabaseHelper.getInstance(context);
        //Generating course codes from generated semester
        ArrayList<String> courseCodes = courseCodeGenerator(setSemester());
        return db.getDayData(courseCodes, section, dept, campus, program);
    }

    public void saveSnapShot() {
        prefManager.saveSnapshotDayData(loadRoutine());
    }

    public void loadPersonalDayData() {
        ArrayList<DayData> loadedDayData = prefManager.getSavedDayData();
        if (loadedDayData.size() > 0) {
            //Checking for modified daydata
            if (prefManager.getEditedDayData() != null) {
                ArrayList<DayData> editedDayData = prefManager.getEditedDayData();
                for (DayData eachEditedDayData : editedDayData) {
                    loadedDayData.add(eachEditedDayData);
                }
            }
            //Checking for deleted classes
            if (prefManager.getDeletedDayData() != null) {
                ArrayList<DayData> deletedDayData = prefManager.getDeletedDayData();
                for (DayData eachDeletedDayData : deletedDayData) {
                    for (DayData eachLoadedDayData : loadedDayData) {
                        int dataMatchCount = 0;
                        if (eachDeletedDayData.getCourseCode().equalsIgnoreCase(eachLoadedDayData.getCourseCode())) {
                            dataMatchCount++;
                        }
                        if (eachDeletedDayData.getTimeWeight() == eachLoadedDayData.getTimeWeight()) {
                            dataMatchCount++;
                        }
                        if (eachDeletedDayData.getRoomNo().equalsIgnoreCase(eachLoadedDayData.getRoomNo())) {
                            dataMatchCount++;
                        }
                        if (eachDeletedDayData.getDay().equalsIgnoreCase(eachLoadedDayData.getDay())) {
                            dataMatchCount++;
                        }
                        if (eachDeletedDayData.getTeachersInitial().equalsIgnoreCase(eachLoadedDayData.getTeachersInitial())) {
                            dataMatchCount++;
                        }
                        if (dataMatchCount == 5) {
                            loadedDayData.remove(eachLoadedDayData);
                            break;
                        }
                    }
                }
            }
        }
        prefManager.saveDayData(loadedDayData);
    }

    public boolean isUpdated() {
        ArrayList<DayData> snapShot = prefManager.getSnapshotDayData();
        if (snapShot != null) {
            ArrayList<DayData> currentDayData = loadRoutine();
            if (snapShot.size() == currentDayData.size()) {
                int matchedElement = 0;
                for (DayData eachSnapShot : snapShot) {
                    for (DayData eachCurrentData : currentDayData) {
                        int dataMatchCount = 0;
                        if (eachCurrentData.getCourseCode().equalsIgnoreCase(eachSnapShot.getCourseCode())) {
                            dataMatchCount++;
                        }
                        if (eachCurrentData.getTimeWeight() == eachSnapShot.getTimeWeight()) {
                            dataMatchCount++;
                        }
                        if (eachCurrentData.getRoomNo().equalsIgnoreCase(eachSnapShot.getRoomNo())) {
                            dataMatchCount++;
                        }
                        if (eachCurrentData.getDay().equalsIgnoreCase(eachSnapShot.getDay())) {
                            dataMatchCount++;
                        }
                        if (eachCurrentData.getTeachersInitial().equalsIgnoreCase(eachSnapShot.getTeachersInitial())) {
                            dataMatchCount++;
                        }
                        if (dataMatchCount == 5) {
                            matchedElement++;
                            break;
                        }
                    }
                    if (matchedElement == currentDayData.size()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
