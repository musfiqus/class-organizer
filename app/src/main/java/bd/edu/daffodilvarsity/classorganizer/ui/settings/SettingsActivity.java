package bd.edu.daffodilvarsity.classorganizer.ui.settings;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.ui.base.BaseActivity;
import bd.edu.daffodilvarsity.classorganizer.utils.ViewUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends BaseActivity {


    private static final String TAG = "SettingsActivity";



    @BindView(R.id.toolbar_settings)
    Toolbar mToolbar;

    //Method to display snackbar properly
    public static void showSnackBar(Activity activity, String message) {
        View rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        ViewCompat.setElevation(mToolbar, ViewUtils.pxFromDp(3));
        if (Build.VERSION.SDK_INT >= 23) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));//status bar or the time bar at the top
        }
        setSupportActionBar(mToolbar);
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);
            actionBar.setTitle("");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
