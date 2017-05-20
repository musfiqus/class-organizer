package bd.edu.daffodilvarsity.classorganizer;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.polaric.colorful.ColorPickerPreference;
import org.polaric.colorful.Colorful;
import org.polaric.colorful.ColorfulActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class SettingsActivity extends ColorfulActivity {

    //Method to display snackbar properly
    public static void showSnackBar(Activity activity, String message) {
        View rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Making navigation bar colored
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(getResources().getColor(Colorful.getThemeDelegate().getPrimaryColor().getColorRes()));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements AdapterView.OnItemSelectedListener {
        //Getting prefmanager to get existing data
        PrefManager prefManager;
        private Spinner campusSpinner;
        private Spinner deptSpinner;
        private Spinner programSpinner;
        private Spinner levelSpinner;
        private Spinner termSpinner;
        private Spinner sectionText;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.main_settings);
            prefManager = new PrefManager(getActivity());
            if (prefManager.hasCampusSettingsChanged()) {
                resetLevelTermSection();
                prefManager.saveModifiedData(null, PrefManager.ADD_DATA_TAG, true);
                prefManager.saveModifiedData(null, PrefManager.DELETE_DATA_TAG, true);
                prefManager.saveModifiedData(null, PrefManager.SAVE_DATA_TAG, true);
                prefManager.saveModifiedData(null, PrefManager.EDIT_DATA_TAG, true);
                prefManager.setHasCampusSettingsChanged(false);
            }
            //Setting department preference
            final Preference deptPreference = findPreference("dept_preference");
            final String dept = prefManager.getDept();
            final String program = prefManager.getProgram();
            final String campus = prefManager.getCampus();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                deptPreference.setSummary(Html.fromHtml("Current Department: <b>" + dept.toUpperCase() + "</b>, Program: <b>" + program.substring(0, 1).toUpperCase() + program.substring(1, program.length()).toLowerCase() + "</b>, Campus: <b>" + campus.substring(0, 1).toUpperCase() + campus.substring(1, campus.length()).toLowerCase() + "</b>", Html.FROM_HTML_MODE_LEGACY));
            } else {
                deptPreference.setSummary(Html.fromHtml("Current Department: <b>" + dept.toUpperCase() + "</b>, Program: <b>" + program.substring(0, 1).toUpperCase() + program.substring(1, program.length()).toLowerCase() + "</b>, Campus: <b>" + campus.substring(0, 1).toUpperCase() + campus.substring(1, campus.length()).toLowerCase() + "</b>"));
            }
            deptPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (!prefManager.isCampusChangeAlertDisabled()) {
                        MaterialDialog.Builder alertBuilder = new MaterialDialog.Builder(getActivity());
                        alertBuilder.title("Warning!");
                        alertBuilder.content("Your saved routine and preferences will be reset upon changing this settings. Do you want to proceed?");
                        alertBuilder.positiveText("PROCEED");
                        alertBuilder.negativeText("CANCEL");
                        alertBuilder.checkBoxPrompt("Don't show this again", false, new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                prefManager.setIsCampusChangeAlertDisabled(isChecked);
                            }
                        });
                        alertBuilder.onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                                showCampusChangeDialogue();
                            }
                        });
                        MaterialDialog materialDialog = alertBuilder.build();
                        materialDialog.show();
                    } else {
                        showCampusChangeDialogue();
                    }
                    return true;
                }
            });

            //Designing routine preference
            final Preference routinePreference = findPreference("routine_preference");
            final String sectionRoot = prefManager.getSection();
            final int levelRoot = prefManager.getLevel();
            final int termRoot = prefManager.getTerm();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                routinePreference.setSummary(Html.fromHtml("Current Section: <b>" + sectionRoot + "</b>, Level: <b>" + (levelRoot + 1) + "</b>, Term: <b>" + (termRoot + 1) + "</b>", Html.FROM_HTML_MODE_LEGACY));
            } else {
                routinePreference.setSummary(Html.fromHtml("Current Section: <b>" + sectionRoot + "</b>, Level: <b>" + (levelRoot + 1) + "</b>, Term: <b>" + (termRoot + 1) + "</b>"));
            }
            routinePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Choose your current class");
                    View dialogView = setupClassSpinners(levelRoot, termRoot, sectionRoot);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int level = levelSpinner.getSelectedItemPosition();
                            int term = termSpinner.getSelectedItemPosition();
                            String section = sectionText.getSelectedItem().toString();
                            RoutineLoader newRoutine = new RoutineLoader(level, term, section, getActivity(), prefManager.getDept(), prefManager.getCampus(), prefManager.getProgram());
                            boolean loadCheck = true;
                            ArrayList<DayData> loadedRoutine = newRoutine.loadRoutine(true);
                            if (loadedRoutine != null) {
                                if (loadedRoutine.size() > 0) {
                                    prefManager.saveDayData(loadedRoutine);
                                    loadCheck = false;
                                } else {
                                    loadCheck = true;
                                }
                            }

                            if (!loadCheck) {
                                prefManager.saveLevel(level);
                                prefManager.saveTerm(term);
                                prefManager.saveSection(section);
                                routinePreference.setSummary("Current Section " + section + ", Level " + (level + 1) + ", Term " + (term + 1));
                                prefManager.saveReCreate(true);
                                onCreate(Bundle.EMPTY);
                                showSnackBar(getActivity(), "Saved!");
                                dialog.dismiss();
                            } else {
                                Toast.makeText(getActivity(), "Section " + section + " currently doesn't exist on level " + (level + 1) + " term " + (term + 1) + ". Please select the correct level, term & section. Or contact the developer to add your section.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Do nothing
                            dialog.dismiss();
                        }
                    });
                    builder.setView(dialogView);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                }
            });

            //Reset preference
            Preference resetPreference = findPreference("reset_preference");
            resetPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {
                    AlertDialog.Builder multiChoiceBuilder = new AlertDialog.Builder(getActivity());
                    multiChoiceBuilder.setTitle("Select Classes To Reset");
                    final CharSequence[] items = {"Deleted", "Edited", "Added", "Saved"};
                    final ArrayList<Integer> selectedItems = new ArrayList<>();
                    multiChoiceBuilder.setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            if (isChecked) {
                                selectedItems.add(which);
                            } else if (selectedItems.contains(which)) {
                                selectedItems.remove(Integer.valueOf(which));
                            }
                        }
                    });
                    multiChoiceBuilder.setPositiveButton("RESET", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!(selectedItems.contains(0) || selectedItems.contains(1) || selectedItems.contains(2) || selectedItems.contains(3))) {
                                showSnackBar(getActivity(), "No items were selected");
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle("Are you sure?");
                                builder.setMessage("Your modified classes will be removed. Do you want to continue?");
                                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        boolean delete = false;
                                        boolean edit = false;
                                        boolean add = false;
                                        boolean save = false;
                                        if (selectedItems.contains(0)) {
                                            delete = true;
                                        }
                                        if (selectedItems.contains(1)) {
                                            edit = true;
                                        }
                                        if (selectedItems.contains(2)) {
                                            add = true;
                                        }
                                        if (selectedItems.contains(3)) {
                                            save = true;
                                        }
                                        prefManager.resetModification(add, edit, save, delete);
                                        prefManager.saveReCreate(true);
                                        onCreate(Bundle.EMPTY);
                                        showSnackBar(getActivity(), "Routine was reset!");
                                        dialog.dismiss();
                                    }
                                });

                                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            }
                            dialog.dismiss();
                        }
                    });
                    multiChoiceBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = multiChoiceBuilder.create();
                    alertDialog.show();
                    return true;
                }
            });

            //Limit Modification
            final SwitchPreferenceCompat switchPreferenceCompat = (SwitchPreferenceCompat) findPreference("limit_preference");
            PreferenceManager preferenceManager = getPreferenceManager();
            if (preferenceManager.getSharedPreferences().getBoolean("limit_preference", true)) {
                switchPreferenceCompat.setSummary("Currently you're modification will show up in only on your modified section");
            } else {
                switchPreferenceCompat.setSummary("Currently you're modification will show up in all sections");
            }
            switchPreferenceCompat.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean isLimited = (boolean) newValue;
                    RoutineLoader routineLoader = new RoutineLoader(prefManager.getLevel(), prefManager.getTerm(), prefManager.getSection(), getContext(), prefManager.getDept(), prefManager.getCampus(), prefManager.getProgram());
                    ArrayList<DayData> newDayData = routineLoader.loadRoutine(true);
                    prefManager.saveDayData(newDayData);
                    if (isLimited) {
                        switchPreferenceCompat.setSummary("Currently you're modification will show up in only on your modified section");
                    } else {
                        switchPreferenceCompat.setSummary("Currently you're modification will show up in all sections");
                    }
                    prefManager.saveReCreate(true);
                    return true;
                }
            });

            //  Changing preview of primary color chooser
            ColorPickerPreference primaryColorPref = (ColorPickerPreference) findPreference("primary");
            primaryColorPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    onCreate(Bundle.EMPTY);
                    return true;
                }
            });

            //  Changing preview of accent color chooser
            ColorPickerPreference accentColorPref = (ColorPickerPreference) findPreference("accent");
            accentColorPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    onCreate(Bundle.EMPTY);
                    return true;
                }
            });

            //Displaying app version in about section
            Preference versionPreference = findPreference("version_preference");
            PackageInfo packageInfo = null;
            try {
                packageInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            if (packageInfo != null) {
                versionPreference.setSummary(packageInfo.versionName);
            }

        }

        private void setupDeptSpinner() {
            String campus = campusSpinner.getSelectedItem().toString();
            if (campus != null) {
                if (campus.equalsIgnoreCase("main")) {
                    ArrayAdapter<CharSequence> deptAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.main_departments, R.layout.spinner_row);
                    deptAdapter.setDropDownViewResource(R.layout.spinner_row);
                    deptAdapter.notifyDataSetChanged();
                    deptSpinner.setAdapter(deptAdapter);
                } else if (campus.equalsIgnoreCase("perm")) {
                    ArrayAdapter<CharSequence> deptAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.permanent_departments, R.layout.spinner_row);
                    deptAdapter.setDropDownViewResource(R.layout.spinner_row);
                    deptAdapter.notifyDataSetChanged();
                    deptSpinner.setAdapter(deptAdapter);
                }
            }
            deptSpinner.setOnItemSelectedListener(this);

        }

        private void setupProgramSpinner() {
            String department = deptSpinner.getSelectedItem().toString();
            String campus = campusSpinner.getSelectedItem().toString().substring(0, 4);
            if (department != null && campus != null) {
                if (department.equalsIgnoreCase("cse") && campus.equalsIgnoreCase("main")) {
                    ArrayAdapter<CharSequence> programAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cse_main_programs, R.layout.spinner_row);
                    programAdapter.setDropDownViewResource(R.layout.spinner_row);
                    programAdapter.notifyDataSetChanged();
                    programSpinner.setAdapter(programAdapter);
                } else if (campus.equalsIgnoreCase("perm")) {
                    ArrayAdapter<CharSequence> programAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cse_perm_programs, R.layout.spinner_row);
                    programAdapter.setDropDownViewResource(R.layout.spinner_row);
                    programAdapter.notifyDataSetChanged();
                    programSpinner.setAdapter(programAdapter);
                }
            }
            programSpinner.setOnItemSelectedListener(this);

        }

        private View setupClassSpinners(int levelRoot, int termRoot, String sectionRoot) {
            View dialogView = getActivity().getLayoutInflater().inflate(R.layout.class_spinner_layout, null);
            TextView levelLabel = (TextView) dialogView.findViewById(R.id.level_spinner_label);
            levelLabel.setTextColor(getResources().getColor(android.R.color.black));
            TextView termLabel = (TextView) dialogView.findViewById(R.id.term_spinner_label);
            termLabel.setTextColor(getResources().getColor(android.R.color.black));
            TextView sectionLabel = (TextView) dialogView.findViewById(R.id.section_spinner_label);
            sectionLabel.setTextColor(getResources().getColor(android.R.color.black));
            levelSpinner = (Spinner) dialogView.findViewById(R.id.level_spinner);
            termSpinner = (Spinner) dialogView.findViewById(R.id.term_spinner);
            sectionText = (Spinner) dialogView.findViewById(R.id.section_selection);
            ArrayAdapter<CharSequence> termAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.term_array, R.layout.spinner_row);
            termAdapter.setDropDownViewResource(R.layout.spinner_row);
            termSpinner.setAdapter(termAdapter);
            termSpinner.setSelection(termRoot);

            String campus = prefManager.getCampus();
            String department = prefManager.getDept();
            String program = prefManager.getProgram();
            if (campus.equalsIgnoreCase("main")) {
                if (department.equalsIgnoreCase("cse")) {
                    if (program.equalsIgnoreCase("day")) {
                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.cse_main_day_level_array, R.layout.spinner_row);
                        adapter.setDropDownViewResource(R.layout.spinner_row);
                        levelSpinner.setAdapter(adapter);
                        levelSpinner.setSelection(levelRoot);
                    } else if (program.equalsIgnoreCase("eve")) {
                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.cse_main_eve_level_array, R.layout.spinner_row);
                        adapter.setDropDownViewResource(R.layout.spinner_row);
                        levelSpinner.setAdapter(adapter);
                        levelSpinner.setSelection(levelRoot);
                    }
                    ArrayAdapter<CharSequence> sectionAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cse_main_day_section_array, R.layout.spinner_row);
                    sectionText.setAdapter(sectionAdapter);
                    String[] sectionListString = getResources().getStringArray(R.array.cse_main_day_section_array);
                    ArrayList<String> sectionList = new ArrayList<>(Arrays.asList(sectionListString));
                    int sectionPosition = -1;
                    for (int i = 0; i < sectionList.size(); i++) {
                        if (sectionList.get(i).equalsIgnoreCase(sectionRoot)) {
                            sectionPosition = i;
                        }
                    }
                    sectionText.setSelection(sectionPosition);
                }
            } else if (campus.equalsIgnoreCase("perm")) {
                if (department.equalsIgnoreCase("cse")) {
                    if (program.equalsIgnoreCase("day")) {
                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.cse_main_day_level_array, R.layout.spinner_row);
                        adapter.setDropDownViewResource(R.layout.spinner_row);
                        levelSpinner.setAdapter(adapter);
                        levelSpinner.setSelection(levelRoot);
                        ArrayAdapter<CharSequence> sectionAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cse_perm_section_array, R.layout.spinner_row);
                        sectionText.setAdapter(sectionAdapter);
                        String[] sectionListString = getResources().getStringArray(R.array.cse_main_day_section_array);
                        ArrayList<String> sectionList = new ArrayList<>(Arrays.asList(sectionListString));
                        int sectionPosition = -1;
                        for (int i = 0; i < sectionList.size(); i++) {
                            if (sectionList.get(i).equalsIgnoreCase(sectionRoot)) {
                                sectionPosition = i;
                            }
                        }
                        sectionText.setSelection(sectionPosition);
                    }
                }
            }
            return dialogView;
        }

        public void resetLevelTermSection() {
            prefManager.saveLevel(0);
            prefManager.saveTerm(0);
            String[] sections;
            if (prefManager.getCampus().equalsIgnoreCase("main")) {
                sections = getResources().getStringArray(R.array.cse_main_day_section_array);
            } else {
                sections = getResources().getStringArray(R.array.cse_perm_section_array);
            }
            prefManager.saveSection(sections[0]);
            RoutineLoader routineLoader = new RoutineLoader(prefManager.getLevel(), prefManager.getTerm(), prefManager.getSection(), getContext(), prefManager.getDept(), prefManager.getCampus(), prefManager.getProgram());
            ArrayList<DayData> resetData = routineLoader.loadRoutine(false);
            prefManager.saveDayData(resetData);
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (parent.getSelectedItem().toString().equalsIgnoreCase("main") || parent.getSelectedItem().toString().equalsIgnoreCase("permanent")) {
                setupDeptSpinner();
                setupProgramSpinner();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

        private void showCampusChangeDialogue() {
            MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
            builder.title("Choose Your Department");
            final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.campus_spinner_layout, getListView(), false);
            //Designing the spinners
            TextView campusLabel = (TextView) dialogView.findViewById(R.id.campus_spinner_label);
            TextView deptLabel = (TextView) dialogView.findViewById(R.id.dept_spinner_label);
            TextView programLabel = (TextView) dialogView.findViewById(R.id.program_spinner_label);
            campusLabel.setTextColor(getResources().getColor(android.R.color.black));
            deptLabel.setTextColor(getResources().getColor(android.R.color.black));
            programLabel.setTextColor(getResources().getColor(android.R.color.black));
            //Spinners
            campusSpinner = (Spinner) dialogView.findViewById(R.id.campus_selection);
            deptSpinner = (Spinner) dialogView.findViewById(R.id.dept_selection);
            programSpinner = (Spinner) dialogView.findViewById(R.id.program_selection);
            ArrayAdapter<CharSequence> campusAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.campuses, R.layout.spinner_row);
            campusAdapter.setDropDownViewResource(R.layout.spinner_row);
            campusSpinner.setAdapter(campusAdapter);
            campusSpinner.setOnItemSelectedListener(this);
            setupDeptSpinner();
            setupProgramSpinner();
            final String oldDept = prefManager.getDept();
            final String oldCampus = prefManager.getCampus();
            final String oldProgram = prefManager.getProgram();
            builder.positiveText("OK");
            builder.onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                    String campus = campusSpinner.getSelectedItem().toString();
                    Log.e("Wut campus", "campus");
                    String department = deptSpinner.getSelectedItem().toString();
                    String program = programSpinner.getSelectedItem().toString();
                    if (campus.equalsIgnoreCase("main")
                            && (department.equalsIgnoreCase("cse"))
                            && (program.equalsIgnoreCase("day") || program.equalsIgnoreCase("evening"))
                            ) {
                        prefManager.saveCampus(campus.toLowerCase().substring(0, 4));
                        prefManager.saveDept(department.toLowerCase());
                        prefManager.saveProgram(program.toLowerCase().substring(0, 3));
                    } else if (campus.equalsIgnoreCase("permanent")) {
                        if (department.equalsIgnoreCase("cse")) {
                            if (program.equalsIgnoreCase("day")) {
                                prefManager.saveCampus(campus.toLowerCase().substring(0, 4));
                                prefManager.saveDept(department.toLowerCase());
                                prefManager.saveProgram(program.toLowerCase().substring(0, 3));
                            }
                        }
                    }
                    if (!oldDept.equalsIgnoreCase(prefManager.getDept()) || !oldCampus.equalsIgnoreCase(prefManager.getCampus()) || !oldProgram.equalsIgnoreCase(prefManager.getProgram())) {
                        prefManager.setHasCampusSettingsChanged(true);
                        prefManager.saveReCreate(true);
                        onCreate(Bundle.EMPTY);
                        showSnackBar(getActivity(), "Department settings changed");
                    } else {
                        showSnackBar(getActivity(), "No changes were made");
                    }
                }
            });
            builder.negativeText("Cancel");
            builder.customView(dialogView, true);
            MaterialDialog dialog = builder.build();
            dialog.show();
        }
    }
}
