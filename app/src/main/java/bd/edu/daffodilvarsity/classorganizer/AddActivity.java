package bd.edu.daffodilvarsity.classorganizer;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.polaric.colorful.Colorful;
import org.polaric.colorful.ColorfulActivity;

import java.util.ArrayList;

public class AddActivity extends ColorfulActivity {

    private PrefManager prefManager;
    private EditText courseCode;
    private EditText addInitial;
    private EditText addRoom;
    private Spinner weekDaySpinner;
    private Spinner startTimeSpinner;
    private Spinner endTimeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Colorful.applyTheme(this);
        setContentView(R.layout.activity_add);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_modify);
        setSupportActionBar(toolbar);
        findViewById(R.id.modify_appbar_layout).bringToFront();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prefManager = new PrefManager(this);

        setupCurrentView();

        // Making navigation bar colored
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(getResources().getColor(Colorful.getThemeDelegate().getPrimaryColor().getColorRes()));
        }
    }

    private void setupCurrentView() {
        courseCode = (EditText) findViewById(R.id.add_course_code);
        addInitial = (EditText) findViewById(R.id.add_initial);
        addRoom = (EditText) findViewById(R.id.add_room);
        //Weekday
        weekDaySpinner = (Spinner) findViewById(R.id.add_week_day);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> termAdapter = ArrayAdapter.createFromResource(this, R.array.weekdays, R.layout.spinner_row);
        // Specify the layout to use when the list of choices appears
        termAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        weekDaySpinner.setAdapter(termAdapter);
        //Start time spinner
        startTimeSpinner = (Spinner) findViewById(R.id.add_time_start);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> startTimeAdapter = ArrayAdapter.createFromResource(this, R.array.start_time, R.layout.spinner_row);
        //Getting the position of start time in spinner
        startTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        startTimeSpinner.setAdapter(startTimeAdapter);

        //End time spinner
        endTimeSpinner = (Spinner) findViewById(R.id.add_time_end);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> endTimeAdapter = ArrayAdapter.createFromResource(this, R.array.end_time, R.layout.spinner_row);
        //Getting the position of start time in spinner
        endTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
                addRoom.getText().toString().equalsIgnoreCase(" ")) {
            Toast.makeText(this, "Fields can't be empty", Toast.LENGTH_SHORT).show();
            return null;
        } else {
            return new DayData(courseCode.getText().toString(), addInitial.getText().toString(), addRoom.getText().toString(), timeJoiner(startTimeSpinner.getSelectedItem().toString(), endTimeSpinner.getSelectedItem().toString()), weekDaySpinner.getSelectedItem().toString(), timeWeight(startTimeSpinner.getSelectedItem().toString()));
        }

    }

    private double timeWeight(String startTime) {
        switch (startTime) {
            case "08.30 AM":
                return 1.0;
            case "10.00 AM":
                return 2.0;
            case "11.30 AM":
                return 3.0;
            case "01.00 PM":
                return 4.0;
            case "02.30 PM":
                return 5.0;
            case "04.00 PM":
                return 6.0;
            case "09.00 AM":
                return 1.5;
            case "11.00 AM":
                return 2.5;
            case "03.00 PM":
                return 4.5;
            default:
                Log.e("EditActivity", "INVALID START TIME");
                return 0;
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
                prefManager.saveModifiedData(newDay, "add" , false);
                reCreate();
            }
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        reCreate();
    }

    public void reCreate() {
        if (prefManager.getReCreate()) {
            prefManager.saveReCreate(false);
            //Refreshing data on screen by restarting activity, because nothing else seems to work for now
            if (MainActivity.getInstance() != null) {
                MainActivity.getInstance().finish();
            }
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
