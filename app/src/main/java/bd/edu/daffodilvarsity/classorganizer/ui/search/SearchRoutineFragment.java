package bd.edu.daffodilvarsity.classorganizer.ui.search;


import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import bd.edu.daffodilvarsity.classorganizer.ClassOrganizer;
import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.model.Routine;
import bd.edu.daffodilvarsity.classorganizer.ui.detail.RoutineDetailActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchRoutineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchRoutineFragment extends Fragment {

    private static final String TAG = "SearchRoutineFragment";

    @BindView(R.id.search_fragment_options_card) CardView mOptionsContainer;
    @BindView(R.id.search_fragment_no_result_text) TextView mNoResultText;
    @BindView(R.id.search_fragment_result_list) RecyclerView mResultView;
    @BindView(R.id.search_fragment_progress)
    MaterialProgressBar mProgress;

    private View mView;
    private SearchViewModel mViewModel;
    private SearchResultAdapter mAdapter;



    public SearchRoutineFragment() {
        // Required empty public constructor
    }

    public static SearchRoutineFragment newInstance() {
        return new SearchRoutineFragment();
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
        View view = inflater.inflate(R.layout.layout_search_routine_browser, mOptionsContainer, true);
        mViewModel = ViewModelProviders.of(getActivity()).get(SearchViewModel.class);
        mViewModel.getProgressListener().observe(getActivity(), aBoolean -> {
            if (aBoolean == null || !aBoolean) {
                mProgress.setVisibility(View.GONE);
            } else {
                mProgress.setVisibility(View.VISIBLE);
            }
        });
        RoutineOptionsViewHolder holder = new RoutineOptionsViewHolder(view);
        mAdapter = new SearchResultAdapter(new ArrayList<>(), SearchResultAdapter.RESULT_TYPE_SECTION_CLASS);
        mAdapter.bindToRecyclerView(mResultView);
        mResultView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mViewModel.getRoutineListListener().observe(getActivity(), listResource -> {
            if (listResource != null) {
                switch (listResource.getStatus()) {
                    case ERROR:
                        mResultView.setVisibility(View.GONE);
                        mNoResultText.setVisibility(View.VISIBLE);
                        mNoResultText.setText(getString(R.string.no_routine_found));
                        break;
                    case LOADING:
                        mNoResultText.setVisibility(View.GONE);
                        mResultView.setVisibility(View.GONE);
                        break;
                    case SUCCESSFUL:
                        mNoResultText.setVisibility(View.GONE);
                        mResultView.setVisibility(View.VISIBLE);
                        mAdapter.replaceData(listResource.getData());
                        break;
                }
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

    class RoutineOptionsViewHolder implements AdapterView.OnItemSelectedListener {
        private int i;

        private View mOptionViewLayout;
        @BindView(R.id.lsrb_level_spinner)
        AppCompatSpinner mLevelSpinner;
        @BindView(R.id.lsrb_term_spinner)
        AppCompatSpinner mTermSpinner;
        @BindView(R.id.lsrb_section_spinner)
        AppCompatSpinner mSectionSpinner;
        @BindView(R.id.lsrb_search_button)
        MaterialButton mSearchButton;

        RoutineOptionsViewHolder(View view) {
            this.mOptionViewLayout = view;
            ButterKnife.bind(this, mOptionViewLayout);
            i = 0;
            setupView();
        }

        private void setupView() {
            ArrayAdapter<CharSequence> levelAdapter = ArrayAdapter.createFromResource(ClassOrganizer.getInstance(), R.array.level_array, R.layout.spinner_row_zero);
            ArrayAdapter<CharSequence> termAdapter = ArrayAdapter.createFromResource(ClassOrganizer.getInstance(), R.array.term_array, R.layout.spinner_row_zero);
            mLevelSpinner.setAdapter(levelAdapter);
            mTermSpinner.setAdapter(termAdapter);
            mLevelSpinner.setOnItemSelectedListener(this);
            mTermSpinner.setOnItemSelectedListener(this);
            mViewModel.getSectionListListener().observe(getActivity(), listResource -> {
                if (listResource != null) {
                    switch (listResource.getStatus()) {
                        case SUCCESSFUL:
                            enableSectionSpinner();
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(ClassOrganizer.getInstance(), R.layout.spinner_row_zero, listResource.getData());
                            adapter.setDropDownViewResource(R.layout.spinner_row_big);
                            mSectionSpinner.setAdapter(adapter);
                            break;
                        case LOADING:
                            disableSectionSpinner();
                            break;
                        case ERROR:
                            disableSectionSpinner();
                            break;
                    }
                }
            });

            mSearchButton.setOnClickListener(v -> {
                if (mLevelSpinner.getSelectedItem() != null &&
                mTermSpinner.getSelectedItem() != null &&
                mSectionSpinner.getSelectedItem() != null) {
                    mViewModel.searchRoutines(
                            mLevelSpinner.getSelectedItemPosition(),
                            mTermSpinner.getSelectedItemPosition(),
                            mSectionSpinner.getSelectedItem().toString()
                    );
                }
            });

        }

        private void disableSectionSpinner() {
            mSectionSpinner.setEnabled(false);
            mSearchButton.setEnabled(false);

        }

        private void enableSectionSpinner() {
            mSectionSpinner.setEnabled(true);
            mSearchButton.setEnabled(true);
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (i >= 1) {
                mViewModel.loadSections(mLevelSpinner.getSelectedItemPosition(), mTermSpinner.getSelectedItemPosition());
            }
            i++;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }


}
