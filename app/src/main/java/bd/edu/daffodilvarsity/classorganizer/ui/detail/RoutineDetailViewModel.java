package bd.edu.daffodilvarsity.classorganizer.ui.detail;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import bd.edu.daffodilvarsity.classorganizer.data.Repository;
import bd.edu.daffodilvarsity.classorganizer.model.Resource;
import bd.edu.daffodilvarsity.classorganizer.model.Routine;
import bd.edu.daffodilvarsity.classorganizer.model.Status;
import bd.edu.daffodilvarsity.classorganizer.model.Teacher;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RoutineDetailViewModel extends ViewModel {
    private MutableLiveData<Resource<Teacher>> mTeacherInfoListener;
    private MutableLiveData<Routine> mRoutineListener;
    private Repository mRepository = Repository.getInstance();
    private CompositeDisposable mDisposable;

    MutableLiveData<Resource<Teacher>> getTeacherInfoListener() {
        if (mTeacherInfoListener == null) {
            mTeacherInfoListener = new MutableLiveData<>();
        }
        return mTeacherInfoListener;
    }

    MutableLiveData<Routine> getRoutineListener() {
        if (mRoutineListener == null) {
            mRoutineListener = new MutableLiveData<>();
        }
        return mRoutineListener;
    }

    void loadTeachersDetails(String teachersInitial) {
        mRepository
                .getTeacherDetails(teachersInitial)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Teacher>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                        getTeacherInfoListener().postValue(new Resource<>(Status.LOADING, null, null));
                    }

                    @Override
                    public void onSuccess(Teacher teacher) {
                        getTeacherInfoListener().postValue(new Resource<>(Status.SUCCESSFUL, teacher, null));
                    }

                    @Override
                    public void onError(Throwable e) {
                        getTeacherInfoListener().postValue(new Resource<>(Status.ERROR, null, e));
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
