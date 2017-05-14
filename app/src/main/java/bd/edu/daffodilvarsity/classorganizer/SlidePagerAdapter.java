package bd.edu.daffodilvarsity.classorganizer;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

/**
 * Created by musfiqus on 3/26/2017.
 */

public class SlidePagerAdapter extends PagerAdapter implements AdapterView.OnItemSelectedListener {
    private LayoutInflater layoutInflater;
    private Context context;
    private int[] layouts;
    private int level;
    private int term;
    private String section;
    private View view;
    private boolean tempLock = true;
    private PrefManager prefManager;

    private Spinner campusSpinner;
    private ArrayAdapter<CharSequence> campusAdapter;
    private Spinner deptSpinner;
    private ArrayAdapter<CharSequence> deptAdapter;
    private Spinner programSpinner;
    private ArrayAdapter<CharSequence> programAdapter;
    Spinner levelSpinner;
    ArrayAdapter<CharSequence> levelAdapter;
    Spinner termSpinner;
    ArrayAdapter<CharSequence> termAdapter;
    Spinner sectionSpinner;
    ArrayAdapter<CharSequence> sectionAdapter;

    public SlidePagerAdapter(Context context, int[] layouts) {
        this.context = context;
        this.layouts = layouts;
        this.prefManager = new PrefManager(context);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = layoutInflater.inflate(layouts[position], container, false);
        container.addView(view);
        if (layouts[position] == R.layout.welcome_slide2) {
            campusSpinnerSetup();
        }
        if (layouts[position] == R.layout.welcome_slide3) {

            classSpinnerSetup();
        }
        return view;
    }

    private void campusSpinnerSetup() {
        //Campus spinner
        campusSpinner = (Spinner) view.findViewById(R.id.campus_selection);
        campusAdapter = ArrayAdapter.createFromResource(context, R.array.campuses, R.layout.spinner_campus_row_welcome);
        campusAdapter.setDropDownViewResource(R.layout.spinner_campus_row_welcome);
        campusSpinner.setAdapter(campusAdapter);
        campusSpinner.setOnItemSelectedListener(this);

        //Department spinner
        deptSpinner = (Spinner) view.findViewById(R.id.dept_selection);
        setDeptSpinnerAdapter();

        //Program selection
        programSpinner = (Spinner) view.findViewById(R.id.program_selection);
        setProgramAdapter();
    }

    private void setDeptSpinnerAdapter() {
        if (campusSpinner.getSelectedItem().toString().equalsIgnoreCase("main")) {
            deptAdapter = ArrayAdapter.createFromResource(context, R.array.main_departments, R.layout.spinner_campus_row_welcome);
            deptAdapter.setDropDownViewResource(R.layout.spinner_campus_row_welcome);
            deptAdapter.notifyDataSetChanged();
            deptSpinner.setAdapter(deptAdapter);
            deptSpinner.setOnItemSelectedListener(this);
        } else {
            deptAdapter = ArrayAdapter.createFromResource(context, R.array.permanent_departments, R.layout.spinner_campus_row_welcome);
            deptAdapter.setDropDownViewResource(R.layout.spinner_campus_row_welcome);
            deptAdapter.notifyDataSetChanged();
            deptSpinner.setAdapter(deptAdapter);
            deptSpinner.setOnItemSelectedListener(this);
        }
    }

    private void setProgramAdapter() {
        if (campusSpinner.getSelectedItem().toString().equalsIgnoreCase("main")) {
            if (deptSpinner.getSelectedItem().toString().equalsIgnoreCase("cse")) {
                programAdapter = ArrayAdapter.createFromResource(context, R.array.cse_main_programs, R.layout.spinner_campus_row_welcome);
                programAdapter.notifyDataSetChanged();
                programSpinner.setAdapter(programAdapter);
                programSpinner.setOnItemSelectedListener(this);
            }
        } else {
            if (deptSpinner.getSelectedItem().toString().equalsIgnoreCase("cse")) {
                programAdapter = ArrayAdapter.createFromResource(context, R.array.cse_perm_programs, R.layout.spinner_campus_row_welcome);
                programAdapter.notifyDataSetChanged();
                programSpinner.setAdapter(programAdapter);
                programSpinner.setOnItemSelectedListener(this);
            }
        }
    }

