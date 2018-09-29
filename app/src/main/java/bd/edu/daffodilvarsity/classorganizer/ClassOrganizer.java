package bd.edu.daffodilvarsity.classorganizer;

import android.app.Application;

import com.google.firebase.FirebaseApp;
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

//        //Disable crashlytics in debug builds
//        CrashlyticsCore core = new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build();
//        Fabric.with(this, new Crashlytics.Builder().core(core).build());

        FirebaseApp.initializeApp(this);
        FirebaseMessaging.getInstance().subscribeToTopic("update");
        if (BuildConfig.DEBUG) FirebaseMessaging.getInstance().subscribeToTopic("debug");
//        MobileAds.initialize(this, getResources().getString(R.string.admob_app_id));
    }

    public static ClassOrganizer getInstance() {
        return instance;
    }
}
