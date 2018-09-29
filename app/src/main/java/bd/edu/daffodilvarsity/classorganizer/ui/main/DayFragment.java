package bd.edu.daffodilvarsity.classorganizer.ui.main;


import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;

import bd.edu.daffodilvarsity.classorganizer.ClassOrganizer;
import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.ui.detail.RoutineDetailActivity;
import bd.edu.daffodilvarsity.classorganizer.model.Routine;
import bd.edu.daffodilvarsity.classorganizer.model.DayData;
import bd.edu.daffodilvarsity.classorganizer.utils.PrefManager;
import bd.edu.daffodilvarsity.classorganizer.utils.ViewUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class DayFragment extends Fragment {
    private static final String TAG = "DayFragment";

    private RoutineAdapter adapter;

    public DayFragment() {
        // Required empty public constructor
    }


    @SuppressWarnings({"unchecked", "ConstantConditions"})
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.class_list, container, false);
        //Getting data from bundle
        Bundle bundle = getArguments();
        ArrayList<Routine> courseData = bundle.getParcelableArrayList("anyDay");
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.class_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        assert courseData != null;
        adapter = new RoutineAdapter(courseData);
        recyclerView.setHasFixedSize(true);
        adapter.bindToRecyclerView(recyclerView);
        adapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()) {
                case R.id.item_class_details_button:
                    Intent intent = new Intent(getActivity(), RoutineDetailActivity.class);
                    intent.putExtra(RoutineDetailActivity.ROUTINE_DETAIL_TAG, (Routine) adapter.getItem(position));
                    getActivity().startActivity(intent);
                    break;
                case  R.id.item_class_notification_button:
                    Routine routine = (Routine) adapter.getItem(position);
                    if (routine != null) {
                        routine.setMuted(!routine.isMuted());
                        ImageView muteYes = (ImageView) adapter.getViewByPosition(position, R.id.item_class_mute_yes);
                        ImageView muteNo = (ImageView) adapter.getViewByPosition(position, R.id.item_class_mute_no);
                        ViewUtils.animateMute(muteYes, muteNo, routine.isMuted());
                        adapter.setData(position, routine);
                    }

                    break;
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
