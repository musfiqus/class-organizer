package bd.edu.daffodilvarsity.classorganizer.ui.setup;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.model.Resource;
import bd.edu.daffodilvarsity.classorganizer.utils.ViewUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class SectionFragment extends Fragment {

    private View mView;
    private SetupViewModel mViewModel;

    public SectionFragment() {
        // Required empty public constructor
    }

    @BindView(R.id.sf_finish_holder)
    ConstraintLayout mFinishHolder;

    @BindView(R.id.sf_holder)
    ConstraintLayout mHolder;
    @BindView(R.id.sf_section_select)
    MaterialButton mSelectSection;
    @BindView(R.id.sf_section_text)
    TextView mSectionSelectionText;


    private AppCompatSpinner mSectionPicker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_section, container, false);
        ButterKnife.bind(this, mView);
        setupView();
        return mView;
    }

    private void setupView() {
        mViewModel = ViewModelProviders.of(getActivity()).get(SetupViewModel.class);
        mViewModel.getUserTypeListener().observe(getActivity(), s -> {
            if (s != null) {
                if (s.equals(SetupViewModel.USER_STUDENT)) {
                    mHolder.setVisibility(View.VISIBLE);
                    mFinishHolder.setVisibility(View.INVISIBLE);
                    mSelectSection.setOnClickListener(v -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppTheme_LightDialog);
                        builder.setView(R.layout.picker_section);

                        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                            String section;
                            if (mSectionPicker != null &&
                                    mViewModel.getSectionListListener(mViewModel.getCampus(), mViewModel.getDepartment(), mViewModel.getProgram(), mViewModel.getLevel(), mViewModel.getTerm()).getValue() != null
                                    && mViewModel.getSectionListListener(mViewModel.getCampus(), mViewModel.getDepartment(), mViewModel.getProgram(), mViewModel.getLevel(), mViewModel.getTerm()).getValue().getData() != null) {
                                mViewModel.updateSection(section = mViewModel.getSectionListListener(mViewModel.getCampus(), mViewModel.getDepartment(), mViewModel.getProgram(), mViewModel.getLevel(), mViewModel.getTerm()).getValue().getData().get(mSectionPicker.getSelectedItemPosition()));
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        mSectionPicker = dialog.findViewById(R.id.sp_picker);
                        ImageView progressView = dialog.findViewById(R.id.sp_progress);
                        ConstraintLayout holderView = dialog.findViewById(R.id.sp_view_holder);
                        TextView errorView = dialog.findViewById(R.id.sp_error);
                        if (mSectionPicker != null && progressView != null && holderView != null && errorView != null) {
                            mViewModel.getSectionListListener(mViewModel.getCampus(), mViewModel.getDepartment(), mViewModel.getProgram(), mViewModel.getLevel(), mViewModel.getTerm()).observe(getActivity(), listResource -> {
                                if (listResource != null) {
                                    switch (listResource.getStatus()) {
                                        case SUCCESSFUL:
                                            holderView.setVisibility(View.VISIBLE);
                                            errorView.setVisibility(View.INVISIBLE);
                                            progressView.setVisibility(View.INVISIBLE);
                                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_row, new ArrayList<>(listResource.getData()));
                                            mSectionPicker.setAdapter(adapter);
                                            break;
                                        case LOADING:
                                            holderView.setVisibility(View.INVISIBLE);
                                            errorView.setVisibility(View.INVISIBLE);
                                            progressView.setVisibility(View.VISIBLE);
                                            CircularProgressDrawable progressDrawable = ViewUtils.getProgressDrawable(4, 24, R.color.text_color_light);
                                            progressDrawable.start();
                                            progressView.setImageDrawable(progressDrawable);
                                            break;
                                        case ERROR:
                                            holderView.setVisibility(View.INVISIBLE);
                                            errorView.setVisibility(View.VISIBLE);
                                            progressView.setVisibility(View.INVISIBLE);
                                            errorView.setText(R.string.sections_Error);
                                            break;
                                    }
                                }
                            });
                        }
                    });
                } else {
                    mHolder.setVisibility(View.INVISIBLE);
                    mFinishHolder.setVisibility(View.VISIBLE);
                }
            }
        });

        mViewModel.getSelectedSectionListener().observe(getActivity(), s -> {
            if (s != null) {
                mSectionSelectionText.setText(getString(R.string.selected_section, s));
            } else {
                mSectionSelectionText.setText(R.string.no_section_selected);
            }

        });


    }



}
