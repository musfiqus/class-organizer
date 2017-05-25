package bd.edu.daffodilvarsity.classorganizer.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.Arrays;

import bd.edu.daffodilvarsity.classorganizer.data.DayData;
import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.activity.EditActivity;
import bd.edu.daffodilvarsity.classorganizer.utils.PrefManager;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

public class DayDataDetailFragment extends Fragment {
    private DayData mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DayDataDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey("DayDataDetails")) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            Bundle bundle = getArguments();
            mItem = bundle.getParcelable("DayDataDetails");

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.getCourseCode());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.daydata_detail, container, false);
        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            FabSpeedDial fabSpeedDial = (FabSpeedDial) rootView.findViewById(R.id.fab_speed_dial);
            fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
                @Override
                public boolean onMenuItemSelected(MenuItem menuItem) {
                    final PrefManager prefManager = new PrefManager(rootView.getContext());
                    if (menuItem.getItemId() == R.id.edit_class) {
                        Intent intent = new Intent(rootView.getContext(), EditActivity.class);
                        intent.putExtra("DAYDATA", (Parcelable) mItem);
                        rootView.getContext().startActivity(intent);
                        return true;
                    } else if (menuItem.getItemId() == R.id.save_class) {

                        MaterialDialog.Builder builder = new MaterialDialog.Builder(rootView.getContext());
                        builder.title("Save to");
                        View dialogView = LayoutInflater.from(rootView.getContext()).inflate(R.layout.class_spinner_layout, null);
                        TextView levelLabel = (TextView) dialogView.findViewById(R.id.level_spinner_label);
                        levelLabel.setTextColor(dialogView.getResources().getColor(android.R.color.black));
                        TextView termLabel = (TextView) dialogView.findViewById(R.id.term_spinner_label);
                        termLabel.setTextColor(dialogView.getResources().getColor(android.R.color.black));
                        TextView sectionLabel = (TextView) dialogView.findViewById(R.id.section_spinner_label);
                        sectionLabel.setTextColor(dialogView.getResources().getColor(android.R.color.black));
                        final Spinner sectionSpinner = (Spinner) dialogView.findViewById(R.id.section_selection);
                        final Spinner levelSpinner = (Spinner) dialogView.findViewById(R.id.level_spinner);
                        final Spinner termSpinner = (Spinner) dialogView.findViewById(R.id.term_spinner);
                        ArrayAdapter<CharSequence> termAdapter = ArrayAdapter.createFromResource(rootView.getContext(), R.array.term_array, R.layout.spinner_row);
                        termAdapter.setDropDownViewResource(R.layout.spinner_row);
                        termSpinner.setAdapter(termAdapter);
                        termSpinner.setSelection(prefManager.getTerm());

                        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(rootView.getContext(), 0);
                        int sectionPosition = -1;

                        if (prefManager.getCampus().equalsIgnoreCase("main")) {
                            if (prefManager.getDept().equalsIgnoreCase("cse")) {
                                if (prefManager.getProgram().equalsIgnoreCase("day")) {
                                    adapter = ArrayAdapter.createFromResource(rootView.getContext(), R.array.cse_main_day_level_array, R.layout.spinner_row);
                                } else if (prefManager.getProgram().equalsIgnoreCase("eve")) {
                                    adapter = ArrayAdapter.createFromResource(rootView.getContext(), R.array.cse_main_day_level_array, R.layout.spinner_row);
                                }
                                ArrayAdapter<CharSequence> sectionAdapter = ArrayAdapter.createFromResource(rootView.getContext(), R.array.cse_main_day_section_array, R.layout.spinner_row);
                                sectionSpinner.setAdapter(sectionAdapter);
                                String[] sectionListString = dialogView.getResources().getStringArray(R.array.cse_main_day_section_array);
                                ArrayList<String> sectionList = new ArrayList<>(Arrays.asList(sectionListString));
                                for (int i = 0; i < sectionList.size(); i++) {
                                    if (sectionList.get(i).equalsIgnoreCase(prefManager.getSection())) {
                                        sectionPosition = i;
                                    }
                                }
                                sectionSpinner.setSelection(sectionPosition);
                            }
                        } else if (prefManager.getCampus().equalsIgnoreCase("perm")) {
                            if (prefManager.getDept().equalsIgnoreCase("cse")) {
                                if (prefManager.getProgram().equalsIgnoreCase("eve")) {
                                    ArrayAdapter<CharSequence> sectionAdapter = ArrayAdapter.createFromResource(rootView.getContext(), R.array.cse_perm_section_array, R.layout.spinner_row);
                                    sectionSpinner.setAdapter(sectionAdapter);
                                    String[] sectionListString = dialogView.getResources().getStringArray(R.array.cse_perm_section_array);
                                    ArrayList<String> sectionList = new ArrayList<>(Arrays.asList(sectionListString));
                                    for (int i = 0; i < sectionList.size(); i++) {
                                        if (sectionList.get(i).equalsIgnoreCase(prefManager.getSection())) {
                                            sectionPosition = i;
                                        }
                                    }
                                    sectionSpinner.setSelection(sectionPosition);
                                }
                            }
                        }
                        adapter.setDropDownViewResource(R.layout.spinner_row);
                        levelSpinner.setAdapter(adapter);
                        levelSpinner.setSelection(prefManager.getLevel());
                        builder.customView(dialogView, true);
                        builder.positiveText("SAVE");
                        builder.negativeText("CANCEL");
                        builder.onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                                DayData toSave = new DayData(mItem.getCourseCode(), mItem.getTeachersInitial(), sectionSpinner.getSelectedItem().toString(), levelSpinner.getSelectedItemPosition(), termSpinner.getSelectedItemPosition(), mItem.getRoomNo(), mItem.getTime(), mItem.getDay(), mItem.getTimeWeight(), null);
                                prefManager.saveModifiedData(toSave, PrefManager.SAVE_DATA_TAG, false);
                                Snackbar.make(container, toSave.getCourseCode() + " saved!", Snackbar.LENGTH_SHORT).show();
                            }
                        });
                        MaterialDialog dialog = builder.build();
                        dialog.show();
                        return true;
                    } else if (menuItem.getItemId() == R.id.delete_class) {
                        //Show confirmation
                        AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());

                        builder.setTitle("Confirm deletion");
                        builder.setMessage("Are you sure?");
                        final ArrayList<DayData> dayDatas = prefManager.getSavedDayData();
                        int position = -1;
                        for (int i = 0; i < dayDatas.size(); i++) {
                            if (mItem.equals(dayDatas.get(i))) {
                                position = i;
                            }
                        }
                        final int finalPosition = position;
                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {

                                if (finalPosition > -1) {
                                    prefManager.saveModifiedData(dayDatas.get(finalPosition), PrefManager.DELETE_DATA_TAG, false);
                                    dayDatas.remove(finalPosition);
                                }
                                prefManager.saveDayData(dayDatas);
                                prefManager.saveSnackData("Deleted");
                                prefManager.saveShowSnack(true);
                                prefManager.saveReCreate(true);
                                dialog.dismiss();
                                getActivity().onBackPressed();
                            }
                        });

                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // Do nothing
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                        return true;
                    }
                    return false;
                }
            });
            String courseTitle = mItem.getCourseTitle();
            if (courseTitle == null) {
                ((TextView) rootView.findViewById(R.id.course_title_tv)).setText("N/A");
            } else {
                ((TextView) rootView.findViewById(R.id.course_title_tv)).setText(courseTitle);
            }
            ((TextView) rootView.findViewById(R.id.teachers_initial_tv)).setText(mItem.getTeachersInitial());
            ((TextView) rootView.findViewById(R.id.weekday_tv)).setText(mItem.getDay());
            ((TextView) rootView.findViewById(R.id.room_no_tv)).setText(mItem.getRoomNo());
            ((TextView) rootView.findViewById(R.id.time_tv)).setText(mItem.getTime());
        }
        return rootView;
    }
}
