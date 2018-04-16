package bd.edu.daffodilvarsity.classorganizer.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.activity.DayDataDetailActivity;
import bd.edu.daffodilvarsity.classorganizer.adapter.DayDataAdapter;
import bd.edu.daffodilvarsity.classorganizer.data.DayData;
import bd.edu.daffodilvarsity.classorganizer.utils.PrefManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class DayFragment extends Fragment implements DayDataAdapter.DayListItemClickListener {
    PrefManager prefManager;
    private DayDataAdapter adapter;

    public DayFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.class_list, container, false);
        prefManager = new PrefManager(getContext());
        //Getting data from bundle
        Bundle bundle = getArguments();
        ArrayList<DayData> courseData = bundle.getParcelableArrayList("anyDay");
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.class_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        assert courseData != null;
        adapter = new DayDataAdapter(courseData, getContext(), R.layout.list_item, DayDataAdapter.HOLDER_ROUTINE, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
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
