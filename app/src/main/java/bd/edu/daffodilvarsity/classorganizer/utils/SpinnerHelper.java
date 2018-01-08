package bd.edu.daffodilvarsity.classorganizer.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IllegalFormatCodePointException;

import bd.edu.daffodilvarsity.classorganizer.R;

/**
 * Created by Mushfiqus Salehin on 6/11/2017.
 * musfiqus@gmail.com
 */

public class SpinnerHelper implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "SpinnerHelper";

    private PrefManager prefManager;
    private Context context;
    private View view;
    private int spinnerRowResource;
    private boolean isStudent;

    private Spinner campusSpinner;
    private ArrayAdapter<String> campusAdapter;

    private Spinner deptSpinner;
    ArrayAdapter<String> deptAdapter;

    private Spinner programSpinner;
    ArrayAdapter<CharSequence> programAdapter;

    private ArrayAdapter<CharSequence>  levelAdapter, termAdapter;
    private Spinner levelSpinner;
    private Spinner termSpinner;
    private Spinner sectionSpinner;
    private ArrayAdapter<String> sectionAdapter;

    private Spinner teachersInitialSpinner;
    private ArrayAdapter<String> teacherAdapter;

    private String campus;
    private String dept;
    private String program;
    private int level;
    private int term;
    private String section;
    private String teachersInitial;

    private int classDataCode;
    private int campusDataCode;

    private boolean isCampusMode;


    public SpinnerHelper(Context context, View view, int spinnerRowResource, boolean isStudent, boolean isCampusMode) {
        this.isCampusMode = isCampusMode;
        this.context = context;
        this.prefManager = new PrefManager(context);
        this.spinnerRowResource = spinnerRowResource;
        this.isStudent = isStudent;
        viewChooser(view);
    }

    private void viewChooser(View view) {
        if (view.getId() == R.id.class_spinner_layout_id || view.getId() == R.id.campus_spinner_layout_id) {
            this.view = view;
        } else {
            if (view.getId() == R.id.welcome_choice_layout_5) {
                Log.e(TAG, "User type: "+ (isStudent ? "Student" : "teacher"));
                if (isStudent) {
                    //user student
                    view.findViewById(R.id.teachers_initial_layout).setVisibility(View.GONE);
                    this.view = view.findViewById(R.id.section_layout_include_id);
                } else {
                    //user teacher
                    view.findViewById(R.id.section_layout_include_id).setVisibility(View.GONE);
                    this.view = view.findViewById(R.id.teachers_initial_layout);
                }
            } else {
                this.view = view.findViewById(R.id.campus_layout_include_id);
            }
        }
    }

    public void createTeacherInitSpinners() {
        teachersInitialSpinner = (Spinner) view.findViewById(R.id.teachers_initial_selection_welcome);

    }

    public void createTeacherInitAdapter(String campus, String dept, String program) {
        teacherAdapter = new ArrayAdapter<String>(context, R.layout.spinner_row, CourseUtils.getInstance(context).getTeachersInitials(campus, dept, program));
    }

    public void attachTeacherInitAdapter() {
        if (teachersInitialSpinner != null && teacherAdapter != null) {
            teachersInitialSpinner.setAdapter(teacherAdapter);
            teachersInitialSpinner.setOnItemSelectedListener(this);
        } else {
            Log.e(TAG, "Teacher Spinner or Adapter null");
        }
    }

    public void setupClassLabelBlack() {
        TextView levelLabel = (TextView) view.findViewById(R.id.level_spinner_label);
        levelLabel.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        TextView termLabel = (TextView) view.findViewById(R.id.term_spinner_label);
        termLabel.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        TextView sectionLabel = (TextView) view.findViewById(R.id.section_spinner_label);
        sectionLabel.setTextColor(ContextCompat.getColor(context, android.R.color.black));
    }

    public void setupCampusLabelBlack() {
        TextView campusLabel = (TextView) view.findViewById(R.id.campus_spinner_label);
        TextView deptLabel = (TextView) view.findViewById(R.id.dept_spinner_label);
        TextView programLabel = (TextView) view.findViewById(R.id.program_spinner_label);
        campusLabel.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        deptLabel.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        programLabel.setTextColor(ContextCompat.getColor(context, android.R.color.black));
    }

    public void setupClass(String campus, String department, String program) {
        setupClassSpinners();
        setupClassAdapters(campus, department, program);
    }

    public void setupCampus() {
        createCampusSpinners();
        setupCampusAdapters();
    }

    //New separate Thread methods
    public void createClassSpinners() {
        sectionSpinner = (Spinner) view.findViewById(R.id.section_selection);
        levelSpinner = (Spinner) view.findViewById(R.id.level_spinner);
        termSpinner = (Spinner) view.findViewById(R.id.term_spinner);
    }

    public void createClassAdapters(String campus, String department, String program) {
        levelAdapter = ArrayAdapter.createFromResource(context, R.array.cse_main_day_level_array, spinnerRowResource);
        termAdapter = ArrayAdapter.createFromResource(context, R.array.term_array, spinnerRowResource);
        ArrayList<String> sections = CourseUtils.getInstance(context).getSections(campus, department, program);
        if (sections != null && sections.size() != 0) {
            sectionAdapter = new ArrayAdapter<>(context, spinnerRowResource, sections);
        } else {
            sectionAdapter = null;
        }
    }

    public void attachClassSpinners() {
        levelSpinner.setAdapter(levelAdapter);
        termSpinner.setAdapter(termAdapter);
        sectionSpinner.setAdapter(sectionAdapter);
        levelSpinner.setOnItemSelectedListener(this);
        termSpinner.setOnItemSelectedListener(this);
        sectionSpinner.setOnItemSelectedListener(this);
    }

    public void setupClassSpinners() {
        sectionSpinner = (Spinner) view.findViewById(R.id.section_selection);
        levelSpinner = (Spinner) view.findViewById(R.id.level_spinner);
        termSpinner = (Spinner) view.findViewById(R.id.term_spinner);
    }

    public void setupClassAdapters(String campus, String department, String program) {
        ArrayAdapter<CharSequence>  levelAdapter, termAdapter;
        levelAdapter = ArrayAdapter.createFromResource(context, R.array.cse_main_day_level_array, spinnerRowResource);
        termAdapter = ArrayAdapter.createFromResource(context, R.array.term_array, spinnerRowResource);
        levelSpinner.setAdapter(levelAdapter);
        termSpinner.setAdapter(termAdapter);
        sectionAdapter(campus, department, program);
        levelSpinner.setOnItemSelectedListener(this);
        termSpinner.setOnItemSelectedListener(this);
        sectionSpinner.setOnItemSelectedListener(this);
    }

    public void sectionAdapter(String campus, String department, String program) {
        Log.e(TAG, campus+department+program);
        if (sectionSpinner != null) {
            ArrayList<String> sections = CourseUtils.getInstance(context).getSections(campus, department, program);
            ArrayAdapter<String> sectionAdapter;
            if (sections != null && sections.size() != 0) {
                sectionAdapter = new ArrayAdapter<>(context, spinnerRowResource, sections);
            } else {
                sectionAdapter = null;
            }
            if (sectionAdapter != null) {
                sectionSpinner.setAdapter(sectionAdapter);
                sectionSpinner.setOnItemSelectedListener(this);
            }
        }
    }

    public void createCampusSpinners() {
        campusSpinner = (Spinner) view.findViewById(R.id.campus_selection);
        deptSpinner = (Spinner) view.findViewById(R.id.dept_selection);
        programSpinner = (Spinner) view.findViewById(R.id.program_selection);
    }

    public void createCampusAdapters() {
        campusAdapter = new ArrayAdapter<>(context, spinnerRowResource, CourseUtils.getInstance(context).getSpinnerList(CourseUtils.GET_CAMPUS));
        deptAdapter = new ArrayAdapter<>(context, spinnerRowResource, CourseUtils.getInstance(context).getSpinnerList(CourseUtils.GET_DEPARTMENT));
        programAdapter = ArrayAdapter.createFromResource(context, R.array.programs, spinnerRowResource);
    }

    public void attachCampusAdapters() {
        campusSpinner.setAdapter(campusAdapter);
        deptSpinner.setAdapter(deptAdapter);
        programSpinner.setAdapter(programAdapter);
        campusSpinner.setOnItemSelectedListener(this);
        deptSpinner.setOnItemSelectedListener(this);
        programSpinner.setOnItemSelectedListener(this);
    }

    public void setupCampusAdapters() {
        ArrayAdapter<String> campusAdapter = new ArrayAdapter<>(context, spinnerRowResource, CourseUtils.getInstance(context).getSpinnerList(CourseUtils.GET_CAMPUS));
        ArrayAdapter<String> deptAdapter = new ArrayAdapter<>(context, spinnerRowResource, CourseUtils.getInstance(context).getSpinnerList(CourseUtils.GET_DEPARTMENT));
        ArrayAdapter<CharSequence> programAdapter = ArrayAdapter.createFromResource(context, R.array.programs, spinnerRowResource);
        campusSpinner.setAdapter(campusAdapter);
        deptSpinner.setAdapter(deptAdapter);
        programSpinner.setAdapter(programAdapter);
            campusSpinner.setOnItemSelectedListener(this);
            deptSpinner.setOnItemSelectedListener(this);
            programSpinner.setOnItemSelectedListener(this);
    }

    public int spinnerPositionGenerator(int id, String string) {
        ArrayList<String> sectionList = new ArrayList<>(Arrays.asList(context.getResources().getStringArray(id)));
        for (int i = 0; i < sectionList.size(); i++) {
            if (sectionList.get(i).toLowerCase().equalsIgnoreCase(string)) {
                return i;
            }
        }
        return 0;
    }

    public int spinnerPositionGenerator(ArrayList<String> list, String string) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).toLowerCase().equalsIgnoreCase(string)) {
                return i;
            }
        }
        return 0;
    }

    public void setClassSpinnerPositions(int level, int term, String section) {
        setLevelSpinnerPosition(level);
        setTermSpinnerPosition(term);
        PrefManager prefManager = new PrefManager(context);
        int position = spinnerPositionGenerator(CourseUtils.getInstance(context).getSections(prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram()), section);
        setSectionSpinnerPosition(position);
    }

    public void setCampusSpinnerPositions(String campus, String dept, String program) {
        ArrayList<String> campuses = CourseUtils.getInstance(context).getSpinnerList(CourseUtils.GET_CAMPUS);
        ArrayList<String> departments = CourseUtils.getInstance(context).getSpinnerList(CourseUtils.GET_DEPARTMENT);
        setCampusSpinnerPosition(spinnerPositionGenerator(campuses, campus));
        setDeptSpinnerPosition(spinnerPositionGenerator(departments, dept));
        setProgramSpinnerPosition(spinnerPositionGenerator(R.array.programs, program));
    }

    public void setLevelSpinnerPosition(int position) {
        if (levelSpinner != null) {
            levelSpinner.setSelection(position);
        }
    }

    public void setTermSpinnerPosition(int position) {
        if (termSpinner != null) {
            termSpinner.setSelection(position);
        }
    }

    public void setSectionSpinnerPosition(int position) {
        if (sectionSpinner != null) {
            sectionSpinner.setSelection(position);
        }
    }

    public void setCampusSpinnerPosition(int position) {
        if (campusSpinner != null) {
            campusSpinner.setSelection(position);
        }
    }

    public void setDeptSpinnerPosition(int position) {
        if (deptSpinner != null) {
            deptSpinner.setSelection(position);
        }
    }

    public void setProgramSpinnerPosition(int position) {
        if (programSpinner != null) {
            programSpinner.setSelection(position);
        }
    }

    public String getCampus() {
        if (campusSpinner == null || campusSpinner.getSelectedItem() == null) {
            return null;
        }
        return campusSpinner.getSelectedItem().toString();
    }

    public String getDept() {
        if (deptSpinner == null || deptSpinner.getSelectedItem() == null) {
            return null;
        }
        return deptSpinner.getSelectedItem().toString();
    }

    public String getProgram() {
        if (programSpinner == null || programSpinner.getSelectedItem() == null) {
            return null;
        }
        return programSpinner.getSelectedItem().toString();
    }

    public int getLevel() {
        if (levelSpinner == null) {
            return 0;
        }
        return levelSpinner.getSelectedItemPosition();
    }

    public int getTerm() {
        if (termSpinner == null) {
            return 0;
        }
        return termSpinner.getSelectedItemPosition();
    }

    public String getSection() {
        if (sectionSpinner == null || sectionSpinner.getSelectedItem() == null) {
            return null;
        }
        return sectionSpinner.getSelectedItem().toString();
    }

    public String getTeachersInitial() {
        return teachersInitial;
    }

    private boolean isCampusSpinnersNull() {
        if (campusSpinner == null) {
            return true;
        }
        if (deptSpinner == null) {
            return true;
        }
        if (programSpinner == null) {
            return true;
        }
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {


        //Saving selections on first launch
        if (isCampusMode) {
            if (!isCampusSpinnersNull()) {
                campus = getCampus();
                dept = getDept();
                program = getProgram();
            }
            if (campus != null) {
                prefManager.saveCampus(campus);
            }
            if (dept != null) {
                prefManager.saveDept(dept);
            }
            if (program != null) {
                prefManager.saveProgram(program);
            }
        }

        if (!isCampusMode) {
            if (isStudent) {
                if (parent.getId() == R.id.level_spinner) {
                    level = getLevel();
                } else if (parent.getId() == R.id.term_spinner) {
                    term = getTerm();
                } else if (parent.getId() == R.id.section_selection) {
                    section = getSection();
                }
                //Saving selections
                prefManager.saveSection(section);
                prefManager.saveTerm(term);
                prefManager.saveLevel(level);
            } else {
                if (parent.getId() == R.id.teachers_initial_selection_welcome) {
                    teachersInitial = teachersInitialSpinner.getSelectedItem().toString();
                    prefManager.saveTeacherInitial(teachersInitial);
                }
            }

        }
        campusDataCode = new DataChecker(context).campusChecker(campus, dept, program);
        if (isStudent) {
            classDataCode = new DataChecker(context).classChecker(section, level, term);
        } else {
            classDataCode = new DataChecker(context).classChecker(teachersInitial);
        }



    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public int getCampusDataCode() {
        return campusDataCode;
    }

    public int getClassDataCode() {
        return classDataCode;
    }
}
