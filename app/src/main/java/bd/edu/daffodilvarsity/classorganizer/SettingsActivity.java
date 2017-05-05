package bd.edu.daffodilvarsity.classorganizer;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.polaric.colorful.ColorPickerPreference;
import org.polaric.colorful.Colorful;
import org.polaric.colorful.ColorfulActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class SettingsActivity extends ColorfulActivity {

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
            case android.R.id.home: onBackPressed();
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        PrefManager prefManager = new PrefManager(getApplication());
        //  Refreshing data on screen by restarting activity, because nothing else seems to work for now
        if (prefManager.getReCreate()) {
            MainActivity.getInstance().finish();
            Intent intent = new Intent(getApplication(), MainActivity.class);
            Log.e("ONPAUSE", "CALLED");
            startActivity(intent);
            prefManager.saveReCreate(false);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.main_settings);

            //Getting prefmanager to get existing data
            final PrefManager prefManager = new PrefManager(getActivity());

            //Designing routine preference
            final Preference routinePreference = findPreference("routine_preference");
            final String sectionRoot = prefManager.getSection();
            final int levelRoot = prefManager.getLevel();
            final int termRoot = prefManager.getTerm();
            routinePreference.setSummary("Current Section " + sectionRoot + ", Level " + (levelRoot + 1) + ", Term " + (termRoot + 1));
            routinePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.spinner_layout, null);
                    builder.setTitle("Choose your current class");

                    //Program selection (COMING SOON TODO)
//                    TextView programLabel = (TextView) dialogView.findViewById(R.id.program_spinner_label);
//                    programLabel.setTextColor(getResources().getColor(android.R.color.black));
//
//                    Spinner programText = (Spinner) dialogView.findViewById(R.id.program_selection);
//                    ArrayAdapter<CharSequence> programAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.program_array, R.layout.spinner_row);
//                    programText.setAdapter(programAdapter);

                    //Level spinner
                    TextView levelLabel = (TextView) dialogView.findViewById(R.id.level_spinner_label);
                    levelLabel.setTextColor(getResources().getColor(android.R.color.black));

                    final Spinner levelSpinner = (Spinner) dialogView.findViewById(R.id.level_spinner);
                    // Create an ArrayAdapter using the string array and a default spinner layout
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.level_array, R.layout.spinner_row);
                    // Specify the layout to use when the list of choices appears
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // Apply the adapter to the spinner
                    levelSpinner.setAdapter(adapter);
                    levelSpinner.setSelection(levelRoot);

                    //Term spinner
                    TextView termLabel = (TextView) dialogView.findViewById(R.id.term_spinner_label);
                    termLabel.setTextColor(getResources().getColor(android.R.color.black));

                    final Spinner termSpinner = (Spinner) dialogView.findViewById(R.id.term_spinner);
                    // Create an ArrayAdapter using the string array and a default spinner layout
                    ArrayAdapter<CharSequence> termAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.term_array, R.layout.spinner_row);
                    // Specify the layout to use when the list of choices appears
                    termAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // Apply the adapter to the spinner
                    termSpinner.setAdapter(termAdapter);
                    termSpinner.setSelection(termRoot);

                    //Section selection
                    TextView sectionLabel = (TextView) dialogView.findViewById(R.id.section_spinner_label);
                    sectionLabel.setTextColor(getResources().getColor(android.R.color.black));

                    final Spinner sectionText = (Spinner) dialogView.findViewById(R.id.section_selection);
                    ArrayAdapter<CharSequence> sectionAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.section_array, R.layout.spinner_row);
                    sectionText.setAdapter(sectionAdapter);
                    String[] sectionListString = getResources().getStringArray(R.array.section_array);
                    ArrayList<String> sectionList = new ArrayList<>(Arrays.asList(sectionListString));
                    int sectionPosition = -1;
                    for (int i = 0; i < sectionList.size(); i++) {
                        if (sectionList.get(i).equalsIgnoreCase(sectionRoot)) {
                            sectionPosition = i;
                        }
                    }
                    sectionText.setSelection(sectionPosition);

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int level = levelSpinner.getSelectedItemPosition();
                            int term = termSpinner.getSelectedItemPosition();
                            String section = sectionText.getSelectedItem().toString();
                            RoutineLoader newRoutine = new RoutineLoader(level, term, section, getActivity());

                            boolean loadCheck = newRoutine.loadRoutine();
                            if (!loadCheck) {
                                prefManager.saveLevel(level);
                                prefManager.saveTerm(term);
                                prefManager.saveSection(section);
                                routinePreference.setSummary("Current Section " + section + ", Level " + (level + 1) + ", Term " + (term + 1));
                                prefManager.saveReCreate(true);
                                onCreate(Bundle.EMPTY);
                                //SHOWING SNACKBAR
                                showSnackBar(getActivity(), "Saved");
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
    }

    //Method to display snackbar properly
    public static void showSnackBar(Activity activity, String message) {
        View rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
    }
}
