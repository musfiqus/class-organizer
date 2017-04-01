package bd.edu.daffodilvarsity.classorganizer;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
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

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.main_settings);

            final PrefManager prefManager = new PrefManager(getActivity());

            //Designing routine preference
            final Preference routinePreference = findPreference("routine_preference");
            final String sectionRoot = prefManager.getSection();
            final int levelRoot = prefManager.getLevel();
            final int termRoot = prefManager.getTerm();

            routinePreference.setSummary("Current Section " + sectionRoot + ", Level " + (levelRoot + 1) + ", Term " + (termRoot + 1));
            routinePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.spinner_layout, null);
                    builder.setTitle("Choose your current class");

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
                            ArrayList<DayData> previousData = prefManager.getSavedDayData();
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
                                onCreate(Bundle.EMPTY);
                                prefManager.saveReCreate(true);

                                //SHOWING SNACKBAR
                                showSnackBar(getActivity(), "Saved");
                                dialog.dismiss();
                            } else {
                                Toast.makeText(getActivity(), "Section " + section + " currently doesn't exist on level " + (level + 1) + " term " + (term + 1) + ". Please select the correct level, term & section. Or contact the developer to add your section.", Toast.LENGTH_LONG).show();
                                prefManager.saveDayData(previousData);
                                onCreate(Bundle.EMPTY);
                                dialog.dismiss();
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
        }

        //Method to display snackbar properly
        public void showSnackBar(Activity activity, String message) {
            View rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
            Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
        }
    }
}
