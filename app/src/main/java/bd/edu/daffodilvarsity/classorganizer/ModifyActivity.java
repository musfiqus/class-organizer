package bd.edu.daffodilvarsity.classorganizer;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class ModifyActivity extends AppCompatActivity {

    private PrefManager prefManager;
    private TextView courseCodeText;
    private EditText editInitial;
    private EditText editRoom;
    private EditText editTime;
    private Spinner weekDaySpinner;
    private int position = -1;
    private ArrayList<DayData> dayDatas;
    private DayData dayData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prefManager = new PrefManager(this);
        Bundle extras = getIntent().getExtras();
        dayData = extras.getParcelable("DAYDATA");
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

        editTime = (EditText) findViewById(R.id.edit_time);
        editTime.setText(dayData.getTime());

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
    }

    private DayData getEditedDay() {
        String courseCode = courseCodeText.getText().toString();
        String initial = editInitial.getText().toString();
        String room = editRoom.getText().toString();
        String time = editTime.getText().toString();
        String day = weekDaySpinner.getSelectedItem().toString();
        return new DayData(courseCode, initial, room, time, day);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // create an action bar button, done
        getMenuInflater().inflate(R.menu.activity_modify_done, menu);
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
            //Refreshing data on screen by restarting activity, because nothing else seems to work for now
            MainActivity.getInstance().finish();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("SNACKBAR", "Saved");
            startActivity(intent);
            finish();
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
                    intent.putExtra("SNACKBAR", "Deleted");
                    startActivity(intent);
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


}
