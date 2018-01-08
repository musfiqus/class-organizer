package bd.edu.daffodilvarsity.classorganizer.utils;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import bd.edu.daffodilvarsity.classorganizer.R;

/**
 * Created by musfiqus on 1/6/2018.
 */

public class UserTypeHelper implements AdapterView.OnItemSelectedListener{
    private boolean isStudent = true;
    private Spinner userTypeSpinner;
    private Context context;
    private View view;
    private PrefManager prefManager;

    public UserTypeHelper (Context context, View view) {
        this.context = context;
        this.view = view;
        this.prefManager = new PrefManager(context);
    }

    public void setupUser() {
        userTypeSpinner = (Spinner) view.findViewById(R.id.user_type_selection);
        userTypeSpinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.user_type, R.layout.spinner_row);
        userTypeSpinner.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
        //setting is teacher or student
        if (parent.getId() == R.id.user_type_selection) {
            String selection = parent.getSelectedItem().toString();
            if (selection != null) {
                //if selection contains student, user type is student
                isStudent = selection.toLowerCase().contains("student");
                prefManager.setUserType(isStudent);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public boolean isStudent() {
        return isStudent;
    }
}
