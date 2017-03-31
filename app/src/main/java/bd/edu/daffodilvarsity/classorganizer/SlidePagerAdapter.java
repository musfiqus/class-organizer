package bd.edu.daffodilvarsity.classorganizer;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by musfiqus on 3/26/2017.
 */

public class SlidePagerAdapter extends PagerAdapter implements AdapterView.OnItemSelectedListener {
    ArrayList<String> courseCodes;
    private LayoutInflater layoutInflater;
    private Context context;
    private int[] layouts;
    private int level;
    private int term;
    private int semester;
    private String section;
    private boolean tempLock = true;

    public SlidePagerAdapter(Context context, int[] layouts) {
        this.context = context;
        this.layouts = layouts;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(layouts[position], container, false);
        container.addView(view);
        if (layouts[position] == R.layout.welcome_slide2) {
            //Level spinner
            Spinner levelSpinner = (Spinner) view.findViewById(R.id.level_spinner);
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.level_array, R.layout.spinner_row);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            levelSpinner.setAdapter(adapter);
            levelSpinner.setOnItemSelectedListener(this);

            //Term spinner
            Spinner termSpinner = (Spinner) view.findViewById(R.id.term_spinner);
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> termAdapter = ArrayAdapter.createFromResource(context, R.array.term_array, R.layout.spinner_row);
            // Specify the layout to use when the list of choices appears
            termAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            termSpinner.setAdapter(termAdapter);
            termSpinner.setOnItemSelectedListener(this);

            //Section selection
            Spinner sectionText = (Spinner) view.findViewById(R.id.section_selection);
            ArrayAdapter<CharSequence> sectionAdapter = ArrayAdapter.createFromResource(context, R.array.section_array, R.layout.spinner_row);
            sectionText.setAdapter(sectionAdapter);
            sectionText.setOnItemSelectedListener(this);
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
        if (parent.getId() == R.id.level_spinner) {
            level = parent.getSelectedItemPosition();
        } else if (parent.getId() == R.id.term_spinner) {
            term = parent.getSelectedItemPosition();
        } else if (parent.getId() == R.id.section_selection) {
            section = parent.getSelectedItem().toString().toUpperCase();
        } else {
            Log.e("Spinner", "Invalid selection");
        }
        setSemester();
//        courseCodeGenerator(semester);
//        loadRoutine(courseCodes, section);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void setSemester() {
        if (this.level == 0) {
            semester = 1 + this.term;
        } else if (this.level == 1) {
            semester = 4 + this.term;
        } else if (this.level == 2) {
            semester = 7 + this.term;
        } else {
            semester = 10 + this.term;
        }
    }

    public void courseCodeGenerator(int semester) {
        if (semester == 1) {
            String[] semesterData = context.getResources().getStringArray(R.array.semester_one);
            courseCodes = new ArrayList<>(Arrays.asList(semesterData));
        } else if (semester == 2) {
            String[] semesterData = context.getResources().getStringArray(R.array.semester_two);
            courseCodes = new ArrayList<>(Arrays.asList(semesterData));
        } else if (semester == 3) {
            String[] semesterData = context.getResources().getStringArray(R.array.semester_three);
            courseCodes = new ArrayList<>(Arrays.asList(semesterData));
        } else if (semester == 4) {
            String[] semesterData = context.getResources().getStringArray(R.array.semester_four);
            courseCodes = new ArrayList<>(Arrays.asList(semesterData));
        } else if (semester == 5) {
            String[] semesterData = context.getResources().getStringArray(R.array.semester_five);
            courseCodes = new ArrayList<>(Arrays.asList(semesterData));
        } else if (semester == 6) {
            String[] semesterData = context.getResources().getStringArray(R.array.semester_six);
            courseCodes = new ArrayList<>(Arrays.asList(semesterData));
        } else if (semester == 7) {
            String[] semesterData = context.getResources().getStringArray(R.array.semester_seven);
            courseCodes = new ArrayList<>(Arrays.asList(semesterData));
        } else if (semester == 8) {
            String[] semesterData = context.getResources().getStringArray(R.array.semester_eight);
            courseCodes = new ArrayList<>(Arrays.asList(semesterData));
        } else if (semester == 9) {
            String[] semesterData = context.getResources().getStringArray(R.array.semester_nine);
            courseCodes = new ArrayList<>(Arrays.asList(semesterData));
        } else if (semester == 10) {
            String[] semesterData = context.getResources().getStringArray(R.array.semester_ten);
            courseCodes = new ArrayList<>(Arrays.asList(semesterData));
        } else if (semester == 11) {
            String[] semesterData = context.getResources().getStringArray(R.array.semester_eleven);
            courseCodes = new ArrayList<>(Arrays.asList(semesterData));
        } else if (semester == 12) {
            String[] semesterData = context.getResources().getStringArray(R.array.semester_twelve);
            courseCodes = new ArrayList<>(Arrays.asList(semesterData));
        }
    }

    private void loadRoutine(ArrayList<String> courseCodes, String section) {
        DatabaseHelper db = DatabaseHelper.getInstance(context);
        ArrayList<DayData> mDayData = db.getDayData(courseCodes, section);
        Log.e("Daydata size ", "" + mDayData.size());
        Log.e("TEMPLOCX", "" + this.tempLock);
        this.tempLock = mDayData.size() <= 0;
        Log.e("TEMPLOCX", "" + this.tempLock);
        PrefManager prefManager = new PrefManager(context);
        prefManager.saveDayData(mDayData);
    }

    //loading semester on button press
    public void loadSemester() {
        courseCodeGenerator(semester);
        loadRoutine(courseCodes, section);
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
}

