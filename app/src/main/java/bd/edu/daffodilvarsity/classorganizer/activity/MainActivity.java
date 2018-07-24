package bd.edu.daffodilvarsity.classorganizer.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
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
import com.google.firebase.iid.FirebaseInstanceId;

import org.polaric.colorful.Colorful;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.adapter.DayFragmentPagerAdapter;
import bd.edu.daffodilvarsity.classorganizer.data.DayData;
import bd.edu.daffodilvarsity.classorganizer.data.Download;
import bd.edu.daffodilvarsity.classorganizer.data.UpdateResponse;
import bd.edu.daffodilvarsity.classorganizer.service.NotificationRestartJobIntentService;
import bd.edu.daffodilvarsity.classorganizer.service.UpdateService;
import bd.edu.daffodilvarsity.classorganizer.utils.CourseUtils;
import bd.edu.daffodilvarsity.classorganizer.utils.RoutineDB;
import bd.edu.daffodilvarsity.classorganizer.utils.PrefManager;
import bd.edu.daffodilvarsity.classorganizer.utils.UpdateGetter;
import es.dmoral.toasty.Toasty;
import io.reactivex.disposables.Disposable;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    private static final String CURRENT_PROMOTION = "Facebook_Page";
    
    private PrefManager prefManager;
    private ArrayList<DayData> mDayData;
    private boolean onStart = false;
    private boolean onCreate = false;
    private boolean alarmRecreated = false;
    private boolean isActivityRunning = false;
    private boolean updateDialogueBlocked = false;
    private DayFragmentPagerAdapter adapter;
    private UpdateGetter updateGetter;
    private Disposable mDisposable;
