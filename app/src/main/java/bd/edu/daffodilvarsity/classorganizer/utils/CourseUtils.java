package bd.edu.daffodilvarsity.classorganizer.utils;

import android.content.Context;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import bd.edu.daffodilvarsity.classorganizer.data.DayData;

/**
 * Created by Mushfiqus Salehin on 5/25/2017.
 * musfiqus@gmail.com
 */

public class CourseUtils{


    public static final int GET_CAMPUS = 0;
    public static final int GET_DEPARTMENT = 1;
    public static final int GET_START_TIME = 2;
    public static final int GET_END_TIME = 4;

    private Context mContext;
    private boolean isUpdatedOnline;


    private static CourseUtils mInstance = null;

    private CourseUtils(Context context) {
        mContext = context.getApplicationContext();
        PrefManager prefManager = new PrefManager(context);
        isUpdatedOnline = prefManager.isUpdatedOnline();
    }

    public CourseUtils(Context context, boolean isUpdatedOnline) {
        this(context);
        this.isUpdatedOnline = isUpdatedOnline;
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

    ArrayList<DayData> getDayData(ArrayList<String> courseCodes, String section, int level, int term, String dept, String campus, String program) {
        if (isUpdatedOnline) {
            return MasterDBOnline.getInstance(mContext).getDayData(courseCodes, section, level, term, dept, campus, program);
        } else {
            return MasterDBOffline.getInstance(mContext).getDayData(courseCodes, section, level, term, dept, campus, program);
        }
    }

    ArrayList<DayData> getDayDataByQuery(String campus, String dept, String program, String query, String columnName) {
        if (isUpdatedOnline) {
            return MasterDBOnline.getInstance(mContext).getDayDataByQuery(campus, dept, program, query, columnName);
        } else {
            return MasterDBOffline.getInstance(mContext).getDayDataByQuery(campus, dept, program, query, columnName);
        }
    }

    public String getCourseTitle(String courseCode, String campus, String dept, String program) {
        if (isUpdatedOnline) {
            return MasterDBOnline.getInstance(mContext).getCourseTitle(courseCode, campus, dept, program);
        } else {
            return MasterDBOffline.getInstance(mContext).getCourseTitle(courseCode, campus, dept, program);
        }
    }

    public String getTime(String timeData) {
        if (isUpdatedOnline) {
            return MasterDBOnline.getInstance(mContext).getTime(timeData);
        } else {
            return MasterDBOffline.getInstance(mContext).getTime(timeData);
        }
    }

    public ArrayList<String> getCourseCodes(int semester, String campus, String department, String program) {
        if (isUpdatedOnline) {
            return MasterDBOnline.getInstance(mContext).getCourseCodes(semester, campus, department, program);
        } else {
            return MasterDBOffline.getInstance(mContext).getCourseCodes(semester, campus, department, program);
        }
    }

    public ArrayList<String> getSections(String campus, String department, String program) {
        if (isUpdatedOnline) {
            return MasterDBOnline.getInstance(mContext).getSections(campus, department, program);
        } else {
            return MasterDBOffline.getInstance(mContext).getSections(campus, department, program);
        }
    }

    //Method to retrieve spinner data
    public ArrayList<String> getSpinnerList(int code) {
        if (isUpdatedOnline) {
            return MasterDBOnline.getInstance(mContext).getSpinnerList(code);
        } else {
            return MasterDBOffline.getInstance(mContext).getSpinnerList(code);
        }
    }


    //Gets the total number of semester for the course. EG: for cse day it's 12. If it doesn't exist the value is 0
    public int getTotalSemester(String campus, String department, String program) {
        if (isUpdatedOnline) {
            return MasterDBOnline.getInstance(mContext).getTotalSemester(campus,department,program);
        } else {
            return MasterDBOffline.getInstance(mContext).getTotalSemester(campus,department,program);
        }
    }


    //Gets the name of the current semester
    public String getCurrentSemester(String campus, String department, String program) {
        if (isUpdatedOnline) {
            return MasterDBOnline.getInstance(mContext).getCurrentSemester(campus,department,program);
        } else {
            return MasterDBOffline.getInstance(mContext).getCurrentSemester(campus,department,program);
        }
    }

    //Gets the integer value of current semester of database
    //This value determines whether to update semester or not
    public int getSemesterCount(String campus, String department, String program) {
        if (isUpdatedOnline) {
            return MasterDBOnline.getInstance(mContext).getSemesterCount(campus,department,program);
        } else {
            return MasterDBOffline.getInstance(mContext).getSemesterCount(campus,department,program);
        }
    }

    //Get time weight from start time
    public double getTimeWeightFromStart(String startTime) {
        if (isUpdatedOnline) {
            return MasterDBOnline.getInstance(mContext).getTimeWeightFromStart(startTime);
        } else {
            return MasterDBOffline.getInstance(mContext).getTimeWeightFromStart(startTime);
        }
    }

    //Checks if table exists in the db using table name
    public boolean doesTableExist(final String TABLE_NAME) {
        if (isUpdatedOnline) {
            return MasterDBOnline.getInstance(mContext).doesTableExist(TABLE_NAME);
        } else {
            return MasterDBOffline.getInstance(mContext).doesTableExist(TABLE_NAME);
        }
    }

    public ArrayList<String> getTeachersInitials(String campus, String department, String program) {
        if (isUpdatedOnline) {
            return MasterDBOnline.getInstance(mContext).getTeachersInitials(campus, department, program);
        } else {
            return MasterDBOffline.getInstance(mContext).getTeachersInitials(campus, department, program);
        }
    }

    //All methods below this are for DataChecker
    public boolean checkDepartment(String campus, String department) {
        if (isUpdatedOnline) {
            return MasterDBOnline.getInstance(mContext).checkDepartment(campus, department);
        } else {
            return MasterDBOffline.getInstance(mContext).checkDepartment(campus, department);
        }
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
