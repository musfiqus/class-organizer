package bd.edu.daffodilvarsity.classorganizer.ui.search;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import androidx.core.view.ViewCompat;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.model.Routine;
import bd.edu.daffodilvarsity.classorganizer.utils.InputHelper;
import bd.edu.daffodilvarsity.classorganizer.utils.ViewUtils;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {
    public static final int RESULT_TYPE_TEACHER = 6943;
    public static final int RESULT_TYPE_SECTION_CLASS = 6942;
    public static final int RESULT_TYPE_TEACHER_CLASS = 6944;
    public static final int RESULT_TYPE_FREE_CLASS = 6945;

    private List<Routine> mData;
    private int viewType;
    private OnItemClickListener mListener;

    public SearchResultAdapter(List<Routine> data, int viewType) {
        this.mData = data;
        this.viewType = viewType;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Routine item = mData.get(position);
        holder.bind(item, viewType, position);
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    public void setData(List<Routine> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public Routine getItem(int position) {
        if (mData != null && position >= 0 && position < mData.size()) {
            return mData.get(position);
        }
        return null; // Handle case where position is invalid
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final Guideline guideline;
        private final LinearLayout codeHolder;
        private final CardView cardView;
        private final TextView classCode, classTitle, classTime, classTeacherTitle, classTeacher, classRoom, classRoomTitle;

        private final FrameLayout notificationButton;

        public ViewHolder(View itemView) {
            super(itemView);
            guideline = itemView.findViewById(R.id.item_class_guideline);
            codeHolder = itemView.findViewById(R.id.item_class_code_holder);
            cardView = (CardView) itemView;
            classCode = itemView.findViewById(R.id.item_class_code);
            classTitle = itemView.findViewById(R.id.item_class_title);
            classTime = itemView.findViewById(R.id.item_class_time);
            classTeacherTitle = itemView.findViewById(R.id.item_class_teacher_title);
            classTeacher = itemView.findViewById(R.id.item_class_teacher);
            classRoom = itemView.findViewById(R.id.item_class_room);
            classRoomTitle = itemView.findViewById(R.id.item_class_room_title);
            notificationButton = itemView.findViewById(R.id.item_class_notification_button);

            itemView.findViewById(R.id.item_class_details_button).setOnClickListener(v -> {
                if (mListener != null) mListener.onDetailsClick(getAdapterPosition());
            });
            itemView.findViewById(R.id.item_class_holder).setOnClickListener(v -> {
                if (mListener != null) mListener.onItemClick(getAdapterPosition());
            });
            itemView.findViewById(R.id.item_class_more_button).setOnClickListener(v -> {
                if (mListener != null) mListener.onMoreClick(getAdapterPosition(), v);
            });
        }

        public void bind(Routine item, int viewType, int position) {
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) guideline.getLayoutParams();
            params.guidePercent = 0.40f; // Adjust this as needed
            guideline.setLayoutParams(params);

            Drawable timeBg = AppCompatResources.getDrawable(itemView.getContext(), R.drawable.time_bg_layer);
            codeHolder.setBackground(timeBg);
            ViewCompat.setBackgroundTintList(codeHolder, ColorStateList.valueOf(ViewUtils.fetchAccentColor(itemView.getContext())));

            switch (viewType) {
                case RESULT_TYPE_SECTION_CLASS:
                    classCode.setText(InputHelper.toNA(item.getCourseCode()));
                    classTitle.setText(InputHelper.toNA(item.getCourseTitle()));
                    classTime.setText(item.getTime());
                    classTeacher.setText(item.getTeachersInitial());
                    classRoomTitle.setText(R.string.day);
                    classRoom.setText(item.getDay());
                    notificationButton.setVisibility(View.GONE);
                    break;
                case RESULT_TYPE_FREE_CLASS:
                    classTitle.setText(InputHelper.toNA(item.getRoomNo()));
                    itemView.findViewById(R.id.item_class_time_holder).setVisibility(View.GONE);
                    itemView.findViewById(R.id.item_class_more_button).setVisibility(View.GONE);
                    itemView.findViewById(R.id.item_class_details_button).setVisibility(View.GONE);
                    itemView.findViewById(R.id.item_class_notification_button).setVisibility(View.GONE);
                    itemView.findViewById(R.id.item_class_teacher).setVisibility(View.GONE);
                    itemView.findViewById(R.id.item_class_teacher_title).setVisibility(View.GONE);
                    itemView.findViewById(R.id.item_class_room).setVisibility(View.GONE);
                    itemView.findViewById(R.id.item_class_room_title).setVisibility(View.GONE);
                    classRoom.setVisibility(View.GONE);
                    break;
                case RESULT_TYPE_TEACHER_CLASS:
                    classCode.setText(InputHelper.toNA(item.getCourseCode()));
                    classTitle.setText(InputHelper.toNA(item.getCourseTitle()));
                    classTime.setText(item.getTime());
                    classTeacherTitle.setText(R.string.section);
                    classTeacher.setText(item.getSection());
                    classRoomTitle.setText(R.string.day);
                    classRoom.setText(item.getDay());
                    notificationButton.setVisibility(View.GONE);
                    break;
            }

            if (position == 0) {
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) cardView.getLayoutParams();
                layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin + (int) ViewUtils.pxFromDp(8), layoutParams.rightMargin, layoutParams.bottomMargin);
                cardView.setLayoutParams(layoutParams);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDetailsClick(int position);
        void onMoreClick(int position, View view);
    }
}
