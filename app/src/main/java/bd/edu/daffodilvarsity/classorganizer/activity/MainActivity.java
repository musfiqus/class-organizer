package bd.edu.daffodilvarsity.classorganizer.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.os.ResultReceiver;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
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
import bd.edu.daffodilvarsity.classorganizer.service.DatabaseUpdateIntentService;
import bd.edu.daffodilvarsity.classorganizer.service.NotificationRestartService;
import bd.edu.daffodilvarsity.classorganizer.utils.CourseUtils;
import bd.edu.daffodilvarsity.classorganizer.utils.MasterDBOffline;
import bd.edu.daffodilvarsity.classorganizer.utils.PrefManager;
import bd.edu.daffodilvarsity.classorganizer.utils.RoutineLoader;
import bd.edu.daffodilvarsity.classorganizer.utils.MasterDBOnline;

public class MainActivity extends ColorfulActivity implements NavigationView.OnNavigationItemSelectedListener {
    private PrefManager prefManager;
    private ArrayList<DayData> mDayData;
    private boolean onStart = false;
    private boolean onCreate = false;
    private boolean alarmRecreated = false;
    private RoutineLoader routineLoader;
    private boolean isActivityRunning = false;
    private boolean updateDialogueBlocked = false;


    //TODO Comment it out before publishing, only for testing purpose
    private static final String DATABASE_VERSION_TAG = "AlphaDatabaseVersion";
    private static final String DATABASE_URL_TAG = "AlphaURL";



//    private static final String DATABASE_VERSION_TAG = "MasterDatabaseVersion";
//    private static final String DATABASE_URL_TAG = "MasterURL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefManager = new PrefManager(this);
        //Managing compat with previous version
        if (prefManager.getMasterDBVersion() <= MasterDBOffline.OFFLINE_DATABASE_VERSION) {
            prefManager.setUpdatedOnline(false);
        }

