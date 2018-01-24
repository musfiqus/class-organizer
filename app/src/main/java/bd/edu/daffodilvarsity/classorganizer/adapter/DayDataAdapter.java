package bd.edu.daffodilvarsity.classorganizer.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.activity.DayDataDetailActivity;
import bd.edu.daffodilvarsity.classorganizer.data.DayData;

/**
 * Created by Mushfiqus Salehin on 5/20/2017.
 * musfiqus@gmail.com
 */

public class DayDataAdapter extends RecyclerView.Adapter<DayDataAdapter.DayDataHolder> {

    private ArrayList<DayData> dayDataArrayList;
    private final ArrayList<DayData> copyOfDayDataList;
    private Context context;
    private int itemResource;
    private DayListItemClickListener onItemClickListener;

    public DayDataAdapter(ArrayList<DayData> dayDataArrayList, Context context, int itemResource, DayListItemClickListener onItemClickListener) {
        this.dayDataArrayList = dayDataArrayList;
        this.context = context;
        this.itemResource = itemResource;
        this.copyOfDayDataList = new ArrayList<>();
        this.copyOfDayDataList.addAll(dayDataArrayList);
        this.onItemClickListener = onItemClickListener;
    }

    public DayDataAdapter(ArrayList<DayData> dayDataArrayList, Context context, int itemResource) {
        this.dayDataArrayList = dayDataArrayList;
        this.context = context;
        this.itemResource = itemResource;
        this.copyOfDayDataList = new ArrayList<>();
        this.copyOfDayDataList.addAll(dayDataArrayList);
    }

