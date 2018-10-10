package bd.edu.daffodilvarsity.classorganizer.ui.setup;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.chip.Chip;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bd.edu.daffodilvarsity.classorganizer.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserTypeFragment extends Fragment {

    private static final String TAG = "UserTypeFragment";


    public UserTypeFragment() {
        // Required empty public constructor
    }

    @BindView(R.id.utf_teacher_chip)
    Chip mTeacherChip;
    @BindView(R.id.utf_student_chip)
    Chip mStudentChip;

    private View mView;

    private SetupViewModel mViewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_user_type, container, false);
        ButterKnife.bind(this, mView);
        setupView();
        return mView;
    }

    private void setupView() {
        mViewModel = ViewModelProviders.of(getActivity()).get(SetupViewModel.class);
        mStudentChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mViewModel.updateUserType(SetupViewModel.USER_STUDENT);
                mTeacherChip.setChecked(false);
            } else {
                if (!mTeacherChip.isChecked()) {
                    mViewModel.updateUserType(null);
                }
            }
        });
        mTeacherChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mViewModel.updateUserType(SetupViewModel.USER_TEACHER);
                mStudentChip.setChecked(false);
            } else {
                if (!mStudentChip.isChecked()) {
                    mViewModel.updateUserType(null);
                }
            }
        });
        mViewModel.getUserTypeListener().observe(getActivity(), s -> {
            if (s == null) {
                if (mStudentChip.isChecked()) {
                    mStudentChip.setChecked(false);
                }
                if (mTeacherChip.isChecked()) {
                    mTeacherChip.setChecked(false);
                }
            } else {
                if (s.equals(SetupViewModel.USER_STUDENT)) {
                    if (!mStudentChip.isChecked()) {
                        mStudentChip.setChecked(true);
                    }
                } else {
                    if (!mTeacherChip.isChecked()) {
                        mTeacherChip.setChecked(true);
                    }
                }
            }
        });
    }

}
