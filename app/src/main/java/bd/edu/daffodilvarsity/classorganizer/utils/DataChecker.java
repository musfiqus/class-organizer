package bd.edu.daffodilvarsity.classorganizer.utils;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import bd.edu.daffodilvarsity.classorganizer.data.DayData;

/**
 * Created by Mushfiqus Salehin on 6/11/2017.
 * musfiqus@gmail.com
 */

public class DataChecker {

    private static final String TAG = "DataChecker";

    private static final int VALID_CHOICE = 0;
    private static final int INVALID_LEVEL_TERM = 1;
    private static final int INVALID_SECTION = 2;
    private static final int INVALID_DEPARTMENT = 3;
    private static final int INVALID_PROGRAM = 4;
    private static final int INVALID_CAMPUS = 5;
    private static final int INVALID_CAMPUS_MIXED = 6;
    private static final int INVALID_CLASS_MIXED = 7;
    private static final int INVALID_TEACHER_INITIAL = 9;

    private Context context;

    public DataChecker(Context context) {
        this.context = context;
    }

    public int campusChecker(String campus, String department, String program, boolean isUserStudent) {
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
        if (isUserStudent) {
            ArrayList<String> sections = courseUtils.getSections(campus, department, program);
            if (sections == null || sections.size() == 0) {
                Log.e(TAG, "HEREEEEEE");
                return INVALID_CAMPUS_MIXED;
            }
            ArrayList<DayData> routine = new RoutineLoader(0,0, sections.get(0), context, department, campus, program).loadRoutine(false);
            if (routine == null || routine.size() == 0) {
                Log.e(TAG, "NOOOOOOO HEREEEEEE");
                return INVALID_CAMPUS_MIXED;
            }
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

    public int teacherChecker(String teachersInitial) {
        CourseUtils courseUtils = CourseUtils.getInstance(context);
        PrefManager prefManager = new PrefManager(context);
        ArrayList<DayData> routine = new RoutineLoader(teachersInitial, prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram(), context).loadRoutine(false);
        if (routine == null || routine.size() == 0) {
            return INVALID_TEACHER_INITIAL;
        }
        return VALID_CHOICE;
    }

    public int campusTeacherChecker(String campus, String dept, String program) {
        CourseUtils courseUtils = CourseUtils.getInstance(context);
        PrefManager prefManager = new PrefManager(context);
        String initial = CourseUtils.getInstance(context).getTeachersInitials(campus, dept, program).get(0);
        if (initial == null) {
            return INVALID_TEACHER_INITIAL;
        }
        ArrayList<DayData> routine = new RoutineLoader(initial,campus, dept, program, context).loadRoutine(false);
        if (routine == null || routine.size() == 0) {
            return INVALID_TEACHER_INITIAL;
        }
        return VALID_CHOICE;
    }

    public static void errorMessage(final Activity activity, int errorCode, String message) {
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
        if (message != null && activity != null) {
            final String finalMessage = message;
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(activity, finalMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public static void errorSnack(Activity activity, int errorCode) {
        String message;
            if (errorCode == INVALID_CAMPUS) {
                message = "Invalid Campus Selection";
            } else if (errorCode == INVALID_DEPARTMENT) {
                message = "Routine for this department isn't available yet.";
            } else if (errorCode == INVALID_PROGRAM) {
                message = "Routine for this program isn't available yet.";
            } else if (errorCode == INVALID_LEVEL_TERM) {
                message = "Routine for this level or term isn't available yet.";
            }else if (errorCode == INVALID_SECTION) {
                message = "Routine for this section isn't yet included";
            } else if (errorCode == INVALID_CAMPUS_MIXED) {
                message = "No routine found for your selection";
            } else {
                message = "Routine for this section in this level or term isn't available yet.";
            }
        showSnackBar(activity, message);
    }

    public static void formattedError(final Activity activity, int errorCode, String campus, String dept, String program, int level, int term, String section) {
        String message;
        if (errorCode == INVALID_CAMPUS) {
            message = "Invalid campus selection";
        } else if (errorCode == INVALID_DEPARTMENT) {
            message = "Routine for "+dept.toUpperCase()+" department isn't available yet.";
        } else if (errorCode == INVALID_PROGRAM) {
            message = "Routine for "+program.toUpperCase()+" program isn't available yet.";
        } else if (errorCode == INVALID_LEVEL_TERM) {
            message = "Routine for level "+(level+1)+" or term "+(term+1)+" isn't available yet.";
        }else if (errorCode == INVALID_SECTION) {
            message = "Routine for section "+section+" isn't included yet.";
        } else if (errorCode == INVALID_CAMPUS_MIXED) {
            message = "No routine found for your selection";
        } else {
            message = "Routine for section "+section.toUpperCase()+" in level "+(level+1)+" term "+(term+1)+" isn't available yet.";
        }
        if (activity != null) {
            final String finalMessage = message;
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(activity, finalMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    //Method to display snackbar properly
    public static void showSnackBar(final Activity activity, final String message) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
                Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
            }
        });

    }
}
