package bd.edu.daffodilvarsity.classorganizer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.polaric.colorful.Colorful;
import org.polaric.colorful.ColorfulActivity;

import java.util.ArrayList;

import bd.edu.daffodilvarsity.classorganizer.data.DayData;
import bd.edu.daffodilvarsity.classorganizer.utils.CourseUtils;
import bd.edu.daffodilvarsity.classorganizer.utils.PrefManager;
import bd.edu.daffodilvarsity.classorganizer.R;

public class EditActivity extends ColorfulActivity {

    private PrefManager prefManager;
    private TextView courseCodeText;
    private EditText courseTitle;
    private EditText editInitial;
    private EditText editRoom;
    private Spinner weekDaySpinner;
    private Spinner startTimeSpinner;
    private Spinner endTimeSpinner;
    private int position = -1;
    private ArrayList<DayData> dayDatas;
    private DayData dayData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_modify);
        setSupportActionBar(toolbar);
        findViewById(R.id.modify_appbar_layout).bringToFront();
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        prefManager = new PrefManager(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            dayData = extras.getParcelable("DAYDATA");
        }
        dayDatas = prefManager.getSavedDayData();
        for (int i = 0; i < dayDatas.size(); i++) {
            if (dayData.equals(dayDatas.get(i))) {
                position = i;
            }
        }

        //Setting course current daydatas
        setupCurrentDay();
        // Making navigation bar colored
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(getResources().getColor(Colorful.getThemeDelegate().getPrimaryColor().getColorRes()));
        }
    }

    private void setupCurrentDay() {
        CourseUtils courseUtils = CourseUtils.getInstance(this);
        courseCodeText = (TextView) findViewById(R.id.course_code_title);
        courseCodeText.setText(dayData.getCourseCode());

        courseTitle = (EditText) findViewById(R.id.edit_course_title);
        courseTitle.setText(dayData.getCourseTitle());

        editInitial = (EditText) findViewById(R.id.edit_initial);
        editInitial.setText(dayData.getTeachersInitial());

        editRoom = (EditText) findViewById(R.id.edit_room);
        editRoom.setText(dayData.getRoomNo());

        //Term spinner
        weekDaySpinner = (Spinner) findViewById(R.id.edit_week_day);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> termAdapter = ArrayAdapter.createFromResource(this, R.array.weekdays, R.layout.spinner_row);
        //Getting the position of current day in spinner
        int spinnerPos = termAdapter.getPosition(dayData.getDay().substring(0, 1).toUpperCase() + dayData.getDay().substring(1).toLowerCase());
        // Specify the layout to use when the list of choices appears
        termAdapter.setDropDownViewResource(R.layout.spinner_row);
        // Apply the adapter to the spinner
        weekDaySpinner.setAdapter(termAdapter);
        weekDaySpinner.setSelection(spinnerPos);

        String[] startEndTime = timeSplitter(dayData.getTime());
        String startTime = startEndTime[0];
        startTime = startTime.substring(0, startTime.length() - 2) + startTime.substring(startTime.length() - 2, startTime.length() - 1);
        String endTime = startEndTime[1];
        endTime = endTime.substring(1, endTime.length());
        //Start time spinner
        startTimeSpinner = (Spinner) findViewById(R.id.modify_time_start);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> startTimeAdapter = new ArrayAdapter<>(this, R.layout.spinner_row, courseUtils.getSpinnerList(CourseUtils.GET_START_TIME));
        //Getting the position of start time in spinner
        int startTimePos = startTimeAdapter.getPosition(startTime);
        startTimeAdapter.setDropDownViewResource(R.layout.spinner_row);
        startTimeSpinner.setAdapter(startTimeAdapter);
        startTimeSpinner.setSelection(startTimePos);

        //End time spinner
        endTimeSpinner = (Spinner) findViewById(R.id.modify_time_end);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> endTimeAdapter = new ArrayAdapter<>(this, R.layout.spinner_row, courseUtils.getSpinnerList(CourseUtils.GET_END_TIME));
        //Getting the position of start time in spinner
        int endTimePos = endTimeAdapter.getPosition(endTime);
        endTimeAdapter.setDropDownViewResource(R.layout.spinner_row);
        endTimeSpinner.setAdapter(endTimeAdapter);
        endTimeSpinner.setSelection(endTimePos);
    }

    private String timeJoiner(String startTime, String endTime) {
        return startTime + " - " + endTime;
    }

    private String[] timeSplitter(String time) {
        return time.split("-");
    }

    private DayData getEditedDay() {
        String newCourseTitle = courseTitle.getText().toString();
        String courseCode = courseCodeText.getText().toString();
        String initial = editInitial.getText().toString();
        String room = editRoom.getText().toString();
        String time = timeJoiner(startTimeSpinner.getSelectedItem().toString(), endTimeSpinner.getSelectedItem().toString());
        String day = weekDaySpinner.getSelectedItem().toString();
        double timeWeight = CourseUtils.getInstance(getApplicationContext()).getTimeWeightFromStart(startTimeSpinner.getSelectedItem().toString());
        return new DayData(courseCode, initial, prefManager.getSection(), prefManager.getLevel(), prefManager.getTerm(), room, time, day, timeWeight, newCourseTitle, dayData.isMuted());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // create an action bar button, done
        getMenuInflater().inflate(R.menu.activity_modify_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        DayData editedDay = getEditedDay();
        if (item.getItemId() == R.id.done_button) {
            //Saving the changed data
            if (position > -1) {
                prefManager.saveModifiedData(editedDay, PrefManager.EDIT_DATA_TAG, false);
                dayDatas.set(position, editedDay);
            }
            prefManager.saveDayData(dayDatas);
            prefManager.saveReCreate(true);
            showSnackBar(this, "Saved");
        } else if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, DayDataDetailActivity.class);
            intent.putExtra("DayDataDetails", (Parcelable) editedDay);
            startActivity(intent);
            finish();
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    //Method to display snackbar properly
    public void showSnackBar(Activity activity, String message) {
        View rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
    }

}
