package bd.edu.daffodilvarsity.classorganizer.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.firebase.crash.FirebaseCrash;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;

import bd.edu.daffodilvarsity.classorganizer.data.DayData;

/**
 * Created by musfiqus on 3/25/2017.
 */

public class DatabaseHelper extends SQLiteAssetHelper {
    //Increment the version to erase previous db
    public static final int DATABASE_VERSION = 43;
    private static final String COLUMN_COURSE_CODE = "course_code";
    private static final String COLUMN_TEACHERS_INITIAL = "teachers_initial";
    private static final String COLUMN_WEEK_DAYS = "week_days";
    private static final String COLUMN_ROOM_NO = "room_no";
    private static final String COLUMN_TIME = "time_data";
    private static final String DATABASE_NAME = "routinedb.db";
    private static DatabaseHelper mInstance = null;
    private ArrayList<DayData> finalDayData = new ArrayList<>();
    private Context mContext;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade();
        mContext = context.getApplicationContext();
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

    public ArrayList<DayData> getDayData(ArrayList<String> courseCodes, String section, int level, int term, String dept, String campus, String program) {
        SQLiteDatabase db = this.getReadableDatabase();
        final String currentTable = dept.toLowerCase() + "_" + campus.toLowerCase() + "_" + program.toLowerCase();
        if (finalDayData != null) {
            finalDayData.clear();
        }
        CourseUtils.CourseTitleGenerator courseTitleGenerator = CourseUtils.CourseTitleGenerator.getInstance(mContext);
        if (courseCodes != null) {
            for (String eachCourse : courseCodes) {
                String id = removeSpaces(eachCourse) + strippedStringMinimal(section);
                Cursor cursor = db.query(currentTable, new String[]{COLUMN_COURSE_CODE,
                                COLUMN_TEACHERS_INITIAL, COLUMN_WEEK_DAYS, COLUMN_ROOM_NO, COLUMN_TIME}, COLUMN_COURSE_CODE + "=?",
                        new String[]{id}, null, null, null, null);
                if (cursor.moveToFirst()) {
                    do {
                        DayData newDayData = new DayData(getCourseCode(eachCourse), trimInitial(cursor.getString(1)), section, level, term, cursor.getString(3), getTime(cursor.getString(4)), cursor.getString(2), getTimeWeight(cursor.getString(4)), courseTitleGenerator.getCourseTitle(eachCourse, dept, program));
                        finalDayData.add(newDayData);
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        }
        return finalDayData;
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
                return "08.30 AM - 10.00 AM";
            case "2.0":
                return "10.00 AM - 11.30 AM";
            case "3.0":
                return "11.30 AM - 01.00 PM";
            case "4.0":
                return "01.00 PM - 02.30 PM";
            case "5.0":
                return "02.30 PM - 04.00 PM";
            case "6.0":
                return "04.00 PM - 05.30 PM";
            case "7.0":
                return "06.00 PM - 07.30 PM";
            case "8.0":
                return "07.30 PM - 09.00 PM";
            case "9.0":
                return "09.00 PM - 12.00 AM";
            case "1.5":
                return "09.00 AM - 11.00 AM";
            case "2.5":
                return "11.00 AM - 01.00 PM";
            case "3.5":
                return "01.00 PM - 03.00 PM";
            case "4.5":
                return "03.00 PM - 05.00 PM";
            case "4.6":
                return "03.00 PM - 06.00 PM";
            case "7.5":
                return "06.00 PM - 09.00 PM";
            default:
                FirebaseCrash.report(new Exception("DATABASE ERROR INVALID TIME"));
                return null;
        }
    }

    private String strippedStringMinimal(String string) {
        if (string != null) {
            string = string.replaceAll("\\s+", "");
            string = string.replaceAll("\\p{P}", "");
        }
        return string;
    }

    private String removeSpaces(String strings) {
        return strings.replaceAll("\\s+", "");
    }

    private String trimInitial(String initial) {
        return initial.replaceAll("\\s+", "");
    }
}
