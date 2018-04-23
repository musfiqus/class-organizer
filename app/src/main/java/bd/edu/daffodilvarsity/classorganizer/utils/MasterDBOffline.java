package bd.edu.daffodilvarsity.classorganizer.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import bd.edu.daffodilvarsity.classorganizer.data.DayData;

/**
 * Created by Mushfiqus Salehin on 10/8/2017.
 * musfiqus@gmail.com
 */

public class MasterDBOffline extends SQLiteAssetHelper {
    private static final String TAG = "MasterDBOffline";
    public static final int OFFLINE_DATABASE_VERSION = 24;

    //Increment the version to erase previous db
    private static final String COLUMN_COURSE_CODE = "course_code";
    public static final String COLUMN_TEACHERS_INITIAL = "teachers_initial";
    private static final String COLUMN_WEEK_DAYS = "week_days";
    private static final String COLUMN_ROOM_NO = "room_no";
    private static final String COLUMN_TIME = "time_data";
    public static final String COLUMN_SCHEDULES_CLASS_START = "class_start";
    public static final String COLUMN_SCHEDULES_CLASS_END = "class_end";
    public static final String COLUMN_SCHEDULES_MID_START = "mid_start";
    public static final String COLUMN_SCHEDULES_MID_END = "mid_end";
    public static final String COLUMN_SCHEDULES_VACATION_ONE_START = "vacation_one_start";
    public static final String COLUMN_SCHEDULES_VACATION_ONE_END = "vacation_one_end";
    public static final String COLUMN_SCHEDULES_VACATION_TWO_START = "vacation_two_start";
    public static final String COLUMN_SCHEDULES_VACATION_TWO_END = "vacation_two_end";
    private ArrayList<DayData> finalDayData = new ArrayList<>();
    private Context mContext;
    private static MasterDBOffline mInstance = null;

    private MasterDBOffline(Context context) {
        super(context, new PrefManager(context).getOfflineDbName(), null, new PrefManager(context).getDatabaseVersion());
        setForcedUpgrade();
        mContext = context.getApplicationContext();
    }

    public MasterDBOffline(Context context, String databaseName, int databaseVersion) {
        super(context, databaseName, null, databaseVersion);
        setForcedUpgrade();
        mContext = context.getApplicationContext();
    }

