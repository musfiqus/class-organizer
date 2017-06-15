package bd.edu.daffodilvarsity.classorganizer.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;

import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.activity.EditActivity;
import bd.edu.daffodilvarsity.classorganizer.adapter.DayDataAdapter;
import bd.edu.daffodilvarsity.classorganizer.data.DayData;
import bd.edu.daffodilvarsity.classorganizer.utils.DataChecker;
import bd.edu.daffodilvarsity.classorganizer.utils.PrefManager;
import bd.edu.daffodilvarsity.classorganizer.utils.SpinnerHelper;
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
                        intent.putExtra("DAYDETAIL", true);
                        rootView.getContext().startActivity(intent);
                        getActivity().finish();
                        return true;
                    } else if (menuItem.getItemId() == R.id.save_class) {

                        MaterialDialog.Builder builder = new MaterialDialog.Builder(rootView.getContext());
                        builder.title("Save to");
                        View dialogView = LayoutInflater.from(rootView.getContext()).inflate(R.layout.class_spinner_layout, null);
                        final SpinnerHelper classHelper = new SpinnerHelper(getContext(), dialogView, R.layout.spinner_row);
                        classHelper.setupClassLabelBlack();
                        classHelper.setupClass(prefManager.getCampus());
                        if (DataChecker.isMain(prefManager.getCampus())) {
                            classHelper.setClassSpinnerPositions(prefManager.getLevel(), prefManager.getTerm(), classHelper.spinnerPositionGenerator(R.array.cse_main_day_section_array, prefManager.getSection()));
                        } else {
                            classHelper.setClassSpinnerPositions(prefManager.getLevel(), prefManager.getTerm(), classHelper.spinnerPositionGenerator(R.array.cse_perm_section_array, prefManager.getSection()));
                        }
                        builder.customView(dialogView, true);
                        builder.positiveText("SAVE");
                        builder.negativeText(android.R.string.cancel);
                        builder.onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                                DayData toSave = new DayData(mItem.getCourseCode(), mItem.getTeachersInitial(), classHelper.getSection(), classHelper.getLevel(), classHelper.getTerm(), mItem.getRoomNo(), mItem.getTime(), mItem.getDay(), mItem.getTimeWeight(), mItem.getCourseTitle());
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
                        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

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

                        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
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
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(rootView.getContext());
            boolean isRamadan = preferences.getBoolean("ramadan_preference", false);
            if (isRamadan) {
                ((TextView) rootView.findViewById(R.id.time_tv)).setText(DayDataAdapter.DayDataHolder.convertToRamadanTime(mItem.getTime(), mItem.getTimeWeight()));
            } else {
                ((TextView) rootView.findViewById(R.id.time_tv)).setText(mItem.getTime());
            }
        }
        return rootView;
    }
}
