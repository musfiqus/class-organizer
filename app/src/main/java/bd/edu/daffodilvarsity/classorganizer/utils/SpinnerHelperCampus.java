package bd.edu.daffodilvarsity.classorganizer.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
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

    private AppCompatCheckBox multiProgramCheck;

    private String campus;
    private String dept;
    private String program;

    private int campusDataCode;


    public SpinnerHelperCampus(Context context, View view, int spinnerRowResource, boolean isStudent) {
        viewChooser(view);
        this.context = context;
        this.prefManager = new PrefManager(context);
        this.spinnerRowResource = spinnerRowResource;
        this.isStudent = isStudent;
    }

    private void viewChooser(View view) {
        if (view.getId() == R.id.campus_spinner_layout_id) {
            this.view = view;
        } else {
            this.view = view.findViewById(R.id.campus_layout_include_id);
        }
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    //* Google: setsupportbuttontintlist can only be called
    //* Stackoverflow: https://stackoverflow.com/a/44926919
    ///////////////////////////////////////////////////////////////////////////////////////////
    @SuppressLint("RestrictedApi")
    public void setupCampusLabelBlack() {
        TextView campusLabel = (TextView) view.findViewById(R.id.campus_spinner_label);
        TextView deptLabel = (TextView) view.findViewById(R.id.dept_spinner_label);
        TextView programLabel = (TextView) view.findViewById(R.id.program_spinner_label);
        campusLabel.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        deptLabel.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        programLabel.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        if (!isStudent && multiProgramCheck != null) {
            Log.e(TAG, "setupCampusLabelBlack: Noice");
            ///////////////////////////////////////////////////////////////////////////////////////////
            //* Google: change checkbox theme programmatically
            //* Stackoverflow: https://stackoverflow.com/a/40212769
            ///////////////////////////////////////////////////////////////////////////////////////////
            ColorStateList colorStateList = new ColorStateList(
                    new int[][]{
                            new int[]{-android.R.attr.state_checked}, // unchecked
                            new int[]{android.R.attr.state_checked}, // checked
                    },
                    new int[]{
                            ContextCompat.getColor(view.getContext(), android.R.color.black),
                            ContextCompat.getColor(view.getContext(), android.R.color.black),
                    }
            );

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                multiProgramCheck.setButtonTintList(colorStateList);
            } else {
                multiProgramCheck.setSupportButtonTintList(colorStateList);
            }
            multiProgramCheck.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        } else {
            if (multiProgramCheck == null) {
                Log.e(TAG, "setupCampusLabelBlack: NELZ");
            }

        }
    }

    public void setupCampus() {
        createCampusSpinners();
        setupCampusAdapters();
        multiProgramCheck = view.findViewById(R.id.teacher_multi_program);
        if (multiProgramCheck != null) {
            if (isStudent) {
                multiProgramCheck.setVisibility(View.GONE);
            } else {
                multiProgramCheck.setVisibility(View.VISIBLE);
                if (prefManager != null) {
                    multiProgramCheck.setChecked(prefManager.isMultiProgram());
                }
                multiProgramCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        prefManager.setMultiProgram(isChecked);
                    }
                });
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
        campusDataCode = new DataChecker(context).campusChecker(campus, dept, program, isStudent);
        Log.e(TAG, "CampusCode: " + campusDataCode);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public int getCampusDataCode() {
        return campusDataCode;
    }

    public void setUserType(boolean isStudent) {
        this.isStudent = isStudent;
    }

}
