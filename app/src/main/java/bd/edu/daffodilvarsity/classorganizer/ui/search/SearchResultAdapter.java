package bd.edu.daffodilvarsity.classorganizer.ui.search;

import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.design.card.MaterialCardView;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.model.Routine;
import bd.edu.daffodilvarsity.classorganizer.utils.ViewUtils;

public class SearchResultAdapter extends BaseQuickAdapter<Routine, BaseViewHolder> {
    public static final int RESULT_TYPE_TEACHER = 6943;
    public static final int RESULT_TYPE_SECTION_CLASS = 6942;
    public static final int RESULT_TYPE_TEACHER_CLASS = 6944;
    private int viewType;
    public SearchResultAdapter(@Nullable List<Routine> data, int viewType) {
        super(R.layout.item_class, data);
        this.viewType = viewType;
    }

    @Override
    protected void convert(BaseViewHolder helper, Routine item) {
        if (viewType == RESULT_TYPE_SECTION_CLASS) {
            helper.setText(R.id.item_class_code, item.getCourseCode());
            helper.setText(R.id.item_class_title, item.getCourseTitle());
            helper.setText(R.id.routine_detail_time, item.getTime());
            helper.setText(R.id.item_class_teacher, item.getTeachersInitial());
            helper.setText(R.id.item_class_room_title, R.string.day);
            helper.setText(R.id.item_class_room, item.getDay());
            MaterialButton detailsButton = helper.getView(R.id.item_class_details_button);
            if (helper.getAdapterPosition() == 0) {
                MaterialCardView cardView = (MaterialCardView) helper.itemView;
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)cardView.getLayoutParams();
                layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin + (int)ViewUtils.pxFromDp(8), layoutParams.rightMargin, layoutParams.bottomMargin);
                cardView.setLayoutParams(layoutParams);
            }

            detailsButton.setOnClickListener(v -> {

            });
        }
    }
}
