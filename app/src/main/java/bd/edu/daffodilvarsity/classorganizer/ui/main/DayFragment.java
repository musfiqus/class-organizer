package bd.edu.daffodilvarsity.classorganizer.ui.main;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
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
    private ActivityResultLauncher<Intent> modifyActivityLauncher;

    @BindView(R.id.class_list)
    RecyclerView mRecyclerView;

    public DayFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Register the ActivityResultLauncher
        modifyActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (getActivity() != null) {
                        MainActivity activity = (MainActivity) getActivity();
                        activity.onActivityResult(
                                MainActivity.REQUEST_CODE_REFRESHABLE_ACTIVITY,
                                result.getResultCode(),
                                result.getData()
                        );
                    }
                }
        );
    }

    @SuppressWarnings({"ConstantConditions"})
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.class_list, container, false);
        ButterKnife.bind(this, rootView);
        mViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);

        // Getting data from bundle
        Bundle bundle = getArguments();
        ArrayList<Routine> courseData = bundle.getParcelableArrayList("anyDay");
        assert courseData != null;

        // Setup RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        // Setup Adapter
        mAdapter = new RoutineAdapter(courseData);
        mAdapter.setHasStableIds(true);
        mRecyclerView.setAdapter(mAdapter);

        // Setup click listener
        mAdapter.setOnItemClickListener((view, position, routine) -> {
            int viewId = view.getId();
            if (viewId == R.id.item_class_notification_button) {
                mViewModel.modifyRoutine(routine);
                View itemView = mRecyclerView.findViewHolderForAdapterPosition(position).itemView;
                ImageView muteYes = itemView.findViewById(R.id.item_class_mute_yes);
                ImageView muteNo = itemView.findViewById(R.id.item_class_mute_no);
                ViewUtils.animateMute(muteYes, muteNo, !routine.isMuted());
                routine.setMuted(!routine.isMuted());
                MaterialProgressBar progressBar = itemView.findViewById(R.id.item_class_progress);

                mViewModel.getClassProgressListener().observe(getActivity(), aBoolean -> {
                    if (aBoolean == null || !aBoolean) {
                        progressBar.setVisibility(View.INVISIBLE);
                    } else {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                });
            } else if (viewId == R.id.item_class_more_button) {
                PopupMenu popup = getPopupMenu(view, routine);
                popup.show();
            } else {
                Intent intent = new Intent(getActivity(), RoutineDetailActivity.class);
                intent.putExtra(RoutineDetailActivity.ROUTINE_DETAIL_TAG, (Parcelable) routine);
                startActivity(intent);
            }
        });

        return rootView;
    }

    private PopupMenu getPopupMenu(View view, Routine routine) {
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        popup.inflate(R.menu.item_class_menu);
        popup.setOnMenuItemClickListener(menuItem -> {
            int itemId = menuItem.getItemId();
            if (itemId == R.id.delete_class) {
                mViewModel.deleteRoutine(routine);
            } else if (itemId == R.id.edit_class) {
                Intent intent = new Intent(getActivity(), ModifyActivity.class);
                intent.putExtra(ModifyActivity.KEY_EDIT_OBJECT, (Parcelable) routine);
                modifyActivityLauncher.launch(intent);
            }
            return false;
        });
        return popup;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
