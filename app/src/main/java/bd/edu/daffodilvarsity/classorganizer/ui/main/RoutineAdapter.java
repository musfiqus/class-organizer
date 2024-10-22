package bd.edu.daffodilvarsity.classorganizer.ui.main;


import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.Guideline;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.model.Routine;
import bd.edu.daffodilvarsity.classorganizer.utils.InputHelper;
import bd.edu.daffodilvarsity.classorganizer.utils.PreferenceGetter;
import bd.edu.daffodilvarsity.classorganizer.utils.ViewUtils;

public class RoutineAdapter extends RecyclerView.Adapter<RoutineAdapter.RoutineViewHolder> {
    private final List<Routine> routineList;
    private final boolean isStudent;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position, Routine routine);
    }

    public RoutineAdapter(List<Routine> routineList) {
        this.routineList = routineList;
        this.isStudent = PreferenceGetter.isStudent();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RoutineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_class, parent, false);
        return new RoutineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoutineViewHolder holder, int position) {
        Routine item = routineList.get(position);
        holder.bind(item, position);
    }

    @Override
    public int getItemCount() {
        return routineList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public Routine getItem(int position) {
        return routineList.get(position);
    }

    class RoutineViewHolder extends RecyclerView.ViewHolder {
        private final TextView codeText;
        private final TextView titleText;
        private final TextView timeText;
        private final TextView teacherText;
        private final TextView teacherTitleText;
        private final TextView roomText;
        private final ImageView muteYes;
        private final ImageView muteNo;
        private final LinearLayout codeHolder;
        private final Guideline guideline;
        private final View detailsButton;
        private final View notificationButton;
        private final View moreButton;
        private final View holderView;

        RoutineViewHolder(@NonNull View itemView) {
            super(itemView);
            codeText = itemView.findViewById(R.id.item_class_code);
            titleText = itemView.findViewById(R.id.item_class_title);
            timeText = itemView.findViewById(R.id.item_class_time);
            teacherText = itemView.findViewById(R.id.item_class_teacher);
            teacherTitleText = itemView.findViewById(R.id.item_class_teacher_title);
            roomText = itemView.findViewById(R.id.item_class_room);
            muteYes = itemView.findViewById(R.id.item_class_mute_yes);
            muteNo = itemView.findViewById(R.id.item_class_mute_no);
            codeHolder = itemView.findViewById(R.id.item_class_code_holder);
            guideline = itemView.findViewById(R.id.item_class_guideline);
            detailsButton = itemView.findViewById(R.id.item_class_details_button);
            notificationButton = itemView.findViewById(R.id.item_class_notification_button);
            moreButton = itemView.findViewById(R.id.item_class_more_button);
            holderView = itemView.findViewById(R.id.item_class_holder);
        }

        void bind(Routine item, int position) {
            // Set guideline position based on screen density
            androidx.constraintlayout.widget.ConstraintLayout.LayoutParams params =
                    (androidx.constraintlayout.widget.ConstraintLayout.LayoutParams) guideline.getLayoutParams();
            if (ViewUtils.pxFromDp(1) <= 2f) {
                params.guidePercent = 0.40f;
                guideline.setLayoutParams(params);
            }

            // Set code holder background
            Drawable timeBg = AppCompatResources.getDrawable(itemView.getContext(), R.drawable.time_bg_layer);
            codeHolder.setBackground(timeBg);
            ViewCompat.setBackgroundTintList(codeHolder,
                    ColorStateList.valueOf(ViewUtils.fetchAccentColor(itemView.getContext())));

            // Set text fields
            codeText.setText(InputHelper.toNA(item.getCourseCode()));
            titleText.setText(InputHelper.toNA(item.getCourseTitle()));
            timeText.setText(item.getTime());
            roomText.setText(item.getRoomNo());

            if (isStudent) {
                teacherText.setText(InputHelper.toNA(item.getTeachersInitial()));
            } else {
                teacherTitleText.setText(R.string.section);
                teacherText.setText(InputHelper.toNA(item.getSection()));
            }

            // Handle first item margin
            if (position == 0) {
                ViewGroup.MarginLayoutParams layoutParams =
                        (ViewGroup.MarginLayoutParams) itemView.getLayoutParams();
                if (layoutParams != null) {
                    layoutParams.topMargin = (int) ViewUtils.pxFromDp(16);
                    itemView.setLayoutParams(layoutParams);
                }
            }

            // Set mute state
            ViewUtils.animateMute(muteYes, muteNo, item.isMuted());

            // Set click listeners
            View.OnClickListener clickListener = v -> {
                if (listener != null) {
                    listener.onItemClick(v, getAdapterPosition(), item);
                }
            };

            detailsButton.setOnClickListener(clickListener);
            notificationButton.setOnClickListener(clickListener);
            moreButton.setOnClickListener(clickListener);
            holderView.setOnClickListener(clickListener);
        }
    }
}
