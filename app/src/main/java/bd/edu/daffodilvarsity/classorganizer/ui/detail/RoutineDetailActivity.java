package bd.edu.daffodilvarsity.classorganizer.ui.detail;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.model.Routine;
import bd.edu.daffodilvarsity.classorganizer.ui.base.BaseActivity;
import bd.edu.daffodilvarsity.classorganizer.ui.main.MainActivity;
import bd.edu.daffodilvarsity.classorganizer.utils.InputHelper;
import bd.edu.daffodilvarsity.classorganizer.utils.PreferenceGetter;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An activity representing a single DayData detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
// * in a {@link MainActivity}.
 */
public class RoutineDetailActivity extends BaseActivity {
    private Routine mRoutine;
    private Bundle bundle;
    private boolean fromNotification = false;

    public static final String ROUTINE_DETAIL_TAG = "routine_details";

    @BindView(R.id.routine_detail_toolbar_title) TextView mToolbarTitle;
    @BindView(R.id.routine_detail_course_title) TextView mCourseTitle;
    @BindView(R.id.routine_detail_initial) TextView mTeacherInitial;
    @BindView(R.id.routine_detail_section) TextView mSection;
    @BindView(R.id.routine_detail_weekday) TextView mWeekday;
    @BindView(R.id.routine_detail_room_no) TextView mRoomNo;
    @BindView(R.id.routine_detail_mute_yes) ImageView mMuteYes;
    @BindView(R.id.routine_detail_mute_no) ImageView mMuteNo;
    @BindView(R.id.routine_detail_mute_status) TextView mMuteText;
    @BindView(R.id.routine_detail_time) TextView mTime;
    @BindView(R.id.routine_detail_mute_container) RelativeLayout mMuteContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Orientation change hack
        bundle = savedInstanceState;

        setContentView(R.layout.activity_routine_detail);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.routine_detail_toolbar);
        setSupportActionBar(toolbar);


        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);
            actionBar.setTitle("");
        }

        if (Build.VERSION.SDK_INT >= 23) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));//status bar or the time bar at the top
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

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = getIntent().getExtras();
            if (arguments != null) {
                mRoutine = arguments.getParcelable(ROUTINE_DETAIL_TAG);
//                Bundle bundle = arguments.getBundle(AlarmHelper.TAG_ALARM_BUNDLE_DATA);
//                if (bundle != null) {
//                    byte[] byteDayData = bundle.getByteArray(NotificationPublisher.TAG_NOTIFICATION_DATA);
//                    if (byteDayData != null) {
//                        mRoutine = CourseUtils.convertToDayData(byteDayData);
//                        fromNotification = true;
//                    }
//                }
            }

            loadUi();
        }
    }

    private void loadUi() {
        if (mRoutine != null) {
            //set toolbar
            mToolbarTitle.setText(InputHelper.isEmpty(mRoutine.getCourseCode()) ? "N/A": mRoutine.getCourseCode());

            //set view
            mCourseTitle.setText(InputHelper.isEmpty(mRoutine.getCourseCode()) ? "N/A": mRoutine.getCourseTitle());
            mTeacherInitial.setText(mRoutine.getTeachersInitial());
            mRoomNo.setText(mRoutine.getRoomNo());
            mWeekday.setText(mRoutine.getDay());
            mSection.setText(mRoutine.getSection());
            mTime.setText(PreferenceGetter.isRamadanEnabled() ? mRoutine.getAltTime() : mRoutine.getTime());
            //pre animate
            animateMute(mMuteYes, mMuteNo, mRoutine.isMuted());
            mMuteText.setText(mRoutine.isMuted() ? getResources().getString(R.string.muted_text):
                    getResources().getString(R.string.unmuted_Text));

            mMuteContainer.setOnClickListener(v -> {
//                    PrefManager prefManager = new PrefManager(getApplicationContext());
//                    ArrayList<DayData> updatedList = prefManager.getSavedDayData();
//                    int position = prefManager.getDayDataPosition(mRoutine);
//                    //update current object
//                    mRoutine.setMuted(!mRoutine.isMuted());
//                    if (position != -1) {
//                        updatedList.set(position, mRoutine);
//                    }
//                    //save
//                    prefManager.saveDayData(updatedList);
//                    prefManager.enableDataRefresh(true);

                animateMute(mMuteYes, mMuteNo, mRoutine.isMuted());
                if (mRoutine.isMuted()) {
                    mMuteText.setText(R.string.muted_text);
                } else {
                    mMuteText.setText(R.string.unmuted_Text);
                }

            });





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
        if (mRoutine != null) {
            outState.putCharSequence("AppBarTitle", mRoutine.getCourseCode());
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
