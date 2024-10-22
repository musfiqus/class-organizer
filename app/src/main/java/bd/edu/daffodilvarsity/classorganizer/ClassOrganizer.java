package bd.edu.daffodilvarsity.classorganizer;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Created by Mushfiqus Salehin on 4/2/2017.
 * musfiqus@gmail.com
 */

public class ClassOrganizer extends Application {

    private static ClassOrganizer instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        FirebaseApp.initializeApp(this);
        FirebaseMessaging.getInstance().subscribeToTopic("update");
        //Disable crashlytics in debug builds
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG);
        if (BuildConfig.DEBUG) FirebaseMessaging.getInstance().subscribeToTopic("debug");
    }

    public static ClassOrganizer getInstance() {
        return instance;
    }
}
