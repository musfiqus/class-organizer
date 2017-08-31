package bd.edu.daffodilvarsity.classorganizer.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.data.DayData;

/**
 * Created by Mushfiqus Salehin on 6/11/2017.
 * musfiqus@gmail.com
 */

public class DataChecker {
    private static final String CAMPUS_MAIN = "main";
    private static final String CAMPUS_PERMANENT = "permanent";
    private static final String PROGRAM_DAY = "day";
    private static final String PROGRAM_EVENING = "evening";
    private static final String DEPARTMENT_CSE = "cse";

    private static final int VALID_CHOICE = 0;
    private static final int INVALID_LEVEL_TERM = 1;
    private static final int INVALID_SECTION = 2;
    private static final int INVALID_DEPARTMENT = 3;
    private static final int INVALID_PROGRAM = 4;
    private static final int INVALID_CAMPUS = 5;

    private static Toast toast;
    private Context context;
    private int level = 0;
    private int term = 0;
    private String section;
    private String dept = "CSE";
    private String campus = "main";
    private String program = "day";

    public DataChecker(Context context, String dept, String campus, String program) {
        this.context = context;
        if (dept != null) {
            this.dept = dept;
        }
        if (campus != null) {
            this.campus = campus;
        }
        if (program != null) {
            this.program = program;
        }
    }

    public DataChecker(Context context, int level, int term, String section, String dept, String campus, String program) {
        this.context = context;
        this.level = level;
        this.term = term;
        this.section = section;
        this.dept = dept;
        this.campus = campus;
        this.program = program;
    }

    public static boolean isEvening(String program) {
        if (program == null) {
            return false;
        }
        return PROGRAM_EVENING.equalsIgnoreCase(program);
    }

    public static boolean isDay(String program) {
        if (program == null) {
            return false;
        }
        return PROGRAM_DAY.equalsIgnoreCase(program);
    }

    public static boolean isMain(String campus) {
        Log.e("Campus: ", " "+campus);
        if (campus == null) {
            return false;
        }
        return CAMPUS_MAIN.equalsIgnoreCase(campus);
    }

    public static boolean isPermanent(String campus) {
        if (campus == null) {
            return false;
        }
        return CAMPUS_PERMANENT.equalsIgnoreCase(campus);
    }

    public static boolean isCSE(String department) {
        return DEPARTMENT_CSE.equalsIgnoreCase(department);
    }

    public int dataChecker() {
        if (isMain(campus)) {
            if (new RoutineLoader(0, 0, "A", context, "CSE", campus, "day").loadRoutine(false) == null) {
                return INVALID_CAMPUS;
            }
        } else if (isPermanent(campus)) {
            if (new RoutineLoader(0, 0, "PC-A", context, "CSE", campus, "day").loadRoutine(false) == null) {
                return INVALID_CAMPUS;
            }
        }
        if (!checkDepartment(campus, dept)) {
            return INVALID_DEPARTMENT;
        }
        if (!checkProgram(campus, dept, program)) {
            return INVALID_PROGRAM;
        }
        ArrayList<String> codes = RoutineLoader.courseCodeGenerator(context, RoutineLoader.getSemester(level, term), campus, dept, program);
        if (codes == null) {
            return INVALID_LEVEL_TERM;
        } else {
            if (codes.size() == 0) {
                return INVALID_LEVEL_TERM;
            }
        }
        if (section != null) {
            ArrayList<DayData> size = new RoutineLoader(level, term, section, context, dept, campus, program).loadRoutine(false);
            if (size == null) {
                Log.e("PC", "Null");
                return INVALID_SECTION;
            } else {
                if (size.size() == 0) {
                    Log.e("PC", "0");
                    return INVALID_SECTION;
                }
            }
        }
        return VALID_CHOICE;
    }

    private boolean checkDepartment(String campus, String dept) {
        if (isMain(campus)) {
            return doesStringExist(new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.main_departments))), dept);
        } else {
            return doesStringExist(new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.permanent_departments))), dept);
        }
    }

    private boolean checkProgram(String campus, String dept, String program) {
        if (isMain(campus)) {
            if (isCSE(dept)) {
                return doesStringExist(new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.cse_main_programs))), program);
            }
        } else {
            if (isCSE(dept)) {
                return doesStringExist(new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.cse_perm_programs))), program);
            }
        }
        return false;
    }

    private boolean doesStringExist(ArrayList<String> list, String string) {
        if (list != null) {
            for (String eachString : list) {
                if (eachString.equalsIgnoreCase(string)) {
                    return true;
                }
            }
        }
        return false;
    }



    public static void errorMessage(Context context, int errorCode, String message) {
        if (message == null) {
            if (errorCode == INVALID_CAMPUS) {
                message = "Invalid Campus Selection";
            }
            if (errorCode == INVALID_DEPARTMENT) {
                message = "Routine for this department isn't yet available";
            }
            if (errorCode == INVALID_PROGRAM) {
                message = "Routine for this program isn't yet available";
            }
            if (errorCode == INVALID_LEVEL_TERM) {
                message = "Routine for this level or term isn't yet available";
            }
            if (errorCode == INVALID_SECTION) {
                message = "Routine for this section isn't yet included";
            }
        }
        if (message != null && toast == null) {
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.show();
            toast = null;
        }
    }
}
