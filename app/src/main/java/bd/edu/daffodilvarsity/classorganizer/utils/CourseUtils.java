package bd.edu.daffodilvarsity.classorganizer.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;

import bd.edu.daffodilvarsity.classorganizer.data.DayData;

/**
 * Created by Mushfiqus Salehin on 5/25/2017.
 * musfiqus@gmail.com
 */

public class CourseUtils extends SQLiteAssetHelper {
    public static final int OFFLINE_DATABASE_VERSION = 1;

    //Increment the version to erase previous db
    static final String COLUMN_COURSE_CODE = "course_code";
    static final String COLUMN_TEACHERS_INITIAL = "teachers_initial";
    static final String COLUMN_WEEK_DAYS = "week_days";
    static final String COLUMN_ROOM_NO = "room_no";
    static final String COLUMN_TIME = "time_data";
    private ArrayList<DayData> finalDayData = new ArrayList<>();

    public static final int GET_CAMPUS = 0;
    public static final int GET_DEPARTMENT = 1;
    private static final String DATABASE_NAME = "masterdb.db";
    public static final String UPDATED_DATABASE_NAME = "masterdb_updated.db";
    public static final int ROUTINEDB_URL_CODE = 0;
    public static final int MASTERDB_URL_CODE = 1;

    private Context mContext;


    private static CourseUtils mInstance = null;

    private CourseUtils(Context context) {
        super(context, (new PrefManager(context).isUpdatedOnline()) ? UPDATED_DATABASE_NAME : DATABASE_NAME,
                null, OFFLINE_DATABASE_VERSION);
        setForcedUpgrade();
        mContext = context.getApplicationContext();
    }

    public static CourseUtils getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (mInstance != null) {
            mInstance = null;
        }
        mInstance = new CourseUtils(context.getApplicationContext());
        return mInstance;
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            mContext.deleteDatabase(DATABASE_NAME);
            new CourseUtils(mContext);
        }else
            super.onUpgrade(db, oldVersion, newVersion);
    }

    ArrayList<DayData> getDayData(ArrayList<String> courseCodes, String section, int level, int term, String dept, String campus, String program) {
        SQLiteDatabase db = this.getReadableDatabase();
        final String currentTable = "routine_"+campus.toLowerCase() + "_" + dept.toLowerCase() + "_" + program.toLowerCase();
        if (finalDayData != null) {
            finalDayData.clear();
        }
        CourseUtils courseUtils = CourseUtils.getInstance(mContext);
        if (courseCodes != null) {
            for (String eachCourse : courseCodes) {
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
        return finalDayData;
    }

    public String getCourseTitle(String courseCode, String campus, String dept, String program) {
        SQLiteDatabase db = this.getReadableDatabase();
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
        SQLiteDatabase db = this.getReadableDatabase();
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
        Log.e(getClass().getSimpleName(), columnName);
        int column = getColumnNumber(columnNames, columnName);
        Log.e(getClass().getSimpleName(), "Column: "+column);
        if (column >= 0) {
            return getRowsByColumn(column, TABLE_NAME, columnNames);
        }
        return null;
    }

    //Method to retrieve spinner data
    public ArrayList<String> getSpinnerList(int code) {
        final String TABLE_NAME = "spinners";
        String[] columnNames = getColumnNames(TABLE_NAME);
        switch (code) {
            case GET_CAMPUS: return getRowsByColumn(GET_CAMPUS, TABLE_NAME, columnNames);
            case GET_DEPARTMENT: return getRowsByColumn(GET_DEPARTMENT, TABLE_NAME, columnNames);
            default: return null;
        }
    }

    public int getTotalSemester(String campus, String department, String program) {
        SQLiteDatabase db = this.getReadableDatabase();
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
        Log.e(getClass().getSimpleName(), getRowsByColumn(code, TABLE_NAME, columnNames).get(0));
        return getRowsByColumn(code, TABLE_NAME, columnNames).get(0);
    }



    public String getCurrentSemester(String campus, String department, String program) {
        final String TABLE_NAME = "current_semester";
        String[] columnNames = getColumnNames(TABLE_NAME);
        SQLiteDatabase db = this.getReadableDatabase();
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

    public int getSemesterCount(String campus, String department, String program) {
        final String TABLE_NAME = "current_semester";
        String[] columnNames = getColumnNames(TABLE_NAME);
        SQLiteDatabase db = this.getReadableDatabase();
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

    //Checks if table exists in the db
    private boolean doesTableExist(final String TABLE_NAME) {
        SQLiteDatabase db = this.getReadableDatabase();
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
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        String[] columnNames = cursor.getColumnNames();
        cursor.close();
        return columnNames;
    }

    private int getColumnNumber(String[] columnNames, String columnName) {
        for (int i = 0; i < columnNames.length; i++) {
            if (columnName.equalsIgnoreCase(columnNames[i])) {
                return i;
            }
        }
        return -1;
    }

    private ArrayList<String> getRowsByColumn(int column, final String TABLE_NAME, String[] columnNames) {
        ArrayList<String> rows = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        cursor = db.query(TABLE_NAME, columnNames, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(column) != null) {
                    Log.e(getClass().getSimpleName(), cursor.getString(column));
                    rows.add(cursor.getString(column));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return rows;
    }

    static double getTimeWeight(String weight) {
        weight = weight.replace("\\s+", "");
        double timeWeight = 0;
        try {
            timeWeight = Double.parseDouble(weight);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return timeWeight;
    }

    static String getCourseCode(String courseCode) {
        String[] split = {courseCode.substring(0, 3), courseCode.substring(3)};
        courseCode = split[0] + " " + split[1];
        return courseCode;
    }

    static String strippedStringMinimal(String string) {
        if (string != null) {
            string = string.replaceAll("\\s+", "");
            string = string.replaceAll("\\p{P}", "");
        }
        return string;
    }

    static String trimInitial(String initial) {
        return initial.replaceAll("\\s+", "");
    }

    private String removeSpaces(String strings) {
        return strings.replaceAll("\\s+", "");
    }

}
