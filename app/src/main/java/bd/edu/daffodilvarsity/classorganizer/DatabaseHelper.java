package bd.edu.daffodilvarsity.classorganizer;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;

/**
 * Created by musfiqus on 3/25/2017.
 */

class DatabaseHelper extends SQLiteAssetHelper {
    //Increment the version to erase previous db
    public static final int DATABASE_VERSION = 3;

    private String currentTable;

    private static final String COLUMN_COURSE_CODE = "course_code";
    private static final String COLUMN_TEACHERS_INITIAL = "teachers_initial";
    private static final String COLUMN_WEEK_DAYS = "week_days";
    private static final String COLUMN_ROOM_NO = "room_no";
    private static final String COLUMN_TIME = "time_data";
    private static final String DATABASE_NAME = "routinedb.db";
    private static DatabaseHelper mInstance = null;
    private ArrayList<DayData> finalDayData = new ArrayList<>();

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade();
    }

    //Instantiation method to prevent data leak
    public static DatabaseHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (mInstance == null) {
            mInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return mInstance;
    }

    public ArrayList<DayData> getDayData(ArrayList<String> courseCodes, String section) {
        SQLiteDatabase db = this.getReadableDatabase();
        if (finalDayData != null) {
            finalDayData.clear();
        }
        for (String eachCourse : courseCodes) {
            String id = eachCourse + section;
            Cursor cursor = db.query(currentTable, new String[]{COLUMN_COURSE_CODE,
                            COLUMN_TEACHERS_INITIAL, COLUMN_WEEK_DAYS, COLUMN_ROOM_NO, COLUMN_TIME}, COLUMN_COURSE_CODE + "=?",
                    new String[]{id}, null, null, null, null);

            if (cursor.moveToFirst()) {
                do {
                    DayData newDayData = new DayData(getCourseCode(eachCourse), cursor.getString(1), cursor.getString(3), getTime(cursor.getString(4)), cursor.getString(2), getTimeWeight(cursor.getString(4)));
                    finalDayData.add(newDayData);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return finalDayData;
    }

    public ArrayList<DayData> getDayData(ArrayList<String> courseCodes, String section, String dept, String campus, String program) {
        //We will create table names using this format: TABLE_DEPARTMENT_CAMPUS_PROGRAM
        this.currentTable = dept.toLowerCase() + "_" + campus.toLowerCase() + "_" + program.toLowerCase();
        return getDayData(courseCodes, section);
    }

    private double getTimeWeight(String weight) {
        weight = weight.replace("\\s+","");
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

    private String getTime(String time) {
        switch (time) {
            case "1.0":
                return "08.30-10.00";
            case "2.0":
                return "10.00-11.30";
            case "3.0":
                return "11.30-01.00";
            case "4.0":
                return "01.00-02.30";
            case "5.0":
                return "02.30-04.00";
            case "6.0":
                return "04.00-05.30";
            case "1.5":
                return "09.00-11.00";
            case "2.5":
                return "11.00-01.00";
            case "3.5":
                return "01.00-03.00";
            case "4.5":
                return "03.00-05.00";
            default:
                Log.e("DATABASE ERROR", "INVALID TIME");
                return null;
        }
    }
}
