package bd.edu.daffodilvarsity.classorganizer;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.google.firebase.FirebaseApp;

import org.polaric.colorful.Colorful;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Mushfiqus Salehin on 4/2/2017.
 * musfiqus@gmail.com
 */

public class ClassOrganizer extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //Disable crashlytics in debug builds
        CrashlyticsCore core = new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build();
        Fabric.with(this, new Crashlytics.Builder().core(core).build());

        Colorful.defaults()
                .primaryColor(Colorful.ThemeColor.BLUE_GREY_CUSTOM)
                .accentColor(Colorful.ThemeColor.TEAL_CUSTOM)
                .translucent(false)
                .dark(false);

        Colorful.init(this);
        FirebaseApp.initializeApp(this);
//        MobileAds.initialize(this, getResources().getString(R.string.admob_app_id));
    }
}
