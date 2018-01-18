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

import bd.edu.daffodilvarsity.classorganizer.R;

/**
 * Created by musfiqus on 1/10/2018.
 */

public class SpinnerHelperClass implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "SpinnerHelperClass";

    private PrefManager prefManager;
    private Context context;
    private View view;
    private int spinnerRowResource;
    private boolean isStudent;

    private ArrayAdapter<CharSequence> levelAdapter, termAdapter;
    private Spinner levelSpinner;
    private Spinner termSpinner;
    private Spinner sectionSpinner;
    private ArrayAdapter<String> sectionAdapter;

    private int level;
    private int term;
    private String section;
    private String teachersInitial;
    private int classDataCode;
    private Spinner teachersInitialSpinner;
    private ArrayAdapter<String> teacherAdapter;

    public SpinnerHelperClass(Context context, View view, int spinnerRowResource, boolean isStudent) {
        this.context = context;
        this.prefManager = new PrefManager(context);
        this.spinnerRowResource = spinnerRowResource;
        this.isStudent = isStudent;
        viewChooser(view);
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

    public void setupClass(String campus, String department, String program) {
        setupClassSpinners();
        setupClassAdapters(campus, department, program);
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
        if (levelSpinner != null && termSpinner != null && sectionSpinner != null &&
                levelAdapter != null && termAdapter != null && sectionAdapter != null) {
            levelSpinner.setAdapter(levelAdapter);
            termSpinner.setAdapter(termAdapter);
            sectionSpinner.setAdapter(sectionAdapter);
            levelSpinner.setOnItemSelectedListener(this);
            termSpinner.setOnItemSelectedListener(this);
            sectionSpinner.setOnItemSelectedListener(this);
        }
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

    public void setClassSpinnerPositions(int level, int term, String section) {
        setLevelSpinnerPosition(level);
        setTermSpinnerPosition(term);
        PrefManager prefManager = new PrefManager(context);
        int position = spinnerPositionGenerator(CourseUtils.getInstance(context).getSections(prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram()), section);
        setSectionSpinnerPosition(position);
    }

    public void setTeacherSpinnerPosition() {
        String current = prefManager.getTeacherInitial();
        int position = spinnerPositionGenerator(CourseUtils.getInstance(context).getTeachersInitials(prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram()), current);
        if (teachersInitialSpinner != null) {
            teachersInitialSpinner.setSelection(position);
        }
    }

    public int spinnerPositionGenerator(ArrayList<String> list, String string) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).toLowerCase().equalsIgnoreCase(string)) {
                return i;
            }
        }
        return 0;
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


    public void setupClassLabelBlack() {
        TextView levelLabel = (TextView) view.findViewById(R.id.level_spinner_label);
        levelLabel.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        TextView termLabel = (TextView) view.findViewById(R.id.term_spinner_label);
        termLabel.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        TextView sectionLabel = (TextView) view.findViewById(R.id.section_spinner_label);
        sectionLabel.setTextColor(ContextCompat.getColor(context, android.R.color.black));
    }

    public void setupTeacherLabelBlack() {
        TextView teacherLabel = (TextView) view.findViewById(R.id.teachers_initial_selection_welcome_title);
        teacherLabel.setTextColor(ContextCompat.getColor(context, android.R.color.black));
    }

    private void viewChooser(View view) {
        if (view.getId() == R.id.class_spinner_layout_id || view.getId() == R.id.campus_spinner_layout_id || view.getId() == R.id.teachers_spinner_layout) {
            this.view = view;
        } else {
            if (view.getId() == R.id.welcome_choice_layout_5) {
                Log.e(TAG, "User type: "+ (isStudent ? "Student" : "teacher"));
                if (isStudent) {
                    //user student
                    view.findViewById(R.id.teachers_spinner_layout).setVisibility(View.GONE);
                    this.view = view.findViewById(R.id.section_layout_include_id);
                } else {
                    //user teacher
                    view.findViewById(R.id.section_layout_include_id).setVisibility(View.GONE);
                    this.view = view.findViewById(R.id.teachers_spinner_layout);
                }
            } else {
                this.view = view.findViewById(R.id.campus_layout_include_id);
            }
        }
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

    public int getClassDataCode() {
        return classDataCode;
    }


    public String getTeachersInitial() {
        return teachersInitial;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {

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
        if (isStudent) {
            classDataCode = new DataChecker(context).classChecker(section, level, term);
        } else {
            classDataCode = new DataChecker(context).teacherChecker(teachersInitial);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