    private void classSpinnerSetup() {
        //Level spinner
        levelSpinner = (Spinner) view.findViewById(R.id.level_spinner);

        //Term spinner
        termSpinner = (Spinner) view.findViewById(R.id.term_spinner);

        //Section selection
        sectionSpinner = (Spinner) view.findViewById(R.id.section_selection);
        setClassAdapter();
    }

    private boolean isSpinnerNotNull() {
        return (levelAdapter != null && termSpinner != null && sectionSpinner != null);
    }

    private void setClassAdapter() {
        if (campusSpinner.getSelectedItem().toString().equalsIgnoreCase("main") && deptSpinner.getSelectedItem().toString().equalsIgnoreCase("cse") && programSpinner.getSelectedItem().toString().equalsIgnoreCase("day")) {
            // Create an ArrayAdapter using the string array and a default spinner layout
            levelAdapter = ArrayAdapter.createFromResource(context, R.array.level_array, R.layout.spinner_class_row_welcome);
            // Specify the layout to use when the list of choices appears
            levelAdapter.setDropDownViewResource(R.layout.spinner_class_row_welcome);
            levelAdapter.notifyDataSetChanged();
            // Apply the levelAdapter to the spinner
            levelSpinner.setAdapter(levelAdapter);
            levelSpinner.setOnItemSelectedListener(this);

            termAdapter = ArrayAdapter.createFromResource(context, R.array.term_array, R.layout.spinner_class_row_welcome);
            termAdapter.setDropDownViewResource(R.layout.spinner_class_row_welcome);
            termAdapter.notifyDataSetChanged();
            termSpinner.setAdapter(termAdapter);
            termSpinner.setOnItemSelectedListener(this);

            sectionAdapter = ArrayAdapter.createFromResource(context, R.array.section_array, R.layout.spinner_class_row_welcome);
            sectionAdapter.setDropDownViewResource(R.layout.spinner_class_row_welcome);
            sectionAdapter.notifyDataSetChanged();
            sectionSpinner.setAdapter(sectionAdapter);
            sectionSpinner.setOnItemSelectedListener(this);
        }
    }

    @Override
    public int getCount() {
        return layouts.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Saving selections on first launch
        prefManager.saveCampus(campusSpinner.getSelectedItem().toString().substring(0, 4).toLowerCase());
        prefManager.saveDept(deptSpinner.getSelectedItem().toString().toLowerCase());
        prefManager.saveProgram(programSpinner.getSelectedItem().toString().substring(0, 3).toLowerCase());
        if (parent.getId() == R.id.level_spinner) {
            level = parent.getSelectedItemPosition();
        } else if (parent.getId() == R.id.term_spinner) {
            term = parent.getSelectedItemPosition();
        } else if (parent.getId() == R.id.section_selection) {
            section = parent.getSelectedItem().toString().toUpperCase();
        } else if (parent.getId() == R.id.campus_selection) {
            setDeptSpinnerAdapter();
            setProgramAdapter();
            if (isSpinnerNotNull()) {
                setClassAdapter();
            }
        } else if (parent.getId() == R.id.dept_selection) {
            setProgramAdapter();
            if (isSpinnerNotNull()) {
                setClassAdapter();
            }
        } else if (parent.getId() == R.id.program_selection) {
            if (isSpinnerNotNull()) {
                setClassAdapter();
            }
        }
        //Saving selections
        prefManager.saveSection(section);
        prefManager.saveTerm(term);
        prefManager.saveLevel(level);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //loading semester on button press
    public void loadSemester() {
        RoutineLoader routineLoader = new RoutineLoader(level, term, section, context, prefManager.getDept(), prefManager.getCampus(), prefManager.getProgram());
        ArrayList<DayData> loadedRoutine = routineLoader.loadRoutine(false);
        if (loadedRoutine != null) {
            if (loadedRoutine.size() > 0) {
                prefManager.saveDayData(loadedRoutine);
                this.tempLock = false;
            } else {
                this.tempLock = true;
            }
        }
    }

    public boolean isTempLock() {
        return tempLock;
    }


    public String getSection() {
        return section;
    }

    public int getLevel() {
        return level;
    }

    public int getTerm() {
        return term;
    }

    public View getView() {
        return view;
    }
}

