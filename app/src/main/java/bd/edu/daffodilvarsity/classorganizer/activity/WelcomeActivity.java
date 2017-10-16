package bd.edu.daffodilvarsity.classorganizer.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;

import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.adapter.WelcomeSlidePagerAdapter;
import bd.edu.daffodilvarsity.classorganizer.utils.CourseUtils;
import bd.edu.daffodilvarsity.classorganizer.utils.DataChecker;
import bd.edu.daffodilvarsity.classorganizer.utils.MasterDBOnline;
import bd.edu.daffodilvarsity.classorganizer.utils.PrefManager;

public class WelcomeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    private ViewPager viewPager;
    private WelcomeSlidePagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private Button btnPrevious, btnNext;
    private boolean isUpdateSuccessful = true;
    private boolean isUpdateAvailable = false;
    private TextView checkText;
    private SpinKitView spinKitView;
    private ImageView cloud;
    private boolean isUpdateAlreadyExecuted = false;
    private boolean hasSkipped = false;
    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
            onPageSelectedCustom(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Checking for first time launch - before calling setContentView()
        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_welcome);


        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnPrevious = (Button) findViewById(R.id.btn_previous);
        btnNext = (Button) findViewById(R.id.btn_next);


        /* layouts of all welcome sliders */
        layouts = new int[]{
                R.layout.welcome_slide1,
                R.layout.welcome_slide2,
                R.layout.welcome_slide3,
                R.layout.welcome_slide4,
                R.layout.welcome_slide5};

        // adding bottom dots
        addBottomDots(0);

        /* making notification bar transparent*/
        changeStatusBarColor();

        myViewPagerAdapter = new WelcomeSlidePagerAdapter(this, layouts);
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        //Disabling previous on the opening page
        onPageSelectedCustom(0);

        //disabling swipe
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = getItem(0);
                viewPager.setCurrentItem(current - 1);
                if ((current - 1) == 0) {
                    onPageSelectedCustom(0);
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // checking for last page
                // if last page home screen will be launched
                int current = getItem(+1);
                if (btnNext.getText().toString().equalsIgnoreCase(getResources().getString(R.string.skip))) {
                    hasSkipped = true;
                }
                if (current == layouts.length - 1) {
                    if (myViewPagerAdapter.getClassDataCode() > 0) {
                        DataChecker.errorMessage(getApplicationContext(), myViewPagerAdapter.getClassDataCode(), null);
                        showSnackBar(myViewPagerAdapter.getCampus(), myViewPagerAdapter.getDept(), myViewPagerAdapter.getProgram(), myViewPagerAdapter.getSection(), Integer.toString(myViewPagerAdapter.getLevel() + 1), Integer.toString(myViewPagerAdapter.getTerm() + 1));
                        viewPager.setCurrentItem(current - 1);
                    } else {
                        viewPager.setCurrentItem(current);
                    }
                } else if (current == layouts.length - 2) {
                    if (myViewPagerAdapter.getCampusDataCode() > 0) {
                        DataChecker.errorMessage(getApplicationContext(), myViewPagerAdapter.getCampusDataCode(), null);
                        showSnackBar(myViewPagerAdapter.getCampus(), myViewPagerAdapter.getDept(), myViewPagerAdapter.getProgram(), myViewPagerAdapter.getSection(), Integer.toString(myViewPagerAdapter.getLevel() + 1), Integer.toString(myViewPagerAdapter.getTerm() + 1));
                        viewPager.setCurrentItem(current - 1);
                    } else {
                        myViewPagerAdapter.setupSectionAdapter();
                        viewPager.setCurrentItem(current);
                    }
                } else if (current < layouts.length) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                } else {
                    prefManager.setSemesterCount(CourseUtils.getInstance(getApplicationContext()).getSemesterCount(prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram()));
                    prefManager.saveSemester(CourseUtils.getInstance(getApplicationContext()).getCurrentSemester(prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram()));
                    myViewPagerAdapter.loadSemester();
                    launchHomeScreen();
                }
            }
        });
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        finish();
    }

    public void onPageSelectedCustom(int position) {
        if (position < 3) {
            btnPrevious.setVisibility(View.GONE);
        } else {
            btnPrevious.setVisibility(View.VISIBLE);
        }
        // changing the next button text 'NEXT' / 'GOT IT'
        if (position == layouts.length - 1) {
            // last page. make button text to GOT IT
            btnNext.setPadding(0, 0, 32, 0);
            btnNext.setText(getString(R.string.start));
        } else if (position == 1) {
            btnNext.setText(R.string.skip);
            if (spinKitView == null) {
                spinKitView = (SpinKitView) findViewById(R.id.spin_kit);
            }
            if (cloud == null) {
                cloud = (ImageView) findViewById(R.id.cloud_icon);
            }
            if (checkText == null) {
                checkText = (TextView) findViewById(R.id.check_Text);
            }
            if (!isUpdateAlreadyExecuted) {
                checkText.setText(R.string.check_latest_routine_text);
                spinKitView.setVisibility(View.VISIBLE);
                cloud.setImageResource(R.drawable.ic_cloud_download_white_48dp);
                new StartUpdateTask().execute();
            }
        } else {
            // still pages are left
            btnNext.setText(getString(R.string.next));
        }
    }

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /*Method to display snackbar properly*/
    public void showSnackBar(final String campus, final String dept, final String program, final String section, final String level, final String term) {
        String message = getString(R.string.contact_mailll);
        View rootView = findViewById(R.id.welcome_view_pager_parent);
        Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.bg_screen3));
        snackbar.setAction(R.string.send_mail, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                composeEmail(campus, dept, program, section, level, term);
            }
        });
        snackbar.show();
    }

    public void composeEmail(String campus, String department, String program, String section, String level, String term) {
        String appVersion = null;
        PackageInfo packageInfo = null;
        try {
            packageInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (packageInfo != null) {
            appVersion = packageInfo.versionName;
        }
        String message = "Campus: " + campus.substring(0, 1).toUpperCase() + campus.substring(1, campus.length()).toLowerCase();
        message += "\nDepartment: " + department.toUpperCase();
        message += "\nProgram: " + program.substring(0, 1).toUpperCase() + program.substring(1, program.length()).toLowerCase();
        message += "\nSection: " + section + "\nLevel: " + level + "\nTerm: " + term;
        message += "\nApp version: " + appVersion;
        message += "\nDB version: " + prefManager.getMasterDBVersion();
        message += "\n";
        message += "\n*** Important: Insert your class routine for quicker response ***";
        String subject = getString(R.string.suggestion_email_subject);
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.auth_email)});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void checkUpdate() {
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference(MainActivity.DATABASE_VERSION_TAG);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int newVersion = 0;
                try {
                    newVersion = dataSnapshot.getValue(Integer.class);
                } catch (Exception ignored) {
                    isUpdateSuccessful = false;
                    return;
                }
                final int newDBVersion = newVersion;
                if (prefManager.getMasterDBVersion() < newDBVersion) {
                    isUpdateAvailable = true;
                    DatabaseReference urlReference = firebaseDatabase.getReference(MainActivity.DATABASE_URL_TAG);
                    urlReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String dbURL = null;
                            try {
                                dbURL = dataSnapshot.getValue(String.class);
                            } catch (Exception ignored) {
                            }
                            if (dbURL != null) {
                                String[] arr = new String[]{dbURL, ""+newDBVersion};
                                new DbDownloadTask().execute(arr);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    spinKitView.setVisibility(View.GONE);
                    isUpdateAlreadyExecuted = true;
                    checkText.setText(R.string.already_updated_text);
                    cloud.setImageResource(R.drawable.ic_cloud_done_white_48dp);
                    btnNext.setText(R.string.next);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private class StartUpdateTask extends AsyncTask<Void, Void, Void> {
        private boolean isOnline;
        // TCP/HTTP/DNS (depending on the port, 53=DNS, 80=HTTP, etc.)
        public boolean isOnline() {
            try {
                int timeoutMs = 1500;
                Socket sock = new Socket();
                SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);

                sock.connect(sockaddr, timeoutMs);
                sock.close();

                return true;
            } catch (IOException e) { return false; }
        }

        @Override
        protected Void doInBackground(Void... params) {
            isOnline = isOnline();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (isOnline) {
                if (!isUpdateAlreadyExecuted) {
                    checkUpdate();
                }
            } else {
                if (!isUpdateAlreadyExecuted) {
                    cloud.setImageResource(R.drawable.ic_cloud_download_white_48dp);
                    checkText.setText(R.string.no_internet_text);
                    spinKitView.setVisibility(View.GONE);
                    btnNext.setText(R.string.next);
                }

            }
        }
    }

    private class DbDownloadTask extends AsyncTask<String, Void, Void> {
        private int newDBVersion;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            checkText.setText(R.string.downloading_update_text);

        }

        @Override
        protected Void doInBackground(String... params) {
            File downloadFile = new File(getDatabasePath(MasterDBOnline.UPDATED_DATABASE_NAME).getAbsolutePath());
            if (downloadFile.exists()) {
                downloadFile.delete();
            }
            try {
                downloadFile.createNewFile();
                String dlURL = params[0];
                newDBVersion = Integer.parseInt(params[1]);
                if (dlURL != null) {
                    URL downloadURL = new URL(dlURL);
                    HttpURLConnection conn = (HttpURLConnection) downloadURL
                            .openConnection();
                    int responseCode = conn.getResponseCode();
                    if (responseCode != 200)
                        throw new Exception("Error in connection");
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    FileOutputStream os = new FileOutputStream(downloadFile);
                    byte buffer[] = new byte[1024];
                    int byteCount;
                    while ((byteCount = is.read(buffer)) != -1) {
                        // Write data to file
                        os.write(buffer, 0, byteCount);
                    }
                    os.close();
                    is.close();
                } else {
                    throw new Exception("Error parsing URL");
                }

            } catch (Exception e) {
                e.printStackTrace();
                isUpdateSuccessful = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //If skip button wasn't pressed
            if (!hasSkipped) {
                spinKitView.setVisibility(View.GONE);
                //Forcing db to update by increasing version
                int prevDB = prefManager.getMasterDBVersion();
                prefManager.setMasterDbVersion(newDBVersion);
                prefManager.incrementDatabaseVersion();
                CourseUtils courseUtils = new CourseUtils(getApplicationContext(), true);
                if (!courseUtils.doesTableExist("departments_main")) {
                    isUpdateSuccessful = false;
                    prefManager.setMasterDbVersion(prevDB);
                }
                if (isUpdateAvailable) {
                    if (isUpdateSuccessful) {
                        isUpdateAlreadyExecuted = true;
                        checkText.setText(R.string.update_successful_text);
                        cloud.setImageResource(R.drawable.ic_cloud_done_white_48dp);
                        prefManager.setUpdatedOnline(true);

                    } else {
                        checkText.setText(R.string.update_failed_text);
                    }
                } else {
                    isUpdateAlreadyExecuted = true;
                    checkText.setText(R.string.already_updated_text);
                    cloud.setImageResource(R.drawable.ic_cloud_done_white_48dp);
                }
                btnNext.setText(R.string.next);
            }
        }
    }



}