package bd.edu.daffodilvarsity.classorganizer.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;

/**
 * Created by Mushfiqus Salehin on 5/25/2017.
 * musfiqus@gmail.com
 */

public class CourseUtils extends SQLiteAssetHelper{
    private static String DATABASE_NAME = "masterdb.db";

    public static final int DATABASE_VERSION = 3;
    private static CourseUtils mInstance = null;

    private CourseUtils(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade();
    }

    public static CourseUtils getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (mInstance == null) {
            mInstance = new CourseUtils(context.getApplicationContext());
        }
        return mInstance;
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
        SQLiteDatabase db = this.getReadableDatabase();
        final String TABLE_NAME = "course_codes_"+campus+"_"+department+"_"+program;
        boolean isTableExisting = doesTableExist(TABLE_NAME);
        if (isTableExisting) {
            Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
            String[] columnNames = cursor.getColumnNames();
            cursor.close();
            if(columnNames.length >= semester) {
                cursor = db.query(TABLE_NAME, columnNames, null, null, null, null, null);
                ArrayList<String> courseCodes = new ArrayList<>();
                if (cursor.moveToFirst()) {
                    do {
                        if (cursor.getString(semester-1) != null) {
                            courseCodes.add(cursor.getString(semester-1));
                        }
                    } while (cursor.moveToNext());
                }

                cursor.close();
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

    private String removeSpaces(String strings) {
        return strings.replaceAll("\\s+", "");
    }

}
