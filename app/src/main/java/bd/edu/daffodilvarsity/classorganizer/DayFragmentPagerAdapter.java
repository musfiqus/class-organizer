package bd.edu.daffodilvarsity.classorganizer;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

import static bd.edu.daffodilvarsity.classorganizer.R.string.saturday;

/**
 * Created by musfiqus on 3/21/2017.
 */

public class DayFragmentPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<DayData> dayData = new ArrayList<>();
    private ArrayList<Bundle> bundles = new ArrayList<>();
    private ArrayList<DayData> satDayData = new ArrayList<>();
    private ArrayList<DayData> sunDayData = new ArrayList<>();
    private ArrayList<DayData> monDayData = new ArrayList<>();
    private ArrayList<DayData> tueDayData = new ArrayList<>();
    private ArrayList<DayData> wedDayData = new ArrayList<>();
    private ArrayList<DayData> thuDayData = new ArrayList<>();
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

    private void weekDayGenerator() {
        boolean hasSatDay = false;
        boolean hasSunDay = false;
        boolean hasMonDay = false;
        boolean hasTueDay = false;
        boolean hasWedDay = false;
        boolean hasThuDay = false;

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
            }
        }

        //Setting dynamic page count, titles and bundles
        if (hasSatDay) {
            pageCount++;
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("anyDay", satDayData);
            bundles.add(bundle);
            titles.add(context.getString(saturday));
        }
        if (hasSunDay) {
            pageCount++;
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("anyDay", sunDayData);
            bundles.add(bundle);
            titles.add(context.getString(R.string.sunday));
        }
        if (hasMonDay) {
            pageCount++;
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("anyDay", monDayData);
            bundles.add(bundle);
            titles.add(context.getString(R.string.monday));
        }
        if (hasTueDay) {
            pageCount++;
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("anyDay", tueDayData);
            bundles.add(bundle);
            titles.add(context.getString(R.string.tuesday));
        }
        if (hasWedDay) {
            pageCount++;
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("anyDay", wedDayData);
            bundles.add(bundle);
            titles.add(context.getString(R.string.wednesday));
        }
        if (hasThuDay) {
            pageCount++;
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("anyDay", thuDayData);
            bundles.add(bundle);
            titles.add(context.getString(R.string.thursday));
        }
    }
}
