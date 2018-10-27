package bd.edu.daffodilvarsity.classorganizer.ui.setup;


import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.heinrichreimersoftware.materialintro.app.SlideFragment;

import bd.edu.daffodilvarsity.classorganizer.ClassOrganizer;
import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.utils.ViewUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateFragment extends SlideFragment {
    private static final String TAG = "UpdateFragment";


    public UpdateFragment() {
        // Required empty public constructor
    }

    private View mView;

    private SetupViewModel mViewModel;

    @BindView(R.id.wf_progress)
    ImageView mProgressView;
    @BindView(R.id.wf_progress_text)
    TextView mProgressText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_update, container, false);
        ButterKnife.bind(this, mView);
        setupView();
        return mView;
    }

    private void setupView() {
        mViewModel = ViewModelProviders.of(getActivity()).get(SetupViewModel.class);
        mViewModel.getUpdateListener()
                .observe(getActivity(), booleanResource -> {
                    CircularProgressDrawable progressDrawable = ViewUtils.getProgressDrawable(2, 8, R.color.md_grey_500);
                    progressDrawable.start();
                    if (booleanResource != null) {
                        switch (booleanResource.getStatus()) {
                            case ERROR: {
                                progressDrawable.stop();
                                //SET ERROR
                                mProgressView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_close_black_24dp));
                                mProgressText.setText(R.string.update_error);
                                break;
                            }
                            case LOADING: {
                                mProgressView.setImageDrawable(progressDrawable);
                                mProgressText.setText(R.string.checking_update);
                                break;
                            }
                            case UPDATING : {
                                mProgressView.setImageDrawable(progressDrawable);
                                mProgressText.setText(R.string.download_update);
                                break;
                            }
                            case SUCCESSFUL: {
                                progressDrawable.stop();
                                mProgressView.setImageDrawable(ContextCompat.getDrawable(ClassOrganizer.getInstance(), R.drawable.ic_done_vector));
                                if (booleanResource.getData()) {
                                    mProgressText.setText(R.string.routine_updated);
                                } else {
                                    mProgressText.setText(R.string.already_updated);
                                }
                                break;
                            }
                        }
                    }
                });
    }

}
