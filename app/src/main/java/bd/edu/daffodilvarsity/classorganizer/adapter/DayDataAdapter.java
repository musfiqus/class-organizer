package bd.edu.daffodilvarsity.classorganizer.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.activity.DayDataDetailActivity;
import bd.edu.daffodilvarsity.classorganizer.data.DayData;

/**
 * Created by musfiqus on 5/20/2017.
 */

public class DayDataAdapter extends RecyclerView.Adapter<DayDataAdapter.DayDataHolder> {

    private final ArrayList<DayData> dayDataArrayList;
    private Context context;
    private int itemResource;

    public DayDataAdapter(ArrayList<DayData> dayDataArrayList, Context context, int itemResource) {
        this.dayDataArrayList = dayDataArrayList;
        this.context = context;
        this.itemResource = itemResource;
    }

    @Override
    public DayDataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(itemResource, parent, false);
        return new DayDataHolder(context, view, parent);
    }

    @Override
    public void onBindViewHolder(final DayDataHolder holder, int position) {
        final DayData dayData = dayDataArrayList.get(position);
        holder.bindDayData(dayData);
        holder.getmView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, DayDataDetailActivity.class);
                intent.putExtra("DayDataDetails", (Parcelable) dayData);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dayDataArrayList.size();
    }

    /**
     * Created by musfiqus on 5/20/2017.
     */

    public static class DayDataHolder extends RecyclerView.ViewHolder {
        private Context context;
        private TextView courseCodeTextView;
        private TextView teachersInitialTextView;
        private TextView roomNoTextView;
        private TextView timeTextView;
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
}
