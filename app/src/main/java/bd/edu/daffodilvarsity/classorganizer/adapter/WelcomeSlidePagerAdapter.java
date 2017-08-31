package bd.edu.daffodilvarsity.classorganizer.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;

import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.data.DayData;
import bd.edu.daffodilvarsity.classorganizer.utils.DataChecker;
import bd.edu.daffodilvarsity.classorganizer.utils.PrefManager;
import bd.edu.daffodilvarsity.classorganizer.utils.RoutineLoader;
import bd.edu.daffodilvarsity.classorganizer.utils.SpinnerHelper;

/**
 * Created by Mushfiqus Salehin on 3/26/2017.
 * musfiqus@gmail.com
 */

public class WelcomeSlidePagerAdapter extends PagerAdapter implements AdapterView.OnItemSelectedListener {
    private Context context;
    private int[] layouts;
    private String campus;
    private String dept;
    private String program;
    private int level;
    private int term;
    private String section;
    private View view;
    private PrefManager prefManager;
    private int classDataCode;
    private int campusDataCode;
    private int previousCampusSelection = 0;

    private SpinnerHelper classHelper;
    private SpinnerHelper campusHelper;

    public WelcomeSlidePagerAdapter(Context context, int[] layouts) {
        this.context = context;
        this.layouts = layouts;
        this.prefManager = new PrefManager(context);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = layoutInflater.inflate(layouts[position], container, false);
        container.addView(view);
        if (layouts[position] == R.layout.welcome_slide2) {
            campusHelper = new SpinnerHelper(context, view, R.layout.spinner_campus_row_welcome, this);
            campusHelper.setupCampus();
        }
        if (layouts[position] == R.layout.welcome_slide3) {
            classHelper = new SpinnerHelper(context, view, R.layout.spinner_class_row_welcome, this);
            classHelper.setupClass(campus);
        }
        return view;
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
        campus = campusHelper.getCampus();
        dept = campusHelper.getDept();
        program = campusHelper.getProgram();
//        if (campus != null) {
//            campus = campus.substring(0, 4).toLowerCase();
//        }
//        if (dept != null) {
//            dept = dept.toLowerCase();
//        }
//        if (program != null) {
//            program = program.substring(0, 3).toLowerCase();
//        }
        prefManager.saveCampus(campus);
        prefManager.saveDept(dept);
        prefManager.saveProgram(program);
        if (parent.getId() == R.id.level_spinner) {
            level = parent.getSelectedItemPosition();
        } else if (parent.getId() == R.id.term_spinner) {
            term = parent.getSelectedItemPosition();
        } else if (parent.getId() == R.id.section_selection) {
            section = parent.getSelectedItem().toString().toUpperCase();
        } else if (parent.getId() == R.id.campus_selection) {
            if(campus != null && classHelper != null && parent.getSelectedItemPosition() != previousCampusSelection) {
                classHelper.sectionAdapter(campus);
                previousCampusSelection = parent.getSelectedItemPosition();
            }
        }
        campusDataCode = new DataChecker(context, dept, campus, program).dataChecker();
        classDataCode = new DataChecker(context, level, term, section, dept, campus, program).dataChecker();
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
            prefManager.saveDayData(loadedRoutine);
        }
    }

    public String getCampus() {
        return campus;
    }


    public String getSection() {
        return section;
    }

    public String getDept() {
        return dept;
    }

    public String getProgram() {
        return program;
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

    public int getClassDataCode() {
        return classDataCode;
    }

    public int getCampusDataCode() {
        return campusDataCode;
    }
}

