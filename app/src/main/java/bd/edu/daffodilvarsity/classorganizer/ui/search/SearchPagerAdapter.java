package bd.edu.daffodilvarsity.classorganizer.ui.search;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import bd.edu.daffodilvarsity.classorganizer.R;

/**
 * Created by musfiqus on 1/18/2018.
 */

public class SearchPagerAdapter extends FragmentPagerAdapter {
    private Context context;
    private SearchRoutineFragment searchRoutineFragment;
    private SearchFreeRoomFragment searchFreeRoomFragment;
    private SearchTeachersFragment searchTeachersFragment;

    public SearchPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            if (searchRoutineFragment == null) {
                searchRoutineFragment = SearchRoutineFragment.newInstance();
            }
            return searchRoutineFragment;
        } else if (position == 1) {
            if (searchFreeRoomFragment == null) {
                searchFreeRoomFragment = SearchFreeRoomFragment.newInstance();
            }
            return searchFreeRoomFragment;
        } else if (position == 2) {
            if (searchTeachersFragment == null) {
                searchTeachersFragment = SearchTeachersFragment.newInstance();
            }
            return searchTeachersFragment;
        }
        return null;

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
