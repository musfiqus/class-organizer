package bd.edu.daffodilvarsity.classorganizer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by musfiqus on 5/20/2017.
 */

public class DayDataAdapter extends RecyclerView.Adapter<DayDataHolder> {

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
    public void onBindViewHolder(DayDataHolder holder, int position) {
        DayData dayData = dayDataArrayList.get(position);
        holder.bindDayData(dayData);
    }

    @Override
    public int getItemCount() {
        return dayDataArrayList.size();
    }
}
