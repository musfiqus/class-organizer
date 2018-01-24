package bd.edu.daffodilvarsity.classorganizer.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.data.DayData;
import bd.edu.daffodilvarsity.classorganizer.utils.CourseUtils;
import bd.edu.daffodilvarsity.classorganizer.utils.CustomFilterArrayAdapter;
import bd.edu.daffodilvarsity.classorganizer.utils.PrefManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragmentRoom#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragmentRoom extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "SearchFragmentRoom";

    private View mView;
    private CardView mOptionContainer;
    private CardView mResultContainer;
    private View mOptionLayout;
    private View mResultLayout;

    //Room vars
    private boolean isRoomLayout;
    private Spinner mSearchTypeSpinner;
    private Spinner mSearchDaySpinner;
    private Spinner mSearchTimeSpinner;
    private AppCompatAutoCompleteTextView mSearchByRoom;
    private RelativeLayout mSearchByRoomLayout;
    private RelativeLayout mSearchByTimeLayout;
    private TextView resultTitle;
    private ResultDayDataAdapter adapter;

    public SearchFragmentRoom() {
        // Required empty public constructor
    }

    public static SearchFragmentRoom newInstance() {
        return new SearchFragmentRoom();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_search, container, false);
        }
        //Set container
        setupFAB();
        setupOptionContainer();
        setupOptionLayout(inflater);
        setupResultLayout(inflater);

        // Inflate the layout for this fragment
        return mView;
    }



    private void setupOptionContainer() {
        mOptionContainer = mView.findViewById(R.id.search_options_container);
    }

    private void setupOptionLayout(@NonNull LayoutInflater inflater) {
        mOptionLayout = inflater.inflate(R.layout.layout_search_option_room, mOptionContainer, true);
        mSearchByRoomLayout = mOptionLayout.findViewById(R.id.search_option_room_by_room_layout);
        mSearchByTimeLayout = mOptionLayout.findViewById(R.id.search_option_room_by_time_layout);
        mSearchByRoom = mSearchByRoomLayout.findViewById(R.id.search_room_input);
        mSearchDaySpinner = mSearchByTimeLayout.findViewById(R.id.search_option_room_weekday);
        mSearchTimeSpinner = mSearchByTimeLayout.findViewById(R.id.search_option_room_time);
        mSearchTypeSpinner = mOptionLayout.findViewById(R.id.search_option_room_type);
        //set adapters on the spinners we can
        mSearchTypeSpinner.setAdapter(ArrayAdapter.createFromResource(mOptionLayout.getContext(), R.array.search_room_type, R.layout.spinner_row));
        mSearchDaySpinner.setAdapter(ArrayAdapter.createFromResource(mOptionLayout.getContext(), R.array.weekdays, R.layout.spinner_row));
        ArrayAdapter<String> startTimeAdapter = new ArrayAdapter<>(mOptionLayout.getContext(),
                R.layout.spinner_row, CourseUtils.getInstance(mOptionLayout.getContext()).getSpinnerList(CourseUtils.GET_START_TIME));
        mSearchTimeSpinner.setAdapter(startTimeAdapter);
        mSearchTypeSpinner.setOnItemSelectedListener(this);
        PrefManager prefManager = new PrefManager(mOptionLayout.getContext());
        ArrayList<String> rooms = CourseUtils.getInstance(mOptionLayout.getContext()).getRoomNo(prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram());
        CustomFilterArrayAdapter adapter = new CustomFilterArrayAdapter(mOptionLayout.getContext(), R.layout.spinner_row, rooms);
        mSearchByRoom.setAdapter(adapter);

    }

    private void setupResultLayout(LayoutInflater inflater) {
        mResultContainer = mView.findViewById(R.id.search_results_container);
        resultTitle = mView.findViewById(R.id.result_layout_title);
        mResultLayout = inflater.inflate(R.layout.layout_search_result, mResultContainer, true);
        RecyclerView recyclerView = (RecyclerView) mResultLayout.findViewById(R.id.class_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mResultLayout.getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ResultDayDataAdapter(mView.getContext(), R.layout.list_item);
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);
        //hiding result section on first time SUPPRRRISSE MADAPAKA
        showResultLayout(false);
    }

    private void setupFAB() {
        FloatingActionButton fab = mView.findViewById(R.id.search_fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //do stuff
                    if (isRoomLayout) {
                        searchByRoom();
                    } else {
                        searchByTime();
                    }
                }
            });
        }
    }

    private void searchByRoom() {

        if (mSearchByRoom != null) {
            if (validateInput()) {
                showResultLayout(true);
                String room = mSearchByRoom.getText().toString();
                CourseUtils courseUtils = CourseUtils.getInstance(mOptionLayout.getContext());
                PrefManager prefManager = new PrefManager(mOptionLayout.getContext());
                ArrayList<DayData> result = courseUtils.getFreeRoomsByRoom(prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram(),room);
                if (result.size() == 0) {
                    noResult(true);
                    adapter.loadResult(result);
                } else {
                    noResult(false);
                    adapter.loadResult(result);
                }
            }
        }
    }

    private void searchByTime() {
        if (mSearchTimeSpinner != null && mSearchDaySpinner != null) {
            showResultLayout(true);
            CourseUtils courseUtils = CourseUtils.getInstance(mOptionContainer.getContext());
            PrefManager prefManager = new PrefManager(mOptionContainer.getContext());
            String day = mSearchDaySpinner.getSelectedItem().toString();

            double timeWeight = courseUtils.getTimeWeightFromStart(mSearchTimeSpinner.getSelectedItem().toString());
            ArrayList<DayData> result = courseUtils.getFreeRoomsByTime(prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram(), day, String.valueOf(timeWeight));
            if (result.size() == 0) {
                noResult(true);
            } else {
                noResult(false);
                adapter.loadResult(result);
            }
        }
    }


    private void noResult(boolean result) {
        TextView noResult = mResultLayout.findViewById(R.id.search_routine_result_zero);
        if (noResult != null) {
            if (result) {
                noResult.setVisibility(View.VISIBLE);
            } else {
                noResult.setVisibility(View.GONE);
            }
        }
    }

    private void showResultLayout(boolean yes) {
        if (yes) {
            if (resultTitle != null && resultTitle.getVisibility() == View.GONE) {
                resultTitle.setVisibility(View.VISIBLE);
            }
            if (mResultLayout != null && mResultLayout.getVisibility() == View.GONE) {
                mResultLayout.setVisibility(View.VISIBLE);
            }
        } else {
            if (resultTitle != null && resultTitle.getVisibility() == View.VISIBLE) {
                resultTitle.setVisibility(View.GONE);
            }
            if (mResultLayout != null && mResultLayout.getVisibility() == View.VISIBLE) {
                mResultLayout.setVisibility(View.GONE);
            }
        }

    }

    private void showByRoomLayout(boolean yes) {
        if (mSearchByRoomLayout != null && mSearchByTimeLayout != null) {
            if (yes) {
                mSearchByRoomLayout.setVisibility(View.VISIBLE);
                mSearchByTimeLayout.setVisibility(View.GONE);
            } else {
                mSearchByRoomLayout.setVisibility(View.GONE);
                mSearchByTimeLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private boolean validateInput() {
        boolean valid = true;

        if (mSearchByRoom == null) {
            return false;
        }
        String searchPhrase = mSearchByRoom.getText().toString();
        if (searchPhrase.isEmpty()) {
            mSearchByRoom.setError("enter a keyword");
            valid = false;
        } else {
            mSearchByRoom.setError(null);
        }

        return valid;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.search_option_room_type) {
            showResultLayout(false);
            isRoomLayout = parent.getSelectedItemPosition() == 0;
            showByRoomLayout(isRoomLayout);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public class ResultDayDataAdapter extends RecyclerView.Adapter<SearchFragmentRoom.DayDataHolder> {

        private ArrayList<DayData> dayDataArrayList;
        private final ArrayList<DayData> copyOfDayDataList;
        private Context context;
        private int itemResource;

        public ResultDayDataAdapter(Context context, int itemResource) {
            this.dayDataArrayList = new ArrayList<>();
            this.context = context;
            this.itemResource = itemResource;
            this.copyOfDayDataList = new ArrayList<>();
            this.copyOfDayDataList.addAll(dayDataArrayList);
        }

        @Override
        public SearchFragmentRoom.DayDataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(itemResource, parent, false);
            return new SearchFragmentRoom.DayDataHolder(context, view, parent);
        }

        @Override
        public void onBindViewHolder(final SearchFragmentRoom.DayDataHolder holder, int position) {
            final DayData dayData = dayDataArrayList.get(position);
            holder.bindDayData(dayData);
        }

        @Override
        public int getItemCount() {
            return dayDataArrayList.size();
        }

        public void loadResult(ArrayList<DayData> result) {
            if (result != null) {
                dayDataArrayList.clear();
                dayDataArrayList.addAll(result);
            }
            notifyDataSetChanged();
        }

    }

    public static class DayDataHolder extends RecyclerView.ViewHolder {
        private Context context;
        private TextView roomNoView;
        private TextView dayView;
        private TextView statusView;
        private TextView timeTextView;
        private TextView statusLabel;
        private ViewGroup parent;
        private DayData dayData;
        private final View mView;

        public DayDataHolder(Context context, View itemView, ViewGroup parent) {
            super(itemView);
            this.context = context;
            this.roomNoView = (TextView) itemView.findViewById(R.id.course_code);
            this.dayView = (TextView) itemView.findViewById(R.id.teachers_initial);
            this.statusView = (TextView) itemView.findViewById(R.id.room_no);
            this.timeTextView = (TextView) itemView.findViewById(R.id.schedule);
            this.statusLabel = (TextView) itemView.findViewById(R.id.room_label);
            this.parent = parent;
            this.mView = itemView;
        }

        public void bindDayData(final DayData dayData) {
            this.dayData = dayData;
            this.roomNoView.setText(this.dayData.getRoomNo());
            this.dayView.setText(this.dayData.getDay());
            this.statusView.setText(R.string.available);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            boolean isRamadan = preferences.getBoolean("ramadan_preference", false);
            if (isRamadan) {
                this.timeTextView.setText(convertToRamadanTime(this.dayData.getTime(), this.dayData.getTimeWeight()));
            } else {
                this.timeTextView.setText(this.dayData.getTime());
            }
            statusLabel.setText(R.string.status);
            statusLabel.setAllCaps(true);

        }

        public static String convertToRamadanTime(String normalTime, double timeWeight) {
            String startTime = normalTime.substring(0, 8);
            String endTime = normalTime.substring(normalTime.length() - 8, normalTime.length());
            if (timeWeight == 1.5 || timeWeight == 2.5 || timeWeight == 3.5 || timeWeight == 4.5) {
                if (startTime.equalsIgnoreCase("09.00 AM")) {
                    startTime = "09.30 AM";
                } else if (startTime.equalsIgnoreCase("11.00 AM")) {
                    startTime = "11.00 AM";
                } else if (startTime.equalsIgnoreCase("01.00 PM")) {
                    startTime = "12.30 PM";
                } else if (startTime.equalsIgnoreCase("03.00 PM")) {
                    startTime = "02.00 PM";
                }
                if (endTime.equalsIgnoreCase("11.00 AM")) {
                    endTime = "11.00 AM";
                } else if (endTime.equalsIgnoreCase("01.00 PM")) {
                    endTime = "12.30 AM";
                } else if (endTime.equalsIgnoreCase("03.00 PM")) {
                    endTime = "02.00 PM";
                } else if (endTime.equalsIgnoreCase("05.00 PM")) {
                    endTime = "03.30 PM";
                }
                return startTime + " - " +endTime;
            }
            if (startTime.equalsIgnoreCase("08.30 AM")) {
                startTime = "09.30 AM";
            } else if (startTime.equalsIgnoreCase("10.00 AM")) {
                startTime = "10.25 AM";
            } else if (startTime.equalsIgnoreCase("11.30 AM")) {
                startTime = "11.20 AM";
            } else if (startTime.equalsIgnoreCase("01.00 PM")) {
                startTime = "12.15 PM";
            } else if (startTime.equalsIgnoreCase("02.30 PM")) {
                startTime = "01.40 PM";
            } else if (startTime.equalsIgnoreCase("04.00 PM")) {
                startTime = "02.35 PM";
            } else if (startTime.equalsIgnoreCase("06.00 PM")) {
                startTime = "03.30 PM";
            } else if (startTime.equalsIgnoreCase("07.30 PM")) {
                startTime = "04.30 PM";
            }

            if (endTime.equalsIgnoreCase("10.00 AM")) {
                endTime = "10.25 AM";
            } else if (endTime.equalsIgnoreCase("11.30 AM")) {
                endTime = "11.20 AM";
            } else if (endTime.equalsIgnoreCase("01.00 PM")) {
                endTime = "12.15 PM";
            } else if (endTime.equalsIgnoreCase("02.30 PM")) {
                endTime = "01.10 PM";
            } else if (endTime.equalsIgnoreCase("04.00 PM")) {
                endTime = "02.35 PM";
            } else if (endTime.equalsIgnoreCase("05.30 PM")) {
                endTime = "03.30 PM";
            } else if (endTime.equalsIgnoreCase("07.30 PM")) {
                endTime = "04.30 PM";
            } else if (endTime.equalsIgnoreCase("09.00 PM")) {
                endTime = "05.30 PM";
            }
            return startTime + " - " +endTime;
        }
    }
}
