package bd.edu.daffodilvarsity.classorganizer.ui.search;

import android.content.Intent;
import android.net.Uri;
import androidx.annotation.Nullable;
import com.google.android.material.button.MaterialButton;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.model.Teacher;
import bd.edu.daffodilvarsity.classorganizer.utils.InputHelper;

public class SearchTeacherAdapter extends BaseQuickAdapter<Teacher, BaseViewHolder> {

    public SearchTeacherAdapter(@Nullable List<Teacher> data) {
        super(R.layout.item_teacher, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Teacher item) {
        helper.setText(R.id.item_teacher_name, InputHelper.toNA(item.getName()));
        helper.setText(R.id.item_teacher_department, InputHelper.toNA(item.getDepartment()));
        helper.setText(R.id.item_teacher_designation, InputHelper.toNA(item.getDesignation()));
        helper.setText(R.id.item_teacher_room, InputHelper.toNA(item.getRoomNo()));
        helper.setText(R.id.item_teacher_faculty, InputHelper.toNA(item.getFaculty()));
        helper.setText(R.id.item_teacher_email, InputHelper.toNA(item.getEmail()));
        helper.setText(R.id.item_teacher_phone, InputHelper.toNA(item.getPhoneNo()));
        MaterialButton contributeButton = helper.getView(R.id.item_teacher_contribute);
        contributeButton.setOnClickListener(v -> {
            String url = "http://bit.ly/2LjFdnN";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            contributeButton.getContext().startActivity(i);

        });

    }
}
