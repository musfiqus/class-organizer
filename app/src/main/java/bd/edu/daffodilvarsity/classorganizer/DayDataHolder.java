package bd.edu.daffodilvarsity.classorganizer;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by musfiqus on 5/20/2017.
 */

public class DayDataHolder extends RecyclerView.ViewHolder {
    private Context context;
    private TextView courseCodeTextView;
    private TextView teachersInitialTextView;
    private TextView roomNoTextView;
    private TextView timeTextView;
    private ImageButton popupButton;
    private PrefManager prefManager;
    private ViewGroup parent;
    private DayData dayData;
    private final View mView;

    public DayDataHolder(Context context, View itemView, ViewGroup parent) {
        super(itemView);
        this.context = context;
        this.courseCodeTextView = (TextView) itemView.findViewById(R.id.course_code);
        this.teachersInitialTextView = (TextView) itemView.findViewById(R.id.teachers_initial);
        this.roomNoTextView = (TextView) itemView.findViewById(R.id.room_no);
        this.timeTextView = (TextView) itemView.findViewById(R.id.schedule);
        this.prefManager = new PrefManager(context);
        this.parent = parent;
        this.mView = itemView;
    }

    public void bindDayData(final DayData dayData) {
        this.dayData = dayData;
        this.courseCodeTextView.setText(this.dayData.getCourseCode());
        this.teachersInitialTextView.setText(this.dayData.getTeachersInitial());
        this.roomNoTextView.setText(this.dayData.getRoomNo());
        this.timeTextView.setText(this.dayData.getTime());
    }

    public View getmView() {
        return mView;
    }
}

