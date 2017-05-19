package bd.edu.daffodilvarsity.classorganizer;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by musfiqus on 3/22/2017.
 */

public class DayDataAdapter extends ArrayAdapter<DayData> {
    private PrefManager prefManager;
    public DayDataAdapter(@NonNull Context context, @NonNull ArrayList<DayData> objects) {
        super(context, 0, objects);
        prefManager = new PrefManager(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        // Get the CourseData object located at this position in the list
        final DayData currentClass = getItem(position);
        assert currentClass != null;

        // Find the TextView in the list_item.xml layout with the ID course_code.
        TextView courseCodeTextView = (TextView) listItemView.findViewById(R.id.course_code);
        // Get the course code from the currentClass object and set this text on
        // the course code TextView.

        courseCodeTextView.setText(currentClass.getCourseCode());

        // Find the TextView in the list_item.xml layout with the ID teachers_initial.
        TextView teachersInitialTextView = (TextView) listItemView.findViewById(R.id.teachers_initial);
        // Get the teachers initial from the currentClass object and set this text on
        // the teachers initial TextView.
        teachersInitialTextView.setText(currentClass.getTeachersInitial());

        // Find the TextView in the list_item.xml layout with the ID teachers_initial.
        TextView roomNoTextView = (TextView) listItemView.findViewById(R.id.room_no);
        // Get the teachers initial from the currentClass object and set this text on
        // the teachers initial TextView.
        roomNoTextView.setText(currentClass.getRoomNo());

        // Find the TextView in the list_item.xml layout with the ID teachers_initial.
        TextView timeTextView = (TextView) listItemView.findViewById(R.id.schedule);
        // Get the teachers initial from the currentClass object and set this text on
        // the teachers initial TextView.
        timeTextView.setText(currentClass.getTime());

        final ImageButton popupButton = (ImageButton) listItemView.findViewById(R.id.popup_list_button);
        popupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                PopupMenu popupMenu = new PopupMenu(getContext(), v);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.edit_class) {
                            Intent intent = new Intent(v.getContext(), EditActivity.class);
                            intent.putExtra("DAYDATA", (Parcelable) currentClass);
                            v.getContext().startActivity(intent);
                        } else if (item.getItemId() == R.id.save_class) {
                            MaterialDialog.Builder builder = new MaterialDialog.Builder(v.getContext());
                            builder.title("Save to");
                            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.class_spinner_layout, parent, false);
                            TextView levelLabel = (TextView) dialogView.findViewById(R.id.level_spinner_label);
                            levelLabel.setTextColor(dialogView.getResources().getColor(android.R.color.black));
                            TextView termLabel = (TextView) dialogView.findViewById(R.id.term_spinner_label);
                            termLabel.setTextColor(dialogView.getResources().getColor(android.R.color.black));
                            TextView sectionLabel = (TextView) dialogView.findViewById(R.id.section_spinner_label);
                            sectionLabel.setTextColor(dialogView.getResources().getColor(android.R.color.black));
                            final Spinner sectionSpinner = (Spinner) dialogView.findViewById(R.id.section_selection);
                            final Spinner levelSpinner = (Spinner) dialogView.findViewById(R.id.level_spinner);
                            final Spinner termSpinner = (Spinner) dialogView.findViewById(R.id.term_spinner);
                            ArrayAdapter<CharSequence> termAdapter = ArrayAdapter.createFromResource(getContext(), R.array.term_array, R.layout.spinner_row);
                            termAdapter.setDropDownViewResource(R.layout.spinner_row);
                            termSpinner.setAdapter(termAdapter);
                            termSpinner.setSelection(prefManager.getTerm());
                            ArrayAdapter<CharSequence> sectionAdapter = ArrayAdapter.createFromResource(getContext(), R.array.cse_main_day_section_array, R.layout.spinner_row);
                            sectionSpinner.setAdapter(sectionAdapter);
                            String[] sectionListString = dialogView.getResources().getStringArray(R.array.cse_main_day_section_array);
                            ArrayList<String> sectionList = new ArrayList<>(Arrays.asList(sectionListString));
                            ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getContext(), 0);
                            int sectionPosition = -1;
                            for (int i = 0; i < sectionList.size(); i++) {
                                if (sectionList.get(i).equalsIgnoreCase(prefManager.getSection())) {
                                    sectionPosition = i;
                                }
                            }
                            sectionSpinner.setSelection(sectionPosition);
                            if (prefManager.getCampus().equalsIgnoreCase("main")) {
                                if (prefManager.getDept().equalsIgnoreCase("cse")) {
                                    if (prefManager.getProgram().equalsIgnoreCase("day")) {
                                        adapter = ArrayAdapter.createFromResource(getContext(), R.array.cse_main_day_level_array, R.layout.spinner_row);
                                    } else if (prefManager.getProgram().equalsIgnoreCase("eve")) {
                                        adapter = ArrayAdapter.createFromResource(getContext(), R.array.cse_main_day_level_array, R.layout.spinner_row);
                                    }
                                }
                            }
                            adapter.setDropDownViewResource(R.layout.spinner_row);
                            levelSpinner.setAdapter(adapter);
                            levelSpinner.setSelection(prefManager.getLevel());
                            builder.customView(dialogView, true);
                            builder.positiveText("SAVE");
                            builder.negativeText("CANCEL");
                            builder.onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                                    DayData toSave = new DayData(currentClass.getCourseCode(), currentClass.getTeachersInitial(), sectionSpinner.getSelectedItem().toString(), levelSpinner.getSelectedItemPosition(), termSpinner.getSelectedItemPosition(), currentClass.getRoomNo(), currentClass.getTime(), currentClass.getDay(), currentClass.getTimeWeight());
                                    prefManager.saveModifiedData(toSave, PrefManager.SAVE_DATA_TAG, false);
                                    Snackbar.make(parent, toSave.getCourseCode() + " saved!", Snackbar.LENGTH_SHORT).show();
                                }
                            });
                            MaterialDialog dialog = builder.build();
                            dialog.show();
                        }
                        return true;
                    }
                });
            }
        });
        return listItemView;
    }
}


