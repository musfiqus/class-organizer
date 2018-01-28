package bd.edu.daffodilvarsity.classorganizer.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.activity.DayDataDetailActivity;
import bd.edu.daffodilvarsity.classorganizer.adapter.DayDataAdapter;
import bd.edu.daffodilvarsity.classorganizer.data.DayData;
import bd.edu.daffodilvarsity.classorganizer.utils.PrefManager;
import bd.edu.daffodilvarsity.classorganizer.utils.RoutineLoader;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragmentRoutine#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragmentRoutine extends Fragment implements DayDataAdapter.DayListItemClickListener {

    private static final String TAG = "SearchFragmentRoutine";

    private View mView;
    private CardView mOptionView;
    private CardView mResultView;
    private View mOptionLayout;
    private View mResultLayout;

    //Routine vars
    private EditText searchInput;
    private CheckBox searchByCode;
    private CheckBox searchByTitle;
    private CheckBox searchByTeacher;
    private CheckBox searchByRoom;
    private DayDataAdapter adapter;
    private TextView resultTitle;

    public SearchFragmentRoutine() {
        // Required empty public constructor
    }

    public static SearchFragmentRoutine newInstance() {
        return new SearchFragmentRoutine();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_search, container, false);
        }
        //Set container
        setupFAB();
        if (mOptionView == null) {
            setupOptionContainer();
        }
        if (mOptionLayout == null) {
            setupOptionLayout(inflater);
        }
        if (mResultLayout == null) {
            setupResultLayout(inflater);
        }
        // Inflate the layout for this fragment
        return mView;
    }

    private void setupResultLayout(LayoutInflater inflater) {
        mResultView = mView.findViewById(R.id.search_results_container);
        resultTitle = mView.findViewById(R.id.result_layout_title);
        mResultLayout = inflater.inflate(R.layout.layout_search_result, mResultView, true);

        PrefManager prefManager = new PrefManager(mView.getContext());
        RoutineLoader routineLoader;
        if (prefManager.isUserStudent()) {
            routineLoader = new RoutineLoader(prefManager.getLevel(), prefManager.getTerm(), prefManager.getSection(), mView.getContext(), prefManager.getDept(), prefManager.getCampus(), prefManager.getProgram());
        } else {
            routineLoader = new RoutineLoader(prefManager.getTeacherInitial(), prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram(), mView.getContext());
        }
        RecyclerView recyclerView = (RecyclerView) mResultLayout.findViewById(R.id.class_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mResultLayout.getContext());
        recyclerView.setLayoutManager(layoutManager);
        ArrayList<DayData> loadedRoutine = routineLoader.loadRoutine(true);
        adapter = new DayDataAdapter(loadedRoutine, mView.getContext(), R.layout.list_item, DayDataAdapter.HOLDER_ROOM, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);
        filterAdapter();
        //hiding result section on first time SUPPRRRISSE MADAPAKA
        if (resultTitle != null) {
            resultTitle.setVisibility(View.GONE);
        }
        if (mResultLayout != null) {
            mResultLayout.setVisibility(View.GONE);
        }

    }

    private void setupOptionContainer() {
        mOptionView = mView.findViewById(R.id.search_options_container);


    }

    private void setupOptionLayout(@NonNull LayoutInflater inflater) {
        mOptionLayout = inflater.inflate(R.layout.layout_search_option_routine, mOptionView, true);
        searchByCode = mOptionLayout.findViewById(R.id.search_by_code_check);
        searchByTitle = mOptionLayout.findViewById(R.id.search_by_title_check);
        searchByTeacher = mOptionLayout.findViewById(R.id.search_by_teacher_check);
        searchByRoom = mOptionLayout.findViewById(R.id.search_by_room_check);
        searchInput = mOptionLayout.findViewById(R.id.search_routine_input);
        final AppCompatButton advancedButton = mOptionLayout.findViewById(R.id.search_advanced_button_routine);
        final RelativeLayout advancedLayout = mOptionLayout.findViewById(R.id.search_routine_advanced_layout);
        //Coloring advanced button
        advancedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (advancedLayout != null) {
                    if (advancedLayout.getVisibility() == View.GONE) {
                        advancedButton.setText(R.string.hide);
                        advancedLayout.setVisibility(View.VISIBLE);
                    } else {
                        advancedButton.setText(R.string.advanced);
                        advancedLayout.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void setupFAB() {
        FloatingActionButton fab = mView.findViewById(R.id.search_fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (validateInput()) {
                        filterAdapter();
                    }
                }
            });
        }
    }

    private boolean validateInput() {
        boolean valid = true;

        if (searchInput == null) {
            return false;
        }
        String searchPhrase = searchInput.getText().toString();
        if (searchPhrase.isEmpty()) {
            searchInput.setError("enter a keyword");
            valid = false;
        } else {
            searchInput.setError(null);
        }

        return valid;
    }

    private void filterAdapter() {
        if (adapter != null && searchInput != null && searchByCode != null &&
                searchByTitle != null && searchByTeacher != null && searchByRoom != null) {
            String query = searchInput.getText().toString();
            boolean searchByCode = this.searchByCode.isChecked();
            boolean searchByTitle = this.searchByTitle.isChecked();
            boolean searchByTeacher = this.searchByTeacher.isChecked();
            boolean searchByRoom = this.searchByRoom.isChecked();
            adapter.filter(query, searchByCode, searchByTitle, searchByTeacher, searchByRoom);
            if (resultTitle != null && resultTitle.getVisibility() == View.GONE) {
                resultTitle.setVisibility(View.VISIBLE);
            }
            if (mResultLayout != null && mResultLayout.getVisibility() == View.GONE) {
                mResultLayout.setVisibility(View.VISIBLE);
            }
            if (adapter.dayDataSize() == 0) {
                noResult(true);
            }
        }
    }

    private void noResult(boolean result) {
        TextView noResult = mResultLayout.findViewById(R.id.search_routine_result_zero);
        if (noResult != null) {
            if (result) {
                noResult.setVisibility(View.VISIBLE);
            } else {
                noResult.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public void onDayItemClick(int pos, DayData dayData, DayDataAdapter.DayDataHolder holder) {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), DayDataDetailActivity.class);
            intent.putExtra(DayDataDetailActivity.DAYDATA_DETAIL_TAG, (Parcelable) dayData);
            startActivity(intent);
        }
    }
}
