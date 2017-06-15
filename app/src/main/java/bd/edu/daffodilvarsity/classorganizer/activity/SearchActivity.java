package bd.edu.daffodilvarsity.classorganizer.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.polaric.colorful.ColorfulActivity;

import java.util.ArrayList;

import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.adapter.DayDataAdapter;
import bd.edu.daffodilvarsity.classorganizer.data.DayData;
import bd.edu.daffodilvarsity.classorganizer.utils.PrefManager;
import bd.edu.daffodilvarsity.classorganizer.utils.RoutineLoader;

public class SearchActivity extends ColorfulActivity {
    private SearchView searchView;
    private DayDataAdapter adapter;
    private boolean searchByCode;
    private boolean searchByTitle;
    private boolean searchByTeacher;
    private boolean searchByRoom;
    private TextView searchEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_search);
        setSupportActionBar(toolbar);
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        searchEmpty = (TextView) findViewById(R.id.search_result_empty);
        searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setIconified(false);
        searchView.onActionViewExpanded();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query, searchByCode, searchByTitle, searchByTeacher, searchByRoom);
                if (adapter.dayDataSize() > 0) {
                    searchEmpty.setVisibility(View.INVISIBLE);
                } else {
                    searchEmpty.setVisibility(View.VISIBLE);
                    searchEmpty.setText(R.string.empty_search_hint);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText,searchByCode, searchByTitle, searchByTeacher, searchByRoom);
                if (adapter.dayDataSize() > 0) {
                    searchEmpty.setVisibility(View.INVISIBLE);
                } else {
                    searchEmpty.setVisibility(View.VISIBLE);
                    searchEmpty.setText(R.string.not_found_search_hint);
                }
                return true;
            }
        });

        PrefManager prefManager = new PrefManager(this);
        RoutineLoader routineLoader = new RoutineLoader(prefManager.getLevel(), prefManager.getTerm(), prefManager.getSection(), this, prefManager.getDept(), prefManager.getCampus(), prefManager.getProgram());
        ArrayList<DayData> loadedData = routineLoader.loadRoutine(true);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.class_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DayDataAdapter(loadedData, this, R.layout.list_item);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        adapter.clearDayData();
        if (adapter.dayDataSize() > 0) {
            searchEmpty.setVisibility(View.INVISIBLE);
        } else {
            searchEmpty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_search_menu, menu);
        MenuItem searchByCode = menu.findItem(R.id.search_by_code);
        MenuItem searchByTitle = menu.findItem(R.id.search_by_title);
        MenuItem searchByTeacher = menu.findItem(R.id.search_by_teacher);
        MenuItem searchByRoom = menu.findItem(R.id.search_by_room);
        this.searchByCode = searchByCode.isChecked();
        this.searchByTitle = searchByTitle.isChecked();
        this.searchByTeacher = searchByTeacher.isChecked();
        this.searchByRoom = searchByRoom.isChecked();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (item.isChecked()) {
            item.setChecked(false);
        } else {
            item.setChecked(true);
        }
        if (item.getItemId() == R.id.search_by_code) {
            this.searchByCode = item.isChecked();
        } else if (item.getItemId() == R.id.search_by_title) {
            this.searchByTitle = item.isChecked();
        } else if (item.getItemId() == R.id.search_by_teacher) {
            this.searchByTeacher = item.isChecked();
        } else if (item.getItemId() == R.id.search_by_room) {
            this.searchByRoom = item.isChecked();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
