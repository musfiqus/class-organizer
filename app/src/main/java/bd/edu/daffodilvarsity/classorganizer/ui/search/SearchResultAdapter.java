package bd.edu.daffodilvarsity.classorganizer.ui.search;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Guideline;
import android.support.design.button.MaterialButton;
import android.support.design.card.MaterialCardView;
import android.support.v4.view.ViewCompat;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.model.Routine;
import bd.edu.daffodilvarsity.classorganizer.utils.InputHelper;
import bd.edu.daffodilvarsity.classorganizer.utils.ViewUtils;

public class SearchResultAdapter extends BaseQuickAdapter<Routine, BaseViewHolder> {
    public static final int RESULT_TYPE_TEACHER = 6943;
    public static final int RESULT_TYPE_SECTION_CLASS = 6942;
    public static final int RESULT_TYPE_TEACHER_CLASS = 6944;
    public static final int RESULT_TYPE_FREE_CLASS = 6945;
    private int viewType;
    public SearchResultAdapter(@Nullable List<Routine> data, int viewType) {
        super(R.layout.item_class, data);
        this.viewType = viewType;
    }

    @Override
    protected void convert(BaseViewHolder helper, Routine item) {
        Guideline guideline = helper.getView(R.id.item_class_guideline);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) guideline.getLayoutParams();
        if (ViewUtils.pxFromDp(1) <= 2f) {
            params.guidePercent = 0.40f; // 45% // range: 0 <-> 1
            guideline.setLayoutParams(params);
        }
        LinearLayout codeHolder = helper.getView(R.id.item_class_code_holder);
        Drawable timeBg = AppCompatResources.getDrawable(helper.itemView.getContext(), R.drawable.time_bg_layer);
        codeHolder.setBackground(timeBg);
        ViewCompat.setBackgroundTintList(codeHolder, ColorStateList.valueOf(ViewUtils.fetchAccentColor(helper.itemView.getContext())));
        switch (viewType) {
            case RESULT_TYPE_SECTION_CLASS:
                helper.setText(R.id.item_class_code, InputHelper.toNA(item.getCourseCode()));
                helper.setText(R.id.item_class_title, InputHelper.toNA(item.getCourseTitle()));
                helper.setText(R.id.item_class_time, item.getTime());
                helper.setText(R.id.item_class_teacher, item.getTeachersInitial());
                helper.setText(R.id.item_class_room_title, R.string.day);
                helper.setText(R.id.item_class_room, item.getDay());
                if (helper.getAdapterPosition() == 0) {
                    MaterialCardView cardView = (MaterialCardView) helper.itemView;
                    RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)cardView.getLayoutParams();
                    layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin + (int)ViewUtils.pxFromDp(8), layoutParams.rightMargin, layoutParams.bottomMargin);
                    cardView.setLayoutParams(layoutParams);
                }
                helper.getView(R.id.item_class_notification_button).setVisibility(View.GONE);
                helper.addOnClickListener(R.id.item_class_details_button)
                        .addOnClickListener(R.id.item_class_holder)
                        .addOnClickListener(R.id.item_class_more_button);
                break;
            case RESULT_TYPE_FREE_CLASS:
                helper.setText(R.id.item_class_title, InputHelper.toNA(item.getRoomNo()));
                helper.getView(R.id.item_class_time_holder).setVisibility(View.GONE);
                helper.getView(R.id.item_class_more_button).setVisibility(View.GONE);
                helper.getView(R.id.item_class_details_button).setVisibility(View.GONE);
                helper.getView(R.id.item_class_notification_button).setVisibility(View.GONE);
                helper.getView(R.id.item_class_teacher).setVisibility(View.GONE);
                helper.getView(R.id.item_class_teacher_title).setVisibility(View.GONE);
                helper.getView(R.id.item_class_room).setVisibility(View.GONE);
                helper.getView(R.id.item_class_room_title).setVisibility(View.GONE);
                MaterialCardView materialCardView = (MaterialCardView) helper.itemView;
                materialCardView.setCardElevation(ViewUtils.pxFromDp(1));
                break;
            case RESULT_TYPE_TEACHER_CLASS:
                helper.setText(R.id.item_class_code, InputHelper.toNA(item.getCourseCode()));
                helper.setText(R.id.item_class_title, InputHelper.toNA(item.getCourseTitle()));
                helper.setText(R.id.item_class_time, item.getTime());
                helper.setText(R.id.item_class_teacher_title, R.string.section);
                helper.setText(R.id.item_class_teacher, item.getSection());
                helper.setText(R.id.item_class_room_title, R.string.day);
                helper.setText(R.id.item_class_room, item.getDay());
                if (helper.getAdapterPosition() == 0) {
                    MaterialCardView cardView = (MaterialCardView) helper.itemView;
                    RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)cardView.getLayoutParams();
                    layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin + (int)ViewUtils.pxFromDp(8), layoutParams.rightMargin, layoutParams.bottomMargin);
                    cardView.setLayoutParams(layoutParams);
                }
                helper.getView(R.id.item_class_notification_button).setVisibility(View.GONE);
                helper.addOnClickListener(R.id.item_class_details_button)
                        .addOnClickListener(R.id.item_class_holder)
                        .addOnClickListener(R.id.item_class_more_button);
                break;
        }
    }
}
