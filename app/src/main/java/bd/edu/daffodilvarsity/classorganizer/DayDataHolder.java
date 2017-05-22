package bd.edu.daffodilvarsity.classorganizer;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
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
 * Created by musfiqus on 5/20/2017.
 */

public class DayDataHolder extends RecyclerView.ViewHolder {
    private Context context;
    private TextView courseCodeTextView;
    private TextView teachersInitialTextView;
    private TextView roomNoTextView;
    private TextView timeTextView;
    private ImageButton popupButton;
    private PrefManager prefManager;
    private ViewGroup parent;
    private DayData dayData;

    public DayDataHolder(Context context, View itemView, ViewGroup parent) {
        super(itemView);
        this.context = context;
        this.courseCodeTextView = (TextView) itemView.findViewById(R.id.course_code);
        this.teachersInitialTextView = (TextView) itemView.findViewById(R.id.teachers_initial);
        this.roomNoTextView = (TextView) itemView.findViewById(R.id.room_no);
        this.timeTextView = (TextView) itemView.findViewById(R.id.schedule);
        this.popupButton = (ImageButton) itemView.findViewById(R.id.popup_list_button);
        this.prefManager = new PrefManager(context);
        this.parent = parent;
    }

    public void bindDayData(final DayData dayData) {
        this.dayData = dayData;
        this.courseCodeTextView.setText(this.dayData.getCourseCode());
        this.teachersInitialTextView.setText(this.dayData.getTeachersInitial());
        this.roomNoTextView.setText(this.dayData.getRoomNo());
        this.timeTextView.setText(this.dayData.getTime());
        this.popupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.edit_class) {
                            Intent intent = new Intent(v.getContext(), EditActivity.class);
                            intent.putExtra("DAYDATA", (Parcelable) dayData);
                            v.getContext().startActivity(intent);
                        } else if (item.getItemId() == R.id.save_class) {
                            MaterialDialog.Builder builder = new MaterialDialog.Builder(v.getContext());
                            builder.title("Save to");
                            View dialogView = LayoutInflater.from(context).inflate(R.layout.class_spinner_layout, null);
                            TextView levelLabel = (TextView) dialogView.findViewById(R.id.level_spinner_label);
                            levelLabel.setTextColor(dialogView.getResources().getColor(android.R.color.black));
                            TextView termLabel = (TextView) dialogView.findViewById(R.id.term_spinner_label);
                            termLabel.setTextColor(dialogView.getResources().getColor(android.R.color.black));
                            TextView sectionLabel = (TextView) dialogView.findViewById(R.id.section_spinner_label);
                            sectionLabel.setTextColor(dialogView.getResources().getColor(android.R.color.black));
                            final Spinner sectionSpinner = (Spinner) dialogView.findViewById(R.id.section_selection);
                            final Spinner levelSpinner = (Spinner) dialogView.findViewById(R.id.level_spinner);
                            final Spinner termSpinner = (Spinner) dialogView.findViewById(R.id.term_spinner);
                            ArrayAdapter<CharSequence> termAdapter = ArrayAdapter.createFromResource(context, R.array.term_array, R.layout.spinner_row);
                            termAdapter.setDropDownViewResource(R.layout.spinner_row);
                            termSpinner.setAdapter(termAdapter);
                            termSpinner.setSelection(prefManager.getTerm());

                            ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(context, 0);
                            int sectionPosition = -1;

                            if (prefManager.getCampus().equalsIgnoreCase("main")) {
                                if (prefManager.getDept().equalsIgnoreCase("cse")) {
                                    if (prefManager.getProgram().equalsIgnoreCase("day")) {
                                        adapter = ArrayAdapter.createFromResource(context, R.array.cse_main_day_level_array, R.layout.spinner_row);
                                    } else if (prefManager.getProgram().equalsIgnoreCase("eve")) {
                                        adapter = ArrayAdapter.createFromResource(context, R.array.cse_main_day_level_array, R.layout.spinner_row);
                                    }
                                    ArrayAdapter<CharSequence> sectionAdapter = ArrayAdapter.createFromResource(context, R.array.cse_main_day_section_array, R.layout.spinner_row);
                                    sectionSpinner.setAdapter(sectionAdapter);
                                    String[] sectionListString = dialogView.getResources().getStringArray(R.array.cse_main_day_section_array);
                                    ArrayList<String> sectionList = new ArrayList<>(Arrays.asList(sectionListString));
                                    for (int i = 0; i < sectionList.size(); i++) {
                                        if (sectionList.get(i).equalsIgnoreCase(prefManager.getSection())) {
                                            sectionPosition = i;
                                        }
                                    }
                                    sectionSpinner.setSelection(sectionPosition);
                                }
                            } else if (prefManager.getCampus().equalsIgnoreCase("perm")) {
                                if (prefManager.getDept().equalsIgnoreCase("cse")) {
                                    if (prefManager.getProgram().equalsIgnoreCase("eve")) {
                                        ArrayAdapter<CharSequence> sectionAdapter = ArrayAdapter.createFromResource(context, R.array.cse_perm_section_array, R.layout.spinner_row);
                                        sectionSpinner.setAdapter(sectionAdapter);
                                        String[] sectionListString = dialogView.getResources().getStringArray(R.array.cse_perm_section_array);
                                        ArrayList<String> sectionList = new ArrayList<>(Arrays.asList(sectionListString));
                                        for (int i = 0; i < sectionList.size(); i++) {
                                            if (sectionList.get(i).equalsIgnoreCase(prefManager.getSection())) {
                                                sectionPosition = i;
                                            }
                                        }
                                        sectionSpinner.setSelection(sectionPosition);
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
                                    DayData toSave = new DayData(dayData.getCourseCode(), dayData.getTeachersInitial(), sectionSpinner.getSelectedItem().toString(), levelSpinner.getSelectedItemPosition(), termSpinner.getSelectedItemPosition(), dayData.getRoomNo(), dayData.getTime(), dayData.getDay(), dayData.getTimeWeight(), null);
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
    }
}

