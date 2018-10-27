package bd.edu.daffodilvarsity.classorganizer.ui.setup;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.button.MaterialButton;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.utils.InputHelper;
import bd.edu.daffodilvarsity.classorganizer.utils.ViewUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class DepartmentFragment extends Fragment {

    private View mView;
    private SetupViewModel mViewModel;

    public DepartmentFragment() {
        // Required empty public constructor
    }

    @BindView(R.id.df_department_select)
    MaterialButton mSelectDept;
    @BindView(R.id.df_select_program)
    MaterialButton mSelectProgram;
    @BindView(R.id.df_department_text)
    TextView mDepartmentText;
    @BindView(R.id.df_program_text)
    TextView mProgramText;

    private AppCompatSpinner mDepartmentPicker;
    private AppCompatSpinner mProgramPicker;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_department, container, false);
        ButterKnife.bind(this, mView);
        setupView();
        return mView;
    }

    private void setupView() {
        mViewModel = ViewModelProviders.of(getActivity()).get(SetupViewModel.class);


        mSelectDept.setOnClickListener(v -> {
            MaterialDialog materialDialog = new MaterialDialog.Builder(getActivity())
                    .customView(R.layout.picker_department, false)
                    .positiveText(android.R.string.ok)
                    .onPositive((dialog1, which) -> {
                        if (mDepartmentPicker != null
                                && mViewModel.getDepartmentListListener(mViewModel.getCampus()).getValue() != null
                                && mViewModel.getDepartmentListListener(mViewModel.getCampus()).getValue().getData() != null) {
                            mViewModel.updateDepartment (
                                    mViewModel.getDepartmentListListener(mViewModel.getCampus()).getValue().getData().get(mDepartmentPicker.getSelectedItemPosition())
                            );
                        }
                    })
                    .build();
            materialDialog.show();
            ConstraintLayout holder = materialDialog.getCustomView().findViewById(R.id.dp_holder);
            ImageView progress = materialDialog.getCustomView().findViewById(R.id.dp_progress);
            TextView error = materialDialog.getCustomView().findViewById(R.id.dp_error);
            mDepartmentPicker = materialDialog.getCustomView().findViewById(R.id.dp_picker);
            if (holder != null && progress != null && error != null && mDepartmentPicker != null) {
                mViewModel.getDepartmentListListener(mViewModel.getCampus()).observe(getActivity(), listResource -> {
                    if (listResource != null) {
                        switch (listResource.getStatus()) {
                            case SUCCESSFUL:
                                holder.setVisibility(View.VISIBLE);
                                error.setVisibility(View.INVISIBLE);
                                progress.setVisibility(View.INVISIBLE);
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_row_zero, ViewUtils.upperCaseList(listResource.getData()));
                                adapter.setDropDownViewResource(R.layout.spinner_row);
                                mDepartmentPicker.setAdapter(adapter);
                                break;
                            case ERROR:
                                holder.setVisibility(View.INVISIBLE);
                                error.setVisibility(View.VISIBLE);
                                progress.setVisibility(View.INVISIBLE);
                                error.setText(R.string.program_error);
                                break;
                            case LOADING:
                                holder.setVisibility(View.INVISIBLE);
                                error.setVisibility(View.INVISIBLE);
                                progress.setVisibility(View.VISIBLE);
                                CircularProgressDrawable progressDrawable = ViewUtils.getProgressDrawable(4, 16, R.color.text_color_light);
                                progressDrawable.start();
                                progress.setImageDrawable(progressDrawable);
                                break;
                        }
                    }
                });
            }

        });

        mViewModel.getSelectedDepartmentListener().observe(getActivity(), s -> {
            if (s != null && mViewModel.getUserTypeListener().getValue() != null
                    && mViewModel.getUserTypeListener().getValue().equals(SetupViewModel.USER_STUDENT)) {
                mSelectProgram.setVisibility(View.VISIBLE);
                mProgramText.setVisibility(View.VISIBLE);
                mSelectProgram.setOnClickListener(v -> {
                    MaterialDialog materialDialog = new MaterialDialog.Builder(getActivity())
                            .customView(R.layout.picker_program, false)
                            .positiveText(android.R.string.ok)
                            .onPositive((dialog12, which) -> {
                                String program;
                                if (mProgramPicker != null
                                        && mViewModel.getProgramListListener(mViewModel.getCampus(), mViewModel.getDepartment()).getValue() != null
                                        && mViewModel.getProgramListListener(mViewModel.getCampus(), mViewModel.getDepartment()).getValue().getData() != null) {
                                    mViewModel.updateProgram (
                                            program = mViewModel.getProgramListListener(mViewModel.getCampus(), mViewModel.getDepartment()).getValue().getData().get(mProgramPicker.getSelectedItemPosition())
                                    );
                                    mProgramText.setText(getString(R.string.selected_program, InputHelper.capitalizeFirstLetter(program)));
                                }
                            })
                            .build();
                    materialDialog.show();
                    ConstraintLayout holder = materialDialog.getCustomView().findViewById(R.id.pp_holder);
                    ImageView progress = materialDialog.getCustomView().findViewById(R.id.pp_progress);
                    TextView error = materialDialog.getCustomView().findViewById(R.id.pp_error);
                    mProgramPicker = materialDialog.getCustomView().findViewById(R.id.pp_picker);
                    if (holder != null && progress != null && error != null && mProgramPicker != null) {
                        mViewModel.getProgramListListener(mViewModel.getCampus(), mViewModel.getDepartment()).observe(getActivity(), listResource -> {
                            if (listResource != null) {
                                switch (listResource.getStatus()) {
                                    case SUCCESSFUL:
                                        holder.setVisibility(View.VISIBLE);
                                        error.setVisibility(View.INVISIBLE);
                                        progress.setVisibility(View.INVISIBLE);
                                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_row_zero, ViewUtils.capitalizeList(listResource.getData()));
                                        mProgramPicker.setAdapter(adapter);
                                        break;
                                    case ERROR:
                                        holder.setVisibility(View.INVISIBLE);
                                        error.setVisibility(View.VISIBLE);
                                        progress.setVisibility(View.INVISIBLE);
                                        error.setText(R.string.program_error);
                                        break;
                                    case LOADING:
                                        holder.setVisibility(View.INVISIBLE);
                                        error.setVisibility(View.INVISIBLE);
                                        progress.setVisibility(View.VISIBLE);
                                        CircularProgressDrawable progressDrawable = ViewUtils.getProgressDrawable(4, 16, R.color.text_color_light);
                                        progressDrawable.start();
                                        progress.setImageDrawable(progressDrawable);
                                        break;
                                }
                            }
                        });
                    }

                });
            } else {
                mSelectProgram.setVisibility(View.INVISIBLE);
                mProgramText.setVisibility(View.INVISIBLE);
            }
        });

        mViewModel.getUserTypeListener().observe(getActivity(), s -> {
            if (!InputHelper.isEmpty(s)) {
                if (s.equals(SetupViewModel.USER_STUDENT)) {
                    mSelectProgram.setVisibility(View.VISIBLE);
                    mProgramText.setVisibility(View.VISIBLE);
                } else {
                    mSelectProgram.setVisibility(View.INVISIBLE);
                    mProgramText.setVisibility(View.INVISIBLE);
                }
            }
        });
        mViewModel.getSelectedDepartmentListener().observe(getActivity(), s -> {
            if (s != null) {
                mDepartmentText.setText(getString(R.string.selected_department, s.toUpperCase()));
            } else {
                mDepartmentText.setText(R.string.no_department_selected);
            }
        });
        mViewModel.getSelectedProgramListener().observe(getActivity(), s -> {
            if (s != null) {
                mProgramText.setText(getString(R.string.selected_program, s.toUpperCase()));
            } else {
                mProgramText.setText(R.string.no_program_selected);
            }
        });
    }

}
