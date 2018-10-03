package bd.edu.daffodilvarsity.classorganizer.ui.main;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.model.Routine;
import bd.edu.daffodilvarsity.classorganizer.ui.detail.RoutineDetailActivity;
import bd.edu.daffodilvarsity.classorganizer.ui.modify.ModifyActivity;
import bd.edu.daffodilvarsity.classorganizer.utils.ViewUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * A simple {@link Fragment} subclass.
 */
public class DayFragment extends Fragment {
    private static final String TAG = "DayFragment";

    private RoutineAdapter mAdapter;
    private MainViewModel mViewModel;

    @BindView(R.id.class_list)
    RecyclerView mRecyclerView;

    public DayFragment() {
        // Required empty public constructor
    }


    @SuppressWarnings({"unchecked", "ConstantConditions"})
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.class_list, container, false);
        ButterKnife.bind(this, rootView);
        mViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        //Getting data from bundle
        Bundle bundle = getArguments();
        ArrayList<Routine> courseData = bundle.getParcelableArrayList("anyDay");
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        assert courseData != null;
        mAdapter = new RoutineAdapter(courseData);
        mAdapter.setHasStableIds(true);
        mRecyclerView.setHasFixedSize(true);
        mAdapter.bindToRecyclerView(mRecyclerView);
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()) {
                case R.id.item_class_notification_button:
                    Routine originalRoutine = (Routine) adapter.getItem(position);
                    mViewModel.modifyRoutine(originalRoutine);
                    ImageView muteYes = (ImageView) adapter.getViewByPosition(position, R.id.item_class_mute_yes);
                    ImageView muteNo = (ImageView) adapter.getViewByPosition(position, R.id.item_class_mute_no);
                    ViewUtils.animateMute(muteYes, muteNo, !originalRoutine.isMuted());
                    originalRoutine.setMuted(!originalRoutine.isMuted());
                    MaterialProgressBar progressBar = (MaterialProgressBar) adapter.getViewByPosition(position, R.id.item_class_progress);
                    mViewModel.getClassProgressListener().observe(getActivity(), aBoolean -> {
                        if (aBoolean == null || !aBoolean) {
                            progressBar.setVisibility(View.INVISIBLE);
                        } else {
                            progressBar.setVisibility(View.VISIBLE);
                        }
                    });
                    break;
                case R.id.item_class_more_button:
                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(view.getContext(), view);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.item_class_menu);

                    popup.setOnMenuItemClickListener(menuItem -> {
                        switch (menuItem.getItemId()) {
                            case R.id.delete_class:
                                mViewModel.deleteRoutine((Routine) adapter.getItem(position));
                                break;
                            case R.id.edit_class:
                                Intent intent = new Intent(getActivity(), ModifyActivity.class);
                                intent.putExtra(ModifyActivity.KEY_EDIT_OBJECT, (Routine) adapter.getItem(position));
                                startActivityForResult(intent, MainActivity.REQUEST_CODE_REFRESHABLE_ACTIVITY);
                                break;
                        }
                        return false;
                    });
                    //displaying the popup
                    popup.show();
                    break;
                default:
                    Intent intent = new Intent(getActivity(), RoutineDetailActivity.class);
                    intent.putExtra(RoutineDetailActivity.ROUTINE_DETAIL_TAG, (Routine) adapter.getItem(position));
                    startActivity(intent);
                    break;
            }
        });


        return rootView;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (getActivity() != null) {
            MainActivity activity = (MainActivity) getActivity();
            activity.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
