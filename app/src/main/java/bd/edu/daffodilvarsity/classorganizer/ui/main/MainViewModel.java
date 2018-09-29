package bd.edu.daffodilvarsity.classorganizer.ui.main;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bd.edu.daffodilvarsity.classorganizer.ClassOrganizer;
import bd.edu.daffodilvarsity.classorganizer.data.Repository;
import bd.edu.daffodilvarsity.classorganizer.model.Resource;
import bd.edu.daffodilvarsity.classorganizer.model.Routine;
import bd.edu.daffodilvarsity.classorganizer.model.Status;
import es.dmoral.toasty.Toasty;
import io.reactivex.CompletableObserver;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainViewModel extends ViewModel {
    private static final String TAG = "MainViewModel";
    private MutableLiveData<Resource<Map<String, ArrayList<Routine>>>> routineListListener;
    private MutableLiveData<Boolean> classProgressListener;

    private Repository mRepository;
    private CompositeDisposable mDisposable;

    public static final String SATURDAY = "Saturday";
    public static final String SUNDAY = "Sunday";
    public static final String MONDAY = "Monday";
    public static final String TUESDAY = "Tuesday";
    public static final String WEDNESDAY = "Wednesday";
    public static final String THURSDAY = "Thursday";
    public static final String FRIDAY = "Friday";


    public MainViewModel() {
        mRepository = Repository.getInstance();
    }

    MutableLiveData<Resource<Map<String, ArrayList<Routine>>>> getRoutineListListener() {
        if (routineListListener == null) {
            routineListListener = new MutableLiveData<>();
        }
        loadRoutine();
        return routineListListener;
    }

    MutableLiveData<Boolean> getClassProgressListener() {
        if (classProgressListener == null) {
            classProgressListener = new MutableLiveData<>();
        }
        return classProgressListener;
    }

    void loadRoutine() {
        mRepository
                .getSavedRoutine()
                .flatMap(routines -> Single.fromCallable(() -> getSortedRoutine(routines)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Map<String, ArrayList<Routine>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                        routineListListener.postValue(new Resource<>(Status.LOADING, null, null));
                    }

                    @Override
                    public void onSuccess(Map<String, ArrayList<Routine>> stringArrayListMap) {
                        routineListListener.postValue(new Resource<>(Status.SUCCESSFUL, stringArrayListMap, null));

                    }

                    @Override
                    public void onError(Throwable e) {
                        routineListListener.postValue(new Resource<>(Status.ERROR, null, e));
                    }
                });

    }



    private Map<String, ArrayList<Routine>> getSortedRoutine(List<Routine> routines) {
        Log.e(TAG, "getSortedRoutine: routine sort size: "+routines.size() );
        ArrayList<Routine> satDayData = new ArrayList<>();
        ArrayList<Routine> sunDayData = new ArrayList<>();
        ArrayList<Routine> monDayData = new ArrayList<>();
        ArrayList<Routine> tueDayData = new ArrayList<>();
        ArrayList<Routine> wedDayData = new ArrayList<>();
        ArrayList<Routine> thuDayData = new ArrayList<>();
        ArrayList<Routine> friDayData = new ArrayList<>();

        for (Routine eachDay : routines) {
            if (eachDay != null && eachDay.getDay() != null) {
                if (eachDay.getDay().equalsIgnoreCase(SATURDAY)) {
                    satDayData.add(eachDay);
                } else if (eachDay.getDay().equalsIgnoreCase(SUNDAY)) {
                    sunDayData.add(eachDay);
                } else if (eachDay.getDay().equalsIgnoreCase(MONDAY)) {
                    monDayData.add(eachDay);
                } else if (eachDay.getDay().equalsIgnoreCase(TUESDAY)) {
                    tueDayData.add(eachDay);
                } else if (eachDay.getDay().equalsIgnoreCase(WEDNESDAY)) {
                    wedDayData.add(eachDay);
                } else if (eachDay.getDay().equalsIgnoreCase(THURSDAY)) {
                    thuDayData.add(eachDay);
                } else if (eachDay.getDay().equalsIgnoreCase(FRIDAY)) {
                    friDayData.add(eachDay);
                }
            }
        }
        Collections.sort(satDayData, (o1, o2) -> Double.valueOf(o1.getTimeWeight()).compareTo(Double.valueOf(o2.getTimeWeight())));
        Collections.sort(sunDayData, (o1, o2) -> Double.valueOf(o1.getTimeWeight()).compareTo(Double.valueOf(o2.getTimeWeight())));
        Collections.sort(monDayData, (o1, o2) -> Double.valueOf(o1.getTimeWeight()).compareTo(Double.valueOf(o2.getTimeWeight())));
        Collections.sort(tueDayData, (o1, o2) -> Double.valueOf(o1.getTimeWeight()).compareTo(Double.valueOf(o2.getTimeWeight())));
        Collections.sort(wedDayData, (o1, o2) -> Double.valueOf(o1.getTimeWeight()).compareTo(Double.valueOf(o2.getTimeWeight())));
        Collections.sort(thuDayData, (o1, o2) -> Double.valueOf(o1.getTimeWeight()).compareTo(Double.valueOf(o2.getTimeWeight())));
        Collections.sort(friDayData, (o1, o2) -> Double.valueOf(o1.getTimeWeight()).compareTo(Double.valueOf(o2.getTimeWeight())));

        Map<String, ArrayList<Routine>> arrayListMap = new HashMap<>();
        arrayListMap.put(SATURDAY, satDayData);
        arrayListMap.put(SUNDAY, sunDayData);
        arrayListMap.put(MONDAY, monDayData);
        arrayListMap.put(TUESDAY, tueDayData);
        arrayListMap.put(WEDNESDAY, wedDayData);
        arrayListMap.put(THURSDAY, thuDayData);
        arrayListMap.put(FRIDAY, friDayData);
        return arrayListMap;
    }

    void modifyRoutine(Routine routine) {
        mRepository
                .modifyRoutine(routine)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                        getClassProgressListener().postValue(true);
                    }

                    @Override
                    public void onComplete() {
                        getClassProgressListener().postValue(false);
                        if (routine.isMuted()) {
                            Toasty.success(ClassOrganizer.getInstance(), routine.getCourseCode()+" has been muted", Toast.LENGTH_SHORT, true).show();
                        } else {
                            Toasty.success(ClassOrganizer.getInstance(), routine.getCourseCode()+" has been unmuted", Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        getClassProgressListener().postValue(false);
                        Toasty.error(ClassOrganizer.getInstance(), "Failed to mute "+routine.getCourseCode(), Toast.LENGTH_SHORT, true).show();
                    }
                });
    }

    private void addDisposable(Disposable disposable) {
        if (mDisposable == null) {
            mDisposable = new CompositeDisposable();
        }
        mDisposable.add(disposable);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (mDisposable != null && !mDisposable.isDisposed()){
            mDisposable.dispose();
        }
    }
}
