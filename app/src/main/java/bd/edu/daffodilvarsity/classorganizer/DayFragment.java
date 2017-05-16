package bd.edu.daffodilvarsity.classorganizer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class DayFragment extends Fragment {
    private DayDataAdapter adapter;

    public DayFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.class_list, container, false);

        //Getting data from bundle
        Bundle bundle = getArguments();
        ArrayList<DayData> courseData = bundle.getParcelableArrayList("anyDay");

        assert courseData != null;
        adapter = new DayDataAdapter(getActivity(), courseData);
        ListView listView = (ListView) rootView.findViewById(R.id.class_list);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}
