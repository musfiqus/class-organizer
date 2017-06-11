package bd.edu.daffodilvarsity.classorganizer.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Mushfiqus Salehin on 6/11/2017.
 * musfiqus@gmail.com
 */

public class DataChecker {
    public static int VALID_CHOICE = 0;
    public static int INVALID_LEVEL_TERM = 1;
    public static int INVALID_SECTION = 2;
    public static int INVALID_DEPARTMENT = 3;
    public static int INVALID_PROGRAM = 4;
    public static int INVALID_CAMPUS = 5;

    private Context context;
    private int level = 0;
    private int term = 0;
    private String section = "A";
    private String dept = "CSE";
    private String campus = "main";
    private String program = "day";

    public DataChecker(Context context, int level, int term, String section) {
        this.context = context;
        this.level = level;
        this.term = term;
        if (section != null) {
            this.section = section;
        }
    }

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

    public int dataChecker() {
        if (new RoutineLoader(0, 0, "A", context, "CSE", campus, "day").loadRoutine(false) == null) {
            return INVALID_CAMPUS;
        }
        if (new RoutineLoader(0, 0, "A", context, dept, campus, "day").loadRoutine(false) == null) {
            return INVALID_DEPARTMENT;
        }
        if (new RoutineLoader(0, 0, "A", context, dept, campus, program).loadRoutine(false) == null) {
            return INVALID_PROGRAM;
        }
        if (RoutineLoader.courseCodeGenerator(context, RoutineLoader.getSemester(level, term), campus, dept, program) == null) {
            return INVALID_LEVEL_TERM;
        }
        if (new RoutineLoader(level, term, section, context, dept, campus, program).loadRoutine(false) == null) {
            return INVALID_SECTION;
        }
        return VALID_CHOICE;
    }

    public void errorMessage(Context context, int errorCode, String message) {
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
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
