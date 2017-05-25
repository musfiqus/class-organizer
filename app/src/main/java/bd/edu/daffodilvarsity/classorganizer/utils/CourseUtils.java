package bd.edu.daffodilvarsity.classorganizer.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import bd.edu.daffodilvarsity.classorganizer.data.DayData;

/**
 * Created by musfiqus on 5/25/2017.
 */

public class CourseUtils {
    public static class CourseTitleGenerator extends SQLiteAssetHelper {
        private static String DATABASE_NAME = "course_title.db";
        private static String COLUMN_COURSE_CODE = "course_code";
        private static String COLUMN_COURSE_TITLE = "course_title";
        public static final int DATABASE_VERSION = 2;
        private static CourseTitleGenerator mInstance = null;

        private CourseTitleGenerator(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            setForcedUpgrade();
        }

        public static CourseTitleGenerator getInstance(Context context) {

            // Use the application context, which will ensure that you
            // don't accidentally leak an Activity's context.
            // See this article for more information: http://bit.ly/6LRzfx
            if (mInstance == null) {
                mInstance = new CourseTitleGenerator(context.getApplicationContext());
            }
            return mInstance;
        }

        public String getCourseTitle(String courseCode, String dept, String program) {
            SQLiteDatabase db = this.getReadableDatabase();
            final String tableName = "title_"+dept+"_"+program;
            String courseTitle = null;
            if (courseCode != null) {
                String id = removeSpaces(courseCode);
                Cursor cursor = db.query(tableName, new String[]{COLUMN_COURSE_CODE,
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
        private String removeSpaces(String strings) {
            return strings.replaceAll("\\s+", "");
        }
    }

}
