package bd.edu.daffodilvarsity.classorganizer.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
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

        CourseUtils courseUtils = CourseUtils.getInstance(context);
        vanillaRoutine = courseUtils.getDayData(courseCodes, section, level, term, dept, campus, program);
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

    public boolean verifyUpdatedDb() {
        CourseUtils dbChecker = new CourseUtils(context, true);
        //First we'll check if a new semester is available
        if (!dbChecker.getCurrentSemester(prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram()).equalsIgnoreCase(prefManager.getSemester())) {
            //If available we won't do any further checks
            return true;
        }
        ArrayList<String> courseCodes = courseCodeGenerator(getSemester());
        if (courseCodes == null || courseCodes.size() == 0) {
            return false;
        }
        ArrayList<String> sections = dbChecker.getSections(campus, dept, program);
        if (sections == null || sections.size() == 0) {
            return false;
        }
        ArrayList<DayData> vanillaRoutine = dbChecker.getDayData(courseCodes, sections.get(0), level, term, dept, campus, program);
        if (vanillaRoutine == null) {
            return false;
        }
        if (vanillaRoutine.size() == 0) {
            return false;
        }
        return true;
    }

    //Checks if a new semester is available in db, if it's available it informs upgrade function
    // to process upgrade
    private boolean isNewSemesterAvailable(boolean isUpdatedOnline) {
        int maxSemester = new CourseUtils(context, isUpdatedOnline).getTotalSemester(prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram());
        int currentSemester = RoutineLoader.getSemester(prefManager.getLevel(), prefManager.getTerm());
        if (prefManager.getSemesterCount() < CourseUtils.getInstance(context).getSemesterCount(prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram()) && currentSemester < maxSemester) {
            return true;
        }
        return false;
    }

    public boolean isNewSemesterAvailable() {
        return isNewSemesterAvailable(prefManager.isUpdatedOnline());
    }

    public void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.exists()) {
            destFile.createNewFile();
        }
        FileChannel source = null;
        FileChannel destination = null;
        FileInputStream is = null;
        FileOutputStream os = null;
        try {
            is = new FileInputStream(sourceFile);
            os = new FileOutputStream(destFile);
            source = is.getChannel();
            destination = os.getChannel();

            long count = 0;
            long size = source.size();
            while ((count += destination.transferFrom(source, count, size - count)) < size)
                ;
        } catch (Exception ex) {
        } finally {
            if (source != null) {
                source.close();
            }
            if (is != null) {
                is.close();
            }
            if (destination != null) {
                destination.close();
            }
            if (os != null) {
                os.close();
            }
        }
    }
}
