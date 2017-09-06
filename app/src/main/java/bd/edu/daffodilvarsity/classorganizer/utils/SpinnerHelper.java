package bd.edu.daffodilvarsity.classorganizer.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;
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

public class SpinnerHelper {


    private Context context;
    private View view;
    private int spinnerRowResource;
    private AdapterView.OnItemSelectedListener onItemSelectedListener = null;

    private Spinner campusSpinner;
    private Spinner deptSpinner;
    private Spinner programSpinner;
    private Spinner levelSpinner;
    private Spinner termSpinner;
    private Spinner sectionSpinner;

    public SpinnerHelper(Context context, View view, int spinnerRowResource, AdapterView.OnItemSelectedListener onItemSelectedListener) {
        this.context = context;
        this.spinnerRowResource = spinnerRowResource;
        viewChooser(view);
        this.onItemSelectedListener = onItemSelectedListener;
    }


    public SpinnerHelper(Context context, View view, int spinnerRowResource) {
        this.context = context;
        viewChooser(view);
        this.spinnerRowResource = spinnerRowResource;
    }

    private void viewChooser(View view) {
        if (view.getId() == R.id.class_spinner_layout_id || view.getId() == R.id.campus_spinner_layout_id) {
            this.view = view;
        } else {
            if (view.getId() == R.id.welcome_choice_layout_3) {
                this.view = view.findViewById(R.id.section_layout_include_id);
            } else {
                this.view = view.findViewById(R.id.campus_layout_include_id);
            }
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
        setupCampusSpinners();
        setupCampusAdapters();
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
        if (onItemSelectedListener != null) {
            levelSpinner.setOnItemSelectedListener(onItemSelectedListener);
            termSpinner.setOnItemSelectedListener(onItemSelectedListener);
            sectionSpinner.setOnItemSelectedListener(onItemSelectedListener);
        }
    }

    public void sectionAdapter(String campus, String department, String program) {
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
                sectionSpinner.setOnItemSelectedListener(onItemSelectedListener);
            }
        }
    }

    public void setupCampusSpinners() {
        campusSpinner = (Spinner) view.findViewById(R.id.campus_selection);
        deptSpinner = (Spinner) view.findViewById(R.id.dept_selection);
        programSpinner = (Spinner) view.findViewById(R.id.program_selection);
    }

    public void setupCampusAdapters() {
        ArrayAdapter<String> campusAdapter = new ArrayAdapter<>(context, spinnerRowResource, CourseUtils.getInstance(context).getSpinnerList(CourseUtils.GET_CAMPUS));
        ArrayAdapter<String> deptAdapter = new ArrayAdapter<>(context, spinnerRowResource, CourseUtils.getInstance(context).getSpinnerList(CourseUtils.GET_DEPARTMENT));
        ArrayAdapter<CharSequence> programAdapter = ArrayAdapter.createFromResource(context, R.array.programs, spinnerRowResource);
        campusSpinner.setAdapter(campusAdapter);
        deptSpinner.setAdapter(deptAdapter);
        programSpinner.setAdapter(programAdapter);
        if (onItemSelectedListener != null) {
            campusSpinner.setOnItemSelectedListener(onItemSelectedListener);
            deptSpinner.setOnItemSelectedListener(onItemSelectedListener);
            programSpinner.setOnItemSelectedListener(onItemSelectedListener);
        }
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
        if (campusSpinner == null) {
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
        if (sectionSpinner == null) {
            return null;
        }
        return sectionSpinner.getSelectedItem().toString();
    }
}
