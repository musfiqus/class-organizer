package bd.edu.daffodilvarsity.classorganizer.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.data.DayData;
import bd.edu.daffodilvarsity.classorganizer.utils.PrefManager;
import bd.edu.daffodilvarsity.classorganizer.utils.RoutineLoader;
import bd.edu.daffodilvarsity.classorganizer.utils.SpinnerHelperCampus;
import bd.edu.daffodilvarsity.classorganizer.utils.SpinnerHelperClass;
import bd.edu.daffodilvarsity.classorganizer.utils.UserTypeHelper;

/**
 * Created by Mushfiqus Salehin on 3/26/2017.
 * musfiqus@gmail.com
 */

public class WelcomeSlidePagerAdapter extends PagerAdapter{
    private static final String TAG = "WelcomeSlidePagerAdapte";

    private Context context;
    private int[] layouts;
    private View view;
    private PrefManager prefManager;

    private SpinnerHelperClass classHelper;
    private SpinnerHelperCampus campusHelper;
    private UserTypeHelper userTypeHelper;


    public WelcomeSlidePagerAdapter(Context context, int[] layouts) {
        this.context = context;
        this.layouts = layouts;
        this.prefManager = new PrefManager(context);
    }

    //Note: Everything is instantiated before any button press or events
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(layouts[position], container, false);
        container.addView(view);
        if (layouts[position] == R.layout.welcome_slide3) {
            userTypeHelper = new UserTypeHelper(context, view);
            userTypeHelper.setupUser();
        }
        if (layouts[position] == R.layout.welcome_slide4) {
            if (userTypeHelper == null) {
                Log.e(TAG, "User null");
            }
            campusHelper = new SpinnerHelperCampus(context, view, R.layout.spinner_campus_row_welcome, userTypeHelper.isStudent(), true);
            campusHelper.setupCampus();
        }
        if (layouts[position] == R.layout.welcome_slide5) {
            Log.e(TAG, "Called Class Slide");
            classHelper = new SpinnerHelperClass(context, view, R.layout.spinner_class_row_welcome, userTypeHelper == null || userTypeHelper.isStudent());
            if (isStudent()) {
                classHelper.createClassSpinners();
            } else {
                classHelper.createTeacherInitSpinners();
            }
        }
        return view;
    }

    @Override
    public int getCount() {
        return layouts.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object obj) {
        return view == obj;
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        View view = (View) object;
        container.removeView(view);
    }


    //Sets up section spinner on Next button press
    public void setupSectionAdapter() {
        classHelper.sectionAdapter(campusHelper.getCampus(), campusHelper.getDept(), campusHelper.getProgram());
    }


    //loading semester on button press
    public void loadSemester() {
        if (prefManager.isUserStudent()) {
            RoutineLoader routineLoader = new RoutineLoader(prefManager.getLevel(), prefManager.getTerm(), prefManager.getSection(), context, prefManager.getDept(), prefManager.getCampus(), prefManager.getProgram());
            ArrayList<DayData> loadedRoutine = routineLoader.loadRoutine(false);
            if (loadedRoutine != null) {
                prefManager.saveDayData(loadedRoutine);
            }
        } else {
            RoutineLoader routineLoader = new RoutineLoader(prefManager.getTeacherInitial(), prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram(), context);
            ArrayList<DayData> loadedRoutine = routineLoader.loadRoutine(false);
            if (loadedRoutine != null) {
                prefManager.saveDayData(loadedRoutine);
            }
        }

    }

    public boolean isStudent() {
        if (userTypeHelper != null) {
            return userTypeHelper.isStudent();
        }
        return true;
    }

    public String getCampus() {
        return campusHelper.getCampus();
    }

    public String getDept() {
        return campusHelper.getDept();
    }

    public String getProgram() {
        return campusHelper.getProgram();
    }

    public int getLevel() {
        return classHelper.getLevel();
    }

    public int getTerm() {
        return classHelper.getTerm();
    }

    public String getSection() {
        return classHelper.getSection();
    }

    public View getView() {
        return view;
    }

    public int getClassDataCode() {
        return classHelper.getClassDataCode();
    }

    public int getCampusDataCode() {
        return campusHelper.getCampusDataCode();
    }

    public SpinnerHelperClass getClassHelper() {
        return classHelper;
    }

    public SpinnerHelperCampus getCampusHelper() {
        return campusHelper;
    }
}