//    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefManager = new PrefManager(this);
        updateGetter = UpdateGetter.getInstance(this);
        UpdateResponse updateResponse = getIntent().getParcelableExtra(UpdateService.TAG_UPDATE_RESPONSE);
        if (updateResponse != null) {
            //Activity was started from update notification, handle updateresponse
            updateGetter.initUpdate(updateResponse);
            Log.d(TAG, "onCreate: Activity started from update notification");
        } else {
            mDisposable = updateGetter.getUpdate();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "onCreate: token: "+token);

        try {
            Log.d(TAG, "onCreate: Asset path"+getAssets().open("databases/routine.db"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        setSupportActionBar(toolbar);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setEnterTransition(null);
//            getWindow().setExitTransition(null);
//        }
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

        registerReceiver();

        //And load adz
//        if (adView == null) {
//            adView = (AdView) findViewById(R.id.adView);
//        }
//        adView.loadAd(new AdRequest.Builder().build());
        showOneTimePromotionalDialog();


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
//            Intent intent = new Intent(this, SearchActivity.class);
            Intent intent = new Intent(this, SearchRefinedActivity.class);
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
            refreshData();
        }
        //won't run again on onResume
        onStart = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActivityRunning = true;
        if (!onStart) {
            refreshData();
        }
        if (prefManager.getReCreate()) {
            refreshData();
            prefManager.saveReCreate(false);
        }
        if (updateDialogueBlocked) {
            updateDialogueBlocked = false;
        }
        if (prefManager.showSnack()) {
            showSnackBar(this, prefManager.getSnackData());
            prefManager.saveShowSnack(false);
        }
        if (prefManager.isRefreshPending()) {
            refreshData();
            prefManager.enableDataRefresh(false);
            Log.d(TAG, "onResume: Data refreshed");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActivityRunning = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
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
            adapter = new DayFragmentPagerAdapter(this, getSupportFragmentManager(), mDayData);
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

    public void updateData() {
        Log.i(TAG, "updateData() called");
        mDayData.clear();
        mDayData = prefManager.getSavedDayData();
        if (adapter != null) {
            adapter.updateData(mDayData);
        } else {
            Log.w(TAG, "DayFragmentPagerAdapter is null");
            loadData();
        }

    }

    private void refreshData() {
        if (isActivityRunning) {
            mDayData.clear();
            mDayData = prefManager.getSavedDayData();
            if (adapter != null) {
                adapter.updateData(mDayData);
                if (prefManager.showSnack()) {
                    showSnackBar(this, prefManager.getSnackData());
                    prefManager.saveShowSnack(false);
                }
            }
        } else {
            prefManager.enableDataRefresh(true);
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
        if (RoutineDB.OFFLINE_DATABASE_VERSION > prefManager.getDatabaseVersion()) {
            String[] params = new String[]{"offline", ""+ RoutineDB.OFFLINE_DATABASE_VERSION};
            new UpdateTask(this).execute(params);
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
        if (isActivityRunning) {
            View rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
            Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
        }
    }

    public boolean isActivityRunning() {
        return isActivityRunning;
    }


    private void registerReceiver(){

        LocalBroadcastManager bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UpdateService.PROGRESS_UPDATE);
        bManager.registerReceiver(broadcastReceiver, intentFilter);

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction() != null && intent.getAction().equals(UpdateService.PROGRESS_UPDATE)){

                Download download = intent.getParcelableExtra(UpdateService.TAG_DOWNLOAD);

                if(download.getProgress() == 200){
                    //Normal routine updated
                    refreshData();
                    if (isActivityRunning) {
                        showSnackBar(MainActivity.this, "Routine updated");
                    } else {
                        Toasty.success(getApplicationContext(), "Routine updated", Toast.LENGTH_SHORT, true).show();
                    }
                } else if (download.getProgress() == 300) {
                    //semester updated
                    if (isActivityRunning) {
                        showUpgradeDialogue();
                    } else {
                        Toasty.success(getApplicationContext(), "The routine was updated as per " +
                                CourseUtils.getInstance(getApplicationContext()).getCurrentSemester(prefManager.getCampus()
                                        , prefManager.getDept(), prefManager.getProgram()) + " semester."
                                , Toast.LENGTH_SHORT, true).show();
                    }
                }
            }
        }
    };

    public void showUpgradeDialogue() {
        PrefManager prefManager = new PrefManager(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Semester!");
        builder.setMessage("The routine was updated as per " + CourseUtils.getInstance(this).getCurrentSemester(prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram()) + " semester.\n" +
                "Note: Your level and term will automatically get updated based on current selection and your modifications will be reset.");
        builder.setPositiveButton("OK", (dialog, which) -> {
            refreshData();
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    private static class UpdateTask extends AsyncTask<String, Void, String> {

        private static final String TAG = "UpdateTask";

        private WeakReference<MainActivity> activityReference;
        private boolean isSuccessful = false;
        private boolean isUpgrade = false;


        UpdateTask(MainActivity activity) {
            activityReference = new WeakReference<>(activity);
        }


        @Override
        protected String doInBackground(String... params) {
            //params: online/offline, db version
            //Simple update function loads new routine if db version changes
            isUpgrade = UpdateService.isNewSemesterAvailable(activityReference.get());
            isSuccessful = UpdateService.loadRoutineFromDB(activityReference.get(), isUpgrade);
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            final MainActivity activity = activityReference.get();
            if (activity != null && activity.isActivityRunning()) {
                if (isSuccessful) {
                    PrefManager prefManager = new PrefManager(activity);
                    CourseUtils courseUtils = CourseUtils.getInstance(activity);
                    prefManager.saveSemester(courseUtils.getCurrentSemester(prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram()));
                    prefManager.setSemesterCount(courseUtils.getSemesterCount(prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram()));
                    prefManager.setDatabaseVersion(RoutineDB.OFFLINE_DATABASE_VERSION);
                    if (isUpgrade) prefManager.resetModification(true, true, true, true);
                    if (isUpgrade) {
                        activity.showUpgradeDialogue();
                    } else {
                        activity.updateData();
                        activity.showSnackBar(activity, "Routine updated");
                    }

                } else {
                    if (activity.isActivityRunning()) {
                        activity.showSnackBar(activity, "Error loading updated routine!");
                    }
                }
            } else if (activity != null && !activity.isActivityRunning()) {
                PrefManager prefManager = new PrefManager(activity.getApplicationContext());
                if (isSuccessful) {
                    prefManager.saveShowSnack(true);
                    prefManager.saveSnackData("Routine updated");
                } else {
                    prefManager.saveShowSnack(true);
                    prefManager.saveSnackData( "Error loading updated routine!");
                }
            }
        }
    }

    private void showOneTimePromotionalDialog() {
        if (CURRENT_PROMOTION.equalsIgnoreCase(prefManager.getExpiredPromotion())) {
            return;
        }
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Connect with us on Facebook!")
                .content("Did you know Class Organizer is now on Facebook?\n " +
                        "Like Class Organizer's Facebook page to get all the latest news and updates.")
                .positiveText("Visit Facebook")
                .negativeText("Cancel")
                .onPositive((dialog1, which) -> {
                    startActivity(getOpenFacebookIntent(this));
                    prefManager.setExpiredPromotion(CURRENT_PROMOTION);
                    dialog1.dismiss();
                })
                .onNegative((dialog12, which) -> dialog12.dismiss())
                .checkBoxPrompt("Don't remind me again", false, (buttonView, isChecked) -> {
                    if (isChecked) {
                        prefManager.setExpiredPromotion(CURRENT_PROMOTION);
                    }
                })
                .build();
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (!dialog.isShowing() && isActivityRunning()) {
                dialog.show();
            }
        }, 1500);


    }
    public static Intent getOpenFacebookIntent(Context context) {
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo("com.facebook.katana",0);
            if (ai.enabled) {
                return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/385913368514734"));
            } else {
                return new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.facebook.com/classorganizerdiu"));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.facebook.com/classorganizerdiu"));
        }
    }
}
