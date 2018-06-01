package bd.edu.daffodilvarsity.classorganizer.utils;

import android.content.Context;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.data.DayData;

/**
 * Created by Mushfiqus Salehin on 5/25/2017.
 * musfiqus@gmail.com
 */

public class CourseUtils {

    private static final String TAG = "CourseUtils";

    public static final int GET_CAMPUS = 0;
    public static final int GET_DEPARTMENT = 1;
    public static final int GET_START_TIME = 2;
    public static final int GET_END_TIME = 4;

    private Context mContext;


    private static CourseUtils mInstance = null;

    private CourseUtils(Context context) {
        mContext = context.getApplicationContext();
    }

    public CourseUtils(Context context, boolean isUpdatedOnline) {
        this(context);
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

    public boolean isDatabaseWritable() {
        return RoutineDB.getInstance(mContext).isDatabaseWritable();
    }

    ArrayList<DayData> getDayData(ArrayList<String> courseCodes, String section, int level, int term, String dept, String campus, String program) {
        return RoutineDB.getInstance(mContext).getDayData(courseCodes, section, level, term, dept, campus, program);
    }

    public ArrayList<DayData> getDayDataByQuery(String campus, String dept, String program, String query, String columnName) {
        PrefManager prefManager = new PrefManager(mContext);
        ArrayList<DayData> dayDataList = new ArrayList<>();
        if (!prefManager.isUserStudent() && prefManager.isMultiProgram()) {
            ArrayList<DayData> day;
            ArrayList<DayData> eve;
            dayDataList.clear();
            day = RoutineDB.getInstance(mContext).getDayDataByQuery(campus, dept,
                        mContext.getResources().getStringArray(R.array.programs)[0].toLowerCase(), query, columnName);
            eve = RoutineDB.getInstance(mContext).getDayDataByQuery(campus, dept,
                        mContext.getResources().getStringArray(R.array.programs)[1].toLowerCase(), query, columnName);
            dayDataList.addAll(day);
            dayDataList.addAll(eve);
        } else {
            dayDataList.clear();
            dayDataList = RoutineDB.getInstance(mContext).getDayDataByQuery(campus, dept, program, query, columnName);
        }
        return dayDataList;

    }

    public ArrayList<DayData> getFreeRoomsByTime(String campus, String dept, String program, String weekday, String timeWeight) {
        PrefManager prefManager = new PrefManager(mContext);
        ArrayList<DayData> dayDataList = new ArrayList<>();
        if (!prefManager.isUserStudent() && prefManager.isMultiProgram()) {
            dayDataList.clear();
            ArrayList<DayData> day;
            ArrayList<DayData> eve;
            day = RoutineDB.getInstance(mContext).getFreeRoomsByTime(campus, dept,
                        mContext.getResources().getStringArray(R.array.programs)[0].toLowerCase(), weekday, timeWeight);
            eve = RoutineDB.getInstance(mContext).getFreeRoomsByTime(campus, dept,
                        mContext.getResources().getStringArray(R.array.programs)[1].toLowerCase(), weekday, timeWeight);
            if (day != null) {
                dayDataList.addAll(day);
            }
            if (eve != null) {
                dayDataList.addAll(eve);
            }
        } else {
            dayDataList.clear();
            dayDataList = RoutineDB.getInstance(mContext).getFreeRoomsByTime(campus, dept, program, weekday, timeWeight);
        }
        return dayDataList;
    }

    public ArrayList<DayData> getFreeRoomsByRoom(String campus, String dept, String program, String room) {
        PrefManager prefManager = new PrefManager(mContext);
        ArrayList<DayData> dayDataList = new ArrayList<>();
        if (!prefManager.isUserStudent() && prefManager.isMultiProgram()) {
            dayDataList.clear();
            ArrayList<DayData> day;
            ArrayList<DayData> eve;
            day = RoutineDB.getInstance(mContext).getFreeRoomsByRoom(campus, dept,
                        mContext.getResources().getStringArray(R.array.programs)[0].toLowerCase(), room);
            eve = RoutineDB.getInstance(mContext).getFreeRoomsByRoom(campus, dept,
                        mContext.getResources().getStringArray(R.array.programs)[1].toLowerCase(), room);
            if (day != null) {
                dayDataList.addAll(day);
            }
            if (eve != null) {
                dayDataList.addAll(eve);
            }
        } else {
            dayDataList.clear();
            dayDataList = RoutineDB.getInstance(mContext).getFreeRoomsByRoom(campus, dept, program, room);

        }
        return dayDataList;
    }

    public String getCourseTitle(String courseCode, String campus, String dept, String program) {
        return RoutineDB.getInstance(mContext).getCourseTitle(courseCode, campus, dept, program);
    }

    public String getTime(String timeData) {
        return RoutineDB.getInstance(mContext).getTime(timeData);
    }

    public ArrayList<String> getCourseCodes(int semester, String campus, String department, String program) {
        return RoutineDB.getInstance(mContext).getCourseCodes(semester, campus, department, program);
    }

    public ArrayList<String> getSections(String campus, String department, String program) {
        return RoutineDB.getInstance(mContext).getSections(campus, department, program);
    }

    //Method to retrieve spinner data
    public ArrayList<String> getSpinnerList(int code) {
        return RoutineDB.getInstance(mContext).getSpinnerList(code);
    }


    //Gets the total number of semester for the course. EG: for cse day it's 12. If it doesn't exist the value is 0
    public int getTotalSemester(String campus, String department, String program) {
        return RoutineDB.getInstance(mContext).getTotalSemester(campus,department,program);
    }


    //Gets the name of the current semester
    public String getCurrentSemester(String campus, String department, String program) {
        return RoutineDB.getInstance(mContext).getCurrentSemester(campus,department,program);
    }

    //Gets the integer value of current semester of database
    //This value determines whether to update semester or not
    public int getSemesterCount(String campus, String department, String program) {
        return RoutineDB.getInstance(mContext).getSemesterCount(campus,department,program);
    }

    //Get time weight from start time
    public double getTimeWeightFromStart(String startTime) {
        return RoutineDB.getInstance(mContext).getTimeWeightFromStart(startTime);
    }

    //Checks if table exists in the db using table name
    public boolean doesTableExist(final String TABLE_NAME) {
        return RoutineDB.getInstance(mContext).doesTableExist(TABLE_NAME);
    }

    public ArrayList<String> getTeachersInitials(String campus, String department, String program) {
        PrefManager prefManager = new PrefManager(mContext);
        if (!prefManager.isUserStudent() && prefManager.isMultiProgram()) {
            Set<String> dataArrayList = new HashSet<>();
            ArrayList<String> day;
            ArrayList<String> eve;

            day = RoutineDB.getInstance(mContext).getTeachersInitials(campus, department, mContext.getResources().getStringArray(R.array.programs)[0].toLowerCase());
            eve = RoutineDB.getInstance(mContext).getTeachersInitials(campus, department, mContext.getResources().getStringArray(R.array.programs)[1].toLowerCase());

            if (day != null) {
                dataArrayList.addAll(day);
            }
            if (eve != null) {
                dataArrayList.addAll(eve);
            }
            ArrayList<String> list = new ArrayList<>();
            list.addAll(dataArrayList);
            Collections.sort(list, new Comparator<String>() {
                @Override
                public int compare(String s1, String s2) {
                    return s1.compareToIgnoreCase(s2);
                }
            });
            return list;
        } else {
            return RoutineDB.getInstance(mContext).getTeachersInitials(campus, department, program);
        }
    }

    public ArrayList<String> getRoomNo(String campus, String department, String program) {
        return RoutineDB.getInstance(mContext).getRoomNo(campus, department, program);
    }

    //All methods below this are for DataChecker
    public boolean checkDepartment(String campus, String department) {
        return RoutineDB.getInstance(mContext).checkDepartment(campus, department);
    }
    public Date getDateFromSchedule(final String COLUMN_NAME, String currentSemester, String campus, String department, String program) {
        return RoutineDB.getInstance(mContext).getDateFromSchedule(COLUMN_NAME, currentSemester, campus, department, program);
    }

    public static DayData convertToDayData(byte[] dayByte) {
        ByteArrayInputStream bis = new ByteArrayInputStream(dayByte);
        ObjectInput in = null;
        DayData dayData = null;
        try {
            in = new ObjectInputStream(bis);
            dayData = (DayData)in.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return dayData;
    }

    public static byte[] convertToByteArray(DayData dayData) {
        byte[] data = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(dayData);
            out.flush();
            data = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return data;
    }
}
