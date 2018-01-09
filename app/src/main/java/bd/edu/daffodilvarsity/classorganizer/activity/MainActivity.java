package bd.edu.daffodilvarsity.classorganizer.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.polaric.colorful.Colorful;
import org.polaric.colorful.ColorfulActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.adapter.DayFragmentPagerAdapter;
import bd.edu.daffodilvarsity.classorganizer.data.DayData;
import bd.edu.daffodilvarsity.classorganizer.service.NotificationRestartJobIntentService;
import bd.edu.daffodilvarsity.classorganizer.utils.CourseUtils;
import bd.edu.daffodilvarsity.classorganizer.utils.FileUtils;
import bd.edu.daffodilvarsity.classorganizer.utils.MasterDBOffline;
import bd.edu.daffodilvarsity.classorganizer.utils.PrefManager;
import bd.edu.daffodilvarsity.classorganizer.utils.RoutineLoader;
import bd.edu.daffodilvarsity.classorganizer.utils.UpdateTask;

public class MainActivity extends ColorfulActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    
    private PrefManager prefManager;
    private ArrayList<DayData> mDayData;
    private boolean onStart = false;
    private boolean onCreate = false;
    private boolean alarmRecreated = false;
    private boolean isActivityRunning = false;
    private boolean updateDialogueBlocked = false;
    private boolean isDownloadSuccessful;
    private AdView adView;


    //TODO Comment it out before publishing, only for testing purpose
