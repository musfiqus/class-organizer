package bd.edu.daffodilvarsity.classorganizer.ui.setup;


import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.button.MaterialButton;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import bd.edu.daffodilvarsity.classorganizer.R;
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
                        MaterialDialog materialDialog = new MaterialDialog.Builder(getActivity())
                                .customView(R.layout.picker_section, false)
                                .positiveText(android.R.string.ok)
                                .onPositive((dialog1, which) -> {
                                    String section;
                                    if (mSectionPicker != null &&
                                            mViewModel.getSectionListListener(mViewModel.getCampus(), mViewModel.getDepartment(), mViewModel.getProgram(), mViewModel.getLevel(), mViewModel.getTerm()).getValue() != null
                                            && mViewModel.getSectionListListener(mViewModel.getCampus(), mViewModel.getDepartment(), mViewModel.getProgram(), mViewModel.getLevel(), mViewModel.getTerm()).getValue().getData() != null) {
                                        mViewModel.updateSection(section = mViewModel.getSectionListListener(mViewModel.getCampus(), mViewModel.getDepartment(), mViewModel.getProgram(), mViewModel.getLevel(), mViewModel.getTerm()).getValue().getData().get(mSectionPicker.getSelectedItemPosition()));
                                    }
                                })
                                .build();
                        materialDialog.show();
                        mSectionPicker = materialDialog.getCustomView().findViewById(R.id.sp_picker);
                        ImageView progressView = materialDialog.getCustomView().findViewById(R.id.sp_progress);
                        ConstraintLayout holderView = materialDialog.getCustomView().findViewById(R.id.sp_view_holder);
                        TextView errorView = materialDialog.getCustomView().findViewById(R.id.sp_error);
                        if (mSectionPicker != null && progressView != null && holderView != null && errorView != null) {
                            mViewModel.getSectionListListener(mViewModel.getCampus(), mViewModel.getDepartment(), mViewModel.getProgram(), mViewModel.getLevel(), mViewModel.getTerm()).observe(getActivity(), listResource -> {
                                if (listResource != null) {
                                    switch (listResource.getStatus()) {
                                        case SUCCESSFUL:
                                            holderView.setVisibility(View.VISIBLE);
                                            errorView.setVisibility(View.INVISIBLE);
                                            progressView.setVisibility(View.INVISIBLE);
                                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_row, new ArrayList<>(listResource.getData()));
                                            adapter.setDropDownViewResource(R.layout.spinner_row_big);
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
