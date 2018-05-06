package bd.edu.daffodilvarsity.classorganizer.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.polaric.colorful.Colorful;
import org.polaric.colorful.ColorfulActivity;

import java.util.ArrayList;

import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.adapter.DayDataAdapter;
import bd.edu.daffodilvarsity.classorganizer.data.DayData;
import bd.edu.daffodilvarsity.classorganizer.receiver.NotificationPublisher;
import bd.edu.daffodilvarsity.classorganizer.utils.AlarmHelper;
import bd.edu.daffodilvarsity.classorganizer.utils.CourseUtils;
import bd.edu.daffodilvarsity.classorganizer.utils.PrefManager;
import bd.edu.daffodilvarsity.classorganizer.utils.SpinnerHelperClass;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

/**
 * An activity representing a single DayData detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
// * in a {@link MainActivity}.
 */
public class DayDataDetailActivity extends ColorfulActivity {
    private DayData dayData;
    private Bundle bundle;
    private boolean fromNotification = false;
    public static final String DAYDATA_DETAIL_TAG = "DayDataDetails";
    private RelativeLayout mMuteContainer;
    private TextView mMuteText;
    private ImageView mMuteYes;
    private ImageView mMuteNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Orientation change hack
        bundle = savedInstanceState;

        setContentView(R.layout.activity_daydata_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setEnterTransition(null);
//            getWindow().setExitTransition(null);
//        }

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState != null) {
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
            appBarLayout.setTitle(savedInstanceState.getCharSequence("AppBarTitle"));

        }
        // Making navigation bar colored
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, Colorful.getThemeDelegate().getPrimaryColor().getColorRes()));
        }
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = getIntent().getExtras();
            if (arguments != null) {
                dayData = arguments.getParcelable(DAYDATA_DETAIL_TAG);
                Bundle bundle = arguments.getBundle(AlarmHelper.TAG_ALARM_BUNDLE_DATA);
                if (bundle != null) {
                    byte[] byteDayData = bundle.getByteArray(NotificationPublisher.TAG_NOTIFICATION_DATA);
                    if (byteDayData != null) {
                        dayData = CourseUtils.convertToDayData(byteDayData);
                        fromNotification = true;
                    }
                }
            }

            loadUi();
