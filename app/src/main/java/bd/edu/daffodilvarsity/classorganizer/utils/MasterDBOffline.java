package bd.edu.daffodilvarsity.classorganizer.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;

import bd.edu.daffodilvarsity.classorganizer.data.DayData;

/**
 * Created by Mushfiqus Salehin on 10/8/2017.
 * musfiqus@gmail.com
 */

public class MasterDBOffline extends SQLiteAssetHelper {
    public static final int OFFLINE_DATABASE_VERSION = 16;

    //Increment the version to erase previous db
    private static final String COLUMN_COURSE_CODE = "course_code";
    private static final String COLUMN_TEACHERS_INITIAL = "teachers_initial";
    private static final String COLUMN_WEEK_DAYS = "week_days";
    private static final String COLUMN_ROOM_NO = "room_no";
    private static final String COLUMN_TIME = "time_data";
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

    ArrayList<DayData> getDayData(ArrayList<String> courseCodes, String section, int level, int term, String dept, String campus, String program) {
        SQLiteDatabase db = getWritableDatabase();
        final String currentTable = "routine_"+campus.toLowerCase() + "_" + dept.toLowerCase() + "_" + program.toLowerCase();
        if (finalDayData != null) {
            finalDayData.clear();
        }
        CourseUtils courseUtils = CourseUtils.getInstance(mContext);
        if (courseCodes != null) {
            for (String eachCourse : courseCodes) {
                //Checking if any course is null
                if (eachCourse != null) {
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

    public String getCourseTitle(String courseCode, String campus, String dept, String program) {
        SQLiteDatabase db = getWritableDatabase();
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
        SQLiteDatabase db = getWritableDatabase();
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
        SQLiteDatabase db = getWritableDatabase();
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

    public String getUpdateURL(int code) {
        final String TABLE_NAME = "update_urls";
        String[] columnNames = getColumnNames(TABLE_NAME);
        return getRowsByColumn(code, TABLE_NAME, columnNames).get(0);
    }


    //Gets the name of the current semester
    public String getCurrentSemester(String campus, String department, String program) {
        final String TABLE_NAME = "current_semester";
        String[] columnNames = getColumnNames(TABLE_NAME);
        SQLiteDatabase db = getWritableDatabase();
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
        SQLiteDatabase db = getWritableDatabase();
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
                e.printStackTrace();
            }
        }
        return timeWeight;
    }

    //Checks if table exists in the db using table name
    public boolean doesTableExist(final String TABLE_NAME) {
        SQLiteDatabase db = getWritableDatabase();
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
        SQLiteDatabase db = getWritableDatabase();
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

    private String getCourseCode(String courseCode) {
        String[] split = {courseCode.substring(0, 3), courseCode.substring(3)};
        courseCode = split[0] + " " + split[1];
        return courseCode;
    }

    private String strippedStringMinimal(String string) {
        if (string != null) {
            string = string.replaceAll("\\s+", "");
            string = string.replaceAll("\\p{P}", "");
        }
        return string;
    }

    private String trimInitial(String initial) {
        return initial.replaceAll("\\s+", "");
    }

    private String removeSpaces(String strings) {
        return strings.replaceAll("\\s+", "");
    }


    //All methods below this are for DataChecker
    public boolean checkDepartment(String campus, String department) {
        SQLiteDatabase db = getWritableDatabase();
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
}
