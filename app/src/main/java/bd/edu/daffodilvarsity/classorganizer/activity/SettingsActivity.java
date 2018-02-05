package bd.edu.daffodilvarsity.classorganizer.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.polaric.colorful.ColorPickerPreference;
import org.polaric.colorful.Colorful;
import org.polaric.colorful.ColorfulActivity;

import java.util.ArrayList;

import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.data.DayData;
import bd.edu.daffodilvarsity.classorganizer.utils.AlarmHelper;
import bd.edu.daffodilvarsity.classorganizer.utils.CourseUtils;
import bd.edu.daffodilvarsity.classorganizer.utils.DataChecker;
import bd.edu.daffodilvarsity.classorganizer.utils.PrefManager;
import bd.edu.daffodilvarsity.classorganizer.utils.RoutineLoader;
import bd.edu.daffodilvarsity.classorganizer.utils.SpinnerHelperCampus;
import bd.edu.daffodilvarsity.classorganizer.utils.SpinnerHelperClass;
import bd.edu.daffodilvarsity.classorganizer.utils.SpinnerHelperUser;

public class SettingsActivity extends ColorfulActivity {

    private static final String TAG = "SettingsActivity";

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
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        // Making navigation bar colored
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, Colorful.getThemeDelegate().getPrimaryColor().getColorRes()));
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

    public static class SettingsFragment extends PreferenceFragmentCompat {
        //Getting prefmanager to get existing data
        PrefManager prefManager;
        private SpinnerHelperClass classHelper;
        private SpinnerHelperCampus campusHelper;
        private PreferenceManager preferenceManager;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.main_settings);
            preferenceManager = getPreferenceManager();
            prefManager = new PrefManager(getActivity());
            if (prefManager.hasCampusSettingsChanged()) {
                if (prefManager.isUserStudent()) {
                    resetLevelTermSection();
                } else {
                    resetInitial();
                }
                prefManager.saveModifiedData(null, PrefManager.ADD_DATA_TAG, true);
                prefManager.saveModifiedData(null, PrefManager.DELETE_DATA_TAG, true);
                prefManager.saveModifiedData(null, PrefManager.SAVE_DATA_TAG, true);
                prefManager.saveModifiedData(null, PrefManager.EDIT_DATA_TAG, true);
                prefManager.setHasCampusSettingsChanged(false);
            }
            //Set user type
            userSettings();
            //Setting department preference
            departmentSettings();
            //Designing class preference
            classSettings();
            //Reset preference
            resetSettings();
            //Limit Modification
            limitSettings();
            //Display ramadan time
            ramadanSettings();
            //Notifications
            notificationSettings();

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

        private void userSettings() {
            final Preference userPreference = findPreference("user_preference");
            String user;
            if (prefManager.isUserStudent()) {
                user = "Student";
            } else {
                user = "Teacher";
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                userPreference.setSummary(Html.fromHtml(getString(R.string.user_preference_summary, user.toUpperCase()), Html.FROM_HTML_MODE_LEGACY));
            } else {
                userPreference.setSummary(Html.fromHtml(getString(R.string.user_preference_summary, user.toUpperCase())));
            }
            userPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (!prefManager.isCampusChangeAlertDisabled()) {
                        MaterialDialog.Builder alertBuilder = new MaterialDialog.Builder(getActivity());
                        alertBuilder.title(R.string.warning);
                        alertBuilder.content(R.string.warning_msg);
                        alertBuilder.positiveText(R.string.proceed);
                        alertBuilder.negativeText(getString(android.R.string.cancel));
                        alertBuilder.checkBoxPrompt(getResources().getString(R.string.dont_show_this_again), false, new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                prefManager.setIsCampusChangeAlertDisabled(isChecked);
                            }
                        });
                        alertBuilder.onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                                showUserChangeDialogue();
                            }
                        });
                        MaterialDialog materialDialog = alertBuilder.build();
                        materialDialog.show();
                    } else {
                        showUserChangeDialogue();
                    }
                    return true;
                }
            });
        }

        private void showUserChangeDialogue() {
            MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
            builder.title(R.string.user_popup_title);
            final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.user_spinner_layout, getListView(), false);
            //Designing the spinners TODO
            final SpinnerHelperUser spinnerHelperUser = new SpinnerHelperUser(getContext(), dialogView);
            spinnerHelperUser.setupUser();
            spinnerHelperUser.setUserTypeLabelBlack();
            final boolean oldUser = prefManager.isUserStudent();
            builder.positiveText(android.R.string.ok);
            builder.onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                    boolean newUser = spinnerHelperUser.isStudent();
                    if (oldUser == newUser) {
                        showSnackBar(getActivity(), getResources().getString(R.string.no_changes));
                    } else {
                        prefManager.setUserType(newUser);
                        prefManager.setHasCampusSettingsChanged(true);
                        prefManager.saveReCreate(true);
                        onCreate(Bundle.EMPTY);
                        showSnackBar(getActivity(), getResources().getString(R.string.user_settings_changed));
                    }
                }
            });
            builder.negativeText(android.R.string.cancel);
            builder.customView(dialogView, true);
            MaterialDialog dialog = builder.build();
            dialog.show();
        }


        //Reset methods for the reset option
        public void resetLevelTermSection() {
            prefManager.saveLevel(0);
            prefManager.saveTerm(0);
            prefManager.saveSection(CourseUtils.getInstance(getContext()).getSections(prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram()).get(0));
            RoutineLoader routineLoader = new RoutineLoader(prefManager.getLevel(), prefManager.getTerm(), prefManager.getSection(), getContext(), prefManager.getDept(), prefManager.getCampus(), prefManager.getProgram());
            ArrayList<DayData> resetData = routineLoader.loadRoutine(false);
            prefManager.saveDayData(resetData);
        }

        public void resetInitial() {
            prefManager.saveTeacherInitial(CourseUtils.getInstance(getContext()).getTeachersInitials(prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram()).get(0));
            RoutineLoader routineLoader = new RoutineLoader(prefManager.getTeacherInitial(), prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram(), getContext());
            ArrayList<DayData> resetData = routineLoader.loadRoutine(false);
            prefManager.saveDayData(resetData);
        }


        private void showCampusChangeDialogue() {
            MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
            builder.title(R.string.dept_popup_title);
            final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.campus_spinner_layout, getListView(), false);
            //Designing the spinners
            campusHelper = new SpinnerHelperCampus(getContext(), dialogView, R.layout.spinner_row, prefManager.isUserStudent());
            campusHelper.setupCampus();
            campusHelper.setupCampusLabelBlack();
            final String oldDept = prefManager.getDept();
            final String oldCampus = prefManager.getCampus();
            final String oldProgram = prefManager.getProgram();
            campusHelper.setCampusSpinnerPositions(oldCampus, oldDept, oldProgram);
            builder.positiveText(android.R.string.ok);
            builder.onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                    String campus = campusHelper.getCampus().toLowerCase();
                    String department = campusHelper.getDept().toLowerCase();
                    String program = campusHelper.getProgram().toLowerCase();
                    DataChecker campusChecker = new DataChecker(getContext());
                    int campusCode;
                    if (prefManager.isUserStudent()) {
                        campusCode = campusChecker.campusChecker(campus, department, program, prefManager.isUserStudent());
                    } else {
                        campusCode = campusChecker.campusTeacherChecker(campus, department, program);
                    }
                    if (oldDept.equalsIgnoreCase(campus) && oldCampus.equalsIgnoreCase(department) && oldProgram.equalsIgnoreCase(program)) {
                        showSnackBar(getActivity(), getResources().getString(R.string.no_changes));
                    } else if (campusCode == 0) {
                        prefManager.saveCampus(campus);
                        prefManager.saveDept(department);
                        prefManager.saveProgram(program);
                        prefManager.setHasCampusSettingsChanged(true);
                        prefManager.saveReCreate(true);
                        onCreate(Bundle.EMPTY);
                        showSnackBar(getActivity(), getResources().getString(R.string.dept_settings_changed));
                    } else {
                        DataChecker.errorMessage(getActivity(), campusCode, null);
                    }
                }
            });
            builder.negativeText(android.R.string.cancel);
            builder.customView(dialogView, true);
            builder.autoDismiss(false);
            MaterialDialog dialog = builder.build();
            dialog.show();
        }

        //Setting selection index for the reminder popup
        private int delayIndex() {
            int delay = prefManager.getReminderDelay();
            if (delay == 15) {
                return 0;
            } else if (delay == 30) {
                return 1;
            } else if (delay == 45) {
                return 2;
            } else {
                return 3;
            }
        }

        private void departmentSettings() {
            final Preference deptPreference = findPreference("dept_preference");
            final String dept = prefManager.getDept();
            final String program = prefManager.getProgram();
            final String campus = prefManager.getCampus();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                deptPreference.setSummary(Html.fromHtml(getString(R.string.department_preference_summary, dept.toUpperCase(), program.substring(0, 1).toUpperCase() + program.substring(1, program.length()).toLowerCase(), campus.substring(0, 1).toUpperCase() + campus.substring(1, campus.length()).toLowerCase()), Html.FROM_HTML_MODE_LEGACY));
            } else {
                deptPreference.setSummary(Html.fromHtml(getString(R.string.department_preference_summary, dept.toUpperCase(), program.substring(0, 1).toUpperCase() + program.substring(1, program.length()).toLowerCase(), campus.substring(0, 1).toUpperCase() + campus.substring(1, campus.length()).toLowerCase())));
            }
            deptPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (!prefManager.isCampusChangeAlertDisabled()) {
                        MaterialDialog.Builder alertBuilder = new MaterialDialog.Builder(getActivity());
                        alertBuilder.title(R.string.warning);
                        alertBuilder.content(R.string.warning_msg);
                        alertBuilder.positiveText(R.string.proceed);
                        alertBuilder.negativeText(getString(android.R.string.cancel));
                        alertBuilder.checkBoxPrompt(getResources().getString(R.string.dont_show_this_again), false, new CompoundButton.OnCheckedChangeListener() {
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
        }

        private void classSettings() {

            if (prefManager.isUserStudent()) {
                studentClassSettings();
            } else {
                teacherClassSettings();
            }

        }

        private void studentClassSettings() {
            final Preference routinePreference = findPreference("routine_preference");
            final String sectionRoot = prefManager.getSection();
            final int levelRoot = prefManager.getLevel();
            final int termRoot = prefManager.getTerm();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                routinePreference.setSummary(Html.fromHtml(getString(R.string.routine_preference_summary_student, sectionRoot, (levelRoot + 1), (termRoot + 1)), Html.FROM_HTML_MODE_LEGACY));
            } else {
                routinePreference.setSummary(Html.fromHtml(getString(R.string.routine_preference_summary_student, sectionRoot, (levelRoot + 1), (termRoot + 1))));
            }
            routinePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Choose Your New Class");
                    //Setting up spinners for class settings
                    View dialogView = getActivity().getLayoutInflater().inflate(R.layout.student_spinner_layout, getListView(), false);
                    classHelper = new SpinnerHelperClass(getContext(), dialogView, R.layout.spinner_row, true);
                    classHelper.setupClassLabelBlack();
                    classHelper.setupClass(prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram());
                    classHelper.setClassSpinnerPositions(levelRoot, termRoot, sectionRoot);

                    builder.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    builder.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setView(dialogView);
                    final AlertDialog dialog = builder.create();
                    dialog.show();
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int level = classHelper.getLevel();
                            int term = classHelper.getTerm();
                            String section = classHelper.getSection();
                            RoutineLoader newRoutine = new RoutineLoader(level, term, section, getActivity(), prefManager.getDept(), prefManager.getCampus(), prefManager.getProgram());
                            DataChecker classChecker = new DataChecker(getContext());
                            int classCode = classChecker.classChecker(section, level, term);
                            if (classCode == 0) {
                                prefManager.saveLevel(level);
                                prefManager.saveTerm(term);
                                prefManager.saveSection(section);
                                prefManager.saveReCreate(true);
                                prefManager.saveDayData(newRoutine.loadRoutine(true));
                                onCreate(Bundle.EMPTY);
                                showSnackBar(getActivity(), getResources().getString(R.string.saved));
                                dialog.dismiss();
                            } else {
                                DataChecker.formattedError(getActivity(), classCode,
                                        prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram(),
                                        level, term, section);
                            }
                        }
                    });
                    return true;
                }
            });
        }

        private void teacherClassSettings() {
            final Preference routinePreference = findPreference("routine_preference");
            final String teacherInitialRoot = prefManager.getTeacherInitial();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                routinePreference.setSummary(Html.fromHtml(getString(R.string.routine_preference_summary_teacher, teacherInitialRoot), Html.FROM_HTML_MODE_LEGACY));
            } else {
                routinePreference.setSummary(Html.fromHtml(getString(R.string.routine_preference_summary_teacher, teacherInitialRoot)));
            }
            routinePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Choose Your New Initial");
                    //Setting up spinners for class settings
                    View dialogView = getActivity().getLayoutInflater().inflate(R.layout.teacher_spinner_layout, getListView(), false);
                    classHelper = new SpinnerHelperClass(getContext(), dialogView, R.layout.spinner_row, false);
                    classHelper.createTeacherInitSpinners();
                    classHelper.createTeacherInitAdapter(prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram());
                    classHelper.attachTeacherInitAdapter();
                    classHelper.setTeacherSpinnerPosition();
                    classHelper.setupTeacherLabelBlack();

                    builder.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String teacherInitial = classHelper.getTeachersInitial();
                            RoutineLoader newRoutine = new RoutineLoader(teacherInitial, prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram(), getContext());
                            DataChecker classChecker = new DataChecker(getContext());
                            int classCode = classChecker.teacherChecker(teacherInitial);
                            if (classCode == 0) {
                                prefManager.saveTeacherInitial(teacherInitial);
                                prefManager.saveReCreate(true);
                                prefManager.saveDayData(newRoutine.loadRoutine(true));
                                onCreate(Bundle.EMPTY);
                                showSnackBar(getActivity(), getResources().getString(R.string.saved));
                                dialog.dismiss();
                            } else {
                                DataChecker.errorMessage(getActivity(), classCode, null);
                            }
                        }
                    });

                    builder.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
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

        private void resetSettings() {
            Preference resetPreference = findPreference("reset_preference");
            resetPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {
                    AlertDialog.Builder multiChoiceBuilder = new AlertDialog.Builder(getActivity());
                    multiChoiceBuilder.setTitle(R.string.reset_title);
                    final CharSequence[] items = {getResources().getString(R.string.deleted), getResources().getString(R.string.edited), getResources().getString(R.string.added), getResources().getString(R.string.saved_nonexc)};
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
                    multiChoiceBuilder.setPositiveButton(R.string.reset, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!(selectedItems.contains(0) || selectedItems.contains(1) || selectedItems.contains(2) || selectedItems.contains(3))) {
                                showSnackBar(getActivity(), "No items were selected");
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle("Are you sure?");
                                builder.setMessage("Your modified classes will be removed. Do you want to continue?");
                                builder.setPositiveButton(getString(android.R.string.yes), new DialogInterface.OnClickListener() {
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

                                builder.setNegativeButton(getString(android.R.string.no), new DialogInterface.OnClickListener() {
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
                    multiChoiceBuilder.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
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
        }

        private void limitSettings() {
            final SwitchPreferenceCompat limitPreference = (SwitchPreferenceCompat) findPreference("limit_preference");
            if (preferenceManager.getSharedPreferences().getBoolean("limit_preference", true)) {
                limitPreference.setSummary(getString(R.string.limit_preference_enabled_summary));
            } else {
                limitPreference.setSummary(getString(R.string.limit_preference_disabled_summary));
            }
            if (!prefManager.isUserStudent()) {
                if (preferenceManager.getSharedPreferences().getBoolean("limit_preference", true)) {
                    limitPreference.setChecked(false);
                    RoutineLoader routineLoader = RoutineLoader.newInstance(getContext());
                    ArrayList<DayData> newDayData = routineLoader.loadRoutine(true);
                    prefManager.saveDayData(newDayData);
                }
                if (limitPreference.isEnabled()) {
                    limitPreference.setEnabled(false);
                }
            } else {
                if (!limitPreference.isEnabled()) {
                    limitPreference.setEnabled(true);
                }
                limitPreference.setChecked(preferenceManager.getSharedPreferences().getBoolean("limit_preference", true));
                limitPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        boolean isLimited = (boolean) newValue;
                        RoutineLoader routineLoader = RoutineLoader.newInstance(getContext());
                        ArrayList<DayData> newDayData = routineLoader.loadRoutine(true);
                        prefManager.saveDayData(newDayData);
                        if (isLimited) {
                            limitPreference.setSummary(getString(R.string.limit_preference_enabled_summary));
                        } else {
                            limitPreference.setSummary(getString(R.string.limit_preference_disabled_summary));
                        }
                        prefManager.saveReCreate(true);
                        return true;
                    }
                });
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
            ramadanPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean hasRamadanTime = (boolean) newValue;
                    if (hasRamadanTime) {
                        ramadanPreference.setSummary(getString(R.string.ramadan_enabled_summary));
                    } else {
                        ramadanPreference.setSummary(getString(R.string.ramadan_disabled_summary));
                    }
                    boolean isNotificationEnabled = preferenceManager.getSharedPreferences().getBoolean("notification_preference", true);
                    if (isNotificationEnabled) {
                        AlarmHelper alarmHelper = new AlarmHelper(getContext());
                        alarmHelper.forceRestart(hasRamadanTime);
                    }
                    prefManager.saveReCreate(true);
                    return true;
                }
            });
        }

        private void notificationSettings() {
            final AlarmHelper alarmHelper = new AlarmHelper(getContext());
            final SwitchPreferenceCompat notification = (SwitchPreferenceCompat) findPreference("notification_preference");
            boolean isNotificationEnabled = preferenceManager.getSharedPreferences().getBoolean("notification_preference", true);
            notification.setChecked(isNotificationEnabled);
            if (isNotificationEnabled) {
                notification.setSummary(getString(R.string.notification_enabled_summary));
            } else {
                notification.setSummary(R.string.notification_disabled_summary);
            }

            final Preference timeDelay = findPreference("notification_delay_preference");
            timeDelay.setEnabled(isNotificationEnabled);
            timeDelay.setSummary(getString(R.string.time_delay_summary, timeText()));
            timeDelay.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {
                    new MaterialDialog.Builder(getActivity())
                            .title("Send Notification Before")
                            .items(R.array.time_delay)
                            .alwaysCallSingleChoiceCallback()
                            .itemsCallbackSingleChoice(delayIndex(), new MaterialDialog.ListCallbackSingleChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                                    if (i == 0) {
                                        prefManager.saveReminderDelay(15);
                                    } else if (i == 1) {
                                        prefManager.saveReminderDelay(30);
                                    } else if (i == 2) {
                                        prefManager.saveReminderDelay(45);
                                    } else {
                                        prefManager.saveReminderDelay(60);
                                    }
                                    timeDelay.setSummary(getString(R.string.time_delay_summary, timeText()));
                                    alarmHelper.cancelAll();
                                    alarmHelper.startAll();
                                    return true;
                                }
                            })
                            .negativeText(getString(android.R.string.cancel))
                            .build()
                            .show();
                    return true;
                }
            });

            notification.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean isEnabled = (boolean) newValue;
                    if (isEnabled) {
                        alarmHelper.cancelAll();
                        alarmHelper.startAll();
                        notification.setSummary(getString(R.string.notification_enabled_summary));
                    } else {
                        alarmHelper.cancelAll();
                        notification.setSummary(R.string.notification_disabled_summary);
                    }
                    timeDelay.setEnabled(isEnabled);
                    return true;
                }
            });
        }

        private String timeText() {
            if (prefManager.getReminderDelay() == 60) {
                return  getResources().getString(R.string.one_hour);
            } else {
                return prefManager.getReminderDelay()+" minutes";
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
