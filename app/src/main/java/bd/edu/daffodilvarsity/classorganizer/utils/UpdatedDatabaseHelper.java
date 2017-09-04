package bd.edu.daffodilvarsity.classorganizer.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;

import bd.edu.daffodilvarsity.classorganizer.data.DayData;

/**
 * Created by Mushfiqus Salehin on 5/29/2017.
 * musfiqus@gmail.com
 */

public class UpdatedDatabaseHelper extends SQLiteAssetHelper {
    private static UpdatedDatabaseHelper mInstance = null;
    public static final String UPDATED_DATABASE_NAME = "updated_routine.db";
    private ArrayList<DayData> finalDayData = new ArrayList<>();
    private Context mContext;

    private UpdatedDatabaseHelper(Context context, int databaseVersion) {
        super(context, UPDATED_DATABASE_NAME, null, databaseVersion);
        setForcedUpgrade();
        this.mContext = context;
    }

    //Instantiation method to prevent data leak
    public static UpdatedDatabaseHelper getInstance(Context context, int dbVersion) {
        if (mInstance != null) {
            mInstance = null;
        }
        mInstance = new UpdatedDatabaseHelper(context.getApplicationContext(), dbVersion);
        return mInstance;
    }

    public ArrayList<DayData> getDayData(ArrayList<String> courseCodes, String section, int level, int term, String dept, String campus, String program) {
        SQLiteDatabase db = this.getWritableDatabase();
        final String currentTable = dept.toLowerCase() + "_" + campus.toLowerCase() + "_" + program.toLowerCase();
        if (finalDayData != null) {
            finalDayData.clear();
        }
        CourseUtils courseUtils = CourseUtils.getInstance(mContext);
        if (courseCodes != null) {
            for (String eachCourse : courseCodes) {
                String id = DatabaseHelper.removeSpaces(eachCourse) + DatabaseHelper.strippedStringMinimal(section);
                Cursor cursor = db.query(currentTable, new String[]{DatabaseHelper.COLUMN_COURSE_CODE,
                                DatabaseHelper.COLUMN_TEACHERS_INITIAL, DatabaseHelper.COLUMN_WEEK_DAYS, DatabaseHelper.COLUMN_ROOM_NO, DatabaseHelper.COLUMN_TIME}, DatabaseHelper.COLUMN_COURSE_CODE + "=?",
                        new String[]{id}, null, null, null, null);
                if (cursor.moveToFirst()) {
                    do {
                        DayData newDayData = new DayData(DatabaseHelper.getCourseCode(eachCourse), DatabaseHelper.trimInitial(cursor.getString(1)), section, level, term, cursor.getString(3), courseUtils.getTime(cursor.getString(4)), cursor.getString(2), DatabaseHelper.getTimeWeight(cursor.getString(4)), courseUtils.getCourseTitle(eachCourse, campus, dept, program));
                        finalDayData.add(newDayData);
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        }
        return finalDayData;
    }
}