//            Bundle newArgs = new Bundle();
//            newArgs.putParcelable("DayDataDetails", dayData);
//            DayDataDetailFragment fragment = new DayDataDetailFragment();
//            fragment.setArguments(newArgs);
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.daydata_detail_container, fragment)
//                    .commit();
        }
    }

    private void loadUi() {
        if (dayData != null) {
            //set toolbar
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(dayData.getCourseCode());
            }
            //mute setup
            mMuteContainer = findViewById(R.id.mute_container);
            mMuteYes = findViewById(R.id.mute_yes);
            mMuteNo = findViewById(R.id.mute_no);
            mMuteText = findViewById(R.id.mute_status_text);
            //set tint
            ImageViewCompat.setImageTintList(mMuteNo, ColorStateList.valueOf(ContextCompat.getColor(this, R.color.detailTitleColor)));
            ImageViewCompat.setImageTintList(mMuteYes, ColorStateList.valueOf(ContextCompat.getColor(this, R.color.detailTitleColor)));
            //pre animate
            animateMute(mMuteYes, mMuteNo, dayData.isMuted());
            mMuteText.setText(dayData.isMuted() ? getResources().getString(R.string.muted_text):
                    getResources().getString(R.string.unmuted_Text));

            mMuteContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PrefManager prefManager = new PrefManager(getApplicationContext());
                    ArrayList<DayData> updatedList = prefManager.getSavedDayData();
                    int position = prefManager.getDayDataPosition(dayData);
                    //update current object
                    dayData.setMuted(!dayData.isMuted());
                    if (position != -1) {
                        updatedList.set(position, dayData);
                    }
                    //save
                    prefManager.saveDayData(updatedList);
                    prefManager.enableDataRefresh(true);

                    animateMute(mMuteYes, mMuteNo, dayData.isMuted());
                    if (dayData.isMuted()) {
                        mMuteText.setText(R.string.muted_text);
                    } else {
                        mMuteText.setText(R.string.unmuted_Text);
                    }

                }
            });
            //setup fab
            FabSpeedDial fabSpeedDial = (FabSpeedDial) findViewById(R.id.fab_speed_dial);
            fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
                @Override
                public boolean onMenuItemSelected(MenuItem menuItem) {
                    final PrefManager prefManager = new PrefManager(DayDataDetailActivity.this);
                    if (menuItem.getItemId() == R.id.edit_class) {
                        Intent intent = new Intent(DayDataDetailActivity.this, EditActivity.class);
                        intent.putExtra("DAYDATA", (Parcelable) dayData);
                        intent.putExtra("DAYDETAIL", true);
                        DayDataDetailActivity.this.startActivity(intent);
                        return true;
                    } else if (menuItem.getItemId() == R.id.save_class) {
                        MaterialDialog.Builder builder = new MaterialDialog.Builder(DayDataDetailActivity.this);
                        builder.title("Save to");
                        View dialogView = LayoutInflater.from(DayDataDetailActivity.this).inflate(R.layout.student_spinner_layout, null);
                        final SpinnerHelperClass classHelper = new SpinnerHelperClass(DayDataDetailActivity.this, dialogView, R.layout.spinner_row, true);
                        classHelper.setupClassLabelBlack();
                        classHelper.setupClass(prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram());
                        classHelper.setClassSpinnerPositions(prefManager.getLevel(), prefManager.getTerm(),  prefManager.getSection());
                        builder.customView(dialogView, true);
                        builder.positiveText("SAVE");
                        builder.negativeText(android.R.string.cancel);
                        builder.onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                                DayData toSave = new DayData(dayData.getCourseCode(), dayData.getTeachersInitial(), classHelper.getSection(), classHelper.getLevel(), classHelper.getTerm(), dayData.getRoomNo(), dayData.getTime(), dayData.getDay(), dayData.getTimeWeight(), dayData.getCourseTitle(), dayData.isMuted());
                                prefManager.saveModifiedData(toSave, PrefManager.SAVE_DATA_TAG, false);
                                Snackbar.make(DayDataDetailActivity.this.getWindow().getDecorView().findViewById(android.R.id.content), toSave.getCourseCode() + " saved!", Snackbar.LENGTH_SHORT).show();
                            }
                        });
                        MaterialDialog dialog = builder.build();
                        dialog.show();
                        return true;
                    } else if (menuItem.getItemId() == R.id.delete_class) {
                        //Show confirmation
                        AlertDialog.Builder builder = new AlertDialog.Builder(DayDataDetailActivity.this);

                        builder.setTitle("Confirm deletion");
                        builder.setMessage("Are you sure?");
                        final ArrayList<DayData> dayDatas = prefManager.getSavedDayData();
                        int position = -1;
                        for (int i = 0; i < dayDatas.size(); i++) {
                            if (dayData.equals(dayDatas.get(i))) {
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
                                onBackPressed();
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

            //SET COURSE TITLE
            ((TextView) findViewById(R.id.course_title_tv)).setText((dayData.getCourseTitle() != null) ? dayData.getCourseTitle() : "N/A");
            ((TextView) findViewById(R.id.teachers_initial_tv)).setText(dayData.getTeachersInitial());
            ((TextView) findViewById(R.id.section_tv)).setText(dayData.getSection());
            ((TextView) findViewById(R.id.weekday_tv)).setText(dayData.getDay());
            ((TextView) findViewById(R.id.room_no_tv)).setText(dayData.getRoomNo());
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            boolean isRamadan = preferences.getBoolean("ramadan_preference", false);
            if (isRamadan) {
                ((TextView) findViewById(R.id.time_tv)).setText(DayDataAdapter.DayDataHolder.convertToRamadanTime(dayData.getTime(), dayData.getTimeWeight()));
            } else {
                ((TextView) findViewById(R.id.time_tv)).setText(dayData.getTime());
            }


        }
    }

    public void animateMute(View imageMuted, View imageUnmuted, boolean isMuted) {
        imageMuted.setVisibility(View.VISIBLE);
        imageUnmuted.setVisibility(View.VISIBLE);

        imageUnmuted.animate().scaleX(isMuted ? 0 : 1).scaleY(isMuted ? 0 : 1).alpha(isMuted ? 0 : 1).start();
        imageMuted.animate().scaleX(isMuted ? 1 : 0).scaleY(isMuted ? 1 : 0).alpha(isMuted ? 1 : 0).start();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Orientation change hack
        if (dayData != null) {
            outState.putCharSequence("AppBarTitle", dayData.getCourseCode());
        } else {
            outState.putCharSequence("AppBarTitle", bundle.getCharSequence("AppBarTitle"));
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            if (fromNotification) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                onBackPressed();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
