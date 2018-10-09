package bd.edu.daffodilvarsity.classorganizer.ui.search;


import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.model.Routine;
import bd.edu.daffodilvarsity.classorganizer.model.Teacher;
import bd.edu.daffodilvarsity.classorganizer.ui.TextInputAutoCompleteTextView;
import bd.edu.daffodilvarsity.classorganizer.ui.detail.RoutineDetailActivity;
import bd.edu.daffodilvarsity.classorganizer.utils.CustomFilterArrayAdapter;
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
            if (aBoolean == null || !aBoolean) {
                mProgress.setVisibility(View.GONE);
            } else {
                mProgress.setVisibility(View.VISIBLE);
                mProgress.setIndeterminate(true);
            }
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
                            mAdapter.replaceData(listResource.getData());
                        } else {
                            mResultView.setVisibility(View.GONE);
                            mNoResultText.setVisibility(View.VISIBLE);
                            mNoResultText.setText(getString(R.string.no_teachers_found));
                        }
                        break;
                }
            }
        });
        mViewModel.getTeacherInfoListener().observe(getActivity(), teacher -> {
            if (teacher != null) {
                mTeacherList.setVisibility(View.VISIBLE);
                mTeacherList.setLayoutManager(new LinearLayoutManager(getActivity()));
                List<Teacher> teachers = new ArrayList<>();
                teachers.add(teacher);
                SearchTeacherAdapter adapter = new SearchTeacherAdapter(teachers);
                adapter.bindToRecyclerView(mTeacherList);
            } else {
                mTeacherList.setVisibility(View.GONE);
            }
        });

        mAdapter.setOnItemChildClickListener((adapter, view1, position) -> {
            switch (view1.getId()) {
                case R.id.item_class_more_button:
                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(view1.getContext(), view1);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.activity_search_menu);

                    popup.setOnMenuItemClickListener(menuItem -> {
                        switch (menuItem.getItemId()) {
                            case R.id.search_save_menu:
                                mViewModel.saveRoutine((Routine) adapter.getItem(position));
                                getActivity().setResult(Activity.RESULT_OK);
                                break;
                        }
                        return true;
                    });
                    //displaying the popup
                    popup.show();
                    break;
                default:
                    Intent intent = new Intent(getActivity(), RoutineDetailActivity.class);
                    intent.putExtra(RoutineDetailActivity.ROUTINE_DETAIL_TAG, (Parcelable) adapter.getItem(position));
                    startActivity(intent);
                    break;
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
                            CustomFilterArrayAdapter arrayAdapter = new CustomFilterArrayAdapter(getActivity(), R.layout.spinner_row, listResource.getData());
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
