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

import bd.edu.daffodilvarsity.classorganizer.R;

/**
 * Created by Mushfiqus Salehin on 6/11/2017.
 * musfiqus@gmail.com
 */

public class SpinnerHelperCampus implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "SpinnerHelperCampus";

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

    private String campus;
    private String dept;
    private String program;

    private int campusDataCode;

    private boolean isCampusMode;


    public SpinnerHelperCampus(Context context, View view, int spinnerRowResource, boolean isStudent, boolean isCampusMode) {
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

    public void setupCampusLabelBlack() {
        TextView campusLabel = (TextView) view.findViewById(R.id.campus_spinner_label);
        TextView deptLabel = (TextView) view.findViewById(R.id.dept_spinner_label);
        TextView programLabel = (TextView) view.findViewById(R.id.program_spinner_label);
        campusLabel.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        deptLabel.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        programLabel.setTextColor(ContextCompat.getColor(context, android.R.color.black));
    }

    public void setupCampus() {
        createCampusSpinners();
        setupCampusAdapters();
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

    public void setCampusSpinnerPositions(String campus, String dept, String program) {
        ArrayList<String> campuses = CourseUtils.getInstance(context).getSpinnerList(CourseUtils.GET_CAMPUS);
        ArrayList<String> departments = CourseUtils.getInstance(context).getSpinnerList(CourseUtils.GET_DEPARTMENT);
        setCampusSpinnerPosition(spinnerPositionGenerator(campuses, campus));
        setDeptSpinnerPosition(spinnerPositionGenerator(departments, dept));
        setProgramSpinnerPosition(spinnerPositionGenerator(R.array.programs, program));
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
        return campusSpinner.getSelectedItem().toString().toLowerCase();
    }

    public String getDept() {
        if (deptSpinner == null || deptSpinner.getSelectedItem() == null) {
            return null;
        }
        return deptSpinner.getSelectedItem().toString().toLowerCase();
    }

    public String getProgram() {
        if (programSpinner == null || programSpinner.getSelectedItem() == null) {
            return null;
        }
        return programSpinner.getSelectedItem().toString().toLowerCase();
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
        campusDataCode = new DataChecker(context).campusChecker(campus, dept, program);
        Log.e(TAG, "CampusCode: "+campusDataCode);
        Log.e(TAG, "Kampus: "+campus+" Dept:"+dept+" Program:"+program);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public int getCampusDataCode() {
        return campusDataCode;
    }

}
