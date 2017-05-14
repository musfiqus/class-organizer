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
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by musfiqus on 3/22/2017.
 */

public class DayDataAdapter extends ArrayAdapter<DayData> {

    public DayDataAdapter(@NonNull Context context, @NonNull ArrayList<DayData> objects) {
        super(context, 0, objects);
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
                            PrefManager prefManager = new PrefManager(getContext());
                            prefManager.saveEditedDayData(currentClass, false);
                            Snackbar.make(parent, currentClass.getCourseCode() + " saved!", Snackbar.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                });
            }
        });
        return listItemView;
    }
}


