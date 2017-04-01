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
        PrefManager prefManager = new PrefManager(context);
        prefManager.saveSection(section);
        prefManager.saveTerm(term);
        prefManager.saveLevel(level);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //loading semester on button press
    public void loadSemester() {
        RoutineLoader routineLoader = new RoutineLoader(level, term, section, context);
        this.tempLock = routineLoader.loadRoutine();
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