        routineLoader = new RoutineLoader(prefManager.getLevel(), prefManager.getTerm(), prefManager.getSection(), this, prefManager.getDept(), prefManager.getCampus(), prefManager.getProgram());

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
                startService(new Intent(this, NotificationRestartService.class));
            }
        }

        //Checking for built in DB updates
        offlineUpdate();
        //Checking if we missed any semester update
        if (routineLoader.isNewSemesterAvailable()) {
            upgradeRoutine(true, prefManager.getMasterDBVersion(), false);
        }

        if (prefManager.showSnack()) {
            showSnackBar(this, prefManager.getSnackData());
            prefManager.saveShowSnack(false);
        }

        //Aaannnd just before loading data we'll check for an online update
        checkFirebase();
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
            if (routineLoader.isNewSemesterAvailable()) {
                upgradeRoutine(true, prefManager.getMasterDBVersion(), false);
            }
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
        if (MasterDBOffline.OFFLINE_DATABASE_VERSION > prefManager.getMasterDBVersion()) {
            prefManager.setUpdatedOnline(false);
            if (routineLoader.isNewSemesterAvailable()) {
                upgradeRoutine(true, MasterDBOffline.OFFLINE_DATABASE_VERSION, false);
            } else {
                boolean isNotUpdated = upgradeRoutine(false, MasterDBOffline.OFFLINE_DATABASE_VERSION, true);
                if (isNotUpdated) {
                    showSnackBar(this, "Error loading updated routine!");
                }
            }
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

    private boolean upgradeRoutine(boolean isUpgrade, final int dbVersion, boolean loadPersonal) {
        if (isUpgrade) {
            //Shows new semester upgrade dialogue and upgrades it
            if (routineLoader.isNewSemesterAvailable()) {
                if (isActivityRunning) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("New Semester!");
                    builder.setMessage("The routine was updated as per " + CourseUtils.getInstance(this).getCurrentSemester(prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram()) + " semester.\n" +
                            "Note: Your level and term will automatically get updated based on current selection and your modifications will be reset. ");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setLevelTermOnUpgrade();
                            //Deleting modified data before loading new semester routine
                            prefManager.resetModification(true, true, true, true);
                            boolean loadCheck = upgradeRoutine(false, dbVersion, false);
                            if (!loadCheck) {
                                prefManager.saveShowSnack(true);
                                prefManager.saveSnackData("Routine updated");
                                prefManager.setSemesterCount(CourseUtils.getInstance(getApplicationContext()).getSemesterCount(prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram()));
                                prefManager.saveSemester(CourseUtils.getInstance(getApplicationContext()).getCurrentSemester(prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram()));
                                routineLoader = new RoutineLoader(prefManager.getLevel(), prefManager.getTerm(), prefManager.getSection(), getApplicationContext(), prefManager.getDept(), prefManager.getCampus(), prefManager.getProgram());
                                ArrayList<DayData> updatedRoutine = routineLoader.loadRoutine(false);
                                prefManager.saveDayData(updatedRoutine);
                                loadData();
                            } else {
                                Toast.makeText(getApplicationContext(), "Error loading routine", Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();

                } else {
                    updateDialogueBlocked = true;
                }
            }
            return true;
        } else {
            //Simple update function loads new routine if db version changes
            routineLoader = new RoutineLoader(prefManager.getLevel(), prefManager.getTerm(), prefManager.getSection(), this, prefManager.getDept(), prefManager.getCampus(), prefManager.getProgram());
            ArrayList<DayData> updatedRoutine = routineLoader.loadRoutine(loadPersonal);
            if (updatedRoutine != null) {
                if (updatedRoutine.size() > 0) {
                    prefManager.saveDayData(updatedRoutine);
                    prefManager.setMasterDbVersion(dbVersion);
                    if (isActivityRunning) {
                        loadData();
                        showSnackBar(MainActivity.this, "Routine Updated");
                    } else {
                        prefManager.saveReCreate(true);
                        prefManager.saveShowSnack(true);
                        prefManager.saveSnackData("Routine Updated");
                    }
                    return false;
                }
            }
            return true;
        }
    }




    //This method starts the online db downloading process
    private void startDBUpdate(int newVersion, final String dbURL) {
        if (isActivityRunning) {
            showSnackBar(MainActivity.this, "Updating routine");
        }
        DatabaseUpdateResultReceiver resultReceiver = new DatabaseUpdateResultReceiver(this, new Handler());
        Intent updateIntent = new Intent(MainActivity.this, DatabaseUpdateIntentService.class);
        updateIntent.putExtra(DatabaseUpdateIntentService.TAG_DATABASE_NAME, MasterDBOnline.UPDATED_DATABASE_NAME);
        updateIntent.putExtra(DatabaseUpdateIntentService.TAG_DB_URL, dbURL);
        updateIntent.putExtra(DatabaseUpdateIntentService.TAG_DATABASE_VERSION, newVersion);
        updateIntent.putExtra(DatabaseUpdateIntentService.TAG_RECEIVER, resultReceiver);
        startService(updateIntent);
    }

    //Calculates the new level and term upon a new semester routine
    private void setLevelTermOnUpgrade() {
        int currentLevel = prefManager.getLevel();
        int currentTerm = prefManager.getTerm();
        if (currentTerm == 2) {
            if (currentLevel < 3) {
                currentLevel++;
                prefManager.saveLevel(currentLevel);
            }
            currentTerm = 0;
            prefManager.saveTerm(currentTerm);
        } else {
            currentTerm++;
            prefManager.saveTerm(currentTerm);
        }
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
        View rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
    }


    //This class start db download and processes the result in case of success or failure
    public class DatabaseUpdateResultReceiver extends ResultReceiver {

        public DatabaseUpdateResultReceiver(Context context, Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            switch (resultCode) {
                case DatabaseUpdateIntentService.DOWNLOAD_ERROR:
                    //Do nothing for now
                    if (isActivityRunning) {
                        showSnackBar(MainActivity.this, "Error downloading update");
                    }
                    break;
                case DatabaseUpdateIntentService.DOWNLOAD_SUCCESS:
                    int dbVersion = resultData.getInt(DatabaseUpdateIntentService.TAG_DATABASE_VERSION);
                    String dbName = resultData.getString(DatabaseUpdateIntentService.TAG_DATABASE_NAME);
                    if (dbName != null) {
                        if (dbName.equalsIgnoreCase(MasterDBOnline.UPDATED_DATABASE_NAME)) {
                            boolean prevUpdateValue = prefManager.isUpdatedOnline();
                            int prevDatabaseValue = prefManager.getMasterDBVersion();
                            prefManager.setUpdatedOnline(true);
                            prefManager.setMasterDbVersion(dbVersion);
                            boolean isVerified = routineLoader.verifyUpdatedDb();
                            if (isVerified) {
                                routineLoader = null;
                                prefManager.setUpdatedOnline(true);
                                if (routineLoader.isNewSemesterAvailable()) {
                                    upgradeRoutine(true, dbVersion, false);
                                } else {
                                    upgradeRoutine(false, dbVersion, true);
                                }
                            } else {
                                prefManager.setUpdatedOnline(prevUpdateValue);
                                prefManager.setMasterDbVersion(prevDatabaseValue);
                                if (isActivityRunning) {
                                    showSnackBar(MainActivity.this, "Update corrupted");
                                }
                            }
                        }
                    }

            }
            super.onReceiveResult(resultCode, resultData);
        }
    }

}
