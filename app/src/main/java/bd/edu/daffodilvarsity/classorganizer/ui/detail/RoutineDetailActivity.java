package bd.edu.daffodilvarsity.classorganizer.ui.detail;

import androidx.lifecycle.ViewModelProviders;
import android.os.Build;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.model.Routine;
import bd.edu.daffodilvarsity.classorganizer.model.Teacher;
import bd.edu.daffodilvarsity.classorganizer.ui.base.BaseActivity;
import bd.edu.daffodilvarsity.classorganizer.ui.main.MainActivity;
import bd.edu.daffodilvarsity.classorganizer.utils.InputHelper;
import bd.edu.daffodilvarsity.classorganizer.utils.PreferenceGetter;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * An activity representing a single DayData detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
// * in a {@link MainActivity}.
 */
public class RoutineDetailActivity extends BaseActivity {

    private RoutineDetailViewModel mViewModel;

    public static final String ROUTINE_DETAIL_TAG = "routine_details";

    @BindView(R.id.routine_detail_toolbar_title) TextView mToolbarTitle;
    @BindView(R.id.routine_detail_course_title) TextView mCourseTitle;
    @BindView(R.id.routine_detail_initial) TextView mTeacherInitial;
    @BindView(R.id.routine_detail_section) TextView mSection;
    @BindView(R.id.routine_detail_weekday) TextView mWeekday;
    @BindView(R.id.routine_detail_room_no) TextView mRoomNo;
    @BindView(R.id.routine_detail_time) TextView mTime;
    @BindView(R.id.routine_detail_toolbar) Toolbar mToolbar;
    @BindView(R.id.routine_detail_progress) MaterialProgressBar mProgress;
    @BindView(R.id.routine_detail_teacher_name_title) TextView mTeacherNameTitle;
    @BindView(R.id.routine_detail_teacher_name) TextView mTeacherName;
    @BindView(R.id.routine_detail_teacher_designation_title) TextView mTeacherDesignationTitle;
    @BindView(R.id.routine_detail_teacher_designation) TextView mTeacherDesignation;
    @BindView(R.id.routine_detail_teacher_phone_title) TextView mTeacherPhoneTitle;
    @BindView(R.id.routine_detail_teacher_phone) TextView mTeacherPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_detail);
        ButterKnife.bind(this);
        mViewModel = ViewModelProviders.of(this).get(RoutineDetailViewModel.class);
        setSupportActionBar(mToolbar);
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
        if (getIntent().getExtras() != null) {
            Routine routine = getIntent().getExtras().getParcelable(ROUTINE_DETAIL_TAG);
            if (routine != null) {
                mViewModel.getRoutineListener().postValue(routine);
            }
        }
        loadUi();

    }

    private void loadUi() {

        mViewModel.getRoutineListener().observe(this, routine -> {
            if (routine != null) {
                //set view
                mCourseTitle.setText(InputHelper.toNA(routine.getCourseTitle()));
                mTeacherInitial.setText(InputHelper.toNA(routine.getTeachersInitial()));
                mRoomNo.setText(InputHelper.toNA(routine.getRoomNo()));
                mWeekday.setText(InputHelper.toNA(routine.getDay()));
                mSection.setText(InputHelper.toNA(routine.getSection()));
                mTime.setText(PreferenceGetter.isRamadanEnabled() ? routine.getAltTime() : routine.getTime());
                mViewModel.loadTeachersDetails(routine.getTeachersInitial());
            }
        });
        mViewModel.getTeacherInfoListener().observe(this, teacherResource -> {
            if (teacherResource != null) {
                switch (teacherResource.getStatus()) {
                    case LOADING:
                        mProgress.setIndeterminate(true);
                        mProgress.setVisibility(View.VISIBLE);
                        hideTeacherSection();
                        break;
                    case ERROR:
                        mProgress.setVisibility(View.GONE);
                        hideTeacherSection();
                        break;
                    case SUCCESSFUL:
                        mProgress.setVisibility(View.GONE);
                        showTeacherSection(teacherResource.getData());
                        break;
                }
            } else {
                hideTeacherSection();
            }
        });
    }

    private void hideTeacherSection() {
        mTeacherDesignation.setVisibility(View.GONE);
        mTeacherDesignationTitle.setVisibility(View.GONE);
        mTeacherName.setVisibility(View.GONE);
        mTeacherNameTitle.setVisibility(View.GONE);
        mTeacherPhone.setVisibility(View.GONE);
        mTeacherPhoneTitle.setVisibility(View.GONE);
    }
    private void showTeacherSection(Teacher teacher) {
        mTeacherDesignation.setVisibility(View.VISIBLE);
        mTeacherDesignationTitle.setVisibility(View.VISIBLE);
        mTeacherName.setVisibility(View.VISIBLE);
        mTeacherNameTitle.setVisibility(View.VISIBLE);
        mTeacherPhone.setVisibility(View.VISIBLE);
        mTeacherPhoneTitle.setVisibility(View.VISIBLE);
        mTeacherName.setText(InputHelper.toNA(teacher.getName()));
        mTeacherDesignation.setText(InputHelper.toNA(teacher.getDesignation()));
        if (!InputHelper.isEmpty(teacher.getPhoneNo())) {
            mTeacherPhone.setText(teacher.getPhoneNo());
        } else if (!InputHelper.isEmpty(teacher.getRoomNo())) {
            mTeacherPhoneTitle.setText(R.string.room);
            mTeacherPhone.setText(teacher.getRoomNo());
        } else {
            mTeacherPhoneTitle.setText(getString(R.string.email));
            mTeacherPhone.setText(InputHelper.toNA(teacher.getEmail()));
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
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
