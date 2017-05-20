package bd.edu.daffodilvarsity.classorganizer;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static bd.edu.daffodilvarsity.classorganizer.R.string.saturday;

/**
 * Created by musfiqus on 3/21/2017.
 */

public class DayFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private static String BUNDLE_TAG = "anyDay";
    private ArrayList<DayData> dayData = new ArrayList<>();
    private ArrayList<Bundle> bundles = new ArrayList<>();
    private ArrayList<DayData> satDayData = new ArrayList<>();
    private ArrayList<DayData> sunDayData = new ArrayList<>();
    private ArrayList<DayData> monDayData = new ArrayList<>();
    private ArrayList<DayData> tueDayData = new ArrayList<>();
    private ArrayList<DayData> wedDayData = new ArrayList<>();
    private ArrayList<DayData> thuDayData = new ArrayList<>();
    private ArrayList<DayData> friDayData = new ArrayList<>();
    private int pageCount = 0;
    private ArrayList<String> titles = new ArrayList<>();
    private Context context;


    public DayFragmentPagerAdapter(Context context, FragmentManager fm, ArrayList<DayData> dayData) {
        super(fm);
        this.context = context;
        this.dayData = dayData;
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
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    private void weekDayGenerator() {
        boolean hasSatDay = false;
        boolean hasSunDay = false;
        boolean hasMonDay = false;
        boolean hasTueDay = false;
        boolean hasWedDay = false;
        boolean hasThuDay = false;
        boolean hasFriDay = false;

        for (DayData eachDay : dayData) {

            if (eachDay.getDay().equalsIgnoreCase("saturday")) {
                satDayData.add(eachDay);
                if (!hasSatDay) {
                    hasSatDay = true;
                }
            } else if (eachDay.getDay().equalsIgnoreCase("sunday")) {
                sunDayData.add(eachDay);
                if (!hasSunDay) {
                    hasSunDay = true;
                }
            } else if (eachDay.getDay().equalsIgnoreCase("monday")) {
                monDayData.add(eachDay);
                if (!hasMonDay) {
                    hasMonDay = true;
                }
            } else if (eachDay.getDay().equalsIgnoreCase("tuesday")) {
                tueDayData.add(eachDay);
                if (!hasTueDay) {
                    hasTueDay = true;
                }
            } else if (eachDay.getDay().equalsIgnoreCase("wednesday")) {
                wedDayData.add(eachDay);
                if (!hasWedDay) {
                    hasWedDay = true;
                }
            } else if (eachDay.getDay().equalsIgnoreCase("thursday")) {
                thuDayData.add(eachDay);
                if (!hasThuDay) {
                    hasThuDay = true;
                }
            } else if (eachDay.getDay().equalsIgnoreCase("friday")) {
                friDayData.add(eachDay);
                if (!hasFriDay) {
                    hasFriDay = true;
                }
            }
        }

        //Setting dynamic page count, titles and bundles
        if (hasSatDay) {
            pageCount++;

            //Sort daydata by time weight, ascending order
            Collections.sort(satDayData, new Comparator<DayData>() {
                @Override
                public int compare(DayData o1, DayData o2) {
                    return Double.valueOf(o1.getTimeWeight()).compareTo(o2.getTimeWeight());
                }
            });
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(BUNDLE_TAG, satDayData);
            bundles.add(bundle);
            titles.add(context.getString(saturday));
        }
        if (hasSunDay) {
            pageCount++;
            //Sort daydata by time weight, ascending order
            Collections.sort(sunDayData, new Comparator<DayData>() {
                @Override
                public int compare(DayData o1, DayData o2) {
                    return Double.valueOf(o1.getTimeWeight()).compareTo(o2.getTimeWeight());
                }
            });
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(BUNDLE_TAG, sunDayData);
            bundles.add(bundle);
            titles.add(context.getString(R.string.sunday));
        }
        if (hasMonDay) {
            pageCount++;
            //Sort daydata by time weight, ascending order
            Collections.sort(monDayData, new Comparator<DayData>() {
                @Override
                public int compare(DayData o1, DayData o2) {
                    return Double.valueOf(o1.getTimeWeight()).compareTo(o2.getTimeWeight());
                }
            });
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(BUNDLE_TAG, monDayData);
            bundles.add(bundle);
            titles.add(context.getString(R.string.monday));
        }
        if (hasTueDay) {
            pageCount++;
            //Sort daydata by time weight, ascending order
            Collections.sort(tueDayData, new Comparator<DayData>() {
                @Override
                public int compare(DayData o1, DayData o2) {
                    return Double.valueOf(o1.getTimeWeight()).compareTo(o2.getTimeWeight());
                }
            });
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(BUNDLE_TAG, tueDayData);
            bundles.add(bundle);
            titles.add(context.getString(R.string.tuesday));
        }
        if (hasWedDay) {
            pageCount++;
            //Sort daydata by time weight, ascending order
            Collections.sort(wedDayData, new Comparator<DayData>() {
                @Override
                public int compare(DayData o1, DayData o2) {
                    return Double.valueOf(o1.getTimeWeight()).compareTo(o2.getTimeWeight());
                }
            });
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(BUNDLE_TAG, wedDayData);
            bundles.add(bundle);
            titles.add(context.getString(R.string.wednesday));
        }
        if (hasThuDay) {
            pageCount++;
            //Sort daydata by time weight, ascending order
            Collections.sort(thuDayData, new Comparator<DayData>() {
                @Override
                public int compare(DayData o1, DayData o2) {
                    return Double.valueOf(o1.getTimeWeight()).compareTo(o2.getTimeWeight());
                }
            });
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(BUNDLE_TAG, thuDayData);
            bundles.add(bundle);
            titles.add(context.getString(R.string.thursday));
        }
        if (hasFriDay) {
            pageCount++;
            //Sort daydata by time weight, ascending order
            Collections.sort(friDayData, new Comparator<DayData>() {
                @Override
                public int compare(DayData o1, DayData o2) {
                    return Double.valueOf(o1.getTimeWeight()).compareTo(o2.getTimeWeight());
                }
            });
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(BUNDLE_TAG, friDayData);
            bundles.add(bundle);
            titles.add(context.getString(R.string.friday));
        }
    }
}