//    public static final String DATABASE_VERSION_TAG = "AlphaDatabaseVersion";
//    public static final String DATABASE_URL_TAG = "AlphaURL";



    public static final String DATABASE_VERSION_TAG = "MasterDatabaseVersion";
    public static final String DATABASE_URL_TAG = "MasterURL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefManager = new PrefManager(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //If primary color and accent are same we are setting tab indicator to white
        if (Colorful.getThemeDelegate().getAccentColor() == Colorful.getThemeDelegate().getPrimaryColor()) {
            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, android.R.color.white));
        }

        // Making navigation bar colored
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, Colorful.getThemeDelegate().getPrimaryColor().getColorRes()));
        }

        //Setting drawer up
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        loadData();
        onCreate = true;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean hasNotification = preferences.getBoolean("notification_preference", true);
        if (hasNotification) {
            if (!alarmRecreated) {
                NotificationRestartJobIntentService.enqueueWork(this, new Intent(this, NotificationRestartJobIntentService.class));
            }
        }

        //Checking for built in DB updates
        offlineUpdate();
        //Checking if we missed any semester update

        if (prefManager.showSnack()) {
            showSnackBar(this, prefManager.getSnackData());
            prefManager.saveShowSnack(false);
        }

        //Aaannnd just before loading data we'll check for an online update
        checkFirebase();

        //And load adz
        if (adView == null) {
            adView = (AdView) findViewById(R.id.adView);
        }
        adView.loadAd(new AdRequest.Builder().build());

    }

    @Override
    public void onBackPressed() {
        if (isActivityRunning) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
                finishAffinity();
            }
        } else {
            super.onBackPressed();
            finishAffinity();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_button) {
            Intent intent = new Intent(this, AddActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.search_button_main) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        }
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_manage) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_text_extra));
            startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.share_title)));
        } else if (id == R.id.nav_send) {
            composeEmail();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!onCreate) {
            loadData();
        }
        //won't run again on onResume
        onStart = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActivityRunning = true;
        if (!onStart) {
            loadData();
        }
        if (prefManager.getReCreate()) {
            loadData();
            prefManager.saveReCreate(false);
        }
        if (updateDialogueBlocked) {
            checkFirebase();
            updateDialogueBlocked = false;
        }
        if (prefManager.showSnack()) {
            showSnackBar(this, prefManager.getSnackData());
            prefManager.saveShowSnack(false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActivityRunning = false;
    }

    public void loadData() {
        //Load Data
        if (mDayData != null) {
            mDayData.clear();
        }

        mDayData = prefManager.getSavedDayData();

        if (mDayData != null) {

            // Find the view pager that will allow the user to swipe between fragments
            ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
            // Create an adapter that knows which fragment should be shown on each page
            DayFragmentPagerAdapter adapter = new DayFragmentPagerAdapter(this, getSupportFragmentManager(), mDayData);
            // Set the adapter onto the view pager
            viewPager.setAdapter(adapter);
            //Setting current date and tab
            Calendar calendar = Calendar.getInstance();
            Date date = calendar.getTime();
            String currentDay = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime());
            for (int i = 0; i < adapter.getCount(); i++) {
                if (adapter.getPageTitle(i).toString().equalsIgnoreCase(currentDay)) {
                    viewPager.setCurrentItem(i);
                }
            }
            // Find the tab layout that shows the tabs
            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
            // Connect the tab layout with the view pager. This will
            //   1. Update the tab layout when the view pager is swiped
            //   2. Update the view pager when a tab is selected
            //   3. Set the tab layout's tab names with the view pager's adapter's titles
            //      by calling onPageTitle()
            tabLayout.setupWithViewPager(viewPager);
            if (prefManager.showSnack()) {
                showSnackBar(this, prefManager.getSnackData());
                prefManager.saveShowSnack(false);
            }
        }
    }

    //Checking if activity is in state loss
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        isActivityRunning = false;
    }

    //Processes the update if the in app db is updated
    private void offlineUpdate() {
        //If there is a new routine, update
        if (MasterDBOffline.OFFLINE_DATABASE_VERSION > prefManager.getOfflineDbVersion() && MasterDBOffline.OFFLINE_DATABASE_VERSION > prefManager.getOnlineDbVersion()) {
            Log.e(TAG, "OFFLINE: "+MasterDBOffline.OFFLINE_DATABASE_VERSION+" SAVED OFFLINE: "+prefManager.getOfflineDbVersion()+" SAVED ONLINE: "+prefManager.getOnlineDbVersion());
            Log.e(TAG, "Y THO?");
            String[] params = new String[]{"offline", ""+MasterDBOffline.OFFLINE_DATABASE_VERSION};
            new UpdateTask(this, getApplicationContext()).execute(params);
        }
    }

    //Checks if a new version of db is available via the db version stored in cloud
    private void checkFirebase() {
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference(DATABASE_VERSION_TAG);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int newVersion = 0;
                try {
                    newVersion = dataSnapshot.getValue(Integer.class);
                } catch (Exception ignored) {
                }
                final int newDBVersion = newVersion;
                if (prefManager.getMasterDBVersion() < newDBVersion) {
                    DatabaseReference urlReference = firebaseDatabase.getReference(DATABASE_URL_TAG);
                    urlReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String dbURL = null;
                            try {
                                dbURL = dataSnapshot.getValue(String.class);
                            } catch (Exception ignored){}
                            final String dbDlURL = dbURL;
                            if (dbDlURL != null) {
                                if (isActivityRunning) {
                                    if (newDBVersion > prefManager.getMasterDBVersion() && newDBVersion != prefManager.getSuppressedMasterDbVersion()) {
                                        MaterialDialog.Builder builder = new MaterialDialog.Builder(MainActivity.this)
                                                .title("Update Available!")
                                                .positiveText("YES")
                                                .negativeText("NO")
                                                .content("A new routine update is available. Do you want to download it now?")
                                                .checkBoxPrompt("Don't remind again me for this update", false, new CompoundButton.OnCheckedChangeListener() {
                                                    @Override
                                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                        if (isChecked) {
                                                            prefManager.setSuppressedMasterDbVersion(newDBVersion);
                                                        } else {
                                                            prefManager.setSuppressedMasterDbVersion(0);
                                                        }
                                                    }
                                                })
                                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                    @Override
                                                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                                                        startDBUpdate(newDBVersion, dbDlURL);
                                                    }
                                                });
                                        MaterialDialog dialog = builder.build();
                                        if (!dialog.isShowing()) {
                                            dialog.show();
                                        }
                                    }
                                } else {
                                    updateDialogueBlocked = true;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //This method starts the online db downloading process
    private void startDBUpdate(int newVersion, final String dbURL) {
        if (isActivityRunning) {
            showSnackBar(MainActivity.this, "Updating routine");
        }
        new DbDownloadTask().execute(dbURL, String.valueOf(newVersion));
    }

    //This function provides the skeleton of the suggestion email
    public void composeEmail() {
        String message = "Your suggestions: ";
        String subject = "Suggestions for DIU Class Organizer";
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.auth_email)});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /*Method to display snackbar properly*/
    public void showSnackBar(Activity activity, String message) {
        if (isActivityRunning) {
            View rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
            Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
        }
    }

    public boolean isActivityRunning() {
        return isActivityRunning;
    }

    private class DbDownloadTask extends AsyncTask<String, Void, Void> {
        private int newDBVersion;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isDownloadSuccessful = true;
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                String dlURL = params[0];
                newDBVersion = Integer.parseInt(params[1]);
                if (dlURL != null) {
                    FileUtils.dbDownloader(dlURL, FileUtils.generateMasterOnlineDbPath(getApplicationContext(), newDBVersion));
                }
            } catch (Exception e) {
                e.printStackTrace();
                isDownloadSuccessful = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (isDownloadSuccessful) {
                String[] params = new String[]{"online", ""+newDBVersion};
                new UpdateTask(MainActivity.this, getApplicationContext()).execute(params);
            } else {
                //Delete downloaded db
                FileUtils.deleteMasterDb(getApplicationContext(), true, newDBVersion);
                showSnackBar(MainActivity.this, "Download failed");
            }
        }
    }

}
