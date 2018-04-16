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
import bd.edu.daffodilvarsity.classorganizer.utils.SpinnerHelperUser;

/**
 * Created by Mushfiqus Salehin on 3/26/2017.
 * musfiqus@gmail.com
 */

public class WelcomeSlideAdapter extends PagerAdapter implements SpinnerHelperUser.OnUserChangeListener{
    private static final String TAG = "WelcomeSlideAdapter";

    private Context context;
    private int[] layouts;
    private View view;
    private PrefManager prefManager;

    private static final String TAG_USER = "UserView";
    private static final String TAG_CAMPUS = "CampusView";
    private static final String TAG_CLASS = "ClassView";

    private SpinnerHelperClass classHelper;
    private SpinnerHelperCampus campusHelper;
    private SpinnerHelperUser spinnerHelperUser;


    public WelcomeSlideAdapter(Context context, int[] layouts) {
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

        if (layouts[position] == R.layout.welcome_slide3) {
            view.setTag(TAG_USER);
            spinnerHelperUser = new SpinnerHelperUser(context, view, this);
            spinnerHelperUser.setupUser();
        } else if (layouts[position] == R.layout.welcome_slide4) {
            view.setTag(TAG_CAMPUS);
            campusHelper = new SpinnerHelperCampus(context, view, R.layout.spinner_campus_row_welcome, spinnerHelperUser.isStudent());
            campusHelper.setupCampus();
        } else if (layouts[position] == R.layout.welcome_slide5) {
            view.setTag(TAG_CLASS);
            classHelper = new SpinnerHelperClass(context, view, R.layout.spinner_class_row_welcome, spinnerHelperUser == null || spinnerHelperUser.isStudent());
            if (isStudent()) {
                classHelper.createClassSpinners();
            } else {
                classHelper.createTeacherInitSpinners();
            }
        }
        container.addView(view);
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
        if (spinnerHelperUser != null) {
            return spinnerHelperUser.isStudent();
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

    @Override
    public void onUserChange(boolean isStudent) {

        ///////////////////////////////////////////////////////////////////////////////////////////
        //* Google: findviewwithtag returns null
        //* Stackoverflow: https://stackoverflow.com/a/20586547
        ///////////////////////////////////////////////////////////////////////////////////////////

        View parent = (View) view.getParent();
        if (parent != null) {
            View campusView = parent.findViewWithTag(TAG_CAMPUS);
            View classView = parent.findViewWithTag(TAG_CLASS);
            if (campusView != null) {
                campusHelper = new SpinnerHelperCampus(context, campusView, R.layout.spinner_campus_row_welcome, isStudent);
                campusHelper.setupCampus();
            }
            if (classView != null) {
                classHelper = null;
                classHelper = new SpinnerHelperClass(context, classView, R.layout.spinner_class_row_welcome, isStudent);
                if (isStudent()) {
                    classHelper.createClassSpinners();
                } else {
                    classHelper.createTeacherInitSpinners();
                }
            } else {
                Log.e(TAG, "onUserChange: classview null");
            }

        } else {
            Log.e(TAG, "onUserChange: Parent null");
        }

    }
}

