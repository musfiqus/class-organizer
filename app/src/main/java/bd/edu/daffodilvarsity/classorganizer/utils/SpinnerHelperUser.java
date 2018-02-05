package bd.edu.daffodilvarsity.classorganizer.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import bd.edu.daffodilvarsity.classorganizer.R;

/**
 * Created by musfiqus on 1/6/2018.
 */

public class SpinnerHelperUser implements AdapterView.OnItemSelectedListener{
    private static final String TAG = "SpinnerHelperUser";

    private boolean isStudent = true;
    private Spinner userTypeSpinner;
    private Context context;
    private View view;
    private PrefManager prefManager;
    private OnUserChangeListener userChangeListener;

    public SpinnerHelperUser(Context context, View view) {
        this.context = context;
        this.view = view;
        this.prefManager = new PrefManager(context);
    }
    public SpinnerHelperUser(Context context, View view, OnUserChangeListener userChangeListener) {
        this.context = context;
        this.view = view;
        this.prefManager = new PrefManager(context);
        this.userChangeListener = userChangeListener;
    }

    public void setupUser() {
        userTypeSpinner = (Spinner) view.findViewById(R.id.user_type_selection);
        userTypeSpinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.user_type, R.layout.spinner_row);
        userTypeSpinner.setAdapter(adapter);
    }

    public void setUserTypeLabelBlack() {
        TextView userTypeLabel = (TextView) view.findViewById(R.id.user_type_spinner_label);
        if (userTypeLabel != null) {
            userTypeLabel.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
        //setting is teacher or student
        if (parent.getId() == R.id.user_type_selection) {
            String selection = parent.getSelectedItem().toString();
            if (selection != null) {
                //if selection contains student, user type is student
                isStudent = selection.toLowerCase().contains("student");
                Log.e(TAG, "onItemSelected: "+isStudent );
                prefManager.setUserType(isStudent);
                if (userChangeListener != null) {
                    userChangeListener.onUserChange(isStudent);
                }
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public boolean isStudent() {
        return isStudent;
    }

    public interface OnUserChangeListener {
        void onUserChange(boolean isStudent);
    }
}
