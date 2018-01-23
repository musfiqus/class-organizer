package bd.edu.daffodilvarsity.classorganizer.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.fragment.SearchFragmentRoom;
import bd.edu.daffodilvarsity.classorganizer.fragment.SearchFragmentRoutine;
import bd.edu.daffodilvarsity.classorganizer.fragment.SearchFragmentTeacher;

/**
 * Created by musfiqus on 1/18/2018.
 */

public class SearchPagerAdapter extends FragmentPagerAdapter {
    private Context context;
    private SearchFragmentRoutine searchFragmentRoutine;
    private SearchFragmentRoom searchFragmentRoom;
    private SearchFragmentTeacher searchFragmentTeacher;
    public SearchPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            if (searchFragmentRoutine == null) {
                searchFragmentRoutine = SearchFragmentRoutine.newInstance();
            }
            return searchFragmentRoutine;
        } else if (position == 1){
            if (searchFragmentRoom == null) {
                searchFragmentRoom = SearchFragmentRoom.newInstance();
            }
            return searchFragmentRoom;
        } else {
            if (searchFragmentTeacher == null) {
                searchFragmentTeacher = SearchFragmentTeacher.newInstance();
            }
            return searchFragmentTeacher;
        }

    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return context.getResources().getStringArray(R.array.search_page_titles)[position];
    }
}
