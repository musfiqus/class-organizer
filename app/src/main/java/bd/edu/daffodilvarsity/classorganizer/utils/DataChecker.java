package bd.edu.daffodilvarsity.classorganizer.utils;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;

import bd.edu.daffodilvarsity.classorganizer.data.DayData;

/**
 * Created by Mushfiqus Salehin on 6/11/2017.
 * musfiqus@gmail.com
 */

public class DataChecker {

    private static final int VALID_CHOICE = 0;
    private static final int INVALID_LEVEL_TERM = 1;
    private static final int INVALID_SECTION = 2;
    private static final int INVALID_DEPARTMENT = 3;
    private static final int INVALID_PROGRAM = 4;
    private static final int INVALID_CAMPUS = 5;
    private static final int INVALID_CAMPUS_MIXED = 6;
    private static final int INVALID_CLASS_MIXED = 7;
    private static final int INVALID_TEACHER_INITIAL = 9;

    private static Toast toast;
    private Context context;

    public DataChecker(Context context) {
        this.context = context;
    }

    public int campusChecker(String campus, String department, String program) {
        CourseUtils courseUtils = CourseUtils.getInstance(context);
        if (campus == null) {
            return INVALID_CAMPUS;
        }
        if (department == null) {
            return INVALID_DEPARTMENT;
        }
        if (program == null) {
            return INVALID_PROGRAM;
        }
        if (!courseUtils.doesTableExist("departments_"+campus)) {
            return INVALID_CAMPUS;
        }
        if (!courseUtils.checkDepartment(campus, department)) {
            return INVALID_DEPARTMENT;
        }
        int totalSemester = courseUtils.getTotalSemester(campus, department, program);
        if (totalSemester == 0) {
            return INVALID_PROGRAM;
        }
        ArrayList<String> sections = courseUtils.getSections(campus, department, program);
        if (sections == null || sections.size() == 0) {
            return INVALID_CAMPUS_MIXED;
        }
        ArrayList<DayData> routine = new RoutineLoader(0,0, sections.get(0), context, department, campus, program).loadRoutine(false);
        if (routine == null || routine.size() == 0) {
            return INVALID_CAMPUS_MIXED;
        }
        return VALID_CHOICE;
    }

    public int classChecker(String section, int level, int term) {
        CourseUtils courseUtils = CourseUtils.getInstance(context);
        PrefManager prefManager = new PrefManager(context);
        if (section == null) {
            return INVALID_SECTION;
        }
        ArrayList<String> codes = courseUtils.getCourseCodes(RoutineLoader.getSemester(level, term), prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram());
        if (codes == null || codes.size() == 0) {
            return INVALID_LEVEL_TERM;
        }
        ArrayList<DayData> routine = new RoutineLoader(level,term, section, context, prefManager.getDept(), prefManager.getCampus(), prefManager.getProgram()).loadRoutine(false);
        if (routine == null || routine.size() == 0) {
            return INVALID_CLASS_MIXED;
        }
        return VALID_CHOICE;
    }

    public int classChecker(String teachersInitial) {
        CourseUtils courseUtils = CourseUtils.getInstance(context);
        PrefManager prefManager = new PrefManager(context);
        ArrayList<DayData> routine = new RoutineLoader(teachersInitial, prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram(), context).loadRoutine(false);
        if (routine == null || routine.size() == 0) {
            return INVALID_TEACHER_INITIAL;
        }
        return VALID_CHOICE;
    }

    public static void errorMessage(Context context, int errorCode, String message) {
        if (message == null) {
            if (errorCode == INVALID_CAMPUS) {
                message = "Invalid Campus Selection";
            }
            if (errorCode == INVALID_DEPARTMENT) {
                message = "Routine for this department isn't available yet.";
            }
            if (errorCode == INVALID_PROGRAM) {
                message = "Routine for this program isn't available yet.";
            }
            if (errorCode == INVALID_LEVEL_TERM) {
                message = "Routine for this level or term isn't available yet.";
            }
            if (errorCode == INVALID_SECTION) {
                message = "Routine for this section isn't yet included";
            }
            if (errorCode == INVALID_CAMPUS_MIXED) {
                message = "No routine found for your selection";
            }
            if (errorCode == INVALID_CLASS_MIXED) {
                message = "Routine for this section in this level or term isn't available yet.";
            }
        }
        if (message != null && toast == null) {
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.show();
            toast = null;
        }
    }
}
