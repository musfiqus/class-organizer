package bd.edu.daffodilvarsity.classorganizer.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.Map;

import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.model.Routine;

import static bd.edu.daffodilvarsity.classorganizer.R.string.saturday;

/**
 * Created by Mushfiqus Salehin on 3/21/2017.
 * musfiqus@gmail.com
 */

public class DayFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = "DayFragmentPagerAdapter";

    private final static String BUNDLE_TAG = "anyDay";
    private Map<String, ArrayList<Routine>> data;
    private ArrayList<Bundle> bundles = new ArrayList<>();

    private int pageCount = 0;
    private ArrayList<String> titles = new ArrayList<>();
    private Context context;


    DayFragmentPagerAdapter(Context context, FragmentManager fm, Map<String, ArrayList<Routine>> data) {
        super(fm);
        this.context = context;
        this.data = data;
        weekDayGenerator();
    }

    @Override
    public Fragment getItem(int position) {
        DayFragment dayFragment = new DayFragment();
        dayFragment.setArguments(bundles.get(position));
        return dayFragment;
    }

    @Override
    public int getCount() {
        return pageCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    private void weekDayGenerator() {
        ArrayList<Routine> satDayData = data.get(MainViewModel.SATURDAY);
        ArrayList<Routine> sunDayData = data.get(MainViewModel.SUNDAY);
        ArrayList<Routine> monDayData = data.get(MainViewModel.MONDAY);
        ArrayList<Routine> tueDayData = data.get(MainViewModel.TUESDAY);
        ArrayList<Routine> wedDayData = data.get(MainViewModel.WEDNESDAY);
        ArrayList<Routine> thuDayData = data.get(MainViewModel.THURSDAY);
        ArrayList<Routine> friDayData = data.get(MainViewModel.FRIDAY);

        //Setting dynamic page count, titles and bundles
        if (satDayData != null && satDayData.size() > 0) {
            pageCount++;

            //Sort daydata by time weight, ascending order
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(BUNDLE_TAG, satDayData);
            bundles.add(bundle);
            titles.add(context.getString(saturday));
        }
        if (sunDayData != null && sunDayData.size() > 0) {
            pageCount++;
            //Sort daydata by time weight, ascending order
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(BUNDLE_TAG, sunDayData);
            bundles.add(bundle);
            titles.add(context.getString(R.string.sunday));
        }
        if (monDayData != null && monDayData.size() > 0) {
            pageCount++;
            //Sort daydata by time weight, ascending order

            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(BUNDLE_TAG, monDayData);
            bundles.add(bundle);
            titles.add(context.getString(R.string.monday));
        }
        if (tueDayData != null && tueDayData.size() > 0) {
            pageCount++;
            //Sort daydata by time weight, ascending order
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(BUNDLE_TAG, tueDayData);
            bundles.add(bundle);
            titles.add(context.getString(R.string.tuesday));
        }
        if (wedDayData != null && wedDayData.size() > 0) {
            pageCount++;
            //Sort daydata by time weight, ascending order
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(BUNDLE_TAG, wedDayData);
            bundles.add(bundle);
            titles.add(context.getString(R.string.wednesday));
        }
        if (thuDayData != null && thuDayData.size() > 0) {
            pageCount++;
            //Sort daydata by time weight, ascending order
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(BUNDLE_TAG, thuDayData);
            bundles.add(bundle);
            titles.add(context.getString(R.string.thursday));
        }
        if (friDayData != null && friDayData.size() > 0) {
            pageCount++;
            //Sort daydata by time weight, ascending order
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(BUNDLE_TAG, friDayData);
            bundles.add(bundle);
            titles.add(context.getString(R.string.friday));
        }
    }

    void updateData(Map<String, ArrayList<Routine>> data) {
        //CLEAR EVERYTHING
        this.data.clear();
        this.bundles.clear();
        this.titles.clear();
        this.data = data;
        pageCount = 0;
        weekDayGenerator();
        notifyDataSetChanged();
    }
}