    public static MasterDBOffline getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (mInstance != null) {
            mInstance = null;
        }
        mInstance = new MasterDBOffline(context.getApplicationContext());
        return mInstance;
    }

    public boolean isDatabaseWritable() {
        SQLiteDatabase db;
        boolean result = true;
        try {
            db = getWritableDatabase();
        } catch (Exception e) {
            result = false;
            if (e instanceof NullPointerException) {
                errorLog(e);
            } else if (e instanceof SQLiteAssetException) {
                errorLog(e);
            } else {
                errorLog(new Exception("Unknown error getting writable db"));
                errorLog(e);
            }
        }
        return result;
    }

    ArrayList<DayData> getDayData(ArrayList<String> courseCodes, String section, int level, int term, String dept, String campus, String program) {
        SQLiteDatabase db;
        try {
            db = getWritableDatabase();
        } catch (NullPointerException e) {
            errorLog(e);
            return null;
        } catch (SQLiteAssetException e) {
            errorLog(e);
            return null;
        }
        final String currentTable = "routine_"+campus.toLowerCase() + "_" + dept.toLowerCase() + "_" + program.toLowerCase();
        if (finalDayData != null) {
            finalDayData.clear();
        }
        CourseUtils courseUtils = CourseUtils.getInstance(mContext);
        if (courseCodes != null) {
            for (String eachCourse : courseCodes) {
                //Checking if any course is null
                if (eachCourse != null && section != null) {
                    String id = removeSpaces(eachCourse).toUpperCase() + strippedStringMinimal(section).toUpperCase();
                    Cursor cursor = db.query(currentTable, new String[]{COLUMN_COURSE_CODE,
                                    COLUMN_TEACHERS_INITIAL, COLUMN_WEEK_DAYS, COLUMN_ROOM_NO, COLUMN_TIME}, COLUMN_COURSE_CODE + "=?",
                            new String[]{id}, null, null, null, null);
                    if (cursor.moveToFirst()) {
                        do {
                            DayData newDayData = new DayData(getCourseCode(eachCourse), trimInitial(cursor.getString(1)), section, level, term, cursor.getString(3), courseUtils.getTime(cursor.getString(4)), cursor.getString(2), getTimeWeight(cursor.getString(4)), courseUtils.getCourseTitle(eachCourse, campus, dept, program));
                            finalDayData.add(newDayData);
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                }
            }
        }
        return finalDayData;
    }

    ArrayList<DayData> getDayDataByQuery(String campus, String dept, String program, String query, String columnName) {
        ArrayList<DayData> list = new ArrayList<>();
        if (query != null) {

            SQLiteDatabase db;
            try {
                db = getWritableDatabase();
            } catch (NullPointerException e) {
                errorLog(e);
                return null;
            } catch (SQLiteAssetException e) {
                errorLog(e);
                return null;
            }
            final String TABLE_NAME = "routine_"+campus.toLowerCase() + "_" + dept.toLowerCase() + "_" + program.toLowerCase();
            if (doesTableExist(TABLE_NAME)) {
                CourseUtils courseUtils = CourseUtils.getInstance(mContext);
                String[] columnNames = getColumnNames(TABLE_NAME);
                Cursor cursor = db.query(TABLE_NAME, columnNames, columnName + "=?",
                        new String[]{query}, null, null, null, null);

                if (cursor.moveToFirst()) {
                    do {
                        if (cursor.getString(0) != null) {
                            String[] dynamicCode = dynamicCourseCode(cursor.getString(0));
                            if (dynamicCode != null) {
                                String courseCode = dynamicCode[0];
                                String section = dynamicCode[1];
                                int semester = getColumnNumberByQuery("course_codes_"+campus+"_"+dept+"_"+program, courseCode);
                                int[] levelTerm = RoutineLoader.getLevelTerm(semester+1);
                                DayData newDayData = new DayData(getCourseCode(courseCode), trimInitial(cursor.getString(1)), section, levelTerm[0], levelTerm[1], cursor.getString(3), courseUtils.getTime(cursor.getString(4)), cursor.getString(2), getTimeWeight(cursor.getString(4)), courseUtils.getCourseTitle(courseCode, campus, dept, program));
                                list.add(newDayData);
                            }

                        }
                    } while (cursor.moveToNext());
                }
                cursor.close();

            }
            return list;
            } else {
                return null;
            }

    }

    ArrayList<DayData> getFreeRoomsByTime(String campus, String dept, String program, String day, String timeWeight) {
        SQLiteDatabase db;
        try {
            db = getWritableDatabase();
        } catch (NullPointerException e) {
            errorLog(e);
            return null;
        } catch (SQLiteAssetException e) {
            errorLog(e);
            return null;
        }
        final String currentTable = "routine_"+campus.toLowerCase() + "_" + dept.toLowerCase() + "_" + program.toLowerCase();
        String[] columnNames = getColumnNames(currentTable);
        final String SELECTION = columnNames[0] + "=?" + " AND " + columnNames[1] + "=?" + " AND " +columnNames[2] + " LIKE ?" + " AND " +columnNames[4] + " LIKE ?";
        ArrayList<DayData> list = new ArrayList<>();
        final String[] SELECTION_ARGS = { "N/A", "N/A", day, timeWeight };
        Cursor cursor = db.query(currentTable, columnNames, SELECTION, SELECTION_ARGS, null, null, null, null);
        CourseUtils courseUtils = CourseUtils.getInstance(mContext);
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(0) != null) {
                    String courseCode = cursor.getString(0);
                    DayData newDayData = new DayData(getCourseCode(courseCode), trimInitial(cursor.getString(1)), "B", 0, 0, cursor.getString(3), courseUtils.getTime(cursor.getString(4)), cursor.getString(2), getTimeWeight(cursor.getString(4)), courseUtils.getCourseTitle(courseCode, campus, dept, program));
                    list.add(newDayData);

                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    ArrayList<DayData> getFreeRoomsByRoom(String campus, String dept, String program, String room) {
        SQLiteDatabase db;
        try {
            db = getWritableDatabase();
        } catch (NullPointerException e) {
            errorLog(e);
            return null;
        } catch (SQLiteAssetException e) {
            errorLog(e);
            return null;
        }
        final String currentTable = "routine_"+campus.toLowerCase() + "_" + dept.toLowerCase() + "_" + program.toLowerCase();
        String[] columnNames = getColumnNames(currentTable);
        final String SELECTION = columnNames[0] + "=?" + " AND " + columnNames[1] + "=?" + " AND " +columnNames[3] + " LIKE ?";
        ArrayList<DayData> list = new ArrayList<>();
        final String[] SELECTION_ARGS = { "N/A", "N/A", room };
        Cursor cursor = db.query(currentTable, columnNames, SELECTION, SELECTION_ARGS, null, null, null, null);
        CourseUtils courseUtils = CourseUtils.getInstance(mContext);
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(0) != null) {
                    String courseCode = cursor.getString(0);
                    DayData newDayData = new DayData(getCourseCode(courseCode), trimInitial(cursor.getString(1)), "B", 0, 0, cursor.getString(3), courseUtils.getTime(cursor.getString(4)), cursor.getString(2), getTimeWeight(cursor.getString(4)), courseUtils.getCourseTitle(courseCode, campus, dept, program));
                    list.add(newDayData);

                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    private int getColumnNumberByQuery(final String TABLE_NAME, String query) {
        String[] columnNames = getColumnNames(TABLE_NAME);
        for (int i = 0; i < columnNames.length; i++) {
            ArrayList<String> data = getRowsByColumn(i, TABLE_NAME, columnNames);
            if (data.contains(query)) {
                return i;
            }
        }
        return 0;
    }

    public String getCourseTitle(String courseCode, String campus, String dept, String program) {
        SQLiteDatabase db;
        try {
            db = getWritableDatabase();
        } catch (NullPointerException e) {
            errorLog(e);
            return null;
        } catch (SQLiteAssetException e) {
            errorLog(e);
            return null;
        }
        final String COLUMN_COURSE_CODE = "course_code";
        final String COLUMN_COURSE_TITLE = "course_title";
        final String TABLE_NAME = "course_title_"+campus+"_"+dept+"_"+program;
        String courseTitle = null;
        if (courseCode != null) {
            String id = removeSpaces(courseCode);
            Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_COURSE_CODE,
                    COLUMN_COURSE_TITLE}, COLUMN_COURSE_CODE + "=?", new String[]{String.valueOf(id)}, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    courseTitle = cursor.getString(1);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        if (courseTitle == null) {
            return "N/A";
        } else {
            return courseTitle;
        }
    }

    public String getTime(String timeData) {
        SQLiteDatabase db;
        try {
            db = getWritableDatabase();
        } catch (NullPointerException e) {
            errorLog(e);
            return null;
        } catch (SQLiteAssetException e) {
            errorLog(e);
            return null;
        }
        final String COLUMN_TIME_DATA = "time_data";
        final String COLUMN_TIME = "time";
        final String TABLE_NAME = "time_table";

        String time = null;
        if (timeData != null) {
            String time_temp = removeSpaces(timeData);
            Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_TIME_DATA,
                    COLUMN_TIME}, COLUMN_TIME_DATA + "=?", new String[]{String.valueOf(time_temp)}, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    time = cursor.getString(1);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return time;
    }

    public ArrayList<String> getCourseCodes(int semester, String campus, String department, String program) {
        final String TABLE_NAME = "course_codes_"+campus+"_"+department+"_"+program;
        boolean isTableExisting = doesTableExist(TABLE_NAME);
        if (isTableExisting) {
            String[] columnNames = getColumnNames(TABLE_NAME);
            if(columnNames.length >= semester) {
                ArrayList<String> courseCodes = getRowsByColumn(semester-1, TABLE_NAME, columnNames);
                //Checking for blank column
                if (courseCodes.size() > 0) {
                    return courseCodes;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public ArrayList<String> getSections(String campus, String department, String program) {
        final String TABLE_NAME = "sections";
        String[] columnNames = getColumnNames(TABLE_NAME);
        String columnName = (campus+"_"+department+"_"+program).toLowerCase();
        int column = getColumnNumber(columnNames, columnName);
        if (column >= 0) {
            return getRowsByColumn(column, TABLE_NAME, columnNames);
        }
        return null;
    }

    //Method to retrieve spinner data
    public ArrayList<String> getSpinnerList(int code) {
        final String TABLE_NAME = "spinners";
        String[] columnNames = getColumnNames(TABLE_NAME);
        return getRowsByColumn(code, TABLE_NAME, columnNames);
    }


    //Gets the total number of semester for the course. EG: for cse day it's 12. If it doesn't exist the value is 0
    public int getTotalSemester(String campus, String department, String program) {
        SQLiteDatabase db;
        try {
            db = getWritableDatabase();
        } catch (NullPointerException e) {
            errorLog(e);
            return 0;
        } catch (SQLiteAssetException e) {
            errorLog(e);
            return 0;
        }
        final String TABLE_NAME = "departments_"+campus;
        final String COLUMN_DEPARTMENTS = "departments";
        String[] columnNames = getColumnNames(TABLE_NAME);
        int count = 0;
        Cursor cursor = db.query(TABLE_NAME, columnNames, COLUMN_DEPARTMENTS + "=?", new String[]{department}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getInt(getColumnNumber(columnNames, program)) != 0) {
                    count = cursor.getInt(getColumnNumber(columnNames, program));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return count;
    }

    public ArrayList<String> getTeachersInitials(String campus, String department, String program) {
        final String TABLE_NAME = "routine_"+campus.toLowerCase() + "_" + department.toLowerCase() + "_" + program.toLowerCase();
        boolean isTableExisting = doesTableExist(TABLE_NAME);
        if (isTableExisting) {
            String[] columnNames = getColumnNames(TABLE_NAME);
            int column = getColumnNumber(columnNames, "teachers_initial");
            ArrayList<String> initials = new ArrayList<>();
            SQLiteDatabase db;
            try {
                db = getWritableDatabase();
            } catch (NullPointerException e) {
                errorLog(e);
                return null;
            }
            Cursor cursor = db.query(TABLE_NAME, columnNames, null, null, null, null, null);
            Set<String> set = new HashSet<>();
            if (cursor.moveToFirst()) {
                do {
                    if (cursor.getString(column) != null) {
                        String newInitial = cursor.getString(column);
                        if (!newInitial.equalsIgnoreCase("N/A")) {
                            set.add(newInitial);
                        }
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
            initials.addAll(set);
            Collections.sort(initials, new Comparator<String>() {
                @Override
                public int compare(String s1, String s2) {
                    return s1.compareToIgnoreCase(s2);
                }
            });
            return initials;
        }
        return null;
    }

    public ArrayList<String> getRoomNo(String campus, String department, String program) {
        final String TABLE_NAME = "routine_"+campus.toLowerCase() + "_" + department.toLowerCase() + "_" + program.toLowerCase();
        boolean isTableExisting = doesTableExist(TABLE_NAME);
        if (isTableExisting) {
            String[] columnNames = getColumnNames(TABLE_NAME);
            int column = getColumnNumber(columnNames, "room_no");
            ArrayList<String> rooms = new ArrayList<>();
            SQLiteDatabase db;
            try {
                db = getWritableDatabase();
            } catch (NullPointerException e) {
                errorLog(e);
                return null;
            }
            Cursor cursor = db.query(TABLE_NAME, columnNames, null, null, null, null, null);
            Set<String> set = new HashSet<>();
            if (cursor.moveToFirst()) {
                do {
                    if (cursor.getString(column) != null) {
                        String newRoom = cursor.getString(column);
                        if (!newRoom.equalsIgnoreCase("N/A")) {
                            set.add(newRoom);
                        }
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
            rooms.addAll(set);
            Collections.sort(rooms, new Comparator<String>() {
                @Override
                public int compare(String s1, String s2) {
                    return s1.compareToIgnoreCase(s2);
                }
            });
            return rooms;
        }
        return null;
    }


    //Gets the name of the current semester
    public String getCurrentSemester(String campus, String department, String program) {
        final String TABLE_NAME = "current_semester";
        String[] columnNames = getColumnNames(TABLE_NAME);
        SQLiteDatabase db;
        try {
            db = getWritableDatabase();
        } catch (NullPointerException e) {
            errorLog(e);
            return null;
        }
        Cursor cursor = db.query(TABLE_NAME, columnNames, null, null, null, null, null);
        String semester = null;
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(0) != null && cursor.getString(1) != null && cursor.getString(2) != null && cursor.getString(3) != null) {
                    if (cursor.getString(0).equalsIgnoreCase(campus) && cursor.getString(1).equalsIgnoreCase(department) && cursor.getString(2).equalsIgnoreCase(program)) {
                        semester = cursor.getString(3);
                    }
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return semester;
    }

    //Gets the integer value of current semester of database
    //This value determines whether to update semester or not
    public int getSemesterCount(String campus, String department, String program) {
        final String TABLE_NAME = "current_semester";
        String[] columnNames = getColumnNames(TABLE_NAME);
        SQLiteDatabase db;
        try {
            db = getWritableDatabase();
        } catch (NullPointerException e) {
            errorLog(e);
            return 0;
        }
        Cursor cursor = db.query(TABLE_NAME, columnNames, null, null, null, null, null);
        int semester = 1;
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(0) != null && cursor.getString(1) != null && cursor.getString(2) != null && cursor.getString(3) != null) {
                    if (cursor.getString(0).equalsIgnoreCase(campus) && cursor.getString(1).equalsIgnoreCase(department) && cursor.getString(2).equalsIgnoreCase(program)) {
                        semester = cursor.getInt(4);
                    }
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return semester;
    }

    //Get time weight from start time
    public double getTimeWeightFromStart(String startTime) {
        ArrayList<String> startTimes = getSpinnerList(CourseUtils.GET_START_TIME);
        ArrayList<String> weights = getSpinnerList(3);
        String strippedTime = removeSpaces(startTime);
        String weight = null;
        for (int i = 0; i < startTimes.size(); i++) {
            if (removeSpaces(startTimes.get(i)).equalsIgnoreCase(strippedTime)) {
                weight = weights.get(i);
                break;
            }
        }
        double timeWeight = 0;
        if (weight != null) {
            try {
                timeWeight = Double.parseDouble(weight);
            } catch (Exception e) {
                errorLog(e);
            }
        }
        return timeWeight;
    }

    //Checks if table exists in the db using table name
    public boolean doesTableExist(final String TABLE_NAME) {
        SQLiteDatabase db;
        try {
            db = getWritableDatabase();
        } catch (NullPointerException e) {
            errorLog(e);
            return false;
        }
        boolean isTableExisting;
        try {
            Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
            isTableExisting = true;
            cursor.close();
        } catch (Exception e) {
            isTableExisting = false;
        }
        return isTableExisting;
    }

    //Finds the list of column names of a table
    private String[] getColumnNames(final String TABLE_NAME) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        String[] columnNames = cursor.getColumnNames();
        cursor.close();
        return columnNames;
    }

    //Get column number using column name from an  array of column names
    private int getColumnNumber(String[] columnNames, String columnName) {
        for (int i = 0; i < columnNames.length; i++) {
            if (columnName.equalsIgnoreCase(columnNames[i])) {
                return i;
            }
        }
        return -1;
    }

    //Get all the values of a particular column
    private ArrayList<String> getRowsByColumn(int column, final String TABLE_NAME, String[] columnNames) {
        ArrayList<String> rows = new ArrayList<>();
        SQLiteDatabase db;
        try {
            db = getWritableDatabase();
        } catch (NullPointerException e) {
            errorLog(e);
            return null;
        } catch (SQLiteAssetException e) {
            errorLog(e);
            return null;
        }
        Cursor cursor = null;
        cursor = db.query(TABLE_NAME, columnNames, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(column) != null) {
                    rows.add(cursor.getString(column));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return rows;
    }

    private double getTimeWeight(String weight) {
        weight = weight.replace("\\s+", "");
        double timeWeight = 0;
        try {
            timeWeight = Double.parseDouble(weight);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return timeWeight;
    }

    private String getCourseCode(String rawCode) {
        int i = 0;
        int beginIndex = 0;
        boolean brake = false;

        while(!brake) {
            try {
                Integer.parseInt(rawCode.substring(i, i+1));
                brake = true;
            } catch (NumberFormatException e) {
                i++;
                brake = false;
            }
            if (rawCode.length() == i+1) {
                return null;
            }
        }
        String course = rawCode.substring(beginIndex, i);
        Log.e(TAG, "getCourseCode: Course"+ course+" i: "+i);
        brake = false;
        beginIndex = i;
        Log.e(TAG, rawCode);
        while (!brake) {
            if (rawCode.length() == i) {
                break;
            }
            if (rawCode.length() != i+1 && rawCode.substring(i, i+1).equalsIgnoreCase("L")) {
                i++;
                Log.e(TAG, "getCourseCode: i 2nd: "+i );
                continue;
            }
            try {
                Integer.parseInt(rawCode.substring(i, i+1));
                brake = false;
                i++;
            } catch (NumberFormatException e) {
                brake = true;
            }
        }
        String code = rawCode.substring(beginIndex, i);
        return course+" "+code;
    }

    private String strippedStringMinimal(String string) {
        if (string != null) {
            string = string.replaceAll("\\s+", "");
            string = string.replaceAll("\\p{P}", "");
        }
        return string;
    }

    private String[] dynamicCourseCode(String rawCode) {
        int i = 0;
        int beginIndex = 0;
        boolean brake = false;

        while(!brake) {
            try {
                Integer.parseInt(rawCode.substring(i, i+1));
                brake = true;
            } catch (NumberFormatException e) {
                i++;
                brake = false;
            }
            if (rawCode.length() == i+1) {
                return null;
            }
        }
        String course = rawCode.substring(beginIndex, i);
        brake = false;
        beginIndex = i;
        Log.e(TAG, rawCode);
        while (!brake) {
            if (rawCode.substring(i, i+1).equalsIgnoreCase("L") && rawCode.length() != i+1) {
                i++;
                continue;
            }
            try {
                Integer.parseInt(rawCode.substring(i, i+1));
                brake = false;
                i++;
            } catch (NumberFormatException e) {
                brake = true;
            }
        }
        String code = rawCode.substring(beginIndex, i);
        String section = rawCode.substring(i, rawCode.length());
        section = section.substring(0,1);
        return new String[]{course+" "+code, section};
    }

    private String trimInitial(String initial) {
        return initial.replaceAll("\\s+", "");
    }

    private String removeSpaces(String strings) {
        return strings.replaceAll("\\s+", "");
    }


    //All methods below this are for DataChecker
    public boolean checkDepartment(String campus, String department) {
        SQLiteDatabase db;
        try {
            db = getWritableDatabase();
        } catch (NullPointerException e) {
            errorLog(e);
            return false;
        } catch (SQLiteAssetException e) {
            errorLog(e);
            return false;
        }
        final String TABLE_NAME = "departments_" + campus;
        final String COLUMN_DEPARTMENTS = "departments";
        String[] columnNames = getColumnNames(TABLE_NAME);
        String found = null;
        Cursor cursor = db.query(TABLE_NAME, columnNames, COLUMN_DEPARTMENTS + "=?", new String[]{department}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(0) != null) {
                    found = cursor.getString(0);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return found != null && found.equalsIgnoreCase(department);
    }

    public Date getDateFromSchedule(final String COLUMN_NAME, String currentSemester, String campus, String department, String program) {
        SQLiteDatabase db;
        try {
            db = getWritableDatabase();
        } catch (NullPointerException e) {
            errorLog(e);
            return null;
        } catch (SQLiteAssetException e) {
            errorLog(e);
            return null;
        }
        final String TABLE_NAME = "semester_schedules";
        final String COLUMN_SEMESTER = "semester";
        final String COLUMN_CAMPUS = "campus";
        final String COLUMN_DEPARTMENT = "department";
        final String COLUMN_PROGRAM = "program";
        String[] columnNames = getColumnNames(TABLE_NAME);
        Log.e(TAG, "getDateFromSchedule: campus dept prog"+campus+department+program+currentSemester );
        final String SELECTION = COLUMN_SEMESTER + " =? AND "+COLUMN_CAMPUS + " =? AND " + COLUMN_DEPARTMENT + " =? AND "+COLUMN_PROGRAM+" =?";
        Cursor cursor = db.query(TABLE_NAME, columnNames, SELECTION,
                new String[]{ currentSemester.toLowerCase(), campus.toLowerCase(), department.toLowerCase(), program.toLowerCase()}, null, null, null, null);
        int column = cursor.getColumnIndex(COLUMN_NAME);
        String dateString = null;
        Log.e(TAG, "getDateFromSchedule: cursor "+cursor.getCount() );
        if (cursor.moveToFirst()) {
            do {
                dateString = cursor.getString(column);
            } while (cursor.moveToNext());
        }
        cursor.close();
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        Date date = null;
        if (dateString != null) {
            try {
                date = format.parse(dateString);
            } catch (ParseException e) {
                FileUtils.logAnError(mContext, TAG, "getDateFromSchedule: Invalid date. Error: "+e.toString());
            }
        }
        Log.e(TAG, "getDateFromSchedule: Date :"+date);

        return date;
    }

    protected void errorLog(Exception e) {
        Log.e(TAG, e.toString());
        Crashlytics.logException(e);
    }
}
