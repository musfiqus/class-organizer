package bd.edu.daffodilvarsity.classorganizer.ui.setup;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
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

import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.utils.InputHelper;
import bd.edu.daffodilvarsity.classorganizer.utils.ViewUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class CampusFragment extends Fragment {


    public CampusFragment() {
        // Required empty public constructor
    }

    private View mView;

    private AppCompatSpinner mNumberPicker;

    private SetupViewModel mViewModel;

    @BindView(R.id.cf_campus_select_button)
    MaterialButton mCampusButton;
    @BindView(R.id.cf_campus_icon)
    ImageView mCampusIcon;
    @BindView(R.id.cf_selected_campus)
    TextView mSelectedCampus;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_campus, container, false);
        ButterKnife.bind(this, mView);
        setupView();
        return mView;
    }

    private void setupView() {
        mViewModel = ViewModelProviders.of(getActivity()).get(SetupViewModel.class);
        mViewModel.getSelectedCampusListener().observe(getActivity(), s -> {
            if (s == null) {
                mSelectedCampus.setText(R.string.no_campus);
            } else {
                mSelectedCampus.setText(InputHelper.capitalizeFirstLetter(s));
            }
        });
        mCampusButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppTheme_LightDialog);
            builder.setView(R.layout.picker_campus);
            builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                if (mNumberPicker != null
                        && mViewModel.getCampusListListener().getValue() != null
                        && mViewModel.getCampusListListener().getValue().getData() != null) {
                    mViewModel.updateCampus (
                            mViewModel.getCampusListListener().getValue().getData().get(mNumberPicker.getSelectedItemPosition())
                    );
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            ImageView progress = dialog.findViewById(R.id.cp_progress);
            ConstraintLayout holder = dialog.findViewById(R.id.cp_view_holder);
            TextView error = dialog.findViewById(R.id.cp_error);
            mViewModel.getCampusListListener().observe(this, listResource -> {
                if (listResource != null) {
                    if (progress != null && holder != null && error != null) {
                        switch (listResource.getStatus()) {
                            case SUCCESSFUL: {
                                progress.setVisibility(View.INVISIBLE);
                                holder.setVisibility(View.VISIBLE);
                                mNumberPicker = dialog.findViewById(R.id.cp_picker);
                                if (listResource.getData() != null && mNumberPicker != null) {
                                    ArrayList<String> valuesToDisplay = new ArrayList<>();
                                    for (String s: listResource.getData()) {
                                        if (s != null) {
                                            s = s.substring(0, 1).toUpperCase() + s.substring(1);
                                        }
                                        valuesToDisplay.add(s);
                                    }
                                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_row_zero, valuesToDisplay);
                                    adapter.setDropDownViewResource(R.layout.spinner_row);
                                    mNumberPicker.setAdapter(adapter);
                                }
                                break;
                            }
                            case LOADING: {
                                progress.setVisibility(View.VISIBLE);
                                CircularProgressDrawable progressDrawable = ViewUtils.getProgressDrawable(4, 24, R.color.text_color_light);
                                progressDrawable.start();
                                progress.setImageDrawable(progressDrawable);
                                holder.setVisibility(View.INVISIBLE);
                                break;
                            }
                            case ERROR: {
                                holder.setVisibility(View.INVISIBLE);
                                progress.setVisibility(View.INVISIBLE);
                                error.setVisibility(View.VISIBLE);
                                error.setText(R.string.campus_error);
                            }
                        }

                    }
                    }

            });



        });
    }

}
