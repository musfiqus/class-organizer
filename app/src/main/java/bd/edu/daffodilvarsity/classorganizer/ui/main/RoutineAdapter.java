package bd.edu.daffodilvarsity.classorganizer.ui.main;

import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Guideline;
import android.support.design.card.MaterialCardView;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.model.Routine;
import bd.edu.daffodilvarsity.classorganizer.utils.InputHelper;
import bd.edu.daffodilvarsity.classorganizer.utils.PreferenceGetter;
import bd.edu.daffodilvarsity.classorganizer.utils.ViewUtils;

public class RoutineAdapter extends BaseQuickAdapter<Routine, BaseViewHolder> {
    private boolean isStudent;
    public RoutineAdapter(@Nullable List<Routine> data) {
        super(R.layout.item_class, data);
        isStudent = PreferenceGetter.isStudent();
    }


    @Override
    protected void convert(BaseViewHolder helper, Routine item) {
        if (ViewUtils.pxFromDp(1) <= 2f) {
            Guideline guideline = helper.getView(R.id.item_class_guideline);
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) guideline.getLayoutParams();
            params.guidePercent = 0.3f; // 45% // range: 0 <-> 1
            guideline.setLayoutParams(params);
        }
        helper.setText(R.id.item_class_code, InputHelper.toNA(item.getCourseCode()));
        helper.setText(R.id.item_class_title, InputHelper.toNA(item.getCourseTitle()));
        helper.setText(R.id.item_class_time, item.getTime());
        if (isStudent) {
            helper.setText(R.id.item_class_teacher, InputHelper.toNA(item.getTeachersInitial()));
        } else {
            helper.setText(R.id.item_class_teacher_title, R.string.section);
            helper.setText(R.id.item_class_teacher, InputHelper.toNA(item.getSection()));
        }

        helper.setText(R.id.item_class_room, item.getRoomNo());
        if (helper.getAdapterPosition() == 0) {
            MaterialCardView cardView = (MaterialCardView) helper.itemView;
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)cardView.getLayoutParams();
            layoutParams.setMargins(layoutParams.leftMargin, (int)ViewUtils.pxFromDp(16), layoutParams.rightMargin, layoutParams.bottomMargin);
            cardView.setLayoutParams(layoutParams);
        }
        ViewUtils.animateMute(helper.getView(R.id.item_class_mute_yes), helper.getView(R.id.item_class_mute_no), item.isMuted());
        helper.addOnClickListener(R.id.item_class_details_button)
                .addOnClickListener(R.id.item_class_notification_button)
                .addOnClickListener(R.id.item_class_holder)
                .addOnClickListener(R.id.item_class_more_button);

    }


}
