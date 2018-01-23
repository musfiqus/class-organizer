package bd.edu.daffodilvarsity.classorganizer.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by musfiqus on 1/23/2018.
 */

public class CustomFilterArrayAdapter extends ArrayAdapter implements Filterable {

    List<String> allCodes;
    List<String> originalCodes;
    StringFilter filter;

    public CustomFilterArrayAdapter(Context context, int resource, List<String> keys) {
        super(context, resource, keys);
        allCodes = keys;
        originalCodes = keys;
    }

    public int getCount() {
        int size = 0;
        try {
            size = allCodes.size();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return size;
    }



    public Object getItem(int position) {
        return allCodes.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    private class StringFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            final List<String> list = originalCodes;

            int count = list.size();
            final ArrayList<String> nlist = new ArrayList<String>(count);
            String filterableString ;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i);
                if (filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(filterableString);
                }
            }

            results.values = nlist;
            results.count = nlist.size();
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            allCodes = (ArrayList<String>) results.values;
            notifyDataSetChanged();
        }

    }


    @NonNull
    @Override
    public Filter getFilter()
    {
        return new StringFilter();
    }
}