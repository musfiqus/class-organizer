package bd.edu.daffodilvarsity.classorganizer.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import bd.edu.daffodilvarsity.classorganizer.R;

/**
 * Created by Mushfiqus Salehin on 6/11/2017.
 * musfiqus@gmail.com
 */

public class SpinnerHelper {
    public static final String CAMPUS_MAIN = "main";
    public static final String CAMPUS_PERMANENT = "perm";
    public static final String PROGRAM_DAY = "day";
    public static final String PROGRAM_EVENING = "eve";
    public static final String DEPARTMENT_CSE = "cse";

    private Context context;
    private View view;
    private int spinnerRowResource;

    private Spinner sectionSpinner;
    private Spinner levelSpinner;
    private Spinner termSpinner;

    public SpinnerHelper(Context context, View view, int spinnerRowResource) {
        this.context = context;
        this.spinnerRowResource = spinnerRowResource;
        if (view.getId() == R.id.class_spinner_layout_id) {
            this.view = view;
            log("Normal choice");
        } else {
            if (view.getId() == R.id.welcome_choice_layout_3) {
                this.view = view.findViewById(R.id.section_layout_include_id);
                log("Welcome choice 3");
            } else {
                this.view = view.findViewById(R.id.campus_layout_include_id);
                log("Welcome choice 2");
            }

        }
    }

    public SpinnerHelper(Context context) {
        this.context = context;
    }


    public static boolean isEvening(String program) {
        if (program.length() < 3) {
            return false;
        }
        return PROGRAM_EVENING.equalsIgnoreCase(program.substring(0, 3));
    }

    public static boolean isDay(String program) {
        if (program.length() < 3) {
            return false;
        }
        return PROGRAM_DAY.equalsIgnoreCase(program.substring(0, 3));
    }

    public static boolean isMain(String campus) {
        if (campus.length() < 4) {
            return false;
        }
        return CAMPUS_MAIN.equalsIgnoreCase(campus.substring(0, 4));
    }

    public static boolean isPermanent(String campus) {
        if (campus.length() < 4) {
            return false;
        }
        return CAMPUS_PERMANENT.equalsIgnoreCase(campus.substring(0, 4));
    }

    public static boolean isCSE(String department) {
        return DEPARTMENT_CSE.equalsIgnoreCase(department);
    }

    public ArrayList<Spinner> getCampusSetupSpinners(View view) {
        ArrayList<Spinner> spinners = new ArrayList<>();
        spinners.add((Spinner) view.findViewById(R.id.campus_selection));
        spinners.add((Spinner) view.findViewById(R.id.dept_selection));
        spinners.add((Spinner) view.findViewById(R.id.program_selection));
        return spinners;
    }

    public ArrayList<Spinner> getSectionSetupSpinners(View view) {
        ArrayList<Spinner> spinners = new ArrayList<>();
        spinners.add((Spinner) view.findViewById(R.id.section_selection));
        spinners.add((Spinner) view.findViewById(R.id.level_spinner));
        spinners.add((Spinner) view.findViewById(R.id.term_spinner));
        return spinners;
    }

    public ArrayList<ArrayAdapter<CharSequence>> getCampusSetupAdapters(int spinnerRowResource, String campus, String dept) {
        ArrayList<ArrayAdapter<CharSequence>> adapters = new ArrayList<>();
        adapters.add(ArrayAdapter.createFromResource(context, R.array.campuses, spinnerRowResource));
        if (isMain(campus)) {
            adapters.add(ArrayAdapter.createFromResource(context, R.array.main_departments, spinnerRowResource));
            if (isCSE(dept)) {
                adapters.add(ArrayAdapter.createFromResource(context, R.array.cse_main_programs, spinnerRowResource));
            }
        } else {
            adapters.add(ArrayAdapter.createFromResource(context, R.array.permanent_departments, spinnerRowResource));
            if (isCSE(dept)) {
                adapters.add(ArrayAdapter.createFromResource(context, R.array.cse_perm_programs, spinnerRowResource));
            }
        }
        return adapters;
    }

