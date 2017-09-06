package bd.edu.daffodilvarsity.classorganizer.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.polaric.colorful.Colorful;
import org.polaric.colorful.ColorfulActivity;

import java.util.ArrayList;

import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.data.DayData;
import bd.edu.daffodilvarsity.classorganizer.utils.CourseUtils;
import bd.edu.daffodilvarsity.classorganizer.utils.PrefManager;

public class AddActivity extends ColorfulActivity {

    private PrefManager prefManager;
    private EditText courseCode;
    private EditText courseTitle;
    private EditText addInitial;
    private EditText addRoom;
    private Spinner weekDaySpinner;
    private Spinner startTimeSpinner;
    private Spinner endTimeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_modify);
        setSupportActionBar(toolbar);
        findViewById(R.id.modify_appbar_layout).bringToFront();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        prefManager = new PrefManager(this);

        setupCurrentView();

        // Making navigation bar colored
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(getResources().getColor(Colorful.getThemeDelegate().getPrimaryColor().getColorRes()));
        }
    }

    private void setupCurrentView() {
        CourseUtils courseUtils = CourseUtils.getInstance(this);
        courseCode = (EditText) findViewById(R.id.add_course_code);
        courseTitle = (EditText) findViewById(R.id.add_course_title);
        addInitial = (EditText) findViewById(R.id.add_initial);
        addRoom = (EditText) findViewById(R.id.add_room);
        //Weekday
        weekDaySpinner = (Spinner) findViewById(R.id.add_week_day);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> termAdapter = ArrayAdapter.createFromResource(this, R.array.weekdays, R.layout.spinner_row);
        // Specify the layout to use when the list of choices appears
        termAdapter.setDropDownViewResource(R.layout.spinner_row);
        // Apply the adapter to the spinner
        weekDaySpinner.setAdapter(termAdapter);
        //Start time spinner
        startTimeSpinner = (Spinner) findViewById(R.id.add_time_start);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> startTimeAdapter = new ArrayAdapter<>(this, R.layout.spinner_row, courseUtils.getSpinnerList(CourseUtils.GET_START_TIME));
        //Getting the position of start time in spinner
        startTimeAdapter.setDropDownViewResource(R.layout.spinner_row);
        startTimeSpinner.setAdapter(startTimeAdapter);

        //End time spinner
        endTimeSpinner = (Spinner) findViewById(R.id.add_time_end);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> endTimeAdapter = new ArrayAdapter<>(this, R.layout.spinner_row, courseUtils.getSpinnerList(CourseUtils.GET_END_TIME));
        //Getting the position of start time in spinner
        endTimeAdapter.setDropDownViewResource(R.layout.spinner_row);
        endTimeSpinner.setAdapter(endTimeAdapter);
    }

    private String timeJoiner(String startTime, String endTime) {
        return startTime + " - " + endTime;
    }

    private DayData getNewDay() {
        if (courseCode.getText().toString().equalsIgnoreCase("") ||
                courseCode.getText().toString().equalsIgnoreCase(" ") ||
                addInitial.getText().toString().equalsIgnoreCase("") ||
                addInitial.getText().toString().equalsIgnoreCase(" ") ||
                addRoom.getText().toString().equalsIgnoreCase("") ||
                addRoom.getText().toString().equalsIgnoreCase(" ")||
                courseTitle.getText().toString().equalsIgnoreCase("") ||
                courseTitle.getText().toString().equalsIgnoreCase(" ")) {
            Toast.makeText(this, "Fields can't be empty", Toast.LENGTH_SHORT).show();
            return null;
        } else {
            return new DayData(courseCode.getText().toString(), addInitial.getText().toString(), prefManager.getSection(), prefManager.getLevel(), prefManager.getTerm(), addRoom.getText().toString(), timeJoiner(startTimeSpinner.getSelectedItem().toString(), endTimeSpinner.getSelectedItem().toString()), weekDaySpinner.getSelectedItem().toString(), CourseUtils.getInstance(getApplicationContext()).getTimeWeightFromStart(startTimeSpinner.getSelectedItem().toString()), courseTitle.getText().toString());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // create an action bar button, done
        getMenuInflater().inflate(R.menu.activity_add_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.done_button) {
            //Saving the new Class
            DayData newDay = getNewDay();
            if (newDay != null) {
                ArrayList<DayData> dayDataArrayList = prefManager.getSavedDayData();
                dayDataArrayList.add(newDay);
                prefManager.saveDayData(dayDataArrayList);
                prefManager.saveReCreate(true);
                prefManager.saveSnackData("Added");
                prefManager.saveShowSnack(true);
                prefManager.saveModifiedData(newDay, PrefManager.ADD_DATA_TAG, false);
                finish();
            }
        } else if (item.getItemId() == android.R.id.home) {
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
