package bd.edu.daffodilvarsity.classorganizer.ui.search;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import bd.edu.daffodilvarsity.classorganizer.data.Repository;
import bd.edu.daffodilvarsity.classorganizer.model.Resource;
import bd.edu.daffodilvarsity.classorganizer.model.Routine;
import bd.edu.daffodilvarsity.classorganizer.model.Status;
import bd.edu.daffodilvarsity.classorganizer.utils.PreferenceGetter;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SearchViewModel extends ViewModel {
    private CompositeDisposable mDisposable;

    private MutableLiveData<Boolean> progressListener;
    private MutableLiveData<Resource<List<String>>> sectionListListener;
    private MutableLiveData<Resource<List<Routine>>> routineListListener;
    private MutableLiveData<Resource<List<String>>> timeListListener;
    private MutableLiveData<Resource<List<Routine>>> freeRoomListListener;
    private MutableLiveData<Resource<List<String>>> teachersInitialListListener;
    private MutableLiveData<Resource<List<Routine>>> classListByInitialListener;
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

    void searchClassesByInitial(String initial) {
        repository
                .searchRoutineByInitial(initial)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new SingleObserver<List<Routine>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                        getProgressListener().postValue(true);
                        getClassListByInitialListener().postValue(new Resource<>(Status.LOADING, null, null));
                    }

                    @Override
                    public void onSuccess(List<Routine> routines) {
                        getProgressListener().postValue(false);
                        getClassListByInitialListener().postValue(new Resource<>(Status.SUCCESSFUL, routines, null));
                    }

                    @Override
                    public void onError(Throwable e) {
                        getProgressListener().postValue(false);
                        getClassListByInitialListener().postValue(new Resource<>(Status.ERROR, null, e));
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
