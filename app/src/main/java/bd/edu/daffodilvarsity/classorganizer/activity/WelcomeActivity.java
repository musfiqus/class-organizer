package bd.edu.daffodilvarsity.classorganizer.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.adapter.SlidePagerAdapter;
import bd.edu.daffodilvarsity.classorganizer.utils.DataChecker;
import bd.edu.daffodilvarsity.classorganizer.utils.DatabaseHelper;
import bd.edu.daffodilvarsity.classorganizer.utils.PrefManager;

public class WelcomeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    private ViewPager viewPager;
    private SlidePagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private Button btnPrevious, btnNext;
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
                R.layout.welcome_slide4};

        // adding bottom dots
        addBottomDots(0);

        /* making notification bar transparent*/
        changeStatusBarColor();

        myViewPagerAdapter = new SlidePagerAdapter(this, layouts);
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
                        viewPager.setCurrentItem(current -1);
                    } else {
                        viewPager.setCurrentItem(current);
                    }
                } else if (current < layouts.length) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                } else {
                    prefManager.saveSemester(getResources().getString(R.string.current_semester));
                    prefManager.saveDatabaseVersion(DatabaseHelper.OFFLINE_DATABASE_VERSION);
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
        // changing the next button text 'NEXT' / 'GOT IT'
        if (position == layouts.length - 1) {
            // last page. make button text to GOT IT
            btnNext.setPadding(0, 0, 32, 0);
            btnNext.setText(getString(R.string.start));
        } else if (position == 0) {
            btnPrevious.setVisibility(View.GONE);
        } else {
            // still pages are left
            btnNext.setText(getString(R.string.next));
            btnPrevious.setVisibility(View.VISIBLE);
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
        snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.bg_screen2));
        snackbar.setAction(R.string.send_mail, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                composeEmail(campus, dept, program, section, level, term);
            }
        });
        snackbar.show();
    }

    public void composeEmail(String campus, String department, String program, String section, String level, String term) {
        String message = "Campus: "+campus+" Department: "+department+" Program: "+program;
        message += "\nSection: " + section + "Level: " + level + "Term: " + term;
        message += "\nPlease insert or attach your routine for quicker response";
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

}