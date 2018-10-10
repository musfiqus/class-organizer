package bd.edu.daffodilvarsity.classorganizer.ui.modify;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.model.Routine;
import bd.edu.daffodilvarsity.classorganizer.ui.SpinnerWithListener;
import bd.edu.daffodilvarsity.classorganizer.ui.TextInputAutoCompleteTextView;
import bd.edu.daffodilvarsity.classorganizer.utils.InputHelper;
import bd.edu.daffodilvarsity.classorganizer.utils.PreferenceGetter;
import bd.edu.daffodilvarsity.classorganizer.utils.ViewUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class ModifyActivity extends AppCompatActivity {
    public static final String KEY_EDIT_OBJECT = "edit_routine";


    private ModifyViewModel mViewModel;

    @BindView(R.id.ea_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.ea_toolbar_title)
    TextView mTitle;
    @BindView(R.id.ea_course_title_layout)
    TextInputLayout mTitleLayout;
    @BindView(R.id.ea_course_title)
    TextInputAutoCompleteTextView mTitleInput;
    @BindView(R.id.ea_course_code_layout)
    TextInputLayout mCodeLayout;
    @BindView(R.id.ea_course_code)
    TextInputAutoCompleteTextView mCodeInput;
    @BindView(R.id.ea_course_teacher_layout)
    TextInputLayout mTeacherLayout;
    @BindView(R.id.ea_course_teacher)
    TextInputAutoCompleteTextView mTeacherInput;
    @BindView(R.id.ea_room_no_layout)
    TextInputLayout mRoomLayout;
    @BindView(R.id.ea_room_no)
    TextInputAutoCompleteTextView mRoomInput;
    @BindView(R.id.ea_section_layout)
    TextInputLayout mSectionLayout;
    @BindView(R.id.ea_section)
    TextInputAutoCompleteTextView mSectionInput;
    @BindView(R.id.ea_time_title)
    TextView mTimeTitle;
    @BindView(R.id.ea_time)
    SpinnerWithListener mTimeInput;
    @BindView(R.id.ea_day)
    SpinnerWithListener mDayInput;
    @BindView(R.id.ea_progress)
    MaterialProgressBar mProgressBar;


    private static final String TAG = "ModifyActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        ViewCompat.setElevation(mToolbar, 0);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);
        if (Build.VERSION.SDK_INT >= 23) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));//status bar or the time bar at the top
        }
        mViewModel = ViewModelProviders.of(ModifyActivity.this).get(ModifyViewModel.class);
        Routine editRoutine = getIntent().getParcelableExtra(KEY_EDIT_OBJECT);
        mViewModel.getEditDataListener().setValue(editRoutine);
        mViewModel.getEditDataListener().observe(this, routine -> {
            if (routine == null) {
                Log.e(TAG, "onCreate: add omega?");
                setupAddView();
            } else {
                setupEditView(routine);
            }
        });
        mViewModel.getClassProgressListener().observe(this, aBoolean -> {
            if (aBoolean == null || !aBoolean) {
                mProgressBar.setVisibility(View.INVISIBLE);
            } else {
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setIndeterminate(true);
            }
        });
        mViewModel.getModificationListener().observe(this, aBoolean -> {
            if (aBoolean != null) {
                if (aBoolean) {
                    setResult(RESULT_OK);
                    finish();
                } else {
                    setResult(RESULT_CANCELED);
                    finish();
                }
            }
        });
        mViewModel.getAdapterListener().observe(this, adapterModelResource -> {
            if (adapterModelResource != null) {
                switch (adapterModelResource.getStatus()) {
                    case SUCCESSFUL:
                        mTitleInput.setAdapter(adapterModelResource.getData().titles);
                        mCodeInput.setAdapter(adapterModelResource.getData().codes);
                        mTeacherInput.setAdapter(adapterModelResource.getData().teachers);
                        mRoomInput.setAdapter(adapterModelResource.getData().rooms);
                        mSectionInput.setAdapter(adapterModelResource.getData().sections);
                        mTimeInput.setAdapter(adapterModelResource.getData().times);
                        mTimeInput.setSelection(adapterModelResource.getData().timePosition, true);
                        mDayInput.setAdapter(ArrayAdapter.createFromResource(this, R.array.weekdays, R.layout.spinner_row));
                        if (mViewModel.getEditDataListener().getValue() != null) {
                            Routine editableRoutine = mViewModel.getEditDataListener().getValue();
                            for (int i = 0; i < getResources().getStringArray(R.array.weekdays).length; i++) {
                                if (getResources().getStringArray(R.array.weekdays)[i].equalsIgnoreCase(editableRoutine.getDay())) {
                                    mDayInput.setSelection(i);
                                    break;
                                }
                            }
                        }
                        break;
                    case ERROR:
                        Toasty.error(ModifyActivity.this, getString(R.string.class_list_error), Toast.LENGTH_SHORT, true).show();
                        break;
                    case LOADING:
                        break;
                }
            }
        });
        mTimeInput.setSpinnerEventsListener(new SpinnerWithListener.OnSpinnerEventsListener() {
            @Override
            public void onSpinnerOpened(AppCompatSpinner spinner) {
                mTimeTitle.setTextColor(ViewUtils.fetchAccentColor(ModifyActivity.this));
            }

            @Override
            public void onSpinnerClosed(AppCompatSpinner spinner) {
                mTimeTitle.setTextColor(ContextCompat.getColor(ModifyActivity.this, R.color.md_grey_500));
            }
        });
    }

    private void setupAddView() {
        mTitle.setText(R.string.add_title);
        mViewModel.loadAdapters(null);
    }

    private void setupEditView(Routine routine) {
        mTitle.setText(R.string.edit_title);
        mTitleInput.setText(routine.getCourseTitle());
        mCodeInput.setText(routine.getCourseCode());
        mRoomInput.setText(routine.getRoomNo());
        mTeacherInput.setText(routine.getTeachersInitial());
        mSectionInput.setText(routine.getSection());
        mViewModel.loadAdapters(PreferenceGetter.isRamadanEnabled() ? routine.getAltTime(): routine.getTime());
    }

    private boolean validate() {
        // Reset errors.
        mTitleLayout.setError(null);
        mCodeLayout.setError(null);
        mTeacherLayout.setError(null);
        mRoomLayout.setError(null);
        mSectionLayout.setError(null);

        boolean invalid = false;
        View focusView = null;

        //Check for a valid title
        if (InputHelper.isEmpty(mTitleInput)) {
            mTitleLayout.setError(getString(R.string.field_empty_error));
            focusView = mTitleLayout;
            invalid = true;
        }
        if (InputHelper.isEmpty(mCodeInput)) {
            mCodeLayout.setError(getString(R.string.field_empty_error));
            focusView = mCodeLayout;
            invalid = true;
        }
        if (InputHelper.isEmpty(mTeacherInput)) {
            mTeacherLayout.setError(getString(R.string.field_empty_error));
            focusView = mTeacherLayout;
            invalid = true;
        }
        if (InputHelper.isEmpty(mRoomInput)) {
            mRoomLayout.setError(getString(R.string.field_empty_error));
            focusView = mRoomLayout;
            invalid = true;
        }
        if (InputHelper.isEmpty(mSectionInput)) {
            mSectionLayout.setError(getString(R.string.field_empty_error));
            focusView = mSectionLayout;
            invalid = true;
        }

        if (invalid) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }
        return !invalid;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_modify_menu, menu);
        Drawable drawable = menu.findItem(R.id.ea_done_button).getIcon();
        if (drawable != null) {
            // If we don't mutate the drawable, then all drawable's with this id will have a color
            // filter applied to it.
            drawable.mutate();
            drawable.setColorFilter(ViewUtils.fetchAccentColor(this), PorterDuff.Mode.SRC_ATOP);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.ea_done_button) {
            if (validate()) {
                Routine routine = new Routine();
                routine.setCourseTitle(mTitleInput.getText().toString());
                routine.setCourseCode(mCodeInput.getText().toString());
                routine.setTeachersInitial(mTeacherInput.getText().toString());
                routine.setRoomNo(mRoomInput.getText().toString());
                routine.setSection(mSectionInput.getText().toString());
                routine.setDay(mDayInput.getSelectedItem().toString());
                if (PreferenceGetter.isRamadanEnabled()) {
                    routine.setAltTime(mTimeInput.getSelectedItem().toString());
                } else {
                    routine.setTime(mTimeInput.getSelectedItem().toString());
                }
                Routine originalRoutine = mViewModel.getEditDataListener().getValue();
                if (originalRoutine == null) {
                    routine.setId(0);
                    routine.setDepartment(PreferenceGetter.getDepartment());
                    routine.setCampus(PreferenceGetter.getCampus());
                    routine.setProgram(PreferenceGetter.getProgram());
                    mViewModel.addRoutine(routine);
                } else {
                    routine.setId(originalRoutine.getId());
                    routine.setCampus(originalRoutine.getCampus());
                    routine.setDepartment(originalRoutine.getDepartment());
                    routine.setProgram(originalRoutine.getProgram());
                    routine.setLevel(originalRoutine.getLevel());
                    routine.setTerm(originalRoutine.getTerm());
                    if (routine.equals(originalRoutine)) {
                        Toasty.success(this, "Saved").show();
                        finish();
                    } else {
                        mViewModel.modifyRoutine(originalRoutine, routine);
                    }

                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
