package bd.edu.daffodilvarsity.classorganizer.ui.search;


import android.app.Activity;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.model.Routine;
import bd.edu.daffodilvarsity.classorganizer.model.Teacher;
import bd.edu.daffodilvarsity.classorganizer.ui.TextInputAutoCompleteTextView;
import bd.edu.daffodilvarsity.classorganizer.ui.detail.RoutineDetailActivity;
import bd.edu.daffodilvarsity.classorganizer.utils.CustomFilterArrayAdapter;
import bd.edu.daffodilvarsity.classorganizer.utils.InputHelper;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchTeachersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchTeachersFragment extends Fragment {

    private static final String TAG = "SearchTeachersFragment";

    @BindView(R.id.search_fragment_options_card)
    CardView mOptionsContainer;
    @BindView(R.id.search_fragment_no_result_text)
    TextView mNoResultText;
    @BindView(R.id.search_fragment_result_list)
    RecyclerView mResultView;
    @BindView(R.id.search_fragment_teacher_list)
    RecyclerView mTeacherList;
    @BindView(R.id.search_fragment_progress)
    MaterialProgressBar mProgress;

    private View mView;
    private SearchViewModel mViewModel;
    private SearchResultAdapter mAdapter;



    public SearchTeachersFragment() {
        // Required empty public constructor
    }

    public static SearchTeachersFragment newInstance() {
        return new SearchTeachersFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, mView);
        // Inflate the layout for this fragment
        setupView(inflater);
        return mView;
    }

    private void setupView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.layout_search_teacher, mOptionsContainer, true);
        mViewModel = ViewModelProviders.of(getActivity()).get(SearchViewModel.class);
        mViewModel.getProgressListener().observe(getActivity(), aBoolean -> {
            mProgress.setVisibility(aBoolean != null && aBoolean ? View.VISIBLE : View.GONE);
        });
        TeacherOptionsViewHolder holder = new TeacherOptionsViewHolder(view);
        mAdapter = new SearchResultAdapter(new ArrayList<>(), SearchResultAdapter.RESULT_TYPE_TEACHER_CLASS);
        mResultView.setAdapter(mAdapter);
        mResultView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mViewModel.getClassListByInitialListener().observe(getActivity(), listResource -> {
            if (listResource != null) {
                switch (listResource.getStatus()) {
                    case ERROR:
                        mResultView.setVisibility(View.GONE);
                        mNoResultText.setVisibility(View.VISIBLE);
                        mNoResultText.setText(getString(R.string.no_teachers_found));
                        break;
                    case LOADING:
                        mNoResultText.setVisibility(View.GONE);
                        mResultView.setVisibility(View.GONE);
                        break;
                    case SUCCESSFUL:
                        if (listResource.getData().size() > 0) {
                            mNoResultText.setVisibility(View.GONE);
                            mResultView.setVisibility(View.VISIBLE);
                            mAdapter.setData(listResource.getData());
                        } else {
                            mResultView.setVisibility(View.GONE);
                            mNoResultText.setVisibility(View.VISIBLE);
                            mNoResultText.setText(getString(R.string.no_teachers_found));
                        }
                        break;
                }
            }
        });

        mAdapter.setOnItemClickListener(new SearchResultAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                startActivity(new Intent(getActivity(), RoutineDetailActivity.class)
                        .putExtra(RoutineDetailActivity.ROUTINE_DETAIL_TAG, (Parcelable) mAdapter.getItem(position)));
            }

            @Override
            public void onDetailsClick(int position) {
                startActivity(new Intent(getActivity(), RoutineDetailActivity.class)
                        .putExtra(RoutineDetailActivity.ROUTINE_DETAIL_TAG, (Parcelable) mAdapter.getItem(position)));
            }

            @Override
            public void onMoreClick(int position, View view) {
                PopupMenu popup = new PopupMenu(view.getContext(), view);
                popup.inflate(R.menu.activity_search_menu);
                popup.setOnMenuItemClickListener(menuItem -> {
                    if (menuItem.getItemId() == R.id.search_save_menu) {
                        mViewModel.saveRoutine(mAdapter.getItem(position));
                        getActivity().setResult(Activity.RESULT_OK);
                    }
                    return true;
                });
                popup.show();
            }
        });
    }

    class TeacherOptionsViewHolder {

        private View mOptionViewLayout;
        @BindView(R.id.lst_initial)
        TextInputAutoCompleteTextView mInitialInput;

        @BindView(R.id.lst_initial_layout)
        TextInputLayout mInitialInputLayout;

        @BindView(R.id.lst_search_button)
        MaterialButton mSearchButton;

        TeacherOptionsViewHolder(View view) {
            this.mOptionViewLayout = view;
            ButterKnife.bind(this, mOptionViewLayout);
            setupView();
        }

        private void setupView() {
            mViewModel.getTeachersInitialListListener().observe(getActivity(), listResource -> {
                if (listResource != null) {
                    switch (listResource.getStatus()) {
                        case LOADING:
                            disableInitialLayout();
                            break;
                        case ERROR:
                            disableInitialLayout();
                            break;
                        case SUCCESSFUL:
                            CustomFilterArrayAdapter arrayAdapter = new CustomFilterArrayAdapter(getActivity(), R.layout.spinner_row_zero, listResource.getData());
                            enableInitialLayout();
                            mInitialInput.setAdapter(arrayAdapter);
                            break;
                    }
                }
            });

            mSearchButton.setOnClickListener(v ->
                    mViewModel.searchClassesByInitial(mInitialInput.getText().toString()));

        }

        private void disableInitialLayout() {
            mInitialInput.setEnabled(false);
            mInitialInputLayout.setEnabled(false);
            mSearchButton.setEnabled(false);

        }

        private void enableInitialLayout() {
            mInitialInput.setEnabled(true);
            mInitialInputLayout.setEnabled(true);
            mSearchButton.setEnabled(true);
        }

    }


}
