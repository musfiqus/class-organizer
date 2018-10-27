package bd.edu.daffodilvarsity.classorganizer.ui.search;

import android.os.Build;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;

import bd.edu.daffodilvarsity.classorganizer.R;

public class  SearchRefinedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_refined);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.search_toolbar);
        if (toolbar != null) {
            toolbar.setTitle("");
            setSupportActionBar(toolbar);
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);
        }

        if (Build.VERSION.SDK_INT >= 23) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));//status bar or the time bar at the top
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
