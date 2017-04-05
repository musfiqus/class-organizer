package bd.edu.daffodilvarsity.classorganizer;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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

public class ModifyActivity extends ColorfulActivity {

    private PrefManager prefManager;
    private TextView courseCodeText;
    private EditText editInitial;
    private EditText editRoom;
    private Spinner weekDaySpinner;
    private Spinner startTimeSpinner;
    private Spinner endTimeSpinner;
    private int position = -1;
    private ArrayList<DayData> dayDatas;
    private DayData dayData = null;
    private RoutineLoader routineLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Colorful.applyTheme(this);
        setContentView(R.layout.activity_modify);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_modify);
        setSupportActionBar(toolbar);
        findViewById(R.id.modify_appbar_layout).bringToFront();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prefManager = new PrefManager(this);
        Bundle extras = getIntent().getExtras();
        dayData = extras.getParcelable("DAYDATA");
        routineLoader = new RoutineLoader(this);
        dayDatas = prefManager.getSavedDayData();
        for (int i = 0; i < dayDatas.size(); i++) {
            if (dayDatas.get(i).getCourseCode().equalsIgnoreCase(dayData.getCourseCode())) {
                if (dayDatas.get(i).getTeachersInitial().equalsIgnoreCase(dayData.getTeachersInitial())) {
                    if (dayDatas.get(i).getDay().equalsIgnoreCase(dayData.getDay())) {
                        if (dayDatas.get(i).getRoomNo().equalsIgnoreCase(dayData.getRoomNo())) {
                            if (dayDatas.get(i).getDay().equalsIgnoreCase(dayData.getDay())) {
                                position = i;
                            }
                        }
                    }
                }
            }
        }

        //Setting course current daydatas
        setupCurrentDay();
    }

    private void setupCurrentDay() {
        courseCodeText = (TextView) findViewById(R.id.course_code_title);
        courseCodeText.setText(dayData.getCourseCode());

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
        termAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        weekDaySpinner.setAdapter(termAdapter);
        weekDaySpinner.setSelection(spinnerPos);

        String[] startEndTime = timeSplitter(dayData.getTime());
        String startTime = startEndTime[0];
        String endTime = startEndTime[1];

        //Start time spinner
        startTimeSpinner = (Spinner) findViewById(R.id.modify_time_start);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> startTimeAdapter = ArrayAdapter.createFromResource(this, R.array.start_time, R.layout.spinner_row);
        //Getting the position of start time in spinner
        int startTimePos = startTimeAdapter.getPosition(startTime);
        startTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        startTimeSpinner.setAdapter(startTimeAdapter);
        startTimeSpinner.setSelection(startTimePos);

        //End time spinner
        endTimeSpinner = (Spinner) findViewById(R.id.modify_time_end);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> endTimeAdapter = ArrayAdapter.createFromResource(this, R.array.end_time, R.layout.spinner_row);
        //Getting the position of start time in spinner
        int endTimePos = endTimeAdapter.getPosition(endTime);
        endTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        endTimeSpinner.setAdapter(endTimeAdapter);
        endTimeSpinner.setSelection(endTimePos);
    }

    private String timeJoiner(String startTime, String endTime) {
        return startTime + "-" + endTime;
    }

    private String[] timeSplitter(String time) {
        return time.split("-");
    }

    private DayData getEditedDay() {
        String courseCode = courseCodeText.getText().toString();
        String initial = editInitial.getText().toString();
        String room = editRoom.getText().toString();
        String time = timeJoiner(startTimeSpinner.getSelectedItem().toString(), endTimeSpinner.getSelectedItem().toString());
        String day = weekDaySpinner.getSelectedItem().toString();
        double timeWeight = timeWeight(startTimeSpinner.getSelectedItem().toString());
        return new DayData(courseCode, initial, room, time, day, timeWeight);
    }

    private double timeWeight(String startTime) {
        switch (startTime) {
            case "08.30":
                return 1.0;
            case "10.00":
                return 2.0;
            case "11.30":
                return 3.0;
            case "01.00":
                return 4.0;
            case "02.30":
                return 5.0;
            case "04.00":
                return 6.0;
            case "09.00":
                return 1.5;
            case "11.00":
                return 2.5;
            case "03.00":
                return 4.5;
            default:
                Log.e("ModifyActivity", "INVALID START TIME");
                return 0;
        }
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
        if (item.getItemId() == R.id.done_button) {
            //Saving the changed data
            DayData editedDay = getEditedDay();
            if (position > -1) {
                dayDatas.set(position, editedDay);
            }
            prefManager.saveDayData(dayDatas);
            prefManager.saveReCreate(true);
            showSnackBar(this, "Saved");

        } else if (item.getItemId() == R.id.delete_button) {
            //Show confirmation
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Confirm deletion");
            builder.setMessage("Are you sure?");

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    if (position > -1) {
                        dayDatas.remove(position);
                    }
                    prefManager.saveDayData(dayDatas);
                    //Refreshing data on screen by restarting activity, because nothing else seems to work for now
                    MainActivity.getInstance().finish();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    prefManager.saveSnackData("Deleted");
                    prefManager.saveShowSnack(true);
                    finish();
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    // Do nothing
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (prefManager.getReCreate()) {
            Log.e("Pause", "Called");
            prefManager.saveReCreate(false);
            //Refreshing data on screen by restarting activity, because nothing else seems to work for now
            MainActivity.getInstance().finish();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("SNACKBAR", "Saved");
            startActivity(intent);
            finish();
        }
    }

    //Method to display snackbar properly
    public void showSnackBar(Activity activity, String message) {
        View rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
    }
}