    public DayDataAdapter(Context context, int itemResource) {
        this.dayDataArrayList = new ArrayList<>();
        this.context = context;
        this.itemResource = itemResource;
        this.copyOfDayDataList = new ArrayList<>();
        this.copyOfDayDataList.addAll(dayDataArrayList);
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
        if (onItemClickListener != null) {
            holder.getMView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onDayItemClick(holder.getAdapterPosition(), dayData, holder);
                }
            });
        }

    }


    @Override
    public int getItemCount() {
        return dayDataArrayList.size();
    }

    public void filter(String query, boolean courseCode, boolean courseTitle, boolean teacher, boolean room) {
        dayDataArrayList.clear();
        if (query == null || query.isEmpty()) {
            dayDataArrayList.clear();
        } else {
            query = query.toLowerCase();
            for (DayData eachDay : copyOfDayDataList) {
                if (courseCode) {
                    if (eachDay.getCourseCode().toLowerCase().contains(query)) {
                        dayDataArrayList.add(eachDay);
                    }
                }
                if (courseTitle) {
                    if (!dayDataArrayList.contains(eachDay)) {
                        if (eachDay.getCourseTitle().toLowerCase().contains(query)) {
                            dayDataArrayList.add(eachDay);
                        }
                    }
                }
                if (teacher) {
                    if (!dayDataArrayList.contains(eachDay)) {
                        if (eachDay.getTeachersInitial().toLowerCase().contains(query)) {
                            dayDataArrayList.add(eachDay);
                        }
                    }
                }
                if (room) {
                    if (!dayDataArrayList.contains(eachDay)) {
                        if (eachDay.getRoomNo().toLowerCase().contains(query)) {
                            dayDataArrayList.add(eachDay);
                        }
                    }
                }
            }
        }
        notifyDataSetChanged();
    }
    public void clearDayData() {
        dayDataArrayList.clear();
        notifyDataSetChanged();
    }

    public int dayDataSize() {
        if (dayDataArrayList != null) {
            return dayDataArrayList.size();
        }
        return 0;
    }
    public void loadResult(ArrayList<DayData> result) {
        if (result != null) {
            dayDataArrayList.clear();
            dayDataArrayList.addAll(result);
        }
        notifyDataSetChanged();
    }

    /**
     * Created by musfiqus on 5/20/2017.
     */

    public static class DayDataHolder extends RecyclerView.ViewHolder {
        private Context context;
        public TextView courseCodeTextView;
        public TextView teachersInitialTextView;
        public TextView roomNoTextView;
        public TextView timeTextView;
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
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            boolean isRamadan = preferences.getBoolean("ramadan_preference", false);
            if (isRamadan) {
                this.timeTextView.setText(convertToRamadanTime(this.dayData.getTime(), this.dayData.getTimeWeight()));
            } else {
                this.timeTextView.setText(this.dayData.getTime());
            }

        }

        public View getMView() {
            return mView;
        }

        public static String convertToRamadanTime(String normalTime, double timeWeight) {
            String startTime = normalTime.substring(0, 8);
            String endTime = normalTime.substring(normalTime.length() - 8, normalTime.length());
            if (timeWeight == 1.5 || timeWeight == 2.5 || timeWeight == 3.5 || timeWeight == 4.5) {
                if (startTime.equalsIgnoreCase("09.00 AM")) {
                    startTime = "09.30 AM";
                } else if (startTime.equalsIgnoreCase("11.00 AM")) {
                    startTime = "11.00 AM";
                } else if (startTime.equalsIgnoreCase("01.00 PM")) {
                    startTime = "12.30 PM";
                } else if (startTime.equalsIgnoreCase("03.00 PM")) {
                    startTime = "02.00 PM";
                }
                if (endTime.equalsIgnoreCase("11.00 AM")) {
                    endTime = "11.00 AM";
                } else if (endTime.equalsIgnoreCase("01.00 PM")) {
                    endTime = "12.30 AM";
                } else if (endTime.equalsIgnoreCase("03.00 PM")) {
                    endTime = "02.00 PM";
                } else if (endTime.equalsIgnoreCase("05.00 PM")) {
                    endTime = "03.30 PM";
                }
                return startTime + " - " +endTime;
            }
            if (startTime.equalsIgnoreCase("08.30 AM")) {
                startTime = "09.30 AM";
            } else if (startTime.equalsIgnoreCase("10.00 AM")) {
                startTime = "10.25 AM";
            } else if (startTime.equalsIgnoreCase("11.30 AM")) {
                startTime = "11.20 AM";
            } else if (startTime.equalsIgnoreCase("01.00 PM")) {
                startTime = "12.15 PM";
            } else if (startTime.equalsIgnoreCase("02.30 PM")) {
                startTime = "01.40 PM";
            } else if (startTime.equalsIgnoreCase("04.00 PM")) {
                startTime = "02.35 PM";
            } else if (startTime.equalsIgnoreCase("06.00 PM")) {
                startTime = "03.30 PM";
            } else if (startTime.equalsIgnoreCase("07.30 PM")) {
                startTime = "04.30 PM";
            }

            if (endTime.equalsIgnoreCase("10.00 AM")) {
                endTime = "10.25 AM";
            } else if (endTime.equalsIgnoreCase("11.30 AM")) {
                endTime = "11.20 AM";
            } else if (endTime.equalsIgnoreCase("01.00 PM")) {
                endTime = "12.15 PM";
            } else if (endTime.equalsIgnoreCase("02.30 PM")) {
                endTime = "01.10 PM";
            } else if (endTime.equalsIgnoreCase("04.00 PM")) {
                endTime = "02.35 PM";
            } else if (endTime.equalsIgnoreCase("05.30 PM")) {
                endTime = "03.30 PM";
            } else if (endTime.equalsIgnoreCase("07.30 PM")) {
                endTime = "04.30 PM";
            } else if (endTime.equalsIgnoreCase("09.00 PM")) {
                endTime = "05.30 PM";
            }
            return startTime + " - " +endTime;
        }
    }

    public interface DayListItemClickListener {
        void onDayItemClick(int pos, DayData dayData, DayDataHolder holder);
    }
}
