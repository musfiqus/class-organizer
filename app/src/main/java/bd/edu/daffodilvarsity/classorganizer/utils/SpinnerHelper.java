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

    public void setupClass(String campus) {
        setupClassSpinners();
        setupClassAdapters(campus);
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

    public void setupClassAdapters(String campus) {
        ArrayAdapter<CharSequence>  levelAdapter, termAdapter;
        levelAdapter = ArrayAdapter.createFromResource(context, R.array.cse_main_day_level_array, spinnerRowResource);
        termAdapter = ArrayAdapter.createFromResource(context, R.array.term_array, spinnerRowResource);
        levelSpinner.setAdapter(levelAdapter);
        termSpinner.setAdapter(termAdapter);
        sectionAdapter(campus);
        if (onItemSelectedListener != null) {
            levelSpinner.setOnItemSelectedListener(onItemSelectedListener);
            termSpinner.setOnItemSelectedListener(onItemSelectedListener);
            sectionSpinner.setOnItemSelectedListener(onItemSelectedListener);
        }
    }

    public void sectionAdapter(String campus) {
        if (sectionSpinner != null) {
            ArrayAdapter<CharSequence> sectionAdapter;
            if (DataChecker.isMain(campus)) {
                sectionAdapter = ArrayAdapter.createFromResource(context, R.array.cse_main_day_section_array, spinnerRowResource);
            } else {
                sectionAdapter =  ArrayAdapter.createFromResource(context, R.array.cse_perm_section_array, spinnerRowResource);
            }
            sectionSpinner.setAdapter(sectionAdapter);
            sectionSpinner.setOnItemSelectedListener(onItemSelectedListener);
        }
    }

    public void setupCampusSpinners() {
        campusSpinner = (Spinner) view.findViewById(R.id.campus_selection);
        deptSpinner = (Spinner) view.findViewById(R.id.dept_selection);
        programSpinner = (Spinner) view.findViewById(R.id.program_selection);
    }

    public void setupCampusAdapters() {
        ArrayAdapter<CharSequence> campusAdapter = ArrayAdapter.createFromResource(context, R.array.campuses, spinnerRowResource);
        ArrayAdapter<CharSequence> deptAdapter = ArrayAdapter.createFromResource(context, R.array.departments, spinnerRowResource);
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
        if (DataChecker.isPermanent(string)) {
            string = "permanent";
        }
        if (DataChecker.isEvening(string)) {
            string = "evening";
        }
        ArrayList<String> sectionList = new ArrayList<>(Arrays.asList(context.getResources().getStringArray(id)));
        for (int i = 0; i < sectionList.size(); i++) {
            if (sectionList.get(i).toLowerCase().equalsIgnoreCase(string)) {
                return i;
            }
        }
        return 0;
    }

    public void setClassSpinnerPositions(int level, int term, int section) {
        setLevelSpinnerPosition(level);
        setTermSpinnerPosition(term);
        setSectionSpinnerPosition(section);
    }

    public void setCampusSpinnerPositions(int campus, int dept, int program) {
        setCampusSpinnerPosition(campus);
        setDeptSpinnerPosition(dept);
        setProgramSpinnerPosition(program);
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
        if (deptSpinner == null) {
            return null;
        }
        return deptSpinner.getSelectedItem().toString();
    }

    public String getProgram() {
        if (programSpinner == null) {
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
