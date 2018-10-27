package bd.edu.daffodilvarsity.classorganizer.ui.setup;

import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
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


public class LevelFragment extends Fragment {
    private static final String TAG = "LevelFragment";


    private View mView;
    private SetupViewModel mViewModel;

    private AppCompatSpinner mInitialPicker;
    private AppCompatSpinner mLevelPicker;
    private AppCompatSpinner mTermPicker;

    public LevelFragment() {
        // Required empty public constructor
    }

    private String mUserType;

    @BindView(R.id.lf_level_term_header)
    TextView mHeader;
    @BindView(R.id.lf_level_select)
    MaterialButton mSelectLevel;
    @BindView(R.id.lf_level_term)
    TextView mLevelTermText;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_level, container, false);
        ButterKnife.bind(this, mView);
        setupView();
        return mView;
    }

    private void setupView() {
        mViewModel = ViewModelProviders.of(getActivity()).get(SetupViewModel.class);
        mViewModel.getUserTypeListener().observe(getActivity(), s -> {
            if (s != null) {
                mUserType = s;
                if (s.equals(SetupViewModel.USER_STUDENT)) {
                    mHeader.setText(R.string.select_your_level_amp_term);
                    mSelectLevel.setText(R.string.select_level_term);
                    mLevelTermText.setText(R.string.level_term_not_selected);
                    mSelectLevel.setOnClickListener(v -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppTheme_LightDialog);
                        builder.setView(R.layout.picker_level);
                        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                            if (mLevelPicker != null && mTermPicker != null) {
                                int level = mLevelPicker.getSelectedItemPosition();
                                int term = mTermPicker.getSelectedItemPosition();
                                mViewModel.updateLevelTerm(level, term);
                                mLevelTermText.setText(getString(R.string.selected_level_term, level+1, term+1));
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        mLevelPicker = dialog.findViewById(R.id.lp_level_spinner);
                        mTermPicker = dialog.findViewById(R.id.lp_term_spinner);
                        ArrayAdapter<CharSequence> levelAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.level_array, R.layout.spinner_row_zero);
                        ArrayAdapter<CharSequence> termAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.term_array, R.layout.spinner_row_zero);
                        levelAdapter.setDropDownViewResource(R.layout.spinner_row);
                        termAdapter.setDropDownViewResource(R.layout.spinner_row);
                        mLevelPicker.setAdapter(levelAdapter);
                        mTermPicker.setAdapter(termAdapter);

                    });
                } else {
                    mHeader.setText(R.string.select_your_initial);
                    mSelectLevel.setText(R.string.select_initial);
                    mLevelTermText.setText(R.string.initial_not_selected);
                    mSelectLevel.setOnClickListener(v -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppTheme_LightDialog);
                        builder.setView(R.layout.picker_initial);
                        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                            if (mInitialPicker != null
                                    && mViewModel.getTeachersInitialListener(mViewModel.getCampus(), mViewModel.getDepartment()).getValue() != null
                                    && mViewModel.getTeachersInitialListener(mViewModel.getCampus(), mViewModel.getDepartment()).getValue().getData() != null) {
                                mViewModel.updateInitial(mViewModel.getTeachersInitialListener(mViewModel.getCampus(), mViewModel.getDepartment()).getValue().getData().get(mInitialPicker.getSelectedItemPosition()));
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        mViewModel.getTeachersInitialListener(
                                mViewModel.getSelectedCampusListener().getValue(), "cse"
                        ).observe(getActivity(), listResource -> {
                            if (listResource != null) {
                                mInitialPicker = dialog.findViewById(R.id.ip_picker);
                                ImageView progressView = dialog.findViewById(R.id.ip_progress);
                                ConstraintLayout holderView = dialog.findViewById(R.id.ip_view_holder);
                                TextView errorView = dialog.findViewById(R.id.ip_error);
                                if (holderView != null && progressView != null && errorView != null && mInitialPicker != null) {
                                    switch (listResource.getStatus()) {
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
                                            errorView.setText(R.string.initials_error);
                                            break;
                                        case SUCCESSFUL:
                                            holderView.setVisibility(View.VISIBLE);
                                            errorView.setVisibility(View.INVISIBLE);
                                            progressView.setVisibility(View.INVISIBLE);
                                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_row_zero, new ArrayList<>(listResource.getData()));
                                            mInitialPicker.setAdapter(adapter);
                                            break;
                                    }

                                }
                            }
                        });


                    });
                }
            }
        });
        mViewModel.getSelectedInitialListener().observe(getActivity(), s -> {
            if (s != null) {
                mLevelTermText.setText(getString(R.string.selected_initial, s));
            } else {
                if (mViewModel.getUserType().equals(SetupViewModel.USER_TEACHER)) {
                    mLevelTermText.setText(R.string.no_initials_selected);
                }
            }

        });

        mViewModel.getSelectedLevelListener().observe(getActivity(), integer -> {
            if (integer != null) {
                mLevelTermText.setText(getString(R.string.selected_level_term, integer+1, 1));
            } else {
                if (mViewModel.getUserType().equals(SetupViewModel.USER_STUDENT)) {
                    mLevelTermText.setText(R.string.level_term_not_selected);
                }
            }
        });

        mViewModel.getSelectedTermListener().observe(getActivity(), integer -> {
            if (integer != null) {
                mLevelTermText.setText(getString(R.string.selected_level_term, mViewModel.getSelectedLevelListener().getValue()+1, integer+1));
            }
        });

    }


}
