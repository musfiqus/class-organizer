package bd.edu.daffodilvarsity.classorganizer.ui.settings;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.text.HtmlCompat;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.SwitchPreferenceCompat;

import com.afollestad.materialdialogs.MaterialDialog;

import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.ui.setup.SetupActivity;
import bd.edu.daffodilvarsity.classorganizer.utils.PrefManager;
import bd.edu.daffodilvarsity.classorganizer.utils.PreferenceGetter;

import static android.app.Activity.RESULT_OK;

public class SettingsFragment extends PreferenceFragmentCompat {
    private static final int SETUP_REQUEST_CODE = 6969;
    private String currentRoutine;

    //Getting prefmanager to get existing data
    PrefManager prefManager;
    private PreferenceManager preferenceManager;
    private SettingsViewModel mViewModel;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.main_settings);
        mViewModel = ViewModelProviders.of(getActivity()).get(SettingsViewModel.class);
        preferenceManager = getPreferenceManager();

        setupRoutinePreference();
        setupSemesterPreference();


        //Notifications
        notificationSettings();



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

        //Displaying database version in routine section
        ///////////////////////////////////////////////////////////////////////////////////////////
        //* Google:
        //* Stackoverflow: https://stackoverflow.com/a/11189583
        ///////////////////////////////////////////////////////////////////////////////////////////
        Preference dbVersion = findPreference("current_database_version_preference");
        dbVersion.setSummary(Integer.toString(PreferenceGetter.getDatabaseVersion()));



    }

    private void setupSemesterPreference() {
        final Preference semesterPreference = findPreference("current_semester_preference");
        mViewModel.getSemesterNameListener().observe(getActivity(), s -> {
            if (s != null) {
                semesterPreference.setSummary(s);
            }
        });
    }

    private void setupRoutinePreference() {
        final Preference routinePreference = findPreference("routine_preference");
        mViewModel.getRoutineChangeListener().observe(getActivity(), s -> {
            if (s != null) {
                routinePreference.setSummary(HtmlCompat.fromHtml(s, HtmlCompat.FROM_HTML_MODE_COMPACT));
                if (currentRoutine != null && getActivity() != null){
                    getActivity().setResult(RESULT_OK);
                }
                currentRoutine = s;
            }
        });

        routinePreference.setOnPreferenceClickListener(preference -> {
            startActivityForResult(new Intent(getContext(), SetupActivity.class), SETUP_REQUEST_CODE);
            return true;
        });
    }


    //Setting selection index for the reminder popup
    private int delayIndex() {
        int delay = PreferenceGetter.getNotificationDelay();
        if (delay == 15) {
            return 0;
        } else if (delay == 30) {
            return 1;
        } else if (delay == 45) {
            return 2;
        } else if (delay == 60){
            return 3;
        } else {
            return 4;
        }
    }

    private void ramadanSettings() {
        final SwitchPreferenceCompat ramadanPreference = (SwitchPreferenceCompat) findPreference("ramadan_preference");
        boolean isRamadanTime = preferenceManager.getSharedPreferences().getBoolean("ramadan_preference", false);
        ramadanPreference.setChecked(isRamadanTime);
        if (isRamadanTime) {
            ramadanPreference.setSummary(getString(R.string.ramadan_enabled_summary));
        } else {
            ramadanPreference.setSummary(getString(R.string.ramadan_disabled_summary));
        }
        ramadanPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean hasRamadanTime = (boolean) newValue;
            if (hasRamadanTime) {
                ramadanPreference.setSummary(getString(R.string.ramadan_enabled_summary));
            } else {
                ramadanPreference.setSummary(getString(R.string.ramadan_disabled_summary));
            }
            boolean isNotificationEnabled = preferenceManager.getSharedPreferences().getBoolean("notification_preference", true);
            if (isNotificationEnabled) {
                mViewModel.restartAlarms();
            }
            prefManager.saveReCreate(true);
            return true;
        });
    }

    private void notificationSettings() {

        final SwitchPreferenceCompat notification = (SwitchPreferenceCompat) findPreference("notification_preference");
        boolean isNotificationEnabled = preferenceManager.getSharedPreferences().getBoolean("notification_preference", true);
        notification.setChecked(isNotificationEnabled);
        if (isNotificationEnabled) {
            notification.setSummary(getString(R.string.notification_enabled_summary));
        } else {
            notification.setSummary(R.string.notification_disabled_summary);
        }

        final Preference timeDelay = findPreference("notification_delay_preference");
        timeDelay.setVisible(isNotificationEnabled);
        timeDelay.setSummary(HtmlCompat.fromHtml(getString(R.string.time_delay_summary, timeText()), HtmlCompat.FROM_HTML_MODE_COMPACT));
        timeDelay.setOnPreferenceClickListener(preference -> {
            new MaterialDialog.Builder(getActivity())
                    .title("Send Notification Before")
                    .items(R.array.time_delay)
                    .alwaysCallSingleChoiceCallback()
                    .itemsCallbackSingleChoice(delayIndex(), (materialDialog, view, i, charSequence) -> {
                        if (i == 0) {
                            PreferenceGetter.setNotificationDelay(15);
                        } else if (i == 1) {
                            PreferenceGetter.setNotificationDelay(30);
                        } else if (i == 2) {
                            PreferenceGetter.setNotificationDelay(45);
                        } else if (i == 3){
                            PreferenceGetter.setNotificationDelay(60);
                        } else {
                            PreferenceGetter.setNotificationDelay(120);
                        }
                        timeDelay.setSummary(HtmlCompat.fromHtml(getString(R.string.time_delay_summary, timeText()), HtmlCompat.FROM_HTML_MODE_COMPACT));
                        mViewModel.restartAlarms();
                        return true;
                    })
                    .negativeText(getString(android.R.string.cancel))
                    .build()
                    .show();
            return true;
        });

        notification.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean isEnabled = (boolean) newValue;
            if (isEnabled) {
                mViewModel.startAlarms();
                notification.setSummary(getString(R.string.notification_enabled_summary));
            } else {
                mViewModel.cancelAlarms();
                notification.setSummary(R.string.notification_disabled_summary);
            }
            timeDelay.setVisible(isEnabled);
            return true;
        });
    }

    private String timeText() {
        if (PreferenceGetter.getNotificationDelay() == 60) {
            return getResources().getString(R.string.one_hour);
        } else if (PreferenceGetter.getNotificationDelay() == 120) {
            return getResources().getString(R.string.two_hours);
        } else {
            return getString(R.string.delay_minutes, PreferenceGetter.getNotificationDelay());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETUP_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                mViewModel.refreshRoutineSettings();
            }
        }
    }
}
