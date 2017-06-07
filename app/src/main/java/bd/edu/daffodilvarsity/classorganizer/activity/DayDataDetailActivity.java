package bd.edu.daffodilvarsity.classorganizer.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import org.polaric.colorful.Colorful;
import org.polaric.colorful.ColorfulActivity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;

import bd.edu.daffodilvarsity.classorganizer.data.DayData;
import bd.edu.daffodilvarsity.classorganizer.fragment.DayDataDetailFragment;
import bd.edu.daffodilvarsity.classorganizer.R;

/**
 * An activity representing a single DayData detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
// * in a {@link MainActivity}.
 */
public class DayDataDetailActivity extends ColorfulActivity {
    private DayData dayData;
    private Bundle bundle;
    private boolean fromNotification = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Orientation change hack
        bundle = savedInstanceState;

        setContentView(R.layout.activity_daydata_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);



        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState != null) {
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
            appBarLayout.setTitle(savedInstanceState.getCharSequence("AppBarTitle"));

        }
        // Making navigation bar colored
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, Colorful.getThemeDelegate().getPrimaryColor().getColorRes()));
        }
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = getIntent().getExtras();
            dayData = arguments.getParcelable("DayDataDetails");
            Bundle bundle = arguments.getBundle("bundled_data");
            if (bundle != null) {
                byte[] byteDayData = bundle.getByteArray("NotificationData");
                if (byteDayData != null) {
                    dayData = convertToDayData(byteDayData);
                    fromNotification = true;
                }
            }

            Bundle newArgs = new Bundle();
            newArgs.putParcelable("DayDataDetails", dayData);
            DayDataDetailFragment fragment = new DayDataDetailFragment();
            fragment.setArguments(newArgs);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.daydata_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Orientation change hack
        if (dayData != null) {
            outState.putCharSequence("AppBarTitle", dayData.getCourseCode());
        } else {
            outState.putCharSequence("AppBarTitle", bundle.getCharSequence("AppBarTitle"));
        }

    }

    private DayData convertToDayData(byte[] dayByte) {
        ByteArrayInputStream bis = new ByteArrayInputStream(dayByte);
        ObjectInput in = null;
        DayData dayData = null;
        try {
            in = new ObjectInputStream(bis);
            dayData = (DayData)in.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return dayData;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            if (fromNotification) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                navigateUpTo(new Intent(this, MainActivity.class));
                finish();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