    public ArrayList<ArrayAdapter<CharSequence>> getSectionSetupAdapters(int spinnerRowResource, String campus, String dept, String program) {
        ArrayList<ArrayAdapter<CharSequence>> adapters = new ArrayList<>();
        if (isMain(campus)) {
            adapters.add(ArrayAdapter.createFromResource(context, R.array.cse_main_day_section_array, spinnerRowResource));
            if (isCSE(dept)) {
                if (isDay(program)) {
                    adapters.add(ArrayAdapter.createFromResource(context, R.array.cse_main_day_level_array, spinnerRowResource));
                } else {
                    adapters.add(ArrayAdapter.createFromResource(context, R.array.cse_main_eve_level_array, spinnerRowResource));
                }
            }
        } else {
            adapters.add(ArrayAdapter.createFromResource(context, R.array.cse_perm_section_array, spinnerRowResource));
            if (isCSE(dept)) {
                if (isDay(program)) {
                    adapters.add(ArrayAdapter.createFromResource(context, R.array.cse_main_day_level_array, spinnerRowResource));
                }
            }
        }
        adapters.add(ArrayAdapter.createFromResource(context, R.array.term_array, spinnerRowResource));
        return adapters;
    }

    public void setupClassLabelBlack() {
        TextView levelLabel = (TextView) view.findViewById(R.id.level_spinner_label);
        levelLabel.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        TextView termLabel = (TextView) view.findViewById(R.id.term_spinner_label);
        termLabel.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        TextView sectionLabel = (TextView) view.findViewById(R.id.section_spinner_label);
        sectionLabel.setTextColor(ContextCompat.getColor(context, android.R.color.black));
    }

    public void setupClassSpinners() {
        sectionSpinner = (Spinner) view.findViewById(R.id.section_selection);
        levelSpinner = (Spinner) view.findViewById(R.id.level_spinner);
        termSpinner = (Spinner) view.findViewById(R.id.term_spinner);
    }

    public void setupClassAdapters(String campus, String dept, String program) {
        ArrayAdapter<CharSequence> sectionAdapter, levelAdapter, termAdapter;
        if (isMain(campus)) {
            sectionAdapter = ArrayAdapter.createFromResource(context, R.array.cse_main_day_section_array, spinnerRowResource);
            if (isCSE(dept)) {
                if (isDay(program)) {
                    levelAdapter = ArrayAdapter.createFromResource(context, R.array.cse_main_day_level_array, spinnerRowResource);
                } else {
                    levelAdapter = ArrayAdapter.createFromResource(context, R.array.cse_main_eve_level_array, spinnerRowResource);
                }
            } else {
                //Placeholder
                levelAdapter = ArrayAdapter.createFromResource(context, R.array.cse_main_day_level_array, spinnerRowResource);
            }
        } else {
            sectionAdapter = ArrayAdapter.createFromResource(context, R.array.cse_perm_section_array, spinnerRowResource);
            if (isCSE(dept)) {
                if (isDay(program)) {
                    levelAdapter = ArrayAdapter.createFromResource(context, R.array.cse_main_day_level_array, spinnerRowResource);
                } else {
                    //Placeholder
                    levelAdapter = ArrayAdapter.createFromResource(context, R.array.cse_main_day_level_array, spinnerRowResource);
                }
            } else {
                //Placeholder
                levelAdapter = ArrayAdapter.createFromResource(context, R.array.cse_main_day_level_array, spinnerRowResource);
            }
        }
        termAdapter = ArrayAdapter.createFromResource(context, R.array.term_array, spinnerRowResource);
        sectionSpinner.setAdapter(sectionAdapter);
        levelSpinner.setAdapter(levelAdapter);

    }

    private void log(String message) {
        Log.e("SpinnerHelper", message);
    }
}
