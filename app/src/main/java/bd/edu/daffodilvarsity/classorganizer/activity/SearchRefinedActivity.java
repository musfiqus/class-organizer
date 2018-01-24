package bd.edu.daffodilvarsity.classorganizer.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import org.polaric.colorful.ColorfulActivity;

import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.adapter.SearchPagerAdapter;

public class SearchRefinedActivity extends ColorfulActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_refined);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.search_toolbar);
        if (toolbar != null) {
            toolbar.setTitle(R.string.search);
            setSupportActionBar(toolbar);
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Setup viewpager
        setupViewPager();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return true;
    }

    private void setupViewPager() {
        ViewPager viewPager = findViewById(R.id.search_viewpager);
        SearchPagerAdapter adapter = new SearchPagerAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.search_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }
}
