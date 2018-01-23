package bd.edu.daffodilvarsity.classorganizer.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import javax.xml.transform.Result;

import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.adapter.DayDataAdapter;
import bd.edu.daffodilvarsity.classorganizer.data.DayData;
import bd.edu.daffodilvarsity.classorganizer.utils.CourseUtils;
import bd.edu.daffodilvarsity.classorganizer.utils.MasterDBOffline;
import bd.edu.daffodilvarsity.classorganizer.utils.PrefManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragmentTeacher#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragmentTeacher extends Fragment {

    private static final String TAG = "SearchFragmentRoom";

    private View mView;
    private CardView mOptionContainer;
    private CardView mResultContainer;
    private View mOptionLayout;
    private View mResultLayout;

    //Room vars
    private Spinner mSearchTeacherSpinner;
    private TextView resultTitle;
    private DayDataAdapter adapter;

    public SearchFragmentTeacher() {
        // Required empty public constructor
    }

    public static SearchFragmentTeacher newInstance() {
        return new SearchFragmentTeacher();
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
        setupOptionContainer();
        if (mOptionLayout == null) {
            setupOptionLayout(inflater);
        }
        if (mResultLayout == null) {
            setupResultLayout(inflater);
        }
        // Inflate the layout for this fragment
        return mView;
    }



    private void setupOptionContainer() {
        mOptionContainer = mView.findViewById(R.id.search_options_container);
    }

    private void setupOptionLayout(@NonNull LayoutInflater inflater) {
        mOptionLayout = inflater.inflate(R.layout.layout_search_option_teacher, mOptionContainer, true);
        mSearchTeacherSpinner = mOptionLayout.findViewById(R.id.search_option_teacher_spinner);
        //set adapters on the spinners we can
        PrefManager prefManager = new PrefManager(mOptionLayout.getContext());
        ArrayList<String> teachersInitials = CourseUtils.getInstance(mOptionLayout.getContext()).getTeachersInitials(prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram());
        mSearchTeacherSpinner.setAdapter(new ArrayAdapter<String>(mOptionLayout.getContext(),
                R.layout.spinner_row, teachersInitials));

    }

    private void setupResultLayout(LayoutInflater inflater) {
        mResultContainer = mView.findViewById(R.id.search_results_container);
        resultTitle = mView.findViewById(R.id.result_layout_title);
        mResultLayout = inflater.inflate(R.layout.layout_search_result, mResultContainer, true);
        RecyclerView recyclerView = (RecyclerView) mResultLayout.findViewById(R.id.class_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mResultLayout.getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DayDataAdapter(mView.getContext(), R.layout.list_item);
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);
        //hiding result section on first time SUPPRRRISSE MADAPAKA
        showResultLayout(false);
    }

    private void setupFAB() {
        FloatingActionButton fab = mView.findViewById(R.id.search_fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchByInitial();
                }
            });
        }
    }

    private void searchByInitial() {
        if (mSearchTeacherSpinner != null) {
            showResultLayout(true);
            CourseUtils courseUtils = CourseUtils.getInstance(mOptionContainer.getContext());
            PrefManager prefManager = new PrefManager(mOptionContainer.getContext());
            String initial = mSearchTeacherSpinner.getSelectedItem().toString();
            ArrayList<DayData> result = courseUtils.getDayDataByQuery(prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram(), initial, MasterDBOffline.COLUMN_TEACHERS_INITIAL);
            if (result.size() == 0) {
                noResult(true);
                Log.e(TAG, "ZERO");
            } else {
                noResult(false);
                Log.e(TAG, "OKA");
                adapter.loadResult(result);
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

    private void showResultLayout(boolean yes) {
        if (yes) {
            if (resultTitle != null && resultTitle.getVisibility() == View.GONE) {
                resultTitle.setVisibility(View.VISIBLE);
            }
            if (mResultLayout != null && mResultLayout.getVisibility() == View.GONE) {
                mResultLayout.setVisibility(View.VISIBLE);
            }
        } else {
            if (resultTitle != null && resultTitle.getVisibility() == View.VISIBLE) {
                resultTitle.setVisibility(View.GONE);
            }
            if (mResultLayout != null && mResultLayout.getVisibility() == View.VISIBLE) {
                mResultLayout.setVisibility(View.GONE);
            }
        }

    }


}
