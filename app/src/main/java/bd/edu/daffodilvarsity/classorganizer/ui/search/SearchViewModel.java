package bd.edu.daffodilvarsity.classorganizer.ui.search;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import bd.edu.daffodilvarsity.classorganizer.ClassOrganizer;
import bd.edu.daffodilvarsity.classorganizer.data.Repository;
import bd.edu.daffodilvarsity.classorganizer.model.Resource;
import bd.edu.daffodilvarsity.classorganizer.model.Routine;
import bd.edu.daffodilvarsity.classorganizer.model.Status;
import bd.edu.daffodilvarsity.classorganizer.model.Teacher;
import bd.edu.daffodilvarsity.classorganizer.utils.PreferenceGetter;
import es.dmoral.toasty.Toasty;
import io.reactivex.CompletableObserver;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SearchViewModel extends ViewModel {
    private static final String TAG = "SearchViewModel";
    private CompositeDisposable mDisposable;
    private boolean isRoutineSearchFinished;

    private MutableLiveData<Boolean> progressListener;
    private MutableLiveData<Resource<List<String>>> sectionListListener;
    private MutableLiveData<Resource<List<Routine>>> routineListListener;
    private MutableLiveData<Resource<List<String>>> timeListListener;
    private MutableLiveData<Resource<List<Routine>>> freeRoomListListener;
    private MutableLiveData<Resource<List<String>>> teachersInitialListListener;
    private MutableLiveData<Resource<List<Routine>>> classListByInitialListener;
    private MutableLiveData<Teacher> mTeacherInfoListener;
    private Repository repository = Repository.getInstance();

    MutableLiveData<Boolean> getProgressListener() {
        if (progressListener == null) {
            progressListener = new MutableLiveData<>();
        }
        return progressListener;
    }

    MutableLiveData<Resource<List<String>>> getSectionListListener() {
        if (sectionListListener == null) {
            sectionListListener = new MutableLiveData<>();
        }
        return sectionListListener;
    }

    MutableLiveData<Resource<List<Routine>>> getRoutineListListener() {
        if (routineListListener == null) {
            routineListListener = new MutableLiveData<>();
        }
        return routineListListener;
    }

    MutableLiveData<Resource<List<String>>> getTimeListListener() {
        if (timeListListener == null) {
            timeListListener = new MutableLiveData<>();
        }
        loadTimeList();
        return timeListListener;
    }

    MutableLiveData<Resource<List<Routine>>> getFreeRoomListListener() {
        if (freeRoomListListener == null) {
            freeRoomListListener = new MutableLiveData<>();
        }
        return freeRoomListListener;
    }

    MutableLiveData<Resource<List<String>>> getTeachersInitialListListener() {
        if (teachersInitialListListener == null) {
            teachersInitialListListener = new MutableLiveData<>();
        }
        loadInitials();
        return teachersInitialListListener;
    }

    MutableLiveData<Resource<List<Routine>>> getClassListByInitialListener() {
        if (classListByInitialListener == null) {
            classListByInitialListener = new MutableLiveData<>();
        }
        return classListByInitialListener;
    }

    MutableLiveData<Teacher> getTeacherInfoListener() {
        if (mTeacherInfoListener == null) {
            mTeacherInfoListener = new MutableLiveData<>();
        }
        return mTeacherInfoListener;
    }

    void searchClassesByInitial(String initial) {
        repository
                .searchRoutineByInitial(initial)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new SingleObserver<List<Routine>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                        isRoutineSearchFinished = false;
                        getProgressListener().postValue(true);
                        getClassListByInitialListener().postValue(new Resource<>(Status.LOADING, null, null));
                    }

                    @Override
                    public void onSuccess(List<Routine> routines) {
                        getProgressListener().postValue(false);
                        getClassListByInitialListener().postValue(new Resource<>(Status.SUCCESSFUL, routines, null));
                        isRoutineSearchFinished = true;
                    }

                    @Override
                    public void onError(Throwable e) {
                        getProgressListener().postValue(false);
                        getClassListByInitialListener().postValue(new Resource<>(Status.ERROR, null, e));
                        isRoutineSearchFinished = true;
                    }
                });
        repository
                .getTeacherDetails(initial)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Teacher>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                        getProgressListener().postValue(true);
                    }

                    @Override
                    public void onSuccess(Teacher teacher) {
                        getTeacherInfoListener().postValue(teacher);
                        if (isRoutineSearchFinished) {
                            getProgressListener().postValue(false);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (isRoutineSearchFinished) {
                            getProgressListener().postValue(false);
                        }
                    }
                });
    }

    private void loadInitials() {
        repository
                .getTeachersInitials(PreferenceGetter.getCampus(), PreferenceGetter.getDepartment())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new SingleObserver<List<String>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                        getProgressListener().postValue(true);
                        teachersInitialListListener.postValue(new Resource<>(Status.LOADING, null, null));
                    }

                    @Override
                    public void onSuccess(List<String> strings) {
                        getProgressListener().postValue(false);
                        teachersInitialListListener.postValue(new Resource<>(Status.SUCCESSFUL, strings, null));
                    }

                    @Override
                    public void onError(Throwable e) {
                        getProgressListener().postValue(false);
                        teachersInitialListListener.postValue(new Resource<>(Status.ERROR, null, e));
                    }
                });
    }

    private void loadTimeList() {
        repository
                .getTimeList()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new SingleObserver<List<String>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                        getProgressListener().postValue(true);
                        timeListListener.postValue(new Resource<>(Status.LOADING, null, null));
                    }

                    @Override
                    public void onSuccess(List<String> strings) {
                        getProgressListener().postValue(false);
                        timeListListener.postValue(new Resource<>(Status.SUCCESSFUL, strings, null));
                    }

                    @Override
                    public void onError(Throwable e) {
                        getProgressListener().postValue(false);
                        timeListListener.postValue(new Resource<>(Status.ERROR, null, e));
                    }
                });
    }


    void searchRoutines(int level, int term, String section) {
        repository
                .searchRoutine(level, term, section)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new SingleObserver<List<Routine>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                        getRoutineListListener().postValue(new Resource<>(Status.LOADING, null, null));
                        getProgressListener().postValue(true);
                    }

                    @Override
                    public void onSuccess(List<Routine> routines) {
                        getRoutineListListener().postValue(new Resource<>(Status.SUCCESSFUL, routines, null));
                        getProgressListener().postValue(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getRoutineListListener().postValue(new Resource<>(Status.ERROR, null, e));
                        getProgressListener().postValue(false);
                    }
                });
    }

    void loadSections(int level, int term) {
        repository
                .getSections(level, term)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new SingleObserver<List<String>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                        sectionListListener.postValue(new Resource<>(Status.LOADING, null, null));
                        getProgressListener().postValue(true);
                    }

                    @Override
                    public void onSuccess(List<String> list) {
                        sectionListListener.postValue(new Resource<>(Status.SUCCESSFUL, list, null));
                        getProgressListener().postValue(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        sectionListListener.postValue(new Resource<>(Status.ERROR, null, e));
                        getProgressListener().postValue(false);
                    }
                });
    }

    void searchFreeRooms(String time, String day) {
        repository
                .searchFreeRooms(time, day)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new SingleObserver<List<Routine>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                        getProgressListener().postValue(true);
                        getFreeRoomListListener().postValue(new Resource<>(Status.LOADING, null, null));
                    }

                    @Override
                    public void onSuccess(List<Routine> routines) {
                        getProgressListener().postValue(false);
                        getFreeRoomListListener().postValue(new Resource<>(Status.SUCCESSFUL, routines, null));
                    }

                    @Override
                    public void onError(Throwable e) {
                        getProgressListener().postValue(false);
                        getFreeRoomListListener().postValue(new Resource<>(Status.ERROR, null, e));
                    }
                });
    }

    void saveRoutine(Routine routine) {
        repository
                .addRoutine(routine)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                        getProgressListener().postValue(true);
                    }

                    @Override
                    public void onComplete() {
                        getProgressListener().postValue(false);
                        Toasty.success(ClassOrganizer.getInstance(), "Saved "+routine.getCourseCode(), Toast.LENGTH_SHORT, true).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        getProgressListener().postValue(false);
                        Toasty.error(ClassOrganizer.getInstance(), "Failed to saved "+routine.getCourseCode(), Toast.LENGTH_SHORT, true).show();

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
